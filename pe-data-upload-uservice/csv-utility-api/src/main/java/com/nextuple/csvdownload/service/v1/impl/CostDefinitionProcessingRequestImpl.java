/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.COST_TYPE;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.COST_DEFINITION_DATA_UPLOAD_EMPTY_INVALID_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.COST_DEFINITION_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.COST_DEFINITION_DATA_UPLOAD_INVALID_HEADERS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.COST_DEF_COST_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.COST_DEF_ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CSV_HEADERS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CSV_VALUES;

import com.google.gson.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.BooleanUtil;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.domain.mapper.CostDefinitionRequestMapper;
import com.nextuple.csvdownload.domain.pojo.CostDefinitionErrorLogs;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.CostDefinitionService;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsConsumerClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.CostValueDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostItineraryCostFactorsDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CostDefinitionProcessingRequestImpl extends AbstractProcessingRequest {
  private final FileService fileService;
  private final CostDefinitionService costDefinitionService;
  private final JobsConsumerClient jobsConsumerClient;
  private final FileMetaDataClient fileMetaDataClient;
  private static final CostDefinitionRequestMapper INSTANCE =
      Mappers.getMapper(CostDefinitionRequestMapper.class);
  private static final String CSV_FILE_SUFFIX = ".csv";
  private static final String DYNAMIC_ROW_CONTENT = "Dynamic - incremental per pound cost bucket";
  private static final String PIPE_DELIMITER = "|";
  private static final String EMPTY_STRING = "";
  public static final String DYNAMIC_COST_FACTOR = "Dynamic cost factor";
  public static final String NOTES_HEADER = "Notes: Filled in values will be in";

  public CostDefinitionProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient,
      FileService fileService1,
      CostDefinitionService costDefinitionService,
      JobsConsumerClient jobsConsumerClient,
      FileMetaDataClient fileMetaDataClient1) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
    this.fileService = fileService1;
    this.costDefinitionService = costDefinitionService;
    this.jobsConsumerClient = jobsConsumerClient;
    this.fileMetaDataClient = fileMetaDataClient1;
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-cost-definition";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDto)
      throws IOException, CommonServiceException {
    var costDefinitionErrorLogs =
        recordStatusDto.stream().map(this::getRequestBody).collect(Collectors.toList());

    Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap =
        costDefinitionErrorLogs.stream()
            .collect(
                Collectors.toMap(
                    CostDefinitionErrorLogs::getCostFactorCombinationKey, Function.identity()));

    // download file from storage
    var fileResponse =
        getUploadedCSVFileForJobId(
            recordStatusDto.get(0).getOrgId(), recordStatusDto.get(0).getJobId());

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(DataUploadUtil.setFilePermissions());
    String fileName = "cost-definition-upload-%s".formatted(new Date().getTime());
    Path tempCostDefinitionFile = Files.createTempFile(fileName, CSV_FILE_SUFFIX, attr);

    try {
      writeStreamToTempFile(fileResponse.getInputStream(), tempCostDefinitionFile);
      int rowCount = fetchRowsCount(tempCostDefinitionFile);
      if (rowCount > 5) {
        // Grid/Table CSV formation
        formCSVDataForGridOrTable(tempCostDefinitionFile, writer, costDefinitionErrorLogsMap);
      } else {
        // Static CSV formation
        formCSVDataForStaticTable(tempCostDefinitionFile, writer, costDefinitionErrorLogsMap);
      }
    } finally {
      writer.flush();
      tempCostDefinitionFile.toFile().delete(); // NOSONAR
    }
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.COST_DEFINITION.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_COST_DEFINITION, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {
    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), COST_DEFINITION_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();
    DataUploadUtil.validateEmptyCSVForCostDefinition(
        csvFileContents, COST_DEFINITION_DATA_UPLOAD_EMPTY_INVALID_HEADERS, csvReader);
    validateOrgIdAndCostTypeFromRequestAndCSV(request, csvFileContents, csvReader);
    String[] orgIdRow = csvFileContents.get(1);
    String[] costType = csvFileContents.get(2);

    // validate static headers
    DataUploadUtil.validateCSVHeaders(
        new String[] {orgIdRow[0], costType[0]},
        getModuleType(),
        COST_DEFINITION_DATA_UPLOAD_INVALID_HEADERS,
        csvReader);

    // fetch cost config detail for org and cost type
    CostTypeValidationResponse response =
        costDefinitionService.getCostTypeValidationResponse(orgIdRow[1], costType[1]);
    String selectorCfValue =
        response.getSelectorCf().isBlank() ? EMPTY_STRING : csvFileContents.get(3)[1];
    CostItineraryCostFactorsDto costFactorsDto = getCostFactorDetails(response, selectorCfValue);

    validateSelectorCfValue(request, costFactorsDto, selectorCfValue, csvReader);

    List<String> expectedHeaders = getExpectedHeaders(response, costFactorsDto);
    List<String> actualCSVHeaders = getCSVHeaders(csvFileContents);

    validateHeaders(expectedHeaders, actualCSVHeaders, csvReader);
    validateColCfValuesForGrid(Objects.requireNonNull(costFactorsDto), csvFileContents, csvReader);
    validateCostFactorValues(
        csvFileContents, costFactorsDto, selectorCfValue, response.getSelectorCf(), csvReader);

    csvReader.close();
  }

  private void validateSelectorCfValue(
      GenericUploadRequest request,
      CostItineraryCostFactorsDto costFactorsDto,
      String selectorCfValue,
      CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (Objects.isNull(costFactorsDto)) {
      csvReader.close();
      var errorMessage = "Invalid selector cost factor value: %s".formatted(selectorCfValue);
      throwCommonServiceException(errorMessage, 0x1776, ORG_ID, request.getOrgId(), null);
    }
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_COST_DEFINITION;
  }

  private void validateCostFactorValues(
      List<String[]> csvFileContents,
      CostItineraryCostFactorsDto costFactorsDto,
      String selectorCf,
      String selectorCfValue,
      CSVReader csvReader)
      throws IOException, CommonServiceException {
    int headersToSkip = selectorCf.isBlank() && selectorCfValue.isBlank() ? 3 : 4;
    var filterCfCount = costFactorsDto.getCostFactors().size();
    Map<String, List<String>> costFactorValueMap =
        getCostFactorValueMap(costFactorsDto.getCostFactors());

    for (int csvRowIndex = 0; csvRowIndex < filterCfCount; csvRowIndex++) {
      List<String> values =
          costFactorValueMap.get(csvFileContents.get(headersToSkip + csvRowIndex)[0]);
      String csvFilterCfValue = csvFileContents.get(headersToSkip + csvRowIndex)[1];
      if (!values.contains(csvFilterCfValue)) {
        var errorMessage = "Invalid filter cost factor value: %s".formatted(csvFilterCfValue);
        csvReader.close();
        throwCommonServiceException(errorMessage, 0x1776, CSV_VALUES, csvFilterCfValue, null);
      }
    }

    int currentRowIndex = headersToSkip + filterCfCount;
    if ((currentRowIndex + 1) < csvFileContents.size()) {
      // validate dynamic cost factor boolean value
      BooleanUtil.checkValidBooleanValue(
          csvFileContents.get(currentRowIndex)[1], DYNAMIC_COST_FACTOR);

      // validate the rows count when we have Dynamic row in the input CSV
      var totalCostValueCount = csvFileContents.size() - currentRowIndex - 1;
      var dynamicValue = csvFileContents.get(currentRowIndex)[1];

      if (totalCostValueCount <= 2 && Boolean.parseBoolean(dynamicValue)) {
        csvReader.close();
        throwCommonServiceException(
            "Cost value upload row count should be greater than one when dynamic cost value is true.",
            0x1776,
            null,
            null,
            null);
      }
    }
  }

  private Map<String, List<String>> getCostFactorValueMap(
      List<CostFactorDescriptionDto> costFactors) {
    return costFactors.stream()
        .collect(
            Collectors.toMap(
                CostFactorDescriptionDto::getDisplayName, CostFactorDescriptionDto::getValues));
  }

  private void validateColCfValuesForGrid(
      CostItineraryCostFactorsDto costFactorsDto,
      List<String[]> csvFileContents,
      CSVReader csvReader)
      throws IOException, CommonServiceException {
    if (Objects.nonNull(costFactorsDto.getRow()) && Objects.nonNull(costFactorsDto.getColumn())) {
      List<String> expectedColCfValues = costFactorsDto.getColumn().getValues();
      List<String> csvColCfValues =
          Arrays.asList(getColCfValueRow(costFactorsDto, csvFileContents))
              .subList(1, getColCfValueRow(costFactorsDto, csvFileContents).length);

      if (!expectedColCfValues.equals(csvColCfValues)) {
        csvReader.close();
        throwCommonServiceException(
            "Invalid column cost factor headers.",
            0x1776,
            CSV_HEADERS,
            csvColCfValues,
            expectedColCfValues);
      }
    }
  }

  private String[] getColCfValueRow(
      CostItineraryCostFactorsDto factorsDto, List<String[]> costFactorsDto) {
    String rowColHeader = getRowColHeader(factorsDto);
    for (String[] csvColCfRow : costFactorsDto) {
      if (csvColCfRow[0].equals(rowColHeader)) return csvColCfRow;
    }

    return new String[0];
  }

  private void validateHeaders(
      List<String> expectedHeaders, List<String> actualCSVHeaders, CSVReader csvReader)
      throws IOException, CommonServiceException {
    if (!expectedHeaders.equals(actualCSVHeaders)) {
      csvReader.close();
      throwCommonServiceException(
          "Invalid CSV Headers for Cost definition upload.",
          0x2777,
          CSV_HEADERS,
          actualCSVHeaders,
          expectedHeaders);
    }
  }

  private List<String> getCSVHeaders(List<String[]> csvFileContents) {
    List<String> csvHeaders = new ArrayList<>();
    for (String[] csvRow : csvFileContents) {
      csvHeaders.add(csvRow[0]);
    }
    return csvHeaders;
  }

  private List<String> getExpectedHeaders(
      CostTypeValidationResponse response, CostItineraryCostFactorsDto costFactorsDto) {
    List<String> expectedHeaders = new ArrayList<>();
    String currencyHeader =
        "Notes: Filled in values will be in %s".formatted(response.getCurrency());
    Collections.addAll(expectedHeaders, currencyHeader, COST_DEF_ORG_ID, COST_DEF_COST_TYPE);

    // add selector cf
    if (!response.getSelectorCf().isBlank())
      expectedHeaders.add(response.getSelectorCfDisplayName());

    // add filter cfs
    if (Objects.nonNull(costFactorsDto.getCostFactors()))
      costFactorsDto
          .getCostFactors()
          .forEach(costFactor -> expectedHeaders.add(costFactor.getDisplayName()));

    // add dynamic row cfs, row-col header and row cf values
    if (Objects.nonNull(costFactorsDto.getRow()) || Objects.nonNull(costFactorsDto.getColumn())) {
      expectedHeaders.add(getDynamicRowDetails(costFactorsDto));
      expectedHeaders.add(getRowColHeader(costFactorsDto));
      expectedHeaders.addAll(costFactorsDto.getRow().getValues());
    } else {
      expectedHeaders.add(response.getDisplayName());
    }

    return expectedHeaders;
  }

  private String getRowColHeader(CostItineraryCostFactorsDto costFactorsDto) {
    if (Objects.nonNull(costFactorsDto.getRow()) && Objects.nonNull(costFactorsDto.getColumn()))
      return "%s/%s"
          .formatted(
              costFactorsDto.getRow().getDisplayName(),
              costFactorsDto.getColumn().getDisplayName());
    return costFactorsDto.getRow().getDisplayName();
  }

  private String getDynamicRowDetails(CostItineraryCostFactorsDto costFactorsDto) {
    String rowCostFactor = costFactorsDto.getRow().getDisplayName();
    List<String> rowCostFactorValues = costFactorsDto.getRow().getValues();
    return "Dynamic - incremental per pound cost bucket ( %s: %s)"
        .formatted(rowCostFactor, rowCostFactorValues.get(rowCostFactorValues.size() - 1));
  }

  private CostItineraryCostFactorsDto getCostFactorDetails(
      CostTypeValidationResponse response, String inputSelectorCfValue) {
    if (response.getSelectorCf().isBlank() && inputSelectorCfValue.isBlank())
      return getCostItineraryCostFactorsDto(
          response.getCostItinerary(),
          response.getCostFactors(),
          response.getRow(),
          response.getColumn());

    for (SelectorCfInfo selectorCf : response.getSelectorCfInfo()) {

      if (selectorCf.getSelectorCfValue().equals(inputSelectorCfValue))
        return getCostItineraryCostFactorsDto(
            selectorCf.getCostItinerary(),
            selectorCf.getCostFactors(),
            selectorCf.getRow(),
            selectorCf.getColumn());
    }
    return null;
  }

  private CostItineraryCostFactorsDto getCostItineraryCostFactorsDto(
      String costItinerary,
      List<CostFactorDescriptionDto> costFactors,
      CostFactorDescriptionDto row,
      CostFactorDescriptionDto column) {
    return CostItineraryCostFactorsDto.builder()
        .costItinerary(costItinerary)
        .costFactors(costFactors)
        .row(row)
        .column(column)
        .build();
  }

  private void validateOrgIdAndCostTypeFromRequestAndCSV(
      GenericUploadRequest request, List<String[]> csvFileContents, CSVReader csvReader)
      throws CommonServiceException, IOException {
    validateNotesHeader(csvFileContents, csvReader);
    validateOrgId(request, csvFileContents, csvReader);
    validateCostType(request, csvFileContents, csvReader);
  }

  private void validateNotesHeader(List<String[]> csvFileContents, CSVReader csvReader)
      throws IOException, CommonServiceException {
    String notesHeader = csvFileContents.get(0)[0];
    if (!(notesHeader.contains(NOTES_HEADER))) {
      csvReader.close();
      throw new CommonServiceException(
          "Invalid Notes headers",
          HttpStatus.BAD_REQUEST,
          0x1772,
          Map.of("notesHeader", FieldError.builder().rejectedValue(notesHeader).build()));
    }
  }

  private void validateOrgId(
      GenericUploadRequest request, List<String[]> csvFileContents, CSVReader csvReader)
      throws IOException, CommonServiceException {
    // validate orgId in the request
    if (!StringUtils.hasLength(request.getOrgId())) {
      csvReader.close();
      throwCommonServiceException(
          "OrgId value provided in request is empty or null.",
          0x1776,
          ORG_ID,
          request.getOrgId(),
          null);
    }

    // compare orgId value in request and CSV
    String requestCostTypeValue = request.getOrgId();
    String csvCostTypeValue = csvFileContents.get(1)[1];
    if (!requestCostTypeValue.equals(csvCostTypeValue)) {
      csvReader.close();
      String errorMessage =
          "OrgId value provided in the CSV does not match with request org %s."
              .formatted(requestCostTypeValue);
      throwCommonServiceException(
          errorMessage, 0x1776, ORG_ID, csvCostTypeValue, requestCostTypeValue);
    }
  }

  private void validateCostType(
      GenericUploadRequest request, List<String[]> csvFileContents, CSVReader csvReader)
      throws IOException, CommonServiceException {
    // validate cost type in the request
    Map<String, String> additionalReferenceMap = request.getAdditionalReference();
    if (Objects.isNull(additionalReferenceMap)
        || !StringUtils.hasLength(additionalReferenceMap.get(COST_TYPE))) {
      csvReader.close();
      throwCommonServiceException(
          "Cost type value provided in request's additionalReference is empty or null.",
          0x1776,
          COST_TYPE,
          request.getOrgId(),
          null);
    }

    // compare cost type value in request and CSV
    String requestCostTypeValue = additionalReferenceMap.get(COST_TYPE);
    String csvCostTypeValue = csvFileContents.get(2)[1];
    if (!requestCostTypeValue.equals(csvCostTypeValue)) {
      csvReader.close();
      String errorMessage =
          "Cost type value provided in the CSV does not match with request cost type %s."
              .formatted(requestCostTypeValue);
      throwCommonServiceException(
          errorMessage, 0x1776, COST_TYPE, csvCostTypeValue, requestCostTypeValue);
    }
  }

  private FileResponse getUploadedCSVFileForJobId(String orgId, String jobId)
      throws CommonServiceException {
    // get the fileMetaDataId from jobId
    JobDto jobDto = jobsConsumerClient.getJob(orgId, jobId).getPayload();

    // get bucket name and file path to fetch the file
    FileMetaDataResponse fileMetaDataResponse =
        fileMetaDataClient.findFileMetadataById(jobDto.getFileMetaDataId()).getPayload();

    String bucketName = fileMetaDataResponse.getPath().split("/", 2)[0];
    String filePath = fileMetaDataResponse.getPath().split("/", 2)[1];

    return fileService.getFile(bucketName, filePath);
  }

  private void formCSVDataForStaticTable(
      Path tempCostDefinitionFile,
      CSVWriter writer,
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap) {

    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      List<String[]> csvContents = csvReader.readAll();
      // write headers
      writer.writeNext(csvContents.remove(0));
      writer.writeNext(csvContents.remove(0));
      writer.writeNext(csvContents.remove(0));
      if (csvContents.size() == 2) {
        // with selector cost factor
        String[] selectorCf = csvContents.remove(0);
        writer.writeNext(selectorCf);
        writer.writeNext(
            new String[] {
              csvContents.get(0)[0],
              getErrorMessageForKey(costDefinitionErrorLogsMap, selectorCf[1])
            });
      } else {
        // without selector cost factor
        writer.writeNext(
            new String[] {
              csvContents.get(0)[0], getErrorMessageForKey(costDefinitionErrorLogsMap, EMPTY_STRING)
            });
      }

    } catch (Exception e) {
      log.error("Error in forming Static Table CSV error logs data", e);
    }
  }

  private void formCSVDataForGridOrTable(
      Path tempCostDefinitionFile,
      CSVWriter writer,
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap) {

    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      List<String> costFactors = new ArrayList<>();

      writeHeaders(csvReader, costFactors, writer);
      writeErrorData(csvReader, costFactors, writer, costDefinitionErrorLogsMap);
    } catch (Exception e) {
      log.error("Error in writing the data to error logs CSV", e);
    }
  }

  private void writeErrorData(
      CSVReader csvReader,
      List<String> costFactors,
      CSVWriter writer,
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap)
      throws CsvValidationException, IOException {
    String[] row;
    List<String> colCfValues = new ArrayList<>();
    String costFactorPrefixKey = String.join(PIPE_DELIMITER, costFactors);

    // write the grid/table header
    var headerRow = csvReader.readNext();
    writer.writeNext(headerRow);
    if (!headerRow[1].isEmpty()) {
      colCfValues = Arrays.asList(headerRow).subList(1, headerRow.length);
    }

    // write errors messages
    while ((row = csvReader.readNext()) != null) {
      if (colCfValues.isEmpty()) {
        constructRowsForTable(costFactorPrefixKey, row, writer, costDefinitionErrorLogsMap);
      } else {
        constructRowsForGrid(
            costFactorPrefixKey, colCfValues, row, costDefinitionErrorLogsMap, writer);
      }
    }
  }

  private void constructRowsForGrid(
      String costFactorPrefixKey,
      List<String> colCfValues,
      String[] row,
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap,
      CSVWriter writer) {
    List<String> errorRow = new ArrayList<>();
    errorRow.add(row[0]);
    colCfValues.forEach(
        colCf -> {
          var cosFactorCombinationKey =
              String.join(PIPE_DELIMITER, costFactorPrefixKey, colCf, row[0]);
          var errorMessage =
              getErrorMessageForKey(costDefinitionErrorLogsMap, cosFactorCombinationKey);
          errorRow.add(errorMessage);
        });
    writer.writeNext(errorRow.toArray(new String[0]));
  }

  private void constructRowsForTable(
      String costFactorPrefixKey,
      String[] row,
      CSVWriter writer,
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap) {
    String cosFactorCombinationKey = String.join(PIPE_DELIMITER, costFactorPrefixKey, row[0]);
    String errorMessage =
        getErrorMessageForKey(costDefinitionErrorLogsMap, cosFactorCombinationKey);

    writer.writeNext(new String[] {row[0], errorMessage});
  }

  private void writeHeaders(CSVReader csvReader, List<String> costFactors, CSVWriter writer)
      throws CsvValidationException, IOException {
    String[] row;
    while (!(row = csvReader.readNext())[0].contains(DYNAMIC_ROW_CONTENT)) {
      writer.writeNext(row);
      if (csvReader.getLinesRead() > 3) costFactors.add(row[1]);
    }
    // write the dynamic row
    writer.writeNext(row);
  }

  private String getErrorMessageForKey(
      Map<String, CostDefinitionErrorLogs> costDefinitionErrorLogsMap,
      String cosFactorCombinationKey) {
    CostDefinitionErrorLogs costDefinitionErrorLogs =
        costDefinitionErrorLogsMap.get(cosFactorCombinationKey);
    if (Objects.nonNull(costDefinitionErrorLogs)) {
      return errorMessage(costDefinitionErrorLogs);
    }
    return null;
  }

  private int fetchRowsCount(Path tempCostDefinitionFile) {
    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      var totalRecords = 0;
      while (Objects.nonNull(csvReader.readNext())) {
        totalRecords++;
      }
      return totalRecords;
    } catch (Exception e) {
      log.error("Error in fetching total rows in CSV", e);
      return 0;
    }
  }

  private CostDefinitionErrorLogs getRequestBody(RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var errorLogsPojo =
        INSTANCE.convertToCostDefinitionErrorLogs(
            gson.fromJson(recordStatusDto.getRequestBody(), CostValueDataUpload.class));
    errorLogsPojo.setErrorMessage(recordStatusDto.getErrorMessage());
    errorLogsPojo.setException(recordStatusDto.getException());
    return errorLogsPojo;
  }

  private void writeStreamToTempFile(InputStream inputStream, Path tempCostDefinitionFile) {
    try (OutputStream output = new FileOutputStream(tempCostDefinitionFile.toFile(), false)) {
      inputStream.transferTo(output);
    } catch (Exception e) {
      log.error("Error in writing the input stream to temp cost definition file", e);
    }
  }

  private String errorMessage(CostDefinitionErrorLogs costDefinitionErrorLogs) {
    if (ObjectUtils.isEmpty(costDefinitionErrorLogs.getErrorMessage())) {
      log.error("Empty error message received. Defaulting to internal server error message");
      return "Internal Server Error";
    } else if (("feign.RetryableException").equals(costDefinitionErrorLogs.getException())) {
      return String.valueOf(costDefinitionErrorLogs.getCostValue());
    } else {
      return costDefinitionErrorLogs.getErrorMessage();
    }
  }

  private void throwCommonServiceException(
      String errorMessage,
      Integer errorCode,
      String field,
      Object rejectedFieldValue,
      Object actualFieldValue)
      throws CommonServiceException {
    log.error(errorMessage);
    Map<String, FieldError> errorMap = null;
    if (StringUtils.hasLength(field))
      errorMap =
          Map.of(
              field,
              FieldError.builder()
                  .rejectedValue(rejectedFieldValue)
                  .actualValue(actualFieldValue)
                  .build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
  }
}
