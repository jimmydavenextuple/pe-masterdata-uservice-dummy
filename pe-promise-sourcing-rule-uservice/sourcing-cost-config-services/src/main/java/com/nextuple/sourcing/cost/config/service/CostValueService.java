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
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostValueEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostValueMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CreateCostValueRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateCostValueRequest;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CostValueService {

  public static final String ORG_ID = "orgId";
  public static final String COST_ITINERARY = "costItinerary";
  public static final String COST_FACTOR_COMBINATION_KEY = "costFactorCombinationKey";
  public static final String PREV_SLAB_VALUE = "prevSlabValue";
  public static final String COST_ITINERARY_NOT_FOUND = "Cost itinerary not found for given orgId";
  public static final String RATE_CARD_LOOK_UP_NOT_REQUIRED =
      "Cost value cannot be added as the rate card look up is not required for the given cost itinerary";
  public static final String COST_TABLE_ID = "costValueId";
  public static final String COST_TABLE_NOT_FOUND = "Cost value not found for given details";
  public static final String PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND =
      "Cost value not found for previous slab";
  private static final String COMMA_DELIMITER = ",";
  private final CostValueRepository costValueRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final CostFactorRepository costFactorRepository;

  private static final CostValueMapper INSTANCE = Mappers.getMapper(CostValueMapper.class);

  public CostValueResponse createCostValue(
      String orgId, CreateCostValueRequest createCostValueRequest) throws CommonServiceException {
    validateCostValueRequest(orgId, createCostValueRequest);
    Optional<CostValueEntity> existingCostValueEntity =
        costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            orgId,
            createCostValueRequest.getCostItinerary(),
            createCostValueRequest.getCostFactorCombinationKey());
    if (existingCostValueEntity.isPresent()) {
      validateCostValueOfPrevSlab(
          orgId,
          createCostValueRequest.getCostFactorCombinationKey(),
          createCostValueRequest.getCostItinerary(),
          createCostValueRequest.getPrevSlabValue());
      existingCostValueEntity.get().setCostValue(createCostValueRequest.getCostValue());
      existingCostValueEntity.get().setPrevSlabValue(createCostValueRequest.getPrevSlabValue());
      existingCostValueEntity
          .get()
          .setCustomAttributes(createCostValueRequest.getCustomAttributes());
      return INSTANCE.toCostValueResponse(costValueRepository.save(existingCostValueEntity.get()));
    }

    validateCostValueOfPrevSlab(
        orgId,
        createCostValueRequest.getCostFactorCombinationKey(),
        createCostValueRequest.getCostItinerary(),
        createCostValueRequest.getPrevSlabValue());
    CostValueEntity costValueEntity =
        INSTANCE.toCostValueEntityFromCreateCostValueRequest(createCostValueRequest);
    costValueEntity.setOrgId(orgId);
    return INSTANCE.toCostValueResponse(costValueRepository.save(costValueEntity));
  }

  public CostValueResponse getCostValue(String orgId, Long costValueId)
      throws CommonServiceException {
    CostValueEntity existingCostValueEntity =
        findCostValueEntityOrThrowException(orgId, costValueId);
    return INSTANCE.toCostValueResponse(existingCostValueEntity);
  }

  public CostValueResponse updateCostValue(
      Long costValueId, String orgId, UpdateCostValueRequest updateCostValueRequest)
      throws CommonServiceException {
    CostValueEntity costValueEntity = findCostValueEntityOrThrowException(orgId, costValueId);
    validateCostValueOfPrevSlab(
        orgId,
        costValueEntity.getCostFactorCombinationKey(),
        costValueEntity.getCostItinerary(),
        updateCostValueRequest.getPrevSlabValue());
    String prevSlabValue = updateCostValueRequest.getPrevSlabValue();
    costValueEntity.setCostValue(updateCostValueRequest.getCostValue());
    costValueEntity.setPrevSlabValue(
        (prevSlabValue != null && !prevSlabValue.isBlank()) ? prevSlabValue : null);
    return INSTANCE.toCostValueResponse(costValueRepository.save(costValueEntity));
  }

  public CostValueResponse deleteCostValue(String orgId, Long costValueId)
      throws CommonServiceException {
    CostValueEntity costValueEntity = findCostValueEntityOrThrowException(orgId, costValueId);
    costValueRepository.delete(costValueEntity);
    return INSTANCE.toCostValueResponse(costValueEntity);
  }

  private void validateCostValueRequest(String orgId, CreateCostValueRequest createCostValueRequest)
      throws CommonServiceException {
    String costItinerary = createCostValueRequest.getCostItinerary();
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
    validateCostItinerary(orgId, costItinerary, costItineraryAndFactorsEntity);
    validateForCostFactorRateCardLookUpValue(orgId, costItinerary, costItineraryAndFactorsEntity);
  }

  private void validateForCostFactorRateCardLookUpValue(
      String orgId,
      String costItinerary,
      Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity)
      throws CommonServiceException {
    if (costItineraryAndFactorsEntity.isPresent()
        && Objects.nonNull(costItineraryAndFactorsEntity.get().getCostFactors())) {
      String[] costFactors =
          costItineraryAndFactorsEntity.get().getCostFactors().split(COMMA_DELIMITER);
      CostFactorEntity costFactorEntity =
          costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactors[0]).getFirst();
      if (Boolean.FALSE.equals(costFactorEntity.getIsRateCardLookUpRequired()))
        throwCommonServiceException(RATE_CARD_LOOK_UP_NOT_REQUIRED, orgId, costItinerary, 0x1784);
    }
  }

  private void validateCostItinerary(
      String orgId,
      String costItinerary,
      Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity)
      throws CommonServiceException {
    if (costItineraryAndFactorsEntity.isEmpty())
      throwCommonServiceException(COST_ITINERARY_NOT_FOUND, orgId, costItinerary, 0x1771);
  }

  private void throwCommonServiceException(
      String errorMessage, String orgId, String costItinerary, int errorCode)
      throws CommonServiceException {
    log.error(errorMessage);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, errorCode, errorMap);
  }

  private CostValueEntity findCostValueEntityOrThrowException(String orgId, Long costValueId)
      throws CommonServiceException {
    Optional<CostValueEntity> existingCostValueEntity =
        costValueRepository.findByIdAndOrgId(costValueId, orgId);
    if (existingCostValueEntity.isEmpty()) {
      log.error(COST_TABLE_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_TABLE_ID, FieldError.builder().rejectedValue(costValueId).build());
      throw new CommonServiceException(
          COST_TABLE_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return existingCostValueEntity.get();
  }

  public CostValueResponse getCostValueForCostFactorCombinationKey(
      String orgId, String costItinerary, String costFactorCombinationKey)
      throws CommonServiceException {
    CostValueEntity existingCostValueEntity =
        getAndValidateExistingCostValueEntity(orgId, costItinerary, costFactorCombinationKey);
    return INSTANCE.toCostValueResponse(existingCostValueEntity);
  }

  public CostValueResponse deleteCostValueForCostFactorCombinationKey(
      String orgId, String costItinerary, String costFactorCombinationKey)
      throws CommonServiceException {
    CostValueEntity costValueEntity =
        getAndValidateExistingCostValueEntity(orgId, costItinerary, costFactorCombinationKey);
    costValueRepository.delete(costValueEntity);
    return INSTANCE.toCostValueResponse(costValueEntity);
  }

  private CostValueEntity getAndValidateExistingCostValueEntity(
      String orgId, String costItinerary, String costFactorCombinationKey)
      throws CommonServiceException {
    Optional<CostValueEntity> existingCostValueEntity =
        costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
            orgId, costItinerary, costFactorCombinationKey);
    if (existingCostValueEntity.isEmpty()) {
      log.error(COST_TABLE_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      errorMap.put(
          COST_FACTOR_COMBINATION_KEY,
          FieldError.builder().rejectedValue(costFactorCombinationKey).build());
      throw new CommonServiceException(
          COST_TABLE_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return existingCostValueEntity.get();
  }

  private void validateCostValueOfPrevSlab(
      String orgId, String costFactorCombinationKey, String costItinerary, String prevSlabValue)
      throws CommonServiceException {
    if (StringUtils.hasLength(prevSlabValue)) {
      Optional<CostValueEntity> prevSlabValueCostValueEntity =
          costValueRepository.findByOrgIdAndCostItineraryAndCostFactorCombinationKey(
              orgId, costItinerary, prevSlabValue);
      if (prevSlabValueCostValueEntity.isEmpty()) {
        log.error(PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND);
        throw new CommonServiceException(
            PREV_SLAB_VALUE_COST_VALUE_NOT_FOUND,
            HttpStatus.PRECONDITION_FAILED,
            0x1771,
            Map.of(
                ORG_ID,
                FieldError.builder().rejectedValue(orgId).build(),
                COST_FACTOR_COMBINATION_KEY,
                FieldError.builder().rejectedValue(costFactorCombinationKey).build(),
                PREV_SLAB_VALUE,
                FieldError.builder().rejectedValue(prevSlabValue).build()));
      }
    }
  }

  public List<CostValueCacheKeyDto> getCostValueCacheKeys(Integer limit) {
    var costValueEntities = costValueRepository.findAllCostValueEntities(limit);

    return INSTANCE.toCostValueCacheKeyResponseList(costValueEntities);
  }
}
