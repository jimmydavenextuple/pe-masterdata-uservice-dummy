/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static org.apache.commons.lang3.BooleanUtils.FALSE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostValueEntity;
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorHeadersInfoDto;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.RateCardColumnsDto;
import com.nextuple.sourcing.cost.config.dto.RateCardRowsDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.service.validation.RateCardRequestValidationFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateCardTableDashboardService {
  private static final String COMMA_DELIMITER = ",";
  private static final String PIPE_DELIMITER = "|";
  private static final String IS_DYNAMIC_BUCKET = "isDynamicBucket";
  private static final String EMPTY_STRING = "";
  private static final String NA = "NA";
  private static final Integer FIRST = 0;
  public static final String STRING_SPLIT_REGEX = "[\\W_]+";
  public static final String INVALID_SELECTOR_FOR_COST_TYPE =
      "Invalid selector of cost type for given orgId.";
  private static final String SELECTOR = "selector";

  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  private final CostFactorRepository costFactorRepository;
  private final CostValueRepository costValueRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final TenantCostTypeRepository tenantCostTypeRepository;
  private final CostTypeDashboardService costTypeDashboardService;
  private final RateCardRequestValidationFactory rateCardRequestValidationFactory;
  private final PreferenceSelectorRepository preferenceSelectorRepository;

  public CostDefinitionResponse getRateCardTableData(
      String orgId, CostDefinitionRequest costDefinitionRequest) throws CommonServiceException {
    validateRateCardRequest(orgId, costDefinitionRequest);

    String selectorCf =
        (Objects.nonNull(costDefinitionRequest.getSelector())
            ? costDefinitionRequest.getSelector().getSelectorCf()
            : null);
    String selectorCfValue =
        ((Objects.nonNull(costDefinitionRequest.getSelector())
                && !ObjectUtils.isEmpty(costDefinitionRequest.getSelector().getSelectorCfValue()))
            ? costDefinitionRequest.getSelector().getSelectorCfValue()
            : null);
    /* Get the cost itinerary from Selector and Cost Itinerary Table */
    var costItineraryMappingEntity =
        getCostItinerary(orgId, selectorCf, selectorCfValue, costDefinitionRequest.getCostType());

    /* Prepare and return cost definition response */
    return prepareCostDefinitionResponse(
        orgId, costDefinitionRequest, costItineraryMappingEntity.getCostItinerary());
  }

  private void validateRateCardRequest(String orgId, CostDefinitionRequest costDefinitionRequest)
      throws CommonServiceException {
    var costType = costDefinitionRequest.getCostType();
    var selector = costDefinitionRequest.getSelector();
    validateCostType(orgId, costType);
    validateSelectorCf(orgId, costType, selector);
    var costTypeValidationResponse =
        costTypeDashboardService.getCostTypeDetailsForValidation(orgId, costType);
    var requestValidation =
        rateCardRequestValidationFactory.getRateCardRequestValidationFactory(selector);
    requestValidation.validateCostDefinitionRequest(
        costDefinitionRequest, costTypeValidationResponse);
  }

  private void validateSelectorCf(String orgId, String costType, SelectorCostFactorInfoDto selector)
      throws CommonServiceException {
    List<PreferenceSelectorEntity> preferenceSelectorEntityList =
        preferenceSelectorRepository.findByOrgIdAndCostType(orgId, costType);

    if (preferenceSelectorEntityList.isEmpty()) {
      if (Objects.nonNull(selector) && Objects.nonNull(selector.getSelectorCf())) {
        throwCommonServiceException(SELECTOR, costType, INVALID_SELECTOR_FOR_COST_TYPE);
      }
    } else if (Objects.isNull(selector) || Objects.isNull(selector.getSelectorCf())) {
      throwCommonServiceException(SELECTOR, selector, INVALID_SELECTOR_FOR_COST_TYPE);
    } else {
      var selectorForCostType = preferenceSelectorEntityList.get(FIRST).getSelectorCf();
      if (!selector.getSelectorCf().equals(selectorForCostType)) {
        throwCommonServiceException(SELECTOR, selector, INVALID_SELECTOR_FOR_COST_TYPE);
      }
    }
  }

  private CostDefinitionResponse prepareCostDefinitionResponse(
      String orgId, CostDefinitionRequest costDefinitionRequest, String costItinerary)
      throws CommonServiceException {
    var costDefinitionResponse = new CostDefinitionResponse();
    costDefinitionResponse.setRateCardActive(getIsRateCardActive(orgId, costItinerary));
    var costType = costDefinitionRequest.getCostType();
    var costTypeDisplayName = getCostTypeDisplayName(orgId, costType);

    if (Objects.nonNull(costDefinitionRequest.getRow())
        && Objects.nonNull(costDefinitionRequest.getColumn())) {
      prepareGridData(orgId, costDefinitionRequest, costItinerary, costDefinitionResponse);
    } else if (Objects.nonNull(costDefinitionRequest.getRow())) {
      prepareTableData(
          orgId, costDefinitionRequest, costItinerary, costDefinitionResponse, costTypeDisplayName);
    } else {
      prepareStaticTable(
          orgId, costDefinitionRequest, costItinerary, costDefinitionResponse, costTypeDisplayName);
    }

    return costDefinitionResponse;
  }

  private void prepareGridData(
      String orgId,
      CostDefinitionRequest costDefinitionRequest,
      String costItinerary,
      CostDefinitionResponse costDefinitionResponse)
      throws CommonServiceException {
    var rateCardColumnsDto = new RateCardColumnsDto();
    var rowCostFactorDetails = getCostFactorValues(orgId, costDefinitionRequest.getRow());
    var columnCostFactorDetails = getCostFactorValues(orgId, costDefinitionRequest.getColumn());
    rateCardColumnsDto.setTitle(columnCostFactorDetails.getDisplayName());

    /* Get row and column cost factor values from Tenant Cost Factor Table */
    String[] rowCfValues =
        getCostFactorValues(orgId, costDefinitionRequest.getRow())
            .getValues()
            .split(COMMA_DELIMITER);
    String[] columnCfValues =
        getCostFactorValues(orgId, costDefinitionRequest.getColumn())
            .getValues()
            .split(COMMA_DELIMITER);

    List<String> costFactorKeys =
        fetchCostFactorKeysForGrid(costDefinitionRequest, rowCfValues, columnCfValues);

    /* Get cost values from Cost Value Table */
    Map<String, CostValueEntity> costValueMap =
        getCostValuesMap(orgId, costItinerary, costFactorKeys);

    /* Prepare and set colum data */
    prepareColumnDataForGrid(
        rowCostFactorDetails, columnCostFactorDetails, rateCardColumnsDto, costDefinitionResponse);

    /* Prepare and set row data */
    prepareRowDataForGrid(
        costDefinitionRequest,
        rowCfValues,
        columnCfValues,
        rowCostFactorDetails,
        costDefinitionResponse,
        costValueMap);
  }

  private void prepareRowDataForGrid(
      CostDefinitionRequest costDefinitionRequest,
      String[] rowCfValues,
      String[] columnCfValues,
      CostFactorEntity rowCostFactorDetails,
      CostDefinitionResponse costDefinitionResponse,
      Map<String, CostValueEntity> costValueMap) {
    List<Map<String, String>> rowDataList = new ArrayList<>();
    String costValuePrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);
    String lastRowCfValue = rowCfValues[rowCfValues.length - 1];
    for (String rowCfValue : rowCfValues) {
      Map<String, String> rowData = new HashMap<>();
      boolean isDynamicBucket = false;
      rowData.put(convertToCamelCase(rowCostFactorDetails.getCostFactor()), rowCfValue);
      for (String colCfValue : columnCfValues) {
        var costValueEntity = getCostValue(rowCfValue, colCfValue, costValueMap, costValuePrefix);
        rowData.put(
            convertToCamelCase(colCfValue),
            Objects.isNull(costValueEntity) ? NA : String.valueOf(costValueEntity.getCostValue()));
        if (rowCfValue.equals(lastRowCfValue)) {
          isDynamicBucket =
              isDynamicBucket
                  || (!Objects.isNull(costValueEntity)
                      && StringUtils.hasLength(costValueEntity.getPrevSlabValue()));
        }
      }
      rowData.put(IS_DYNAMIC_BUCKET, String.valueOf(isDynamicBucket));
      rowDataList.add(rowData);
    }
    costDefinitionResponse.setRows(RateCardRowsDto.builder().data(rowDataList).build());
  }

  private void prepareColumnDataForGrid(
      CostFactorEntity rowCostFactorDetails,
      CostFactorEntity columnCostFactorDetails,
      RateCardColumnsDto rateCardColumnsDto,
      CostDefinitionResponse costDefinitionResponse) {
    List<CostFactorHeadersInfoDto> costFactorHeadersInfoList = new ArrayList<>();

    // fetch cost factor data for the first column
    var costFactorHeaderForFirstCol =
        getCostFactorHeadersInfoDto(
            rowCostFactorDetails.getDisplayName(), rowCostFactorDetails.getCostFactor(), true);
    costFactorHeadersInfoList.add(costFactorHeaderForFirstCol);

    // fetch cost factor values data for the other columns
    String[] cfValues = columnCostFactorDetails.getValues().split(COMMA_DELIMITER);
    Arrays.stream(cfValues)
        .forEach(
            cfValue -> {
              var costFactorHeaderForOtherCols =
                  getCostFactorHeadersInfoDto(cfValue, cfValue, false);
              costFactorHeadersInfoList.add(costFactorHeaderForOtherCols);
            });
    rateCardColumnsDto.setHeaders(costFactorHeadersInfoList);
    costDefinitionResponse.setColumns(rateCardColumnsDto);
  }

  private void prepareTableData(
      String orgId,
      CostDefinitionRequest costDefinitionRequest,
      String costItinerary,
      CostDefinitionResponse costDefinitionResponse,
      String costTypeDisplayName)
      throws CommonServiceException {
    var rateCardColumnsDto = new RateCardColumnsDto();
    var rowCostFactorDetails = getCostFactorValues(orgId, costDefinitionRequest.getRow());
    rateCardColumnsDto.setTitle(EMPTY_STRING);

    /* Get row and column cost factor values from Tenant Cost Factor Table */
    String[] rowCfValues =
        getCostFactorValues(orgId, costDefinitionRequest.getRow())
            .getValues()
            .split(COMMA_DELIMITER);

    List<String> costFactorKeys = fetchCostFactorKeysForTable(costDefinitionRequest, rowCfValues);

    /* Get cost values from Cost Value Table */
    Map<String, CostValueEntity> costValueMap =
        getCostValuesMap(orgId, costItinerary, costFactorKeys);

    /* Prepare and set column data */
    prepareColumnDataForTable(
        costDefinitionRequest,
        rowCostFactorDetails,
        rateCardColumnsDto,
        costDefinitionResponse,
        costTypeDisplayName);

    /* Prepare and set row data */
    prepareRowDataForTable(
        costDefinitionRequest,
        costDefinitionResponse,
        rowCostFactorDetails,
        rowCfValues,
        costValueMap);
  }

  private void prepareRowDataForTable(
      CostDefinitionRequest costDefinitionRequest,
      CostDefinitionResponse costDefinitionResponse,
      CostFactorEntity rowCostFactorDetails,
      String[] rowCfValues,
      Map<String, CostValueEntity> costValueMap) {
    List<Map<String, String>> rowDataList = new ArrayList<>();
    String costValuePrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);
    Arrays.stream(rowCfValues)
        .forEach(
            rowCfValue -> {
              Map<String, String> rowData = new HashMap<>();
              rowData.put(convertToCamelCase(rowCostFactorDetails.getCostFactor()), rowCfValue);
              var costValueEntity = getCostValue(rowCfValue, "", costValueMap, costValuePrefix);
              rowData.put(
                  convertToCamelCase(costDefinitionRequest.getCostType()),
                  Objects.isNull(costValueEntity)
                      ? NA
                      : String.valueOf(costValueEntity.getCostValue()));
              rowData.put(
                  IS_DYNAMIC_BUCKET,
                  Objects.isNull(costValueEntity)
                      ? FALSE
                      : String.valueOf(StringUtils.hasLength(costValueEntity.getPrevSlabValue())));
              rowDataList.add(rowData);
            });

    costDefinitionResponse.setRows(RateCardRowsDto.builder().data(rowDataList).build());
  }

  private void prepareColumnDataForTable(
      CostDefinitionRequest costDefinitionRequest,
      CostFactorEntity rowCostFactorDetails,
      RateCardColumnsDto rateCardColumnsDto,
      CostDefinitionResponse costDefinitionResponse,
      String costTypeDisplayName) {
    List<CostFactorHeadersInfoDto> costFactorHeadersInfoList = new ArrayList<>();

    // get cost factor data for the first column
    var costFactorHeaderForFirstCol =
        getCostFactorHeadersInfoDto(
            rowCostFactorDetails.getDisplayName(), rowCostFactorDetails.getCostFactor(), true);

    // get cost factor data for second column based on costType
    var costFactorHeaderForSecondCol =
        getCostFactorHeadersInfoDto(
            costTypeDisplayName, costDefinitionRequest.getCostType(), false);

    costFactorHeadersInfoList.add(costFactorHeaderForFirstCol);
    costFactorHeadersInfoList.add(costFactorHeaderForSecondCol);
    rateCardColumnsDto.setHeaders(costFactorHeadersInfoList);
    costDefinitionResponse.setColumns(rateCardColumnsDto);
  }

  private void prepareStaticTable(
      String orgId,
      CostDefinitionRequest costDefinitionRequest,
      String costItinerary,
      CostDefinitionResponse costDefinitionResponse,
      String costTypeDisplayName) {
    var rateCardColumnsDto = new RateCardColumnsDto();
    List<String> costFactorKeys = fetchCostFactorKeysForStaticTable(costDefinitionRequest);
    rateCardColumnsDto.setTitle(EMPTY_STRING);

    /* Get cost value from Cost Value Table */
    Map<String, CostValueEntity> costValueMap =
        getCostValuesMap(orgId, costItinerary, costFactorKeys);

    /* Prepare and set column data */
    prepareColumnDataForStaticTable(
        costDefinitionRequest, rateCardColumnsDto, costDefinitionResponse, costTypeDisplayName);

    /* Prepare and set row data */
    prepareRowDataForStaticTable(costDefinitionRequest, costDefinitionResponse, costValueMap);
  }

  private List<String> fetchCostFactorKeysForStaticTable(
      CostDefinitionRequest costDefinitionRequest) {
    String costFactorPrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);
    return Collections.singletonList(composePrefixedValue(costFactorPrefix));
  }

  private void prepareRowDataForStaticTable(
      CostDefinitionRequest costDefinitionRequest,
      CostDefinitionResponse costDefinitionResponse,
      Map<String, CostValueEntity> costValueMap) {
    List<Map<String, String>> rowDataList = new ArrayList<>();
    String costValuePrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);
    Map<String, String> rowData = new HashMap<>();
    var costValueEntity = costValueMap.get(composePrefixedValue(costValuePrefix));
    rowData.put(
        convertToCamelCase(costDefinitionRequest.getCostType()),
        Objects.isNull(costValueEntity) ? NA : String.valueOf(costValueEntity.getCostValue()));
    rowDataList.add(rowData);

    costDefinitionResponse.setRows(RateCardRowsDto.builder().data(rowDataList).build());
  }

  private void prepareColumnDataForStaticTable(
      CostDefinitionRequest costDefinitionRequest,
      RateCardColumnsDto rateCardColumnsDto,
      CostDefinitionResponse costDefinitionResponse,
      String costTypeDisplayName) {
    List<CostFactorHeadersInfoDto> costFactorHeadersInfoList = new ArrayList<>();
    var costFactorHeader =
        getCostFactorHeadersInfoDto(
            costTypeDisplayName, costDefinitionRequest.getCostType(), false);
    costFactorHeadersInfoList.add(costFactorHeader);
    rateCardColumnsDto.setHeaders(costFactorHeadersInfoList);
    costDefinitionResponse.setColumns(rateCardColumnsDto);
  }

  private List<String> fetchCostFactorKeysForTable(
      CostDefinitionRequest costDefinitionRequest, String[] rowCfValues) {
    String costFactorKeyPrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);

    return Arrays.stream(rowCfValues)
        .map(rowCfValue -> composePrefixAndValue(costFactorKeyPrefix, rowCfValue))
        .toList();
  }

  private CostValueEntity getCostValue(
      String rowCfValue,
      String colCfValue,
      Map<String, CostValueEntity> costValueMap,
      String costValuePrefix) {
    String costFactorCombinationKey;

    if (colCfValue.isEmpty()) {
      costFactorCombinationKey = composePrefixAndValue(costValuePrefix, rowCfValue);
    } else {
      costFactorCombinationKey = composePrefixAndValues(costValuePrefix, rowCfValue, colCfValue);
    }

    return costValueMap.get(costFactorCombinationKey);
  }

  private CostFactorHeadersInfoDto getCostFactorHeadersInfoDto(
      String columnName, String inputColumnMeta, boolean isCostFactor) {
    String columnMeta = convertToCamelCase(inputColumnMeta);
    return CostFactorHeadersInfoDto.builder()
        .columnName(columnName)
        .columnMeta(columnMeta)
        .isCostFactor(isCostFactor)
        .build();
  }

  private static String convertToCamelCase(String inputColumnMeta) {
    if (!StringUtils.hasLength(inputColumnMeta)) {
      return inputColumnMeta;
    }
    String[] words = inputColumnMeta.split(STRING_SPLIT_REGEX);
    if (words.length == 1) {
      return words[0].toLowerCase();
    }
    StringBuilder builder = new StringBuilder();
    Arrays.stream(words)
        .forEach(
            word -> {
              if (!word.isEmpty()) {
                if (builder.length() == 0) {
                  builder.append(word.toLowerCase());
                } else {
                  builder
                      .append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase());
                }
              }
            });
    return builder.toString();
  }

  private Map<String, CostValueEntity> getCostValuesMap(
      String orgId, String costItinerary, List<String> costFactorKeys) {
    Map<String, CostValueEntity> costValueMap = new HashMap<>();
    List<CostValueEntity> costValueEntities =
        costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKeyIn(
            orgId, costItinerary, costFactorKeys);

    costValueEntities.forEach(
        costValueEntity ->
            costValueMap.put(costValueEntity.getCostFactorCombinationKey(), costValueEntity));
    return costValueMap;
  }

  private String getCostTypeDisplayName(String orgId, String costType) {
    var tenantCostTypeEntity = tenantCostTypeRepository.findByOrgIdAndCostType(orgId, costType);
    return tenantCostTypeEntity.isPresent()
        ? tenantCostTypeEntity.get().getDisplayName()
        : EMPTY_STRING;
  }

  private List<String> fetchCostFactorKeysForGrid(
      CostDefinitionRequest costDefinitionRequest, String[] rowCfValues, String[] columnCfValues) {
    String costFactorKeyPrefix = fetchCostFactorKeyPrefix(costDefinitionRequest);

    return Arrays.stream(rowCfValues)
        .flatMap(
            rowCfValue ->
                Arrays.stream(columnCfValues)
                    .map(
                        columnCfValue ->
                            composePrefixAndValues(costFactorKeyPrefix, rowCfValue, columnCfValue)))
        .toList();
  }

  private String fetchCostFactorKeyPrefix(CostDefinitionRequest costDefinitionRequest) {
    String selectorCfKey =
        (Objects.nonNull(costDefinitionRequest.getSelector())
            ? costDefinitionRequest.getSelector().getSelectorCfValue()
            : null);
    if (costDefinitionRequest.getFilters().isEmpty()) return selectorCfKey;

    String filterCfKey =
        costDefinitionRequest.getFilters().stream()
            .map(FilterCostFactorInfoDto::getCostFactorValue)
            .collect(Collectors.joining(PIPE_DELIMITER));

    return composePrefixAndValue(selectorCfKey, filterCfKey);
  }

  private CostFactorEntity getCostFactorValues(String orgId, String costFactor)
      throws CommonServiceException {
    List<CostFactorEntity> costFactorEntities =
        costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (costFactorEntities.isEmpty()) {
      throwCommonServiceException("costFactor", costFactor, "Cost factor details not found.");
    }
    return costFactorEntities.get(FIRST);
  }

  private SelectorAndCostItineraryMappingEntity getCostItinerary(
      String orgId, String selectorCf, String selectCfValue, String costType)
      throws CommonServiceException {
    List<SelectorAndCostItineraryMappingEntity> costItineraryMappingEntityList =
        selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
                orgId, costType, selectorCf, selectCfValue);
    if (costItineraryMappingEntityList.isEmpty()) {
      throwCommonServiceException(
          "selectorCf", selectorCf, "Cost itinerary not found for selector cost factor.");
    }
    return costItineraryMappingEntityList.get(0);
  }

  private void validateCostType(String orgId, String costType) throws CommonServiceException {
    var tenantCostTypeEntity = tenantCostTypeRepository.findByOrgIdAndCostType(orgId, costType);
    if (tenantCostTypeEntity.isEmpty())
      throwCommonServiceException("costType", costType, "Invalid cost type for given orgId.");
  }

  private void validateFilterValues(String orgId, List<FilterCostFactorInfoDto> filters)
      throws CommonServiceException {
    for (FilterCostFactorInfoDto filter : filters) {
      var filterCostFactorEntity =
          costFactorRepository.findFirstByOrgIdAndCostFactor(orgId, filter.getCostFactor());
      if (filterCostFactorEntity.isEmpty())
        throwCommonServiceException(
            "costFactor", filter.getCostFactor(), "Cost factor for given orgId not found.");
      String costFactorValues = filterCostFactorEntity.get().getValues();
      List<String> valuesList = Arrays.asList(costFactorValues.split(","));
      if (valuesList.stream().noneMatch(value -> value.equals(filter.getCostFactorValue())))
        throwCommonServiceException(
            "costFactorValue",
            filter.getCostFactorValue(),
            "Cost factor value for given cost factor and orgId not found.");
    }
  }

  private boolean getIsRateCardActive(String orgId, String costItinerary) {
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactor =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);

    return costItineraryAndFactor.isPresent() && costItineraryAndFactor.get().getIsActive();
  }

  private void throwCommonServiceException(
      String rejectedField, Object rejectedValue, String errorMessage)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(rejectedField, FieldError.builder().rejectedValue(rejectedValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }

  private String composePrefixedValue(String costFactorPrefix) {
    return StringUtils.hasLength(costFactorPrefix) ? costFactorPrefix : "";
  }

  private String composePrefixAndValue(String preFixKey, String secondKey) {
    return StringUtils.hasLength(preFixKey)
        ? String.join(PIPE_DELIMITER, preFixKey, secondKey)
        : secondKey;
  }

  private String composePrefixAndValues(
      String costFactorKeyPrefix, String rowCfValue, String columnCfValue) {
    return StringUtils.hasLength(costFactorKeyPrefix)
        ? String.join(PIPE_DELIMITER, costFactorKeyPrefix, columnCfValue, rowCfValue)
        : String.join(PIPE_DELIMITER, columnCfValue, rowCfValue);
  }
}
