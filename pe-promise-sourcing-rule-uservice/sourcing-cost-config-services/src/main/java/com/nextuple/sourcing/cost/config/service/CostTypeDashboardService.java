/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.OptimizationAndCostTypesMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.OptimizationAndCostTypesMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorFieldsInfo;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorUIValues;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorValueDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostTypeDtoInfo;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.RowColumnDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfUIInfo;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.inbound.UpdateRateCardStatusRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
import com.nextuple.sourcing.cost.config.pojo.SelectorInfo;
import jakarta.transaction.Transactional;
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
@RequiredArgsConstructor
@Slf4j
public class CostTypeDashboardService {
  private static final String COMMA_DELIMITER = ",";
  private static final String CURRENCY = "currency";
  private static final Integer FIRST = 0;
  private static final String DEFAULT_CURRENCY = "USD";
  private static final String NULL = null;
  private static final String EMPTY_STRING = "";
  public static final String ORG_ID = "orgId";
  public static final String SELECTOR_CF = "selectorCf";
  public static final String SELECTOR_CF_VALUE = "selectorCfValue";
  public static final String COST_TYPE = "costType";
  public static final String COST_TYPES = "costTypes";
  private static final String COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost Itinerary And Cost Factors Mapping not found";
  private static final String INACTIVATE_RATE_CARD_STATUS_EXCEPTION_MESSAGE =
      "Default itinerary cannot be deactivated";
  private static final String IS_RATE_CARD_ACTIVE = "isRateCardActive";
  private static final String COST_ITINERARY = "costItinerary";
  private static final String SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION =
      "Selector Itinerary Mapping not found with given details";
  private static final Boolean IS_ACTIVE_FALSE = false;
  private static final String TENANT_COST_TYPE_EXCEPTION =
      "Tenant cost type not found for the orgId";
  public static final String ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE =
      "Unable to deactivate only itinerary for the cost type";

  private final PreferenceSelectorRepository preferenceSelectorRepository;
  private final TenantCostTypeRepository tenantCostTypeRepository;
  private final CostFactorRepository costFactorRepository;
  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final ConfigurationFeign configurationFeign;
  private final OptimizationAndCostTypesMappingRepository optimizationAndCostTypesMappingRepository;

  public CostTypeResponse getCostTypes(String orgId) {
    List<CostTypeDtoInfo> costTypeDtoList = new ArrayList<>();
    CostTypeResponse costTypeResponse =
        CostTypeResponse.builder()
            .currency(validString(getCurrency(orgId)))
            .costTypeList(validList(costTypeDtoList))
            .build();

    /* Step 1: Get cost types from Tenant Cost Type for the org. Ex [ SHIPPING_COST,NODE_PROCESSING_COST ]*/
    List<TenantCostTypeEntity> tenantCostTypeEntities = getTenantCostTypesByOrgID(orgId);

    for (TenantCostTypeEntity costTypeEntity : tenantCostTypeEntities) {
      String costType = costTypeEntity.getCostType();

      /* Step 2: Get the selector for each cost type from Tenant Preference for Selector */
      String selectCf = gePreferenceSelectorForCostType(orgId, costType);

      /* Step 3: If selector Cf is not Empty */
      if (StringUtils.hasLength(selectCf)) {
        CostTypeDtoInfo costTypeDto =
            getCostTypeDtoInfo(costType, costTypeEntity.getDisplayName(), selectCf, orgId);
        costTypeDtoList.add(costTypeDto);
        /* Step 4: Get the values of selector from the Tenant Cost Factor table */
        CostFactorEntity costFactorEntity = getCostFactorEntityForCostFactor(orgId, selectCf);
        String displayName =
            Objects.nonNull(costFactorEntity)
                ? validString(costFactorEntity.getDisplayName())
                : EMPTY_STRING;
        List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntityList =
            getSelectorAndCostItineraryMappingDetails(orgId, costType, selectCf);
        setCostFactorAndItineraryDetails(
            orgId,
            selectorAndCostItineraryMappingEntityList,
            displayName,
            costTypeDto,
            costType,
            selectCf);
      } else {
        CostTypeDtoInfo costTypeDto =
            getCostTypeDtoInfo(costType, costTypeEntity.getDisplayName(), EMPTY_STRING, orgId);
        /*Step 3: For empty selector Cf,find the default itinerary for the cost type*/
        CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
            getCreatedCostItineraryForSelectCf(orgId, costType, NULL, NULL);
        String costFactorsOfItinerary =
            Objects.isNull(costItineraryAndFactorsEntity)
                ? ""
                : costItineraryAndFactorsEntity.getCostFactors();
        boolean isRateCardLookupRequiredForItineraryOfCostType =
            getIsRateCardLookupRequiredForItineraryOfCostType(orgId, costFactorsOfItinerary);
        if (Boolean.TRUE.equals(isRateCardLookupRequiredForItineraryOfCostType)) {
          costTypeDto.setCostFactors(validList(List.of()));
          /* Step 4: Using the itinerary get the cost factors from Cost Itinerary & Cost Factors Mapping */
          mapCostFactorDetailsToFields(orgId, costItineraryAndFactorsEntity, selectCf, costTypeDto);
          costTypeDtoList.add(costTypeDto);
        }
      }
    }
    return costTypeResponse;
  }

