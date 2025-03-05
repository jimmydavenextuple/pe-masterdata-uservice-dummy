/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.SelectorAndCostItineraryMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.SelectorAndCostItineraryMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.inbound.SelectorAndCostItineraryMappingRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateSelectorAndCostItineraryMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectorAndCostItineraryMappingService {

  private static final Logger logger =
      LoggerFactory.getLogger(SelectorAndCostItineraryMappingService.class);

  private static final SelectorAndCostItineraryMapper INSTANCE =
      Mappers.getMapper(SelectorAndCostItineraryMapper.class);
  private static final String SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION =
      "Selector Itinerary Mapping not found with given details";

  private static final String COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION =
      "Cost Itinerary and factors not found with given details";
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  public static final String COST_ITINERARY = "costItinerary";
  public static final String COST_TYPE = "costType";
  public static final String SELECTOR_CF = "selectorCf";
  public static final String SELECTOR_CF_VALUE = "selectorCfValue";

  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;

  private final TenantCostTypeRepository tenantCostTypeRepository;

  private final PreferenceSelectorRepository preferenceSelectorRepository;
  private final CostFactorRepository costFactorRepository;

  @Transactional
  public SelectorAndCostItineraryMappingResponse createSelectorAndCostItineraryMapping(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    validationIfSelectorCfIsNull(orgId, selectorAndCostItineraryMappingRequest);
    validationIfSelectorCfIsEmptyString(orgId, selectorAndCostItineraryMappingRequest);
    validationIfSelectorCfValueIsEmptyString(orgId, selectorAndCostItineraryMappingRequest);
    validationSelectorCf(orgId, selectorAndCostItineraryMappingRequest);
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        validateCostItinerary(orgId, selectorAndCostItineraryMappingRequest.getCostItinerary());
    requestValidations(
        orgId,
        selectorAndCostItineraryMappingRequest.getCostType(),
        selectorAndCostItineraryMappingRequest.getSelectorCf());
    validationsOnSelectorCf(
        orgId, selectorAndCostItineraryMappingRequest, costItineraryAndFactorsEntity);
    validationsOnSelectorCfValue(orgId, selectorAndCostItineraryMappingRequest);
    validateCostItineraryStatus(
        orgId,
        costItineraryAndFactorsEntity.getCostItinerary(),
        costItineraryAndFactorsEntity.getItineraryStatus());
    updateIsActiveStatusForDefaultItinerary(
        selectorAndCostItineraryMappingRequest.getSelectorCfValue(), costItineraryAndFactorsEntity);
    var selectorAndCostItineraryMappingEntity =
        INSTANCE.toSelectorAndCostItineraryMappingEntity(selectorAndCostItineraryMappingRequest);
    selectorAndCostItineraryMappingEntity.setOrgId(orgId);
    return INSTANCE.toSelectorAndCostItineraryMappingResponse(
        selectorAndCostItineraryMappingRepository.save(selectorAndCostItineraryMappingEntity));
  }

  private void validationSelectorCf(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    Optional<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findFirstByOrgIdAndCostType(
            orgId, selectorAndCostItineraryMappingRequest.getCostType());

    String selectorCf = selectorAndCostItineraryMappingRequest.getSelectorCf();
    if (Objects.nonNull(selectorCf)
        && (preferenceSelectorEntity.isEmpty()
            || !selectorCf.equals(preferenceSelectorEntity.get().getSelectorCf()))) {
      log.error(
          "Selector is invalid for given orgId: {} and cost type: {}.",
          orgId,
          selectorAndCostItineraryMappingRequest.getCostType());
      throw new CommonServiceException(
          "Selector is invalid for given orgId and cost type.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              SELECTOR_CF,
              FieldError.builder().rejectedValue(selectorCf).build()));
    }
  }

  private CostItineraryAndFactorsEntity validateCostItinerary(String orgId, String costItinerary)
      throws CommonServiceException {
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
    if (costItineraryAndFactorsEntity.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Cost Itinerary provided in the request is not valid",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    return costItineraryAndFactorsEntity.get();
  }

  private void requestValidations(String orgId, String costType, String selectorCf)
      throws CommonServiceException {
    Boolean isInValidRequest = Boolean.FALSE;
    Map<String, FieldError> errorMap = new HashMap<>();
    if (Boolean.TRUE.equals(isInvalidCostType(orgId, costType))) {
      errorMap.put(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      isInValidRequest = Boolean.TRUE;
    }
    if (StringUtils.hasLength(selectorCf)
        && Boolean.TRUE.equals(isInvalidSelectorCf(orgId, selectorCf))) {
      errorMap.put(SELECTOR_CF, FieldError.builder().rejectedValue(selectorCf).build());
      isInValidRequest = Boolean.TRUE;
    }
    if (Boolean.TRUE.equals(isInValidRequest)) {
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Selector And CostItinerary Mapping Request is not valid.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validationsOnSelectorCf(
      String orgId,
      SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest,
      CostItineraryAndFactorsEntity costItineraryAndFactorsEntity)
      throws CommonServiceException {
    if (StringUtils.hasLength(selectorAndCostItineraryMappingRequest.getSelectorCf())) {
      validateSelectorAndCostItinerary(
          orgId,
          selectorAndCostItineraryMappingRequest.getSelectorCf(),
          selectorAndCostItineraryMappingRequest.getCostItinerary(),
          costItineraryAndFactorsEntity.getCostFactors());
      if (Objects.isNull(selectorAndCostItineraryMappingRequest.getSelectorCfValue()))
        validateDefaultItineraryWhereSelectorCfIsNonNull(
            orgId, selectorAndCostItineraryMappingRequest);
    } else {
      validateCostTypeAndItineraryAssociation(
          orgId, selectorAndCostItineraryMappingRequest.getCostType());
    }
  }

  private void validateDefaultItineraryWhereSelectorCfIsNonNull(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository
            .findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
                orgId,
                selectorAndCostItineraryMappingRequest.getCostType(),
                selectorAndCostItineraryMappingRequest.getSelectorCf(),
                selectorAndCostItineraryMappingRequest.getSelectorCfValue());
    if (!selectorAndCostItineraryMappingEntity.isEmpty()) {
      throw new CommonServiceException(
          "An itinerary is already associated with given cost type with valid selector and null selector value",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              SELECTOR_CF,
              FieldError.builder()
                  .rejectedValue(selectorAndCostItineraryMappingRequest.getSelectorCf())
                  .build(),
              COST_TYPE,
              FieldError.builder()
                  .rejectedValue(selectorAndCostItineraryMappingRequest.getCostType())
                  .build()));
    }
  }

  private void validationsOnSelectorCfValue(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    if (StringUtils.hasLength(selectorAndCostItineraryMappingRequest.getSelectorCfValue())) {
      validateSelectorCfValueAssociation(
          orgId,
          selectorAndCostItineraryMappingRequest.getSelectorCfValue(),
          selectorAndCostItineraryMappingRequest.getCostType());
      validateSelectorCfValue(
          orgId,
          selectorAndCostItineraryMappingRequest.getSelectorCf(),
          selectorAndCostItineraryMappingRequest.getSelectorCfValue());
    }
  }

  private void validationIfSelectorCfIsEmptyString(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    if (Objects.nonNull(selectorAndCostItineraryMappingRequest.getSelectorCf())
        && (selectorAndCostItineraryMappingRequest.getSelectorCf().isEmpty())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SELECTOR_CF,
          FieldError.builder()
              .rejectedValue(selectorAndCostItineraryMappingRequest.getSelectorCf())
              .build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Selector should be null or a valid string", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private void validationIfSelectorCfValueIsEmptyString(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    if (Objects.nonNull(selectorAndCostItineraryMappingRequest.getSelectorCfValue())
        && (selectorAndCostItineraryMappingRequest.getSelectorCfValue().isEmpty())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SELECTOR_CF_VALUE,
          FieldError.builder()
              .rejectedValue(selectorAndCostItineraryMappingRequest.getSelectorCfValue())
              .build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Selector value should be null or one of the values defined for selector",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validationIfSelectorCfIsNull(
      String orgId, SelectorAndCostItineraryMappingRequest selectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    if (Objects.isNull(selectorAndCostItineraryMappingRequest.getSelectorCf())
        && (Objects.nonNull(selectorAndCostItineraryMappingRequest.getSelectorCfValue()))) {
      throw new CommonServiceException(
          "Allowed value for selector should be null when selector is null",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              SELECTOR_CF_VALUE,
              FieldError.builder()
                  .rejectedValue(selectorAndCostItineraryMappingRequest.getSelectorCfValue())
                  .build()));
    }
  }

  private Boolean isInvalidSelectorCf(String orgId, String selectorCf) {
    return preferenceSelectorRepository.findByOrgIdAndSelectorCf(orgId, selectorCf).isEmpty();
  }

  private Boolean isInvalidCostType(String orgId, String costType) {
    return tenantCostTypeRepository.findByOrgIdAndCostType(orgId, costType).isEmpty();
  }

  private void validateSelectorCfValue(String orgId, String selectorCf, String selectorCfValue)
      throws CommonServiceException {
    if (StringUtils.hasLength(selectorCfValue)) {
      List<CostFactorEntity> costFactorEntity =
          costFactorRepository.findByOrgIdAndCostFactor(orgId, selectorCf);
      String[] possibleValuesOfSelectorCf = costFactorEntity.get(0).getValues().split(",");
      boolean isSelectorCfValueValid =
          Arrays.asList(possibleValuesOfSelectorCf).contains(selectorCfValue);
      if (!isSelectorCfValueValid) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(SELECTOR_CF, FieldError.builder().rejectedValue(selectorCf).build());
        errorMap.put(
            SELECTOR_CF_VALUE, FieldError.builder().rejectedValue(selectorCfValue).build());
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        throw new CommonServiceException(
            "Selector value in the request is not part of selector's possible values",
            HttpStatus.BAD_REQUEST,
            0x1771,
            errorMap);
      }
    }
  }

  private void validateSelectorAndCostItinerary(
      String orgId, String selectorCf, String costItinerary, String costFactorsOfCostItinerary)
      throws CommonServiceException {
    String[] costFactorsListOfCostItinerary = costFactorsOfCostItinerary.split(",");
    boolean isSelectorPartOfCostItinerary =
        Arrays.asList(costFactorsListOfCostItinerary).contains(selectorCf);
    if (!isSelectorPartOfCostItinerary) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SELECTOR_CF, FieldError.builder().rejectedValue(selectorCf).build());
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Selector is not part of cost factors associated with cost itinerary",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validateSelectorCfValueAssociation(
      String orgId, String selectorCfValue, String costType) throws CommonServiceException {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCfValueAndCostType(
            orgId, selectorCfValue, costType);
    if (!selectorAndCostItineraryMappingEntity.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SELECTOR_CF_VALUE, FieldError.builder().rejectedValue(selectorCfValue).build());
      throw new CommonServiceException(
          "Selector value is associated with an itinerary",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validateCostTypeAndItineraryAssociation(String orgId, String costType)
      throws CommonServiceException {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(orgId, costType);
    if (!selectorAndCostItineraryMappingEntity.isEmpty()) {
      throw new CommonServiceException(
          "Cost type is already associated with an itinerary",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_TYPE,
              FieldError.builder().rejectedValue(costType).build()));
    }
  }

  private void validateCostItineraryStatus(
      String orgId, String costItinerary, ItineraryStatusEnum costItineraryStatus)
      throws CommonServiceException {
    if (ItineraryStatusEnum.DRAFT.equals(costItineraryStatus)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      throw new CommonServiceException(
          "Can't associate itinerary with DRAFT status", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private void updateIsActiveStatusForDefaultItinerary(
      String selectorCfValue, CostItineraryAndFactorsEntity costItineraryAndFactorsEntity) {
    if (!StringUtils.hasLength(selectorCfValue)) {
      costItineraryAndFactorsEntity.setIsActive(true);
      costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity);
    }
  }

  public SelectorAndCostItineraryMappingResponse getSelectorAndCostItineraryMapping(
      String orgId, Long id) throws CommonServiceException {

    var selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository.findByIdAndOrgId(id, orgId);
    handleEmptySelectorAndCostItineraryMappingEntity(
        id, orgId, selectorAndCostItineraryMappingEntity);
    return INSTANCE.toSelectorAndCostItineraryMappingResponse(
        selectorAndCostItineraryMappingEntity.get());
  }

  public List<SelectorAndCostItineraryMappingResponse>
      getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
          String orgId, String selectorCf, String costType) throws CommonServiceException {

    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntityList;
    if (selectorCf != null) {
      selectorAndCostItineraryMappingEntityList =
          selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCfAndCostType(
              orgId, selectorCf, costType);
    } else {
      selectorAndCostItineraryMappingEntityList =
          selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(orgId, costType);
    }
    handleEmptySelectorAndCostItineraryMappingEntityList(
        orgId, selectorCf, costType, selectorAndCostItineraryMappingEntityList);
    return INSTANCE.toSelectorAndCostItineraryMappingListResponse(
        selectorAndCostItineraryMappingEntityList);
  }

  public SelectorAndCostItineraryMappingResponse deleteSelectorAndCostItineraryMapping(
      String orgId, Long id) throws CommonServiceException {
    logger.debug(
        "-- inside deleteSelectorAndCostItineraryMapping service for orgId {} and Id {} --",
        orgId,
        id);
    var selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository.findByIdAndOrgId(id, orgId);
    handleEmptySelectorAndCostItineraryMappingEntity(
        id, orgId, selectorAndCostItineraryMappingEntity);
    selectorAndCostItineraryMappingRepository.delete(selectorAndCostItineraryMappingEntity.get());
    return INSTANCE.toSelectorAndCostItineraryMappingResponse(
        selectorAndCostItineraryMappingEntity.get());
  }

  private static void handleEmptySelectorAndCostItineraryMappingEntity(
      Long id,
      String orgId,
      Optional<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryEntity)
      throws CommonServiceException {
    if (selectorAndCostItineraryEntity.isEmpty()) {
      logger.error(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private void handleEmptySelectorAndCostItineraryMappingEntityList(
      String orgId,
      String selectorCf,
      String costType,
      List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntityList)
      throws CommonServiceException {
    if (selectorAndCostItineraryMappingEntityList.isEmpty()) {
      logger.error(SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SELECTOR_CF, FieldError.builder().rejectedValue(selectorCf).build());
      errorMap.put(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      throw new CommonServiceException(
          SELECTOR_ITINERARY_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  public List<SelectorAndCostItineraryMappingCacheKeyDto> getAllSelectorAndCostItineraryCacheKeys(
      Integer limit) {
    return INSTANCE.toSelectorAndCostItineraryCacheKeyResponseList(
        selectorAndCostItineraryMappingRepository.findAllSelectorAndCostItineraryMappingEntities(
            limit));
  }

  public SelectorAndCostItineraryMappingResponse updateSelectorAndCostItineraryMappingByIdAndOrgId(
      Long id,
      String orgId,
      UpdateSelectorAndCostItineraryMappingRequest updateSelectorAndCostItineraryMappingRequest)
      throws CommonServiceException {
    Optional<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntity =
        selectorAndCostItineraryMappingRepository.findByIdAndOrgId(id, orgId);
    handleEmptySelectorAndCostItineraryMappingEntity(
        id, orgId, selectorAndCostItineraryMappingEntity);
    String costItinerary = updateSelectorAndCostItineraryMappingRequest.getCostItinerary();
    validateCostItineraryStatusForCreatedStatus(orgId, costItinerary);
    selectorAndCostItineraryMappingEntity
        .get()
        .setCostItinerary(updateSelectorAndCostItineraryMappingRequest.getCostItinerary());
    selectorAndCostItineraryMappingEntity
            .get()
            .setCustomAttributes(updateSelectorAndCostItineraryMappingRequest.getCustomAttributes());
    return INSTANCE.toSelectorAndCostItineraryMappingResponse(
        selectorAndCostItineraryMappingRepository.save(
            selectorAndCostItineraryMappingEntity.get()));
  }

  private void validateCostItineraryStatusForCreatedStatus(String orgId, String costItinerary)
      throws CommonServiceException {
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
    handleEmptyCostItineraryAndFactorsEntity(orgId, costItinerary, costItineraryAndFactorsEntity);
    ItineraryStatusEnum itineraryStatus = costItineraryAndFactorsEntity.get().getItineraryStatus();
    validateCostItineraryStatus(orgId, costItinerary, itineraryStatus);
  }

  private void handleEmptyCostItineraryAndFactorsEntity(
      String orgId,
      String costItinerary,
      Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity)
      throws CommonServiceException {
    if (costItineraryAndFactorsEntity.isEmpty()) {
      logger.error(COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION);
      throw new CommonServiceException(
          COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION,
          HttpStatus.NOT_FOUND,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }
}
