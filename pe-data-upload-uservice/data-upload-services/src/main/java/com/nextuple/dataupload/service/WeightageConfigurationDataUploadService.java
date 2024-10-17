/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.KEY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.WEIGHTAGE;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.WeightageConfigurationDataUploadConstants.WEIGHTAGE_CONFIGURATION_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.feign.WeightageConfigurationFeign;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
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

          if (action.equalsIgnoreCase(UPDATE)) {
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
