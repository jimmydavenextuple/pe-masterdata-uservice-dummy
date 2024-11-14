/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.utils;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.dataupload.common.headers.DataUploadUtilityExpectedHeaders;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtil {
  public static void compareHeaders(CSVParser csvParser, String serviceName, String errorMessage)
      throws CommonServiceException {

    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName);
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
  }

  public static void validateAction(Path path) throws CommonServiceException, IOException {
    var line = "";
    List<String> actions = new ArrayList<>();

    try (var br = Files.newBufferedReader(path)) {
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        actions.add(values[0]);
      }
      actions.remove("action");
      for (Object action : actions) {
        if (action == null
            || !(action.toString().equalsIgnoreCase(UPDATE)
                || action.toString().equalsIgnoreCase(DELETE))) {
          throw new CommonServiceException(
              ACTION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST, 0x1777, null);
        }
      }
    }
  }

  public static ResponseEntity<BaseResponse<String>> getResponse(
      Map<String, Boolean> resultMap, String serviceName) {
    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message(serviceName + " Data successfully uploaded!").build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message(serviceName + " Data upload failed!").build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(serviceName + " Data partially uploaded with some rows failed!")
                .build());
  }

  public static void validateFileType(String fileUri, String errorMessage)
      throws IOException, CommonServiceException {
    String fileType = FilenameUtils.getExtension(fileUri);
    log.debug("file type : {}", fileType);

    if (!fileType.equals("csv")) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
    }
  }

  public static void validateFileSize(
      Path path, String fileUri, double maxSizeInKiloBytes, String errorMessage)
      throws IOException, CommonServiceException {
    double sizeOfFileInKiloBytes = Files.size(path) / 1024.0;
    log.debug("Size in kB : {}", sizeOfFileInKiloBytes);
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
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2774, errorMap);
    }
  }

  public static void validateFileRows(Path path, String fileUri, long maxRows, String errorMessage)
      throws IOException, CommonServiceException {
    long numberOfRowsInFile;
    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }
    log.debug("Number of rows : {}", numberOfRowsInFile);
    if (numberOfRowsInFile > maxRows) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file contains "
                      + numberOfRowsInFile
                      + " rows, Maximum number of rows allowed is "
                      + maxRows)
              .build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2776, errorMap);
    }
  }

  public static void checkForEmptyRecords(Path path, String fileUri, String errorMessage)
      throws IOException, CommonServiceException {
    long numberOfRowsInFile;
    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }
    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
    }
  }

  public static CSVParser getCSVParserWithSetQuoteMode(Reader reader) throws IOException {
    return new CSVParser(
        reader,
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setIgnoreHeaderCase(true)
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setEscape('\\')
            .setQuoteMode(QuoteMode.NONE)
            .build());
  }

  public static CSVParser getCSVParser(Reader reader) throws IOException {
    return new CSVParser(
        reader,
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setIgnoreHeaderCase(true)
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build());
  }

  public static Path getPath(String basePath, String fileUri) {
    var path = Paths.get(basePath.concat(fileUri));
    log.debug("fileName : {}", path.getFileName());
    return path;
  }

  public static Map<String, Boolean> storeToMap(boolean isAllPassed, boolean isAllFailed) {
    Map<String, Boolean> resultMap = new HashMap<>();
    resultMap.put("isAllPassed", isAllPassed);
    resultMap.put("isAllFailed", isAllFailed);
    return resultMap;
  }

  public static Optional<CarrierServiceCalendarResponse> getActiveCalendarResponse(
      List<CarrierServiceCalendarResponse> carrierServiceCalendarResponses) {

    String presentDate = getPresentDate();

    Optional<CarrierServiceCalendarResponse> activeCarrierCalendar =
        carrierServiceCalendarResponses.stream()
            .filter(x -> x.getEffectiveDate().compareTo(presentDate) <= 0)
            .max(Comparator.comparing(CarrierServiceCalendarResponse::getEffectiveDate));

    if (activeCarrierCalendar.isEmpty()) {
      activeCarrierCalendar =
          carrierServiceCalendarResponses.stream()
              .filter(x -> x.getEffectiveDate().compareTo(presentDate) > 0)
              .min(Comparator.comparing(CarrierServiceCalendarResponse::getEffectiveDate));
    }

    return activeCarrierCalendar;
  }

  public static String getPresentDate() {
    var dt = new DateTime();
    var dtWithUTC = dt.withZone(DateTimeZone.forID("UTC"));

    return dtWithUTC.toString("yyyy-MM-dd");
  }
}
