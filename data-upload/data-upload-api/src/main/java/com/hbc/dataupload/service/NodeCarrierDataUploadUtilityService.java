package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.NodeCarrierDataUploadUtilityConstants.NODE_CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadUtilityConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadUtilityConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadUtilityConstants.NODE_CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadUtilityConstants.NODE_CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.FILE_TYPE;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.PROCESSING_TIME;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.upload.utility.common.constants.DataUploadUtilityConstants.UPDATE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.upload.utility.common.constants.DataUploadUtilityConstants;
import com.hbc.upload.utility.common.headers.DataUploadUtilityExpectedHeaders;
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
public class NodeCarrierDataUploadUtilityService {
  @Value("${properties.basePath}")
  private String basePath;

  @Value("${properties.maxSizeInKiloBytes}")
  private double maxSizeInKiloBytes;

  @Value("${properties.maxRows}")
  private long maxRows;

  private final NodeCarrierFeign nodeCarrierFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeCarrierData(String fileUri)
      throws CommonServiceException, IOException {
    long numberOfRowsInFile;
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("node-carrier");
    final String csvFilePath = basePath.concat(fileUri);

    Path path = Paths.get(csvFilePath);

    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }

    double sizeOfFileInKiloBytes = Files.size(path) / 1024.0;

    log.debug("number of records : {}", numberOfRowsInFile);
    log.debug("fileName : {}", path.getFileName());
    log.debug("size in kB : {}", sizeOfFileInKiloBytes);

    if (!DataUploadUtilityConstants.FILE_TYPE.equals(Files.probeContentType(path))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(DataUploadUtilityConstants.FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
    }

    if (sizeOfFileInKiloBytes > maxSizeInKiloBytes) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          DataUploadUtilityConstants.FILE_URI,
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
          NODE_CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE, HttpStatus.BAD_REQUEST, 0x2774, errorMap);
    }

    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(DataUploadUtilityConstants.FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          NODE_CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
    }

    if (numberOfRowsInFile > maxRows) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          DataUploadUtilityConstants.FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file contains "
                      + numberOfRowsInFile
                      + " rows, Maximum number of rows allowed is "
                      + maxRows)
              .build());
      throw new CommonServiceException(
          NODE_CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE, HttpStatus.BAD_REQUEST, 0x2776, errorMap);
    }

    Map<String, Boolean> resultMap = csvReader(expectedHeaders, path);

    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message("Node Carrier Data successfully uploaded!").build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message("Node Carrier Data upload failed!").build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message("Node Carrier Data partially uploaded with some rows failed!")
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
            DataUploadUtilityConstants.FILE_URI,
            FieldError.builder()
                .rejectedValue(csvParser.getHeaderNames())
                .actualValue(expectedHeaders)
                .errorMessage("CSV File Headers are invalid")
                .build());
        throw new CommonServiceException(
            NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS,
            HttpStatus.BAD_REQUEST,
            0x2777,
            errorMap);
      }

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(DataUploadUtilityConstants.ACTION);
          String nodeId = csvRecord.get(DataUploadUtilityConstants.NODE_ID);
          String orgId = csvRecord.get(DataUploadUtilityConstants.ORG_ID);
          String carrierServiceId = csvRecord.get(DataUploadUtilityConstants.CARRIER_SERVICE_ID);
          String serviceOption = csvRecord.get(DataUploadUtilityConstants.SERVICE_OPTION);
          Double processingTime = Double.valueOf(csvRecord.get(DataUploadUtilityConstants.PROCESSING_TIME));
          String lastPickupTime = csvRecord.get(DataUploadUtilityConstants.LAST_PICKUP_TIME);

          switch (action) {
            case DataUploadUtilityConstants.CREATE:
              {
                NodeCarrierRequest nodeCarrierRequest =
                    NodeCarrierRequest.builder()
                        .nodeId(nodeId)
                        .orgId(orgId)
                        .carrierServiceId(carrierServiceId)
                        .serviceOption(serviceOption)
                        .processingTime(processingTime)
                        .lastPickupTime(lastPickupTime)
                        .build();
                BaseResponse<NodeCarrierResponse> baseResponse =
                    nodeCarrierFeign.createNodeCarrier(nodeCarrierRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DataUploadUtilityConstants.UPDATE:
              {
                NodeCarrierUpdateRequest nodeCarrierUpdateRequest =
                    NodeCarrierUpdateRequest.builder()
                        .processingTime(processingTime)
                        .lastPickupTime(lastPickupTime)
                        .build();
                BaseResponse<NodeCarrierResponse> baseResponse =
                    nodeCarrierFeign.updateNodeCarrier(
                        nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);
                result = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DataUploadUtilityConstants.DELETE:
              {
                BaseResponse<NodeCarrierResponse> baseResponse =
                    nodeCarrierFeign.deleteNodeCarrier(
                        nodeId, orgId, carrierServiceId, serviceOption);
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