  private void mapCostFactorDetailsToFields(
      String orgId,
      CostItineraryAndFactorsEntity costItineraryAndFactorsEntity,
      String selectCf,
      CostFactorFieldsInfo costFactorFieldsInfo) {
    if (!ObjectUtils.isEmpty(costItineraryAndFactorsEntity)) {
      String[] costFactors = costItineraryAndFactorsEntity.getCostFactors().split(COMMA_DELIMITER);
      /* Step 5: Map cost factors, N-1th cost factor as row , N-2th cost factor as column, and other cost factors to filters  */
      createRowColumnAndFilters(orgId, selectCf, costFactorFieldsInfo, costFactors);
    }
  }

  private void setCostFactorAndItineraryDetails(
      String orgId,
      List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntityList,
      String displayName,
      CostTypeDtoInfo costTypeDto,
      String costType,
      String selectCf) {
    if (!ObjectUtils.isEmpty(selectorAndCostItineraryMappingEntityList)) {
      costTypeDto.setSelectorCfDisplayName(displayName);
      List<SelectorCfUIInfo> selectorCfInfoList =
          selectorAndCostItineraryMappingEntityList.stream()
              .map(
                  entity -> {
                    SelectorCfUIInfo selectorCfUIInfo =
                        getSelectorCfInfo(orgId, entity.getSelectorCfValue(), costType, selectCf);
                    selectorCfUIInfo.setDisplayName(
                        Objects.nonNull(entity.getSelectorCfValue())
                            ? entity.getSelectorCfValue()
                            : "Default");

                    return selectorCfUIInfo;
                  })
              .toList();
      costTypeDto.setCostFactors(List.of());
      costTypeDto.setSelectorCfInfo(validList(selectorCfInfoList));
    } else {
      costTypeDto.setCostFactors(List.of());
      costTypeDto.setSelectorCfInfo(List.of());
    }
  }

  private SelectorCfUIInfo getSelectorCfInfo(
      String orgId, String selectCfValue, String costType, String selectCf) {
    SelectorCfUIInfo selectorCfInfo =
        SelectorCfUIInfo.builder()
            .selectorCfValue(validString(selectCfValue))
            .displayName(validString(selectCfValue))
            .build();
    selectorCfInfo.setCostFactors(validList(List.of()));
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        getCreatedCostItineraryForSelectCf(orgId, costType, selectCf, selectCfValue);
    mapCostFactorDetailsToFields(orgId, costItineraryAndFactorsEntity, selectCf, selectorCfInfo);
    return selectorCfInfo;
  }

  private CostTypeDtoInfo getCostTypeDtoInfo(
      String costType, String displayName, String selectCf, String orgId) {
    /* Find optimization strategies for the cost type */
    var optimizationAndCostTypesMappingEntityList =
        optimizationAndCostTypesMappingRepository.findByOrgIdAndCostTypesContaining(
            orgId, costType);
    var optimizationStrategies =
        optimizationAndCostTypesMappingEntityList.stream()
            .map(OptimizationAndCostTypesMappingEntity::getOptimizationStrategy)
            .collect(Collectors.joining(","));
    return CostTypeDtoInfo.builder()
        .costType(validString(costType))
        .displayName(validString(displayName))
        .selectorCf(selectCf)
        .selectorCfInfo(validList(List.of()))
        .selectorCfDisplayName(EMPTY_STRING)
        .optimizationStrategies(optimizationStrategies)
        .build();
  }

