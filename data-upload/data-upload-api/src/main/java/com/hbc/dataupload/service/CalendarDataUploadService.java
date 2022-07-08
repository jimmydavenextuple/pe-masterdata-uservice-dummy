package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
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
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.headers.DataUploadUtilityExpectedHeaders;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    long numberOfRowsInFile;
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("calendar");
    final String csvFilePath = basePath.concat(fileUri);

    Path path = Paths.get(csvFilePath);

    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }

    double sizeOfFileInKiloBytes = Files.size(path) / 1024.0;

    log.debug("number of records : {}", numberOfRowsInFile);
    log.debug("fileName : {}", path.getFileName());
    log.debug("size in kB : {}", sizeOfFileInKiloBytes);

    if (!FILE_TYPE.equals(Files.probeContentType(path))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
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
          CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE, HttpStatus.BAD_REQUEST, 0x2774, errorMap);
    }

    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
    }

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
      throw new CommonServiceException(
          CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE, HttpStatus.BAD_REQUEST, 0x2776, errorMap);
    }

    Map<String, Boolean> resultMap = csvReader(expectedHeaders, path);

    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message("Calendar Data successfully uploaded!").build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message("Calendar Data upload failed!").build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message("Calendar Data partially uploaded with some rows failed!")
                .build());
  }

  private Map<String, Boolean> csvReader(List<String> expectedHeaders, Path path)
      throws IOException, CommonServiceException {
    boolean isAllFailed = true;
    boolean isAllPassed = true;
    boolean result = false;
    Map<String, Boolean> resultMap = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();

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
                    .setEscape('\\')
                    .setQuoteMode(QuoteMode.NONE)
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
            CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS, HttpStatus.BAD_REQUEST, 0x2777, errorMap);
      }

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
            result = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error("action type invalid");
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
