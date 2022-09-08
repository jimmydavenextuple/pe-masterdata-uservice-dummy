package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CsvDownloadUtilityService {

  private final Logger logger = LoggerFactory.getLogger(CsvDownloadUtilityService.class);

  private final PostalCodeTimeZoneService postalCodeTimeZoneService;
  private final TransitService transitService;

  private final JobsDashboardService jobsDashboardService;
  private final JobsConsumerService jobsConsumerService;

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
      List<RecordStatusDto> recordStatusDtos =
          jobsDashboardService.getJobRecords(jobId, orgId, status);

      if (jobType.equals(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES)) {
        return downloadProcessingLeadTimeErrorLogs(recordStatusDtos);
      } else if (jobType.equals(JobTypeEnum.UPLOAD_TRANSIT_TIMES)) {
        return downloadTransitTimeErrorLogs(recordStatusDtos);
      } else {
        throw new CommonServiceException(
            "Incorrect jobType specified", HttpStatus.BAD_REQUEST, 0x1772, null);
      }
    } catch (Exception e) {
      throw new CommonServiceException(
          "Error processing transit time and processing lead time csv",
          HttpStatus.BAD_REQUEST,
          0x1772,
          null);
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
    var requestBody = gson.fromJson(recordStatusDto.getRequestBody(), NodeCarrierRequest.class);
    return String.join(
        ",",
        requestBody.getNodeId(),
        requestBody.getOrgId(),
        requestBody.getServiceOption(),
        requestBody.getProcessingTime().toString(),
        recordStatusDto.getErrorMessage());
  }

  private String downloadTransitTimeErrorLogs(List<RecordStatusDto> recordStatusDtoList)
      throws CommonServiceException {
    try {
      var requestBody =
          recordStatusDtoList.stream().map(this::getRequestBody).collect(Collectors.toList());

      Set<String> sourceFsaSet =
          requestBody.stream()
              .map(TransitDataCreationRequest::getSourceGeozone)
              .collect(Collectors.toSet());

      List<TransitDataCreationRequest> transitDataCreationRequestList =
          requestBody.stream()
              .filter(reqBody -> sourceFsaSet.contains(reqBody.getSourceGeozone()))
              .collect(Collectors.toList());

      Map<String, Float> sourceAndTransitTimeMap = new TreeMap<>();

      sourceFsaSet.forEach(fsa -> sourceAndTransitTimeMap.put(fsa, null));

      Map<String, Map<String, Float>> transitTimeDataMap = new HashMap<>();

      transitDataCreationRequestList.forEach(
          transitRequest -> {
            if (CollectionUtils.isEmpty(
                transitTimeDataMap.get(transitRequest.getDestinationGeozone()))) {
              transitTimeDataMap.put(
                  transitRequest.getDestinationGeozone(), new HashMap<>(sourceAndTransitTimeMap));
            }
          });

      transitDataCreationRequestList.forEach(
          transitRequest ->
              transitTimeDataMap
                  .get(transitRequest.getDestinationGeozone())
                  .put(transitRequest.getSourceGeozone(), transitRequest.getTransitDays()));

      var sourceFsaHeader = String.join(",", sourceFsaSet);

      return constructCsvDataForTransit(transitTimeDataMap, sourceFsaHeader, recordStatusDtoList);
    } catch (Exception e) {
      logger.error("Error while forming csv contents string");
      throw new CommonServiceException(
          "Error while forming csv contents string", HttpStatus.BAD_REQUEST, 0x1771, null);
    }
  }

  private String constructCsvDataForTransit(
      Map<String, Map<String, Float>> transitTimeDataMap,
      String sourceFsaheader,
      List<RecordStatusDto> recordStatusDtoList) {
    var csvContent = String.format("Destination FSA / Source FSA ->,%s", sourceFsaheader);

    String rows =
        transitTimeDataMap.keySet().stream()
            .map(destinationFsa -> constructCsvRowsForTransit(destinationFsa, recordStatusDtoList))
            .collect(Collectors.joining("\n"));
    return String.join("\n", csvContent, rows);
  }

  private String constructCsvRowsForTransit(
      String destinationFsa, List<RecordStatusDto> recordStatusDtoList) {
    String errorMessage =
        recordStatusDtoList.stream()
            .map(error -> String.valueOf(error.getErrorMessage()))
            .collect(Collectors.joining(","));

    return String.join(",", destinationFsa, errorMessage);
  }

  private TransitDataCreationRequest getRequestBody(RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    return gson.fromJson(recordStatusDto.getRequestBody(), TransitDataCreationRequest.class);
  }
}
