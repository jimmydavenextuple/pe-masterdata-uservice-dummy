package com.hbc.csvdownload.service;

import static com.hbc.csvdownload.common.constants.CSVCommonConstants.CART_ID;
import static com.hbc.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BASKET_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CUTOFF_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EDD;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EDD_COMPUTATION_ORDER_AND_LINE_LIMIT_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ERROR_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ERROR_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ITEM_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_ITEM_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_REQUEST_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_LINES_UNAVAILABLE_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.HAS_EXCEPTIONS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ITEM_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_ITEM_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_SELLER;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_ITEM_UNIT_OF_MEASURE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_LINE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_MAX_AVAILABLE_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_PROMISE_DETAILS_SOURCE_NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE_FILL_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_REQUEST_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_REQUIRED_QTY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LINES_SHIP_TO_ADDRESS_ZIPCODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORGANIZATION_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PAGE_NAME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SESSION_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SFCC_LINE_LIMIT;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SFCC_ORDER_LIMIT;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_ADDRESS_ZIPCODE;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.helper.EddComputationUploadConstants;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.promise.common.domain.Item;
import com.hbc.promise.common.domain.SfccErrorResponseLine;
import com.hbc.promise.common.domain.SfccOrder;
import com.hbc.promise.common.domain.SfccOrderLine;
import com.hbc.promise.common.domain.SfccResponse;
import com.hbc.promise.common.domain.SfccSuggestedPromiseOption;
import com.hbc.promise.common.domain.ShipToAddress;
import com.hbc.promise.common.feign.IntermediaryServiceFeign;
import com.opencsv.CSVWriter;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class EddComputationService {

  private final Logger logger = LoggerFactory.getLogger(EddComputationService.class);

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${edd-computation.lines}")
  private long maxEddComputationLines;

  @Value("${edd-computation.orders}")
  private double maxEddComputationOrders;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private static int emptyColumnsForErrorResponse = 9;

  private static int emptyColumnsForValidEddResponse = 6;

  private final IntermediaryServiceFeign intermediaryServiceFeign;

  private final FileService fileService;

  private static final String NA = "NA";

  public File uploadEddCompuationData(GenericUploadRequest uploadRequest)
      throws CommonServiceException, IOException {

    String bucketName = uploadRequest.getFilePath().split("/", 2)[0];
    String filePath = uploadRequest.getFilePath().split("/", 2)[1];

    // download file from storage
    var fileResponse = fileService.getFile(bucketName, filePath);
    List<SfccResponse> sfccResponseList = csvReader(fileResponse);
    return downloadEddComputation(sfccResponseList);
  }

  private List<SfccResponse> csvReader(FileResponse fileResponse)
      throws IOException, CommonServiceException {
    try (Reader reader = new BufferedReader(new InputStreamReader(fileResponse.getInputStream()));
        var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "edd-computation",
          EddComputationUploadConstants.EDD_COMPUTATION_DATA_UPLOAD_INVALID_FILE_HEADERS);
      Iterator<CSVRecord> iterator = csvParser.iterator();
      List<SfccResponse> sfccResponseList = new ArrayList<>();
      List<SfccOrder> sfccOrders = new ArrayList<>();
      SfccOrder sfccOrder = null;
      while (iterator.hasNext()) {
        var csvRecord = iterator.next();
        long row = csvParser.getCurrentLineNumber();
        sfccOrder = createSfccOrders(sfccOrder, csvRecord, row, sfccOrders);
      }
      if (Objects.nonNull(sfccOrder)) {
        sfccOrders.add(sfccOrder);
      }
      long numberOfRows = csvParser.getRecordNumber();
      if (numberOfRows > maxEddComputationLines || sfccOrders.size() > maxEddComputationOrders) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            SFCC_ORDER_LIMIT, FieldError.builder().rejectedValue(sfccOrders.size()).build());
        errorMap.put(SFCC_LINE_LIMIT, FieldError.builder().rejectedValue(numberOfRows).build());
        throw new CommonServiceException(
            EDD_COMPUTATION_ORDER_AND_LINE_LIMIT_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
      }
      eddComputation(sfccOrders, sfccResponseList);
      return sfccResponseList;
    }
  }

  private void eddComputation(List<SfccOrder> sfccOrders, List<SfccResponse> sfccResponseList) {
    sfccOrders.forEach(sfccOrder -> getIntermediaryResponse(sfccOrder, sfccResponseList));
  }

  private SfccOrder createSfccOrders(
      SfccOrder sfccOrder, CSVRecord csvRecord, long row, List<SfccOrder> sfccOrders) {
    try {
      String basketId = csvRecord.get(BASKET_ID);
      if (basketId.isEmpty() && Objects.nonNull(sfccOrder)) {
        List<SfccOrderLine> orderLines = new ArrayList<>(sfccOrder.getLines());
        var sfccOrderLine = createSfccOrderLine(csvRecord);
        orderLines.add(sfccOrderLine);
        sfccOrder.setLines(orderLines);
      } else {
        if (Objects.nonNull(sfccOrder)) {
          sfccOrders.add(sfccOrder);
        }
        sfccOrder = null;
        sfccOrder = createSfccOrder(csvRecord);
      }
    } catch (Exception e) {
      log.error("Failed to store Calendar CSV data for row number : {}", row);
    }
    return sfccOrder;
  }

  private void getIntermediaryResponse(SfccOrder sfccOrder, List<SfccResponse> sfccResponseList) {
    var sfccResponse = intermediaryServiceFeign.intermediaryCalculateEdd(sfccOrder);
    boolean eddComputationResult = Objects.nonNull(sfccResponse) ? Boolean.TRUE : Boolean.FALSE;
    if (eddComputationResult) {
      sfccResponseList.add(sfccResponse);
    }
    log.debug(String.valueOf(sfccResponse));
  }

  private SfccOrderLine createSfccOrderLine(CSVRecord csvRecord) {

    String linesItemItemId = csvRecord.get(LINES_ITEM_ITEM_ID);
    String linesItemItemType = csvRecord.get(LINES_ITEM_ITEM_TYPE);
    String linesItemUnitOfMeasure = csvRecord.get(LINES_ITEM_UNIT_OF_MEASURE);
    String linesItemSeller = csvRecord.get(LINES_ITEM_SELLER);
    String linesLineId = csvRecord.get(LINES_LINE_ID);
    var linesRequiredQty = Double.valueOf(csvRecord.get(LINES_REQUIRED_QTY));
    String linesShipToAddressZipCode = csvRecord.get(LINES_SHIP_TO_ADDRESS_ZIPCODE);
    String linesShipToAddressProvince = csvRecord.get(LINES_SHIP_TO_ADDRESS_PROVINCE);

    return SfccOrderLine.builder()
        .item(
            Item.builder()
                .unitOfMeasure(linesItemUnitOfMeasure)
                .itemId(linesItemItemId)
                .itemType(linesItemItemType)
                .seller(linesItemSeller)
                .build())
        .lineId(linesLineId)
        .requiredQty(linesRequiredQty)
        .shipToAddress(
            ShipToAddress.builder()
                .zipCode(linesShipToAddressZipCode)
                .province(linesShipToAddressProvince)
                .build())
        .build();
  }

  private SfccOrder createSfccOrder(CSVRecord csvRecord) {
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
    var linesRequiredQty = Double.valueOf(csvRecord.get(LINES_REQUIRED_QTY));
    String linesShipToAddressZipCode = csvRecord.get(LINES_SHIP_TO_ADDRESS_ZIPCODE);
    String linesShipToAddressProvince = csvRecord.get(LINES_SHIP_TO_ADDRESS_PROVINCE);

    return SfccOrder.builder()
        .organizationCode(orgId)
        .sessionId(sessionId)
        .basketId(basketId)
        .pageName(pageName)
        .shipToAddress(
            ShipToAddress.builder()
                .province(shipToAddressProvince)
                .zipCode(shipToAddressZipCode)
                .build())
        .lines(
            List.of(
                SfccOrderLine.builder()
                    .item(
                        Item.builder()
                            .unitOfMeasure(linesItemUnitOfMeasure)
                            .itemId(linesItemItemId)
                            .itemType(linesItemItemType)
                            .seller(linesItemSeller)
                            .build())
                    .lineId(linesLineId)
                    .requiredQty(linesRequiredQty)
                    .shipToAddress(
                        ShipToAddress.builder()
                            .zipCode(linesShipToAddressZipCode)
                            .province(linesShipToAddressProvince)
                            .build())
                    .build()))
        .build();
  }

  public File downloadEddComputation(List<SfccResponse> sfccResponseList) throws IOException {
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
            SERVICE_OPTION,
            EDD,
            CUTOFF_TIME,
            LINES_ITEM_ID,
            LINES_ITEM_TYPE,
            LINES_REQUEST_QTY,
            LINES_MAX_AVAILABLE_QTY,
            LINES_PROMISE_DETAILS_SOURCE_NODE_ID,
            LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE,
            LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE_FILL_QTY,
            HAS_EXCEPTIONS,
            EXCEPTION_LINES_ITEM_ID,
            EXCEPTION_LINES_ITEM_TYPE,
            EXCEPTION_LINES_ERROR_CODE,
            EXCEPTION_LINES_ERROR_MESSAGE,
            EXCEPTION_LINES_REQUEST_QTY,
            EXCEPTION_LINES_UNAVAILABLE_QTY
          };
      writer.writeNext(header);
      writeSfccResponseToFile(writer, sfccResponseList);
      writer.flush();
    }

    return tempFile.toFile();
  }

  private void writeSfccResponseToFile(CSVWriter writer, List<SfccResponse> responseList) {
    responseList.forEach(
        response -> {
          if (Objects.nonNull(response.getSdnd())) {
            response
                .getSdnd()
                .forEach(
                    sfccSuggestedPromiseOption -> {
                      List<String> sfccSuggestedOptionResponseRow =
                          getSfccSuggestedOptionResponseRow(response);
                      sfccSuggestedOptionResponseRow.add("SDND");
                      writeSfccSuggestedPromiseOptionToCSV(
                          writer,
                          response,
                          sfccSuggestedOptionResponseRow,
                          sfccSuggestedPromiseOption);
                    });
          }
          if (Objects.nonNull(response.getStandard())) {
            response
                .getStandard()
                .forEach(
                    sfccSuggestedPromiseOption -> {
                      List<String> sfccSuggestedOptionResponseRow =
                          getSfccSuggestedOptionResponseRow(response);
                      sfccSuggestedOptionResponseRow.add("STANDARD");
                      writeSfccSuggestedPromiseOptionToCSV(
                          writer,
                          response,
                          sfccSuggestedOptionResponseRow,
                          sfccSuggestedPromiseOption);
                    });
          }
          if (Objects.nonNull(response.getExpress())) {
            response
                .getExpress()
                .forEach(
                    sfccSuggestedPromiseOption -> {
                      List<String> sfccSuggestedOptionResponseRow =
                          getSfccSuggestedOptionResponseRow(response);
                      sfccSuggestedOptionResponseRow.add("EXPRESS");
                      writeSfccSuggestedPromiseOptionToCSV(
                          writer,
                          response,
                          sfccSuggestedOptionResponseRow,
                          sfccSuggestedPromiseOption);
                    });
          }
          if (Objects.nonNull(response.getNextday())) {
            response
                .getNextday()
                .forEach(
                    sfccSuggestedPromiseOption -> {
                      List<String> sfccSuggestedOptionResponseRow =
                          getSfccSuggestedOptionResponseRow(response);
                      sfccSuggestedOptionResponseRow.add("NEXTDAY");
                      writeSfccSuggestedPromiseOptionToCSV(
                          writer,
                          response,
                          sfccSuggestedOptionResponseRow,
                          sfccSuggestedPromiseOption);
                    });
          }
          if (Boolean.TRUE.equals(response.getHasExceptions())) {
            List<String> sfccSuggestedOptionResponseRowExceptionLines =
                getSfccSuggestedOptionResponseRow(response);
            setExceptionLinesToCSV(writer, response, sfccSuggestedOptionResponseRowExceptionLines);
          }
        });
  }

  private static List<String> getSfccSuggestedOptionResponseRow(SfccResponse response) {
    List<String> sfccSuggestedOptionResponseRow = new ArrayList<>();
    sfccSuggestedOptionResponseRow.add(response.getOrgId());
    sfccSuggestedOptionResponseRow.add(response.getCartId());
    sfccSuggestedOptionResponseRow.add(response.getSessionId());
    sfccSuggestedOptionResponseRow.add(response.getPageName());
    return sfccSuggestedOptionResponseRow;
  }

  private static void setExceptionLinesToCSV(
      CSVWriter writer, SfccResponse response, List<String> sfccSuggestedOptionResponseRow) {
    if (Objects.nonNull(response.getExceptions())
        && Objects.nonNull(response.getExceptions().getSdnd())
        && Objects.nonNull(response.getExceptions().getSdnd().getLines())) {
      response
          .getExceptions()
          .getSdnd()
          .getLines()
          .forEach(
              sfccErrorResponseLine -> {
                List<String> sfccErrorResponseCSV = new ArrayList<>(sfccSuggestedOptionResponseRow);
                sfccErrorResponseCSV.add("SDND");
                writeSfccErrorResponseToCSV(writer, sfccErrorResponseLine, sfccErrorResponseCSV);
              });
    }
    if (Objects.nonNull(response.getExceptions())
        && Objects.nonNull(response.getExceptions().getExpress())
        && Objects.nonNull(response.getExceptions().getExpress().getLines())) {
      response
          .getExceptions()
          .getExpress()
          .getLines()
          .forEach(
              sfccErrorResponseLine -> {
                List<String> sfccErrorResponseCSV = new ArrayList<>(sfccSuggestedOptionResponseRow);
                sfccErrorResponseCSV.add("EXPRESS");
                writeSfccErrorResponseToCSV(writer, sfccErrorResponseLine, sfccErrorResponseCSV);
              });
    }
    if (Objects.nonNull(response.getExceptions())
        && Objects.nonNull(response.getExceptions().getStandard())
        && Objects.nonNull(response.getExceptions().getStandard().getLines())) {
      response
          .getExceptions()
          .getStandard()
          .getLines()
          .forEach(
              sfccErrorResponseLine -> {
                List<String> sfccErrorResponseCSV = new ArrayList<>(sfccSuggestedOptionResponseRow);
                sfccErrorResponseCSV.add("STANDARD");
                writeSfccErrorResponseToCSV(writer, sfccErrorResponseLine, sfccErrorResponseCSV);
              });
    }
    if (Objects.nonNull(response.getExceptions())
        && Objects.nonNull(response.getExceptions().getNextday())
        && Objects.nonNull(response.getExceptions().getNextday().getLines())) {
      response
          .getExceptions()
          .getNextday()
          .getLines()
          .forEach(
              sfccErrorResponseLine -> {
                List<String> sfccErrorResponseCSV = new ArrayList<>(sfccSuggestedOptionResponseRow);
                sfccErrorResponseCSV.add("NEXTDAY");
                writeSfccErrorResponseToCSV(writer, sfccErrorResponseLine, sfccErrorResponseCSV);
              });
    }
  }

  private static void writeSfccErrorResponseToCSV(
      CSVWriter writer,
      SfccErrorResponseLine sfccErrorResponseLine,
      List<String> sfccErrorResponseCSV) {
    for (var i = 0; i < emptyColumnsForErrorResponse; i++) {
      sfccErrorResponseCSV.add("");
    }
    sfccErrorResponseCSV.add("true");
    sfccErrorResponseCSV.add(sfccErrorResponseLine.getItemId());
    sfccErrorResponseCSV.add(sfccErrorResponseLine.getItemType());
    if (Objects.nonNull(sfccErrorResponseLine.getErrorCode())) {
      sfccErrorResponseCSV.add(sfccErrorResponseLine.getErrorCode().toString());
    } else {
      sfccErrorResponseCSV.add("");
    }
    sfccErrorResponseCSV.add(sfccErrorResponseLine.getErrorMessage());
    if (Objects.nonNull(sfccErrorResponseLine.getRequestQuantity())) {
      sfccErrorResponseCSV.add(sfccErrorResponseLine.getRequestQuantity().toString());
    } else {
      sfccErrorResponseCSV.add("");
    }
    if (Objects.nonNull(sfccErrorResponseLine.getUnavailableQuantity())) {
      sfccErrorResponseCSV.add(sfccErrorResponseLine.getUnavailableQuantity().toString());
    } else {
      sfccErrorResponseCSV.add("");
    }
    writer.writeNext(sfccErrorResponseCSV.toArray(new String[0]));
  }

  private static void writeSfccSuggestedPromiseOptionToCSV(
      CSVWriter writer,
      SfccResponse response,
      List<String> firstFourColumns,
      SfccSuggestedPromiseOption sfccSuggestedPromiseOption) {
    List<String> sfccSuggestedRow = new ArrayList<>(firstFourColumns);
    sfccSuggestedRow.add(sfccSuggestedPromiseOption.getEdd());
    sfccSuggestedRow.add(sfccSuggestedPromiseOption.getCutOffTime());
    sfccSuggestedPromiseOption
        .getLines()
        .forEach(
            sfccResponseLine -> {
              List<String> sfccSuggestedLineRow = new ArrayList<>(sfccSuggestedRow);
              sfccSuggestedLineRow.add(sfccResponseLine.getItemId());
              sfccSuggestedLineRow.add(sfccResponseLine.getItemType());
              if (Objects.nonNull(sfccResponseLine.getRequestQuantity())) {
                sfccSuggestedLineRow.add(sfccResponseLine.getRequestQuantity().toString());
              } else {
                sfccSuggestedLineRow.add("");
              }
              if (Objects.nonNull(sfccResponseLine.getMaxAvailableQuantity())) {
                sfccSuggestedLineRow.add(sfccResponseLine.getMaxAvailableQuantity().toString());
              } else {
                sfccSuggestedLineRow.add("");
              }
              sfccResponseLine
                  .getPromiseDetails()
                  .forEach(
                      sfccPromiseDetails -> {
                        List<String> sfccSuggestedPromiseDetailsRow =
                            new ArrayList<>(sfccSuggestedLineRow);
                        sfccSuggestedPromiseDetailsRow.add(sfccPromiseDetails.getSourceNodeId());
                        sfccSuggestedPromiseDetailsRow.add(sfccPromiseDetails.getSourceNodeType());
                        if (Objects.nonNull(sfccPromiseDetails.getFillQuantity())) {
                          sfccSuggestedPromiseDetailsRow.add(
                              sfccPromiseDetails.getFillQuantity().toString());
                        } else {
                          sfccSuggestedPromiseDetailsRow.add("");
                        }
                        if (Objects.nonNull(response.getHasExceptions())) {
                          sfccSuggestedPromiseDetailsRow.add(
                              response.getHasExceptions().toString());
                        } else {
                          sfccSuggestedPromiseDetailsRow.add("");
                        }
                        addEmptyLines(sfccSuggestedPromiseDetailsRow);
                        writer.writeNext(sfccSuggestedPromiseDetailsRow.toArray(new String[0]));
                      });
            });
  }

  private static void addEmptyLines(List<String> sfccSuggestedPromiseDetailsRow) {
    for (var i = 0; i < emptyColumnsForValidEddResponse; i++) {
      sfccSuggestedPromiseDetailsRow.add("");
    }
  }
}
