package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.domain.mapper.TransitDataRequestMapper;
import com.hbc.csvdownload.domain.pojo.DownloadErrorNodeCarrier;
import com.hbc.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.hbc.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.newrelic.relocated.Gson;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CsvDownloadUtilityService {

  private final Logger logger = LoggerFactory.getLogger(CsvDownloadUtilityService.class);

  private final PostalCodeTimeZoneService postalCodeTimeZoneService;
  private final TransitService transitService;

  private final JobsDashboardService jobsDashboardService;
  private final JobsConsumerService jobsConsumerService;

  private static final TransitDataRequestMapper INSTANCE =
      Mappers.getMapper(TransitDataRequestMapper.class);

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

  private static String constructCsvRows(
      Map<String, Map<String, Float>> transitTimesDataMap, String destinationFsa) {
    var sourceFsaAndTransitTimesMap = transitTimesDataMap.get(destinationFsa);
    String transitTimes =
        sourceFsaAndTransitTimesMap.keySet().stream()
            .map(sourceFsa -> String.valueOf(sourceFsaAndTransitTimesMap.get(sourceFsa)))
            .collect(Collectors.joining(","));
    return String.join(",", destinationFsa, transitTimes);
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
        String.join(",", "nodeId", "orgId", "serviceOption", "processingLeadTime", "errorMessage");
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
}
