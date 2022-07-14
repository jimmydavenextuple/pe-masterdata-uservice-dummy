package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROCESSING_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeCarrierDataUploadConstants.NODE_CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
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
public class NodeCarrierDataUploadService {
  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final NodeCarrierFeign nodeCarrierFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeCarrierData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_CARRIER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_CARRIER_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, NODE_CARRIER_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node Carrier");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForNodeCarrier = true;
    boolean isAllPassedForNodeCarrier = true;
    boolean nodeCarrierResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser, "node-carrier", NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String nodeId = csvRecord.get(NODE_ID);
          String orgId = csvRecord.get(ORG_ID);
          String carrierServiceId = csvRecord.get(CARRIER_SERVICE_ID);
          String serviceOption = csvRecord.get(SERVICE_OPTION);
          Double processingTime = Double.valueOf(csvRecord.get(PROCESSING_TIME));
          String lastPickupTime = csvRecord.get(LAST_PICKUP_TIME);

          switch (action) {
            case CREATE:
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
                nodeCarrierResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                NodeCarrierUpdateRequest nodeCarrierUpdateRequest =
                    NodeCarrierUpdateRequest.builder()
                        .processingTime(processingTime)
                        .lastPickupTime(lastPickupTime)
                        .build();
                BaseResponse<NodeCarrierResponse> baseResponse =
                    nodeCarrierFeign.updateNodeCarrier(
                        nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);
                nodeCarrierResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<NodeCarrierResponse> baseResponse =
                    nodeCarrierFeign.deleteNodeCarrier(
                        nodeId, orgId, carrierServiceId, serviceOption);
                nodeCarrierResult = baseResponse.isSuccess();
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
          if (isAllPassedForNodeCarrier) {
            isAllPassedForNodeCarrier = false;
          }
          log.error("Failed to store Node Carrier CSV data for row number : {}", row);
        }

        if (isAllPassedForNodeCarrier) {
          isAllPassedForNodeCarrier = nodeCarrierResult;
        }
        if (isAllFailedForNodeCarrier) {
          isAllFailedForNodeCarrier = !nodeCarrierResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForNodeCarrier, isAllFailedForNodeCarrier);
    }
  }
}
