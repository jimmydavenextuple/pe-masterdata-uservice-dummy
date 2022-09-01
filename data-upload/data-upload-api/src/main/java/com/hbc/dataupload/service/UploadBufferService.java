package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.*;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.*;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadBufferService {
  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final NodeCarrierFeign nodeCarrierFeign;
  private final TransitFeign transitFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(
        fileUri, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = nodeServiceOptionBufferCsvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node ServiceOption Buffer");
  }

  private Map<String, Boolean> nodeServiceOptionBufferCsvReader(Path path)
      throws IOException, CommonServiceException {
    var isAllFailedForNodeServiceOptionBuffer = true;
    var isAllPassedForNodeServiceOptionBuffer = true;
    var nodeServiceOptionBufferResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "node-service-option-buffer",
          NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String orgId = csvRecord.get(ORG_ID);
          String nodeId = csvRecord.get(NODE_ID);
          String serviceOption = csvRecord.get(SERVICE_OPTION);
          Double bufferHours = Double.parseDouble(csvRecord.get(BUFFER_HOURS));
          bufferHours = Double.valueOf(new DecimalFormat("0.00").format(bufferHours));
          var bufferStartDT =
              LocalDateTime.parse(
                  csvRecord.get(START_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));
          var bufferEndDT =
              LocalDateTime.parse(
                  csvRecord.get(END_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));

          var bufferStartDateTime =
              Date.from(bufferStartDT.atZone(ZoneId.systemDefault()).toInstant());
          var bufferEndDateTime = Date.from(bufferEndDT.atZone(ZoneId.systemDefault()).toInstant());

          if (bufferHours < 0.00 && bufferStartDateTime != null && bufferEndDateTime != null) {
            log.error(
                "Incorrect Buffer Details for: " + orgId + "::" + nodeId + "::" + serviceOption);
            Map<String, FieldError> errorMap = new HashMap<>();
            errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
            errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
            errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
            throw new CommonServiceException(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INCORRECT_DATA,
                HttpStatus.BAD_REQUEST,
                0x1772,
                errorMap);
          }

          var nodeServiceOptionBufferRequest =
              NodeCarrierBufferRequest.builder()
                  .nodeId(nodeId)
                  .orgId(orgId)
                  .serviceOption(serviceOption)
                  .bufferHours(bufferHours)
                  .bufferStartDate(bufferStartDateTime)
                  .bufferEndDate(bufferEndDateTime)
                  .build();
          BaseResponse<NodeCarrierResponse> baseResponse;
          baseResponse = nodeCarrierFeign.updateBuffer(nodeServiceOptionBufferRequest);
          nodeServiceOptionBufferResult = baseResponse.isSuccess();
          log.debug(baseResponse.getMessage());
        } catch (Exception e) {
          if (isAllPassedForNodeServiceOptionBuffer) {
            isAllPassedForNodeServiceOptionBuffer = false;
          }
          log.error("Failed to store Node ServiceOption Buffer CSV data for row number : {}", row);
        }

        if (isAllPassedForNodeServiceOptionBuffer) {
          isAllPassedForNodeServiceOptionBuffer = nodeServiceOptionBufferResult;
        }
        if (isAllFailedForNodeServiceOptionBuffer) {
          isAllFailedForNodeServiceOptionBuffer = !nodeServiceOptionBufferResult;
        }
      }
      return DataUploadUtil.storeToMap(
          isAllPassedForNodeServiceOptionBuffer, isAllFailedForNodeServiceOptionBuffer);
    }
  }

  public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = transitBufferCsvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Transit Buffer");
  }

  private Map<String, Boolean> transitBufferCsvReader(Path path)
      throws IOException, CommonServiceException {
    var isAllFailedForTransitBuffer = true;
    var isAllPassedForTransitBuffer = true;
    var transitBufferResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "transit-buffer", TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS);

      DataUploadUtil.validateUpdateAction(path);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String orgId = csvRecord.get(ORG_ID);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          String action = csvRecord.get(ACTION);
          String sourceGeoZone = csvRecord.get(SOURCE_GEO_ZONE);
          String destinationGeozone = csvRecord.get(DESTINATION_GEO_ZONE);
          Double bufferDays = Double.parseDouble(csvRecord.get(BUFFER_DAYS));
          var bufferStartDT =
              LocalDateTime.parse(
                  csvRecord.get(START_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));
          var bufferEndDT =
              LocalDateTime.parse(
                  csvRecord.get(END_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));

          var bufferStartDateTime =
              Date.from(bufferStartDT.atZone(ZoneId.systemDefault()).toInstant());
          var bufferEndDateTime = Date.from(bufferEndDT.atZone(ZoneId.systemDefault()).toInstant());

          if (bufferStartDateTime != null && bufferEndDateTime != null) {
            var transitBufferRequest =
                TransitBufferCreationRequest.builder()
                    .orgId(orgId)
                    .carrierServiceId(carrierServiceId)
                    .sourceGeozone(sourceGeoZone)
                    .destinationGeozone(destinationGeozone)
                    .bufferDays(bufferDays)
                    .bufferStartDate(bufferStartDateTime)
                    .bufferEndDate(bufferEndDateTime)
                    .build();
            BaseResponse<TransitResponse> baseResponse;
            baseResponse = transitFeign.updateTransitBufferDetails(transitBufferRequest);
            transitBufferResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error(
                "Incorrect Buffer Details for: {} :: {} :: {} :: {}",
                orgId,
                carrierServiceId,
                sourceGeoZone,
                destinationGeozone);
            Map<String, FieldError> errorMap = new HashMap<>();
            errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
            errorMap.put(
                CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
            errorMap.put(
                SOURCE_GEO_ZONE, FieldError.builder().rejectedValue(sourceGeoZone).build());
            errorMap.put(
                DESTINATION_GEO_ZONE,
                FieldError.builder().rejectedValue(destinationGeozone).build());
            throw new CommonServiceException(
                TRANSIT_BUFFER_DATA_UPLOAD_INCORRECT_DATA,
                HttpStatus.BAD_REQUEST,
                0x1772,
                errorMap);
          }
        } catch (Exception e) {
          if (isAllPassedForTransitBuffer) {
            isAllPassedForTransitBuffer = false;
          }
          log.error("Failed to store Transit Buffer CSV data for row number : {}", row);
        }

        if (isAllPassedForTransitBuffer) {
          isAllPassedForTransitBuffer = transitBufferResult;
        }
        if (isAllFailedForTransitBuffer) {
          isAllFailedForTransitBuffer = !transitBufferResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForTransitBuffer, isAllFailedForTransitBuffer);
    }
  }
}
