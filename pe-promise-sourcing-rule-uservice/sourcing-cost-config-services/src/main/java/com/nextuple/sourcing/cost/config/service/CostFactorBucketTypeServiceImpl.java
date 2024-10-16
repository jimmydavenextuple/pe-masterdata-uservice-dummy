/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostFactorBucketTypeMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorBucketTypeRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateCostFactorBucketTypeRequest;
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
public class CostFactorBucketTypeServiceImpl implements CostFactorBucketTypeService {
  public static final String UPDATE_EXISTING_BUCKET_TYPE_EXCEPTION =
      "Can't update bucket type as values are assigned to existing bucket type";
  private final CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  private final CostFactorRepository costFactorRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final BucketValidationService bucketValidationService;
  private final CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  private final CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  private static final String COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE = "Cost Factor not found.";
  private static final String COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE =
      "Cost factor bucket type cannot be created as rate card lookup is disabled for cost factor.";
  private static final String COST_FACTOR_ORG_ID_DUPLICATE =
      "OrgId and Cost Factor combination should be unique.";
  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "OrgId and Cost Factor combination not found.";
  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";
  private static final String COST_FACTOR = "costFactor";
  private static final String ORG_ID = "orgId";
  private static final String EXISTING_BUCKET_TYPE = "existingBucketType";

  private static final String NEW_BUCKET_TYPE = "newBucketType";

  private static final CostFactorBucketTypeMapper INSTANCE =
      Mappers.getMapper(CostFactorBucketTypeMapper.class);

  @Transactional
  @Override
  public CostFactorBucketTypeDto createCostFactorBucketType(
      String orgId, CostFactorBucketTypeRequest costFactorBucketTypeRequest)
      throws CommonServiceException {
    validateCostFactor(orgId, costFactorBucketTypeRequest.getCostFactor());
    validateCostFactorOrgIdCombination(orgId, costFactorBucketTypeRequest.getCostFactor());
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorBucketTypeRequest.getCostFactor());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        INSTANCE.convertRequestToCostFactorBucketTypeEntity(costFactorBucketTypeRequest);
    costFactorBucketTypeEntity.setOrgId(orgId);
    return INSTANCE.convertCostTypeBucketEntityToCostTypeBucketDto(
        costFactorBucketTypeRepository.save(costFactorBucketTypeEntity));
  }

  private void validateCostFactor(String orgId, String costFactor) throws CommonServiceException {
    List<CostFactorEntity> costFactorEntityList =
        costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (costFactorEntityList.isEmpty()) {
      log.error(COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          errorMap);
    }
    validateCostFactorIsRateCardLookUp(orgId, costFactor, costFactorEntityList);
  }

  private static void validateCostFactorIsRateCardLookUp(
      String orgId, String costFactor, List<CostFactorEntity> costFactorEntityList)
      throws CommonServiceException {
    CostFactorEntity costFactorEntity = costFactorEntityList.getFirst();
    if (Boolean.FALSE.equals(costFactorEntity.getIsRateCardLookUpRequired())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE,
          HttpStatus.PRECONDITION_FAILED,
          0x1783,
          errorMap);
    }
  }

  private void validateCostFactorOrgIdCombination(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (costFactorBucketTypeOptional.isPresent()) {
      log.error(COST_FACTOR_ORG_ID_DUPLICATE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_ORG_ID_DUPLICATE, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
    }
  }

  @Override
  @Transactional
  public CostFactorBucketTypeDto getCostFactorBucketType(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntityOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    handleOrgIdCostFactorCombinationNotFound(orgId, costFactor, costFactorBucketTypeEntityOptional);
    return INSTANCE.convertCostTypeBucketEntityToCostTypeBucketDto(
        costFactorBucketTypeEntityOptional.get());
  }

  private void handleOrgIdCostFactorCombinationNotFound(
      String orgId,
      String costFactor,
      Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntityOptional)
      throws CommonServiceException {
    if (costFactorBucketTypeEntityOptional.isEmpty()) {
      log.error(COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  @Override
  @Transactional
  public CostFactorBucketTypeDto updateCostFactorBucketType(
      String orgId,
      String costFactor,
      UpdateCostFactorBucketTypeRequest updateCostFactorBucketTypeRequest)
      throws CommonServiceException {
    validateCostFactor(orgId, costFactor);
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntityOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    handleOrgIdCostFactorCombinationNotFound(orgId, costFactor, costFactorBucketTypeEntityOptional);
    bucketValidationService.validateCostItineraryStatus(orgId, costFactor);
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        costFactorBucketTypeEntityOptional.get();
    BucketTypeEnum existingBucketType = costFactorBucketTypeEntity.getBucketType();
    validateBucketTypeUpdate(
        updateCostFactorBucketTypeRequest.getBucketType(), existingBucketType, orgId, costFactor);
    costFactorBucketTypeEntity.setBucketType(updateCostFactorBucketTypeRequest.getBucketType());
    return INSTANCE.convertCostTypeBucketEntityToCostTypeBucketDto(
        costFactorBucketTypeRepository.save(costFactorBucketTypeEntity));
  }

  private void validateBucketTypeUpdate(
      BucketTypeEnum newBucketType,
      BucketTypeEnum existingBucketType,
      String orgId,
      String costFactor)
      throws CommonServiceException {
    if (hasExceptionForBucketType(existingBucketType, orgId, costFactor)) {
      log.error(UPDATE_EXISTING_BUCKET_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      errorMap.put(
          EXISTING_BUCKET_TYPE, FieldError.builder().rejectedValue(existingBucketType).build());
      errorMap.put(NEW_BUCKET_TYPE, FieldError.builder().rejectedValue(newBucketType).build());
      throw new CommonServiceException(
          UPDATE_EXISTING_BUCKET_TYPE_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private boolean hasExceptionForBucketType(
      BucketTypeEnum existingBucketType, String orgId, String costFactor) {
    if (BucketTypeEnum.CONTIGUOUS.equals(existingBucketType)) {
      return !costFactorContiguousBucketRepository
          .findByOrgIdAndCostFactor(orgId, costFactor)
          .isEmpty();

    } else if (BucketTypeEnum.DISCRETE.equals(existingBucketType)) {

      return !costFactorDiscreteBucketRepository
          .findByOrgIdAndCostFactor(orgId, costFactor)
          .isEmpty();
    }
    return false;
  }

  @Override
  @Transactional
  public CostFactorBucketTypeDto deleteCostFactorBucketType(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntityOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    handleOrgIdCostFactorCombinationNotFound(orgId, costFactor, costFactorBucketTypeEntityOptional);
    bucketValidationService.validateCostItineraryStatus(orgId, costFactor);
    costFactorBucketTypeRepository.delete(costFactorBucketTypeEntityOptional.get());
    return INSTANCE.convertCostTypeBucketEntityToCostTypeBucketDto(
        costFactorBucketTypeEntityOptional.get());
  }

  public List<CostFactorBucketTypeCacheKeyDto> getCostFactorBucketTypeCacheKeys(Integer limit) {
    var costFactorBucketTypeEntities =
        costFactorBucketTypeRepository.findAllCostFactorBucketTypeEntities(limit);

    return INSTANCE.toCostFactorBucketTypeCacheKeyResponseList(costFactorBucketTypeEntities);
  }
}
