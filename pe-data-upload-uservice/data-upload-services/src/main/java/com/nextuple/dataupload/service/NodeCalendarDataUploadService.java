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
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.NodeCalendarDataUploadConstants.NODE_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
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
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, NODE_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node Calendar");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForNodeCalendar = true;
    var isAllPassedForNodeCalendar = true;
    var nodeCalendarResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "node-calendar", NODE_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS);

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
            var nodeCalendarRequest =
                NodeCalendarRequest.builder()
                    .calendarId(calendarId)
                    .orgId(orgId)
                    .nodeId(nodeId)
                    .effectiveDate(effectiveDate)
                    .description(description)
                    .build();
            BaseResponse<NodeCalendarResponse> baseResponse =
                calendarFeign.createNodeCalendar(nodeCalendarRequest);
            nodeCalendarResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error(ACTION_INVALID_MESSAGE);
          }
        } catch (Exception e) {
          if (isAllPassedForNodeCalendar) {
            isAllPassedForNodeCalendar = false;
          }
          log.error("Failed to store Node Calendar data for row number : {}", row);
        }

        if (isAllPassedForNodeCalendar) {
          isAllPassedForNodeCalendar = nodeCalendarResult;
        }
        if (isAllFailedForNodeCalendar) {
          isAllFailedForNodeCalendar = !nodeCalendarResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForNodeCalendar, isAllFailedForNodeCalendar);
    }
  }
}
