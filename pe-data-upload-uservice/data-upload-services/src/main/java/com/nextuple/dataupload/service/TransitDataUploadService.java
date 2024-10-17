/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.TRANSIT_DAYS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.TransitDataUploadConstants.TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
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
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, TRANSIT_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, TRANSIT_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, TRANSIT_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, TRANSIT_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Transit");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    var isAllFailedForTransit = true;
    var isAllPassedForTransit = true;
    var transitResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
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
          var transitDays = Float.valueOf(csvRecord.get(TRANSIT_DAYS));

          switch (action) {
            case CREATE:
              {
                transitResult =
                    isCreateTransitDataSuccessful(
                        orgId, sourceGeoZone, destinationGeoZone, carrierServiceId, transitDays);
                break;
              }

            case UPDATE:
              {
                transitResult =
                    isUpdateTransitDataSuccessful(
                        orgId, sourceGeoZone, destinationGeoZone, carrierServiceId, transitDays);
                break;
              }

            case DELETE:
              {
                transitResult =
                    isDeleteTransitDataSuccessful(
                        orgId, sourceGeoZone, destinationGeoZone, carrierServiceId);
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

  private boolean isDeleteTransitDataSuccessful(
      String orgId, String sourceGeoZone, String destinationGeoZone, String carrierServiceId) {
    BaseResponse<TransitResponse> baseResponse =
        transitFeign.deleteTransitDetails(
            orgId, sourceGeoZone, destinationGeoZone, carrierServiceId);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isUpdateTransitDataSuccessful(
      String orgId,
      String sourceGeoZone,
      String destinationGeoZone,
      String carrierServiceId,
      Float transitDays) {
    var transitDataUpdationRequest =
        TransitDataUpdationRequest.builder().transitDays(transitDays).build();
    BaseResponse<TransitResponse> baseResponse =
        transitFeign.updateTransitData(
            orgId, sourceGeoZone, destinationGeoZone, carrierServiceId, transitDataUpdationRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }

  private boolean isCreateTransitDataSuccessful(
      String orgId,
      String sourceGeoZone,
      String destinationGeoZone,
      String carrierServiceId,
      Float transitDays) {
    var transitDataCreationRequest =
        TransitDataCreationRequest.builder()
            .orgId(orgId)
            .sourceGeozone(sourceGeoZone)
            .destinationGeozone(destinationGeoZone)
            .carrierServiceId(carrierServiceId)
            .transitDays(transitDays)
            .build();
    BaseResponse<TransitResponse> baseResponse =
        transitFeign.addTransitData(transitDataCreationRequest);
    log.debug(baseResponse.getMessage());
    return baseResponse.isSuccess();
  }
}
