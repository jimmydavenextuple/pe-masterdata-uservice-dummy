/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.utils.v1;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CSV_HEADERS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DynamicCsvHeadersValidation {

  @Autowired private TenantDatabaseConfig tenantDatabaseConfig;
  public static final String CUSTOM_ATTRIBUTES_HEADER_PREFIX = "customAttributes_";
  public static final String LINES_CUSTOM_ATTRIBUTES_HEADER_PREFIX = "lines_customAttributes_";

  public void validateCSVHeaders(
      String[] csvFileContents, String serviceName, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {

    List<String> expectedHeaders = getCSVExpectedHeaders(serviceName);

    if (!expectedHeaders.equals(Arrays.asList(csvFileContents))) {
      csvReader.close();
      throw new CommonServiceException(
          errorMessage,
          HttpStatus.BAD_REQUEST,
          0x2777,
          Map.of(
              CSV_HEADERS,
              FieldError.builder()
                  .rejectedValue(csvFileContents)
                  .actualValue(expectedHeaders)
                  .build()));
    }
  }

  public List<String> getCSVExpectedHeaders(String serviceName) throws CommonServiceException {
    String[] serviceOptions = tenantDatabaseConfig.getCurrentTenantServiceOptions();

    List<String> expectedHeaders =
        new ArrayList<>(DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName));

    expectedHeaders.addAll(Arrays.asList(serviceOptions));
    return expectedHeaders;
  }

  public void validateCSVHeadersForEDD(
      CSVParser csvParser,
      String serviceName,
      String errorMessage,
      Map<String, String> customAttributes,
      Map<String, String> lineCustomAttributes)
      throws CommonServiceException {
    if (Objects.nonNull(customAttributes) && Objects.nonNull(lineCustomAttributes)) {
      List<String> expectedHeaders =
          new ArrayList<>(DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName));
      lineCustomAttributes.forEach(
          (key, value) -> expectedHeaders.add(LINES_CUSTOM_ATTRIBUTES_HEADER_PREFIX + key));
      customAttributes.forEach(
          (key, value) -> expectedHeaders.add(CUSTOM_ATTRIBUTES_HEADER_PREFIX + key));
      if (!csvParser.getHeaderNames().equals(expectedHeaders)) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            FILE_URI,
            FieldError.builder()
                .rejectedValue(csvParser.getHeaderNames())
                .actualValue(expectedHeaders)
                .errorMessage("CSV File Headers are invalid")
                .build());
        throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2777, errorMap);
      }
    } else {
      DataUploadUtil.compareHeaders(csvParser, serviceName, errorMessage);
    }
  }
}
