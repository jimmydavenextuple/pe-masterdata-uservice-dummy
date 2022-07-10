package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIPPING_STAGE;
import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
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
public class CarrierCalendarDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CalendarFeign calendarFeign;

  public ResponseEntity<BaseResponse<String>> uploadCarrierCalendarData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(path, fileUri, CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Carrier Calendar");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailed = true;
    boolean isAllPassed = true;
    boolean result = false;
    Map<String, Boolean> resultMap = new HashMap<>();

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "carrier-calendar", CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String calendarId = csvRecord.get(CALENDAR_ID);
          String orgId = csvRecord.get(ORG_ID);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          String shippingStage = csvRecord.get(SHIPPING_STAGE);
          String description = csvRecord.get(DESCRIPTION);
          String effectiveDate = csvRecord.get(EFFECTIVE_DATE);

          if (action.equals(CREATE)) {
            CarrierServiceCalendarRequest carrierServiceCalendarRequest =
                CarrierServiceCalendarRequest.builder()
                    .calendarId(calendarId)
                    .orgId(orgId)
                    .carrierServiceId(carrierServiceId)
                    .shippingStage(shippingStage)
                    .description(description)
                    .effectiveDate(effectiveDate)
                    .build();
            BaseResponse<CarrierServiceCalendarResponse> baseResponse =
                calendarFeign.handleCreateCarrierServiceCalendar(carrierServiceCalendarRequest);
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
