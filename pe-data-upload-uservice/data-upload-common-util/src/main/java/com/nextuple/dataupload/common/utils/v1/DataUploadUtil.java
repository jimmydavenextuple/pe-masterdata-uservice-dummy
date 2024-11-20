/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.utils.v1;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CSV_HEADERS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FILE_TYPE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtil {

  public static void validateFileType(String fileType, String errorMessage)
      throws CommonServiceException {
    log.debug("file type : {}", fileType);

    if (!fileType.equals("text/csv")) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_TYPE, FieldError.builder().rejectedValue(fileType).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
    }
  }

  public static void validateCSVHeaders(
      String[] csvFileContents, String serviceName, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName);
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

  public static void validateEmptyCSV(
      List<String[]> csvFileContents, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (CollectionUtils.isEmpty(csvFileContents) || csvFileContents.size() == 1) {
      csvReader.close();
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }

  public static void validateForEmptySourceOrDestinationGeozoneCSVData(
      List<String[]> csvFileContents, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    var sourceGeoZoneCount = 0;
    var destinationGeoZoneCount = 0;

    // Remove the headers
    csvFileContents.remove(0);

    for (String[] rows : csvFileContents) {
      if (StringUtils.hasLength(rows[0])) {
        sourceGeoZoneCount++;
      }
      if (StringUtils.hasLength(rows[1])) {
        destinationGeoZoneCount++;
      }
    }

    if (sourceGeoZoneCount == 0 || destinationGeoZoneCount == 0) {
      csvReader.close();
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }

  public static Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }

  public static void validateEmptyCSVForCostDefinition(
      List<String[]> csvFileContents, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (CollectionUtils.isEmpty(csvFileContents) || csvFileContents.size() < 4) {
      csvReader.close();
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }
}
