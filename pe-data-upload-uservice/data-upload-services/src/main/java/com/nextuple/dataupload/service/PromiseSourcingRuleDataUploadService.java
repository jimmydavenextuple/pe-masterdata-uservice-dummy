/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.PRIORITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.feign.PromiseSourcingRuleFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromiseSourcingRuleDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final PromiseSourcingRuleFeign promiseSourcingRuleFeign;

  public ResponseEntity<BaseResponse<String>> uploadPromiseSourcingRuleData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Promise Sourcing Rule");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForSourcing = true;
    var isAllPassedForSourcing = true;
    var sourcingResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "promiseSourcingRule", PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String serviceOption = csvRecord.get(SERVICE_OPTION);
          String destinationGeoZone = csvRecord.get(DESTINATION_GEO_ZONE);
          String sourceNodes = (csvRecord.get(SOURCE_NODES));
          Set<String> sourceNodesSet =
              new HashSet<>(
                  Arrays.asList(sourceNodes.substring(1, sourceNodes.length() - 1).split(",")));
          var priority = Integer.parseInt(csvRecord.get(PRIORITY));
          String allocationRuleId = csvRecord.get(ALLOCATION_RULE_ID);

          switch (action) {
            case CREATE:
              {
                sourcingResult =
                    isCreatePromiseSourcingRuleSuccessful(
                        orgId,
                        serviceOption,
                        destinationGeoZone,
                        sourceNodesSet,
                        priority,
                        allocationRuleId);
                break;
              }

            case UPDATE:
              {
                sourcingResult =
                    isUpdatePromiseSourcingRuleSuccessful(
                        orgId,
                        serviceOption,
                        destinationGeoZone,
                        sourceNodesSet,
                        priority,
                        allocationRuleId);
                break;
              }

            case DELETE:
              {
                sourcingResult =
                    isDeletePromiseSourcingRuleSuccessful(
                        orgId, serviceOption, destinationGeoZone, priority, allocationRuleId);
                break;
              }

            default:
              {
                log.error(ACTION_INVALID_MESSAGE);
                break;
              }
          }
        } catch (Exception e) {
          if (isAllPassedForSourcing) {
            isAllPassedForSourcing = false;
          }
          log.error("Failed to store Promise Sourcing Rule CSV data for row number : {}", row);
        }

        if (isAllPassedForSourcing) {
          isAllPassedForSourcing = sourcingResult;
        }
        if (isAllFailedForSourcing) {
          isAllFailedForSourcing = !sourcingResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForSourcing, isAllFailedForSourcing);
    }
  }

  private boolean isDeletePromiseSourcingRuleSuccessful(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      int priority,
      String allocationRuleId) {
    BaseResponse<PromiseSourcingRuleDto> baseResponse =
        promiseSourcingRuleFeign.deletePromiseSourcingRule(
            orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isUpdatePromiseSourcingRuleSuccessful(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      Set<String> sourceNodesSet,
      int priority,
      String allocationRuleId) {
    var updatePromiseSourcingRuleRequest =
        UpdatePromiseSourcingRuleRequest.builder().sourceNodes(sourceNodesSet).build();
    BaseResponse<PromiseSourcingRuleDto> baseResponse =
        promiseSourcingRuleFeign.updatePromiseSourcingRule(
            orgId,
            serviceOption,
            destinationGeoZone,
            allocationRuleId,
            priority,
            updatePromiseSourcingRuleRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isCreatePromiseSourcingRuleSuccessful(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      Set<String> sourceNodesSet,
      int priority,
      String allocationRuleId) {
    var createPromiseSourcingRuleRequest =
        CreatePromiseSourcingRuleRequest.builder()
            .orgId(orgId)
            .serviceOption(serviceOption)
            .destinationGeoZone(destinationGeoZone)
            .sourceNodes(sourceNodesSet)
            .priority(priority)
            .allocationRuleId(allocationRuleId)
            .build();
    BaseResponse<PromiseSourcingRuleDto> baseResponse =
        promiseSourcingRuleFeign.createPromiseSourcingRule(createPromiseSourcingRuleRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }
}
