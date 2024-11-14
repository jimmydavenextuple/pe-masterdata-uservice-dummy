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
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostFactorDiscreteBucketMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorDiscreteBucketRequest;
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
public class CostFactorDiscreteBucketServiceImpl implements CostFactorDiscreteBucketService {

  private final CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  private final BucketValidationService bucketValidationService;

  private final CostFactorBucketTypeRepository costFactorBucketTypeRepository;

  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "Bucket type not found for orgId and Cost Factor combination.";

  private static final String BUCKET_NOT_FOUND =
      "Buckets not found for orgId, Cost Factor combination.";

  private static final String BUCKET_BY_ID_NOT_FOUND = "Bucket not found for given id and orgId.";
  private static final String COST_FACTOR_ORG_ID_NOTATION_DUPLICATE =
      "OrgId, Cost Factor and notation combination should be unique.";
  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";
  private static final String DISCRETE_BUCKET_TYPE_EXCEPTION =
      "Bucket type of cost factor should be DISCRETE";
  private static final String COST_FACTOR = "costFactor";
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  private static final String NOTATION = "notation";
  private static final CostFactorDiscreteBucketMapper INSTANCE =
      Mappers.getMapper(CostFactorDiscreteBucketMapper.class);

  @Override
  @Transactional
  public CostFactorDiscreteBucketDto createCostFactorDiscreteBucket(
      String orgId, CostFactorDiscreteBucketRequest costFactorDiscreteBucketRequest)
      throws CommonServiceException {
    bucketValidationService.validateOrgIdAndCostFactor(
        orgId, costFactorDiscreteBucketRequest.getCostFactor());
    validateDiscreteBucketType(orgId, costFactorDiscreteBucketRequest.getCostFactor());
    validateOrgIdCostFactorAndNotationUnique(
        orgId,
        costFactorDiscreteBucketRequest.getCostFactor(),
        costFactorDiscreteBucketRequest.getNotation());
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorDiscreteBucketRequest.getCostFactor());
    CostFactorDiscreteBucketEntity entity =
        CostFactorDiscreteBucketEntity.builder()
            .orgId(orgId)
            .costFactor(costFactorDiscreteBucketRequest.getCostFactor())
            .notation(costFactorDiscreteBucketRequest.getNotation())
            .notationDisplayName(costFactorDiscreteBucketRequest.getNotationDisplayName())
            .valueList(costFactorDiscreteBucketRequest.getValueList())
            .build();
    return INSTANCE.convertCostFactorDiscreteBucketEntityToDto(
        costFactorDiscreteBucketRepository.save(entity));
  }

  @Override
  public List<CostFactorDiscreteBucketDto> getCostFactorDiscreteBucket(
      String orgId, String costFactor) throws CommonServiceException {
    List<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntityList =
        costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    handleBucketNotFound(orgId, costFactor, costFactorDiscreteBucketEntityList);
    return INSTANCE.convertCostFactorDiscreteBucketEntityListTODtoList(
        costFactorDiscreteBucketEntityList);
  }

  private void validateDiscreteBucketType(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntity =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (!BucketTypeEnum.DISCRETE.equals(costFactorBucketTypeEntity.get().getBucketType())) {
      log.error(DISCRETE_BUCKET_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          DISCRETE_BUCKET_TYPE_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private void handleBucketNotFound(
      String orgId,
      String costFactor,
      List<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntityOptional)
      throws CommonServiceException {
    if (costFactorDiscreteBucketEntityOptional.isEmpty()) {
      log.error(BUCKET_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(BUCKET_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private void handleBucketNotFound(
      Long id,
      String orgId,
      Optional<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntityOptional)
      throws CommonServiceException {
    if (costFactorDiscreteBucketEntityOptional.isEmpty()) {
      log.error(BUCKET_BY_ID_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          BUCKET_BY_ID_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  @Override
  public CostFactorDiscreteBucketDto updateCostFactorDiscreteBucket(
      Long id, String orgId, CostFactorDiscreteBucketRequest costFactorDiscreteBucketRequest)
      throws CommonServiceException {
    Optional<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntityOptional =
        costFactorDiscreteBucketRepository.findByIdAndOrgId(id, orgId);
    handleBucketNotFound(id, orgId, costFactorDiscreteBucketEntityOptional);
    bucketValidationService.validateOrgIdAndCostFactor(
        orgId, costFactorDiscreteBucketRequest.getCostFactor());
    validateDiscreteBucketType(orgId, costFactorDiscreteBucketRequest.getCostFactor());
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorDiscreteBucketEntityOptional.get().getCostFactor());
    CostFactorDiscreteBucketEntity costFactorDiscreteBucketEntity =
        INSTANCE.convertCostFactorDiscreteBucketRequestToEntity(costFactorDiscreteBucketRequest);
    costFactorDiscreteBucketEntity.setOrgId(orgId);
    costFactorDiscreteBucketEntity.setId(id);
    return INSTANCE.convertCostFactorDiscreteBucketEntityToDto(
        costFactorDiscreteBucketRepository.save(costFactorDiscreteBucketEntity));
  }

  @Override
  public CostFactorDiscreteBucketDto deleteCostFactorDiscreteBucket(Long id, String orgId)
      throws CommonServiceException {
    Optional<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntityOptional =
        costFactorDiscreteBucketRepository.findByIdAndOrgId(id, orgId);
    handleBucketNotFound(id, orgId, costFactorDiscreteBucketEntityOptional);
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorDiscreteBucketEntityOptional.get().getCostFactor());
    costFactorDiscreteBucketRepository.delete(costFactorDiscreteBucketEntityOptional.get());
    return INSTANCE.convertCostFactorDiscreteBucketEntityToDto(
        costFactorDiscreteBucketEntityOptional.get());
  }

  private void validateOrgIdCostFactorAndNotationUnique(
      String orgId, String costFactor, String notation) throws CommonServiceException {
    Optional<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntity =
        costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            orgId, costFactor, notation);
    if (costFactorDiscreteBucketEntity.isPresent()) {
      log.error(COST_FACTOR_ORG_ID_NOTATION_DUPLICATE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      errorMap.put(NOTATION, FieldError.builder().rejectedValue(notation).build());
      throw new CommonServiceException(
          COST_FACTOR_ORG_ID_NOTATION_DUPLICATE, HttpStatus.CONFLICT, 0x1771, errorMap);
    }
  }

  public List<CostFactorDiscreteBucketCacheKeyDto> getCostFactorDiscreteBucketCacheKeys(
      Integer limit) {
    var costFactorDiscreteBucketEntities =
        costFactorDiscreteBucketRepository.findAllCostFactorDiscreteBucketEntities(limit);

    return INSTANCE.toCostFactorDiscreteBucketCacheKeyResponseList(
        costFactorDiscreteBucketEntities);
  }
}