  private String validString(String s) {
    if (!StringUtils.hasLength(s)) s = EMPTY_STRING;
    return s;
  }

  private <T> List<T> validList(List<T> list) {
    if (Objects.isNull(list)) list = List.of();
    return list;
  }

  private List<TenantCostTypeEntity> getTenantCostTypesByOrgID(String orgId) {
    List<TenantCostTypeEntity> tenantCostTypeEntities = tenantCostTypeRepository.findByOrgId(orgId);
    if (tenantCostTypeEntities.isEmpty()) {
      log.error("Tenant cost type is empty for orgID {}", orgId);
      return List.of();
    }
    return tenantCostTypeEntities;
  }

  private String gePreferenceSelectorForCostType(String orgId, String costType) {
    List<PreferenceSelectorEntity> preferenceSelectorEntityList =
        preferenceSelectorRepository.findByOrgIdAndCostType(orgId, costType);
    // only one selectCF should be there
    if (preferenceSelectorEntityList.isEmpty()) {
      log.error("Preference selection is empty for cost type {}", costType);
      return EMPTY_STRING;
    }
    return preferenceSelectorEntityList.get(0).getSelectorCf();
  }

  private CostFactorEntity getCostFactorEntityForCostFactor(String orgId, String selectCf) {
    List<CostFactorEntity> costFactorEntities =
        costFactorRepository.findByOrgIdAndCostFactor(orgId, selectCf);
    if (costFactorEntities.isEmpty()) {
      log.error("Cost factor values are empty for cost type {}", selectCf);
      return null;
    }
    return costFactorEntities.get(FIRST);
  }

  private List<SelectorAndCostItineraryMappingEntity> getSelectorAndCostItineraryMappingDetails(
      String orgId, String costType, String selectCf) {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntities =
        selectorAndCostItineraryMappingRepository.findByOrgIdAndCostTypeAndSelectorCf(
            orgId, costType, selectCf);
    if (selectorAndCostItineraryMappingEntities.isEmpty()) {
      log.error("Cost selector and factor values are empty for cost type: {}", selectCf);
      return Collections.emptyList();
    }
    return selectorAndCostItineraryMappingEntities;
  }

  private CostItineraryAndFactorsEntity getCreatedCostItineraryForSelectCf(
      String orgId, String costType, String selectCf, String selectCfValue) {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntities =
        selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
                orgId, costType, selectCf, selectCfValue);
    if (selectorAndCostItineraryMappingEntities.isEmpty()) {
      log.error("Cost itinerary empty for selected cost factor {}", selectCfValue);
      return null;
    }

