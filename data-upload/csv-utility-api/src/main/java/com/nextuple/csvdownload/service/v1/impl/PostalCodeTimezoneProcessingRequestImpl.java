package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.MARKET_REGION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.MARKET_REGION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;

import com.newrelic.relocated.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PostalCodeTimezoneProcessingRequestImpl extends AbstractProcessingRequest {

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  public PostalCodeTimezoneProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.POSTAL_CODE_TIMEZONE.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), MARKET_REGION_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0),
        getModuleType(),
        MARKET_REGION_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    csvReader.close();
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-postal-code-timezone";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtos)
      throws IOException {
    recordStatusDtos.forEach(dto -> constructPostalCodeTimezoneError(writer, dto));
    writer.flush();
  }

  private void constructPostalCodeTimezoneError(CSVWriter writer, RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var requestBody =
        gson.fromJson(recordStatusDto.getRequestBody(), PostalCodeTimezoneUpload.class);
    var req =
        new String[] {
          requestBody.getAction(),
          requestBody.getOrgId(),
          requestBody.getPostalCodePrefix(),
          requestBody.getCountry(),
          requestBody.getState(),
          requestBody.getCity(),
          requestBody.getLatitude(),
          requestBody.getLatitude(),
          requestBody.getTimeZone(),
          recordStatusDto.getErrorMessage()
        };
    writeToCSV(req, writer);
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE;
  }
}
