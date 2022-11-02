package com.hbc.csvdownload.service.v1.impl;

import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.v1.AbstractProcessingRequest;
import com.hbc.dataupload.common.constants.DataUploadUtilityConstants;
import com.hbc.dataupload.common.utils.v1.DataUploadUtil;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import com.newrelic.relocated.Gson;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NodeProcessingRequestImpl extends AbstractProcessingRequest {

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  public NodeProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.NODES.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_NODES, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), NODE_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0), getModuleType(), NODE_DATA_UPLOAD_INVALID_FILE_HEADERS, csvReader);

    csvReader.close();
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-node";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtos)
      throws IOException {
    recordStatusDtos.forEach(dto -> constructNodeError(writer, dto));
    writer.flush();
  }

  private void constructNodeError(CSVWriter writer, RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var requestBody = gson.fromJson(recordStatusDto.getRequestBody(), NodeDataUpload.class);

    var sdndEligible =
        requestBody.getServiceOptionEligibilities().get(DataUploadUtilityConstants.SDND_ELIGIBLE)
                == null
            ? ""
            : requestBody
                .getServiceOptionEligibilities()
                .get(DataUploadUtilityConstants.SDND_ELIGIBLE)
                .toString();

    var bopisEligible =
        requestBody.getServiceOptionEligibilities().get(DataUploadUtilityConstants.BOPIS_ELIGIBLE)
                == null
            ? ""
            : requestBody
                .getServiceOptionEligibilities()
                .get(DataUploadUtilityConstants.BOPIS_ELIGIBLE)
                .toString();

    var expressEligible =
        requestBody.getServiceOptionEligibilities().get(DataUploadUtilityConstants.EXPRESS_ELIGIBLE)
                == null
            ? ""
            : requestBody
                .getServiceOptionEligibilities()
                .get(DataUploadUtilityConstants.EXPRESS_ELIGIBLE)
                .toString();

    var nextdayEligible =
        requestBody.getServiceOptionEligibilities().get(DataUploadUtilityConstants.NEXTDAY_ELIGIBLE)
                == null
            ? ""
            : requestBody
                .getServiceOptionEligibilities()
                .get(DataUploadUtilityConstants.NEXTDAY_ELIGIBLE)
                .toString();

    var req =
        new String[] {
          requestBody.getAction(),
          requestBody.getNodeId(),
          requestBody.getOrgId(),
          requestBody.getStreet(),
          requestBody.getCity(),
          requestBody.getProvince(),
          requestBody.getPostalCode(),
          requestBody.getCountry(),
          requestBody.getLatitude(),
          requestBody.getLongitude(),
          requestBody.getTimezone(),
          requestBody.getShipToHome(),
          sdndEligible,
          bopisEligible,
          expressEligible,
          requestBody.getNodeType(),
          requestBody.getIsActive(),
          nextdayEligible,
          recordStatusDto.getErrorMessage()
        };
    writeToCSV(req, writer);
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODES;
  }
}
