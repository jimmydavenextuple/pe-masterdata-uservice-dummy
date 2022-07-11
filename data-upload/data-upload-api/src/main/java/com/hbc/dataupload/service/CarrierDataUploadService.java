package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTIONS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.CarrierDataUploadConstants.CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
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
public class CarrierDataUploadService {
  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final CarrierFeign carrierFeign;

  public ResponseEntity<BaseResponse<String>> uploadCarrierData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(path, fileUri, CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Carrier");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForCarrier = true;
    boolean isAllPassedForCarrier = true;
    boolean carrierResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(csvParser, "carrier", CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS);
      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String orgId = csvRecord.get(ORG_ID);
          String carrierId = csvRecord.get(CARRIER_ID);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          String carrierName = csvRecord.get(CARRIER_NAME);
          String serviceName = csvRecord.get(SERVICE_NAME);
          String serviceOptions = csvRecord.get(SERVICE_OPTIONS);

          switch (action) {
            case CREATE:
              {
                CarrierServiceRequest carrierServiceRequest =
                    CarrierServiceRequest.builder()
                        .orgId(orgId)
                        .carrierId(carrierId)
                        .carrierServiceId(carrierServiceId)
                        .carrierName(carrierName)
                        .serviceName(serviceName)
                        .serviceOptions(serviceOptions)
                        .build();
                BaseResponse<CarrierServiceResponse> baseResponse =
                    carrierFeign.createCarrierService(carrierServiceRequest);
                carrierResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                CarrierServiceUpdateRequest carrierServiceUpdateRequest =
                    CarrierServiceUpdateRequest.builder()
                        .carrierName(carrierName)
                        .serviceName(serviceName)
                        .serviceOptions(serviceOptions)
                        .build();
                BaseResponse<CarrierServiceResponse> baseResponse =
                    carrierFeign.updateCarrierServiceDetails(
                        carrierId, carrierServiceId, orgId, carrierServiceUpdateRequest);
                carrierResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<CarrierServiceResponse> baseResponse =
                    carrierFeign.deleteCarrierService(carrierId, carrierServiceId, orgId);
                carrierResult = baseResponse.isSuccess();
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
          if (isAllPassedForCarrier) {
            isAllPassedForCarrier = false;
          }
          log.error("Failed to store Carrier CSV data for row number : {}", row);
        }

        if (isAllPassedForCarrier) {
          isAllPassedForCarrier = carrierResult;
        }
        if (isAllFailedForCarrier) {
          isAllFailedForCarrier = !carrierResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForCarrier, isAllFailedForCarrier);
    }
  }
}
