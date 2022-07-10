package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.KEY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.WEIGHTAGE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.feign.WeightageConfigurationFeign;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
public class WeightageConfigurationDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final WeightageConfigurationFeign weightageConfigurationFeign;

  public ResponseEntity<BaseResponse<String>> uploadWeightageConfigurationData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(
        path, fileUri, WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Weightage Configuration");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailed = true;
    boolean isAllPassed = true;
    boolean result = false;
    Map<String, Boolean> resultMap = new HashMap<>();

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "weightageConfiguration",
          WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String type = csvRecord.get(TYPE);
          String key = csvRecord.get(KEY);
          Float weightage = Float.valueOf(csvRecord.get(WEIGHTAGE));

          switch (action) {
            case CREATE:
              {
                CreateWeightageConfigurationRequest createWeightageConfigurationRequest =
                    CreateWeightageConfigurationRequest.builder()
                        .orgId(orgId)
                        .type(type)
                        .key(key)
                        .weightage(weightage)
                        .build();
                BaseResponse<WeightageConfigurationDto> baseResponse =
                    weightageConfigurationFeign.createWeightageConfiguration(
                        createWeightageConfigurationRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                UpdateWeightageConfigurationRequest updateWeightageConfigurationRequest =
                    UpdateWeightageConfigurationRequest.builder()
                        .type(type)
                        .key(key)
                        .weightage(weightage)
                        .build();
                BaseResponse<WeightageConfigurationDto> baseResponse =
                    weightageConfigurationFeign.updateWeightageConfiguration(
                        orgId, type, key, updateWeightageConfigurationRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<WeightageConfigurationDto> baseResponse =
                    weightageConfigurationFeign.deleteWeightageConfiguration(orgId, type, key);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
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
