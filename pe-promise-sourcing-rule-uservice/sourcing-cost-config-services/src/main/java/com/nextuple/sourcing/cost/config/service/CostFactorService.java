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
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostFactorMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorAuditLogRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorRequest;
import com.nextuple.sourcing.cost.config.inbound.CostFactorUpdateRequest;
import com.nextuple.sourcing.cost.config.inbound.ExpressionValidationRequest;
import jakarta.transaction.Transactional;
import java.util.Date;
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
public class CostFactorService {
  private final CostFactorRepository costFactorRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final CostFactorAuditLogRepository costFactorAuditLogRepository;
  private final CostAttributeRepository costAttributeRepository;
  private final CostAttributeMappingRepository costAttributeMappingRepository;
  private final ExpressionValidationService expressionValidationService;
  private static final CostFactorMapper INSTANCE = Mappers.getMapper(CostFactorMapper.class);

  public static final String COST_FACTOR_TYPE = "costFactorType";
  public static final String LIBRARY = "library";
  private static final String COST_FACTOR_ID = "costFactorId";
  private static final String COST_FACTOR = "costFactor";
  private static final String DISPLAY_NAME = "displayName";
  private static final String ORG_ID = "orgId";
  private static final String IS_RATE_CARD_LOOKUP_REQUIRED = "isRateCardLookUpRequired";
  private static final String EMPTY_STRING = "";
  public static final String DERIVED_COST_FACTOR_WITHOUT_LIBRARY_EXCEPTION =
      "Derived cost factor type should have library in request";
  private static final String COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE = "Cost Factor not found";
  private static final String COST_FACTOR_NOT_UNIQUE_EXCEPTION_MESSAGE =
      "Combination of orgId and costFactor should be unique";
  public static final String COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE =
      "Combination of orgId and cost factor's display name should be unique";
  public static final String UPDATE_BUCKETED_COST_FACTOR_UOM_EXCEPTION_MESSAGE =
      "Can't update uom of cost factor whose buckets have been defined";
  public static final String EMPTY_UOM_EXCEPTION_MESSAGE = "uom of cost factor can't be empty";

  public CostFactorDto convertToCostFactorDto(CostFactorEntity costFactorEntity) {
    return INSTANCE.toCostFactorDto(costFactorEntity);
  }

  @Transactional
  public CostFactorDto createCostFactor(String orgId, CostFactorRequest costFactorRequest)
      throws CommonServiceException {
    log.debug("-- inside createCostFactor service : {}, {} --", orgId, costFactorRequest);
    validateCostFactorDisplayName(orgId, costFactorRequest.getDisplayName(), false, EMPTY_STRING);
    validateCostFactor(orgId, costFactorRequest);
    validateCostFactorTypeAndIsBucketed(orgId, costFactorRequest);
    validateFormulaValueFromCostAttributes(orgId, costFactorRequest);
    setIsRateCardLookUpRequiredIfNotPassed(costFactorRequest);
    validateRequestWhenIsRateCardLookUpFalse(orgId, costFactorRequest);
    var costFactorEntity = INSTANCE.toCostFactorEntity(costFactorRequest);
    costFactorEntity.setOrgId(orgId);
    var savedCostFactorEntity = costFactorRepository.save(costFactorEntity);
    var costFactorAuditLogEntity = INSTANCE.toCostFactorAuditLogEntity(savedCostFactorEntity);
    costFactorAuditLogEntity.setTimestamp(new Date());
    costFactorAuditLogRepository.save(costFactorAuditLogEntity);
    return convertToCostFactorDto(savedCostFactorEntity);
  }

  private void validateRequestWhenIsRateCardLookUpFalse(
      String orgId, CostFactorRequest costFactorRequest) throws CommonServiceException {
    if (Boolean.FALSE.equals(costFactorRequest.getIsRateCardLookUpRequired())) {
      validateIsBucketedWhenIsRateCardLookUpFalse(orgId, costFactorRequest);
      validateDataTypeWhenIsRateCardLookUpFalse(orgId, costFactorRequest);
    }
  }

