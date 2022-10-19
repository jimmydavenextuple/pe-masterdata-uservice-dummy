package com.hbc.csvdownload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.helper.EddComputationUploadConstants;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.intermediary.domain.Item;
import com.hbc.intermediary.domain.SfccOrder;
import com.hbc.intermediary.domain.SfccOrderLine;
import com.hbc.intermediary.domain.SfccResponse;
import com.hbc.intermediary.domain.ShipToAddress;
import com.hbc.intermediary.feign.IntermediaryServiceFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BASKET_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ITEM_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ITEM_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_SELLER;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_UNIT_OF_MEASURE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_LINE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_REQUIRED_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_ZIPCODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORGANIZATION_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PAGE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SESSION_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_ZIPCODE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EddComputationService {

    @Value("${dataupload.base-path}")
    private String basePath;

    @Value("${dataupload.maxsize-in-kilobytes}")
    private double maxSizeInKiloBytes;

    @Value("${dataupload.max-rows}")
    private long maxRows;

    private final IntermediaryServiceFeign intermediaryServiceFeign;

    public ResponseEntity<BaseResponse<String>> uploadEddCompuationData(String fileUri)
            throws CommonServiceException, IOException {
        var path = DataUploadUtil.getPath(basePath, fileUri);

        DataUploadUtil.validateFileType(fileUri, EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_TYPE);
        DataUploadUtil.validateFileSize(
                path, fileUri, maxSizeInKiloBytes, EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_LARGE_FILE_SIZE);
        DataUploadUtil.validateFileRows(path, fileUri, maxRows, EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_LARGE_ROW_SIZE);
        DataUploadUtil.checkForEmptyRecords(path, fileUri, EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_FILE_EMPTY_RECORDS);

        Map<String, Boolean> resultMap = csvReader(path);
        return DataUploadUtil.getResponse(resultMap, "intermediary");
    }

    private Map<String, Boolean> csvReader(Path path) throws IOException, CommonServiceException {
        var isAllFailedForEddComputation = true;
        var isAllPassedForEddComputation = true;
        var eddComputationResult = false;

        try (Reader reader = Files.newBufferedReader(path);
             var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
            DataUploadUtil.compareHeaders(
                    csvParser, "intermediary", EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_HEADERS);

            List<SfccResponse> sfccResponseList = new ArrayList<>();
            Set<String> basketIdSet = new HashSet<>();
            SfccOrder sfccOrder = null;
            
            for (CSVRecord csvRecord : csvParser) {
                long row = csvParser.getCurrentLineNumber();
                try {
                    // Accessing Values by Column Header Name
                    String orgId = csvRecord.get(ORGANIZATION_CODE);
                    String sessionId = csvRecord.get(SESSION_ID);
                    String basketId = csvRecord.get(BASKET_ID);
                    String pageName = csvRecord.get(PAGE_NAME);
                    String shipToAddressZipCode = csvRecord.get(SHIP_TO_ADDRESS_ZIPCODE);
                    String shipToAddressProvince = csvRecord.get(SHIP_TO_ADDRESS_PROVINCE);
                    String linesItemItemId = csvRecord.get(LINES_ITEM_ITEM_ID);
                    String linesItemItemType = csvRecord.get(LINES_ITEM_ITEM_TYPE);
                    String linesItemUnitOfMeasure = csvRecord.get(LINES_ITEM_UNIT_OF_MEASURE);
                    String linesItemSeller = csvRecord.get(LINES_ITEM_SELLER);
                    String linesLineId = csvRecord.get(LINES_LINE_ID);
                    Double linesRequiredQty = Double.valueOf(csvRecord.get(LINES_REQUIRED_QTY));
                    String linesShipToAddressZipCode = csvRecord.get(LINES_SHIP_TO_ADDRESS_ZIPCODE);
                    String linesShipToAddressProvince = csvRecord.get(LINES_SHIP_TO_ADDRESS_PROVINCE);
                    
                    if(Boolean.FALSE.equals(basketIdSet.contains(basketId))){
                        if(Boolean.FALSE.equals(basketIdSet.isEmpty())){
                            SfccResponse sfccResponse =
                                intermediaryServiceFeign.intermediaryCalculateEdd(sfccOrder);
                            eddComputationResult = Objects.nonNull(sfccResponse) ? Boolean.TRUE : Boolean.FALSE;
                            if(eddComputationResult){
                                sfccResponseList.add(sfccResponse);
                            }
                            log.debug(String.valueOf(sfccResponse));
                            sfccOrder = null;
                        }
                        sfccOrder = SfccOrder
                                .builder()
                                .organizationCode(orgId)
                                .sessionId(sessionId)
                                .basketId(basketId)
                                .pageName(pageName)
                                .shipToAddress(ShipToAddress.builder().province(shipToAddressProvince).zipCode(shipToAddressZipCode).build())
                                .lines(List.of(SfccOrderLine.builder()
                                        .item(Item.builder()
                                                .unitOfMeasure(linesItemUnitOfMeasure)
                                                .itemId(linesItemItemId)
                                                .itemType(linesItemItemType)
                                                .seller(linesItemSeller).build())
                                                .lineId(linesLineId)
                                                .requiredQty(linesRequiredQty)
                                                .shipToAddress(ShipToAddress.builder()
                                                        .zipCode(linesShipToAddressZipCode)
                                                        .province(linesShipToAddressProvince).build())
                                        .build()))
                                .build();
                        basketIdSet.add(basketId);
                    } else if (basketId.isEmpty() && Objects.nonNull(sfccOrder)) {
                        List<SfccOrderLine> lines = sfccOrder.getLines();
                        SfccOrderLine sfccOrderLine = SfccOrderLine
                                .builder()
                                .item(Item.builder()
                                        .unitOfMeasure(linesItemUnitOfMeasure)
                                        .itemId(linesItemItemId)
                                        .itemType(linesItemItemType)
                                        .seller(linesItemSeller).build())
                                .lineId(linesLineId)
                                .requiredQty(linesRequiredQty)
                                .shipToAddress(ShipToAddress.builder()
                                        .zipCode(linesShipToAddressZipCode)
                                        .province(linesShipToAddressProvince).build())
                                .build();
                        lines.add(sfccOrderLine);
                        sfccOrder.setLines(lines);
                    }
                } catch (Exception e) {
                    if (isAllPassedForEddComputation) {
                        isAllPassedForEddComputation = false;
                    }
                    log.error("Failed to store Calendar CSV data for row number : {}", row);
                }
                if (isAllPassedForEddComputation) {
                    isAllPassedForEddComputation = eddComputationResult;
                }
                if (isAllFailedForEddComputation) {
                    isAllFailedForEddComputation = !eddComputationResult;
                }
            }
            return DataUploadUtil.storeToMap(isAllPassedForEddComputation, isAllFailedForEddComputation);
        }
    }
}
