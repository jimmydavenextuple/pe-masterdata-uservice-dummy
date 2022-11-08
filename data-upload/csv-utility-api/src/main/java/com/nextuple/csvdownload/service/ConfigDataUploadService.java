package com.nextuple.csvdownload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.csvdownload.domain.mapper.ProcessingLeadTimeMapper;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.ProcessingRequestFactory;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.service.FileService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigDataUploadService {

  private final Logger logger = LoggerFactory.getLogger(ConfigDataUploadService.class);
  private final JobsDashboardClient jobsDashboardClient;
  private final FileService fileService;
  private final ProcessingRequestFactory processingRequestFactory;

  public static final ProcessingLeadTimeMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeMapper.class);

  public GenericUploadResponse processUploadConfigData(
      String moduleName, GenericUploadRequest uploadRequest)
      throws CommonServiceException, JobSubmissionException, CsvException, IOException {
    if (!validateModuleName(moduleName)) {
      logger.error("Invalid module name = {} supplied", moduleName);
      throw new CommonServiceException(
          "Invalid module name supplied",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of("templateType", FieldError.builder().rejectedValue(moduleName).build()));
    }

    String bucketName = uploadRequest.getFilePath().split("/", 2)[0];
    String filePath = uploadRequest.getFilePath().split("/", 2)[1];

    // download file from storage
    var fileResponse = fileService.getFile(bucketName, filePath);

    var fileMetaDataCreationRequest = getFileMetaDataCreationRequest(fileResponse, uploadRequest);
    var fileMetaDataResponse =
        jobsDashboardClient.createFileMetadata(fileMetaDataCreationRequest).getPayload();

    var processingRequestInterface = processingRequestFactory.getModule(moduleName);

    processingRequestInterface.validate(uploadRequest, fileResponse);
    processingRequestInterface.submitJob(uploadRequest.getOrgId(), fileMetaDataResponse.getId());

    return GenericUploadResponse.builder()
        .orgId(uploadRequest.getOrgId())
        .fileMetaDataId(fileMetaDataResponse.getId())
        .build();
  }

  private FileMetaDataCreationRequest getFileMetaDataCreationRequest(
      FileResponse fileResponse, GenericUploadRequest uploadRequest) {
    return FileMetaDataCreationRequest.builder()
        .name(fileResponse.getFileName())
        .path(String.format("%s/%s", fileResponse.getBucketName(), fileResponse.getFilePath()))
        .size(String.valueOf(fileResponse.getContentLength()))
        .type(fileResponse.getContentType())
        .description("Details of file uploaded by UI")
        .storageType(uploadRequest.getStorageType())
        .createdBy(uploadRequest.getCreatedBy())
        .build();
  }

  public boolean validateModuleName(String moduleName) {
    return Arrays.stream(ModuleEnum.values()).anyMatch(t -> t.getModuleValue().equals(moduleName));
  }
}
