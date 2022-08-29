package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE_D;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.INVALID_SELECTION_CRITERIA;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SELECTION_CRITERIA;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE_U;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.hbc.common.configuration.api.domain.feign.CommonConfigFeign;
import com.hbc.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class NodeCarrierSelectionUploadService {
  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CommonConfigFeign commonConfigFeign;

  public ResponseEntity<BaseResponse<String>> nodeCarrierSelectionUpload(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node Carrier Selection");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForNodeCarrierSelection = true;
    var isAllPassedForNodeCarrierSelection = true;
    var nodeCarrierSelectionResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "node-carrier-selection",
          NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String sourceFSA = csvRecord.get(SOURCE_GEO_ZONE);
          String destinationFSA = csvRecord.get(DESTINATION_GEO_ZONE);
          String serviceOption = csvRecord.get(SERVICE_OPTION);
          String selectionCriteria = csvRecord.get(SELECTION_CRITERIA);
          String key = serviceOption + ":" + sourceFSA + ":" + destinationFSA;
          var value = "";

          switch (selectionCriteria) {
            case "L":
              value = "0";
              break;
            case "E":
              value = "1";
              break;
            default:
              log.error(INVALID_SELECTION_CRITERIA);
              throw new CommonServiceException(
                  INVALID_SELECTION_CRITERIA, HttpStatus.BAD_REQUEST, 0x1776, null);
          }

          switch (action) {
            case UPDATE_U:
              {
                var createCommonConfigurationRequest =
                    CreateCommonConfigurationRequest.builder()
                        .orgId(orgId)
                        .type("NODE_CARRIER_SELECTION_PRIORITY")
                        .key(key)
                        .value(value)
                        .build();
                BaseResponse<CommonConfigurationDto> baseResponse =
                    commonConfigFeign.createCommonConfiguration(createCommonConfigurationRequest);
                nodeCarrierSelectionResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }
            case DELETE_D:
              {
                BaseResponse<CommonConfigurationDto> baseResponse =
                    commonConfigFeign.deleteCommonConfiguration(
                        orgId, "NODE_CARRIER_SELECTION_PRIORITY", key);
                nodeCarrierSelectionResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }
            default:
              {
                log.error(ACTION_INVALID_MESSAGE);
                break;
              }
          }
        } catch (Exception e) {
          if (isAllPassedForNodeCarrierSelection) {
            isAllPassedForNodeCarrierSelection = false;
          }
          log.error("Failed to store Node carrier selection CSV data for row number : {}", row);
        }
        if (isAllPassedForNodeCarrierSelection) {
          isAllPassedForNodeCarrierSelection = nodeCarrierSelectionResult;
        }
        if (isAllFailedForNodeCarrierSelection) {
          isAllFailedForNodeCarrierSelection = !nodeCarrierSelectionResult;
        }
      }
      return DataUploadUtil.storeToMap(
          isAllPassedForNodeCarrierSelection, isAllFailedForNodeCarrierSelection);
    }
  }
}
