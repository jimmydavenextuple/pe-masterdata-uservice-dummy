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
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIPPING_STAGE;
import static com.nextuple.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.inbound.CarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
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
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
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
    var isAllFailedForCarrierCalendar = true;
    var isAllPassedForCarrierCalendar = true;
    var carrierCalendar = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
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
            var carrierServiceCalendarRequest =
                CarrierServiceCalendarRequest.builder()
                    .calendarId(calendarId)
                    .orgId(orgId)
                    .carrierServiceId(carrierServiceId)
                    .shippingStage(shippingStage)
                    .description(description)
                    .effectiveDate(effectiveDate)
                    .build();
            BaseResponse<CarrierServiceCalendarResponse> baseResponse =
                calendarFeign.createCarrierServiceCalendar(carrierServiceCalendarRequest);
            carrierCalendar = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error(ACTION_INVALID_MESSAGE);
          }
        } catch (Exception e) {
          if (isAllPassedForCarrierCalendar) {
            isAllPassedForCarrierCalendar = false;
          }
          log.error("Failed to store Carrier Calendar CSV data for row number : {}", row);
        }

        if (isAllPassedForCarrierCalendar) {
          isAllPassedForCarrierCalendar = carrierCalendar;
        }
        if (isAllFailedForCarrierCalendar) {
          isAllFailedForCarrierCalendar = !carrierCalendar;
        }
      }
      return DataUploadUtil.storeToMap(
          isAllPassedForCarrierCalendar, isAllFailedForCarrierCalendar);
    }
  }
}
