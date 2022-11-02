package com.hbc.csvdownload.service.v1;

import static com.hbc.csvdownload.common.constants.CSVCommonConstants.ERROR_MESSAGE;

import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.hbc.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.CSVWriter;
import feign.FeignException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public abstract class AbstractProcessingRequest implements ProcessingRequestInterface {

  private final Logger logger = LoggerFactory.getLogger(AbstractProcessingRequest.class);
  private final JobsDashboardClient jobsDashboardClient;
  public static final int RECORDS_PER_PAGE = 200;
  private final FileService fileService;
  private final PreSignedUrlInterface preSignedUrlInterface;
  private final FileMetaDataClient fileMetaDataClient;

  @Value("${dataupload.bucket-name}")
  private String bucketName;

  @Value("${storage.type}")
  private String storageType;

  public JobResponse submitJob(String orgId, JobTypeEnum jobType, Long fileMetadataId)
      throws JobSubmissionException {
    try {
      return jobsDashboardClient
          .processJobOfflineWithFileMetaDataId(orgId, jobType, fileMetadataId)
          .getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while submitting job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      logger.error("Error while submitting job to job framework", e);
      throw new JobSubmissionException("Error while submitting job to job framework", e, orgId);
    }
  }

  public Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }

  public PagePayload<RecordStatusDto> getJobRecordsByFilters(
      String orgId, String jobId, Optional<String> status, Integer pageNo, Integer pageSize)
      throws JobSubmissionException {
    try {
      return jobsDashboardClient
          .getJobRecordsByFilters(orgId, jobId, status.orElse(null), pageNo, pageSize)
          .getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while fetching job records by filters for job: {}", jobId, e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      logger.error("Error while fetching job records by filters for job: {}", jobId, e);
      throw new JobSubmissionException("Error while fetching job records by filters", e, orgId);
    }
  }

  public void writeToCSV(String[] data, CSVWriter writer) {
    writer.writeNext(data);
  }

  public Path createErrorLogCSV(String fileName) throws IOException {
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    return Files.createTempFile(fileName, ".csv", attr);
  }

  public List<String> getHeaderWithErrorColumn(String module) {
    List<String> expectedHeaders =
        new ArrayList<>(DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(module));
    expectedHeaders.add(ERROR_MESSAGE);
    return expectedHeaders;
  }

  public PreSignedUrlResponse generateURLResponse(File csv, JobDto jobDto)
      throws CommonServiceException {
    var filePath =
        String.format(
            "%s/%s/%s/%s",
            ModuleEnum.UI_LOGS.getModuleValue(),
            jobDto.getJobType().getModule().getModuleValue(),
            DateTime.now().toString("yyyy-MM-dd"),
            csv.getName());
    fileService.uploadFile(bucketName, filePath, csv);
    var bucketPath = String.format("%s/%s", bucketName, filePath);
    var response = createFileMetaData(csv.getName(), bucketPath, csv.length(), jobDto);
    if (response.isSuccess()) {
      return preSignedUrlInterface.downloadFileURLById(response.getPayload().getId());
    }
    throw new CommonServiceException(
        "Error Creating the file meta", HttpStatus.INTERNAL_SERVER_ERROR, 0x1771, null);
  }

  public BaseResponse<FileMetaDataResponse> createFileMetaData(
      String fileName, String bucketPath, long fileSize, JobDto jobDto) {
    var fileMetaDataCreationRequest =
        FileMetaDataCreationRequest.builder()
            .name(fileName)
            .path(String.format(bucketPath))
            .size(String.valueOf(fileSize))
            .description("Error log file metadata")
            .storageType(storageType)
            .extReferenceId(jobDto.getJobId())
            .type(ModuleEnum.UI_LOGS.getModuleValue())
            .createdBy("SYSTEM")
            .build();
    return fileMetaDataClient.createFileMetadata(fileMetaDataCreationRequest);
  }

  @Override
  public PreSignedUrlResponse downloadErrorLogs(JobDto jobDto, Optional<String> status)
      throws JobSubmissionException, IOException, CommonServiceException {
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile = Files.createTempFile(tempFilePrefix() + new Date().getTime(), ".csv", attr);
    List<String> expectedHeaders = getHeaderWithErrorColumn(getModuleType());
    PagePayload<RecordStatusDto> recordStatusDtos =
        this.getJobRecordsByFilters(
            jobDto.getOrgId(), jobDto.getJobId(), status, 1, RECORDS_PER_PAGE);
    var preSignedURL = new PreSignedUrlResponse();
    if (!CollectionUtils.isEmpty(recordStatusDtos.getData())) {
      var header = expectedHeaders.toArray(new String[0]);
      try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
        writeToCSV(header, writer);
        addErrorLine(writer, recordStatusDtos.getData());
        int totalPages = recordStatusDtos.getPagination().getTotalPages();
        for (var currentPage = 2; currentPage <= totalPages; currentPage++) {
          PagePayload<RecordStatusDto> nextPage =
              this.getJobRecordsByFilters(
                  jobDto.getOrgId(), jobDto.getJobId(), status, currentPage, 1);
          addErrorLine(writer, nextPage.getData());
        }
        preSignedURL = generateURLResponse(tempFile.toFile(), jobDto);
        writer.flush();
      } finally {
        Files.delete(tempFile);
      }
    }
    return preSignedURL;
  }

  public abstract String tempFilePrefix();

  public abstract void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDto)
      throws IOException;
}
