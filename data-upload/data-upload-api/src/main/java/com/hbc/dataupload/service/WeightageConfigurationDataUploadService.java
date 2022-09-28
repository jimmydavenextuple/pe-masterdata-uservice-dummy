package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.KEY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE_U;
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
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE);
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
    var isAllFailedForWeightage = true;
    var isAllPassedForWeightage = true;
    var weightageResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "weightageConfiguration",
          WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS);

      DataUploadUtil.validateAction(path);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String type = csvRecord.get(TYPE);
          String key = csvRecord.get(KEY);
          var weightage = Float.valueOf(csvRecord.get(WEIGHTAGE));

          if (action.equalsIgnoreCase(UPDATE_U)) {
            var createWeightageConfigurationRequest =
                CreateWeightageConfigurationRequest.builder()
                    .orgId(orgId)
                    .type(type)
                    .key(key)
                    .weightage(weightage)
                    .build();
            BaseResponse<WeightageConfigurationDto> baseResponse =
                weightageConfigurationFeign.createWeightageConfiguration(
                    createWeightageConfigurationRequest);
            weightageResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            BaseResponse<WeightageConfigurationDto> baseResponse =
                weightageConfigurationFeign.deleteWeightageConfiguration(orgId, type, key);
            weightageResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          }
        } catch (Exception e) {
          if (isAllPassedForWeightage) {
            isAllPassedForWeightage = false;
          }
          log.error("Failed to store Weightage Configuration CSV data for row number : {}", row);
        }

        if (isAllPassedForWeightage) {
          isAllPassedForWeightage = weightageResult;
        }
        if (isAllFailedForWeightage) {
          isAllFailedForWeightage = !weightageResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForWeightage, isAllFailedForWeightage);
    }
  }
}