  private static void validateDataTypeWhenIsRateCardLookUpFalse(
      String orgId, CostFactorRequest costFactorRequest) throws CommonServiceException {
    if (!costFactorRequest.getDataType().equals(DataTypeEnum.NUMBER)) {
      throw new CommonServiceException(
          "Cost factor cannot be created if rateCardLookUp is disabled and data type is not a number.",
          HttpStatus.BAD_REQUEST,
          0x1782,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              IS_RATE_CARD_LOOKUP_REQUIRED,
              FieldError.builder()
                  .rejectedValue(costFactorRequest.getIsRateCardLookUpRequired())
                  .build(),
              "dataType",
              FieldError.builder().rejectedValue(costFactorRequest.getDataType()).build()));
    }
  }

  private static void validateIsBucketedWhenIsRateCardLookUpFalse(
      String orgId, CostFactorRequest costFactorRequest) throws CommonServiceException {
    if (Boolean.TRUE.equals(costFactorRequest.getIsBucketed())) {
      throw new CommonServiceException(
          "Cost factor cannot be created if rateCardLookUp is disabled and bucketed is enabled.",
          HttpStatus.BAD_REQUEST,
          0x1781,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              IS_RATE_CARD_LOOKUP_REQUIRED,
              FieldError.builder()
                  .rejectedValue(costFactorRequest.getIsRateCardLookUpRequired())
                  .build(),
              "isBucketed",
              FieldError.builder().rejectedValue(costFactorRequest.getIsBucketed()).build()));
    }
  }

  private static void setIsRateCardLookUpRequiredIfNotPassed(CostFactorRequest costFactorRequest) {
    if (Objects.isNull(costFactorRequest.getIsRateCardLookUpRequired())) {
      costFactorRequest.setIsRateCardLookUpRequired(Boolean.TRUE);
    }
  }

  private void validateCostFactorDisplayName(
      String orgId, String displayName, boolean isUpdateOperation, String prevDisplayName)
      throws CommonServiceException {
    var costFactorEntity = costFactorRepository.findFirstByOrgIdAndDisplayName(orgId, displayName);
    boolean isDisplayNameChanged = isUpdateOperation && !prevDisplayName.equals(displayName);
    if (isDisplayNameChanged && costFactorEntity.isPresent()
        || !isUpdateOperation && costFactorEntity.isPresent()) {
      log.error(COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          COST_FACTOR_DISPLAY_NAME_NOT_UNIQUE_EXCEPTION_MESSAGE,
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID, FieldError.builder().rejectedValue(orgId).build(),
              DISPLAY_NAME, FieldError.builder().rejectedValue(displayName).build()));
    }
  }

  private void validateCostFactor(String orgId, CostFactorRequest costFactorRequest)
      throws CommonServiceException {
    List<CostFactorEntity> costFactorEntityList =
        costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactorRequest.getCostFactor());
    if (!costFactorEntityList.isEmpty()) {
      log.error(COST_FACTOR_NOT_UNIQUE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          COST_FACTOR,
          FieldError.builder().rejectedValue(costFactorRequest.getCostFactor()).build());
      throw new CommonServiceException(
          COST_FACTOR_NOT_UNIQUE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private static void validateCostFactorTypeAndIsBucketed(
      String orgId, CostFactorRequest costFactorRequest) throws CommonServiceException {
    if (CostFactorTypeEnum.DERIVED.equals(costFactorRequest.getCostFactorType())
        && Boolean.FALSE.equals(costFactorRequest.getIsBucketed())) {
      log.error("Derived cost factor type cannot have isBucketed as false");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          COST_FACTOR_TYPE,
          FieldError.builder().rejectedValue(costFactorRequest.getCostFactorType()).build());
      errorMap.put(
          "isBucketed",
          FieldError.builder().rejectedValue(costFactorRequest.getIsBucketed()).build());
      throw new CommonServiceException(
          "Derived cost factor type cannot have isBucketed as false",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  private void validateFormulaValueFromCostAttributes(
      String orgId, CostFactorRequest costFactorRequest) throws CommonServiceException {
    if (CostFactorTypeEnum.REGULAR.equals(costFactorRequest.getCostFactorType())) {
      var formula = costFactorRequest.getFormula();
      var costAttributeMappingDetails =
          costAttributeMappingRepository.findByOrgIdAndCanonicalName(orgId, formula);
      if (costAttributeMappingDetails.isEmpty()) {
        Optional<CostAttributeDetailsEntity> costAttributeDetails =
            costAttributeRepository.findByAttributeName(formula);
        if (costAttributeDetails.isEmpty()) {
          log.error("Cost attribute not configured for given formula :{}", formula);
          throw new CommonServiceException(
              "Cost attribute not configured for given formula",
              HttpStatus.PRECONDITION_FAILED,
              0x1771,
              Map.of(
                  ORG_ID,
                  FieldError.builder().rejectedValue(orgId).build(),
                  "formula",
                  FieldError.builder().rejectedValue(formula).build()));
        }
      }
    } else if (CostFactorTypeEnum.DERIVED.equals(costFactorRequest.getCostFactorType())) {
      if (costFactorRequest.getLibrary() == null) {
        log.error(DERIVED_COST_FACTOR_WITHOUT_LIBRARY_EXCEPTION);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(
            COST_FACTOR_TYPE,
            FieldError.builder().rejectedValue(CostFactorTypeEnum.DERIVED).build());
        errorMap.put(
            LIBRARY, FieldError.builder().rejectedValue(costFactorRequest.getLibrary()).build());
        throw new CommonServiceException(
            DERIVED_COST_FACTOR_WITHOUT_LIBRARY_EXCEPTION,
            HttpStatus.BAD_REQUEST,
            0x1771,
            errorMap);
      }
      ExpressionValidationRequest expressionValidationRequest =
          ExpressionValidationRequest.builder()
              .expression(costFactorRequest.getFormula())
              .expressionValueDataType(costFactorRequest.getDataType())
              .build();
      expressionValidationService.validateExpression(
          orgId, costFactorRequest.getLibrary().getLibraryName(), expressionValidationRequest);
    }
  }

  private CostFactorEntity getCostFactorEntity(String orgId, Long costFactorId)
      throws CommonServiceException {
    Optional<CostFactorEntity> costFactorEntity =
        costFactorRepository.findByIdAndOrgId(costFactorId, orgId);
    if (costFactorEntity.isEmpty()) {
      log.error(COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR_ID, FieldError.builder().rejectedValue(costFactorId).build());
      throw new CommonServiceException(
          COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return costFactorEntity.get();
  }

  @Transactional
  public CostFactorDto findCostFactorByOrgIdAndCostFactorId(String orgId, Long costFactorId)
      throws CommonServiceException {
    log.debug(
        "-- inside findCostFactorByOrgIdAndCostFactorId service : {}, {} --", orgId, costFactorId);
    CostFactorEntity costFactorEntity = getCostFactorEntity(orgId, costFactorId);
    return convertToCostFactorDto(costFactorEntity);
  }

  public CostFactorDto findCostFactorByOrgIdAndCostFactor(String orgId, String costFactor)
      throws CommonServiceException {
    log.debug(
        "-- inside findCostFactorByOrgIdAndCostFactor service : {}, {} --", orgId, costFactor);
    List<CostFactorEntity> costFactorEntityList =
        costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (costFactorEntityList.isEmpty()) {
      log.error(COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return convertToCostFactorDto(costFactorEntityList.get(0));
  }

  @Transactional
  public CostFactorDto updateCostFactor(
      Long costFactorId, String orgId, CostFactorUpdateRequest updateCostFactorRequest)
      throws CommonServiceException {
    log.debug(
        "-- inside updateCostFactor service : {}, {}, {} --",
        orgId,
        costFactorId,
        updateCostFactorRequest);
    CostFactorEntity costFactorEntity = getCostFactorEntity(orgId, costFactorId);
    validateCostFactorDisplayName(
        orgId, updateCostFactorRequest.getDisplayName(), true, costFactorEntity.getDisplayName());
    validateUomOfCostFactor(costFactorId, orgId, updateCostFactorRequest, costFactorEntity);
    INSTANCE.updateCostFactorEntity(updateCostFactorRequest, costFactorEntity);
    var updatedCostFactorEntity = costFactorRepository.save(costFactorEntity);
    var costFactorAuditLogEntity = INSTANCE.toCostFactorAuditLogEntity(updatedCostFactorEntity);
    costFactorAuditLogEntity.setTimestamp(new Date());
    costFactorAuditLogRepository.save(costFactorAuditLogEntity);
    return convertToCostFactorDto(updatedCostFactorEntity);
  }

  private static void validateUomOfCostFactor(
      Long costFactorId,
      String orgId,
      CostFactorUpdateRequest updateCostFactorRequest,
      CostFactorEntity costFactorEntity)
      throws CommonServiceException {
    if (Boolean.TRUE.equals(costFactorEntity.getIsBucketed())
        && (StringUtils.hasLength(updateCostFactorRequest.getUom()))) {
      log.error(UPDATE_BUCKETED_COST_FACTOR_UOM_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          UPDATE_BUCKETED_COST_FACTOR_UOM_EXCEPTION_MESSAGE,
          HttpStatus.BAD_REQUEST,
          0x1771,
          Map.of(
              ORG_ID, FieldError.builder().rejectedValue(orgId).build(),
              COST_FACTOR_ID, FieldError.builder().rejectedValue(costFactorId).build()));
    }
    if (!StringUtils.hasLength(updateCostFactorRequest.getUom())) {
      updateCostFactorRequest.setUom(costFactorEntity.getUom());
    }
  }

  @Transactional
  public CostFactorDto deleteCostFactor(Long costFactorId, String orgId)
      throws CommonServiceException {
    log.debug("-- inside deleteCostFactor service {}, {} --", orgId, costFactorId);
    CostFactorEntity costFactorEntity = getCostFactorEntity(orgId, costFactorId);

    List<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntityList =
        costItineraryAndFactorsRepository.findCostFactorInItineraries(
            costFactorEntity.getCostFactor(), costFactorEntity.getOrgId());
    if (!costItineraryAndFactorsEntityList.isEmpty()) {
      log.error("Cost factor is associated with one or more cost itinerary");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_FACTOR_ID, FieldError.builder().rejectedValue(costFactorId).build());
      throw new CommonServiceException(
          "Cost factor is associated with one or more cost itinerary",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    costFactorRepository.delete(costFactorEntity);
    return convertToCostFactorDto(costFactorEntity);
  }

  public List<CostFactorCacheKeyDto> getCostFactorCacheKeys(Integer limit) {
    var costFactorEntities = costFactorRepository.findAllCostFactorEntities(limit);

    return INSTANCE.toCostFactorCacheKeyResponseList(costFactorEntities);
  }
}
