package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_FRIDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_MONDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SATURDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SUNDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_THURSDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_TUESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_WEDNESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.inbound.CalendarRequest;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.pojo.ExceptionDays;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Calendar");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForCalendar = true;
    boolean isAllPassedForCalendar = true;
    boolean calendarResult = false;
    ObjectMapper mapper = new ObjectMapper();

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
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
          Boolean isMondayWorking = Boolean.valueOf(csvRecord.get(IS_MONDAY_WORKING));
          Boolean isTuesdayWorking = Boolean.valueOf(csvRecord.get(IS_TUESDAY_WORKING));
          Boolean isWednesdayWorking = Boolean.valueOf(csvRecord.get(IS_WEDNESDAY_WORKING));
          Boolean isThursdayWorking = Boolean.valueOf(csvRecord.get(IS_THURSDAY_WORKING));
          Boolean isFridayWorking = Boolean.valueOf(csvRecord.get(IS_FRIDAY_WORKING));
          Boolean isSaturdayWorking = Boolean.valueOf(csvRecord.get(IS_SATURDAY_WORKING));
          Boolean isSundayWorking = Boolean.valueOf(csvRecord.get(IS_SUNDAY_WORKING));
          List<ExceptionDays> exceptionDaysList =
              mapper.readValue(
                  csvRecord.get(EXCEPTION_DAYS), new TypeReference<List<ExceptionDays>>() {});

          if (action.equals(CREATE)) {
            CalendarRequest calendarRequest =
                CalendarRequest.builder()
                    .calendarId(calendarId)
                    .description(description)
                    .orgId(orgId)
                    .isMondayWorking(isMondayWorking)
                    .isTuesdayWorking(isTuesdayWorking)
                    .isWednesdayWorking(isWednesdayWorking)
                    .isThursdayWorking(isThursdayWorking)
                    .isFridayWorking(isFridayWorking)
                    .isSaturdayWorking(isSaturdayWorking)
                    .isSundayWorking(isSundayWorking)
                    .exceptionDays(exceptionDaysList)
                    .build();
            BaseResponse<CalendarResponse> baseResponse =
                calendarFeign.handleCreateCalendar(calendarRequest);
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
