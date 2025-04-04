/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.PreferenceSelectorMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.PreferenceSelectorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.inbound.CreatePreferenceSelectorRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdatePreferenceSelectorRequest;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenceSelectorService {

  public static final String ORG_ID = "orgId";
  public static final String COST_TYPE = "costType";
  private final PreferenceSelectorRepository preferenceSelectorRepository;
  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  private final TenantCostTypeRepository tenantCostTypeRepository;
  private final CostFactorRepository costFactorRepository;
  private static final PreferenceSelectorMapper INSTANCE =
      Mappers.getMapper(PreferenceSelectorMapper.class);
  private static final String PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE =
      "Preference Selector not found";
  private static final String INVALID_SELECTOR_CF_AS_COST_FACTOR =
      "selectorCf is not a valid cost factor";
  private static final String SELECTOR_ID = "selectorId";
  private static final String SELECTOR_CF = "selectorCf";

  public PreferenceSelectorDto convertToPreferenceSelectorDto(
      PreferenceSelectorEntity preferenceSelectorEntity) {
    return INSTANCE.toPreferenceSelectorDto(preferenceSelectorEntity);
  }

  @Transactional
  public PreferenceSelectorDto createPreferenceSelector(
      String orgId, CreatePreferenceSelectorRequest createPreferenceSelectorRequest)
      throws CommonServiceException {
    log.debug("-- inside createPreferenceSelector service --");

    validatePreferenceSelector(orgId, createPreferenceSelectorRequest.getCostType());
    validateSelectorCfAsCostFactor(orgId, createPreferenceSelectorRequest.getSelectorCf());
    if (isInvalidCostType(orgId, createPreferenceSelectorRequest.getCostType())) {
      log.error("Invalid cost type for orgId: {}", orgId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          COST_TYPE,
          FieldError.builder()
              .rejectedValue(createPreferenceSelectorRequest.getCostType())
              .build());
      throw new CommonServiceException(
          "Invalid cost type for given orgId.", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    var preferenceSelector = INSTANCE.toPreferenceSelectorEntity(createPreferenceSelectorRequest);
    preferenceSelector.setOrgId(orgId);
    return convertToPreferenceSelectorDto(preferenceSelectorRepository.save(preferenceSelector));
  }

  private void validatePreferenceSelector(String orgId, String costType)
      throws CommonServiceException {
    Optional<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findFirstByOrgIdAndCostType(orgId, costType);
    if (preferenceSelectorEntity.isPresent()) {
      log.error(
          "Preference selector already exist for orgId: {} and costType: {}", orgId, costType);
      throw new CommonServiceException(
          "Preference selector already exist for orgId and costType",
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_TYPE,
              FieldError.builder().rejectedValue(costType).build()));
    }
  }

  private void validateSelectorCfAsCostFactor(String orgId, String selectorCf)
      throws CommonServiceException {
    Optional<CostFactorEntity> costFactorEntity =
        costFactorRepository.findFirstByOrgIdAndCostFactor(orgId, selectorCf);
    if (costFactorEntity.isEmpty()) {
      log.error(INVALID_SELECTOR_CF_AS_COST_FACTOR);
      throw new CommonServiceException(
          INVALID_SELECTOR_CF_AS_COST_FACTOR,
          HttpStatus.NOT_FOUND,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              SELECTOR_CF,
              FieldError.builder().rejectedValue(selectorCf).build()));
    }
  }

  public PreferenceSelectorDto findByOrgIdAndPreferenceSelectorId(String orgId, Long selectorId)
      throws CommonServiceException {
    log.debug("-- inside findByOrgIdAndPreferenceSelectorId service --");
    Optional<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findByIdAndOrgId(selectorId, orgId);
    if (preferenceSelectorEntity.isEmpty()) {
      log.error(PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SELECTOR_ID, FieldError.builder().rejectedValue(selectorId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return convertToPreferenceSelectorDto(preferenceSelectorEntity.get());
  }

  public PreferenceSelectorDto findByOrgIdAndPreferenceCostType(String orgId, String costType)
      throws CommonServiceException {
    log.debug("-- inside findByOrgIdAndPreferenceCostType service --");
    List<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findByOrgIdAndCostType(orgId, costType);
    if (preferenceSelectorEntity.isEmpty()) {
      log.error(PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_TYPE, FieldError.builder().rejectedValue(costType).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return convertToPreferenceSelectorDto(preferenceSelectorEntity.get(0));
  }

  @Transactional
  public PreferenceSelectorDto updatePreferenceSelector(
      Long selectorId,
      String orgId,
      UpdatePreferenceSelectorRequest updateCreatePreferenceSelectorRequest)
      throws CommonServiceException {
    log.debug("-- inside updatePreferenceSelector service --");
    Optional<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findByIdAndOrgId(selectorId, orgId);
    if (preferenceSelectorEntity.isEmpty()) {
      log.error(PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SELECTOR_ID, FieldError.builder().rejectedValue(selectorId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    validateSelectorCf(selectorId, orgId, preferenceSelectorEntity);
    validateSelectorCfAsCostFactor(orgId, updateCreatePreferenceSelectorRequest.getSelectorCf());
    preferenceSelectorEntity
        .get()
        .setSelectorCf(updateCreatePreferenceSelectorRequest.getSelectorCf());
    preferenceSelectorEntity
        .get()
        .setCustomAttributes(updateCreatePreferenceSelectorRequest.getCustomAttributes());
    return convertToPreferenceSelectorDto(
        preferenceSelectorRepository.save(preferenceSelectorEntity.get()));
  }

  @Transactional
  public PreferenceSelectorDto deletePreferenceSelector(Long selectorId, String orgId)
      throws CommonServiceException {
    log.debug("-- inside deletePreferenceSelector service --");
    Optional<PreferenceSelectorEntity> preferenceSelectorEntity =
        preferenceSelectorRepository.findByIdAndOrgId(selectorId, orgId);
    if (preferenceSelectorEntity.isEmpty()) {
      log.error(PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SELECTOR_ID, FieldError.builder().rejectedValue(selectorId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          PREFERENCE_SELECTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    validateSelectorCf(selectorId, orgId, preferenceSelectorEntity);
    preferenceSelectorRepository.delete(preferenceSelectorEntity.get());
    return convertToPreferenceSelectorDto(preferenceSelectorEntity.get());
  }

  private void validateSelectorCf(
      Long selectorId, String orgId, Optional<PreferenceSelectorEntity> preferenceSelectorEntity)
      throws CommonServiceException {
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntityList =
        selectorAndCostItineraryMappingRepository.findByOrgIdAndSelectorCf(
            preferenceSelectorEntity.get().getOrgId(),
            preferenceSelectorEntity.get().getSelectorCf());

    if (!selectorAndCostItineraryMappingEntityList.isEmpty()) {
      log.error(
          "Preference Selector is associated with one or more itinerary for preference selector id : {}",
          selectorId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SELECTOR_ID, FieldError.builder().rejectedValue(selectorId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Preference Selector is associated with one or more itinerary",
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          errorMap);
    }
  }

  private Boolean isInvalidCostType(String orgId, String costType) {
    return tenantCostTypeRepository.findByOrgIdAndCostType(orgId, costType).isEmpty();
  }

  public List<PreferenceSelectorCacheKeyDto> getAllPreferenceSelectorCacheKeys(Integer limit) {

    return INSTANCE.toPreferenceSelectorCacheKeyResponseList(
        preferenceSelectorRepository.findAllPreferenceSelectorEntities(limit));
  }
}
