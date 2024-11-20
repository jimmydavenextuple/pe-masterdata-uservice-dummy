/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.CART_ID;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTIONS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CUTOFF_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EDD_COMPUTATION_ORDER_AND_LINE_LIMIT_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.END_ESTIMATED_DELIVERY_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.END_ESTIMATED_SHIP_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ERROR_CODE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ERROR_MESSAGE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_LINE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.HAS_EXCEPTIONS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ITEM_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_PRODUCT_CLASS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_UNIT_OF_MEASURE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_LINE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_ORDERED_QUANTITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_REQUIRED_QTY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SERVICE_OPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_COUNTRY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_REGION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_STATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_TIMEZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_ZIPCODE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SOURCENODES_FULLFILLED_QUANTITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SOURCENODES_SOURCE_NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LINES_SOURCENODES_SOURCE_NODE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORDER_LIMIT;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORDER_LINE_LIMIT;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.PAGE_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ROW_NUMBER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SESSION_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_COUNTRY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_REGION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_STATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_TIMEZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_ZIPCODE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.helper.EddComputationUploadConstants;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.nextuple.promise.domain.CoreEngineResponse;
import com.nextuple.promise.domain.OrderLinesSourcing;
import com.nextuple.promise.domain.OrderRequest;
import com.nextuple.promise.domain.OrderSolution;
import com.nextuple.promise.domain.ShipDestinationDetails;
import com.nextuple.promise.domain.SourceNodeDetails;
import com.nextuple.promise.domain.SourcingError;
import com.nextuple.promise.domain.SourcingItem;
import com.nextuple.promise.domain.SourcingLineException;
import com.nextuple.promise.domain.SourcingOrderLines;
import com.nextuple.promise.feign.PromisingFeign;
import com.opencsv.CSVWriter;
import feign.FeignException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class EddComputationService {

  private final Logger logger = LoggerFactory.getLogger(EddComputationService.class);

  @Value("${edd-computation.lines}")
  private long maxEddComputationLines;

  @Value("${edd-computation.orders}")
  private double maxEddComputationOrders;

  private final TenantDatabaseConfig tenantDatabaseConfig;
  public static final String ERROR_IN_DOING_EDD_COMPUTATION = "Error in doing EDD computation";
  private static int emptyColumnsForErrorResponse = 9;

  private static int emptyColumnsForValidEddResponse = 3;

  private final PromisingFeign promisingFeign;

  private final FileService fileService;

  private final DynamicCsvHeadersValidation dynamicCsvHeadersValidation;

  public File uploadEddCompuationData(GenericUploadRequest uploadRequest)
      throws CommonServiceException, IOException {
    String bucketName = uploadRequest.getFilePath().split("/", 2)[0];
    String filePath = uploadRequest.getFilePath().split("/", 2)[1];

    // download file from storage
    var fileResponse = fileService.getFile(bucketName, filePath);
    Map<String, String> customAttributes =
        tenantDatabaseConfig.getCurrentTenantCustomAttributes(uploadRequest.getOrgId());
    Map<String, String> lineCustomAttributes =
        tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(uploadRequest.getOrgId());
    List<CoreEngineResponse> coreEngineResponseList =
        processCsv(fileResponse, customAttributes, lineCustomAttributes);
    return downloadEddComputation(coreEngineResponseList);
  }

  private List<CoreEngineResponse> processCsv(
      FileResponse fileResponse,
      Map<String, String> customAttributes,
      Map<String, String> lineCustomAttributes)
      throws IOException, CommonServiceException {
    try (Reader reader = new BufferedReader(new InputStreamReader(fileResponse.getInputStream()));
        var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
      dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
          csvParser,
          "edd-computation",
          EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_HEADERS,
          customAttributes,
          lineCustomAttributes);
      Iterator<CSVRecord> iterator = csvParser.iterator();
      Map<String, OrderRequest> orderRequestMap = new HashMap<>();
      List<OrderRequest> orderRequestList = new ArrayList<>();
      while (iterator.hasNext()) {
        var csvRecord = iterator.next();
        long row = csvParser.getCurrentLineNumber();
        convertCsvToOrders(orderRequestMap, csvRecord, row, customAttributes, lineCustomAttributes);
      }
      orderRequestList = new ArrayList<>(orderRequestMap.values());
      long numberOfRows = csvParser.getRecordNumber();
      if (orderRequestList.stream()
              .anyMatch(order -> order.getOrderLines().size() > maxEddComputationLines)
          || orderRequestList.size() > maxEddComputationOrders) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            ORDER_LIMIT, FieldError.builder().rejectedValue(orderRequestList.size()).build());
        errorMap.put(ORDER_LINE_LIMIT, FieldError.builder().rejectedValue(numberOfRows).build());
        throw new CommonServiceException(
            EDD_COMPUTATION_ORDER_AND_LINE_LIMIT_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
      return eddComputation(orderRequestList);
    }
  }

  private void convertCsvToOrders(
      Map<String, OrderRequest> orderRequestMap,
      CSVRecord csvRecord,
      long row,
      Map<String, String> customAttributes,
      Map<String, String> lineCustomAttributes)
      throws CommonServiceException {
    String cartId = csvRecord.get(CART_ID);
    if (cartId.isBlank()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ROW_NUMBER, FieldError.builder().rejectedValue(row).build());
      errorMap.put(CART_ID, FieldError.builder().rejectedValue(cartId).build());
      throw new CommonServiceException(
          "Cart id is blank at row: " + row, HttpStatus.BAD_REQUEST, 0x1773, errorMap);
    }
    try {
      OrderRequest orderRequest;
      if (orderRequestMap.containsKey(cartId)) {
        orderRequest = orderRequestMap.get(cartId);
        if (!orderRequest.getServiceOptions().contains(csvRecord.get(SERVICE_OPTIONS))) {
          orderRequest.setServiceOptions(
              orderRequest.getServiceOptions() + "," + csvRecord.get(SERVICE_OPTIONS));
        }
        List<SourcingOrderLines> newOrderLines = new ArrayList<>(orderRequest.getOrderLines());
        if (newOrderLines.stream()
            .noneMatch(line -> line.getLineId().equals(csvRecord.get(LINES_LINE_ID)))) {
          SourcingOrderLines newOrderLine = createOrderRequestLine(csvRecord, lineCustomAttributes);
          newOrderLines.add(newOrderLine);
          orderRequest.setOrderLines(newOrderLines);
        }
      } else {
        orderRequest = createOrderRequest(csvRecord, customAttributes, lineCustomAttributes);
      }
      orderRequestMap.put(cartId, orderRequest);
    } catch (Exception e) {
      log.error("Failed to convert CSV record to OrderRequest for row number: {}", row);
    }
  }

  private List<CoreEngineResponse> eddComputation(List<OrderRequest> orderRequestList)
      throws CommonServiceException {
    List<CoreEngineResponse> coreEngineResponseList = new ArrayList<>();
    for (OrderRequest orderRequest : orderRequestList) {
      try {
        coreEngineResponseList.add(promisingFeign.promiseEdd(orderRequest));
      } catch (FeignException e) {
        logger.error("Feign exception while doing EDD computation {}", e);
        throw new CommonServiceException(
            handleFeignException(e).getMessage(), HttpStatus.BAD_REQUEST, 0xfffff4, null);
      }
    }
    return coreEngineResponseList;
  }

  private ErrorResponse handleFeignException(FeignException e) {
    var errorResponse = ExceptionUtils.parseFeignException(e);
    if (errorResponse.getPayload() != null
        && !CollectionUtils.isEmpty(errorResponse.getPayload().getFields())) {
      Map<String, FieldError> errorFieldsMap = errorResponse.getPayload().getFields();
      String errorMessage =
          errorFieldsMap.keySet().stream()
              .filter(key -> !ObjectUtils.isEmpty(errorFieldsMap.get(key).getErrorMessage()))
              .map(key -> errorFieldsMap.get(key).getErrorMessage())
              .findFirst()
              .orElse("");
      errorResponse.setMessage(errorMessage);
    }
    if (errorResponse.getMessage().contains("FeignException"))
      errorResponse.setMessage(extractFeignExceptionMessage(errorResponse.getMessage()));

    return errorResponse;
  }

  private String extractFeignExceptionMessage(String error) {
    var errorMessage = "\"message\":\"";
    if (!error.contains(errorMessage)) return ERROR_IN_DOING_EDD_COMPUTATION;
    int from = error.lastIndexOf(errorMessage) + errorMessage.length();
    int to = error.indexOf('\"', from);
    return error.substring(from, to);
  }

  private SourcingOrderLines createOrderRequestLine(
      CSVRecord csvRecord, Map<String, String> lineCustomAttributes) {
    String linesItemItemId = csvRecord.get(LINES_ITEM_ITEM_ID);
    String linesItemUnitOfMeasure = csvRecord.get(LINES_ITEM_UNIT_OF_MEASURE);
    String linesLineId = csvRecord.get(LINES_LINE_ID);
    var linesRequiredQty = Double.valueOf(csvRecord.get(LINES_REQUIRED_QTY));
    String linesShipToAddressZipCode = csvRecord.get(LINES_SHIP_TO_ADDRESS_ZIPCODE);
    String linesShipToAddressRegion = csvRecord.get(LINES_SHIP_TO_ADDRESS_REGION);
    String linesShipToAddressState = csvRecord.get(LINES_SHIP_TO_ADDRESS_STATE);
    String linesShipToAddressCountry = csvRecord.get(LINES_SHIP_TO_ADDRESS_COUNTRY);
    String linesShipToAddressTimeZone = csvRecord.get(LINES_SHIP_TO_ADDRESS_TIMEZONE);
    String linesServiceOption = csvRecord.get(LINES_SERVICE_OPTION);
    String productClass = csvRecord.get(LINES_ITEM_PRODUCT_CLASS);

    return SourcingOrderLines.builder()
        .item(
            SourcingItem.builder()
                .unitOfMeasure(linesItemUnitOfMeasure)
                .itemId(linesItemItemId)
                .productClass(productClass)
                .build())
        .lineId(linesLineId)
        .serviceOption(linesServiceOption)
        .customAttributes(makeLineCustomAttributesToOrder(csvRecord, lineCustomAttributes))
        .orderedQuantity(linesRequiredQty)
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode(linesShipToAddressZipCode)
                .region(linesShipToAddressRegion)
                .state(linesShipToAddressState)
                .country(linesShipToAddressCountry)
                .timezone(linesShipToAddressTimeZone)
                .build())
        .build();
  }

  private OrderRequest createOrderRequest(
      CSVRecord csvRecord,
      Map<String, String> customAttributes,
      Map<String, String> lineCustomAttributes) {
    String orgId = csvRecord.get(ORG_ID);
    String sessionId = csvRecord.get(SESSION_ID);
    String cartId = csvRecord.get(CART_ID);
    String pageName = csvRecord.get(PAGE_NAME);
    String serviceOptions = csvRecord.get(SERVICE_OPTIONS);
    String shipToAddressZipCode = csvRecord.get(SHIP_TO_ADDRESS_ZIPCODE);
    String shipToAddressRegion = csvRecord.get(SHIP_TO_ADDRESS_REGION);
    String shipToAddressState = csvRecord.get(SHIP_TO_ADDRESS_STATE);
    String shipToAddressCountry = csvRecord.get(SHIP_TO_ADDRESS_COUNTRY);
    String shipToAddressTimeZone = csvRecord.get(SHIP_TO_ADDRESS_TIMEZONE);
    String linesServiceOption = csvRecord.get(LINES_SERVICE_OPTION);
    String productClass = csvRecord.get(LINES_ITEM_PRODUCT_CLASS);
    String linesItemItemId = csvRecord.get(LINES_ITEM_ITEM_ID);
    String linesItemUnitOfMeasure = csvRecord.get(LINES_ITEM_UNIT_OF_MEASURE);
    String linesLineId = csvRecord.get(LINES_LINE_ID);
    var linesRequiredQty = Double.valueOf(csvRecord.get(LINES_REQUIRED_QTY));
    String linesShipToAddressZipCode = csvRecord.get(LINES_SHIP_TO_ADDRESS_ZIPCODE);
    String linesShipToAddressRegion = csvRecord.get(LINES_SHIP_TO_ADDRESS_REGION);
    String linesShipToAddressState = csvRecord.get(LINES_SHIP_TO_ADDRESS_STATE);
    String linesShipToAddressCountry = csvRecord.get(LINES_SHIP_TO_ADDRESS_COUNTRY);
    String linesShipToAddressTimeZone = csvRecord.get(LINES_SHIP_TO_ADDRESS_TIMEZONE);

    OrderRequest orderRequest =
        OrderRequest.builder()
            .orgId(orgId)
            .serviceOptions(serviceOptions)
            .sessionId(sessionId)
            .cartId(cartId)
            .pageName(pageName)
            .shipDestinationDetails(
                ShipDestinationDetails.builder()
                    .zipCode(shipToAddressZipCode)
                    .region(shipToAddressRegion)
                    .state(shipToAddressState)
                    .country(shipToAddressCountry)
                    .timezone(shipToAddressTimeZone)
                    .build())
            .orderLines(
                List.of(
                    SourcingOrderLines.builder()
                        .item(
                            SourcingItem.builder()
                                .unitOfMeasure(linesItemUnitOfMeasure)
                                .productClass(productClass)
                                .itemId(linesItemItemId)
                                .build())
                        .lineId(linesLineId)
                        .serviceOption(linesServiceOption)
                        .customAttributes(
                            makeLineCustomAttributesToOrder(csvRecord, lineCustomAttributes))
                        .orderedQuantity(linesRequiredQty)
                        .shipDestinationDetails(
                            ShipDestinationDetails.builder()
                                .zipCode(linesShipToAddressZipCode)
                                .region(linesShipToAddressRegion)
                                .state(linesShipToAddressState)
                                .country(linesShipToAddressCountry)
                                .timezone(linesShipToAddressTimeZone)
                                .build())
                        .build()))
            .build();
    appendCustomAttributesToOrder(csvRecord, orderRequest, customAttributes);
    return orderRequest;
  }

  private void appendCustomAttributesToOrder(
      CSVRecord csvRecord, OrderRequest orderRequest, Map<String, String> customAttributes) {
    Map<String, Object> customAttr = new HashMap<>();
    customAttributes.forEach(
        (key, value) ->
            customAttr.put(
                DynamicCsvHeadersValidation.CUSTOM_ATTRIBUTES_HEADER_PREFIX + key,
                csvRecord.get(DynamicCsvHeadersValidation.CUSTOM_ATTRIBUTES_HEADER_PREFIX + key)));
    orderRequest.setCustomAttributes(customAttr);
  }

  private Map<String, Object> makeLineCustomAttributesToOrder(
      CSVRecord csvRecord, Map<String, String> customAttributes) {
    Map<String, Object> customAttr = new HashMap<>();
    customAttributes.forEach(
        (key, value) ->
            customAttr.put(
                DynamicCsvHeadersValidation.LINES_CUSTOM_ATTRIBUTES_HEADER_PREFIX + key,
                csvRecord.get(
                    DynamicCsvHeadersValidation.LINES_CUSTOM_ATTRIBUTES_HEADER_PREFIX + key)));
    return customAttr;
  }

  public File downloadEddComputation(List<CoreEngineResponse> coreEngineResponseList)
      throws IOException {
    logger.debug("Processing download processing time buffers for orgId");

    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(posixFilePermissions);
    Path tempFile =
        Files.createTempFile("download-edd-computation" + new Date().getTime(), ".csv", attr);
    try (var writer = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var header =
          new String[] {
            ORG_ID,
            CART_ID,
            SESSION_ID,
            PAGE_NAME,
            SERVICE_OPTIONS,
            END_ESTIMATED_DELIVERY_DATE,
            END_ESTIMATED_SHIP_DATE,
            CUTOFF_TIME,
            LINES_LINE_ID,
            LINES_ITEM_ID,
            LINES_ORDERED_QUANTITY,
            LINES_SOURCENODES_SOURCE_NODE_ID,
            LINES_SOURCENODES_SOURCE_NODE_TYPE,
            LINES_SOURCENODES_FULLFILLED_QUANTITY,
            SHIP_TO_ADDRESS_ZIPCODE,
            SHIP_TO_ADDRESS_REGION,
            SHIP_TO_ADDRESS_STATE,
            SHIP_TO_ADDRESS_COUNTRY,
            SHIP_TO_ADDRESS_TIMEZONE,
            HAS_EXCEPTIONS,
            EXCEPTION_LINES_LINE_ID,
            EXCEPTION_LINES_ERROR_CODE,
            EXCEPTION_LINES_ERROR_MESSAGE
          };
      writer.writeNext(header);
      writeCoreEngineResponseToFile(writer, coreEngineResponseList);

      writer.flush();
    }

    return tempFile.toFile();
  }

  private void writeCoreEngineResponseToFile(
      CSVWriter writer, List<CoreEngineResponse> coreEngineResponseList) {

    coreEngineResponseList.forEach(
        response -> {
          writeCoreEngineResponseToCSV(writer, response);
          writeExceptionToCSV(writer, response);
        });
  }

  private void writeCoreEngineResponseToCSV(CSVWriter writer, CoreEngineResponse response) {
    Map<String, OrderSolution> orderLineSourcingMap = response.getOrderLinesSourcingMap();
    orderLineSourcingMap.forEach(
        (key, value) -> {
          for (OrderLinesSourcing orderLinesSourcing : value.getOrderLinesSourcing()) {
            for (SourceNodeDetails sourceNodeDetails : orderLinesSourcing.getSourceNodes()) {
              if (Objects.nonNull(sourceNodeDetails.getError())) {
                continue;
              }
              List<String> rowData = new ArrayList<>();
              writeCommonHeadersToCsv(response, rowData);
              rowData.add(key);
              rowData.add(orderLinesSourcing.getEndEstimatedDeliveryDate());
              rowData.add(orderLinesSourcing.getEndEstimatedShipDate());
              rowData.add(orderLinesSourcing.getCutOffTime());
              rowData.add(orderLinesSourcing.getLineId());
              rowData.add(orderLinesSourcing.getItem().getItemId());
              rowData.add(String.valueOf(orderLinesSourcing.getOrderedQuantity()));
              rowData.add(sourceNodeDetails.getSourceNodeId());
              rowData.add(sourceNodeDetails.getSourceNodeType());
              rowData.add(String.valueOf(sourceNodeDetails.getFulfilledQuantity()));
              ShipDestinationDetails shipDestinationDetails = response.getShipDestinationDetails();
              writeShipDestinationDetailsToCsv(shipDestinationDetails, rowData);
              rowData.add(String.valueOf(response.getHasErrors()));
              for (int i = 0; i < emptyColumnsForValidEddResponse; i++) {
                rowData.add("");
              }
              String[] strRowData = convertArrayListToStringArray(rowData);
              writer.writeNext(strRowData);
            }
          }
        });
  }

  private void writeExceptionToCSV(CSVWriter writer, CoreEngineResponse response) {
    Map<String, List<SourcingLineException>> exceptionMap = response.getExceptionsList();
    exceptionMap.forEach(
        (key, value) -> {
          for (SourcingLineException sourcingLineException : value) {
            for (SourcingError sourcingError : sourcingLineException.getErrors()) {
              List<String> rowData = new ArrayList<>();
              writeCommonHeadersToCsv(response, rowData);
              rowData.add(key);
              for (int i = 0; i < emptyColumnsForErrorResponse; i++) {
                rowData.add("");
              }
              ShipDestinationDetails shipDestinationDetails = response.getShipDestinationDetails();
              writeShipDestinationDetailsToCsv(shipDestinationDetails, rowData);
              rowData.add(String.valueOf(response.getHasErrors()));
              rowData.add(
                  Objects.nonNull(sourcingLineException.getLineId())
                      ? sourcingLineException.getLineId()
                      : "");
              rowData.add(String.valueOf(sourcingError.getCode()));
              rowData.add(String.valueOf(sourcingError.getMessage()));
              String[] strRowData = convertArrayListToStringArray(rowData);
              writer.writeNext(strRowData);
            }
          }
        });
  }

  private void writeCommonHeadersToCsv(CoreEngineResponse response, List<String> rowData) {
    rowData.add(response.getOrgId());
    rowData.add(response.getCartId());
    rowData.add(Objects.nonNull(response.getSessionId()) ? response.getSessionId() : "");
    rowData.add(Objects.nonNull(response.getPageName()) ? response.getPageName() : "");
  }

  private void writeShipDestinationDetailsToCsv(
      ShipDestinationDetails shipDestinationDetails, List<String> rowData) {
    rowData.add(shipDestinationDetails.getZipCode());
    rowData.add(
        Objects.nonNull(shipDestinationDetails.getRegion())
            ? shipDestinationDetails.getRegion()
            : "");
    rowData.add(
        Objects.nonNull(shipDestinationDetails.getState())
            ? shipDestinationDetails.getState()
            : "");
    rowData.add(
        Objects.nonNull(shipDestinationDetails.getCountry())
            ? shipDestinationDetails.getCountry()
            : "");
    rowData.add(
        Objects.nonNull(shipDestinationDetails.getTimezone())
            ? shipDestinationDetails.getTimezone()
            : "");
  }

  private String[] convertArrayListToStringArray(List<String> rowData) {
    String[] strRowData = new String[rowData.size()];
    for (int i = 0; i < rowData.size(); i++) {
      strRowData[i] = rowData.get(i);
    }
    return strRowData;
  }
}