    // Get the cost itinerary which is in created state
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactors =
        selectorAndCostItineraryMappingEntities.stream()
            .map(
                selectorAndCostItineraryMappingEntity ->
                    costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatus(
                        orgId,
                        selectorAndCostItineraryMappingEntity.getCostItinerary(),
                        ItineraryStatusEnum.CREATED))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    if (costItineraryAndFactors.isEmpty()) {
      log.error(
          "Cost itinerary not in CREATED status for orgId {}, selector {} and value {}",
          orgId,
          selectCf,
          selectCfValue);
      return null;
    }
    return costItineraryAndFactors.get();
  }

  private void createRowColumnAndFilters(
      String orgId,
      String selectorCf,
      CostFactorFieldsInfo costFactorFieldsInfo,
      String[] costFactors) {

    int n = costFactors.length;
    RowColumnDto rowDto = new RowColumnDto();
    RowColumnDto columnDto = new RowColumnDto();
    List<CostFactorUIValues> filters = new ArrayList<>();

    if (StringUtils.hasLength(selectorCf)) { // If first cost factor is selector cf
      if (n >= 2) {
        getAndPopulateCostFactorDetails(orgId, costFactors, n - 1, rowDto);
        costFactorFieldsInfo.setRow(rowDto);
      }
      if (n >= 3) {
        getAndPopulateCostFactorDetails(orgId, costFactors, n - 2, columnDto);
        costFactorFieldsInfo.setColumn(columnDto);
      }

      populateFiltersDetails(orgId, filters, costFactors, 1, n - 2);
    } else { // If first cost factor is not selector cf
      if (n >= 1) {
        /* Create row with last cost factor */
        getAndPopulateCostFactorDetails(orgId, costFactors, n - 1, rowDto);
        costFactorFieldsInfo.setRow(rowDto);
      }
      if (n >= 2) {
        /* Create column with last second cost factor */
        getAndPopulateCostFactorDetails(orgId, costFactors, n - 2, columnDto);
        costFactorFieldsInfo.setColumn(columnDto);
      }
      /* Create filters with all other cost factors */
      populateFiltersDetails(orgId, filters, costFactors, 0, n - 2);
    }
    costFactorFieldsInfo.setCostFactors(validList(filters));
  }

  private void populateFiltersDetails(
      String orgId,
      List<CostFactorUIValues> filters,
      String[] costFactors,
      Integer startIndex,
      Integer endIndex) {
    for (int filtersIndex = startIndex; filtersIndex < endIndex; filtersIndex++) {
      CostFactorUIValues costFactorUIValues = new CostFactorUIValues();

      CostFactorEntity costFactorEntity =
          getAndPopulateCostFactorDetails(orgId, costFactors, filtersIndex, costFactorUIValues);

      List<CostFactorValueDto> costFactorValues = new ArrayList<>();
      costFactorUIValues.setValues(validList(costFactorValues));

      if (!ObjectUtils.isEmpty(costFactorEntity)) {
        String[] costFactorFilterValues = costFactorEntity.getValues().split(COMMA_DELIMITER);
        for (String costFactorFilter : costFactorFilterValues) {
          CostFactorValueDto costFactorValueDto =
              CostFactorValueDto.builder()
                  .value(validString(costFactorFilter))
                  .displayName(validString(costFactorFilter))
                  .build();
          costFactorValues.add(costFactorValueDto);
        }
      }
      filters.add(costFactorUIValues);
    }
  }

  private CostFactorEntity getAndPopulateCostFactorDetails(
      String orgId, String[] costFactors, int costFactorIndex, RowColumnDto rowColumnDto) {
    CostFactorEntity costFactorEntity =
        getCostFactorEntityForCostFactor(orgId, costFactors[costFactorIndex]);

    if (!ObjectUtils.isEmpty(costFactorEntity)) {
      rowColumnDto.setCostFactor(validString(costFactors[costFactorIndex]));
      rowColumnDto.setUom(validString(costFactorEntity.getUom()));
      rowColumnDto.setDisplayName(validString(costFactorEntity.getDisplayName()));
    }
    return costFactorEntity;
  }

  private String getCurrency(String orgId) {
    try {
      BaseResponse<TenantConfigdataResponse> tenantConfigdataResponseBaseResponse =
          configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(orgId, CURRENCY);
      return ObjectUtils.isEmpty(tenantConfigdataResponseBaseResponse.getPayload())
          ? DEFAULT_CURRENCY
          : tenantConfigdataResponseBaseResponse.getPayload().getConfigValue();
    } catch (Exception e) {
      log.error("No currency configured for orgId {}", orgId);
      return DEFAULT_CURRENCY;
    }
  }

  @Transactional
  public UpdateRateCardStatusResponse updateRateCardStatus(
      String orgId, UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    String costType = updateRateCardStatusRequest.getCostType();
    Boolean isRateCardActive = updateRateCardStatusRequest.getIsRateCardActive();
    validateCostType(orgId, updateRateCardStatusRequest.getCostType());
    validateSelectorForCostType(orgId, updateRateCardStatusRequest);
    if (Objects.isNull(updateRateCardStatusRequest.getSelector())) {
      validationForNullSelector(orgId, updateRateCardStatusRequest);
    } else {
      String selectorCf = updateRateCardStatusRequest.getSelector().getSelectorCf();
      String selectorCfValue = updateRateCardStatusRequest.getSelector().getSelectorCfValue();
      validateIfSelectorCfIsNullAndSelectorCfValueIsNonNull(orgId, selectorCf, selectorCfValue);
      validationForDefaultItinerary(orgId, isRateCardActive, selectorCfValue, selectorCf);
      updateIsRateCardActiveStatus(orgId, costType, isRateCardActive, updateRateCardStatusRequest);
    }
    return UpdateRateCardStatusResponse.builder()
        .costType(costType)
        .selector(updateRateCardStatusRequest.getSelector())
        .isRateCardActive(isRateCardActive)
        .build();
  }

  private void validateCostType(String orgId, String costType) throws CommonServiceException {
    var tenantCostTypeEntity = tenantCostTypeRepository.findByOrgIdAndCostType(orgId, costType);
    if (tenantCostTypeEntity.isEmpty()) {
      log.error("Invalid cost type for given orgId");
      throw new CommonServiceException(
          "Invalid cost type for given orgId",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_TYPE,
              FieldError.builder().rejectedValue(costType).build()));
    }
  }

  private void validateSelectorForCostType(
      String orgId, UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    var selectorForCostType =
        preferenceSelectorRepository.findByOrgIdAndCostType(
            orgId, updateRateCardStatusRequest.getCostType());
    if (selectorForCostType.isEmpty()) {
      var selector = updateRateCardStatusRequest.getSelector();
      if (Objects.nonNull(selector) && Objects.nonNull(selector.getSelectorCf())) {
        throwInvalidSelectorError(updateRateCardStatusRequest, orgId);
      }
    }
    if (!selectorForCostType.isEmpty()) {
      var selector = updateRateCardStatusRequest.getSelector();
      if (Objects.isNull(selector) || Objects.isNull(selector.getSelectorCf())) {
        throwInvalidSelectorError(updateRateCardStatusRequest, orgId);
      }
      if (!selector.getSelectorCf().equals(selectorForCostType.get(0).getSelectorCf())) {
        throwInvalidSelectorError(updateRateCardStatusRequest, orgId);
      }
    }
  }

  private void throwInvalidSelectorError(UpdateRateCardStatusRequest request, String orgId)
      throws CommonServiceException {
    log.error("Invalid selector provided in the request");
    throw new CommonServiceException(
        "Invalid selector provided in the request",
        HttpStatus.BAD_REQUEST,
        0x1771,
        Map.of(
            ORG_ID,
            FieldError.builder().rejectedValue(orgId).build(),
            "selector",
            FieldError.builder().rejectedValue(request.getSelector()).build()));
  }

  private void validationForNullSelector(
      String orgId, UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    if (updateRateCardStatusRequest.getIsRateCardActive().equals(IS_ACTIVE_FALSE)) {
      log.error(ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE,
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              IS_RATE_CARD_ACTIVE,
              FieldError.builder()
                  .rejectedValue(updateRateCardStatusRequest.getIsRateCardActive())
                  .build()));
    }
    updateIsRateCardActiveStatus(
        orgId,
        updateRateCardStatusRequest.getCostType(),
        updateRateCardStatusRequest.getIsRateCardActive(),
        updateRateCardStatusRequest);
  }

  private void updateIsRateCardActiveStatus(
      String orgId,
      String costType,
      Boolean isRateCardActive,
      UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    var selectorAndCostItineraryMappingEntity =
        getSelectorAndCostItineraryMappingEntity(orgId, costType, updateRateCardStatusRequest);
    String costItinerary = selectorAndCostItineraryMappingEntity.getCostItinerary();
    updateActiveStatusForCostItinerary(orgId, isRateCardActive, costItinerary);
  }

  private SelectorAndCostItineraryMappingEntity getSelectorAndCostItineraryMappingEntity(
      String orgId, String costType, UpdateRateCardStatusRequest updateRateCardStatusRequest)
      throws CommonServiceException {
    Optional<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity;
    if (Objects.isNull(updateRateCardStatusRequest.getSelector())
        || Objects.isNull(updateRateCardStatusRequest.getSelector().getSelectorCf())) {
      selectorAndCostItineraryMappingEntity =
          selectorAndCostItineraryMappingRepository.findFirstByOrgIdAndCostType(orgId, costType);
    } else {
      selectorAndCostItineraryMappingEntity =
          selectorAndCostItineraryMappingRepository
              .findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
                  orgId,
                  costType,
                  updateRateCardStatusRequest.getSelector().getSelectorCf(),
                  updateRateCardStatusRequest.getSelector().getSelectorCfValue());
    }
    validateEmptySelectorAndCostItineraryEntity(
        orgId,
        costType,
        selectorAndCostItineraryMappingEntity,
        updateRateCardStatusRequest.getSelector());
    return selectorAndCostItineraryMappingEntity.get();
  }

  private void validateIfSelectorCfIsNullAndSelectorCfValueIsNonNull(
      String orgId, String selectorCf, String selectorCfValue) throws CommonServiceException {
    if (Objects.isNull(selectorCf) && Objects.nonNull(selectorCfValue)) {
      throw new CommonServiceException(
          "When selectorCf is null, selectorCfValue must also be null",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              SELECTOR_CF_VALUE,
              FieldError.builder().rejectedValue(selectorCfValue).build()));
    }
  }

  private void updateActiveStatusForCostItinerary(
      String orgId, Boolean isRateCardActive, String costItinerary) throws CommonServiceException {
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
    validateEmptyCostItineraryAndFactorsEntity(orgId, costItinerary, costItineraryAndFactorsEntity);
    costItineraryAndFactorsEntity.get().setIsActive(isRateCardActive);
    costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity.get());
  }

  private static void validationForDefaultItinerary(
      String orgId, Boolean isRateCardActive, String selectorCfValue, String selectorCf)
      throws CommonServiceException {
    if (Objects.isNull(selectorCfValue) && isRateCardActive.equals(IS_ACTIVE_FALSE)) {
      String exceptionMessage = null;
      if (Objects.isNull(selectorCf)) {
        exceptionMessage = ONLY_ITINERARY_DEACTIVATE_EXCEPTION_MESSAGE;
      } else {
        exceptionMessage = INACTIVATE_RATE_CARD_STATUS_EXCEPTION_MESSAGE;
      }
      log.error(exceptionMessage);
      throw new CommonServiceException(
          exceptionMessage,
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              IS_RATE_CARD_ACTIVE,
              FieldError.builder().rejectedValue(isRateCardActive).build()));
    }
  }

  private void validateEmptyCostItineraryAndFactorsEntity(
      String orgId,
      String costItinerary,
      Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity)
      throws CommonServiceException {
    if (costItineraryAndFactorsEntity.isEmpty()) {
      log.error(COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  private static void validateEmptySelectorAndCostItineraryEntity(
      String orgId,
      String costType,
      Optional<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity,
      SelectorInfo selector)
      throws CommonServiceException {
    if (selectorAndCostItineraryMappingEntity.isEmpty()) {
      log.error(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      throw new CommonServiceException(
          SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION,
          HttpStatus.NOT_FOUND,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_TYPE,
              FieldError.builder().rejectedValue(costType).build(),
              "selector",
              FieldError.builder().rejectedValue(selector).build()));
    }
  }

  public CostTypeValidationResponse getCostTypeDetailsForValidation(String orgId, String costType)
      throws CommonServiceException {
    CostTypeResponse costTypeResponse = this.getCostTypes(orgId);
    List<CostTypeDtoInfo> costTypeResponseList = costTypeResponse.getCostTypeList();
    Optional<CostTypeValidationResponse> costTypeValidationResponse =
        costTypeResponseList.stream()
            .filter(costTypeDtoInfo -> costTypeDtoInfo.getCostType().equals(costType))
            .map(
                costTypeDtoInfo ->
                    createCostTypeValidationResponse(
                        orgId, costType, costTypeDtoInfo, costTypeResponse))
            .findFirst();
    if (costTypeValidationResponse.isEmpty()) {
      log.error(TENANT_COST_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return costTypeValidationResponse.get();
  }

  private CostTypeValidationResponse createCostTypeValidationResponse(
      String orgId,
      String costType,
      CostTypeDtoInfo costTypeDtoInfo,
      CostTypeResponse costTypeResponse) {
    return CostTypeValidationResponse.builder()
        .currency(costTypeResponse.getCurrency())
        .costType(costTypeDtoInfo.getCostType())
        .displayName(costTypeDtoInfo.getDisplayName())
        .row(getRowColumnCostFactorDescription(orgId, costTypeDtoInfo.getRow()))
        .column(getRowColumnCostFactorDescription(orgId, costTypeDtoInfo.getColumn()))
        .costFactors(getFilterCostFactorDescription(costTypeDtoInfo.getCostFactors()))
        .costItinerary(
            StringUtils.hasLength(costTypeDtoInfo.getSelectorCf())
                ? EMPTY_STRING
                : fetchItineraryName(orgId, costType, NULL, NULL))
        .selectorCf(costTypeDtoInfo.getSelectorCf())
        .selectorCfDisplayName(costTypeDtoInfo.getSelectorCfDisplayName())
        .selectorCfInfo(getSelectorCfInfos(orgId, costType, costTypeDtoInfo))
        .build();
  }

  private List<SelectorCfInfo> getSelectorCfInfos(
      String orgId, String costType, CostTypeDtoInfo costTypeDtoInfo) {
    return costTypeDtoInfo.getSelectorCfInfo().stream()
        .map(
            selectorCfUIInfo ->
                SelectorCfInfo.builder()
                    .selectorCfValue(selectorCfUIInfo.getSelectorCfValue())
                    .displayName(selectorCfUIInfo.getDisplayName())
                    .costItinerary(
                        fetchItineraryName(
                            orgId,
                            costType,
                            costTypeDtoInfo.getSelectorCf(),
                            !selectorCfUIInfo.getSelectorCfValue().equals(EMPTY_STRING)
                                ? selectorCfUIInfo.getSelectorCfValue()
                                : null))
                    .row(getRowColumnCostFactorDescription(orgId, selectorCfUIInfo.getRow()))
                    .column(getRowColumnCostFactorDescription(orgId, selectorCfUIInfo.getColumn()))
                    .costFactors(getFilterCostFactorDescription(selectorCfUIInfo.getCostFactors()))
                    .build())
        .collect(Collectors.toList());
  }

  private String fetchItineraryName(
      String orgId, String costType, String selectCf, String selectCfValue) {
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        getCreatedCostItineraryForSelectCf(orgId, costType, selectCf, selectCfValue);
    return ObjectUtils.isEmpty(costItineraryAndFactorsEntity)
        ? EMPTY_STRING
        : costItineraryAndFactorsEntity.getCostItinerary();
  }

  private List<CostFactorDescriptionDto> getFilterCostFactorDescription(
      List<CostFactorUIValues> costFactorUIValues) {
    return costFactorUIValues.stream()
        .map(
            costFactorUIValue ->
                CostFactorDescriptionDto.builder()
                    .costFactor(costFactorUIValue.getCostFactor())
                    .uom(costFactorUIValue.getUom())
                    .displayName(costFactorUIValue.getDisplayName())
                    .values(
                        costFactorUIValue.getValues().stream()
                            .map(CostFactorValueDto::getValue)
                            .toList())
                    .build())
        .toList();
  }

  private CostFactorDescriptionDto getRowColumnCostFactorDescription(
      String orgId, RowColumnDto rowColumnDto) {
    if (!ObjectUtils.isEmpty(rowColumnDto)) {
      CostFactorEntity costFactorEntity =
          getCostFactorEntityForCostFactor(orgId, rowColumnDto.getCostFactor());
      return CostFactorDescriptionDto.builder()
          .costFactor(rowColumnDto.getCostFactor())
          .displayName(rowColumnDto.getDisplayName())
          .uom(rowColumnDto.getUom())
          .values(
              (ObjectUtils.isEmpty(costFactorEntity)
                      || ObjectUtils.isEmpty(costFactorEntity.getValues())
                  ? List.of()
                  : Arrays.stream(costFactorEntity.getValues().split(COMMA_DELIMITER)).toList()))
          .build();
    }
    return null;
  }

  private boolean getIsRateCardLookupRequiredForItineraryOfCostType(
      String orgId, String costFactors) {
    List<String> costFactorsList = Arrays.stream(costFactors.split(",")).toList();
    List<CostFactorEntity> costFactorEntityList =
        costFactorRepository.findByOrgIdAndCostFactorIn(orgId, costFactorsList);
    return costFactorEntityList.stream()
        .noneMatch(
            costFactorEntity ->
                Boolean.FALSE.equals(costFactorEntity.getIsRateCardLookUpRequired()));
  }
}
