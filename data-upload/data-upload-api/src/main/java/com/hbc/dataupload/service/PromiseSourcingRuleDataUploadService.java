package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ALLOCATION_RULE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PRIORITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_NODES;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS;
import static com.hbc.dataupload.helper.PromiseSourcingRuleDataUploadConstants.PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.headers.DataUploadUtilityExpectedHeaders;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.feign.PromiseSourcingRuleFeign;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    long numberOfRowsInFile;
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("promiseSourcingRule");
    final String csvFilePath = basePath.concat(fileUri);

    Path path = Paths.get(csvFilePath);

    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }

    double sizeOfFileInKiloBytes = Files.size(path) / 1024.0;

    log.info("number of records : {}", numberOfRowsInFile);
    log.info("fileName : {}", path.getFileName());
    log.info("size in kB : {}", sizeOfFileInKiloBytes);

    if (!FILE_TYPE.equals(Files.probeContentType(path))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_TYPE,
          HttpStatus.BAD_REQUEST,
          0x2773,
          errorMap);
    }

    if (sizeOfFileInKiloBytes > maxSizeInKiloBytes) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file size is "
                      + sizeOfFileInKiloBytes
                      + " kB, Maximum file size allowed is "
                      + maxSizeInKiloBytes
                      + " kB")
              .build());
      throw new CommonServiceException(
          PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_FILE_SIZE,
          HttpStatus.BAD_REQUEST,
          0x2774,
          errorMap);
    }

    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          PROMISE_SOURCING_RULE_DATA_UPLOAD_FILE_EMPTY_RECORDS,
          HttpStatus.BAD_REQUEST,
          0x2775,
          errorMap);
    }

    if (numberOfRowsInFile > maxRows) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file contains "
                      + sizeOfFileInKiloBytes
                      + " rows, Maximum number of rows allowed is "
                      + maxSizeInKiloBytes
                      + " kB")
              .build());
      throw new CommonServiceException(
          PROMISE_SOURCING_RULE_DATA_UPLOAD_LARGE_ROW_SIZE,
          HttpStatus.BAD_REQUEST,
          0x2776,
          errorMap);
    }

    Map<String, Boolean> resultMap = csvReader(expectedHeaders, path);

    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message(PROMISE_SOURCING_RULE_DATA_UPLOAD_SUCCESS).build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message(PROMISE_SOURCING_RULE_DATA_UPLOAD_FAILED).build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(PROMISE_SOURCING_RULE_DATA_UPLOAD_PARTIAL_SUCCESS)
                .build());
  }

  private Map<String, Boolean> csvReader(List<String> expectedHeaders, Path path)
      throws IOException, CommonServiceException {
    boolean isAllFailed = true;
    boolean isAllPassed = true;
    boolean result = false;
    Map<String, Boolean> resultMap = new HashMap<>();

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser =
            new CSVParser(
                reader,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setIgnoreHeaderCase(true)
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build())) {
      if (!csvParser.getHeaderNames().equals(expectedHeaders)) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            FILE_URI,
            FieldError.builder()
                .rejectedValue(csvParser.getHeaderNames())
                .actualValue(expectedHeaders)
                .errorMessage("CSV File Headers are invalid")
                .build());
        throw new CommonServiceException(
            PROMISE_SOURCING_RULE_DATA_UPLOAD_INVALID_FILE_HEADERS,
            HttpStatus.BAD_REQUEST,
            0x2777,
            errorMap);
      }

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
                result = baseResponse.isSuccess();
                log.info(baseResponse.getMessage());
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
                result = baseResponse.isSuccess();
                log.info(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<PromiseSourcingRuleDto> baseResponse =
                    promiseSourcingRuleFeign.deletePromiseSourcingRule(
                        orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
                result = baseResponse.isSuccess();
                log.info(baseResponse.getMessage());
                break;
              }

            default:
              {
                log.error("action type invalid");
                break;
              }
          }
        } catch (Exception e) {
          if (isAllPassed) {
            isAllPassed = false;
          }
          log.error("Failed to store csv data for row number : {}", row);
        }

        if (isAllPassed) {
          isAllPassed = result;
        }
        if (isAllFailed) {
          isAllFailed = !result;
        }
      }
      resultMap.put("isAllPassed", isAllPassed);
      resultMap.put("isAllFailed", isAllFailed);
      return resultMap;
    }
  }
}
