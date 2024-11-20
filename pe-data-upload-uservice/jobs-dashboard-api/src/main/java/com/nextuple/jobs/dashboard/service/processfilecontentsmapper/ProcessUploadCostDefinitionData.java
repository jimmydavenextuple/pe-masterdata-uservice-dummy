/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CostValueDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostItineraryCostFactorsDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.feign.CostConfigDashboardFeign;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessUploadCostDefinitionData implements ProcessFileContents {

  private static final String CSV_FILE_SUFFIX = ".csv";
  private static final String DYNAMIC_ROW_CONTENT = "Dynamic - incremental per pound cost bucket";
  private static final String PIPE_DELIMITER = "|";
  private static final String EMPTY_STRING = "";

  private final CostConfigDashboardFeign costConfigDashboardFeign;

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_COST_DEFINITION;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobDto jobDto, InputStream inputStream)
      throws IOException, CsvException, CommonServiceException {
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(DataUploadUtil.setFilePermissions());
    String fileName = "cost-definition-upload-%s".formatted(new Date().getTime());
    Path tempCostDefinitionFile = Files.createTempFile(fileName, CSV_FILE_SUFFIX, attr);
    try {
      writeStreamToTempFile(inputStream, tempCostDefinitionFile);
      return new ArrayList<>(createCostDefinitionJobRequest(tempCostDefinitionFile));
    } finally {
      tempCostDefinitionFile.toFile().delete(); // NOSONAR
    }
  }

  private void writeStreamToTempFile(InputStream inputStream, Path tempCostDefinitionFile) {
    try (OutputStream output = new FileOutputStream(tempCostDefinitionFile.toFile(), false)) {
      inputStream.transferTo(output);
    } catch (Exception e) {
      log.error("Error in writing the input stream to temp cost definition file", e);
    }
  }

  private List<CostValueDataUpload> createCostDefinitionJobRequest(Path tempCostDefinitionFile) {
    String[] dynamicRow = getDynamicRow(tempCostDefinitionFile);
    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      csvReader.skip(1);
      // extract orgId  value
      var orgId = csvReader.readNext()[1];

      // extract costType and fetch its details
      var costType = csvReader.readNext()[1];
      var costTypeDetails =
          costConfigDashboardFeign.getCostTypesForValidation(orgId, costType).getPayload();

      // get cost factor combination key prefix
      var costFactorPrefix =
          getCostFactorPrefix(tempCostDefinitionFile, costTypeDetails.getSelectorCf(), dynamicRow);
      var selectorCf = costTypeDetails.getSelectorCf();
      var selectorCfValue = selectorCf.isEmpty() ? "" : csvReader.readNext()[1];

      Map<String, CostItineraryCostFactorsDto> costFactorsMap = getCostFactorsMap(costTypeDetails);
      CostItineraryCostFactorsDto costFactorsDto = costFactorsMap.get(selectorCfValue);

      if (dynamicRow.length > 0) {
        // Grid/Table data upload
        return getCostDefinitionJobRequestForGridOrTableUpload(
            orgId, costFactorsDto, dynamicRow, csvReader, costFactorPrefix);
      } else {
        // Static data upload
        return getCostDefinitionJobRequestForStaticTableUpload(
            orgId, costFactorsDto, csvReader, costFactorPrefix, selectorCf);
      }
    } catch (Exception e) {
      log.error("Error in creating cost definition request object.", e);
      return new ArrayList<>();
    }
  }

  private List<CostValueDataUpload> getCostDefinitionJobRequestForStaticTableUpload(
      String orgId,
      CostItineraryCostFactorsDto costFactorsDto,
      CSVReader csvReader,
      String costFactorPrefix,
      String selectorCf)
      throws CsvValidationException, IOException {
    List<CostValueDataUpload> costValueDataUploadList = new ArrayList<>();
    String[] costValueRow = csvReader.readNext();

    CostValueDataUpload costValueDataUpload =
        getCostValueDataUpload(
            orgId,
            costFactorsDto.getCostItinerary(),
            selectorCf.isEmpty() ? "" : costFactorPrefix,
            costValueRow[1],
            EMPTY_STRING);

    costValueDataUploadList.add(costValueDataUpload);
    return costValueDataUploadList;
  }

  private String getCostFactorPrefix(
      Path tempCostDefinitionFile, String selectorCf, String[] dynamicRow) {
    List<String> costFactorPrefixKeys = new ArrayList<>();
    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      String[] row;
      // skip till costFactor headers
      csvReader.skip(3);

      // for static table
      if (dynamicRow.length == 0) {
        return selectorCf.isBlank() ? EMPTY_STRING : csvReader.readNext()[1];
      }

      // for grid and table
      while ((row = csvReader.readNext()) != null && !row[0].contains(DYNAMIC_ROW_CONTENT)) {
        if (!row[1].isEmpty()) costFactorPrefixKeys.add(row[1]);
      }
    } catch (Exception e) {
      log.error("Error while finding cost factor prefix key", e);
      return EMPTY_STRING;
    }
    return String.join(PIPE_DELIMITER, costFactorPrefixKeys);
  }

  private List<CostValueDataUpload> getCostDefinitionJobRequestForGridOrTableUpload(
      String orgId,
      CostItineraryCostFactorsDto costFactorsDto,
      String[] dynamicRow,
      CSVReader csvReader,
      String costFactorPrefix)
      throws CsvValidationException, IOException {
    Boolean dynamicRowValue = Boolean.parseBoolean(dynamicRow[1]);
    var rowCfValuesList = costFactorsDto.getRow().getValues();
    String dynamicCfValue = rowCfValuesList.get(rowCfValuesList.size() - 1);
    String[] csvRow;

    // read till dynamic row data
    while (true) {
      if ((csvRow = csvReader.readNext()) == null || csvRow[0].contains(DYNAMIC_ROW_CONTENT)) break;
    }

    // check for grid or table upload
    if (Objects.nonNull(costFactorsDto.getRow()) && Objects.nonNull(costFactorsDto.getColumn())) {
      return getCostDefinitionJobRequestForGridUploadData(
          orgId, csvReader, dynamicRowValue, dynamicCfValue, costFactorPrefix, costFactorsDto);
    } else {
      return getCostDefinitionJobRequestForTableData(
          orgId, csvReader, dynamicRowValue, dynamicCfValue, costFactorPrefix, costFactorsDto);
    }
  }

  private List<CostValueDataUpload> getCostDefinitionJobRequestForTableData(
      String orgId,
      CSVReader csvReader,
      Boolean dynamicRowValue,
      String dynamicCfValue,
      String costFactorPrefix,
      CostItineraryCostFactorsDto costFactorsDto)
      throws IOException, CsvValidationException {
    List<CostValueDataUpload> costValueDataUploadList = new ArrayList<>();
    String[] rowCfValues;
    String prevSlabValue = EMPTY_STRING;

    // skip header
    csvReader.skip(1);

    // prepare cost value upload for table data upload
    while ((rowCfValues = csvReader.readNext()) != null) {
      if (rowCfValues.length > 1) {
        var rowCf = rowCfValues[0];
        var rowCfValue = rowCfValues[1];
        var costFactorCombinationKey =
            StringUtils.hasLength(costFactorPrefix)
                ? String.join(PIPE_DELIMITER, costFactorPrefix, rowCf)
                : rowCf;

        CostValueDataUpload costValueDataUpload =
            getCostValueDataUpload(
                orgId,
                costFactorsDto.getCostItinerary(),
                costFactorCombinationKey,
                rowCfValue,
                getPrevSlabValue(rowCf, dynamicRowValue, dynamicCfValue, prevSlabValue));

        prevSlabValue = costFactorCombinationKey;
        costValueDataUploadList.add(costValueDataUpload);
      }
    }
    return costValueDataUploadList;
  }

  private List<CostValueDataUpload> getCostDefinitionJobRequestForGridUploadData(
      String orgId,
      CSVReader csvReader,
      Boolean dynamicRowValue,
      String dynamicCfValue,
      String costFactorPrefix,
      CostItineraryCostFactorsDto costFactorsDto)
      throws CsvValidationException, IOException {
    List<CostValueDataUpload> costValueDataUploadList = new ArrayList<>();
    String[] rowCfValues;
    Map<Integer, String> prevSlabValueMap = new HashMap<>();
    String[] colCfValuesWithHeaderRow = csvReader.readNext();
    List<String> colCfValuesWithOutHeaderRow =
        Arrays.asList(colCfValuesWithHeaderRow).subList(1, colCfValuesWithHeaderRow.length);

    // prepare cost value upload for grid data upload
    while ((rowCfValues = csvReader.readNext()) != null) {
      if (rowCfValues.length > 1) {
        var costValueIndex = new AtomicInteger(1);
        String[] finalRowCfValues = rowCfValues;
        colCfValuesWithOutHeaderRow.forEach(
            colCfValue -> {
              var costValue = finalRowCfValues[costValueIndex.getAndIncrement()];
              var rowCfValue = finalRowCfValues[0];
              var costFactorCombinationKey =
                  StringUtils.hasLength(costFactorPrefix)
                      ? String.join(PIPE_DELIMITER, costFactorPrefix, colCfValue, rowCfValue)
                      : String.join(PIPE_DELIMITER, colCfValue, rowCfValue);
              var prevSlabValue =
                  getPrevSlabValue(
                      rowCfValue,
                      dynamicRowValue,
                      dynamicCfValue,
                      prevSlabValueMap.get(costValueIndex.get()));

              CostValueDataUpload costValueDataUpload =
                  getCostValueDataUpload(
                      orgId,
                      costFactorsDto.getCostItinerary(),
                      costFactorCombinationKey,
                      costValue,
                      prevSlabValue);

              prevSlabValueMap.put(costValueIndex.get(), costFactorCombinationKey);

              costValueDataUploadList.add(costValueDataUpload);
            });
      }
    }
    return costValueDataUploadList;
  }

  private CostValueDataUpload getCostValueDataUpload(
      String orgId,
      String costItinerary,
      String costFactorCombinationKey,
      String costValue,
      String prevSlabValue) {
    return CostValueDataUpload.builder()
        .orgId(orgId)
        .costItinerary(costItinerary)
        .costFactorCombinationKey(costFactorCombinationKey)
        .costValue(costValue)
        .prevSlabValue(prevSlabValue)
        .build();
  }

  private String getPrevSlabValue(
      String rowCfValue, Boolean dynamicRowValue, String dynamicCfValue, String prevSlabValue) {
    return rowCfValue.equals(dynamicCfValue) && dynamicRowValue.equals(Boolean.TRUE)
        ? prevSlabValue
        : EMPTY_STRING;
  }

  private Map<String, CostItineraryCostFactorsDto> getCostFactorsMap(
      CostTypeValidationResponse costTypeDetails) {
    Map<String, CostItineraryCostFactorsDto> costFactorsDtoMap = new HashMap<>();

    if (costTypeDetails.getSelectorCf().isEmpty()) {
      costFactorsDtoMap.put(
          costTypeDetails.getSelectorCf(),
          getCostItineraryCostFactorsDto(
              costTypeDetails.getCostItinerary(),
              costTypeDetails.getCostFactors(),
              costTypeDetails.getRow(),
              costTypeDetails.getColumn()));
    } else {
      List<SelectorCfInfo> selectorCfInfoList = costTypeDetails.getSelectorCfInfo();

      costFactorsDtoMap =
          selectorCfInfoList.stream()
              .collect(
                  Collectors.toMap(
                      SelectorCfInfo::getSelectorCfValue,
                      selectorCfInfo ->
                          getCostItineraryCostFactorsDto(
                              selectorCfInfo.getCostItinerary(),
                              selectorCfInfo.getCostFactors(),
                              selectorCfInfo.getRow(),
                              selectorCfInfo.getColumn())));
    }
    return costFactorsDtoMap;
  }

  private CostItineraryCostFactorsDto getCostItineraryCostFactorsDto(
      String costItinerary,
      List<CostFactorDescriptionDto> costFactors,
      CostFactorDescriptionDto rowCostFactors,
      CostFactorDescriptionDto columnCostFactors) {
    return CostItineraryCostFactorsDto.builder()
        .costItinerary(costItinerary)
        .costFactors(costFactors)
        .row(rowCostFactors)
        .column(columnCostFactors)
        .build();
  }

  private String[] getDynamicRow(Path tempCostDefinitionFile) {
    try (CSVReader csvReader =
        new CSVReader(new FileReader(tempCostDefinitionFile.toAbsolutePath().toFile()))) {
      String[] row;
      while ((row = csvReader.readNext()) != null) {
        if (row[0].contains(DYNAMIC_ROW_CONTENT)) return row;
      }
    } catch (Exception e) {
      log.error("Error while finding dynamic row", e);
    }
    return new String[0];
  }
}
