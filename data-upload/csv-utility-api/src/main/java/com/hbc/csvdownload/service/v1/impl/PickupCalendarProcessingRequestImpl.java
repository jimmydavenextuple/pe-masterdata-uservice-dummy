package com.hbc.csvdownload.service.v1.impl;

import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.PICKUP_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.PICKUP_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;

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
import org.springframework.stereotype.Service;

@Service
public class PickupCalendarProcessingRequestImpl extends AbstractProcessingRequest
    implements ProcessingRequestInterface {

  public PickupCalendarProcessingRequestImpl(JobsDashboardClient jobsDashboardClient) {
    super(jobsDashboardClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.PICKUP_CALENDAR.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_PICKUP_CALENDER, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), PICKUP_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0),
        getModuleType(),
        PICKUP_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    csvReader.close();
  }
}
