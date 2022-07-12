package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TRANSIT_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
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
public class TransitDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final TransitFeign transitFeign;

  public ResponseEntity<BaseResponse<String>> uploadTransitData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Transit");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForTransit = true;
    boolean isAllPassedForTransit = true;
    boolean transitResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(csvParser, "transit", TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String sourceGeoZone = csvRecord.get(SOURCE_GEO_ZONE);
          String destinationGeoZone = csvRecord.get(DESTINATION_GEO_ZONE);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          Float transitDays = Float.valueOf(csvRecord.get(TRANSIT_DAYS));

          switch (action) {
            case CREATE:
              {
                TransitDataCreationRequest transitDataCreationRequest =
                    TransitDataCreationRequest.builder()
                        .orgId(orgId)
                        .sourceGeozone(sourceGeoZone)
                        .destinationGeozone(destinationGeoZone)
                        .carrierServiceId(carrierServiceId)
                        .transitDays(transitDays)
                        .build();
                BaseResponse<TransitResponse> baseResponse =
                    transitFeign.addTransitData(transitDataCreationRequest);
                transitResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                TransitDataUpdationRequest transitDataUpdationRequest =
                    TransitDataUpdationRequest.builder().transitDays(transitDays).build();
                BaseResponse<TransitResponse> baseResponse =
                    transitFeign.updateTransitData(
                        orgId,
                        sourceGeoZone,
                        destinationGeoZone,
                        carrierServiceId,
                        transitDataUpdationRequest);
                transitResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<TransitResponse> baseResponse =
                    transitFeign.deleteTransitDetails(
                        orgId, sourceGeoZone, destinationGeoZone, carrierServiceId);
                transitResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            default:
              {
                log.error(ACTION_INVALID_MESSAGE);
                break;
              }
          }
        } catch (Exception e) {
          if (isAllPassedForTransit) {
            isAllPassedForTransit = false;
          }
          log.error("Failed to store Transit CSV data for row number : {}", row);
        }

        if (isAllPassedForTransit) {
          isAllPassedForTransit = transitResult;
        }
        if (isAllFailedForTransit) {
          isAllFailedForTransit = !transitResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForTransit, isAllFailedForTransit);
    }
  }
}
