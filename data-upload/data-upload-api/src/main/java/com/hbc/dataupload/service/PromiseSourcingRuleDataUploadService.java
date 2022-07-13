package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PRIORITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.feign.PromiseSourcingRuleFeign;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
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
import org.apache.commons.csv.CSVParser;
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
    Path path = DataUploadUtil.getPath(basePath, fileUri);

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
    boolean isAllFailedForSourcing = true;
    boolean isAllPassedForSourcing = true;
    boolean sourcingResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
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
          int priority = Integer.parseInt(csvRecord.get(PRIORITY));
          String allocationRuleId = csvRecord.get(ALLOCATION_RULE_ID);

          switch (action) {
            case CREATE:
              {
                CreatePromiseSourcingRuleRequest createPromiseSourcingRuleRequest =
                    CreatePromiseSourcingRuleRequest.builder()
                        .orgId(orgId)
                        .serviceOption(serviceOption)
                        .destinationGeoZone(destinationGeoZone)
                        .sourceNodes(sourceNodesSet)
                        .priority(priority)
                        .allocationRuleId(allocationRuleId)
                        .build();
                BaseResponse<PromiseSourcingRuleDto> baseResponse =
                    promiseSourcingRuleFeign.createPromiseSourcingRule(
                        createPromiseSourcingRuleRequest);
                sourcingResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                UpdatePromiseSourcingRuleRequest updatePromiseSourcingRuleRequest =
                    UpdatePromiseSourcingRuleRequest.builder().sourceNodes(sourceNodesSet).build();
                BaseResponse<PromiseSourcingRuleDto> baseResponse =
                    promiseSourcingRuleFeign.updatePromiseSourcingRule(
                        orgId,
                        serviceOption,
                        destinationGeoZone,
                        allocationRuleId,
                        priority,
                        updatePromiseSourcingRuleRequest);
                sourcingResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<PromiseSourcingRuleDto> baseResponse =
                    promiseSourcingRuleFeign.deletePromiseSourcingRule(
                        orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
                sourcingResult = baseResponse.isSuccess();
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
}
