package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NodeCalendarDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CalendarFeign calendarFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeCalendarData(String fileUri)
      throws CommonServiceException, IOException {
    long numberOfRowsInFile;
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("node-calendar");
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
          NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
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
          NODE_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE, HttpStatus.BAD_REQUEST, 0x2774, errorMap);
    }

    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          NODE_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
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
          NODE_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE, HttpStatus.BAD_REQUEST, 0x2776, errorMap);
    }

    Map<String, Boolean> resultMap = csvReader(expectedHeaders, path);

    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message("Node Calendar Data successfully uploaded!").build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message("Node Calendar Data upload failed!").build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message("Node Calendar Data partially uploaded with some rows failed!")
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
            NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS,
            HttpStatus.BAD_REQUEST,
            0x2777,
            errorMap);
      }

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String calendarId = csvRecord.get(CALENDAR_ID);
          String orgId = csvRecord.get(ORG_ID);
          String nodeId = csvRecord.get(NODE_ID);
          String effectiveDate = csvRecord.get(EFFECTIVE_DATE);
          String description = csvRecord.get(DESCRIPTION);

          if (action.equals(CREATE)) {
            NodeCalendarRequest nodeCalendarRequest =
                NodeCalendarRequest.builder()
                    .calendarId(calendarId)
                    .orgId(orgId)
                    .nodeId(nodeId)
                    .effectiveDate(effectiveDate)
                    .description(description)
                    .build();
            BaseResponse<NodeCalendarResponse> baseResponse =
                calendarFeign.handleCreateNodeCalendar(nodeCalendarRequest);
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
