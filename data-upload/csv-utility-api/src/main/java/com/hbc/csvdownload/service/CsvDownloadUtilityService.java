package com.hbc.csvdownload.service;

import static com.hbc.csvdownload.common.constants.CSVCommonConstants.ACTIVE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CARRIER_ID;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CARRIER_NAME;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CARRIER_SERVICES;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CARRIER_SERVICE_ID;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CITY;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.COUNTRY;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.ERROR_MESSAGE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.INACTIVE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.LATITUDE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.LONGITUDE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.NODE_ID;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.ORG_ID;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.POSTAL_CODE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.POSTAL_CODE_PREFIX;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.PROCESSING_LEAD_TIME;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.PROVINCE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.SERVICE_NAME;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTION;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTIONS;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.STATE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.STATUS;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.STREET;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.TIMEZONE;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.WORKING_CALENDER;

import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.hbc.csvdownload.domain.mapper.TransitDataRequestMapper;
import com.hbc.csvdownload.domain.pojo.DownloadErrorNodeCarrier;
import com.hbc.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.hbc.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import com.hbc.csvdownload.exception.CarrierServiceException;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.dataupload.common.feign.DataUploadFeign;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.newrelic.relocated.Gson;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CsvDownloadUtilityService {

  private static final TransitDataRequestMapper INSTANCE =
      Mappers.getMapper(TransitDataRequestMapper.class);
  private final Logger logger = LoggerFactory.getLogger(CsvDownloadUtilityService.class);
  private final PostalCodeTimeZoneService postalCodeTimeZoneService;
  private final TransitService transitService;
  private final DataUploadFeign dataUploadFeign;
  private final CarrierService carrierService;
  private final CalenderService calenderService;
  private final JobsDashboardService jobsDashboardService;
  private final JobsConsumerService jobsConsumerService;

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  public File downloadCarrierServiceDataCSV(String orgId)
      throws IOException, CarrierServiceException {

    List<CarrierServiceResponse> carrierServiceResponses = carrierService.getCarrierService(orgId);

    Path tempFile = Files.createTempFile("download-carrierService" + new Date().getTime(), ".csv");

    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {

      var headers =
          new String[] {
            CARRIER_SERVICE_ID,
            ORG_ID,
            CARRIER_NAME,
            CARRIER_ID,
            SERVICE_NAME,
            STATUS,
            WORKING_CALENDER
          };
      csvWriter.writeNext(headers);
      for (CarrierServiceResponse carrierServiceResponse : carrierServiceResponses) {
        String carrierServiceId = carrierServiceResponse.getCarrierServiceId();
        List<String> calenderIds = new ArrayList<>();
        List<CarrierServiceCalendarResponse> carrierServiceCalendarResponses = new ArrayList<>();
        getCalenderIds(orgId, carrierServiceId, calenderIds, carrierServiceCalendarResponses);
        var transitTimeEntriesDto = new TransitTimeEntriesDto();

        try {
          transitTimeEntriesDto = transitService.getTransitTimeEntries(orgId, carrierServiceId);
        } catch (TransitServiceException e) {
          transitTimeEntriesDto.setTotalRecords(0);
          logger.debug("No transit entries found");
        }

        String status =
            (!carrierServiceCalendarResponses.isEmpty()
                    && transitTimeEntriesDto.getTotalRecords() > 0)
                ? ACTIVE
                : INACTIVE;
        writeDataOntoFile(csvWriter, orgId, carrierServiceResponse, status, calenderIds);
        csvWriter.flush();
      }
    }
    return tempFile.toFile();
  }

  private void getCalenderIds(
      String orgId,
      String carrierServiceId,
      List<String> calenderIds,
      List<CarrierServiceCalendarResponse> serviceCalendarResponses) {
    try {

      serviceCalendarResponses.addAll(
          calenderService.getCarrierServiceCalender(orgId, carrierServiceId));

      calenderIds.addAll(
          serviceCalendarResponses.stream()
              .map(CarrierServiceCalendarResponse::getCalendarId)
              .collect(Collectors.toSet()));

    } catch (Exception e) {
      logger.error("Empty Carrier Service Calendar Response List");
    }
  }

  public String downloadTransitTimesForSourceAndDestinationRegion(
      String orgId, String carrierServiceId, String sourceRegion, String destinationRegion)
      throws CsvDownloadUtilityServiceException, TransitServiceException,
          PostalCodeTimezoneServiceException {
    logger.debug("Processing download transit times for source and destination regions");

    List<String> destinationFsaList =
        postalCodeTimeZoneService.getFSAsByOrgIdAndState(orgId, destinationRegion);

    Set<String> sourceFsaSet =
        new HashSet<>(postalCodeTimeZoneService.getFSAsByOrgIdAndState(orgId, sourceRegion));
    List<TransitResponse> filteredTransitResponseList =
        transitService.getTransitDetails(orgId, carrierServiceId, destinationFsaList).stream()
            .filter(transitResponse -> sourceFsaSet.contains(transitResponse.getSourceGeozone()))
            .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(filteredTransitResponseList)) {
      logger.error("No transit details available for source and destination FSAs");
      throw new CsvDownloadUtilityServiceException(
          "No transit details available for source and destination FSAs", orgId);
    }

    Set<String> filteredSourceFSASet =
        filteredTransitResponseList.stream()
            .map(TransitResponse::getSourceGeozone)
            .collect(Collectors.toSet());

    Map<String, Float> sourceAndTransitTimeMap = new TreeMap<>();
    filteredSourceFSASet.forEach(fsa -> sourceAndTransitTimeMap.put(fsa, null));

    Map<String, Map<String, Float>> transitTimesDataMap = new HashMap<>();
    filteredTransitResponseList.forEach(
        transitResponse -> {
          if (CollectionUtils.isEmpty(
              transitTimesDataMap.get(transitResponse.getDestinationGeozone()))) {
            transitTimesDataMap.put(
                transitResponse.getDestinationGeozone(), new HashMap<>(sourceAndTransitTimeMap));
          }
        });

    filteredTransitResponseList.forEach(
        transitResponse ->
            transitTimesDataMap
                .get(transitResponse.getDestinationGeozone())
                .put(transitResponse.getSourceGeozone(), transitResponse.getTransitDays()));

    var sourceFsaHeader = String.join(",", filteredSourceFSASet);
    return constructCsvData(orgId, carrierServiceId, transitTimesDataMap, sourceFsaHeader);
  }

  private String constructCsvData(
      String orgId,
      String carrierServiceId,
      Map<String, Map<String, Float>> transitTimesDataMap,
      String sourceFsaHeader) {
    var csvContents =
        String.format(
            "orgId,%s%nCarrier Service:,%s%nDestination FSA / Source FSA ->,%s",
            orgId, carrierServiceId, sourceFsaHeader);
    String rows =
        transitTimesDataMap.keySet().stream()
            .map(destinationFsa -> constructCsvRows(transitTimesDataMap, destinationFsa))
            .collect(Collectors.joining("\n"));
    return String.join("\n", csvContents, rows);
  }

  public String downloadLogsAsCsv(String jobId, String orgId, Optional<String> status)
      throws CommonServiceException {
    logger.debug("Processing download transit time and processing lead time");
    try {
      var jobDto = jobsConsumerService.getJob(jobId, orgId);
      var jobType = jobDto.getJobType();
      if (ObjectUtils.isEmpty(JobTypeEnum.valueOf(jobType.name()))) {
        throw new CommonServiceException(
            "Incorrect jobType specified", HttpStatus.BAD_REQUEST, 0x1772, null);
      }
      List<RecordStatusDto> recordStatusDtos =
          jobsDashboardService.getJobRecords(jobId, orgId, status);

      if (jobType.equals(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES)) {
        return downloadProcessingLeadTimeErrorLogs(recordStatusDtos);
      } else {
        return downloadTransitTimeErrorLogs(recordStatusDtos);
      }
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while downloading error logs as csv", HttpStatus.BAD_REQUEST, 0x1772, null);
    }
  }

  private String downloadProcessingLeadTimeErrorLogs(List<RecordStatusDto> recordStatusDtoList) {
    var header =
        String.join(",", NODE_ID, ORG_ID, SERVICE_OPTION, PROCESSING_LEAD_TIME, ERROR_MESSAGE);
    String rows =
        recordStatusDtoList.stream()
            .map(this::constructRowContent)
            .collect(Collectors.joining("\n"));

    return String.join("\n", header, rows);
  }

  private String constructRowContent(RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var requestBody =
        gson.fromJson(recordStatusDto.getRequestBody(), DownloadErrorNodeCarrier.class);
    return String.join(
        ",",
        requestBody.getNodeId(),
        requestBody.getOrgId(),
        requestBody.getServiceOption(),
        requestBody.getProcessingTime(),
        recordStatusDto.getErrorMessage());
  }

  private String downloadTransitTimeErrorLogs(List<RecordStatusDto> recordStatusDtoList)
      throws CommonServiceException {
    try {
      var transitDataErrorLogsList =
          recordStatusDtoList.stream().map(this::getRequestBody).collect(Collectors.toList());

      String orgId = transitDataErrorLogsList.get(0).getOrgId();
      String carrierServiceId = transitDataErrorLogsList.get(0).getCarrierServiceId();

      Set<String> sourceFsaSet =
          transitDataErrorLogsList.stream()
              .map(TransitDataErrorLogsPojo::getSourceGeozone)
              .collect(Collectors.toSet());

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

      var sourceFsaHeader = String.join(",", sourceFsaSet);

      return constructCsvDataForTransit(
          orgId, carrierServiceId, transitTimeErrorLogsMap, sourceFsaHeader);
    } catch (Exception e) {
      logger.error("Error while forming csv contents string");
      throw new CommonServiceException(
          "Error while forming csv contents string", HttpStatus.BAD_REQUEST, 0x1771, null);
    }
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

  private String constructCsvDataForTransit(
      String orgId,
      String carrierServiceId,
      Map<String, Map<String, String>> transitTimeDataMap,
      String sourceFsaHeader) {
    var csvContent =
        String.format(
            "orgId,%s%nCarrier Service:,%s%nDestination FSA / Source FSA ->,%s",
            orgId, carrierServiceId, sourceFsaHeader);

    String rows =
        transitTimeDataMap.keySet().stream()
            .map(destinationFsa -> constructCsvRowsForTransit(destinationFsa, transitTimeDataMap))
            .collect(Collectors.joining("\n"));
    return String.join("\n", csvContent, rows);
  }

  private String constructCsvRowsForTransit(
      String destinationFsa, Map<String, Map<String, String>> transitTimeDataMap) {
    Map<String, String> sourceFsaAndMessageMap = transitTimeDataMap.get(destinationFsa);
    String row =
        sourceFsaAndMessageMap.keySet().stream()
            .map(sourceFsa -> String.valueOf(sourceFsaAndMessageMap.get(sourceFsa)))
            .collect(Collectors.joining(","));

    return String.join(",", destinationFsa, row);
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

  public String downloadMarketRegionForOrgIdAndCountry(String orgId, String country)
      throws PostalCodeTimezoneServiceException {
    logger.debug("Processing download market regions for orgId and country");
    List<PostalCodeTimezoneDto> postalCodeTimezoneDtoList =
        postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(orgId, country);
    var header =
        String.join(
            ",", ORG_ID, POSTAL_CODE_PREFIX, COUNTRY, STATE, CITY, LATITUDE, LONGITUDE, TIMEZONE);
    var rows =
        postalCodeTimezoneDtoList.stream().map(this::createRow).collect(Collectors.joining("\n"));

    return String.join("\n", header, rows);
  }

  private String createRow(PostalCodeTimezoneDto dto) {
    return dto.getOrgId()
        + ","
        + dto.getPostalCodePrefix()
        + ","
        + dto.getCountry()
        + ","
        + dto.getState()
        + ","
        + dto.getCity()
        + ","
        + dto.getLatitude()
        + ","
        + dto.getLongitude()
        + ","
        + dto.getTimeZone();
  }

  private String constructCsvRows(
      Map<String, Map<String, Float>> transitTimesDataMap, String destinationFsa) {
    var sourceFsaAndTransitTimesMap = transitTimesDataMap.get(destinationFsa);
    String transitTimes =
        sourceFsaAndTransitTimesMap.keySet().stream()
            .map(sourceFsa -> String.valueOf(sourceFsaAndTransitTimesMap.get(sourceFsa)))
            .collect(Collectors.joining(","));
    return String.join(",", destinationFsa, transitTimes);
  }

  private void writeDataOntoFile(
      CSVWriter csvWriter,
      String orgId,
      CarrierServiceResponse carrierServiceResponse,
      String status,
      List<String> calenderIds) {

    calenderIds.forEach(
        calenderId ->
            csvWriter.writeNext(
                new String[] {
                  carrierServiceResponse.getCarrierServiceId(),
                  orgId,
                  carrierServiceResponse.getCarrierName(),
                  carrierServiceResponse.getCarrierId(),
                  carrierServiceResponse.getServiceName(),
                  status,
                  calenderId
                }));
  }

  public DownloadNodeCarrierServiceAndServiceOptionPojo
      downloadNodeCarrierServiceAndServiceOptionsDataCSV(String orgId) throws IOException {

    /** feign client call to get data */
    BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>> response =
        dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            orgId, null, noOfRecordsPerPage, null, null);
    /** Create a temporary file to write the data */
    Path tempFile = Files.createTempFile("download-node-carrierService-serviceOption", ".csv");
    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var headers =
          new String[] {
            NODE_ID,
            ORG_ID,
            STREET,
            CITY,
            PROVINCE,
            POSTAL_CODE,
            CARRIER_SERVICES,
            SERVICE_OPTIONS,
            STATUS
          };
      Integer currentPageNo = 1;
      Integer totalPages = 0;
      List<NodeCarrierServiceAndServiceOptionResponse> responses = new ArrayList<>();
      if (response != null && response.getPayload() != null) {
        csvWriter.writeNext(headers);
        responses = response.getPayload().getData();
        totalPages = response.getPayload().getPagination().getTotalPages();
        currentPageNo = response.getPayload().getPagination().getCurrentPage();
      }

      writeDataOntoFile(csvWriter, responses);
      csvWriter.flush();

      if (totalPages > 0) {
        while (currentPageNo < totalPages) {
          BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>> response2 =
              dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
                  orgId, currentPageNo + 1, noOfRecordsPerPage, null, null);
          if (response2 != null && response2.getPayload() != null) {
            responses = response2.getPayload().getData();
            totalPages = response2.getPayload().getPagination().getTotalPages();
            currentPageNo = response2.getPayload().getPagination().getCurrentPage();
            writeDataOntoFile(csvWriter, responses);
            csvWriter.flush();
          }
        }
      }

      return DownloadNodeCarrierServiceAndServiceOptionPojo.builder()
          .contentsLength(tempFile.toFile().length())
          .fileContents(Files.readAllBytes(tempFile))
          .build();
    } finally {
      tempFile.toFile().delete(); // NOSONAR
    }
  }

  private void writeDataOntoFile(
      CSVWriter csvWriter, List<NodeCarrierServiceAndServiceOptionResponse> responses) {

    if (!CollectionUtils.isEmpty(responses)) {
      responses.forEach(
          response -> {
            var nodeId = response.getNodeId();
            var orgId = response.getOrgId();
            var street = response.getStreet();
            var city = response.getCity();
            var province = response.getProvince();
            var postalCode = response.getPostalCode();
            response
                .getActiveCombination()
                .forEach(
                    activeCombination ->
                        csvWriter.writeNext(
                            new String[] {
                              nodeId,
                              orgId,
                              street,
                              city,
                              province,
                              postalCode,
                              activeCombination.getCarrierServiceId(),
                              activeCombination.getServiceOption(),
                              String.valueOf(activeCombination.isActive())
                            }));
          });
    }
  }
}
