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
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorContiguousBucketEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostFactorContiguousBucketMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorContiguousBucketRequest;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CostFactorContiguousBucketServiceImpl implements CostFactorContiguousBucketService {

  private final CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
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
  private static final String CONTIGUOUS_BUCKET_TYPE_EXCEPTION =
      "Bucket type of cost factor should be CONTIGUOUS";
  private static final String COST_FACTOR = "costFactor";
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  private static final String NOTATION = "notation";
  private static final CostFactorContiguousBucketMapper INSTANCE =
      Mappers.getMapper(CostFactorContiguousBucketMapper.class);

  @Override
  public CostFactorContiguousBucketDto createCostFactorContiguousBucket(
      String orgId, CostFactorContiguousBucketRequest request) throws CommonServiceException {
    bucketValidationService.validateOrgIdAndCostFactor(orgId, request.getCostFactor());
    validateContiguousBucketType(orgId, request.getCostFactor());
    validateOrgIdCostFactorAndNotationUnique(orgId, request.getCostFactor(), request.getNotation());
    bucketValidationService.validateCostItineraryStatus(orgId, request.getCostFactor());
    if (Objects.isNull(request.getIsFromValueInclusive()))
      request.setIsFromValueInclusive(Boolean.TRUE);
    if (Objects.isNull(request.getIsToValueInclusive()))
      request.setIsToValueInclusive(Boolean.FALSE);
    CostFactorContiguousBucketEntity entity =
        INSTANCE.convertCostFactorContiguousBucketRequestToEntity(request);
    entity.setOrgId(orgId);
    return INSTANCE.convertCostFactorContiguousBucketEntityToDto(
        costFactorContiguousBucketRepository.save(entity));
  }

  private void validateContiguousBucketType(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeEntity =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (!BucketTypeEnum.CONTIGUOUS.equals(costFactorBucketTypeEntity.get().getBucketType())) {
      log.error(CONTIGUOUS_BUCKET_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          CONTIGUOUS_BUCKET_TYPE_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private void validateOrgIdCostFactorAndNotationUnique(
      String orgId, String costFactor, String notation) throws CommonServiceException {
    Optional<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntity =
        costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            orgId, costFactor, notation);
    if (costFactorContiguousBucketEntity.isPresent()) {
      log.error(COST_FACTOR_ORG_ID_NOTATION_DUPLICATE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      errorMap.put(NOTATION, FieldError.builder().rejectedValue(notation).build());
      throw new CommonServiceException(
          COST_FACTOR_ORG_ID_NOTATION_DUPLICATE, HttpStatus.CONFLICT, 0x1771, errorMap);
    }
  }

  @Override
  public List<CostFactorContiguousBucketDto> getCostFactorContiguousBuckets(
      String orgId, String costFactor) throws CommonServiceException {
    List<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntities =
        costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    handleBucketNotFound(orgId, costFactor, costFactorContiguousBucketEntities);
    return INSTANCE.convertCostFactorContiguousBucketEntityListToDtoList(
        costFactorContiguousBucketEntities);
  }

  private void handleBucketNotFound(
      String orgId,
      String costFactor,
      List<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntities)
      throws CommonServiceException {
    if (costFactorContiguousBucketEntities.isEmpty()) {
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
      Optional<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntityOptional)
      throws CommonServiceException {
    if (costFactorContiguousBucketEntityOptional.isEmpty()) {
      log.error(BUCKET_BY_ID_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          BUCKET_BY_ID_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  @Override
  public CostFactorContiguousBucketDto updateCostFactorContiguousBucket(
      Long id, String orgId, CostFactorContiguousBucketRequest request)
      throws CommonServiceException {
    Optional<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntityOptional =
        costFactorContiguousBucketRepository.findByIdAndOrgId(id, orgId);
    handleBucketNotFound(id, orgId, costFactorContiguousBucketEntityOptional);
    bucketValidationService.validateOrgIdAndCostFactor(orgId, request.getCostFactor());
    validateContiguousBucketType(orgId, request.getCostFactor());
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorContiguousBucketEntityOptional.get().getCostFactor());
    if (Objects.isNull(request.getIsFromValueInclusive()))
      request.setIsFromValueInclusive(Boolean.TRUE);
    if (Objects.isNull(request.getIsToValueInclusive()))
      request.setIsToValueInclusive(Boolean.FALSE);
    CostFactorContiguousBucketEntity costFactorContiguousBucketEntity =
        INSTANCE.convertCostFactorContiguousBucketRequestToEntity(request);
    costFactorContiguousBucketEntity.setOrgId(orgId);
    costFactorContiguousBucketEntity.setId(id);
    return INSTANCE.convertCostFactorContiguousBucketEntityToDto(
        costFactorContiguousBucketRepository.save(costFactorContiguousBucketEntity));
  }

  @Override
  public CostFactorContiguousBucketDto deleteCostFactorContiguousBucket(Long id, String orgId)
      throws CommonServiceException {
    Optional<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntityOptional =
        costFactorContiguousBucketRepository.findByIdAndOrgId(id, orgId);
    handleBucketNotFound(id, orgId, costFactorContiguousBucketEntityOptional);
    bucketValidationService.validateCostItineraryStatus(
        orgId, costFactorContiguousBucketEntityOptional.get().getCostFactor());
    costFactorContiguousBucketRepository.delete(costFactorContiguousBucketEntityOptional.get());
    return INSTANCE.convertCostFactorContiguousBucketEntityToDto(
        costFactorContiguousBucketEntityOptional.get());
  }

  public List<CostFactorContiguousBucketCacheKeyDto> getCostFactorContiguousBucketCacheKeys(
      Integer limit) {
    var costFactorContiguousBucketEntities =
        costFactorContiguousBucketRepository.findAllCostFactorContiguousBucketEntities(limit);

    return INSTANCE.toCostFactorContiguousBucketCacheKeyResponseList(
        costFactorContiguousBucketEntities);
  }
}
