package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.NodeDataUploadConstants.NODE_DATA_UPLOAD_LARGE_ROW_SIZE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
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
public class NodeDataUploadService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private final NodeFeign nodeFeign;

  public ResponseEntity<BaseResponse<String>> uploadNodeData(String fileUri)
      throws CommonServiceException, IOException {
    Path path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, NODE_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(path, fileUri, maxRows, NODE_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(path, fileUri, NODE_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = csvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node");
  }

  private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
    boolean isAllFailedForNode = true;
    boolean isAllPassedForNode = true;
    boolean nodeResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        CSVParser csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(csvParser, "node", NODE_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String action = csvRecord.get(ACTION);
          String nodeId = csvRecord.get(NODE_ID);
          String orgId = csvRecord.get(ORG_ID);
          String street = csvRecord.get(STREET);
          String city = csvRecord.get(CITY);
          String province = csvRecord.get(PROVINCE);
          String postalCode = csvRecord.get(POSTAL_CODE);
          String country = csvRecord.get(COUNTRY);
          String latitude = csvRecord.get(LATITUDE);
          String longitude = csvRecord.get(LONGITUDE);
          String timezone = csvRecord.get(TIMEZONE);
          Boolean shipToHome = Boolean.valueOf(csvRecord.get(SHIP_TO_HOME));
          Boolean sdndEligible = Boolean.valueOf(csvRecord.get(SDND_ELIGIBLE));
          Boolean bopisEligible = Boolean.valueOf(csvRecord.get(BOPIS_ELIGIBLE));
          Boolean expressEligible = Boolean.valueOf(csvRecord.get(EXPRESS_ELIGIBLE));
          String nodeType = csvRecord.get(NODE_TYPE);
          Boolean isActive = Boolean.valueOf(csvRecord.get(IS_ACTIVE));

          switch (action) {
            case CREATE:
              {
                NodeRequest nodeRequest =
                    NodeRequest.builder()
                        .nodeId(nodeId)
                        .orgId(orgId)
                        .street(street)
                        .city(city)
                        .province(province)
                        .postalCode(postalCode)
                        .country(country)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timezone(timezone)
                        .shipToHome(shipToHome)
                        .sdndEligible(sdndEligible)
                        .bopisEligible(bopisEligible)
                        .expressEligible(expressEligible)
                        .nodeType(nodeType)
                        .isActive(isActive)
                        .build();
                BaseResponse<NodeResponse> baseResponse = nodeFeign.createNode(nodeRequest);
                nodeResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case UPDATE:
              {
                NodeUpdationRequest nodeUpdationRequest =
                    NodeUpdationRequest.builder()
                        .street(street)
                        .city(city)
                        .province(province)
                        .postalCode(postalCode)
                        .country(country)
                        .latitude(latitude)
                        .longitude(longitude)
                        .timezone(timezone)
                        .shipToHome(shipToHome)
                        .sdndEligible(sdndEligible)
                        .bopisEligible(bopisEligible)
                        .expressEligible(expressEligible)
                        .nodeType(nodeType)
                        .isActive(isActive)
                        .build();
                BaseResponse<NodeResponse> baseResponse =
                    nodeFeign.updateNodeDetails(nodeId, orgId, nodeUpdationRequest);
                nodeResult = baseResponse.isSuccess();
                log.debug(baseResponse.getMessage());
                break;
              }

            case DELETE:
              {
                BaseResponse<NodeResponse> baseResponse = nodeFeign.deleteNode(nodeId, orgId);
                nodeResult = baseResponse.isSuccess();
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
          if (isAllPassedForNode) {
            isAllPassedForNode = false;
          }
          log.error("Failed to store Node CSV data for row number : {}", row);
        }

        if (isAllPassedForNode) {
          isAllPassedForNode = nodeResult;
        }
        if (isAllFailedForNode) {
          isAllFailedForNode = !nodeResult;
        }
      }
      return DataUploadUtil.storeToMap(isAllPassedForNode, isAllFailedForNode);
    }
  }
}
