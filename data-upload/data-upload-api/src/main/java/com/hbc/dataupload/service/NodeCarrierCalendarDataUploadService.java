package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.helper.NodeCarrierCalendarDataUploadConstants.NODE_CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCarrierCalendarDataUploadConstants.NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCarrierCalendarDataUploadConstants.NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCarrierCalendarDataUploadConstants.NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierCalendarDataUploadConstants.NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class NodeCarrierCalendarDataUploadService {
  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CalendarFeign calendarFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeCarrierCalendarData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(
        path, fileUri, NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, NODE_CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node Carrier Calendar");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForNodeCarrierCalendar = true;
    boolean isAllPassedForNodeCarrierCalendar = true;
    boolean nodeCarrierCalendarResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "node-carrier-calendar",
          NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String calendarId = csvRecord.get(CALENDAR_ID);
          String orgId = csvRecord.get(ORG_ID);
          String nodeId = csvRecord.get(NODE_ID);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          String description = csvRecord.get(DESCRIPTION);
          String effectiveDate = csvRecord.get(EFFECTIVE_DATE);

          if (action.equals(CREATE)) {
            NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest =
                NodeCarrierServiceCalendarRequest.builder()
                    .calendarId(calendarId)
                    .orgId(orgId)
                    .nodeId(nodeId)
                    .carrierServiceId(carrierServiceId)
                    .description(description)
                    .effectiveDate(effectiveDate)
                    .build();
            BaseResponse<NodeCarrierServiceCalendarResponse> baseResponse =
                calendarFeign.handleCreateNodeCarrierServiceCalendar(
                    nodeCarrierServiceCalendarRequest);
            nodeCarrierCalendarResult = baseResponse.isSuccess();
            log.debug(baseResponse.getMessage());
          } else {
            log.error(ACTION_INVALID_MESSAGE);
          }
        } catch (Exception e) {
          if (isAllPassedForNodeCarrierCalendar) {
            isAllPassedForNodeCarrierCalendar = false;
          }
          log.error("Failed to store Node Carrier Calendar CSV data for row number : {}", row);
        }

        if (isAllPassedForNodeCarrierCalendar) {
          isAllPassedForNodeCarrierCalendar = nodeCarrierCalendarResult;
        }
        if (isAllFailedForNodeCarrierCalendar) {
          isAllFailedForNodeCarrierCalendar = !nodeCarrierCalendarResult;
        }
      }
      return DataUploadUtil.storeToMap(
          isAllPassedForNodeCarrierCalendar, isAllFailedForNodeCarrierCalendar);
    }
  }
}
