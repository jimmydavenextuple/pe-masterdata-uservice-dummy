/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeMappingEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostAttributeMappingMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.dto.CostAttributeMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
import com.nextuple.sourcing.cost.config.utils.SourcingCostConfigUtil;
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
public class CostAttributeMappingService {

  private final CostAttributeMappingRepository costAttributeMappingRepository;
  private final CostAttributeRepository costAttributeRepository;
  private static final CostAttributeMappingMapper INSTANCE =
      Mappers.getMapper(CostAttributeMappingMapper.class);
  private static final String CANONICAL_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE =
      "Combination of orgId and canonicalName should be unique";
  private static final String ATTRIBUTE_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE =
      "Combination of orgId and attributeName should be unique";
  private static final String COST_ATTRIBUTE_MAPPING_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost attribute mapping details not found";
  private static final String CANONICAL_NAME = "canonicalName";
  private static final String ATTRIBUTE_NAME = "attributeName";
  private static final String ORG_ID = "orgId";
  private static final String COST_ATTRIBUTE_MAPPING_ID = "costAttributeMappingId";
  private static final String COST_ATTRIBUTE_NAME = "costAttributeName";

  @Transactional
  public CostAttributeMappingResponse createCostAttributeMapping(
      String orgId, CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    log.debug("-- inside createCostAttributeMapping service --");
    validateCostAttribute(costAttributeMappingRequest);
    validateCostAttributeMappingRequest(orgId, costAttributeMappingRequest);
    var costAttributeMappingEntity =
        INSTANCE.toCostAttributeMappingEntity(costAttributeMappingRequest);
    costAttributeMappingEntity.setOrgId(orgId);
    var savedCostAttributeMappingEntity =
        costAttributeMappingRepository.save(costAttributeMappingEntity);
    return INSTANCE.toCostAttributeMappingResponse(savedCostAttributeMappingEntity);
  }

