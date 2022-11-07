package com.hbc.csvdownload.service.v1.impl;

import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_TIME_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_HEADER_TRANSIT_TIMES;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_AND_DESTINATION_GEO_ZONE;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.domain.mapper.TransitDataRequestMapper;
import com.hbc.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.hbc.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.v1.AbstractProcessingRequest;
import com.hbc.dataupload.common.utils.v1.DataUploadUtil;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
public class TransitTimeProcessingRequestImpl extends AbstractProcessingRequest {

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  private final Logger logger = LoggerFactory.getLogger(TransitTimeProcessingRequestImpl.class);

  private static final TransitDataRequestMapper INSTANCE =
      Mappers.getMapper(TransitDataRequestMapper.class);

  public TransitTimeProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
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

  @Override
  public String tempFilePrefix() {
    return "download-log-transit-time";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtoList)
      throws IOException {

    var transitDataErrorLogsList =
        recordStatusDtoList.stream().map(this::getRequestBody).collect(Collectors.toList());

    var orgId = transitDataErrorLogsList.get(0).getOrgId();
    var carrierServiceId = transitDataErrorLogsList.get(0).getCarrierServiceId();

    Set<String> sourceFsaSet =
        transitDataErrorLogsList.stream()
            .map(TransitDataErrorLogsPojo::getSourceGeozone)
            .collect(Collectors.toSet());

    var sourceFsaList = new ArrayList<>(sourceFsaSet);

    sourceFsaList.add(0, SOURCE_AND_DESTINATION_GEO_ZONE);

    writer.writeNext(new String[] {ORG_ID, orgId});
    writer.writeNext(new String[] {CARRIER_SERVICE_HEADER_TRANSIT_TIMES, carrierServiceId});
    writer.writeNext(sourceFsaList.toArray(new String[0]));

    sourceFsaList.clear();

    Map<String, String> sourceAndTransitTimeMap = new TreeMap<>();

    sourceFsaSet.forEach(fsa -> sourceAndTransitTimeMap.put(fsa, null));

    Map<String, Map<String, String>> transitTimeErrorLogsMap = new HashMap<>();

    transitDataErrorLogsList.forEach(
        transitRequest -> {
          if (CollectionUtils.isEmpty(
              transitTimeErrorLogsMap.get(transitRequest.getDestinationGeozone()))) {
            transitTimeErrorLogsMap.put(
                transitRequest.getDestinationGeozone(), new HashMap<>(sourceAndTransitTimeMap));
          }
        });

    transitDataErrorLogsList.forEach(
        transitRequest ->
            transitTimeErrorLogsMap
                .get(transitRequest.getDestinationGeozone())
                .put(transitRequest.getSourceGeozone(), errorMessage(transitRequest)));

    constructCsvDataForTransit(transitTimeErrorLogsMap, writer);
  }

  private String errorMessage(TransitDataErrorLogsPojo transitDataErrorLog) {
    if (ObjectUtils.isEmpty(transitDataErrorLog.getErrorMessage())) {
      logger.error("Empty error message received. Defaulting to internal server error message");
      return "Internal Server Error";
    } else if (("feign.RetryableException").equals(transitDataErrorLog.getException())) {
      return String.valueOf(transitDataErrorLog.getTransitDays());
    } else {
      return transitDataErrorLog.getErrorMessage();
    }
  }

  private void constructCsvDataForTransit(
      Map<String, Map<String, String>> transitTimeDataMap, CSVWriter writer) throws IOException {

    transitTimeDataMap
        .keySet()
        .forEach(
            destinationFsa ->
                writer.writeNext(constructCsvRowsForTransit(destinationFsa, transitTimeDataMap)));
    writer.flush();
  }

  private String[] constructCsvRowsForTransit(
      String destinationFsa, Map<String, Map<String, String>> transitTimeDataMap) {
    Map<String, String> sourceFsaAndMessageMap = transitTimeDataMap.get(destinationFsa);

    var rowContents =
        sourceFsaAndMessageMap.keySet().stream()
            .map(sourceFsa -> String.valueOf(sourceFsaAndMessageMap.get(sourceFsa)))
            .collect(Collectors.toCollection(ArrayList::new));
    rowContents.add(0, destinationFsa);

    return rowContents.toArray(new String[0]);
  }

  private TransitDataErrorLogsPojo getRequestBody(RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var errorLogsPojo =
        INSTANCE.convertToTransitDataErrorLogsPojo(
            gson.fromJson(recordStatusDto.getRequestBody(), DownloadErrorTransitData.class));
    errorLogsPojo.setErrorMessage(recordStatusDto.getErrorMessage());
    errorLogsPojo.setException(recordStatusDto.getException());
    return errorLogsPojo;
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_TRANSIT_TIMES;
  }
}
