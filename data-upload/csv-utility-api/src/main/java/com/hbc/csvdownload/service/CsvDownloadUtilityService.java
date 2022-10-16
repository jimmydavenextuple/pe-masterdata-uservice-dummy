package com.hbc.csvdownload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_END_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_HOURS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_START_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.INACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATUS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;

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
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.newrelic.relocated.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
  private final NodeService nodeService;
  private final NodeCarrierService nodeCarrierService;

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

  public String downloadMarketRegionForOrgIdAndCountry(String orgId, String country)
      throws PostalCodeTimezoneServiceException {
    logger.debug("Processing download market regions for orgId and country");
    List<PostalCodeTimezoneDto> postalCodeTimezoneDtoList =
        postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(orgId, country);
    var header =
        String.join(
            ",",
            "orgId",
            "postalCodePrefix",
            "country",
            "state",
            "city",
            "latitude",
            "longitude",
            "timeZone");
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

  public File downloadProcessingTimeBuffersByOrgId(String orgId) throws IOException {
    logger.debug("Processing download processing time buffers for orgId");
    String tmpdir = System.getProperty("java.io.tmpdir");
    String separator = System.getProperty("file.separator");
    String pathName = String.format("%s%s%s.csv", tmpdir, separator, new Date().getTime());
    var processingTimeBufferFile = new File(pathName);
    var fileWriter = new FileWriter(processingTimeBufferFile);
    try (var writer = new BufferedWriter(fileWriter)) {
      List<NodeDto> nodeDtoList = nodeService.getNodeList(orgId);
      List<NodeCarrierResponse> nodeCarrierResponseList =
          nodeCarrierService.getNodeCarrierList(orgId);
      var header =
          String.join(
              ",",
              NODE_ID,
              ORG_ID,
              NODE_TYPE,
              STREET,
              CITY,
              PROVINCE,
              POSTAL_CODE,
              SERVICE_OPTION,
              BUFFER_HOURS,
              BUFFER_START_DATE,
              BUFFER_END_DATE,
              STATUS);
      writer.append(header);
      writer.append("\n");

      Map<String, List<NodeCarrierResponse>> nodeCarrierResponseMap =
          constructMap(nodeCarrierResponseList);

      nodeDtoList.forEach(
          node -> {
            List<NodeCarrierResponse> nodeCarrierResponses =
                nodeCarrierResponseMap.get(node.getNodeId());
            if (nodeCarrierResponses != null) {
              nodeCarrierResponses.forEach(
                  nodeCarrierResponse ->
                      appendRowToFile(constructCSVContents(node, nodeCarrierResponse), writer));
            } else appendRowToFile(constructCSVContents(node, new NodeCarrierResponse()), writer);
          });
    }

    return processingTimeBufferFile;
  }

  private void appendRowToFile(String row, BufferedWriter writer) {
    try {
      writer.append(row);
      writer.append("\n");
    } catch (IOException e) {
      logger.error("Error while writing processing time buffers records");
    }
  }

  private Map<String, List<NodeCarrierResponse>> constructMap(
      List<NodeCarrierResponse> nodeCarrierResponseList) {
    Map<String, List<NodeCarrierResponse>> nodeCarrierResponseMap = new HashMap<>();

    nodeCarrierResponseList.forEach(
        nodeCarrier -> {
          if (nodeCarrierResponseMap.containsKey(nodeCarrier.getNodeId())) {
            nodeCarrierResponseMap.get(nodeCarrier.getNodeId()).add(nodeCarrier);
          } else {
            List<NodeCarrierResponse> nodeCarrierResponseList1 = new ArrayList<>();
            nodeCarrierResponseList1.add(nodeCarrier);
            nodeCarrierResponseMap.put(nodeCarrier.getNodeId(), nodeCarrierResponseList1);
          }
        });

    return nodeCarrierResponseMap;
  }

  private String constructCSVContents(NodeDto node, NodeCarrierResponse nodeCarrierResponse) {
    String serviceOption = checkForNullValues(nodeCarrierResponse.getServiceOption());
    String bufferHours = checkForNullValues(nodeCarrierResponse.getBufferHours());
    String bufferStartDate =
        checkForNullValues(convertToStringUTC(nodeCarrierResponse.getBufferStartDate()));
    String bufferEndDate =
        checkForNullValues(convertToStringUTC(nodeCarrierResponse.getBufferEndDate()));
    String status =
        checkForNullValues(
            computeStatus(
                nodeCarrierResponse.getBufferHours(),
                nodeCarrierResponse.getBufferStartDate(),
                nodeCarrierResponse.getBufferEndDate()));

    return node.getNodeId()
        + ","
        + node.getOrgId()
        + ","
        + node.getNodeType()
        + ","
        + node.getStreet()
        + ","
        + node.getCity()
        + ","
        + node.getProvince()
        + ","
        + node.getPostalCode()
        + ","
        + serviceOption
        + ","
        + bufferHours
        + ","
        + bufferStartDate
        + ","
        + bufferEndDate
        + ","
        + status;
  }

  private String checkForNullValues(Object value) {
    return (value == null) ? "NA" : value.toString();
  }

  private String convertToStringUTC(Date value) {
    if (value != null) {
      return value.toInstant().toString();
    }
    return null;
  }

  private String computeStatus(Double bufferHours, Date bufferStartDate, Date bufferEndDate) {
    if (bufferHours != null && bufferStartDate != null && bufferEndDate != null) {
      var currentDate = new Date();
      return currentDate.compareTo(bufferEndDate) <= 0 ? ACTIVE : INACTIVE;
    }
    return null;
  }
}