  private void validateCostAttribute(CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
        costAttributeRepository.findByAttributeName(costAttributeMappingRequest.getAttributeName());
    if (costAttributeDetailsEntity.isEmpty()) {
      log.error(
          "Can't add cost attribute mapping as the cost attribute :{} doesn't exist",
          costAttributeMappingRequest.getAttributeName());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          COST_ATTRIBUTE_NAME,
          FieldError.builder()
              .rejectedValue(costAttributeMappingRequest.getAttributeName())
              .build());
      throw new CommonServiceException(
          "Can't add cost attribute mapping as the cost attribute doesn't exist",
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          errorMap);
    }
  }

  private void validateCostAttributeMappingRequest(
      String orgId, CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    validateUniqueCanonicalName(orgId, costAttributeMappingRequest);
    validateUniqueProductAttributeName(orgId, costAttributeMappingRequest);
    SourcingCostConfigUtil.validateCostAttributeName(
        costAttributeMappingRequest.getCanonicalName());
  }

  private void validateUniqueCanonicalName(
      String orgId, CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    List<CostAttributeMappingEntity> costAttributeMappingEntityList =
        costAttributeMappingRepository.findByOrgIdAndCanonicalName(
            orgId, costAttributeMappingRequest.getCanonicalName());
    if (!costAttributeMappingEntityList.isEmpty()) {
      log.error(CANONICAL_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CANONICAL_NAME,
          FieldError.builder()
              .rejectedValue(costAttributeMappingRequest.getCanonicalName())
              .build());
      throw new CommonServiceException(
          CANONICAL_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private void validateUniqueProductAttributeName(
      String orgId, CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    List<CostAttributeMappingEntity> costAttributeMappingEntityList =
        costAttributeMappingRepository.findByOrgIdAndAttributeName(
            orgId, costAttributeMappingRequest.getAttributeName());
    if (!costAttributeMappingEntityList.isEmpty()) {
      log.error(ATTRIBUTE_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          ATTRIBUTE_NAME,
          FieldError.builder()
              .rejectedValue(costAttributeMappingRequest.getAttributeName())
              .build());
      throw new CommonServiceException(
          ATTRIBUTE_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  @Transactional
  public CostAttributeMappingResponse findCostAttributeMappingByOrgIdAndId(
      String orgId, Long costAttributeMappingId) throws CommonServiceException {
    log.debug("-- inside findCostAttributeMappingByOrgIdAndId service --");
    Optional<CostAttributeMappingEntity> costAttributeMappingEntity =
        costAttributeMappingRepository.findByIdAndOrgId(costAttributeMappingId, orgId);
    if (costAttributeMappingEntity.isEmpty()) {
      log.error(COST_ATTRIBUTE_MAPPING_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          COST_ATTRIBUTE_MAPPING_ID,
          FieldError.builder().rejectedValue(costAttributeMappingId).build());
      throw new CommonServiceException(
          COST_ATTRIBUTE_MAPPING_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toCostAttributeMappingResponse(costAttributeMappingEntity.get());
  }

  @Transactional
  public CostAttributeMappingResponse updateCostAttributeMapping(
      Long costAttributeMappingId,
      String orgId,
      CostAttributeMappingRequest costAttributeMappingRequest)
      throws CommonServiceException {
    log.debug("-- inside updateCostAttributeMapping service --");
    Optional<CostAttributeMappingEntity> costAttributeMappingEntity =
        costAttributeMappingRepository.findByIdAndOrgId(costAttributeMappingId, orgId);
    if (costAttributeMappingEntity.isEmpty()) {
      log.error(COST_ATTRIBUTE_MAPPING_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          COST_ATTRIBUTE_MAPPING_ID,
          FieldError.builder().rejectedValue(costAttributeMappingId).build());
      throw new CommonServiceException(
          COST_ATTRIBUTE_MAPPING_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    if (!costAttributeMappingEntity
        .get()
        .getCanonicalName()
        .equals(costAttributeMappingRequest.getCanonicalName())) {
      validateUniqueCanonicalName(orgId, costAttributeMappingRequest);
      SourcingCostConfigUtil.validateCostAttributeName(
          costAttributeMappingRequest.getCanonicalName());
    }
    if (!costAttributeMappingEntity
        .get()
        .getAttributeName()
        .equals(costAttributeMappingRequest.getAttributeName())) {
      validateCostAttribute(costAttributeMappingRequest);
      validateUniqueProductAttributeName(orgId, costAttributeMappingRequest);
    }
    INSTANCE.updateCostAttributeMappingEntity(
        costAttributeMappingRequest, costAttributeMappingEntity.get());
    var updatedCostAttributeMappingEntity =
        costAttributeMappingRepository.save(costAttributeMappingEntity.get());
    return INSTANCE.toCostAttributeMappingResponse(updatedCostAttributeMappingEntity);
  }

  @Transactional
  public CostAttributeMappingResponse findCostAttributeMappingByOrgIdAndCanonicalName(
      String orgId, String canonicalName) throws CommonServiceException {
    log.debug("-- findCostAttributeMappingByOrgIdAndCanonicalName service --");
    List<CostAttributeMappingEntity> costAttributeMappingEntityList =
        costAttributeMappingRepository.findByOrgIdAndCanonicalName(orgId, canonicalName);
    if (costAttributeMappingEntityList.isEmpty()) {
      log.error(
          "Cost attribute mapping details not found for given orgId :{} and canonicalName :{}",
          orgId,
          canonicalName);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CANONICAL_NAME, FieldError.builder().rejectedValue(canonicalName).build());
      throw new CommonServiceException(
          "Cost attribute mapping details not found for given orgId and canonicalName",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toCostAttributeMappingResponse(costAttributeMappingEntityList.get(0));
  }

  public List<CostAttributeMappingCacheKeyDto> getAllCostAttributeMappingCacheKeys(Integer limit) {
    return INSTANCE.toCostAttributeMappingCacheKeyResponseList(
        costAttributeMappingRepository.findAllCostAttributeMappingEntities(limit));
  }
}
