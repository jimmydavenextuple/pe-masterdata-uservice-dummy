/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService.COLON_SPLIT_REGEX;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.SPLIT_REGEX;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeDetailsUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteOptimizationRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationRuleUpdationUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeDetailsUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PaginationAttribute;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.GroupDefinitionMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.NamedOptimizationStrategyMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.NamedOptimizationStrategyPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingConstraintPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NamedOptimizationStrategyService {

  private static final Logger logger =
      LoggerFactory.getLogger(NamedOptimizationStrategyService.class);

  private static final String ORG_ID = "orgId";
  private static final String GROUP_ID = "groupId";
  private static final String OPTIMIZATION_STRATEGY_NAME = "optimizationStrategyName";
  private static final String SOURCING_ATTRIBUTE_DEFINITION_ID = "sourcingAttributeDefinitionId";
  private static final String OPTIMIZATION_STRATEGY_DETAIL = "optimizationStrategyDetail";
  private static final String OPTIMIZATION_RULE_ID = "optimizationRuleId";
  private static final String ID = "id";
  private static final String ATTRIBUTE_ID = "attributeId";
  private static final List<String> ALLOWED_OPTIMIZATION_STRATEGY_DETAILS =
      List.of("SPEED", "PRIORITY", "SPLIT", "WEIGHTAGE", "COST", "PROFIT");

  private static final String DEFAULT_GROUP_ID = "DEFAULT";
  private static final String PROMISING_ENGINE_GROUP_ID = "PROMISING";
  private static final String OPTIMIZATION_STRATEGY_EXCEPTION_MESSAGE =
      "Named optimization strategy not found for given orgId and groupId";

  private static final String DEFAULT_OPTIMIZATION_STRATEGY_EXCEPTION_MESSAGE =
      "Default named optimization strategy not found for given orgId and groupId";
  private static final String STATUS_INACTIVE_MESSAGE =
      "Can't add the optimization strategy as all the required attributes values are not present";

  private static final String SOURCING_ATTRIBUTES_DEFINITION_ID = "sourcingAttributesDefinitionId";
  private static final String REQ_ATTRIBUTES_VALUE = "reqAttributesValue";
  private static final String REQ_ATTRIBUTES = "reqAttributes";
  private static final String GROUP_NAME = "groupName";
  private static final List<String> ALLOWED_CONSTRAINT_VALUES = List.of("0", "1");

  private static final String SOURCING_CONSTRAINT_VALUE = "sourcingConstraintValue";

  private static final NamedOptimizationStrategyMapper INSTANCE =
      Mappers.getMapper(NamedOptimizationStrategyMapper.class);
  private static final GroupDefinitionMapper GROUP_DEFINITION_MAPPER =
      Mappers.getMapper(GroupDefinitionMapper.class);

  private static final String OPTIMIZATION_RULE_PAGINATION_URL =
      "/ui/optimization-rules/%s?pageNo=%d&pageSize=%d";

  private final GroupDefinitionPersistenceService groupDefinitionPersistenceService;
  private final GroupDefinitionService groupDefinitionService;
  private final NamedOptimizationStrategyPersistenceService
      namedOptimizationStrategyPersistenceService;
  private final SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;
  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;
  private final SourcingConstraintPersistenceService sourcingConstraintPersistenceService;

  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;
  private final SourcingAttributeService sourcingAttributeService;

  @Value("${base-api-url}")
  private String baseApiUrl;

  public NamedOptimizationStrategyResponse processAddOptimizationStrategy(
      NamedOptimizationStrategyRequest namedOptimizationStrategyRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processAddOptimizationStrategy service --");
    if (!(namedOptimizationStrategyRequest.getGroupId().equalsIgnoreCase(DEFAULT_GROUP_ID)
        || namedOptimizationStrategyRequest
            .getGroupId()
            .equalsIgnoreCase(PROMISING_ENGINE_GROUP_ID)))
      validateGroupIdAndOrgId(
          namedOptimizationStrategyRequest.getGroupId(),
          namedOptimizationStrategyRequest.getOrgId());
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            namedOptimizationStrategyRequest.getOrgId(),
            namedOptimizationStrategyRequest.getGroupId());
    if (!namedOptimizationStrategyDomainDtos.isEmpty()) {
      logger.error(
          "Named optimization strategy already associated for given orgId :{} and groupId :{}",
          namedOptimizationStrategyRequest.getOrgId(),
          namedOptimizationStrategyRequest.getGroupId());

      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(namedOptimizationStrategyRequest.getOrgId()).build());
      errorMap.put(
          GROUP_ID,
          FieldError.builder()
              .rejectedValue(namedOptimizationStrategyRequest.getGroupId())
              .build());
      throw new CommonServiceException(
          "Named optimization strategy already associated for given orgId and groupId",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }

    validateOptimizationStrategyDetails(
        namedOptimizationStrategyRequest.getOptimizationStrategyDetails());
    validateStrategyName(
        namedOptimizationStrategyRequest.getOptimizationStrategyName(),
        namedOptimizationStrategyRequest.getOrgId());
    var namedOptimizationStrategy =
        INSTANCE.toNamedOptimizationStrategyEntity(namedOptimizationStrategyRequest);
    return INSTANCE.toNamedOptimizationStrategyResponse(
        namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            namedOptimizationStrategy));
  }

  private void validateGroupIdAndOrgId(String groupId, String orgId)
      throws PromiseEngineException, CommonServiceException {
    Long id = Long.parseLong(groupId);
    Optional<GroupDefinitionDomainDto> groupDefinitionDomainDto =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(id, orgId);
    if (groupDefinitionDomainDto.isEmpty()) {
      logger.error("Invalid groupId:{}", groupId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(GROUP_ID, FieldError.builder().rejectedValue(groupId).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException("Invalid groupId", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private void validateOptimizationStrategyDetails(String optimizationStrategyDetails)
      throws CommonServiceException {

    String[] optimizationStrategyDetailsList = optimizationStrategyDetails.split(SPLIT_REGEX);
    for (String optimizationStrategyDetail : optimizationStrategyDetailsList) {
      if (!ALLOWED_OPTIMIZATION_STRATEGY_DETAILS.contains(optimizationStrategyDetail)) {
        logger.error("Invalid named optimization strategy detail:{}", optimizationStrategyDetail);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            OPTIMIZATION_STRATEGY_DETAIL,
            FieldError.builder().rejectedValue(optimizationStrategyDetail).build());
        throw new CommonServiceException(
            "Invalid named optimization strategy detail", HttpStatus.NOT_FOUND, 0x1771, errorMap);
      }
    }
  }

  private void validateStrategyName(String optimizationStrategyName, String orgId)
      throws CommonServiceException, PromiseEngineException {
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndStrategyName(
            orgId, optimizationStrategyName);
    if (!namedOptimizationStrategyDomainDtos.isEmpty()) {
      logger.error("Optimization strategy name already exists:{}", optimizationStrategyName);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          OPTIMIZATION_STRATEGY_NAME,
          FieldError.builder().rejectedValue(optimizationStrategyName).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Optimization strategy name already exists", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  public NamedOptimizationStrategyResponse processGetOptimizationStrategyByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetOptimizationStrategyById service --");
    Optional<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDto =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByIdAndOrgId(
            id, orgId);
    if (namedOptimizationStrategyDomainDto.isEmpty()) {
      logger.error("Named optimization strategy not found for given id");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          "Named optimization strategy not found for given id",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }

    return INSTANCE.toNamedOptimizationStrategyResponse(namedOptimizationStrategyDomainDto.get());
  }

  public DetailedOptimizationStrategyResponse processGetOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetOptimizationStrategyByOrgIdAndGroupId service --");
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            orgId, groupId);

    NamedOptimizationStrategyDomainDto namedOptimizationStrategy =
        namedOptimizationStrategyDomainDtos.isEmpty()
            ? getDefaultOptimizationStrategy(orgId)
            : namedOptimizationStrategyDomainDtos.get(0);

    DetailedOptimizationStrategyResponse detailedOptimizationStrategyResponse =
        INSTANCE.toOptimizationStrategyResponse(namedOptimizationStrategy);

    // set group name and required attribute details
    detailedOptimizationStrategyResponse.setGroupName(
        fetchGroupName(orgId, namedOptimizationStrategy.getGroupId()));
    detailedOptimizationStrategyResponse.setRequiredAttributes(
        fetchRequiredAttributeDetails(orgId, namedOptimizationStrategy.getGroupId()));

    return detailedOptimizationStrategyResponse;
  }

  private List<AttributeInfo> fetchRequiredAttributeDetails(String orgId, String groupId)
      throws PromiseEngineException, CommonServiceException {
    List<AttributeInfo> attributeInfoList = new ArrayList<>();

    if (!groupId.equalsIgnoreCase(DEFAULT_GROUP_ID)) {
      Optional<GroupDefinitionDomainDto> groupDefinition =
          groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
              Long.valueOf(groupId), orgId);
      if (groupDefinition.isPresent()) {
        Optional<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinition =
            sourcingAttributesDefinitionPersistenceService
                .getSourcingRuleAttributesDefinitionEntityById(
                    groupDefinition.get().getSourcingAttributesDefinitionId());

        if (sourcingAttributesDefinition.isPresent()) {

          String[] reqAttributes =
              sourcingAttributesDefinition.get().getReqAttributes().split(SPLIT_REGEX);
          String[] optimisationRuleValues =
              groupDefinition.get().getReqAttributesValue().split(COLON_SPLIT_REGEX);

          getRequiredAttributes(orgId, attributeInfoList, optimisationRuleValues, reqAttributes);
        }
      }
    }

    return attributeInfoList;
  }

  private void getRequiredAttributes(
      String orgId,
      List<AttributeInfo> reqAttributeList,
      String[] optimisationRuleValues,
      String[] reqAttributes)
      throws PromiseEngineException, CommonServiceException {
    Set<String> uniqueReqAttributes = new HashSet<>();
    for (int attrKey = 0;
        attrKey < reqAttributes.length && attrKey < optimisationRuleValues.length;
        attrKey++) {
      if (uniqueReqAttributes.contains(reqAttributes[attrKey])) continue;
      uniqueReqAttributes.add(reqAttributes[attrKey]);
      // Get sourcingAttribute values for each reqAttribute
      Optional<SourcingAttributeDomainDto> sourcingAttributeDomainDto =
          sourcingAttributePersistenceService.getSourcingAttributeById(
              Long.parseLong(reqAttributes[attrKey]));
      if (sourcingAttributeDomainDto.isEmpty()) {
        logger.error("No mapping for the required attribute found in sourcing attribute");
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
        throw new CommonServiceException(
            "No mapping for the required attribute found in sourcing attribute",
            HttpStatus.NOT_FOUND,
            0x1771,
            errorMap);
      }
      AttributeInfo info =
          AttributeInfo.builder()
              .attributeId(reqAttributes[attrKey])
              .attributeValue(optimisationRuleValues[attrKey])
              .attributeName(sourcingAttributeDomainDto.get().getAttributeName())
              .build();

      reqAttributeList.add(info);
    }
  }

  private String fetchGroupName(String orgId, String groupId) throws PromiseEngineException {
    if (groupId.equalsIgnoreCase(DEFAULT_GROUP_ID)) return groupId;

    Optional<GroupDefinitionDomainDto> groupDefinition =
        groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(
            Long.valueOf(groupId), orgId);
    return groupDefinition.map(GroupDefinitionDomainDto::getGroupName).orElse(null);
  }

  private NamedOptimizationStrategyDomainDto getOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId) throws PromiseEngineException, CommonServiceException {
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            orgId, groupId);
    if (namedOptimizationStrategyDomainDtos.isEmpty()) {
      logger.error(OPTIMIZATION_STRATEGY_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(GROUP_ID, FieldError.builder().rejectedValue(groupId).build());
      throw new CommonServiceException(
          OPTIMIZATION_STRATEGY_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return namedOptimizationStrategyDomainDtos.get(0);
  }

  private NamedOptimizationStrategyDomainDto getDefaultOptimizationStrategy(String orgId)
      throws PromiseEngineException, CommonServiceException {
    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            orgId, DEFAULT_GROUP_ID);
    if (CollectionUtils.isEmpty(namedOptimizationStrategyDomainDtos)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(GROUP_ID, FieldError.builder().rejectedValue(DEFAULT_GROUP_ID).build());
      throw new CommonServiceException(
          DEFAULT_OPTIMIZATION_STRATEGY_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return namedOptimizationStrategyDomainDtos.get(0);
  }

  public NamedOptimizationStrategyResponse processUpdateOptimizationStrategy(
      String orgId,
      String groupId,
      NamedOptimizationStrategyUpdationRequest namedOptimizationStrategyUpdationRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processUpdateOptimizationStrategy service --");

    var namedOptimizationStrategyDomainDto =
        getOptimizationStrategyByOrgIdAndGroupId(orgId, groupId);
    if (!ObjectUtils.isEmpty(
        namedOptimizationStrategyUpdationRequest.getOptimizationStrategyDetails()))
      validateOptimizationStrategyDetails(
          namedOptimizationStrategyUpdationRequest.getOptimizationStrategyDetails());
    INSTANCE.updateNamedOptimizationStrategyEntity(
        namedOptimizationStrategyUpdationRequest, namedOptimizationStrategyDomainDto);
    return INSTANCE.toNamedOptimizationStrategyResponse(
        namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            namedOptimizationStrategyDomainDto));
  }

  public OptimizationRuleUIResponse processEditOptimizationRuleUI(
      String orgId, Long optimizationRuleId, OptimizationRuleUpdationUIRequest request)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processEditOptimizationRuleUI service --");
    Optional<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDto =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(
            optimizationRuleId);
    if (namedOptimizationStrategyDomainDto.isEmpty()) {
      logger.error("Optimization Rule not found for given optimizationRuleId");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          OPTIMIZATION_RULE_ID, FieldError.builder().rejectedValue(optimizationRuleId).build());
      throw new CommonServiceException(
          "Optimization Rule not found for given optimizationRuleId",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    if (!ObjectUtils.isEmpty(request.getOptimizationRuleName())) {
      namedOptimizationStrategyDomainDto
          .get()
          .setOptimizationStrategyName(request.getOptimizationRuleName());
    }
    if (!ObjectUtils.isEmpty(request.getStrategy())) {
      validateOptimizationStrategyDetails(request.getStrategy());
      namedOptimizationStrategyDomainDto
          .get()
          .setOptimizationStrategyDetails(request.getStrategy());
    }
    NamedOptimizationStrategyDomainDto namedOptimizationStrategyResponse =
        namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
            namedOptimizationStrategyDomainDto.get());
    Optional<GroupDefinitionDomainDto> groupDefinitionDomainDtoOptional =
        groupDefinitionPersistenceService.fetchGroupDefinitionById(
            Long.valueOf(namedOptimizationStrategyResponse.getGroupId()));
    Optional<GroupDefinitionResponse> groupDefinitionResponseOptional = Optional.empty();
    if (groupDefinitionDomainDtoOptional.isPresent()) {
      GroupDefinitionResponse response =
          GROUP_DEFINITION_MAPPER.toGroupDefinitionResponse(groupDefinitionDomainDtoOptional.get());
      groupDefinitionResponseOptional = Optional.of(response);
    }

    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(
            orgId, namedOptimizationStrategyResponse.getGroupId());
    // Updating values of existing constraints in the rule
    Map<String, String> sourcingConstraintEntitiesMap = new HashMap<>();
    sourcingConstraintDomainDtos.forEach(
        sourcingConstraintDomainDto ->
            sourcingConstraintEntitiesMap.put(
                String.valueOf(sourcingConstraintDomainDto.getSourcingConstraint()), "0"));
    if (Objects.nonNull(request.getConstraints())) {
      request
          .getConstraints()
          .forEach(constraint -> sourcingConstraintEntitiesMap.put(constraint.getValue(), "1"));
    }
    sourcingConstraintDomainDtos.forEach(
        sourcingConstraintDomainDto ->
            sourcingConstraintDomainDto.setSourcingConstraintValue(
                sourcingConstraintEntitiesMap.getOrDefault(
                    String.valueOf(sourcingConstraintDomainDto.getSourcingConstraint()), "0")));

    sourcingConstraintDomainDtos =
        addNewConstraintsToOptimizationRule(
            orgId,
            request.getConstraints(),
            namedOptimizationStrategyResponse,
            sourcingConstraintDomainDtos);

    List<SourcingConstraintDomainDto> updatedSourcingConstraintDomainDtos =
        sourcingConstraintPersistenceService.saveSourcingConstraintEntities(
            sourcingConstraintDomainDtos);

    return prepareOptimizationStrategyUIResponse(
        orgId,
        groupDefinitionResponseOptional,
        namedOptimizationStrategyResponse,
        updatedSourcingConstraintDomainDtos);
  }

  private List<SourcingConstraintDomainDto> addNewConstraintsToOptimizationRule(
      String orgId,
      List<AllConstraintUIDto> constraints,
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyResponse,
      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos) {
    Set<String> constraintsOfRule =
        sourcingConstraintDomainDtos.stream()
            .map(SourcingConstraintDomainDto::getSourcingConstraint)
            .map(SourcingConstraintEnum::name)
            .collect(Collectors.toSet());
    List<AllConstraintUIDto> newConstraintsInRequest = new ArrayList<>();
    if (Objects.nonNull(constraints)) {
      newConstraintsInRequest =
          constraints.stream()
              .filter(constraint -> !constraintsOfRule.contains(constraint.getValue()))
              .collect(Collectors.toList());
    }
    List<SourcingConstraintDomainDto> newlyAddedSourcingConstraintDomainDto =
        getNewlyAddedSourcingConstraintEntities(
            orgId, namedOptimizationStrategyResponse, newConstraintsInRequest);
    newlyAddedSourcingConstraintDomainDto.addAll(sourcingConstraintDomainDtos);
    return newlyAddedSourcingConstraintDomainDto;
  }

  private List<SourcingConstraintDomainDto> getNewlyAddedSourcingConstraintEntities(
      String orgId,
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyResponse,
      List<AllConstraintUIDto> newConstraintsInRequest) {
    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos = new ArrayList<>();
    if (!CollectionUtils.isEmpty(newConstraintsInRequest)) {
      String groupId = String.valueOf(namedOptimizationStrategyResponse.getGroupId());
      for (AllConstraintUIDto constraint : newConstraintsInRequest) {
        SourcingConstraintDomainDto sourcingConstraintDomainDto = new SourcingConstraintDomainDto();
        sourcingConstraintDomainDto.setOrgId(orgId);
        sourcingConstraintDomainDto.setGroupId(groupId);
        sourcingConstraintDomainDto.setSourcingConstraint(
            SourcingConstraintEnum.valueOf(constraint.getValue()));
        sourcingConstraintDomainDto.setSourcingConstraintValue("1");

        sourcingConstraintDomainDtos.add(sourcingConstraintDomainDto);
      }
    }
    return sourcingConstraintDomainDtos;
  }

  public NamedOptimizationStrategyResponse processDeleteOptimizationStrategy(
      String orgId, String groupId) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processDeleteOptimizationStrategy service --");

    var namedOptimizationStrategyDomainDto =
        getOptimizationStrategyByOrgIdAndGroupId(orgId, groupId);

    var optimizationStrategyResponse =
        INSTANCE.toNamedOptimizationStrategyResponse(namedOptimizationStrategyDomainDto);
    namedOptimizationStrategyPersistenceService.deleteOptimizationStrategy(
        namedOptimizationStrategyDomainDto);
    return optimizationStrategyResponse;
  }

  public Page<NamedOptimizationStrategyResponse> processGetOptimizationStrategyByOrgIdAndGroupIds(
      String orgId, List<String> groupIds, Pageable pageable)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processGetOptimizationStrategyByOrgIdAndGroupIds service --");
    List<NamedOptimizationStrategyResponse> responseList = new ArrayList<>();

    for (String groupId : groupIds) {
      Page<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyDomainDtos =
          namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
              orgId, groupId, pageable);
      responseList.addAll(
          namedOptimizationStrategyDomainDtos
              .map(INSTANCE::toNamedOptimizationStrategyResponse)
              .getContent());
    }
    if (responseList.isEmpty()) {
      logger.error("Named optimization strategies not found for given org id");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Named optimization strategies not found for given org id",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return new PageImpl<>(responseList, pageable, responseList.size());
  }

  public OptimizationRuleUIResponse getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
      String orgId, Long namedOptimizationStrategyId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId service --");

    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        processGetOptimizationStrategyByIdAndOrgId(namedOptimizationStrategyId, orgId);

    return getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponse(
        orgId, namedOptimizationStrategyResponse);
  }

  public OptimizationRuleUIResponse getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponse(
      String orgId, NamedOptimizationStrategyResponse namedOptimizationStrategyResponse)
      throws PromiseEngineException, CommonServiceException {
    // For a default optimization strategy, don't fetch group definition and sourcing rule
    // definition
    if (DEFAULT_GROUP_ID.equals(namedOptimizationStrategyResponse.getGroupId())) {
      return OptimizationRuleUIResponse.builder()
          .optimizationRuleId(namedOptimizationStrategyResponse.getId())
          .orgId(namedOptimizationStrategyResponse.getOrgId())
          .optimizationRuleName(namedOptimizationStrategyResponse.getOptimizationStrategyName())
          .strategy(namedOptimizationStrategyResponse.getOptimizationStrategyDetails())
          .groupId(namedOptimizationStrategyResponse.getGroupId())
          .build();
    }

    GroupDefinitionResponse groupDefinitionResponse =
        groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(
            Long.valueOf(namedOptimizationStrategyResponse.getGroupId()), orgId);

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            groupDefinitionResponse.getSourcingAttributesDefinitionId(), orgId);

    String[] requiredAttributeIds =
        sourcingAttributesDefinitionResponse.getReqAttributes().split(SPLIT_REGEX);
    String[] requiredAttributeValues =
        groupDefinitionResponse.getReqAttributesValue().split(COLON_SPLIT_REGEX);

    String[] optionalAttributeIds =
        StringUtils.hasLength(sourcingAttributesDefinitionResponse.getOptAttributes())
            ? sourcingAttributesDefinitionResponse.getOptAttributes().split(SPLIT_REGEX)
            : new String[0];
    String[] optionalAttributeValues = new String[optionalAttributeIds.length];
    String optionalAttributeValuesInRule = groupDefinitionResponse.getOptionalAttributesValue();
    if (optionalAttributeIds.length > 0 && StringUtils.hasLength(optionalAttributeValuesInRule)) {
      String[] existingOptionalValues = optionalAttributeValuesInRule.split(COLON_SPLIT_REGEX);
      System.arraycopy(
          existingOptionalValues, 0, optionalAttributeValues, 0, existingOptionalValues.length);
    }

    if (requiredAttributeIds.length != requiredAttributeValues.length) {
      logger.error("Error in getting optimization rule due to inconsistent attribute values.");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          REQ_ATTRIBUTES,
          FieldError.builder()
              .rejectedValue(sourcingAttributesDefinitionResponse.getReqAttributes())
              .build());
      errorMap.put(
          REQ_ATTRIBUTES_VALUE,
          FieldError.builder()
              .rejectedValue(groupDefinitionResponse.getReqAttributesValue())
              .build());
      throw new CommonServiceException(
          "Error in getting optimization rule due to inconsistent attribute values.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }

    List<AllConstraintUIDto> allConstraintUIDtoList = new ArrayList<>();
    List<AttributeDetailsUIResponse> requiredAttributeDetailsUIResponseList =
        getAttributeDetailsUIResponse(
            orgId,
            requiredAttributeIds,
            requiredAttributeValues,
            namedOptimizationStrategyResponse,
            allConstraintUIDtoList);

    List<AttributeDetailsUIResponse> optionalAttributeDetailsUIResponseList =
        getAttributeDetailsUIResponse(
            orgId,
            optionalAttributeIds,
            optionalAttributeValues,
            namedOptimizationStrategyResponse,
            allConstraintUIDtoList);

    return OptimizationRuleUIResponse.builder()
        .optimizationRuleId(namedOptimizationStrategyResponse.getId())
        .orgId(orgId)
        .sourcingAttributesDefinitionId(
            String.valueOf(groupDefinitionResponse.getSourcingAttributesDefinitionId()))
        .optimizationRuleName(namedOptimizationStrategyResponse.getOptimizationStrategyName())
        .requiredAttributes(requiredAttributeDetailsUIResponseList)
        .optionalAttributes(optionalAttributeDetailsUIResponseList)
        .groupId(namedOptimizationStrategyResponse.getGroupId())
        .strategy(namedOptimizationStrategyResponse.getOptimizationStrategyDetails())
        .constraints(allConstraintUIDtoList)
        .build();
  }

  private List<AttributeDetailsUIResponse> getAttributeDetailsUIResponse(
      String orgId,
      String[] attributeIds,
      String[] attributeValues,
      NamedOptimizationStrategyResponse namedOptimizationStrategyResponse,
      List<AllConstraintUIDto> allConstraintUIDtoList)
      throws PromiseEngineException, CommonServiceException {
    List<AttributeDetailsUIResponse> attributeDetailsUIResponseList = new ArrayList<>();
    for (int i = 0; i < attributeIds.length; i++) {
      SourcingAttributeResponse sourcingAttributeResponse =
          sourcingAttributeService.getSourcingAttributeByIdAndOrgId(
              Long.valueOf(attributeIds[i]), orgId);

      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
          sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(
              orgId, namedOptimizationStrategyResponse.getGroupId());

      for (SourcingConstraintDomainDto sourcingConstraintDomainDto :
          sourcingConstraintDomainDtoList) {
        if (sourcingConstraintDomainDto.getSourcingConstraintValue().equals("1")) {
          allConstraintUIDtoList.add(
              new AllConstraintUIDto(
                  sourcingConstraintDomainDto.getSourcingConstraint().getConstraintName(),
                  sourcingConstraintDomainDto.getSourcingConstraint().name()));
        }
      }

      attributeDetailsUIResponseList.add(
          new AttributeDetailsUIResponse(
              sourcingAttributeResponse.getId(),
              sourcingAttributeResponse.getAttributeName(),
              attributeValues[i]));
    }
    return attributeDetailsUIResponseList;
  }

  public List<AllConstraintUIDto> getAllUIConstraints() {
    logger.debug("-- inside getAllUIConstraints service --");

    List<AllConstraintUIDto> allConstraintUIDtoList = new ArrayList<>();

    for (SourcingConstraintEnum constraintEnum : SourcingConstraintEnum.values()) {
      allConstraintUIDtoList.add(
          new AllConstraintUIDto(constraintEnum.getConstraintName(), constraintEnum.name()));
    }
    return allConstraintUIDtoList;
  }

  @Transactional(readOnly = true)
  public PageResponse<OptimizationRuleUIResponse> getAllOptimizationRulesByOrgId(
      String orgId, PageParams pageParams) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside getAllOptimizationRuleByOrgId service --");

    Pageable pageable = PromiseSourcingRuleUtil.getPageableForEmptyPageSize(pageParams);

    List<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDtoList =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                orgId,
                SourcingAttributesDefinitionStatus.ACTIVE,
                SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    if (sourcingAttributesDefinitionDomainDtoList.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "No active sourcing rule attributes definition exists for given orgId",
          HttpStatus.NOT_FOUND,
          0X1771,
          errorMap);
    }
    List<String> groupIds = new ArrayList<>();
    for (SourcingAttributesDefinitionDomainDto entity : sourcingAttributesDefinitionDomainDtoList) {
      List<GroupDefinitionDomainDto> fetchedGroups =
          groupDefinitionPersistenceService
              .fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                  orgId, entity.getId());

      groupIds.addAll(
          fetchedGroups.stream()
              .map(groupEntity -> String.valueOf(groupEntity.getId()))
              .collect(Collectors.toList()));
    }

    // Also fetch default optimization strategy
    groupIds.add(DEFAULT_GROUP_ID);
    Page<NamedOptimizationStrategyResponse> namedOptimizationStrategyResponses =
        processGetOptimizationStrategyByOrgIdAndGroupIds(orgId, groupIds, pageable);

    List<OptimizationRuleUIResponse> optimizationRuleUIResponseList = new ArrayList<>();

    for (NamedOptimizationStrategyResponse namedOptimizationStrategyResponse :
        namedOptimizationStrategyResponses.getContent()) {

      optimizationRuleUIResponseList.add(
          getOptimizationRuleByOrgIdAndNamedOptimizationStrategyResponse(
              orgId, namedOptimizationStrategyResponse));
    }

    return this.buildOptimizationRulesUIPageResponse(
        orgId, optimizationRuleUIResponseList, namedOptimizationStrategyResponses, pageParams);
  }

  private PageResponse<OptimizationRuleUIResponse> buildOptimizationRulesUIPageResponse(
      String orgId,
      List<OptimizationRuleUIResponse> optimizationRuleUIResponseList,
      Page<NamedOptimizationStrategyResponse> namedOptimizationStrategyResponses,
      PageParams pageParams) {
    int currentPage =
        pageParams.getPageSize().isEmpty()
            ? 1
            : namedOptimizationStrategyResponses.getPageable().getPageNumber() + 1;

    int totalPages = namedOptimizationStrategyResponses.getTotalPages();
    Long pageSize =
        namedOptimizationStrategyResponses.getPageable().isUnpaged()
            ? namedOptimizationStrategyResponses.getTotalElements()
            : namedOptimizationStrategyResponses.getPageable().getPageSize();
    String paginationUrl = "%s%s".formatted(baseApiUrl, OPTIMIZATION_RULE_PAGINATION_URL);

    PageResponse<OptimizationRuleUIResponse> response = new PageResponse<>();

    response.setData(optimizationRuleUIResponseList);
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            totalPages,
            "next",
            paginationUrl.formatted(orgId, currentPage + 1, pageSize));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            totalPages,
            "previous",
            paginationUrl.formatted(orgId, currentPage - 1, pageSize));
    response.setPagination(
        PaginationAttribute.builder()
            .currentPage(currentPage)
            .totalPages(totalPages)
            .totalRecords(namedOptimizationStrategyResponses.getTotalElements())
            .next(nextUri)
            .previous(previousUri)
            .sortBy(pageParams.getSortBy().orElse(""))
            .sortOrder(pageParams.getSortOrder().orElse(""))
            .build());
    return response;
  }

  public List<OptimizationRuleUIResponse> processDeleteMultipleOptimizationStrategy(
      String orgId, DeleteOptimizationRulesRequest optimizationRuleIds)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside processDeleteMultipleOptimizationStrategy service --");

    List<Long> sourcingConstraintIdsToDelete = new ArrayList<>();
    List<Optional<NamedOptimizationStrategyDomainDto>> strategiesToDelete = new ArrayList<>();

    for (Long optimizationRuleId : optimizationRuleIds.getOptimizationRuleIds()) {
      Optional<NamedOptimizationStrategyDomainDto> entity =
          namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyById(
              optimizationRuleId);
      if (entity.isEmpty()) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ID, FieldError.builder().rejectedValue(optimizationRuleId).build());
        throw new CommonServiceException(
            "No optimization strategy entity found for given id",
            HttpStatus.NOT_FOUND,
            0X1771,
            errorMap);
      }
      strategiesToDelete.add(entity);
    }
    List<String> groupIds = new ArrayList<>();
    for (Optional<NamedOptimizationStrategyDomainDto> entity : strategiesToDelete) {
      if (entity.isPresent()) {
        groupIds.add(entity.get().getGroupId());
      }
    }
    List<Long> groupIdsToDelete = groupIds.stream().map(Long::valueOf).collect(Collectors.toList());

    List<OptimizationRuleUIResponse> optimizationStrategyUIResponses =
        prepareOptimizationRuleResponseForDeletion(orgId, strategiesToDelete);

    namedOptimizationStrategyPersistenceService.deleteByIdIn(
        optimizationRuleIds.getOptimizationRuleIds());

    for (String groupId : groupIds) {
      List<SourcingConstraintDomainDto> sourcingConstraintsToDelete =
          sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(orgId, groupId);
      for (SourcingConstraintDomainDto constraint : sourcingConstraintsToDelete) {
        sourcingConstraintIdsToDelete.add(constraint.getId());
      }
    }
    sourcingConstraintPersistenceService.deleteByIdIn(sourcingConstraintIdsToDelete);

    groupDefinitionPersistenceService.deleteByIdIn(groupIdsToDelete);

    return optimizationStrategyUIResponses;
  }

  private List<OptimizationRuleUIResponse> prepareOptimizationRuleResponseForDeletion(
      String orgId, List<Optional<NamedOptimizationStrategyDomainDto>> strategiesToDelete)
      throws PromiseEngineException, CommonServiceException {
    List<OptimizationRuleUIResponse> optimizationStrategyUIResponses = new ArrayList<>();
    for (Optional<NamedOptimizationStrategyDomainDto> strategy : strategiesToDelete) {
      if (strategy.isEmpty()) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        throw new CommonServiceException(
            "No optimization strategy entity found for given orgId",
            HttpStatus.NOT_FOUND,
            0X1771,
            errorMap);
      }
      OptimizationRuleUIResponse response = new OptimizationRuleUIResponse();
      response.setOptimizationRuleId(strategy.get().getId());
      response.setOrgId(orgId);
      Optional<GroupDefinitionDomainDto> groupDefinitionDomainDto =
          groupDefinitionPersistenceService.fetchGroupDefinitionById(
              Long.valueOf(strategy.get().getGroupId()));
      if (groupDefinitionDomainDto.isPresent()) {
        String sourcingAttributeDefinitionId =
            String.valueOf(groupDefinitionDomainDto.get().getSourcingAttributesDefinitionId());

        response.setSourcingAttributesDefinitionId(sourcingAttributeDefinitionId);
        response.setOptimizationRuleName(strategy.get().getOptimizationStrategyName());
        response.setRequiredAttributes(
            prepareReqAttributesResponse(
                orgId,
                Optional.of(
                    GROUP_DEFINITION_MAPPER.toGroupDefinitionResponse(
                        groupDefinitionDomainDto.get()))));
        response.setStrategy(strategy.get().getOptimizationStrategyDetails());
        List<AllConstraintUIDto> sourcingConstraints =
            prepareSourcingConstraintsResponse(orgId, strategy.get().getGroupId());
        response.setConstraints(
            sourcingConstraints.isEmpty() ? Collections.emptyList() : sourcingConstraints);

        optimizationStrategyUIResponses.add(response);
      }
    }
    return optimizationStrategyUIResponses;
  }

  public OptimizationRuleUIResponse createOptimizationRuleUI(
      String orgId, OptimizationStrategyUIRequest request)
      throws CommonServiceException, PromiseEngineException {
    logger.debug("-- inside createOptimizationRuleUI service --");

    validateStrategyName(orgId, request.getOptimizationRuleName());
    String groupName = request.getOptimizationRuleName().concat(" ").concat("group");

    GroupDefinitionResponse groupDefinitionResponse = addGroupDefinition(orgId, groupName, request);
    Long groupId = groupDefinitionResponse.getId();

    NamedOptimizationStrategyDomainDto optimizationStrategyResponse =
        addOptimizationStrategy(orgId, String.valueOf(groupId), request);

    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos = new ArrayList<>();
    if (Objects.nonNull(request.getConstraints()) && !request.getConstraints().isEmpty()) {
      sourcingConstraintDomainDtos =
          addSourcingConstraint(orgId, String.valueOf(groupId), request.getConstraints());
    }
    return prepareOptimizationStrategyUIResponse(
        orgId,
        Optional.of(groupDefinitionResponse),
        optimizationStrategyResponse,
        sourcingConstraintDomainDtos);
  }

  private GroupDefinitionResponse addGroupDefinition(
      String orgId, String groupName, OptimizationStrategyUIRequest request)
      throws PromiseEngineException, CommonServiceException {
    String reqAttributesValueForGroup =
        request.getRequiredAttributes().stream()
            .map(AttributeDetailsUIRequest::getAttributeValue)
            .collect(Collectors.joining(":"));
    String optionalAttributesValuesForGroup = null;
    if (Objects.nonNull(request.getOptionalAttributes())
        && !request.getOptionalAttributes().isEmpty())
      optionalAttributesValuesForGroup =
          request.getOptionalAttributes().stream()
              .map(AttributeDetailsUIRequest::getAttributeValue)
              .collect(Collectors.joining(":"));
    GroupDefinitionRequest groupDefinitionRequest =
        GroupDefinitionRequest.builder()
            .orgId(orgId)
            .groupName(groupName)
            .reqAttributesValue(reqAttributesValueForGroup)
            .optionalAttributesValue(optionalAttributesValuesForGroup)
            .sourcingAttributesDefinitionId(
                Long.valueOf(request.getSourcingAttributesDefinitionId()))
            .build();
    return groupDefinitionService.processAddGroupDefinition(groupDefinitionRequest);
  }

  private NamedOptimizationStrategyDomainDto addOptimizationStrategy(
      String orgId, String groupId, OptimizationStrategyUIRequest request)
      throws PromiseEngineException, CommonServiceException {
    validateGroupIdAndOrgId(String.valueOf(groupId), orgId);
    NamedOptimizationStrategyDomainDto optimizationStrategyDomainDto =
        new NamedOptimizationStrategyDomainDto();
    optimizationStrategyDomainDto.setOrgId(orgId);
    optimizationStrategyDomainDto.setGroupId(String.valueOf(groupId));
    optimizationStrategyDomainDto.setOptimizationStrategyName(request.getOptimizationRuleName());
    optimizationStrategyDomainDto.setOptimizationStrategyDetails(request.getStrategy());

    List<NamedOptimizationStrategyDomainDto> namedOptimizationStrategyEntities =
        namedOptimizationStrategyPersistenceService.fetchOptimizationStrategyByOrgIdAndGroupId(
            optimizationStrategyDomainDto.getOrgId(), optimizationStrategyDomainDto.getGroupId());
    if (!namedOptimizationStrategyEntities.isEmpty()) {
      logger.error(
          "Named optimization strategy already associated for given orgId :{} and groupId :{}",
          optimizationStrategyDomainDto.getOrgId(),
          optimizationStrategyDomainDto.getGroupId());

      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(optimizationStrategyDomainDto.getOrgId()).build());
      errorMap.put(
          GROUP_ID,
          FieldError.builder().rejectedValue(optimizationStrategyDomainDto.getGroupId()).build());
      throw new CommonServiceException(
          "Named optimization strategy already associated for given orgId and groupId",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
    validateOptimizationStrategyDetails(request.getStrategy());
    return namedOptimizationStrategyPersistenceService.saveOptimizationStrategy(
        optimizationStrategyDomainDto);
  }

  private List<SourcingConstraintDomainDto> addSourcingConstraint(
      String orgId, String groupId, List<AllConstraintUIDto> constraints)
      throws PromiseEngineException, CommonServiceException {
    List<SourcingConstraintDomainDto> sourcingConstraintEntities = new ArrayList<>();

    for (AllConstraintUIDto constraint : constraints) {
      SourcingConstraintDomainDto sourcingConstraintDomainDto = new SourcingConstraintDomainDto();
      sourcingConstraintDomainDto.setOrgId(orgId);
      sourcingConstraintDomainDto.setGroupId(String.valueOf(groupId));
      sourcingConstraintDomainDto.setSourcingConstraint(
          SourcingConstraintEnum.valueOf(constraint.getValue()));
      sourcingConstraintDomainDto.setSourcingConstraintValue("1");

      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtoList =
          sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
              sourcingConstraintDomainDto.getOrgId(),
              sourcingConstraintDomainDto.getGroupId(),
              sourcingConstraintDomainDto.getSourcingConstraint());

      if (!sourcingConstraintDomainDtoList.isEmpty()) {
        logger.error(
            "This constraint is already defined for given orgId :{}",
            sourcingConstraintDomainDto.getOrgId());
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            ORG_ID,
            FieldError.builder().rejectedValue(sourcingConstraintDomainDto.getOrgId()).build());
        throw new CommonServiceException(
            "This constraint is already defined for given orgId",
            HttpStatus.BAD_REQUEST,
            0x1771,
            errorMap);
      }
      validateSourcingConstraintValue(sourcingConstraintDomainDto.getSourcingConstraintValue());
      SourcingConstraintDomainDto sourcingConstraintDetailsResponse =
          sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
              sourcingConstraintDomainDto);
      sourcingConstraintEntities.add(sourcingConstraintDetailsResponse);
    }
    return sourcingConstraintEntities;
  }

  private void validateSourcingConstraintValue(String sourcingConstraintValue)
      throws CommonServiceException {

    if (!ALLOWED_CONSTRAINT_VALUES.contains(sourcingConstraintValue)) {
      logger.error("Invalid constraint value :{}", sourcingConstraintValue);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_CONSTRAINT_VALUE,
          FieldError.builder().rejectedValue(sourcingConstraintValue).build());
      throw new CommonServiceException(
          "Invalid constraint value", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private OptimizationRuleUIResponse prepareOptimizationStrategyUIResponse(
      String orgId,
      Optional<GroupDefinitionResponse> groupDefinitionResponse,
      NamedOptimizationStrategyDomainDto optimizationStrategyResponse,
      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos)
      throws PromiseEngineException, CommonServiceException {
    OptimizationRuleUIResponse response = new OptimizationRuleUIResponse();
    response.setOptimizationRuleId(optimizationStrategyResponse.getId());
    response.setOrgId(orgId);
    if (groupDefinitionResponse.isPresent()) {
      response.setSourcingAttributesDefinitionId(
          String.valueOf(groupDefinitionResponse.get().getSourcingAttributesDefinitionId()));
    }
    response.setOptimizationRuleName(optimizationStrategyResponse.getOptimizationStrategyName());
    response.setStrategy(optimizationStrategyResponse.getOptimizationStrategyDetails());
    response.setRequiredAttributes(prepareReqAttributesResponse(orgId, groupDefinitionResponse));
    response.setOptionalAttributes(prepareOptAttributesResponse(orgId, groupDefinitionResponse));
    if (CollectionUtils.isEmpty(sourcingConstraintDomainDtos)) {
      response.setConstraints(List.of());
    } else {
      List<AllConstraintUIDto> sourcingConstraints = new ArrayList<>();
      sourcingConstraintDomainDtos.forEach(
          sourcingConstraintDomainDto -> {
            if (sourcingConstraintDomainDto.getSourcingConstraintValue().equals("1"))
              sourcingConstraints.add(
                  AllConstraintUIDto.builder()
                      .label(
                          sourcingConstraintDomainDto.getSourcingConstraint().getConstraintName())
                      .value(String.valueOf(sourcingConstraintDomainDto.getSourcingConstraint()))
                      .build());
          });
      response.setConstraints(
          sourcingConstraints.isEmpty() ? Collections.emptyList() : sourcingConstraints);
    }
    return response;
  }

  private List<AllConstraintUIDto> prepareSourcingConstraintsResponse(String orgId, String groupId)
      throws PromiseEngineException {
    List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos =
        sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(orgId, groupId);
    List<AllConstraintUIDto> constraints = new ArrayList<>();
    if (!sourcingConstraintDomainDtos.isEmpty()) {
      for (SourcingConstraintDomainDto entity : sourcingConstraintDomainDtos) {
        AllConstraintUIDto allConstraintUIDto = new AllConstraintUIDto();
        allConstraintUIDto.setLabel(entity.getSourcingConstraint().constraintName);
        allConstraintUIDto.setValue(String.valueOf(entity.getSourcingConstraint()));
        constraints.add(allConstraintUIDto);
      }
    }
    return constraints;
  }

  private List<AttributeDetailsUIResponse> prepareOptAttributesResponse(
      String orgId, Optional<GroupDefinitionResponse> groupDefinitionDomainDto)
      throws PromiseEngineException, CommonServiceException {
    validateEmptyGroupDefinition(orgId, groupDefinitionDomainDto);
    String optAttributeValue = groupDefinitionDomainDto.get().getOptionalAttributesValue();
    if (!StringUtils.hasLength(optAttributeValue)) return Collections.emptyList();
    Long sourcingAttributesDefinitionId =
        groupDefinitionDomainDto.get().getSourcingAttributesDefinitionId();
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        getSourcingAttributesDefinitionDomainDto(sourcingAttributesDefinitionId);
    if (!StringUtils.hasLength(sourcingAttributesDefinitionDomainDto.getOptAttributes()))
      return Collections.emptyList();
    List<String> attributeValues = Arrays.asList(optAttributeValue.split(":"));
    List<String> attributeIds =
        Arrays.asList(sourcingAttributesDefinitionDomainDto.getOptAttributes().split(","));
    return getAttributeDetailsList(orgId, attributeValues, attributeIds);
  }

  private List<AttributeDetailsUIResponse> prepareReqAttributesResponse(
      String orgId, Optional<GroupDefinitionResponse> groupDefinitionDomainDto)
      throws PromiseEngineException, CommonServiceException {
    validateEmptyGroupDefinition(orgId, groupDefinitionDomainDto);
    String reqAttributeValue = groupDefinitionDomainDto.get().getReqAttributesValue();
    Long sourcingAttributesDefinitionId =
        groupDefinitionDomainDto.get().getSourcingAttributesDefinitionId();
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        getSourcingAttributesDefinitionDomainDto(sourcingAttributesDefinitionId);
    List<String> attributeValues = Arrays.asList(reqAttributeValue.split(":"));
    List<String> attributeIds =
        Arrays.asList(sourcingAttributesDefinitionDomainDto.getReqAttributes().split(","));
    return getAttributeDetailsList(orgId, attributeValues, attributeIds);
  }

  private static void validateEmptyGroupDefinition(
      String orgId, Optional<GroupDefinitionResponse> groupDefinitionDomainDto)
      throws CommonServiceException {
    if (groupDefinitionDomainDto.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "No group definition entity found for the given orgId",
          HttpStatus.NOT_FOUND,
          0X1771,
          errorMap);
    }
  }

  @NotNull
  private List<AttributeDetailsUIResponse> getAttributeDetailsList(
      String orgId, List<String> attributeValues, List<String> attributeIds)
      throws PromiseEngineException, CommonServiceException {
    List<AttributeDetailsUIResponse> reqAttributes = new ArrayList<>();
    for (int i = 0; i < attributeValues.size() && i < attributeIds.size(); i++) {
      Long id = Long.parseLong(attributeIds.get(i).trim());
      Optional<SourcingAttributeDomainDto> sourcingAttributeDomainDto =
          sourcingAttributePersistenceService.getSourcingAttributeById(id);
      if (sourcingAttributeDomainDto.isEmpty()) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(ATTRIBUTE_ID, FieldError.builder().rejectedValue(id).build());
        throw new CommonServiceException(
            "No attribute entity found for given orgId and attribute id",
            HttpStatus.NOT_FOUND,
            0X1771,
            errorMap);
      }
      AttributeDetailsUIResponse attribute =
          getAttributeDetailsUIResponse(
              id, sourcingAttributeDomainDto.get(), attributeValues.get(i));
      reqAttributes.add(attribute);
    }
    return reqAttributes;
  }

  private SourcingAttributesDefinitionDomainDto getSourcingAttributesDefinitionDomainDto(
      Long sourcingAttributesDefinitionId) throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDto =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityById(sourcingAttributesDefinitionId);
    if (sourcingAttributesDefinitionDomainDto.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_ATTRIBUTE_DEFINITION_ID,
          FieldError.builder().rejectedValue(sourcingAttributesDefinitionId).build());
      throw new CommonServiceException(
          "No sourcing attributes definition entity found for the given sourcingAttributeDefinitionId",
          HttpStatus.NOT_FOUND,
          0X1772,
          errorMap);
    }
    return sourcingAttributesDefinitionDomainDto.get();
  }

  @NotNull
  private static AttributeDetailsUIResponse getAttributeDetailsUIResponse(
      Long id, SourcingAttributeDomainDto sourcingAttributeDomainDto, String attributeValue) {
    AttributeDetailsUIResponse attribute = new AttributeDetailsUIResponse();
    attribute.setAttributeId(id);
    attribute.setAttributeName(sourcingAttributeDomainDto.getAttributeName());
    attribute.setAttributeValue(attributeValue);
    return attribute;
  }
}
