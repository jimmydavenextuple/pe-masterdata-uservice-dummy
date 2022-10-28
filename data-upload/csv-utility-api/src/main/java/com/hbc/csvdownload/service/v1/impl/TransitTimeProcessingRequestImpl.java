package com.hbc.csvdownload.service.v1.impl;

import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_TYPE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.v1.AbstractProcessingRequest;
import com.hbc.csvdownload.service.v1.ProcessingRequestInterface;
import com.hbc.dataupload.common.utils.v1.DataUploadUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TransitTimeProcessingRequestImpl extends AbstractProcessingRequest
    implements ProcessingRequestInterface {

  public TransitTimeProcessingRequestImpl(JobsDashboardClient jobsDashboardClient) {
    super(jobsDashboardClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.TRANSIT.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_TRANSIT_TIMES, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    // Extract orgId value
    String orgIdHeader = csvFileContents.remove(0)[0];
    // Extract carrierServiceId  value
    String carrierServiceIdHeader = csvFileContents.remove(0)[0];
    // Extract destination/sourceFsa header and sourceFsa values
    String sFsaListHeader = csvFileContents.remove(0)[0];

    validateEmptyCSV(csvFileContents, csvReader);
    DataUploadUtil.validateCSVHeaders(
        new String[] {orgIdHeader, carrierServiceIdHeader, sFsaListHeader},
        getModuleType(),
        TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    csvReader.close();
  }

  private void validateEmptyCSV(List<String[]> csvFileContents, CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (CollectionUtils.isEmpty(csvFileContents)) {
      csvReader.close();
      throw new CommonServiceException(
          NO_RECORDS_FOUND_IN_THE_CSV, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }
}
