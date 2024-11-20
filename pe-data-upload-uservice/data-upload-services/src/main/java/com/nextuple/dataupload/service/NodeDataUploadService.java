/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LAST_WORKING_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.START_WORKING_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ZIP_CODE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.jobs.framework.common.domain.pojo.NodeData;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final NodeFeign nodeFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, NODE_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, NODE_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForNode = true;
    var isAllPassedForNode = true;
    var nodeResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(csvParser, "node", NODE_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          Map<String, Boolean> serviceOptionEligibilities =
              Map.of(
                  SDND_ELIGIBLE, Boolean.valueOf(csvRecord.get(SDND_ELIGIBLE)),
                  EXPRESS_ELIGIBLE, Boolean.valueOf(csvRecord.get(EXPRESS_ELIGIBLE)),
                  NEXTDAY_ELIGIBLE, Boolean.valueOf(csvRecord.get(NEXTDAY_ELIGIBLE)));

          // Accessing Values by Column Header Name
          NodeData nodeData =
              NodeData.builder()
                  .action(csvRecord.get(ACTION))
                  .nodeId(csvRecord.get(NODE_ID))
                  .orgId(csvRecord.get(ORG_ID))
                  .street(csvRecord.get(STREET))
                  .city(csvRecord.get(CITY))
                  .state(csvRecord.get(STATE))
                  .zipCode(csvRecord.get(ZIP_CODE))
                  .country(csvRecord.get(COUNTRY))
                  .latitude(csvRecord.get(LATITUDE))
                  .longitude(csvRecord.get(LONGITUDE))
                  .timezone(csvRecord.get(TIMEZONE))
                  .serviceOptionEligibilities(serviceOptionEligibilities)
                  .shipToHome(Boolean.valueOf(csvRecord.get(SHIP_TO_HOME)))
                  .bopisEligible(Boolean.valueOf(csvRecord.get(BOPIS_ELIGIBLE)))
                  .nodeType(csvRecord.get(NODE_TYPE))
                  .isActive(Boolean.valueOf(csvRecord.get(IS_ACTIVE)))
                  .startWorkingTime(csvRecord.get(START_WORKING_TIME))
                  .lastWorkingTime(csvRecord.get(LAST_WORKING_TIME))
                  .build();

          nodeResult =
              processActionForNode(
                  nodeData.getAction(), nodeData.getNodeId(), nodeData.getOrgId(), nodeData);
        } catch (Exception e) {
          if (isAllPassedForNode) isAllPassedForNode = false;
          log.error("Failed to store Node CSV data for row number : {}", row);
        }

        if (isAllPassedForNode) isAllPassedForNode = nodeResult;
        if (isAllFailedForNode) isAllFailedForNode = !nodeResult;
      }
      return DataUploadUtil.storeToMap(isAllPassedForNode, isAllFailedForNode);
    }
  }

  private boolean processActionForNode(
      String action, String nodeId, String orgId, NodeData nodeData) {
    boolean nodeResult;
    switch (action) {
      case CREATE:
        {
          nodeResult = isCreateNodeSuccessful(nodeData);
          break;
        }

      case UPDATE:
        {
          nodeResult = isUpdateNodeSuccessful(nodeData);
          break;
        }

      case DELETE:
        {
          nodeResult = isDeleteNodeSuccessful(nodeId, orgId);
          break;
        }

      default:
        {
          log.error(ACTION_INVALID_MESSAGE);
          nodeResult = false;
          break;
        }
    }
    return nodeResult;
  }

  private boolean isDeleteNodeSuccessful(String nodeId, String orgId) {
    BaseResponse<NodeResponse> baseResponse = nodeFeign.deleteNode(nodeId, orgId);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isUpdateNodeSuccessful(NodeData nodeData) {
    var nodeUpdationRequest =
        NodeUpdationRequest.builder()
            .street(nodeData.getStreet())
            .city(nodeData.getCity())
            .state(nodeData.getState())
            .zipCode(nodeData.getZipCode())
            .country(nodeData.getCountry())
            .latitude(nodeData.getLatitude())
            .longitude(nodeData.getLongitude())
            .timezone(nodeData.getTimezone())
            .shipToHome(nodeData.getShipToHome())
            .serviceOptionEligibilities(nodeData.getServiceOptionEligibilities())
            .bopisEligible(nodeData.getBopisEligible())
            .nodeType(nodeData.getNodeType())
            .isActive(nodeData.getIsActive())
            .startWorkingTime(nodeData.getStartWorkingTime())
            .lastWorkingTime(nodeData.getLastWorkingTime())
            .build();
    BaseResponse<NodeResponse> baseResponse =
        nodeFeign.updateNodeDetails(nodeData.getNodeId(), nodeData.getOrgId(), nodeUpdationRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isCreateNodeSuccessful(NodeData nodeData) {
    var nodeRequest =
        NodeRequest.builder()
            .nodeId(nodeData.getNodeId())
            .orgId(nodeData.getOrgId())
            .street(nodeData.getStreet())
            .city(nodeData.getCity())
            .state(nodeData.getState())
            .zipCode(nodeData.getZipCode())
            .country(nodeData.getCountry())
            .latitude(nodeData.getLatitude())
            .longitude(nodeData.getLongitude())
            .timezone(nodeData.getTimezone())
            .shipToHome(nodeData.getShipToHome())
            .serviceOptionEligibilities(nodeData.getServiceOptionEligibilities())
            .bopisEligible(nodeData.getBopisEligible())
            .nodeType(nodeData.getNodeType())
            .isActive(nodeData.getIsActive())
            .startWorkingTime(nodeData.getStartWorkingTime())
            .lastWorkingTime(nodeData.getLastWorkingTime())
            .build();
    BaseResponse<NodeResponse> baseResponse = nodeFeign.createNode(nodeRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }
}
