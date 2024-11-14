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
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.TenantCostTypeMapper;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.dto.TenantCostTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeRequest;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeUpdateRequest;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantCostTypeService {

  private static final Logger logger = LoggerFactory.getLogger(TenantCostTypeService.class);
  private static final TenantCostTypeMapper INSTANCE =
      Mappers.getMapper(TenantCostTypeMapper.class);
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  private static final String COST_TYPE = "costType";
  private static final String DISPLAY_NAME = "displayName";
  private static final String TENANT_COST_TYPE_ALREADY_EXIST =
      "Tenant cost type already exist for given orgId";
  private static final String TENANT_COST_TYPE_DISPLAY_NAME_ALREADY_EXIST =
      "Tenant cost type display name already exists for given orgId";
  private static final String TENANT_COST_TYPE_EXCEPTION =
      "Tenant cost type not found with given details";
  private static final String TENANT_COST_TYPE_DELETE_EXCEPTION =
      "Tenant cost type associated with active cost itineraries. Remove those itineraries before performing this delete.";

  private static final String TENANT_COST_TYPE_ASSOCIATION_EXCEPTION =
      "Tenant cost type associated with active cost itineraries. Remove those itineraries before performing this update.";
  private static final String TENANT_COST_TYPE_SAME_DISPLAY_NAME_EXCEPTION =
      "Tenant cost type display name same as before";

  private final TenantCostTypeRepository tenantCostTypeRepository;
  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  @Transactional
  public TenantCostTypeResponse createTenantCostType(
      String orgId, TenantCostTypeRequest tenantCostTypeRequest) throws CommonServiceException {
    var existingTenantCostTypeEntity =
        tenantCostTypeRepository.findByOrgIdAndCostType(orgId, tenantCostTypeRequest.getCostType());
    handleExistingTenantCostTypeEntity(orgId, existingTenantCostTypeEntity);
    validateCostTypeDisplayName(orgId, tenantCostTypeRequest.getDisplayName());
    TenantCostTypeEntity tenantCostTypeEntity =
        INSTANCE.toTenantCostTypeEntity(tenantCostTypeRequest);
    tenantCostTypeEntity.setOrgId(orgId);
    return INSTANCE.toTenantCostTypeResponse(tenantCostTypeRepository.save(tenantCostTypeEntity));
  }

  public TenantCostTypeResponse getTenantCostType(String orgId, Long id)
      throws CommonServiceException {
    var existingTenantCostTypeEntity = tenantCostTypeRepository.findByIdAndOrgId(id, orgId);
    handleEmptyTenantCostTypeEntity(id, orgId, existingTenantCostTypeEntity);
    return INSTANCE.toTenantCostTypeResponse(existingTenantCostTypeEntity.get());
  }

  public List<TenantCostTypeResponse> getTenantCostTypeByOrgId(String orgId)
      throws CommonServiceException {
    var existingTenantCostTypeEntityList = tenantCostTypeRepository.findByOrgId(orgId);
    handleEmptyTenantCostTypeEntityList(orgId, existingTenantCostTypeEntityList);
    return INSTANCE.toTenantCostTypeListResponse((existingTenantCostTypeEntityList));
  }

  private void handleEmptyTenantCostTypeEntityList(
      String orgId, List<TenantCostTypeEntity> existingTenantCostTypeEntityList)
      throws CommonServiceException {
    if (existingTenantCostTypeEntityList.isEmpty()) {
      logger.error(TENANT_COST_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  @Transactional
  public TenantCostTypeResponse updateTenantCostType(
      Long id, String orgId, TenantCostTypeUpdateRequest tenantCostTypeUpdateRequest)
      throws CommonServiceException {
    var existingTenantCostTypeEntity = tenantCostTypeRepository.findByIdAndOrgId(id, orgId);
    handleEmptyTenantCostTypeEntity(id, orgId, existingTenantCostTypeEntity);
    validateDisplayName(
        orgId, tenantCostTypeUpdateRequest.getDisplayName(), existingTenantCostTypeEntity);
    INSTANCE.updateTenantCostTypeToEntity(
        tenantCostTypeUpdateRequest, existingTenantCostTypeEntity.get());
    return INSTANCE.toTenantCostTypeResponse(
        tenantCostTypeRepository.save(existingTenantCostTypeEntity.get()));
  }

  private void validateDisplayName(
      String orgId, String displayName, Optional<TenantCostTypeEntity> existingTenantCostTypeEntity)
      throws CommonServiceException {
    if (existingTenantCostTypeEntity.isPresent()
        && existingTenantCostTypeEntity.get().getDisplayName().equals(displayName)) {
      logger.error(TENANT_COST_TYPE_SAME_DISPLAY_NAME_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(DISPLAY_NAME, FieldError.builder().rejectedValue(displayName).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_SAME_DISPLAY_NAME_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    validateCostTypeDisplayName(orgId, displayName);
  }

  @Transactional
  public TenantCostTypeResponse deleteTenantCostType(long id, String orgId)
      throws CommonServiceException {
    Optional<TenantCostTypeEntity> tenantCostTypeEntity =
        tenantCostTypeRepository.findByIdAndOrgId(id, orgId);
    if (tenantCostTypeEntity.isEmpty()) {
      logger.error(TENANT_COST_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    List<SelectorAndCostItineraryMappingEntity> selectorAndCostItineraryMappingEntities =
        selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(
            orgId, tenantCostTypeEntity.get().getCostType());
    if (!selectorAndCostItineraryMappingEntities.isEmpty()) {
      logger.error(TENANT_COST_TYPE_DELETE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_DELETE_EXCEPTION, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
    }

    tenantCostTypeRepository.deleteByIdAndOrgId(id, orgId);
    return INSTANCE.toTenantCostTypeResponse(tenantCostTypeEntity.get());
  }

  private static void handleEmptyTenantCostTypeEntity(
      Long id, String orgId, Optional<TenantCostTypeEntity> existingTenantCostTypeEntity)
      throws CommonServiceException {
    if (existingTenantCostTypeEntity.isEmpty()) {
      logger.error(TENANT_COST_TYPE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_EXCEPTION, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private static void handleExistingTenantCostTypeEntity(
      String orgId, Optional<TenantCostTypeEntity> existingTenantCostTypeEntity)
      throws CommonServiceException {
    if (existingTenantCostTypeEntity.isPresent()) {
      logger.error(TENANT_COST_TYPE_ALREADY_EXIST);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          TENANT_COST_TYPE_ALREADY_EXIST, HttpStatus.CONFLICT, 0x1771, errorMap);
    }
  }

  private void validateCostTypeDisplayName(String orgId, String displayName)
      throws CommonServiceException {
    var tenantCostTypeEntities =
        tenantCostTypeRepository.findByOrgIdAndDisplayName(orgId, displayName);
    if (!tenantCostTypeEntities.isEmpty()) {
      logger.error(TENANT_COST_TYPE_DISPLAY_NAME_ALREADY_EXIST, displayName);
      throw new CommonServiceException(
          TENANT_COST_TYPE_DISPLAY_NAME_ALREADY_EXIST,
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              DISPLAY_NAME,
              FieldError.builder().rejectedValue(displayName).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  public List<TenantCostTypeCacheKeyDto> getAllTenantCostTypeCacheKeys(Integer limit) {
    return INSTANCE.toTenantCostTypeCacheKeyResponseList(
        tenantCostTypeRepository.findAllTenantCostTypeEntities(limit));
  }
}
