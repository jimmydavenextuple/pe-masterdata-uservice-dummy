package com.nextuple.csvdownload.service.v1.impl;

import com.newrelic.relocated.Gson;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TransitBufferProcessingRequestImpl extends AbstractProcessingRequest {

  private final Logger logger = LoggerFactory.getLogger(TransitBufferProcessingRequestImpl.class);

  public TransitBufferProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.TRANSIT_BUFFER.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    logger.debug("Implementation is differently handled");
    return null;
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {
    logger.debug("Implementation is differently handled");
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-transit-buffer";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtos)
      throws IOException {
    writerProcessingLeadTimesError(writer, recordStatusDtos);
    writer.flush();
  }

  private void writerProcessingLeadTimesError(
      CSVWriter writer, List<RecordStatusDto> recordStatusDtos) {
    recordStatusDtos.forEach(dto -> constructTransitBufferError(writer, dto));
  }

  private void constructTransitBufferError(CSVWriter writer, RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var requestBody = gson.fromJson(recordStatusDto.getRequestBody(), TransitBufferUpload.class);

    var req =
        new String[] {
          requestBody.getSourceGeozone(),
          requestBody.getDestinationGeozone(),
          recordStatusDto.getErrorMessage()
        };
    writeToCSV(req, writer);
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.TRANSIT_BUFFER_REQUEST;
  }
}
