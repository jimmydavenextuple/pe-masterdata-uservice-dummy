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
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostItineraryAndFactorsMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import com.nextuple.sourcing.cost.config.inbound.CostItineraryAndFactorsRequest;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CostItineraryAndFactorsService {
  public static final String ITINERARY_STATUS = "itineraryStatus";
  public static final String IS_ACTIVE = "isActive";
  public static final String LEVEL_APPLIED_OF_COST_FACTOR = "levelAppliedOfCostFactor";
  public static final String LEVEL_APPLIED_OF_COST_ITINERARY_AND_FACTORS =
      "levelAppliedOfCostItineraryAndFactors";
  public static final String INVALID_LEVEL_APPLIED_EXCEPTION =
      "Level applied of cost factor is not valid";
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final CostFactorRepository costFactorRepository;
  private final CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  private final CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  private final CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  private final BucketValidationService bucketValidationService;
  private final CostValueRepository costValueRepository;
  private final SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  private static final CostItineraryAndFactorsMapper INSTANCE =
      Mappers.getMapper(CostItineraryAndFactorsMapper.class);
  public static final String COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost Itinerary And Cost Factors Mapping not found";

  private static final String COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost Factor not found for given orgId.";
  private static final String COST_FACTOR_VALUE_INVALID =
      "Bucket configuration not found for cost factor value.";
  private static final String COST_ITINERARY_ALREADY_PRESENT_EXCEPTION_MESSAGE =
      "Duplicate cost itinerary not allowed for given orgId.";
  public static final String ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE =
      "If any cost factor associated to a given itinerary has isRateCardLookUp set to false, then other cost factors can't be associated to the same itinerary.";

  private static final String COST_ITINERARY_ALREADY_CREATED_EXCEPTION_MESSAGE =
      "Cost itinerary is in CREATED state and cannot be updated.";

  public static final String COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE =
      "Cost itinerary is associated in cost value table and cannot be ";
  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "Bucket type not found for orgId and Cost Factor combination.";
  public static final String COST_ITINERARY_IN_DRAFT_STATE =
      "Can't activate the cost itinerary as it's DRAFT state";
  public static final String COST_ITINERARY_IS_DEFAULT_ITINERARY =
      "Can't inactivate the cost itinerary as it's used as default itinerary by one or multiple cost type";
  public static final String COST_ITINERARY_IS_ONLY_ITINERARY =
      "Can't inactivate the cost itinerary as it's used as only itinerary by one or multiple cost type";
  private static final String COST_FACTOR_VALUE_NOT_FOUND =
      "Cost factor values or default value not configured.";
  private static final String ID = "id";
  private static final String ORG_ID = "orgId";
  private static final String VALUE = "value";
  private static final String COST_FACTOR = "costFactor";
  private static final String COST_FACTORS = "costFactors";
  private static final String COST_ITINERARY = "costItinerary";
  public static final String UPDATE_COST_ITINERARY = "updated";
  public static final String DELETE_COST_ITINERARY = "deleted";

  public CostItineraryAndFactorsDto convertToCostItineraryAndFactorsDto(
      CostItineraryAndFactorsEntity costItineraryAndFactorsEntity) {
    return INSTANCE.toCostItineraryAndFactorsDto(costItineraryAndFactorsEntity);
  }

  @Transactional
  public CostItineraryAndFactorsDto createCostItineraryAndFactors(
      String orgId, CostItineraryAndFactorsRequest costItineraryAndFactorsRequest)
      throws CommonServiceException {
    log.debug("-- inside createCostItineraryAndFactorsDto service --");
    validateCostFactorsForRateCardLookup(
        orgId,
        costItineraryAndFactorsRequest.getCostFactors(),
        costItineraryAndFactorsRequest.getCostItinerary());
    validateCostFactorValuesAndLevelApplied(orgId, costItineraryAndFactorsRequest);
    validateUniqueCostItinerary(orgId, costItineraryAndFactorsRequest.getCostItinerary());
    var costItineraryAndFactorsEntity =
        INSTANCE.toCostItineraryAndFactorsEntity(costItineraryAndFactorsRequest);
    costItineraryAndFactorsEntity.setOrgId(orgId);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.DRAFT);
    costItineraryAndFactorsEntity.setIsActive(false);
    return convertToCostItineraryAndFactorsDto(
        costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity));
  }

  private void validateCostFactorsForRateCardLookup(
      String orgId, String costFactors, String costItinerary) throws CommonServiceException {
    List<String> costFactorsList = Arrays.stream(costFactors.split(",")).toList();
    List<CostFactorEntity> costFactorEntityList =
        costFactorRepository.findByOrgIdAndCostFactorIn(orgId, costFactorsList);
    var isThereAnyCostFactorWithoutRateCardLookup =
        costFactorEntityList.stream()
            .anyMatch(
                costFactorEntity ->
                    Boolean.FALSE.equals(costFactorEntity.getIsRateCardLookUpRequired()));
    if (isThereAnyCostFactorWithoutRateCardLookup && costFactorsList.size() > 1) {
      log.error(ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE,
          HttpStatus.BAD_REQUEST,
          0x1971,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build(),
              COST_FACTORS,
              FieldError.builder().rejectedValue(costFactors).build()));
    }
  }

  private void validateUniqueCostItinerary(String orgId, String costItinerary)
      throws CommonServiceException {
    Optional<CostItineraryAndFactorsEntity> optionalCostItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
    if (optionalCostItineraryAndFactorsEntity.isPresent()) {
      log.error(COST_ITINERARY_ALREADY_PRESENT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      throw new CommonServiceException(
          COST_ITINERARY_ALREADY_PRESENT_EXCEPTION_MESSAGE, HttpStatus.CONFLICT, 0x1771, errorMap);
    }
  }

  private void validateCostFactorValuesAndLevelApplied(
      String orgId, CostItineraryAndFactorsRequest costItineraryAndFactorsRequest)
      throws CommonServiceException {
    List<String> costFactorList =
        Arrays.stream(costItineraryAndFactorsRequest.getCostFactors().split(","))
            .map(String::trim)
            .toList();
    for (String costFactor : costFactorList) {
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
      validateCostFactorLevelApplied(
          orgId, costItineraryAndFactorsRequest, costFactor, costFactorEntityList);
    }
  }

  private void validateCostFactorLevelApplied(
      String orgId,
      CostItineraryAndFactorsRequest costItineraryAndFactorsRequest,
      String costFactor,
      List<CostFactorEntity> costFactorEntityList)
      throws CommonServiceException {
    if (costFactorEntityList.get(0).getDataType().equals(DataTypeEnum.NUMBER)) {
      LevelAppliedEnum costFactorLevelApplied = costFactorEntityList.get(0).getLevelApplied();
      LevelAppliedEnum costItineraryAndFactorsLevelApplied =
          costItineraryAndFactorsRequest.getLevelApplied();
      Boolean isValidCfLevelApplied =
          isValidLevelAppliedOfCostFactor(
              costFactorLevelApplied, costItineraryAndFactorsLevelApplied);
      if (isValidCfLevelApplied.equals(Boolean.FALSE)) {
        log.error(INVALID_LEVEL_APPLIED_EXCEPTION);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
        errorMap.put(
            LEVEL_APPLIED_OF_COST_FACTOR,
            FieldError.builder().rejectedValue(costFactorLevelApplied).build());
        errorMap.put(
            LEVEL_APPLIED_OF_COST_ITINERARY_AND_FACTORS,
            FieldError.builder().rejectedValue(costItineraryAndFactorsLevelApplied).build());
        throw new CommonServiceException(
            INVALID_LEVEL_APPLIED_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }

  private Boolean isValidLevelAppliedOfCostFactor(
      LevelAppliedEnum costFactorLevelApplied,
      LevelAppliedEnum costItineraryAndFactorsLevelApplied) {

    return switch (costItineraryAndFactorsLevelApplied) {
      case SHIPMENT -> costFactorLevelApplied == LevelAppliedEnum.SHIPMENT
          || costFactorLevelApplied == LevelAppliedEnum.ITEM
          || costFactorLevelApplied == LevelAppliedEnum.ITEM_QTY;
      case ITEM -> costFactorLevelApplied == LevelAppliedEnum.ITEM
          || costFactorLevelApplied == LevelAppliedEnum.ITEM_QTY;
      case ITEM_QTY -> costFactorLevelApplied == LevelAppliedEnum.ITEM_QTY;
      case ORDER -> costFactorLevelApplied == LevelAppliedEnum.ORDER;
      default -> Boolean.FALSE;
    };
  }

  @Transactional
  public CostItineraryAndFactorsDto findCostItineraryAndFactorsByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException {
    log.debug("-- inside findCostItineraryAndFactorsByOrgIdAndId service --");
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByIdAndOrgId(id, orgId);
    if (costItineraryAndFactorsEntity.isEmpty()) {
      log.error(COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return convertToCostItineraryAndFactorsDto(costItineraryAndFactorsEntity.get());
  }

  public CostItineraryAndFactorsDto
      findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
          String orgId, String costItinerary, ItineraryStatusEnum itineraryStatus, Boolean isActive)
          throws CommonServiceException {

    log.debug(
        "-- inside findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive service --");
    Optional<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
            orgId, costItinerary, itineraryStatus, isActive);
    if (costItineraryAndFactorsEntity.isEmpty()) {
      log.error(COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_ITINERARY, FieldError.builder().rejectedValue(costItinerary).build());
      errorMap.put(ITINERARY_STATUS, FieldError.builder().rejectedValue(itineraryStatus).build());
      errorMap.put(IS_ACTIVE, FieldError.builder().rejectedValue(isActive).build());
      throw new CommonServiceException(
          COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return convertToCostItineraryAndFactorsDto(costItineraryAndFactorsEntity.get());
  }

  @Transactional
  public CostItineraryAndFactorsDto updateCostItineraryAndFactors(
      Long id, String orgId, CostItineraryAndFactorsRequest updateCostItineraryAndFactorsRequest)
      throws CommonServiceException {
    log.debug("-- inside updateCostItineraryAndFactors service --");
    validateCostFactorsForRateCardLookup(
        orgId,
        updateCostItineraryAndFactorsRequest.getCostFactors(),
        updateCostItineraryAndFactorsRequest.getCostItinerary());
    var costItineraryAndFactorsEntity =
        INSTANCE.toCostItineraryAndFactorsEntityFromDto(
            findCostItineraryAndFactorsByOrgIdAndId(orgId, id));
    validateCostItineraryAssociationInCostValueTable(
        orgId, costItineraryAndFactorsEntity.getCostItinerary(), UPDATE_COST_ITINERARY);
    validateItineraryStatus(
        costItineraryAndFactorsEntity.getCostItinerary(),
        orgId,
        costItineraryAndFactorsEntity.getItineraryStatus());
    validateCostFactorValuesAndLevelApplied(orgId, updateCostItineraryAndFactorsRequest);
    costItineraryAndFactorsEntity.setCostFactors(
        updateCostItineraryAndFactorsRequest.getCostFactors());
    costItineraryAndFactorsEntity.setCostItinerary(
        updateCostItineraryAndFactorsRequest.getCostItinerary());
    return convertToCostItineraryAndFactorsDto(
        costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity));
  }

  private void validateCostItineraryAssociationInCostValueTable(
      String orgId, String costItinerary, String operation) throws CommonServiceException {
    var costValueEntity =
        costValueRepository.findFirstByOrgIdAndCostItinerary(orgId, costItinerary);
    if (costValueEntity.isPresent()) {
      log.error(COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE);
      throw new CommonServiceException(
          COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE + operation,
          HttpStatus.CONFLICT,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  @Transactional
  public CostItineraryAndFactorsDto updateCostItineraryAndFactorsStatus(Long id, String orgId)
      throws CommonServiceException {
    log.debug("-- inside updateCostItineraryAndFactorsStatus service --");
    var costItineraryAndFactorsEntity =
        INSTANCE.toCostItineraryAndFactorsEntityFromDto(
            findCostItineraryAndFactorsByOrgIdAndId(orgId, id));
    validateItineraryStatus(
        costItineraryAndFactorsEntity.getCostItinerary(),
        orgId,
        costItineraryAndFactorsEntity.getItineraryStatus());
    bucketValidationService.validateCostFactorBucketRanges(
        orgId, costItineraryAndFactorsEntity.getCostFactors());

    validateCostFactorBucketValues(orgId, costItineraryAndFactorsEntity.getCostFactors());
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    return convertToCostItineraryAndFactorsDto(
        costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity));
  }

  @Transactional
  public CostItineraryAndFactorsDto updateCostItineraryAndFactorsStatusByCostItinerary(
      String costItinerary, String orgId) throws CommonServiceException {
    log.debug("-- inside updateCostItineraryAndFactorsStatusByCostItinerary service --");
    var costItineraryAndFactorsEntity =
        getCostItineraryAndFactorsEntityByOrgIdAndCostItinerary(costItinerary, orgId);
    validateItineraryStatus(
        costItinerary, orgId, costItineraryAndFactorsEntity.getItineraryStatus());
    bucketValidationService.validateCostFactorBucketRanges(
        orgId, costItineraryAndFactorsEntity.getCostFactors());
    validateCostFactorBucketValues(orgId, costItineraryAndFactorsEntity.getCostFactors());
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    return convertToCostItineraryAndFactorsDto(
        costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity));
  }

  private static void validateItineraryStatus(
      String costItinerary, String orgId, ItineraryStatusEnum itineraryStatus)
      throws CommonServiceException {
    if (ItineraryStatusEnum.CREATED.equals(itineraryStatus)) {
      log.error(COST_ITINERARY_ALREADY_CREATED_EXCEPTION_MESSAGE);
      throw new CommonServiceException(
          COST_ITINERARY_ALREADY_CREATED_EXCEPTION_MESSAGE,
          HttpStatus.CONFLICT,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  private CostItineraryAndFactorsEntity getCostItineraryAndFactorsEntityByOrgIdAndCostItinerary(
      String costItinerary, String orgId) throws CommonServiceException {
    var costItineraryAndFactorsEntity =
        costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(orgId, costItinerary);
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
    return costItineraryAndFactorsEntity.get();
  }

  private void handleInvalidCostFactorValue(String orgId, String costFactor, String value)
      throws CommonServiceException {
    log.error(COST_FACTOR_VALUE_INVALID);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
    errorMap.put(VALUE, FieldError.builder().rejectedValue(value).build());
    throw new CommonServiceException(
        COST_FACTOR_VALUE_INVALID, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
  }

  private void handleCostFactorValueNotFound(String orgId, String costFactor)
      throws CommonServiceException {
    log.error(COST_FACTOR_VALUE_NOT_FOUND);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
    throw new CommonServiceException(
        COST_FACTOR_VALUE_NOT_FOUND, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
  }

  private void validateCostFactorBucketValues(String orgId, String commaSeparatedCostFactors)
      throws CommonServiceException {
    List<String> costFactors = Arrays.stream(commaSeparatedCostFactors.split(",")).toList();
    for (String costFactor : costFactors) {
      CostFactorEntity costFactorEntity =
          costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactor).get(0);
      if (Boolean.TRUE.equals(costFactorEntity.getIsBucketed())) {
        String commaSeparatedBuckets = costFactorEntity.getValues();
        String defaultCostFactorBucket = costFactorEntity.getDefaultValue();

        if (Objects.isNull(commaSeparatedBuckets)) handleCostFactorValueNotFound(orgId, costFactor);
        List<String> costFactorBuckets =
            Arrays.stream(costFactorEntity.getValues().split(",")).collect(Collectors.toList());
        costFactorBuckets.add(defaultCostFactorBucket);
        costFactorBuckets =
            costFactorBuckets.stream().filter(this::filterEmptyBucketValues).toList();

        if (costFactorBuckets.isEmpty()) {
          handleCostFactorValueNotFound(orgId, costFactor);
        }
        List<String> possibleBucketsForCostFactor =
            getPossibleBucketsForCostFactor(orgId, costFactor);
        for (String costFactorBucket : costFactorBuckets) {
          if (!possibleBucketsForCostFactor.contains(costFactorBucket)) {
            handleInvalidCostFactorValue(orgId, costFactor, costFactorBucket);
          }
        }
      }
    }
  }

  private boolean filterEmptyBucketValues(String bucketValue) {
    return StringUtils.hasLength(bucketValue);
  }

  private List<String> getPossibleBucketsForCostFactor(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> bucketTypeEntityOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (bucketTypeEntityOptional.isEmpty()) handleBucketTypeNotFound(orgId, costFactor);
    if (BucketTypeEnum.DISCRETE.equals(bucketTypeEntityOptional.get().getBucketType()))
      return possibleValuesForDiscreteBuckets(orgId, costFactor);
    return possibleValuesForContiguousBuckets(orgId, costFactor);
  }

  private List<String> possibleValuesForContiguousBuckets(String orgId, String costFactor) {
    List<CostFactorContiguousBucketEntity> entityList =
        costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    return entityList.stream().map(CostFactorContiguousBucketEntity::getNotation).toList();
  }

  private List<String> possibleValuesForDiscreteBuckets(String orgId, String costFactor) {
    List<CostFactorDiscreteBucketEntity> entityList =
        costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    return entityList.stream().map(CostFactorDiscreteBucketEntity::getNotation).toList();
  }

  private void handleBucketTypeNotFound(String orgId, String costFactor)
      throws CommonServiceException {
    log.error(COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
    throw new CommonServiceException(
        COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
  }

  @Transactional
  public CostItineraryAndFactorsDto updateCostItineraryAndFactorsActiveStatusByCostItinerary(
      String costItinerary, String orgId, boolean isActive) throws CommonServiceException {
    log.debug("-- inside updateCostItineraryAndFactorsActiveStatusById service --");
    var costItineraryAndFactorsEntity =
        getCostItineraryAndFactorsEntityByOrgIdAndCostItinerary(costItinerary, orgId);
    validateItineraryStatusForActivation(
        orgId,
        costItineraryAndFactorsEntity.getCostItinerary(),
        costItineraryAndFactorsEntity.getItineraryStatus());
    validateDefaultItinerary(costItinerary, orgId, isActive);
    validateOnlyItinerary(costItinerary, orgId, isActive);
    costItineraryAndFactorsEntity.setIsActive(isActive);
    return convertToCostItineraryAndFactorsDto(
        costItineraryAndFactorsRepository.save(costItineraryAndFactorsEntity));
  }

  private void validateDefaultItinerary(String costItinerary, String orgId, boolean isActive)
      throws CommonServiceException {
    var response =
        selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(costItinerary);
    if (!response.isEmpty() && !isActive) {
      log.error(COST_ITINERARY_IS_DEFAULT_ITINERARY);
      throw new CommonServiceException(
          COST_ITINERARY_IS_DEFAULT_ITINERARY,
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  private void validateOnlyItinerary(String costItinerary, String orgId, boolean isActive)
      throws CommonServiceException {
    var response =
        selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(costItinerary);
    if (!response.isEmpty() && !isActive) {
      log.error(COST_ITINERARY_IS_ONLY_ITINERARY);
      throw new CommonServiceException(
          COST_ITINERARY_IS_ONLY_ITINERARY,
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  private static void validateItineraryStatusForActivation(
      String orgId, String costItinerary, ItineraryStatusEnum itineraryStatus)
      throws CommonServiceException {
    if (ItineraryStatusEnum.DRAFT.equals(itineraryStatus)) {
      log.error(COST_ITINERARY_IN_DRAFT_STATE);
      throw new CommonServiceException(
          COST_ITINERARY_IN_DRAFT_STATE,
          HttpStatus.PRECONDITION_FAILED,
          0x1771,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              COST_ITINERARY,
              FieldError.builder().rejectedValue(costItinerary).build()));
    }
  }

  @Transactional
  public CostItineraryAndFactorsDto deleteCostItineraryAndFactors(String orgId, Long id)
      throws CommonServiceException {
    var costItineraryAndFactorsEntity =
        INSTANCE.toCostItineraryAndFactorsEntityFromDto(
            findCostItineraryAndFactorsByOrgIdAndId(orgId, id));
    validateCostItineraryAssociationInCostValueTable(
        orgId, costItineraryAndFactorsEntity.getCostItinerary(), DELETE_COST_ITINERARY);

    costItineraryAndFactorsRepository.delete(costItineraryAndFactorsEntity);
    return convertToCostItineraryAndFactorsDto(costItineraryAndFactorsEntity);
  }

  public List<CostItineraryAndFactorsCacheKeyDto> getCostItineraryAndFactorsCacheKeys(
      Integer limit) {
    var costItineraryAndFactorsEntities =
        costItineraryAndFactorsRepository.findAllCostItineraryAndFactorsEntities(limit);

    return INSTANCE.toCostItineraryAndFactorsCacheKeyResponseList(costItineraryAndFactorsEntities);
  }
}
