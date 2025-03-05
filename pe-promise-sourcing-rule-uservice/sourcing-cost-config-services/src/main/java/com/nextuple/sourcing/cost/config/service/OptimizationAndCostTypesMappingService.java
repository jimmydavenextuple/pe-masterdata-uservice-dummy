/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostTypeDashboardService.COST_TYPE;
import static com.nextuple.sourcing.cost.config.service.CostTypeDashboardService.COST_TYPES;
import static com.nextuple.sourcing.cost.config.service.CostTypeDashboardService.ORG_ID;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.OptimizationAndCostTypesMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.OptimizationAndCostTypesMapper;
import com.nextuple.sourcing.cost.config.domain.repository.OptimizationAndCostTypesMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.enums.LabelEnum;
import com.nextuple.sourcing.cost.config.inbound.CreateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizationAndCostTypesMappingService {
  private static final OptimizationAndCostTypesMapper INSTANCE =
      Mappers.getMapper(OptimizationAndCostTypesMapper.class);
  private final OptimizationAndCostTypesMappingRepository optimizationAndCostTypesMappingRepository;

  private final TenantCostTypeRepository tenantCostTypeRepository;

  public static final String ID = "id";
  public static final String OPT_STRATEGY = "optimizationStrategy";
  public static final String OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID =
      "Optimization and cost type mapping not found with org id and id";
  public static final String OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ORG_ID_AND_OPT_STRATEGY =
      "Optimization and cost type mapping not found with org id and optimization strategy";
  public static final String OPT_COST_TYPE_MAPPING_EXISTS_WITH_ORG_ID_AND_OPT_STRATEGY =
      "Optimization and cost type mapping exists with given org id and optimization strategy";
  public static final String OPTIMIZATION_STRATEGY_DOES_NOT_EXISTS =
      "Optimization strategy does not exist with given org id";
  public static final String COST_TYPES_DOES_NOT_EXISTS =
      "Cost types do not exist with given org id";
  public static final String ONLY_COST_TYPES_FOR_CBO =
      "Only cost types can be mapped to COST based optimization strategy.";
  public static final String BOTH_COST_AND_REVENUE_FOR_PROFIT_OS =
      "Both cost and revenue types should be mapped to PROFIT based optimization strategy.";
  private static final List<String> ALLOWED_OPTIMIZATION_STRATEGY_DETAILS =
      List.of("COST", "PROFIT");

  public OptimizationAndCostTypesMappingResponse createOptimizationAndCostTypesMapping(
      CreateOptimizationAndCostTypesMappingRequest createOptimizationAndCostTypesMappingRequest)
      throws CommonServiceException {
    var orgId = createOptimizationAndCostTypesMappingRequest.getOrgId();
    var optimizationStrategy =
        createOptimizationAndCostTypesMappingRequest.getOptimizationStrategy();
    validateOptimizationOptimizationAndCostTypesMappingInDB(orgId, optimizationStrategy);
    validateOptimizationStrategy(optimizationStrategy);
    //    validateCostTypes(
    //        orgId,
    //        createOptimizationAndCostTypesMappingRequest.getCostTypes(),
    //        createOptimizationAndCostTypesMappingRequest.getOptimizationStrategy());
    var optimizationAndCostTypesMappingEntity =
        INSTANCE.toOptimizationAndCostTypesMappingEntity(
            createOptimizationAndCostTypesMappingRequest);
    return INSTANCE.toOptimizationAndCostTypesMappingResponse(
        optimizationAndCostTypesMappingRepository.save(optimizationAndCostTypesMappingEntity));
  }

  private void validateCostTypesForGivenOptimizationStrategy(
      List<TenantCostTypeEntity> tenantCostTypeEntityList,
      String orgId,
      String optimizationStrategy,
      String costTypesToCheck)
      throws CommonServiceException {
    int costLabelCount =
        Math.toIntExact(
            tenantCostTypeEntityList.stream()
                .filter(
                    tenantCostTypeEntity -> LabelEnum.COST.equals(tenantCostTypeEntity.getLabel()))
                .count());

    int revenueLabelCount =
        Math.toIntExact(
            tenantCostTypeEntityList.stream()
                .filter(
                    tenantCostTypeEntity ->
                        LabelEnum.REVENUE.equals(tenantCostTypeEntity.getLabel()))
                .count());
    validateCostBasedOptimizationCostTypes(
        optimizationStrategy, costLabelCount, revenueLabelCount, costTypesToCheck, orgId);
    validateProfitBasedOptimizationCostTypes(
        optimizationStrategy, costLabelCount, revenueLabelCount, costTypesToCheck, orgId);
  }

  private void validateCostBasedOptimizationCostTypes(
      String optimizationStrategy,
      int costLabelCount,
      int revenueLabelCount,
      String costTypesToCheck,
      String orgId)
      throws CommonServiceException {
    if ("COST".equals(optimizationStrategy) && (revenueLabelCount > 0 || costLabelCount == 0)) {
      log.error(ONLY_COST_TYPES_FOR_CBO);
      throw new CommonServiceException(
          ONLY_COST_TYPES_FOR_CBO,
          HttpStatus.BAD_REQUEST,
          0x1876,
          Map.of(
              COST_TYPES,
              FieldError.builder().rejectedValue(costTypesToCheck).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  private void validateProfitBasedOptimizationCostTypes(
      String optimizationStrategy,
      int costLabelCount,
      int revenueLabelCount,
      String costTypesToCheck,
      String orgId)
      throws CommonServiceException {
    if ("PROFIT".equals(optimizationStrategy) && (costLabelCount == 0 || revenueLabelCount == 0)) {
      log.error(BOTH_COST_AND_REVENUE_FOR_PROFIT_OS);
      throw new CommonServiceException(
          BOTH_COST_AND_REVENUE_FOR_PROFIT_OS,
          HttpStatus.BAD_REQUEST,
          0x1877,
          Map.of(
              COST_TYPES,
              FieldError.builder().rejectedValue(costTypesToCheck).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  private void validateCostTypes(String orgId, String costTypesToCheck, String optimizationStrategy)
      throws CommonServiceException {
    Set<String> costTypesToCheckSet =
        Arrays.stream(costTypesToCheck.split(",")).collect(Collectors.toSet());
    List<TenantCostTypeEntity> tenantCostTypeEntityList =
        tenantCostTypeRepository.findByOrgIdAndCostTypeIn(orgId, costTypesToCheckSet);
    Set<String> foundCostTypeSet =
        tenantCostTypeEntityList.stream()
            .map(TenantCostTypeEntity::getCostType)
            .collect(Collectors.toSet());
    Set<String> notFoundCostTypes = new HashSet<>(costTypesToCheckSet);
    notFoundCostTypes.removeAll(foundCostTypeSet);
    if (!notFoundCostTypes.isEmpty()) {
      log.error(COST_TYPES_DOES_NOT_EXISTS);
      throw new CommonServiceException(
          COST_TYPES_DOES_NOT_EXISTS,
          HttpStatus.BAD_REQUEST,
          0x1875,
          Map.of(
              COST_TYPE,
              FieldError.builder().rejectedValue(String.join(",", notFoundCostTypes)).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
    validateCostTypesForGivenOptimizationStrategy(
        tenantCostTypeEntityList, orgId, optimizationStrategy, costTypesToCheck);
  }

  private void validateOptimizationStrategy(String optimizationStrategy)
      throws CommonServiceException {
    if (!ALLOWED_OPTIMIZATION_STRATEGY_DETAILS.contains(optimizationStrategy)) {
      log.error(OPTIMIZATION_STRATEGY_DOES_NOT_EXISTS);
      throw new CommonServiceException(
          OPTIMIZATION_STRATEGY_DOES_NOT_EXISTS,
          HttpStatus.BAD_REQUEST,
          0x1874,
          Map.of(OPT_STRATEGY, FieldError.builder().rejectedValue(optimizationStrategy).build()));
    }
  }

  private void validateOptimizationOptimizationAndCostTypesMappingInDB(
      String orgId, String optimizationStrategy) throws CommonServiceException {
    var optEntityFromDB =
        optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            orgId, optimizationStrategy);
    if (optEntityFromDB.isPresent()) {
      log.error(OPT_COST_TYPE_MAPPING_EXISTS_WITH_ORG_ID_AND_OPT_STRATEGY);
      throw new CommonServiceException(
          OPT_COST_TYPE_MAPPING_EXISTS_WITH_ORG_ID_AND_OPT_STRATEGY,
          HttpStatus.BAD_REQUEST,
          0x1873,
          Map.of(
              OPT_STRATEGY,
              FieldError.builder().rejectedValue(optimizationStrategy).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
  }

  private OptimizationAndCostTypesMappingEntity
      getOptimizationAndCostTypesMappingEntityByIdAndOrgId(Long id, String orgId)
          throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        optimizationAndCostTypesMappingRepository.findByIdAndOrgId(id, orgId);
    if (optimizationAndCostTypesMappingEntity.isEmpty()) {
      log.error("Optimization and cost type mapping not found with id and org id:{},{}", id, orgId);
      throw new CommonServiceException(
          OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ID_AND_ORG_ID,
          HttpStatus.NOT_FOUND,
          0x1871,
          Map.of(
              ID,
              FieldError.builder().rejectedValue(id).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
    return optimizationAndCostTypesMappingEntity.get();
  }

  public OptimizationAndCostTypesMappingResponse updateOptimizationAndCostTypesMappingByIdAndOrgId(
      Long id,
      String orgId,
      UpdateOptimizationAndCostTypesMappingRequest updateOptimizationAndCostTypesMappingRequest)
      throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        getOptimizationAndCostTypesMappingEntityByIdAndOrgId(id, orgId);
    //    validateCostTypes(
    //        orgId,
    //        updateOptimizationAndCostTypesMappingRequest.getCostTypes(),
    //        optimizationAndCostTypesMappingEntity.getOptimizationStrategy());
    INSTANCE.updateOptimizationAndCostTypesMappingRequestToEntity(
        updateOptimizationAndCostTypesMappingRequest, optimizationAndCostTypesMappingEntity);
    return INSTANCE.toOptimizationAndCostTypesMappingResponse(
        optimizationAndCostTypesMappingRepository.save(optimizationAndCostTypesMappingEntity));
  }

  private OptimizationAndCostTypesMappingEntity
      getOptimizationAndCostTypesMappingEntityByOrgIdAndOptimizationStrategy(
          String orgId, String optimizationStrategy) throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        optimizationAndCostTypesMappingRepository.findByOrgIdAndOptimizationStrategy(
            orgId, optimizationStrategy);
    if (optimizationAndCostTypesMappingEntity.isEmpty()) {
      log.error(
          "Optimization and cost type mapping not found with org id and optimization strategy:{},{}",
          orgId,
          optimizationStrategy);
      throw new CommonServiceException(
          OPT_COST_TYPE_MAPPING_NOT_FOUND_WITH_ORG_ID_AND_OPT_STRATEGY,
          HttpStatus.NOT_FOUND,
          0x1872,
          Map.of(
              OPT_STRATEGY,
              FieldError.builder().rejectedValue(optimizationStrategy).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build()));
    }
    return optimizationAndCostTypesMappingEntity.get();
  }

  public OptimizationAndCostTypesMappingResponse
      fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(
          String orgId, String optimizationStrategy) throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        getOptimizationAndCostTypesMappingEntityByOrgIdAndOptimizationStrategy(
            orgId, optimizationStrategy);
    return INSTANCE.toOptimizationAndCostTypesMappingResponse(
        optimizationAndCostTypesMappingEntity);
  }

  public OptimizationAndCostTypesMappingResponse fetchOptimizationAndCostTypesMappingByOrgIdAndId(
      String orgId, Long id) throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        getOptimizationAndCostTypesMappingEntityByIdAndOrgId(id, orgId);
    return INSTANCE.toOptimizationAndCostTypesMappingResponse(
        optimizationAndCostTypesMappingEntity);
  }

  public OptimizationAndCostTypesMappingResponse deleteOptimizationAndCostTypesMappingByOrgIdAndId(
      String orgId, Long id) throws CommonServiceException {
    var optimizationAndCostTypesMappingEntity =
        getOptimizationAndCostTypesMappingEntityByIdAndOrgId(id, orgId);
    optimizationAndCostTypesMappingRepository.delete(optimizationAndCostTypesMappingEntity);
    return INSTANCE.toOptimizationAndCostTypesMappingResponse(
        optimizationAndCostTypesMappingEntity);
  }
}
