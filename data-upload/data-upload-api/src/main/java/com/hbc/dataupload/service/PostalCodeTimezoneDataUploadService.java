package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.PostalCodeTimezoneDataUploadConstants.POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
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
public class PostalCodeTimezoneDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  public ResponseEntity<BaseResponse<String>> uploadPostalCodeTimezoneData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(
        path, fileUri, POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, POSTAL_CODE_TIMEZONE_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, POSTAL_CODE_TIMEZONE_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Postal Code Timezone");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailed = true;
    boolean isAllPassed = true;
    boolean result = false;
    Map<String, Boolean> resultMap = new HashMap<>();

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "postalCodeTimezone", POSTAL_CODE_TIMEZONE_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String postalCodePrefix = csvRecord.get(POSTAL_CODE_PREFIX);
          String country = csvRecord.get(COUNTRY);
          String state = csvRecord.get(STATE);
          String city = csvRecord.get(CITY);
          String latitude = csvRecord.get(LATITUDE);
          String longitude = csvRecord.get(LONGITUDE);
          String timeZone = csvRecord.get(TIMEZONE);

          switch (action) {
            case CREATE:
              {
                CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
                    CreatePostalCodeTimezoneRequest.builder()
                        .orgId(orgId)
                        .postalCodePrefix(postalCodePrefix)
                        .country(country)
                        .state(state)
                        .city(city)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timeZone(timeZone)
                        .build();
                BaseResponse<PostalCodeTimezoneDto> baseResponse =
                    postalCodeTimezoneFeign.createPostalCodeTimezone(
                        createPostalCodeTimezoneRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                UpdatePostalCodeTimezoneRequest updatePostalCodeTimezoneRequest =
                    UpdatePostalCodeTimezoneRequest.builder()
                        .country(country)
                        .state(state)
                        .city(city)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timeZone(timeZone)
                        .build();
                BaseResponse<PostalCodeTimezoneDto> baseResponse =
                    postalCodeTimezoneFeign.updatePostalCodeTimezone(
                        orgId, postalCodePrefix, updatePostalCodeTimezoneRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<PostalCodeTimezoneDto> baseResponse =
                    postalCodeTimezoneFeign.deletePostalCodeTimezone(orgId, postalCodePrefix);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            default:
              {
                log.error("action type invalid");
                break;
              }
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
