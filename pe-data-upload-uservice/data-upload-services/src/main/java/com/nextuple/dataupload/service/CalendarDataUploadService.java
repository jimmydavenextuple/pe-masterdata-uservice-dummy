/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_DAYS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_FRIDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_MONDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_SATURDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_SUNDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_THURSDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_TUESDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_WEDNESDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.inbound.CalendarRequest;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
public class CalendarDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CalendarFeign calendarFeign;

  public ResponseEntity<BaseResponse<String>> uploadCalendarData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Calendar");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForCalendar = true;
    var isAllPassedForCalendar = true;
    var calendarResult = false;
    var mapper = new ObjectMapper();

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "calendar", CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String calendarId = csvRecord.get(CALENDAR_ID);
          String orgId = csvRecord.get(ORG_ID);
          String description = csvRecord.get(DESCRIPTION);
          var isMondayWorking = Boolean.valueOf(csvRecord.get(IS_MONDAY_WORKING));
          var isTuesdayWorking = Boolean.valueOf(csvRecord.get(IS_TUESDAY_WORKING));
          var isWednesdayWorking = Boolean.valueOf(csvRecord.get(IS_WEDNESDAY_WORKING));
          var isThursdayWorking = Boolean.valueOf(csvRecord.get(IS_THURSDAY_WORKING));
          var isFridayWorking = Boolean.valueOf(csvRecord.get(IS_FRIDAY_WORKING));
          var isSaturdayWorking = Boolean.valueOf(csvRecord.get(IS_SATURDAY_WORKING));
          var isSundayWorking = Boolean.valueOf(csvRecord.get(IS_SUNDAY_WORKING));
          List<ExceptionDays> exceptionDaysList =
              mapper.readValue(
                  csvRecord.get(EXCEPTION_DAYS), new TypeReference<List<ExceptionDays>>() {});

          if (action.equals(CREATE)) {
            var calendarRequest = new CalendarRequest();
            calendarRequest.setCalendarId(calendarId);
            calendarRequest.setDescription(description);
            calendarRequest.setOrgId(orgId);
            calendarRequest.setIsMondayWorking(isMondayWorking);
            calendarRequest.setIsTuesdayWorking(isTuesdayWorking);
            calendarRequest.setIsWednesdayWorking(isWednesdayWorking);
            calendarRequest.setIsThursdayWorking(isThursdayWorking);
            calendarRequest.setIsFridayWorking(isFridayWorking);
            calendarRequest.setIsSaturdayWorking(isSaturdayWorking);
            calendarRequest.setIsSundayWorking(isSundayWorking);
            calendarRequest.setExceptionDays(exceptionDaysList);

            BaseResponse<CalendarResponse> baseResponse =
                calendarFeign.createCalendar(calendarRequest);
            calendarResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error(ACTION_INVALID_MESSAGE);
          }
        } catch (Exception e) {
          if (isAllPassedForCalendar) {
            isAllPassedForCalendar = false;
          }
          log.error("Failed to store Calendar CSV data for row number : {}", row);
        }
        if (isAllPassedForCalendar) {
          isAllPassedForCalendar = calendarResult;
        }
        if (isAllFailedForCalendar) {
          isAllFailedForCalendar = !calendarResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForCalendar, isAllFailedForCalendar);
    }
  }
}
