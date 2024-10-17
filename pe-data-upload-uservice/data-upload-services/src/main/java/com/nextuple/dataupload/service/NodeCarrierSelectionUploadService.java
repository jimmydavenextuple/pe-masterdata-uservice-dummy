/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.INVALID_SELECTION_CRITERIA;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SELECTION_CRITERIA;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.NodeCarrierSelectionUploadConstants.NODE_CARRIER_SELECTION_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.constants.NodeCarrierSelectionPriorityEnum;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
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

  private final NodeCarrierFeign nodeCarrierFeign;

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

      DataUploadUtil.validateAction(path);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();

        // Accessing Values by Column Header Name
        String action = csvRecord.get(ACTION).toUpperCase();
        String orgId = csvRecord.get(ORG_ID);
        String sourceGeozone = csvRecord.get(SOURCE_GEO_ZONE);
        String destinationGeozone = csvRecord.get(DESTINATION_GEO_ZONE);
        String serviceOption = csvRecord.get(SERVICE_OPTION);
        String selectionCriteria = csvRecord.get(SELECTION_CRITERIA);
        var value = "";

        switch (selectionCriteria) {
          case "L":
            value = NodeCarrierSelectionPriorityEnum.LATEST.getValue();
            break;
          case "E":
            value = NodeCarrierSelectionPriorityEnum.EARLIEST.getValue();
            break;
          default:
            log.error(INVALID_SELECTION_CRITERIA);
            throw new CommonServiceException(
                INVALID_SELECTION_CRITERIA, HttpStatus.BAD_REQUEST, 0x1776, null);
        }

        try {
          if (action.equalsIgnoreCase(UPDATE)) {
            var nodeCarrierSelectionRequest =
                NodeCarrierSelectionRequest.builder()
                    .orgId(orgId)
                    .sourceGeozone(sourceGeozone)
                    .destinationGeozone(destinationGeozone)
                    .serviceOption(serviceOption)
                    .priority(value)
                    .build();

            nodeCarrierSelectionResult =
                isAddNodeCarrierSelectionPrioritySuccessful(nodeCarrierSelectionRequest);

          } else if (action.equalsIgnoreCase(DELETE)) {
            var nodeCarrierSelectionRequest =
                NodeCarrierSelectionRequest.builder()
                    .orgId(orgId)
                    .serviceOption(serviceOption)
                    .sourceGeozone(sourceGeozone)
                    .destinationGeozone(destinationGeozone)
                    .build();
            nodeCarrierSelectionResult =
                isDeleteNodeCarrierSelectionDetailsSuccessful(nodeCarrierSelectionRequest);
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

  private boolean isDeleteNodeCarrierSelectionDetailsSuccessful(
      NodeCarrierSelectionRequest nodeCarrierSelectionRequest) {
    BaseResponse<NodeCarrierSelectionResponse> baseResponse =
        nodeCarrierFeign.deleteNodeCarrierSelectionDetails(nodeCarrierSelectionRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isAddNodeCarrierSelectionPrioritySuccessful(
      NodeCarrierSelectionRequest nodeCarrierSelectionRequest) {
    BaseResponse<NodeCarrierSelectionResponse> baseResponse =
        nodeCarrierFeign.addNodeCarrierSelectionPriority(nodeCarrierSelectionRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }
}
