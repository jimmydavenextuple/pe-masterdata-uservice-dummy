/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.*;
import static com.nextuple.csvdownload.util.NodeCalendarUtil.getActiveCalendarForNodeIdAndCarrier;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FAILURE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.PE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.PICKUP_CALENDAR_ID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.nextuple.csvdownload.domain.mapper.TransitDataRequestMapper;
import com.nextuple.csvdownload.domain.pojo.DownloadErrorNodeCarrier;
import com.nextuple.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.nextuple.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import com.nextuple.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.csvdownload.service.v1.ProcessingRequestFactory;
import com.nextuple.csvdownload.service.v1.ProcessingRequestInterface;
import com.nextuple.csvdownload.service.v1.impl.NeipProcessingRequestImpl;
import com.nextuple.csvdownload.util.CsvUtil;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.feign.DataUploadFeign;
import com.nextuple.dataupload.common.outbound.NodeAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.*;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.plt.client.FPMServiceClient;
import com.nextuple.plt.domain.pojo.JobRecordDto;
import com.nextuple.plt.exception.JobRecordException;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.feign.HolidayCutoffUIFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.sourcing.cost.config.dto.CostFactorHeadersInfoDto;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.feign.ITransitBufferFeign;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CsvDownloadUtilityService {

  private static final TransitDataRequestMapper INSTANCE =
      Mappers.getMapper(TransitDataRequestMapper.class);
  private final Logger logger = LoggerFactory.getLogger(CsvDownloadUtilityService.class);
  private final PostalCodeTimeZoneResponseService postalCodeTimeZoneResponseService;
  private final TransitResponseService transitResponseService;
  private final DataUploadFeign dataUploadFeign;
  private final PostalCodeFeign postalCodeFeign;
  private final CarrierResponseService carrierResponseService;
  private final CalenderResponseService calenderResponseService;
  private final JobsDashboardService jobsDashboardService;
  private final JobsConsumerService jobsConsumerService;
  private final ProcessingTimeBuffersService processingTimeBuffersService;
  private final NodeResponseService nodeResponseService;
  private final NodeCarrierResponseService nodeCarrierResponseService;
  private final ProcessingRequestFactory processingRequestFactory;
  private final HolidayCutoffUIFeign holidayCutoffUIFeign;
  private ITransitBufferFeign<?, ?> transitBufferFeign;
  private final TransferScheduleFeign transferScheduleFeign;
  private final CostDefinitionService costDefinitionService;
  private static final String NA = "NA";
  private static final String COLUMN_SPLITTER = "/";
  private static final String EMPTY_STRING = "";
  private static final Set<JobTypeEnum> GRID_JOB_UPLOADS =
      Set.of(
          JobTypeEnum.UPLOAD_TRANSIT_TIMES,
          JobTypeEnum.UPLOAD_ZONES,
          JobTypeEnum.UPLOAD_COST_DEFINITION);
  @Autowired FPMServiceClient fpmServiceClient;
  @Autowired Environment env;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired NeipProcessingRequestImpl neipProcessingRequest;

  private final TenantDatabaseConfig tenantDatabaseConfig;

  @Autowired
  public CsvDownloadUtilityService(
      PostalCodeTimeZoneResponseService postalCodeTimeZoneResponseService,
      TransitResponseService transitResponseService,
      DataUploadFeign dataUploadFeign,
      PostalCodeFeign postalCodeFeign,
      CarrierResponseService carrierResponseService,
      CalenderResponseService calenderResponseService,
      JobsDashboardService jobsDashboardService,
      JobsConsumerService jobsConsumerService,
      ProcessingTimeBuffersService processingTimeBuffersService,
      NodeResponseService nodeResponseService,
      NodeCarrierResponseService nodeCarrierResponseService,
      ProcessingRequestFactory processingRequestFactory,
      HolidayCutoffUIFeign holidayCutoffUIFeign,
      ITransitBufferFeign<?, ?> transitBufferFeign,
      TransferScheduleFeign transferScheduleFeign,
      CostDefinitionService costDefinitionService,
      TenantDatabaseConfig tenantDatabaseConfig) {
    this.postalCodeTimeZoneResponseService = postalCodeTimeZoneResponseService;
    this.transitResponseService = transitResponseService;
    this.dataUploadFeign = dataUploadFeign;
    this.postalCodeFeign = postalCodeFeign;
    this.carrierResponseService = carrierResponseService;
    this.calenderResponseService = calenderResponseService;
    this.jobsDashboardService = jobsDashboardService;
    this.jobsConsumerService = jobsConsumerService;
    this.processingTimeBuffersService = processingTimeBuffersService;
    this.nodeResponseService = nodeResponseService;
    this.nodeCarrierResponseService = nodeCarrierResponseService;
    this.processingRequestFactory = processingRequestFactory;
    this.holidayCutoffUIFeign = holidayCutoffUIFeign;
    this.transitBufferFeign = transitBufferFeign;
    this.transferScheduleFeign = transferScheduleFeign;
    this.costDefinitionService = costDefinitionService;
    this.tenantDatabaseConfig = tenantDatabaseConfig;
  }

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  @Value("${csv-utility-api.csv-download.max-no-of-carrier-services-for-download}")
  public Integer maxNoOfCarrierServicesForDownload;

  public File downloadCarrierServiceDataCSV(String orgId)
      throws IOException, CarrierServiceException {

    List<CarrierServiceResponse> carrierServiceResponses =
        carrierResponseService.getCarrierService(orgId).stream()
            .limit(maxNoOfCarrierServicesForDownload)
            .toList();
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-carrierService" + new Date().getTime(), ".csv", attr);

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
      carrierServiceResponses.parallelStream()
          .forEach(
              carrierServiceResponse -> {
                String carrierServiceId = carrierServiceResponse.getCarrierServiceId();
                List<CarrierServiceCalendarResponse> carrierServiceCalendarResponses =
                    new ArrayList<>();
                String calenderId =
                    getCalendarId(orgId, carrierServiceId, carrierServiceCalendarResponses);

                TransitTimeEntriesDto transitTimeEntriesDto =
                    getTransitTimeEntriesDto(orgId, carrierServiceId);

                String status =
                    (!carrierServiceCalendarResponses.isEmpty()
                            && transitTimeEntriesDto.getTotalRecords() > 0)
                        ? ACTIVE
                        : INACTIVE;

                writeDataOntoFile(csvWriter, orgId, carrierServiceResponse, status, calenderId);
              });
    }
    return tempFile.toFile();
  }

  private TransitTimeEntriesDto getTransitTimeEntriesDto(String orgId, String carrierServiceId) {
    var transitTimeEntriesDto = new TransitTimeEntriesDto();

    try {
      transitTimeEntriesDto = transitResponseService.getTransitTimeEntries(orgId, carrierServiceId);
    } catch (TransitServiceException e) {
      transitTimeEntriesDto.setTotalRecords(0);
      logger.debug("No transit entries found");
    }
    return transitTimeEntriesDto;
  }

  private String getCalendarId(
      String orgId,
      String carrierServiceId,
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponses) {
    try {

      carrierServiceCalendarResponses.addAll(
          calenderResponseService.getCarrierServiceCalender(orgId, carrierServiceId));

      Optional<CarrierServiceCalendarResponse> activeCalendarResponse =
          DataUploadUtil.getActiveCalendarResponse(carrierServiceCalendarResponses);

      return activeCalendarResponse.isPresent() ? activeCalendarResponse.get().getCalendarId() : NA;

    } catch (Exception e) {
      // No Calendar exist for given orgId and carrierServiceId
      logger.error("Empty Carrier Service Calendar Response List");
      return NA;
    }
  }

  public String downloadTransitTimesForSourceAndDestinationRegion(
      String orgId, String carrierServiceId, String sourceRegion, String destinationRegion)
      throws CsvDownloadUtilityServiceException,
          TransitServiceException,
          PostalCodeTimezoneServiceException {
    logger.debug("Processing download transit times for source and destination regions");

    List<String> destinationFsaList =
        postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(orgId, destinationRegion);

    Set<String> sourceFsaSet =
        new HashSet<>(
            postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(orgId, sourceRegion));
    List<TransitResponse> filteredTransitResponseList =
        transitResponseService
            .getTransitDetails(orgId, carrierServiceId, destinationFsaList)
            .stream()
            .filter(transitResponse -> sourceFsaSet.contains(transitResponse.getSourceGeozone()))
            .toList();

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
                transitResponse.getDestinationGeozone(), new TreeMap<>(sourceAndTransitTimeMap));
          }
        });

    filteredTransitResponseList.forEach(
        transitResponse ->
            transitTimesDataMap
                .get(transitResponse.getDestinationGeozone())
                .put(transitResponse.getSourceGeozone(), transitResponse.getTransitDays()));

    var sourceFsaHeader = String.join(",", sourceAndTransitTimeMap.keySet());
    return constructCsvData(orgId, carrierServiceId, transitTimesDataMap, sourceFsaHeader);
  }

  private String constructCsvData(
      String orgId,
      String carrierServiceId,
      Map<String, Map<String, Float>> transitTimesDataMap,
      String sourceFsaHeader) {
    var csvContents =
        "orgId,%s%nCarrier Service:,%s%nDestination FSA / Source FSA ->,%s"
            .formatted(orgId, carrierServiceId, sourceFsaHeader);
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
          recordStatusDtoList.stream().map(this::getRequestBody).toList();

      String orgId = transitDataErrorLogsList.get(0).getOrgId();
      String carrierServiceId = transitDataErrorLogsList.get(0).getCarrierServiceId();

      Set<String> sourceFsaSet =
          transitDataErrorLogsList.stream()
              .map(TransitDataErrorLogsPojo::getSourceGeozone)
              .collect(Collectors.toSet());

      Map<String, String> sourceAndTransitTimeMap = new LinkedHashMap<>();

      sourceFsaSet.forEach(fsa -> sourceAndTransitTimeMap.put(fsa, null));

      Map<String, Map<String, String>> transitTimeErrorLogsMap = new HashMap<>();

      transitDataErrorLogsList.forEach(
          transitRequest -> {
            if (CollectionUtils.isEmpty(
                transitTimeErrorLogsMap.get(transitRequest.getDestinationGeozone()))) {
              transitTimeErrorLogsMap.put(
                  transitRequest.getDestinationGeozone(),
                  new LinkedHashMap<>(sourceAndTransitTimeMap));
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
        "orgId,%s%nCarrier Service:,%s%nDestination geoZone / Source geoZone ->,%s"
            .formatted(orgId, carrierServiceId, sourceFsaHeader);

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
    List<PostalCodeResponse> postalCodeTimezoneDtoList =
        postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(orgId, country);
    var header =
        String.join(
            ",",
            ORG_ID,
            ZIP_CODE,
            ZIP_CODE_PREFIX,
            COUNTRY,
            STATE,
            CITY,
            LATITUDE,
            LONGITUDE,
            TIMEZONE);
    var rows =
        postalCodeTimezoneDtoList.stream().map(this::createRow).collect(Collectors.joining("\n"));

    return String.join("\n", header, rows);
  }

  private String createRow(PostalCodeResponse dto) {

    return dto.getOrgId()
        + ","
        + dto.getZipCode()
        + ","
        + dto.getZipCodePrefix()
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
      String calendarId) {

    csvWriter.writeNext(
        new String[] {
          carrierServiceResponse.getCarrierServiceId(),
          orgId,
          carrierServiceResponse.getCarrierName(),
          carrierServiceResponse.getCarrierId(),
          carrierServiceResponse.getServiceName(),
          status,
          calendarId
        });
    try {
      csvWriter.flush();
    } catch (IOException e) {
      logger.error("Error occurred while flushing file");
    }
  }

  public DownloadNodeCarrierServiceAndServiceOptionPojo
      downloadNodeCarrierServiceAndServiceOptionsDataCSV(String orgId) throws IOException {

    /** feign client call to get data */
    BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>> response =
        dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            orgId, null, noOfRecordsPerPage, null, null);
    /** Create a temporary file to write the data */
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile(
            "download-node-carrierService-serviceOption" + new Date().getTime(), ".csv", attr);
    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var headers =
          new String[] {
            NODE_ID,
            ORG_ID,
            STREET,
            CITY,
            STATE,
            ZIP_CODE,
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
            var state = response.getState();
            var zipCode = response.getZipCode();
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
                              state,
                              zipCode,
                              activeCombination.getCarrierServiceId(),
                              activeCombination.getServiceOption(),
                              activeCombination.isActive() ? ACTIVE : INACTIVE
                            }));
          });
    }
  }

  public DownloadNodeCarrierServiceAndServiceOptionPojo downloadNodeAndServiceOptionsDataCSV(
      String orgId) throws IOException {

    /** feign client call to get data */
    BaseResponse<PagePayload<NodeAndServiceOptionResponse>> response =
        dataUploadFeign.getNodeServiceOption(orgId, null, noOfRecordsPerPage, null, null);
    /** Create a temporary file to write the data */
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-node-serviceOption" + new Date().getTime(), ".csv", attr);
    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var headers =
          new String[] {
            NODE_ID,
            ORG_ID,
            NODE_TYPE,
            STREET,
            CITY,
            STATE,
            STATUS,
            SERVICE_OPTIONS,
            PROCESSING_LEAD_TIME
          };
      Integer currentPageNo = 1;
      Integer totalPages = 0;
      List<NodeAndServiceOptionResponse> responses = new ArrayList<>();
      if (response != null && response.getPayload() != null) {
        csvWriter.writeNext(headers);
        responses = response.getPayload().getData();
        totalPages = response.getPayload().getPagination().getTotalPages();
        currentPageNo = response.getPayload().getPagination().getCurrentPage();
      }

      writeNodeAndServiceOptionDataOntoFile(csvWriter, responses);
      csvWriter.flush();

      if (totalPages > 0) {
        while (currentPageNo < totalPages) {
          BaseResponse<PagePayload<NodeAndServiceOptionResponse>> response2 =
              dataUploadFeign.getNodeServiceOption(
                  orgId, currentPageNo + 1, noOfRecordsPerPage, null, null);
          if (response2 != null && response2.getPayload() != null) {
            responses = response2.getPayload().getData();
            totalPages = response2.getPayload().getPagination().getTotalPages();
            currentPageNo = response2.getPayload().getPagination().getCurrentPage();
            writeNodeAndServiceOptionDataOntoFile(csvWriter, responses);
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

  public InputStream downloadCostDefinitionForOrgId(String orgId, CostDefinitionRequest request)
      throws IOException, CommonServiceException {
    logger.debug("Processing download cost definition for orgId {}", orgId);
    CostTypeValidationResponse costTypeValidationResponse =
        costDefinitionService.getCostTypeValidationResponse(orgId, request.getCostType());
    CostDefinitionResponse costDefinitionResponse =
        costDefinitionService.getCostDefinitionResponse(orgId, request, costTypeValidationResponse);

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream))) {
      writeCurrencyResponseOntoFile(csvWriter, costTypeValidationResponse.getCurrency());
      writeSelectorsAndFiltersOntoFile(csvWriter, orgId, request, costTypeValidationResponse);
      writeRateCardDataOntoFile(csvWriter, costDefinitionResponse);
      csvWriter.flush();
      return new ByteArrayInputStream(outputStream.toByteArray());
    }
  }

  private void writeCurrencyResponseOntoFile(CSVWriter writer, String currency) {
    writeToCSV(new String[] {"Notes: Filled in values will be in " + currency}, writer);
  }

  private void writeSelectorsAndFiltersOntoFile(
      CSVWriter writer,
      String orgId,
      CostDefinitionRequest request,
      CostTypeValidationResponse costDefinitionResponse) {
    writeToCSV(new String[] {"Org Id", orgId}, writer);
    writeToCSV(new String[] {"Cost Type", request.getCostType()}, writer);
    SelectorCostFactorInfoDto selector = request.getSelector();

    List<FilterCostFactorInfoDto> filters = request.getFilters();
    Map<String, String> cfDisplayNameMap = getCfDisplayNameMap(costDefinitionResponse);

    if (!ObjectUtils.isEmpty(selector)) {
      writeToCSV(
          new String[] {
            checkForNullValues(cfDisplayNameMap.get(selector.getSelectorCf())),
            selector.getSelectorCfValue()
          },
          writer);
    }
    if (!CollectionUtils.isEmpty(filters)) {
      filters.forEach(
          filter ->
              writeToCSV(
                  new String[] {
                    checkForNullValues(cfDisplayNameMap.get(filter.getCostFactor())),
                    filter.getCostFactorValue()
                  },
                  writer));
    }
  }

  private Map<String, String> getCfDisplayNameMap(
      CostTypeValidationResponse costDefinitionResponse) {
    return StringUtils.hasLength(costDefinitionResponse.getSelectorCf())
        ? getCfDisplayNameMapForSelector(costDefinitionResponse)
        : getCfDisplayNameMapForNonSelector(costDefinitionResponse);
  }

  private Map<String, String> getCfDisplayNameMapForSelector(
      CostTypeValidationResponse costDefinitionResponse) {
    Map<String, String> cfDisplayNameMap = new HashMap<>();
    cfDisplayNameMap.put(
        costDefinitionResponse.getSelectorCf(), costDefinitionResponse.getSelectorCfDisplayName());
    costDefinitionResponse
        .getSelectorCfInfo()
        .forEach(
            selectorCfInfo ->
                selectorCfInfo
                    .getCostFactors()
                    .forEach(cf -> cfDisplayNameMap.put(cf.getCostFactor(), cf.getDisplayName())));
    return cfDisplayNameMap;
  }

  private Map<String, String> getCfDisplayNameMapForNonSelector(
      CostTypeValidationResponse costDefinitionResponse) {
    Map<String, String> cfDisplayNameMap = new HashMap<>();
    costDefinitionResponse
        .getCostFactors()
        .forEach(cf -> cfDisplayNameMap.put(cf.getCostFactor(), cf.getDisplayName()));
    return cfDisplayNameMap;
  }

  private void writeRateCardDataOntoFile(CSVWriter writer, CostDefinitionResponse response) {
    String[] headerMetas = getHeaderMetas(response);
    String[] headerNames = getHeaderNames(response);

    if (isStaticTable(writer, response, headerMetas)) return;

    String dynamicRowData = dynamicRowData(response, headerNames, headerMetas);
    var rowDataFromRateCardResponse = response.getRows().getData();
    var lastRowData = rowDataFromRateCardResponse.get(rowDataFromRateCardResponse.size() - 1);
    String isDynamicValue = lastRowData.getOrDefault("isDynamicBucket", "false");

    writeToCSV(new String[] {dynamicRowData, isDynamicValue.toUpperCase()}, writer);

    if (StringUtils.hasLength(response.getColumns().getTitle()))
      headerNames[0] = headerNames[0] + COLUMN_SPLITTER + response.getColumns().getTitle();
    else headerNames[1] = EMPTY_STRING;

    writeToCSV(headerNames, writer);

    for (Map<String, String> data : response.getRows().getData()) {
      String[] rowData = new String[headerMetas.length];
      for (int columnIndex = 0; columnIndex < headerMetas.length; columnIndex++) {
        rowData[columnIndex] = data.getOrDefault(headerMetas[columnIndex], NA);
      }
      writeToCSV(rowData, writer);
    }
  }

  private static String dynamicRowData(
      CostDefinitionResponse response, String[] headerNames, String[] headerMetas) {
    return "Dynamic - incremental per pound cost bucket ( "
        + headerNames[0].trim()
        + ": "
        + response
            .getRows()
            .getData()
            .get(response.getRows().getData().size() - 1)
            .getOrDefault(headerMetas[0], NA)
            .trim()
        + ")";
  }

  private boolean isStaticTable(
      CSVWriter writer, CostDefinitionResponse response, String[] headerMetas) {
    if (headerMetas.length == 1) {
      String[] rowData = {
        response.getColumns().getHeaders().get(0).getColumnName(),
        response.getRows().getData().get(0).getOrDefault(headerMetas[0], NA)
      };
      writeToCSV(rowData, writer);
      return true;
    }
    return false;
  }

  private String[] getHeaderMetas(CostDefinitionResponse response) {
    return response.getColumns().getHeaders().stream()
        .map(CostFactorHeadersInfoDto::getColumnMeta)
        .toArray(String[]::new);
  }

  private String[] getHeaderNames(CostDefinitionResponse response) {
    return response.getColumns().getHeaders().stream()
        .map(CostFactorHeadersInfoDto::getColumnName)
        .toArray(String[]::new);
  }

  private void writeNodeAndServiceOptionDataOntoFile(
      CSVWriter csvWriter, List<NodeAndServiceOptionResponse> responses) {

    if (!CollectionUtils.isEmpty(responses)) {
      responses.forEach(
          response -> {
            var nodeId = response.getNodeId();
            var orgId = response.getOrgId();
            var nodeType = response.getNodeType();
            var street = response.getStreet();
            var city = response.getCity();
            var state = response.getState();
            var isActive = response.getIsActive();
            response
                .getProcessingTime()
                .forEach(
                    (serviceOption, processingTime) ->
                        csvWriter.writeNext(
                            new String[] {
                              nodeId,
                              orgId,
                              nodeType,
                              street,
                              city,
                              state,
                              Boolean.TRUE.equals(isActive) ? ACTIVE : INACTIVE,
                              serviceOption,
                              processingTime.toString()
                            }));
          });
    }
  }

  public File downloadProcessingTimeBuffersByOrgId(String orgId, String nodeIds)
      throws IOException {
    logger.debug("Processing download processing time buffers for orgId");
    List<ProcessingTimeBufferResponse> responses =
        processingTimeBuffersService.getProcessingTimeBuffers(orgId, nodeIds);

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile(
            "download-processing-time-buffers" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var header =
          new String[] {
            NODE_ID,
            ORG_ID,
            NODE_TYPE,
            STREET,
            CITY,
            STATE,
            ZIP_CODE,
            SERVICE_OPTION,
            BUFFER_HOURS,
            BUFFER_START_DATE,
            BUFFER_END_DATE,
            STATUS
          };
      writeToCSV(header, writer);

      writerProcessingTimeBufferDataToFile(writer, responses);
      writer.flush();
    }

    return tempFile.toFile();
  }

  private void writerProcessingTimeBufferDataToFile(
      CSVWriter writer, List<ProcessingTimeBufferResponse> responseList) {

    responseList.forEach(
        response -> {
          if (response.getServiceOptions().isEmpty()
              && response.getProcessingTimeBuffers().isEmpty()) {
            List<String> csvData =
                addNodeDetails(
                    response.getNodeId(),
                    response.getOrgId(),
                    response.getNodeType(),
                    response.getStreet(),
                    response.getCity(),
                    response.getState(),
                    response.getZipCode());
            csvData.add(NA);
            csvData.add(NA);
            csvData.add(NA);
            csvData.add(NA);
            csvData.add(NA);
            writeToCSV(csvData.toArray(new String[0]), writer);
          } else {
            response
                .getProcessingTimeBuffers()
                .forEach(
                    processingTimeBuffer -> {
                      List<String> csvData =
                          addNodeDetails(
                              response.getNodeId(),
                              response.getOrgId(),
                              response.getNodeType(),
                              response.getStreet(),
                              response.getCity(),
                              response.getState(),
                              response.getZipCode());
                      csvData.add(processingTimeBuffer.getServiceOption());
                      csvData.add(checkForNullValues(processingTimeBuffer.getBufferHours()));
                      csvData.add(
                          checkForNullValues(
                              convertToStringUTC(processingTimeBuffer.getBufferStartDate())));
                      csvData.add(
                          checkForNullValues(
                              convertToStringUTC(processingTimeBuffer.getBufferEndDate())));
                      csvData.add(checkForNullValues(processingTimeBuffer.getStatus()));
                      writeToCSV(csvData.toArray(new String[0]), writer);
                    });
          }
        });
  }

  private List<String> addNodeDetails(
      String nodeId,
      String orgId,
      String nodeType,
      String street,
      String city,
      String state,
      String zipCode) {
    List<String> csvData = new ArrayList<>();
    csvData.add(nodeId);
    csvData.add(orgId);
    csvData.add(nodeType);
    csvData.add(street);
    csvData.add(city);
    csvData.add(state);
    csvData.add(zipCode);
    return csvData;
  }

  private List<String> addRequiredNodeDetails(NodeDto node) {
    List<String> csvData = new ArrayList<>();
    csvData.add(node.getNodeId());
    csvData.add(node.getOrgId());
    csvData.add(node.getNodeType());
    csvData.add(node.getNodeLabourTier());
    csvData.add(node.getStreet());
    csvData.add(node.getCity());
    csvData.add(node.getState());
    csvData.add(node.getZipCode());
    return csvData;
  }

  private void writeToCSV(String[] data, CSVWriter writer) {
    writer.writeNext(data);
  }

  private String checkForNullValues(Object value) {
    return (value == null) ? NA : value.toString();
  }

  private String convertToStringUTC(Date value) {
    if (value != null) {
      return value.toInstant().toString();
    }
    return null;
  }

  private Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }

  public File downloadNodesByOrgId(String orgId, String nodeIds, String nodeType)
      throws IOException, CommonServiceException {
    List<NodeDto> nodeDtoList = nodeResponseService.getNodeList(orgId, nodeIds, nodeType);

    String serviceOptionString = tenantDatabaseConfig.fetchServiceOptions(orgId);
    String[] serviceOptions = serviceOptionString.split(",");
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile = Files.createTempFile("download-nodes" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      List<String> header =
          new ArrayList<>(
              Arrays.asList(
                  NODE_ID,
                  ORG_ID,
                  NODE_TYPE,
                  NODE_LABOUR_TIER,
                  STREET,
                  CITY,
                  STATE,
                  ZIP_CODE,
                  LATITUDE,
                  LONGITUDE,
                  TIMEZONE,
                  STATUS,
                  NODE_WORKING_CALENDAR,
                  START_WORKING_TIME,
                  LAST_WORKING_TIME));
      List<String> configuredEligibilities =
          Arrays.stream(serviceOptions).map(option -> option.toLowerCase() + "Eligible").toList();
      header.addAll(configuredEligibilities);
      writeToCSV(header.toArray(new String[0]), writer);
      writerNodesDataToFile(writer, nodeDtoList, configuredEligibilities);
      writer.flush();
    }

    return tempFile.toFile();
  }

  public File downloadNodesCarrierPickupCalendarByOrgId(String orgId) throws IOException {
    List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarList =
        calenderResponseService.getNodeCarrierServiceCalender(orgId);
    Map<String, NodeCarrierServiceCalendarResponse> uniqueCalendarMap =
        nodeCarrierServiceCalendarList.stream()
            .collect(
                Collectors.groupingBy(
                    x -> x.getNodeId() + "-" + x.getCarrierServiceId(),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        calendars ->
                            getActiveCalendarForNodeIdAndCarrier(calendars).orElse(null))));
    List<NodeCarrierServiceCalendarResponse> filteredList =
        uniqueCalendarMap.values().stream().filter(Objects::nonNull).toList();
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile(
            "download-nodes-carrier-pickup-calendar" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      List<String> header =
          new ArrayList<>(
              Arrays.asList(
                  ORG_ID, NODE_ID, CARRIER_SERVICE_ID, LAST_PICKUP_TIME, PICKUP_CALENDAR_ID));
      writeToCSV(header.toArray(new String[0]), writer);
      writeNodeCarrierPickupCalendar(writer, filteredList);
      writer.flush();
    }
    return tempFile.toFile();
  }

  private void writerNodesDataToFile(
      CSVWriter writer, List<NodeDto> nodeDtoList, List<String> serviceOptionsEligibilities) {
    for (NodeDto node : nodeDtoList) {
      List<NodeCalendarResponse> nodeCalendarResponses =
          calenderResponseService.getNodeCalendar(node.getOrgId(), node.getNodeId());
      String nodeWorkingCalendar =
          CollectionUtils.isEmpty(nodeCalendarResponses)
              ? "NA"
              : nodeCalendarResponses.get(0).getCalendarId();
      List<String> csvData = addRequiredNodeDetails(node);
      csvData.add(node.getLatitude());
      csvData.add(node.getLongitude());
      csvData.add(node.getTimezone());
      csvData.add(node.getIsActive().equals(Boolean.TRUE) ? "ACTIVE" : "INACTIVE");
      csvData.add(nodeWorkingCalendar);
      csvData.add(node.getStartWorkingTime());
      csvData.add(node.getLastWorkingTime());
      for (String serviceOptionEligibility : serviceOptionsEligibilities) {
        csvData.add(
            node.getServiceOptionEligibilities()
                .getOrDefault(serviceOptionEligibility, Boolean.FALSE)
                .toString());
      }
      writeToCSV(csvData.toArray(new String[0]), writer);
    }
  }

  private void writeNodeCarrierPickupCalendar(
      CSVWriter writer, List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarList) {
    for (NodeCarrierServiceCalendarResponse nodeCarrierServiceCalendarResponse :
        nodeCarrierServiceCalendarList) {
      List<NodeCarrierResponse> nodeCarrierResponses =
          nodeCarrierResponseService.getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
              nodeCarrierServiceCalendarResponse.getOrgId(),
              nodeCarrierServiceCalendarResponse.getNodeId(),
              nodeCarrierServiceCalendarResponse.getCarrierServiceId());
      List<String> csvData = new ArrayList<>();
      for (NodeCarrierResponse nodeCarrierResponse : nodeCarrierResponses) {
        csvData.add(nodeCarrierServiceCalendarResponse.getOrgId());
        csvData.add(nodeCarrierServiceCalendarResponse.getNodeId());
        csvData.add(nodeCarrierServiceCalendarResponse.getCarrierServiceId());
        csvData.add(
            Objects.nonNull(nodeCarrierResponse.getLastPickupTime())
                ? nodeCarrierResponse.getLastPickupTime()
                : "N/A");
        csvData.add(nodeCarrierServiceCalendarResponse.getCalendarId());
        writeToCSV(csvData.toArray(new String[0]), writer);
      }
    }
  }

  public PreSignedUrlResponse downloadLogsAsCsvV2(
      String jobId, String orgId, Optional<String> status, String origin)
      throws CommonServiceException {
    try {
      if (origin.equals(PE)) {
        return downloadLogsAsCsvV1(jobId, orgId, status);
      }
      com.nextuple.plt.domain.pojo.JobDto jobDtoFromFpm =
          fpmServiceClient.getJob(orgId, jobId).getBody();
      String feedType =
          Optional.ofNullable(jobDtoFromFpm)
              .map(com.nextuple.plt.domain.pojo.JobDto::getFeedType)
              .orElse("");
      String csvContent = getCsvContentForNeipRecordErrorLogs(jobId, orgId, feedType);
      Path tempFile = getPath(csvContent, "download-log-" + feedType);
      JobDto jobDto = new JobDto();
      jobDto.setJobId(jobId);
      return neipProcessingRequest.generateURLResponse(tempFile.toFile(), jobDto, feedType);
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while downloading error logs as csv", HttpStatus.BAD_REQUEST, 0x1772, null);
    }
  }

  private Path getPath(String csvContent, String fileName) throws IOException {
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile = Files.createTempFile(fileName + new Date().getTime(), ".csv", attr);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
      writer.write(csvContent);
    }
    return tempFile;
  }

  private String getCsvContentForNeipRecordErrorLogs(String jobId, String orgId, String feedType)
      throws JobRecordException {
    List<JobRecordDto> failedJobRecords =
        fpmServiceClient.getJobRecordByJobIdAndStatus(orgId, jobId, FAILURE).getBody();

    String rows = null;
    if (Objects.nonNull(failedJobRecords)) {
      rows =
          failedJobRecords.stream()
              .map(
                  row ->
                      String.join(
                          ",",
                          row.getRecordNo().toString(),
                          row.getRawRequest().trim(),
                          extractErrorMessage(row.getResponseBody(), row.getErrorMessage())))
              .collect(Collectors.joining("\n"));
    }
    String headerContent = getHeadersFor(feedType);
    return String.join("\n", headerContent, rows);
  }

  private String extractErrorMessage(String jsonString, String errorMessage) {
    try {
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      JsonNode messageNode = jsonNode.get("message");
      if (messageNode != null) {
        return messageNode.asText();
      } else {
        return errorMessage;
      }
    } catch (Exception e) {
      logger.error("Error while extracting the error message from response body: {}", e);
      return errorMessage;
    }
  }

  private String getHeadersFor(String key) {
    return env.getProperty("neip.headers." + key, "No headers found");
  }

  public PreSignedUrlResponse downloadLogsAsCsvV1(
      String jobId, String orgId, Optional<String> status) throws CommonServiceException {
    logger.debug("Processing download transit time and processing lead time");
    try {
      var jobDto = jobsConsumerService.getJob(jobId, orgId);
      var jobType = jobDto.getJobType();
      if (ObjectUtils.isEmpty(JobTypeEnum.valueOf(jobType.name()))) {
        throw new CommonServiceException(
            "Incorrect jobType specified", HttpStatus.BAD_REQUEST, 0x1772, null);
      }

      ProcessingRequestInterface processingRequest =
          processingRequestFactory.getModuleByJobType(jobType);
      if (GRID_JOB_UPLOADS.contains(jobType)) {
        return processingRequest.downloadTransitTimeErrorLogs(jobDto, status);
      }
      return processingRequest.downloadErrorLogs(jobDto, status);

    } catch (Exception e) {
      throw new CommonServiceException(
          "Error while downloading error logs as csv", HttpStatus.BAD_REQUEST, 0x1772, null);
    }
  }

  public PreSignedUrlResponse downloadTransitBufferDetails(
      Long transitBufferConfigRequestId, String createdBy) {
    return transitBufferFeign
        .getTransitBufferDetails(transitBufferConfigRequestId, createdBy)
        .getPayload();
  }

  public File downloadCustomRegionsForOrgId(String orgId) throws IOException {
    logger.debug("Processing download market regions for orgId");
    List<CustomRegionDto> customRegionDtoList =
        postalCodeTimeZoneResponseService.getCustomRegionsByOrgId(orgId);
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-custom-regions" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var header =
          new String[] {ORG_ID, REGION_ID, CUSTOM_REGION_NAME, CUSTOM_REGION_DESCRIPTION, GEOZONES};
      writeToCSV(header, writer);

      writeCustomRegionDataOntoFile(writer, customRegionDtoList);
      writer.flush();
      return tempFile.toFile();
    }
  }

  public String downloadCustomRegionsForOrgIdAndRegionId(String orgId, String regionId)
      throws CustomRegionServiceException {
    logger.debug("Processing download market region for orgId and regionId");
    CustomRegionResponse customRegionResponse =
        postalCodeTimeZoneResponseService.getCustomRegionsByOrgIdAndRegionId(orgId, regionId);
    var header =
        String.join(
            ",", ORG_ID, REGION_ID, CUSTOM_REGION_NAME, CUSTOM_REGION_DESCRIPTION, GEOZONES);

    var row = createRowForCustomRegion(customRegionResponse);

    return String.join("\n", header, row);
  }

  private String createRowForCustomRegion(CustomRegionResponse dto) {

    String geoZones = !dto.getCodes().isEmpty() ? String.join(":", dto.getCodes()) : "";

    return dto.getOrgId()
        + ","
        + dto.getId()
        + ","
        + dto.getCustomRegionName()
        + ","
        + dto.getCustomRegionDescription()
        + ","
        + geoZones;
  }

  private void writeCustomRegionDataOntoFile(CSVWriter csvWriter, List<CustomRegionDto> responses) {

    if (!CollectionUtils.isEmpty(responses)) {
      responses.forEach(
          response -> {
            var regionId = response.getId();
            var orgId = response.getOrgId();
            var customRegionName = response.getCustomRegionName();
            var customRegionDescription = response.getCustomRegionDescription();
            var geoZones =
                !response.getCodes().isEmpty() ? String.join(":", response.getCodes()) : "";
            csvWriter.writeNext(
                new String[] {
                  orgId, regionId, customRegionName, customRegionDescription, geoZones
                });
          });
    }
  }

  public InputStream downloadHolidayCutoffRulesForOrgId(
      String orgId, HolidayCutoffUIRequest holidayCutoffUIRequest) throws IOException {
    logger.debug("Processing download holiday cutoff rules for orgId {}", orgId);
    PageResponseForHolidayCutoff hcoRuleResponse = null;
    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> pageResponse =
        holidayCutoffUIFeign.getHolidayCutoffDetails(
            orgId, false, null, null, null, null, holidayCutoffUIRequest);
    var responseBody = pageResponse.getBody();
    if (responseBody != null) {
      hcoRuleResponse = responseBody.getPayload();
    }

    if (hcoRuleResponse != null && hcoRuleResponse.getData() == null) {
      return new ByteArrayInputStream(new byte[0]);
    }

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream))) {
      if (hcoRuleResponse != null) {
        writeHCODataOntoFile(csvWriter, hcoRuleResponse);
      }
      csvWriter.flush();
      return new ByteArrayInputStream(outputStream.toByteArray());
    }
  }

  private void writeHCODataOntoFile(CSVWriter writer, PageResponseForHolidayCutoff response) {
    String[] headerMetas = CsvUtil.getHCOHeaderMetas(response);
    String[] headerNames = CsvUtil.getHCOHeaderNames(response);
    var rowDataFromHCOResponse = response.getData().getRows();

    writeToCSV(headerNames, writer);

    if (Objects.nonNull(response.getData().getRows()) && !response.getData().getRows().isEmpty()) {
      rowDataFromHCOResponse.stream()
          .map(
              data ->
                  Arrays.stream(headerMetas)
                      .map(
                          headerMeta ->
                              Objects.nonNull(data.get(headerMeta))
                                  ? String.valueOf(data.get(headerMeta))
                                  : "")
                      .toArray(String[]::new))
          .forEach(stringData -> writeToCSV(stringData, writer));
    }
  }

  public File downloadTransferSchedulesData(String orgId, FetchTransferScheduleRequest request)
      throws IOException {
    BaseResponse<PagePayload<TransferScheduleResponse>> transferScheduleList =
        transferScheduleFeign.fetchTransferSchedule(orgId, false, null, null, null, null, request);

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-transfer-schedules" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var header = new String[] {ORIGIN_NODE, DESTINATION_NODE, PICKUP, DROPOFF};
      writeToCSV(header, writer);
      writeTransferSchedulesDataToFile(writer, transferScheduleList.getPayload().getData());
      writer.flush();
    }

    return tempFile.toFile();
  }

  private void writeTransferSchedulesDataToFile(
      CSVWriter writer, List<TransferScheduleResponse> transferScheduleList) {
    for (TransferScheduleResponse transferSchedule : transferScheduleList) {
      List<String> csvData = new ArrayList<>();
      csvData.add(transferSchedule.getSourceNodeId());
      csvData.add(transferSchedule.getDropoffNodeId());
      csvData.add(String.valueOf(transferSchedule.getStartTime()));
      csvData.add(String.valueOf(transferSchedule.getEndTime()));
      writeToCSV(csvData.toArray(new String[0]), writer);
    }
  }

  public File downloadCustomRegionDetails(
      String orgId, String country, String regionIds, String regionNames) throws IOException {
    PagePayload<CustomRegionInfo> customRegionInfoPagePayload =
        postalCodeFeign
            .getCustomRegionInfo(orgId, country, regionIds, regionNames, null, null, null, null)
            .getPayload();
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-custom-regions" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var header =
          new String[] {ORG_ID, REGION_ID, CUSTOM_REGION_NAME, CUSTOM_REGION_DESCRIPTION, GEOZONES};
      Integer currentPageNo = 1;
      Integer totalPages = 0;
      List<CustomRegionInfo> responses = new ArrayList<>();
      if (Objects.nonNull(customRegionInfoPagePayload.getData())) {
        writeToCSV(header, writer);
        responses = customRegionInfoPagePayload.getData();
        totalPages = customRegionInfoPagePayload.getPagination().getTotalPages();
        currentPageNo = customRegionInfoPagePayload.getPagination().getCurrentPage();
        writeCustomRegionDetailsOntoFile(writer, responses);
        currentPageNo++;
        while (currentPageNo <= totalPages) {
          PagePayload<CustomRegionInfo> customRegionInfoNextPages =
              postalCodeFeign
                  .getCustomRegionInfo(
                      orgId, country, regionIds, regionNames, currentPageNo, null, null, null)
                  .getPayload();
          if (Objects.nonNull(customRegionInfoNextPages)
              && Objects.nonNull(customRegionInfoNextPages.getData())) {
            responses = customRegionInfoNextPages.getData();
            writeCustomRegionDetailsOntoFile(writer, responses);
            currentPageNo++;
          }
        }
      }
      writer.flush();
      return tempFile.toFile();
    }
  }

  private void writeCustomRegionDetailsOntoFile(
      CSVWriter csvWriter, List<CustomRegionInfo> responses) {
    if (!CollectionUtils.isEmpty(responses)) {
      responses.forEach(
          response -> {
            var regionId = response.getCustomRegionId();
            var orgId = response.getOrgId();
            var customRegionName = response.getCustomRegionName();
            var customRegionDescription = response.getCustomRegionDescription();
            var geoZones =
                !response.getZipCodes().isEmpty() ? String.join(",", response.getZipCodes()) : "";
            csvWriter.writeNext(
                new String[] {
                  orgId, regionId, customRegionName, customRegionDescription, geoZones
                });
          });
    }
  }
}
