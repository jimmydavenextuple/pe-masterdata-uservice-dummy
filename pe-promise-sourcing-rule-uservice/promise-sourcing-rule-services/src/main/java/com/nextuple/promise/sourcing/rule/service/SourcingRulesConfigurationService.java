/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingRuleIdRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AllSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateNodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingRuleConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupDetailsInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRulesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService;
import com.nextuple.promise.sourcing.rule.domain.mapper.NodeGroupMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.NodePriorityMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingAttributeMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingAttributesDefinitionMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingRuleDetailsMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingRulesConfigurationMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.*;
import com.nextuple.promise.sourcing.rule.persistence.service.*;
import com.nextuple.promise.sourcing.rule.service.impl.RuleRetrievalFactory;
import com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SourcingRulesConfigurationService {

  public static final String SPLIT_REGEX = "\\s*,\\s*";
  public static final String COLON_SPLIT_REGEX = "\\s*:\\s*";
  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRulesConfigurationService.class);
  private static final String ID = "id";
  private static final String ORG_ID = "orgId";
  private static final String SOURCING_ATTRIBUTES_DEFINITION_ID = "sourcingAttributesDefinitionId";
  private static final String NODE_GROUP_ID = "nodeGroupId";
  private static final String SOURCING_RULE = "sourcingRule";
  private static final String SOURCING_RULE_NAME = "sourcingRuleName";
  private static final String SOURCING_RULE_ID = "sourcingRuleId";
  private static final String DEFAULT_SOURCING_RULE = "DEFAULT";
  private static final String SOURCING_RULE_EXCEPTION_MESSAGE = "Sourcing rule not found";
  private static final String SOURCING_RULES_NOT_FOUND_EXCEPTION_MESSAGE =
      "No matching sourcing rules found";
  private static final SourcingRulesConfigurationMapper INSTANCE =
      Mappers.getMapper(SourcingRulesConfigurationMapper.class);
  private static final NodeGroupMapper INSTANCE_NODE_GROUP =
      Mappers.getMapper(NodeGroupMapper.class);
  private static final NodePriorityMapper INSTANCE_NODE_PRIORITY =
      Mappers.getMapper(NodePriorityMapper.class);
  private static final SourcingAttributesDefinitionMapper INSTANCE_ATTRIBUTE_MAPPER =
      Mappers.getMapper(SourcingAttributesDefinitionMapper.class);
  private static final SourcingAttributeMapper INSTANCE_SOURCING_ATTRIBUTE_MAPPER =
      Mappers.getMapper(SourcingAttributeMapper.class);

  private static final SourcingRuleDetailsMapper INSTANCE_SOURCING_RULE_DETAILS =
      Mappers.getMapper(SourcingRuleDetailsMapper.class);
  private static final SourcingAttributesDefinitionMapper ATTRIBUTES_DEFINITION_MAPPER =
      Mappers.getMapper(SourcingAttributesDefinitionMapper.class);

  private final SourcingAttributesDefinitionService sourcingAttrDefService;
  private final SourcingRulesConfigurationPersistenceService rulesConfigPersistenceService;
  private final SourcingAttributesDefinitionPersistenceService sourAttrDefPersistenceService;
  private final NodeGroupPersistenceService nodeGroupPersistenceService;
  private final NodePriorityPersistenceService nodePriorityPersistenceService;
  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;
  private final SourcingRuleDetailsPersistenceService sourcingRuleDetailsPersistenceService;
  private final AttributeValuesPersistenceService attributeValuesPersistenceService;
  private final RuleRetrievalFactory ruleRetrievalFactory;
  private final FetchRulesUtil fetchRulesUtil;

  public SourcingRuleDetails processConfigureSourcingRule(
      RulesConfigurationRequest rulesConfigurationRequest)
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleUtil.validateAttributeValuesFormat(
        rulesConfigurationRequest.getSourcingRule());
    validateNodeGroups(
        rulesConfigurationRequest.getNodeGroups(), rulesConfigurationRequest.getOrgId());
    Optional<SourcingRulesConfigurationDomainDto> rulesConfigurationDomainDtoOptional =
        rulesConfigPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndExactMatchSourcingRule(
                rulesConfigurationRequest.getOrgId(),
                rulesConfigurationRequest.getSourcingAttributesDefinitionId(),
                rulesConfigurationRequest.getSourcingRule());
    if (rulesConfigurationDomainDtoOptional.isEmpty()) {
      validateSourcingRuleName(
          rulesConfigurationRequest.getOrgId(),
          rulesConfigurationRequest.getSourcingAttributesDefinitionId(),
          rulesConfigurationRequest.getSourcingRuleName());
      if (!rulesConfigurationRequest.getSourcingRule().equalsIgnoreCase(DEFAULT_SOURCING_RULE))
        validateSourcingAttributesDefinitionId(
            rulesConfigurationRequest.getSourcingRule(),
            rulesConfigurationRequest.getSourcingAttributesDefinitionId(),
            rulesConfigurationRequest.getOrgId());

      var sourcingRuleEntity =
          rulesConfigPersistenceService.saveSourcingRule(
              INSTANCE.toRulesConfigurationEntity(rulesConfigurationRequest));
      var sourcingRuleDetailsEntity =
          INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetailsEntity(rulesConfigurationRequest);
      sourcingRuleDetailsEntity.setSourcingRuleId(sourcingRuleEntity.getId());
      SourcingRuleDetails sourcingRuleDetails =
          INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(
              sourcingRuleDetailsPersistenceService.saveSourcingNodes(sourcingRuleDetailsEntity));
      sourcingRuleDetails.setSourcingRule(sourcingRuleEntity.getSourcingRule());
      sourcingRuleDetails.setSourcingRuleName(sourcingRuleEntity.getSourcingRuleName());
      sourcingRuleDetails.setSourcingAttributesDefinitionId(
          sourcingRuleEntity.getSourcingAttributesDefinitionId());
      return sourcingRuleDetails;
    }
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        rulesConfigurationDomainDtoOptional.get();
    validateSourcingRuleRequestByRuleName(
        rulesConfigurationRequest.getSourcingRuleName(),
        rulesConfigurationRequest.getSourcingRule(),
        sourcingRulesConfigurationEntity.getSourcingRuleName());
    checkForNodeGroupAssociation(rulesConfigurationRequest, sourcingRulesConfigurationEntity);
    var sourcingRuleDetailsEntity =
        INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetailsEntity(rulesConfigurationRequest);
    sourcingRuleDetailsEntity.setSourcingRuleId(sourcingRulesConfigurationEntity.getId());
    SourcingRuleDetails sourcingRuleDetails =
        INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(
            sourcingRuleDetailsPersistenceService.saveSourcingNodes(sourcingRuleDetailsEntity));
    sourcingRuleDetails.setSourcingRule(sourcingRulesConfigurationEntity.getSourcingRule());
    sourcingRuleDetails.setSourcingRuleName(sourcingRulesConfigurationEntity.getSourcingRuleName());
    sourcingRuleDetails.setSourcingAttributesDefinitionId(
        sourcingRulesConfigurationEntity.getSourcingAttributesDefinitionId());
    return sourcingRuleDetails;
  }

  private void checkForNodeGroupAssociation(
      RulesConfigurationRequest rulesConfigurationRequest,
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity)
      throws PromiseEngineException, CommonServiceException {

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntityList =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(
            sourcingRulesConfigurationEntity.getOrgId(), sourcingRulesConfigurationEntity.getId());

    if (!sourcingRuleDetailsEntityList.isEmpty()) {
      List<String> nodeGroupIds = new ArrayList<>();
      sourcingRuleDetailsEntityList.forEach(
          sourcingRuleDetailsEntity -> nodeGroupIds.add(sourcingRuleDetailsEntity.getNodeGroups()));
      if (nodeGroupIds.contains(rulesConfigurationRequest.getNodeGroups())) {
        logger.error(
            "Combination of sourcing rule and node group already exists : {}",
            rulesConfigurationRequest.getNodeGroups());
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            NODE_GROUP_ID,
            FieldError.builder().rejectedValue(rulesConfigurationRequest.getNodeGroups()).build());
        throw new CommonServiceException(
            "Combination of sourcing rule and node group already exists",
            HttpStatus.BAD_REQUEST,
            0X1771,
            errorMap);
      }
    }
  }

  private void validateSourcingRuleRequestByRuleName(
      String sourcingRuleNameFromRequest, String sourcingRule, String sourcingRuleNameFromEntity)
      throws CommonServiceException {
    if (!sourcingRuleNameFromRequest.equals(sourcingRuleNameFromEntity)) {
      String errorMessage =
          "Can’t create/update sourcing rule as sourcing rule %s already has the same order attributes value"
              .formatted(sourcingRuleNameFromEntity);
      throw new CommonServiceException(
          errorMessage,
          HttpStatus.BAD_REQUEST,
          0X1771,
          Map.of(SOURCING_RULE, FieldError.builder().rejectedValue(sourcingRule).build()));
    }
  }

  private void validateSourcingRuleName(
      String orgId, Long sourcingAttributesDefinitionId, String sourcingRuleName)
      throws PromiseEngineException, CommonServiceException {
    SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity =
        rulesConfigPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                orgId, sourcingAttributesDefinitionId, sourcingRuleName);
    if (Objects.nonNull(sourcingRulesConfigurationEntity)) {
      logger.error(
          "Sourcing Rule exists with given sourcingRuleName : {}  with orgId : {}",
          sourcingRuleName,
          orgId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_RULE_NAME, FieldError.builder().rejectedValue(sourcingRuleName).build());
      throw new CommonServiceException(
          "Sourcing Rule exists with given sourcingRuleName",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  private void validateNodeGroups(String id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    PromiseSourcingRuleUtil.validateNodeGroup(id);
    Optional<NodeGroupDomainDto> nodeGroupEntity =
        nodeGroupPersistenceService.fetchNodeGroupById(Long.valueOf(id));

    if (nodeGroupEntity.isEmpty() || !nodeGroupEntity.get().getOrgId().equals(orgId)) {
      logger.error("Node group doesn't exist with given nodeGroupId :{} and orgId : {}", id, orgId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          "Node group doesn't exist", HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    List<NodePriorityDomainDto> nodePriorityEntityList =
        nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
            orgId, Long.valueOf(id));
    if (nodePriorityEntityList.isEmpty()) {
      logger.error("Node group with given id :{} exists but no nodes are associated to it", id);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(NODE_GROUP_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          "Node group exist but no nodes are associated to it",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  private SourcingAttributesDefinitionDomainDto
      validateSourcingAttributesDefinitionIdForFetchSourcingRules(
          FetchSourcingRulesRequest fetchSourcingRulesRequest)
          throws CommonServiceException, PromiseEngineException {
    String sourcingRule = getSourcingRuleValue(fetchSourcingRulesRequest);
    Long sourcingAttributesDefinitionId =
        fetchSourcingRulesRequest.getSourcingAttributesDefinitionId();
    String requiredAttributesInRule =
        fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getRequiredAttributesValue();
    validateBlankValues(requiredAttributesInRule);
    String orgId = fetchSourcingRulesRequest.getOrgId();
    SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionEntity =
        validateSourcingAttributesDefinitionId(sourcingRule, sourcingAttributesDefinitionId, orgId);
    String[] requiredAttributeValuesList = requiredAttributesInRule.split(COLON_SPLIT_REGEX);
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionEntity.getReqAttributes().split(SPLIT_REGEX);
    if (requiredAttributeValuesList.length > requiredAttributeReferencesList.length) {
      logger.error(
          "Can't fetch the sourcing rule as required attributes values do not match the sourcing attribute definition.");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SOURCING_RULE, FieldError.builder().rejectedValue(sourcingRule).build());
      throw new CommonServiceException(
          "Can't fetch the sourcing rule as required attributes values do not match the sourcing attribute definition.",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    return existingSourcingAttributesDefinitionEntity;
  }

  private SourcingAttributesDefinitionDomainDto fetchAttributeDefinition(
      String orgId, Long definitionId, SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto> existingAttributesDefinition =
        sourAttrDefPersistenceService.getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(
            definitionId, orgId);
    String errorMessage =
        "Invalid attributes definition for given scope: %s / Sourcing attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : %s"
            .formatted(scope, definitionId);
    PromiseSourcingRuleUtil.handleInvalidSourcingAttributeDefinition(
        definitionId, scope, existingAttributesDefinition, 0x1771, errorMessage);

    return existingAttributesDefinition.get();
  }

  private SourcingAttributesDefinitionDomainDto validateSourcingAttributesDefinitionId(
      String sourcingRule, Long sourcingAttributesDefinitionId, String orgId)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDto =
        fetchAttributeDefinition(
            orgId,
            sourcingAttributesDefinitionId,
            SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionDto.getReqAttributes().split(SPLIT_REGEX);
    int optionalAttributesLength = 0;
    if (StringUtils.hasLength(existingSourcingAttributesDefinitionDto.getOptAttributes())) {
      optionalAttributesLength =
          existingSourcingAttributesDefinitionDto.getOptAttributes().split(SPLIT_REGEX).length;
    }
    String[] attributeValuesList = sourcingRule.split(COLON_SPLIT_REGEX);
    PromiseSourcingRuleUtil.checkForRequiredAttributesLength(
        sourcingRule,
        requiredAttributeReferencesList,
        attributeValuesList,
        "Can't add or fetch the sourcing rule as all the required attributes values are not present",
        SOURCING_RULE,
        0x1771);
    PromiseSourcingRuleUtil.checkForTotalAttributesLength(
        sourcingRule,
        requiredAttributeReferencesList,
        optionalAttributesLength,
        attributeValuesList,
        "Can't add the sourcing rule as length of attributes is more than optional and required attributes combined",
        SOURCING_RULE,
        0x1771);
    return existingSourcingAttributesDefinitionDto;
  }

  private void validateBlankValues(String sourcingRule) throws CommonServiceException {
    long colonCount = sourcingRule.chars().filter(ch -> ch == ':').count();
    String[] attributes = sourcingRule.split(COLON_SPLIT_REGEX);
    boolean blankCheck = Arrays.stream(attributes).anyMatch(value -> !StringUtils.hasLength(value));
    if (blankCheck || colonCount >= attributes.length) {
      logger.error("Can't fetch the sourcing rule as sourcing rule contains blank values");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SOURCING_RULE, FieldError.builder().rejectedValue(sourcingRule).build());
      throw new CommonServiceException(
          "Can't fetch the sourcing rule as sourcing rule contains blank values",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public SourcingRuleDetails processGetSourcingRuleDetailsByIdandOrgId(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntity =
        sourcingRuleDetailsPersistenceService.getSourcingRuleDetailsByIdAndOrgId(id, orgId);
    if (sourcingRuleDetailsEntity.isEmpty()) {
      logger.error("Sourcing rule details record not found for given id:{}", id);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          "Sourcing rule details record not found for given id",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    Optional<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntity =
        rulesConfigPersistenceService.getSourcingRuleByIdAndOrgId(
            sourcingRuleDetailsEntity.get().getSourcingRuleId(),
            sourcingRuleDetailsEntity.get().getOrgId());
    if (sourcingRulesConfigurationEntity.isEmpty()) {
      logger.error(
          "Invalid sourcing rule id:{}", sourcingRuleDetailsEntity.get().getSourcingRuleId());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          SOURCING_RULE_ID,
          FieldError.builder()
              .rejectedValue(sourcingRuleDetailsEntity.get().getSourcingRuleId())
              .build());
      throw new CommonServiceException(
          "Invalid sourcing rule id", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    SourcingRuleDetails sourcingRuleDetails =
        INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(sourcingRuleDetailsEntity.get());
    sourcingRuleDetails.setSourcingRule(sourcingRulesConfigurationEntity.get().getSourcingRule());
    sourcingRuleDetails.setSourcingRuleName(
        sourcingRulesConfigurationEntity.get().getSourcingRuleName());
    sourcingRuleDetails.setSourcingAttributesDefinitionId(
        sourcingRulesConfigurationEntity.get().getSourcingAttributesDefinitionId());
    return sourcingRuleDetails;
  }

  public List<AllSourcingRulesResponse> processGetAllSourcingRuleDetailsByOrgId(String orgId)
      throws PromiseEngineException, CommonServiceException {

    List<AllSourcingRulesResponse> allSourcingRulesResponseList = new ArrayList<>();
    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        sourcingRuleDetailsPersistenceService.getAllSourcingRuleByOrgId(orgId);
    List<SourcingRuleDetails> sourcingRuleDetailsList = new ArrayList<>();

    for (SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity : sourcingRuleDetailsEntities) {
      SourcingRuleDetails sourcingRuleDetails =
          INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(sourcingRuleDetailsEntity);

      Optional<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntity =
          rulesConfigPersistenceService.getSourcingRuleById(
              sourcingRuleDetailsEntity.getSourcingRuleId());

      if (sourcingRulesConfigurationEntity.isPresent()) {
        sourcingRuleDetails.setSourcingRule(
            sourcingRulesConfigurationEntity.get().getSourcingRule());
        sourcingRuleDetails.setSourcingRuleName(
            sourcingRulesConfigurationEntity.get().getSourcingRuleName());
        sourcingRuleDetails.setSourcingAttributesDefinitionId(
            sourcingRulesConfigurationEntity.get().getSourcingAttributesDefinitionId());
        sourcingRuleDetailsList.add(sourcingRuleDetails);
      }
    }

    Map<String, List<SourcingRuleDetails>> rulesConfigurationEntityMap =
        sourcingRuleDetailsList.stream()
            .collect(Collectors.groupingBy(SourcingRuleDetails::getSourcingRule));
    getSourcingRulesDetails(orgId, rulesConfigurationEntityMap, allSourcingRulesResponseList, true);

    return allSourcingRulesResponseList;
  }

  public AllSourcingRulesResponse processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
      String orgId, Long sourcingRuleId) throws PromiseEngineException, CommonServiceException {

    List<AllSourcingRulesResponse> allSourcingRulesResponseList = new ArrayList<>();

    Optional<SourcingRulesConfigurationDomainDto> rulesConfigurationEntity =
        rulesConfigPersistenceService.getSourcingRuleByIdAndOrgId(sourcingRuleId, orgId);
    if (rulesConfigurationEntity.isEmpty()) {
      logger.error(SOURCING_RULE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          SOURCING_RULE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    List<SourcingRuleDetails> sourcingRuleDetailsList = new ArrayList<>();
    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(orgId, sourcingRuleId);
    for (SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity : sourcingRuleDetailsEntities) {
      SourcingRuleDetails sourcingRuleDetails =
          INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(sourcingRuleDetailsEntity);
      sourcingRuleDetails.setSourcingRule(rulesConfigurationEntity.get().getSourcingRule());
      sourcingRuleDetails.setSourcingRuleName(rulesConfigurationEntity.get().getSourcingRuleName());
      sourcingRuleDetails.setSourcingAttributesDefinitionId(
          rulesConfigurationEntity.get().getSourcingAttributesDefinitionId());
      sourcingRuleDetailsList.add(sourcingRuleDetails);
    }
    Map<String, List<SourcingRuleDetails>> rulesConfigurationEntityMap =
        Map.of(rulesConfigurationEntity.get().getSourcingRule(), sourcingRuleDetailsList);
    getSourcingRulesDetails(
        orgId, rulesConfigurationEntityMap, allSourcingRulesResponseList, false);

    return allSourcingRulesResponseList.get(0);
  }

  private void getSourcingRulesDetails(
      String orgId,
      Map<String, List<SourcingRuleDetails>> sourcingRuleDetailsMap,
      List<AllSourcingRulesResponse> allSourcingRulesResponseList,
      boolean isOnlyActiveRules)
      throws PromiseEngineException, CommonServiceException {

    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        sourcingAttrDefService.processGetSourcingAttributesDefinitionInActiveStatus(
            orgId, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);

    for (Map.Entry<String, List<SourcingRuleDetails>> sourcingRuleListEntry :
        sourcingRuleDetailsMap.entrySet()) {
      // For each sourcing rule create a allSourcingRulesResponse
      AllSourcingRulesResponse allSourcingRulesResponse = new AllSourcingRulesResponse();

      List<NodeGroupInfo> nodeGroupInfos = new LinkedList<>();
      List<AttributeInfo> reqAttributeList = new LinkedList<>();
      List<AttributeInfo> optAttributeList = new LinkedList<>();

      allSourcingRulesResponse.setOrgId(orgId);
      allSourcingRulesResponse.setSourcingRule(sourcingRuleListEntry.getKey());
      allSourcingRulesResponse.setSourcingRuleId(
          sourcingRuleListEntry.getValue().get(0).getSourcingRuleId());
      // For each sourcing rule , created unique sets for nodeGroups, reqAttributes and
      // optAttributes

      Set<String> uniqueNodeGroupIds = new HashSet<>();
      Set<String> uniqueReqAttributes = new HashSet<>();
      Set<String> uniqueOptAttributes = new HashSet<>();

      boolean isInActiveAttributeDefinition = false;
      getAttributeDetails(
          orgId,
          sourcingRuleListEntry.getValue().getFirst(),
          reqAttributeList,
          optAttributeList,
          uniqueReqAttributes,
          uniqueOptAttributes);
      for (SourcingRuleDetails sourcingRuleDetailsList : sourcingRuleListEntry.getValue()) {
        if (!sourcingAttributesDefinitionResponse
            .getId()
            .equals(sourcingRuleDetailsList.getSourcingAttributesDefinitionId())) {
          if (isOnlyActiveRules) {
            isInActiveAttributeDefinition = true;
          }
          allSourcingRulesResponse.setSourcingAttributesDefinitionId(
              sourcingRuleDetailsList.getSourcingAttributesDefinitionId());
          allSourcingRulesResponse.setNodes(new ArrayList<>());
          allSourcingRulesResponse.setRequiredAttributes(new ArrayList<>());
          allSourcingRulesResponse.setOptionalAttributes(new ArrayList<>());
          continue;
        }

        allSourcingRulesResponse.setSourcingAttributesDefinitionId(
            sourcingRuleDetailsList.getSourcingAttributesDefinitionId());
        allSourcingRulesResponse.setSourcingRuleName(sourcingRuleDetailsList.getSourcingRuleName());
        getNodeGroupInfos(orgId, sourcingRuleDetailsList, nodeGroupInfos, uniqueNodeGroupIds);
        allSourcingRulesResponse.setNodes(nodeGroupInfos);
        allSourcingRulesResponse.setRequiredAttributes(reqAttributeList);
        allSourcingRulesResponse.setOptionalAttributes(optAttributeList);
      }
      if (Objects.nonNull(allSourcingRulesResponse.getSourcingAttributesDefinitionId())
          && !isInActiveAttributeDefinition) {
        allSourcingRulesResponseList.add(allSourcingRulesResponse);
      }
    }
  }

  private void getAttributeDetails(
      String orgId,
      SourcingRuleDetails sourcingRuleDetails,
      List<AttributeInfo> reqAttributeList,
      List<AttributeInfo> optAttributeList,
      Set<String> uniqueReqAttributes,
      Set<String> uniqueOptAttributes)
      throws PromiseEngineException, CommonServiceException {

    Long sourcingAttributesDefinitionId = sourcingRuleDetails.getSourcingAttributesDefinitionId();
    Optional<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionEntity =
        sourAttrDefPersistenceService.getSourcingRuleAttributesDefinitionEntityById(
            sourcingAttributesDefinitionId);
    if (sourcingAttributesDefinitionEntity.isEmpty()) {
      logger.error(
          "Sourcing attributes definition not found for given sourcingAttributesDefinitionId");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Sourcing attributes definition not found", HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        INSTANCE_ATTRIBUTE_MAPPER.toSourcingRuleAttributesDefinitionResponse(
            sourcingAttributesDefinitionEntity.get());
    if (!StringUtils.hasLength(sourcingRuleAttributesDefinitionResponse.getReqAttributes())) {
      logger.error("Required attribute not found in sourcing attributes definition");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          "Required attribute not found in sourcing attributes definition",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }

    if (!sourcingRuleDetails.getSourcingRule().equalsIgnoreCase(DEFAULT_SOURCING_RULE)) {
      String[] sourcingRuleValues = sourcingRuleDetails.getSourcingRule().split(":");

      String[] reqAttributes =
          sourcingRuleAttributesDefinitionResponse.getReqAttributes().split(SPLIT_REGEX);
      fetchRulesUtil.getRequiredAttributeDetails(
          orgId, reqAttributeList, uniqueReqAttributes, sourcingRuleValues, reqAttributes);

      if (StringUtils.hasLength(sourcingRuleAttributesDefinitionResponse.getOptAttributes())) {
        String[] optAttributes =
            sourcingRuleAttributesDefinitionResponse.getOptAttributes().split(SPLIT_REGEX);
        fetchRulesUtil.getOptionalAttributeDetails(
            orgId,
            optAttributeList,
            uniqueOptAttributes,
            sourcingRuleValues,
            reqAttributes,
            optAttributes);
      }
    }
  }

  private void getNodeGroupInfos(
      String orgId,
      SourcingRuleDetails sourcingRuleDetails,
      List<NodeGroupInfo> nodeGroupInfos,
      Set<String> uniqueNodeGroupIds)
      throws PromiseEngineException, CommonServiceException {
    String[] nodeGroups = sourcingRuleDetails.getNodeGroups().split(SPLIT_REGEX);

    int sequence = sourcingRuleDetails.getSequence();
    for (String nodeGroupId : nodeGroups) {
      if (uniqueNodeGroupIds.contains(nodeGroupId)) continue;

      uniqueNodeGroupIds.add(nodeGroupId);
      // for each unique node group id create NodeGroupInfo object
      NodeGroupInfo nodeGroupInfo = new NodeGroupInfo();
      nodeGroupInfo.setNodeGroupId(Long.valueOf(nodeGroupId));
      nodeGroupInfo.setSequence(sequence);
      // Will be getting unique nodeGroupEntity for nodeGroupId
      Optional<NodeGroupDomainDto> nodeGroupEntity =
          nodeGroupPersistenceService.fetchNodeGroupById(Long.parseLong(nodeGroupId));
      if (nodeGroupEntity.isEmpty()) {
        logger.error("Node group not found for node group id");
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ID, FieldError.builder().rejectedValue(orgId).build());
        throw new CommonServiceException(
            "Node group not found for node group id", HttpStatus.NOT_FOUND, 0x1771, errorMap);
      }
      NodeGroupResponse nodeGroupResponse =
          INSTANCE_NODE_GROUP.toNodeGroupResponse(nodeGroupEntity.get());
      String nodeGroupName = nodeGroupResponse.getNodeGroupName();
      nodeGroupInfo.setNodeGroupName(nodeGroupName);

      // Fetching no of nodes associated with nodeGroupId and orgId
      List<NodePriorityDomainDto> nodePriorityEntities =
          nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
              orgId, Long.parseLong(nodeGroupId));
      nodeGroupInfo.setNumberOfNodes(nodePriorityEntities.size());
      nodeGroupInfos.add(nodeGroupInfo);
      nodeGroupInfos.sort(Comparator.comparingInt(NodeGroupInfo::getSequence));
    }
  }

  public FetchSourcingRulesResponse processGetSourcingRules(
      FetchSourcingRulesRequest fetchSourcingRulesRequest)
      throws CommonServiceException, PromiseEngineException {
    String requiredAttributesValue =
        fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getRequiredAttributesValue();
    String optionalAttributesValue =
        fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getOptionalAttributesValue();
    PromiseSourcingRuleUtil.validateAttributeValuesFormat(requiredAttributesValue);
    if (StringUtils.hasLength(optionalAttributesValue))
      PromiseSourcingRuleUtil.validateAttributeValuesFormat(optionalAttributesValue);
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinition =
        validateSourcingAttributesDefinitionIdForFetchSourcingRules(fetchSourcingRulesRequest);
    String generatedRule =
        PromiseSourcingRuleUtil.getRuleFromRequiredAndOptionalAttributeValues(
            requiredAttributesValue, optionalAttributesValue);
    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        rulesConfigPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                fetchSourcingRulesRequest.getOrgId(),
                fetchSourcingRulesRequest.getSourcingAttributesDefinitionId(),
                requiredAttributesValue);
    if (sourcingRulesConfigurationEntityList.isEmpty()) {
      return getDefaultSourcingRules(fetchSourcingRulesRequest);
    }
    var optionalAttributeSizeFromDefinition =
        StringUtils.hasLength(sourcingAttributesDefinition.getOptAttributes())
            ? sourcingAttributesDefinition.getOptAttributes().split(SPLIT_REGEX).length
            : 0;

    List<SourcingRulesConfigurationDomainDto> bestRules;
    bestRules =
        sourcingRulesConfigurationEntityList.stream()
            .filter(rule -> generatedRule.equals(rule.getSourcingRule()))
            .toList();
    if (bestRules.isEmpty()) {
      var attributeDefinitionResponse =
          ATTRIBUTES_DEFINITION_MAPPER.toSourcingRuleAttributesDefinitionResponse(
              sourcingAttributesDefinition);
      RulesRetrievalService rulesRetrievalService =
          ruleRetrievalFactory.getRuleRetrievalService(new SourcingRulesConfigurationDomainDto());
      bestRules =
          rulesRetrievalService.filterAllMatchingRulesByScoring(
              sourcingRulesConfigurationEntityList,
              requiredAttributesValue,
              optionalAttributesValue,
              optionalAttributeSizeFromDefinition,
              attributeDefinitionResponse);
    }
    if (bestRules.isEmpty()) {
      return getDefaultSourcingRules(fetchSourcingRulesRequest);
    }
    return FetchSourcingRulesResponse.builder()
        .sourcingRulesInfo(getSourcingRulesInfo(bestRules.getFirst()))
        .build();
  }

  private String getSourcingRuleValue(FetchSourcingRulesRequest fetchSourcingRulesRequest) {
    return StringUtils.hasLength(
            fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getOptionalAttributesValue())
        ? fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getRequiredAttributesValue()
            + ":"
            + fetchSourcingRulesRequest
                .getSourcingAttributeValuesInfo()
                .getOptionalAttributesValue()
        : fetchSourcingRulesRequest.getSourcingAttributeValuesInfo().getRequiredAttributesValue();
  }

  private FetchSourcingRulesResponse getDefaultSourcingRules(
      FetchSourcingRulesRequest fetchSourcingRulesRequest)
      throws PromiseEngineException, CommonServiceException {

    List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationEntityList =
        rulesConfigPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                fetchSourcingRulesRequest.getOrgId(),
                fetchSourcingRulesRequest.getSourcingAttributesDefinitionId(),
                DEFAULT_SOURCING_RULE);

    if (sourcingRulesConfigurationEntityList.isEmpty()) {
      logger.error("Default sourcing rules not configured");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(fetchSourcingRulesRequest.getOrgId()).build());
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder()
              .rejectedValue(fetchSourcingRulesRequest.getSourcingAttributesDefinitionId())
              .build());

      throw new CommonServiceException(
          "Default sourcing rules not configured", HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return FetchSourcingRulesResponse.builder()
        .sourcingRulesInfo(getSourcingRulesInfo(sourcingRulesConfigurationEntityList.get(0)))
        .build();
  }

  private List<SourcingRulesInfo> getSourcingRulesInfo(
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity)
      throws PromiseEngineException, CommonServiceException {
    List<AttributeInfo> reqAttributeList = new LinkedList<>();
    List<AttributeInfo> optAttributeList = new LinkedList<>();
    Set<String> uniqueReqAttributes = new HashSet<>();
    Set<String> uniqueOptAttributes = new HashSet<>();

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(
            sourcingRulesConfigurationEntity.getOrgId(), sourcingRulesConfigurationEntity.getId());
    Map<Integer, List<SourcingRuleDetailsDomainDto>> sourcingRulesMap =
        sourcingRuleDetailsEntities.stream()
            .collect(Collectors.groupingBy(SourcingRuleDetailsDomainDto::getSequence));
    List<SourcingRulesInfo> sourcingRulesInfoList = new ArrayList<>();
    for (Map.Entry<Integer, List<SourcingRuleDetailsDomainDto>> entry :
        sourcingRulesMap.entrySet()) {
      List<NodeGroupDetailsInfo> nodeGroupDetailsInfoList = new ArrayList<>();
      SourcingRulesInfo sourcingRulesInfo =
          INSTANCE.toSourcingRuleInfo(sourcingRulesConfigurationEntity);
      sourcingRulesInfo.setSequence(entry.getKey());

      // fetch required and optional attribute details
      getAttributeDetails(
          sourcingRulesConfigurationEntity.getOrgId(),
          getSourcingRule(sourcingRulesConfigurationEntity),
          reqAttributeList,
          optAttributeList,
          uniqueReqAttributes,
          uniqueOptAttributes);
      sourcingRulesInfo.setRequiredAttributes(reqAttributeList);
      sourcingRulesInfo.setOptionalAttributes(optAttributeList);
      List<String> nodeGroupIds = new ArrayList<>();
      entry
          .getValue()
          .forEach(
              sourcingRuleDetailsEntity ->
                  nodeGroupIds.add(sourcingRuleDetailsEntity.getNodeGroups()));
      for (String nodeGroup : nodeGroupIds) {
        NodeGroupDetailsInfo nodeGroupDetailsInfo = new NodeGroupDetailsInfo();
        Long nodeGroupId = Long.parseLong(nodeGroup);
        List<NodePriorityDomainDto> nodePriorityEntityList =
            nodePriorityPersistenceService.fetchNodePriorityListByOrgIdAndNodeGroupId(
                entry.getValue().get(0).getOrgId(), nodeGroupId);
        List<NodePriorityInfo> nodePriorityInfoList = new ArrayList<>();
        if (!nodePriorityEntityList.isEmpty()) {
          for (NodePriorityDomainDto nodePriorityEntity : nodePriorityEntityList) {
            NodePriorityInfo nodePriorityInfo = new NodePriorityInfo();
            nodePriorityInfo.setNodeId(nodePriorityEntity.getNodeId());
            nodePriorityInfo.setPriority(nodePriorityEntity.getPriority());
            nodePriorityInfoList.add(nodePriorityInfo);
          }
          nodeGroupDetailsInfo.setNodeGroupId(nodeGroupId);
          nodeGroupDetailsInfo.setNodeGroupName(fetchNodeGroupName(nodeGroupId));
          nodeGroupDetailsInfo.setNodeInfo(nodePriorityInfoList);
          nodeGroupDetailsInfoList.add(nodeGroupDetailsInfo);
        }
      }
      sourcingRulesInfo.setNodeGroupDetailsInfo(nodeGroupDetailsInfoList);
      sourcingRulesInfoList.add(sourcingRulesInfo);
    }
    return sourcingRulesInfoList;
  }

  private SourcingRuleDetails getSourcingRule(
      SourcingRulesConfigurationDomainDto sourcingRulesConfiguration) {
    return SourcingRuleDetails.builder()
        .sourcingRuleId(sourcingRulesConfiguration.getId())
        .sourcingAttributesDefinitionId(
            sourcingRulesConfiguration.getSourcingAttributesDefinitionId())
        .sourcingRule(sourcingRulesConfiguration.getSourcingRule())
        .build();
  }

  private String fetchNodeGroupName(Long nodeGroupId) throws PromiseEngineException {
    Optional<NodeGroupDomainDto> nodeGroupEntity =
        nodeGroupPersistenceService.fetchNodeGroupById(nodeGroupId);
    return nodeGroupEntity.map(NodeGroupDomainDto::getNodeGroupName).orElse(null);
  }

  public SourcingRuleDetails processDeleteSourcingRuleDetails(String orgId, Long sourcingRuleId)
      throws PromiseEngineException, CommonServiceException {

    Optional<SourcingRulesConfigurationDomainDto> rulesConfigurationEntity =
        rulesConfigPersistenceService.getSourcingRuleByIdAndOrgId(sourcingRuleId, orgId);
    if (rulesConfigurationEntity.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCING_RULE_ID, FieldError.builder().rejectedValue(sourcingRuleId).build());
      throw new CommonServiceException(
          "Sourcing rule not found for given orgId and sourcingRuleId",
          HttpStatus.NOT_FOUND,
          0X1771,
          errorMap);
    }
    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(orgId, sourcingRuleId);
    rulesConfigPersistenceService.deleteSourcingRule(rulesConfigurationEntity.get());
    sourcingRuleDetailsPersistenceService.deleteMultipleSourcingRuleDetails(
        sourcingRuleDetailsEntities);
    return INSTANCE.toSourcingRuleDetails(rulesConfigurationEntity.get());
  }

  public List<SourcingRuleDetails> processDeleteMultipleSourcingRuleDetails(
      String orgId, SourcingRuleIdRequest sourcingRuleIdRequest)
      throws PromiseEngineException, CommonServiceException {
    List<SourcingRuleDetails> deletedSourcingRuleList = new ArrayList<>();
    List<SourcingRulesConfigurationDomainDto> sourcingRuleEntityList = new ArrayList<>();
    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsList = new ArrayList<>();
    for (Long sourcingRuleId : sourcingRuleIdRequest.getSourcingRuleIds()) {
      Optional<SourcingRulesConfigurationDomainDto> rulesConfigurationEntity =
          rulesConfigPersistenceService.getSourcingRuleByIdAndOrgId(sourcingRuleId, orgId);
      if (rulesConfigurationEntity.isEmpty()) {
        String errorMessage =
            "Sourcing rule not found for " + orgId + " and sourcingRuleId " + sourcingRuleId;
        logger.error(errorMessage);

        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(SOURCING_RULE_ID, FieldError.builder().rejectedValue(sourcingRuleId).build());
        throw new CommonServiceException(errorMessage, HttpStatus.NOT_FOUND, 0X1771, errorMap);
      }
      List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
          sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(orgId, sourcingRuleId);
      for (SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity : sourcingRuleDetailsEntities) {
        SourcingRuleDetails sourcingRuleDetails =
            INSTANCE_SOURCING_RULE_DETAILS.toSourcingRuleDetails(sourcingRuleDetailsEntity);
        sourcingRuleDetails.setSourcingRule(rulesConfigurationEntity.get().getSourcingRule());
        sourcingRuleDetails.setSourcingRuleName(
            rulesConfigurationEntity.get().getSourcingRuleName());
        sourcingRuleDetails.setSourcingAttributesDefinitionId(
            rulesConfigurationEntity.get().getSourcingAttributesDefinitionId());
        deletedSourcingRuleList.add(sourcingRuleDetails);
      }
      sourcingRuleDetailsList.addAll(sourcingRuleDetailsEntities);
      sourcingRuleEntityList.add(rulesConfigurationEntity.get());
    }
    rulesConfigPersistenceService.deleteMultipleSourcingRules(sourcingRuleEntityList);
    sourcingRuleDetailsPersistenceService.deleteMultipleSourcingRuleDetails(
        sourcingRuleDetailsList);
    return deletedSourcingRuleList;
  }

  public List<SourcingRuleByNodeGroupCountProjection> getActiveSourcingRuleCountByNodeGroupIds(
      List<String> nodeGroupEntityIds, Long sourcingAttributesDefinitionId) {
    return sourAttrDefPersistenceService.fetchActiveSourcingRuleCountByNodeGroupIds(
        nodeGroupEntityIds, sourcingAttributesDefinitionId);
  }

  public AllSourcingRulesResponse createSourcingRule(
      String orgId, AllSourcingRulesResponse createRequest)
      throws PromiseEngineException, CommonServiceException {
    validateBlankNode(createRequest);
    Long attributeDefinitionId = createRequest.getSourcingAttributesDefinitionId();
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        checkActiveAttributeDefinition(orgId, createRequest, attributeDefinitionId);
    List<AttributeInfo> requiredAttributeInfo = createRequest.getRequiredAttributes();
    List<AttributeInfo> optionalAttributeInfo = createRequest.getOptionalAttributes();
    StringBuilder builder = new StringBuilder();
    validateAttributesAndBuildRuleString(
        sourcingAttributesDefinitionResponse,
        requiredAttributeInfo,
        optionalAttributeInfo,
        builder);
    String sourcingRule = builder.toString();

    for (NodeGroupInfo nodeGroupInfo : createRequest.getNodes()) {
      RulesConfigurationRequest request =
          RulesConfigurationRequest.builder()
              .orgId(orgId)
              .sourcingRuleName(createRequest.getSourcingRuleName())
              .sourcingRule(sourcingRule)
              .sourcingAttributesDefinitionId(createRequest.getSourcingAttributesDefinitionId())
              .sequence(nodeGroupInfo.getSequence())
              .nodeGroups(String.valueOf(nodeGroupInfo.getNodeGroupId()))
              .build();
      processConfigureSourcingRule(request);
    }

    createRequest.setOrgId(orgId);
    createRequest.setSourcingRule(sourcingRule);
    Optional<List<SourcingRulesConfigurationDomainDto>> rulesConfigurationEntity =
        rulesConfigPersistenceService.getSourcingRuleByOrgIdAndSourcingRule(orgId, sourcingRule);
    rulesConfigurationEntity.ifPresent(
        sourcingRulesConfigurationEntities ->
            createRequest.setSourcingRuleId(sourcingRulesConfigurationEntities.get(0).getId()));
    return createRequest;
  }

  private static void validateAttributesAndBuildRuleString(
      SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse,
      List<AttributeInfo> requiredAttributeInfo,
      List<AttributeInfo> optionalAttributeInfo,
      StringBuilder builder) {
    List<String> requiredAttributes =
        new ArrayList<>(
            Arrays.asList(sourcingAttributesDefinitionResponse.getReqAttributes().split(",")));
    buildRuleStringWithAttributeInfos(requiredAttributes, requiredAttributeInfo, builder);
    List<String> optionalAttributes;
    if (StringUtils.hasLength(sourcingAttributesDefinitionResponse.getOptAttributes())) {
      optionalAttributes =
          new ArrayList<>(
              Arrays.asList(sourcingAttributesDefinitionResponse.getOptAttributes().split(",")));
      buildRuleStringWithAttributeInfos(optionalAttributes, optionalAttributeInfo, builder);
    }
  }

  private static void buildRuleStringWithAttributeInfos(
      List<String> attributeList, List<AttributeInfo> attributeInfoList, StringBuilder builder) {
    for (String attribute : attributeList) {
      Optional<AttributeInfo> attributeInfo =
          attributeInfoList.stream().filter(x -> attribute.equals(x.getAttributeId())).findFirst();
      if (!builder.toString().isEmpty()) builder.append(":");

      if (attributeInfo.isPresent()
          && StringUtils.hasLength(attributeInfo.get().getAttributeValue())) {
        builder.append(attributeInfo.get().getAttributeValue());
      }
    }
  }

  private static void validateBlankNode(AllSourcingRulesResponse createRequest)
      throws CommonServiceException {
    if (CollectionUtils.isEmpty(createRequest.getNodes())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("Nodes", FieldError.builder().rejectedValue(createRequest.getNodes()).build());
      throw new CommonServiceException(
          "No node/node group have been added", HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
  }

  private SourcingAttributesDefinitionResponse checkActiveAttributeDefinition(
      String orgId, AllSourcingRulesResponse createRequest, Long attributeDefinitionId)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        sourcingAttrDefService.processGetSourcingAttributesDefinitionByIdandOrgId(
            attributeDefinitionId, orgId);
    if (!SourcingAttributesDefinitionStatus.ACTIVE.equals(
        sourcingAttributesDefinitionResponse.getStatus())) {
      logger.error("Sourcing attribute definition is not active");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          SOURCING_ATTRIBUTES_DEFINITION_ID,
          FieldError.builder()
              .rejectedValue(createRequest.getSourcingAttributesDefinitionId())
              .build());
      throw new CommonServiceException(
          "Sourcing attribute definition is not active", HttpStatus.BAD_REQUEST, 0X1771, errorMap);
    }
    return sourcingAttributesDefinitionResponse;
  }

  @Transactional
  public UpdateSourcingRuleConfigurationResponse editSourcingRuleConfiguration(
      String orgId, UpdateSourcingRuleRequest sourcingRuleEditRequest)
      throws PromiseEngineException, CommonServiceException {
    // Fetch all sourcing rule entity by sourcing rule, node group and orgid.
    var exisitngRulesConfiguration =
        rulesConfigPersistenceService.getSourcingRuleByIdAndOrgId(
            sourcingRuleEditRequest.getSourcingRuleId(), orgId);
    if (exisitngRulesConfiguration.isEmpty()) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule.");
    }
    var existingSourcingRule = exisitngRulesConfiguration.get().getSourcingRule();
    if (!sourcingRuleEditRequest
        .getSourcingRuleName()
        .equals(exisitngRulesConfiguration.get().getSourcingRuleName()))
      validateSourcingRuleName(
          orgId,
          sourcingRuleEditRequest.getSourcingAttributesDefinitionId(),
          sourcingRuleEditRequest.getSourcingRuleName());
    List<NodeGroupInfo> nodeGroupInfoList = new ArrayList<>();
    sourcingRuleEditRequest
        .getNodeGroups()
        .forEach(
            nodeGroup -> {
              NodeGroupInfo nodeGroupInfo = new NodeGroupInfo();
              Long nodeGroupId = Long.valueOf(nodeGroup.getNodeGroupId());
              nodeGroupInfo.setNodeGroupId(nodeGroupId);
              nodeGroupInfoList.add(nodeGroupInfo);
            });
    SourcingRulesConfigurationDomainDto existingSourcingRuleConfigDto =
        exisitngRulesConfiguration.get();
    existingSourcingRuleConfigDto.setSourcingRuleName(
        sourcingRuleEditRequest.getSourcingRuleName());
    var sourcingAttributesDefinitionOption =
        sourAttrDefPersistenceService.getSourcingRuleAttributesDefinitionEntityById(
            sourcingRuleEditRequest.getSourcingAttributesDefinitionId());
    SourcingAttributesDefinitionDomainDto sourcingAttrDefDto =
        sourcingAttributesDefinitionOption.orElse(new SourcingAttributesDefinitionDomainDto());
    List<AttributeInfo> requiredAttributeInfo = sourcingRuleEditRequest.getRequiredAttributes();
    List<AttributeInfo> optionalAttributeInfo = sourcingRuleEditRequest.getOptionalAttributes();
    StringBuilder builder = new StringBuilder();
    validateAttributesAndBuildRuleString(
        INSTANCE_ATTRIBUTE_MAPPER.toSourcingRuleAttributesDefinitionResponse(sourcingAttrDefDto),
        requiredAttributeInfo,
        optionalAttributeInfo,
        builder);
    existingSourcingRuleConfigDto.setSourcingRule(builder.toString());
    var sourcingRule = existingSourcingRuleConfigDto.getSourcingRule();
    if (!sourcingRule.equalsIgnoreCase(existingSourcingRule))
      checkForExistingSourcingRule(orgId, sourcingRuleEditRequest, sourcingRule);

    rulesConfigPersistenceService.saveSourcingRule(existingSourcingRuleConfigDto);
    var savedSourcingRule =
        manageCRUDOperationOnSourcingRule(
            orgId, existingSourcingRuleConfigDto, sourcingRuleEditRequest);
    // Get nodes detail.
    var nodeGroupDetailsResponse = new ArrayList<UpdateNodeGroupDetailsResponse>();
    for (SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity : savedSourcingRule) {
      UpdateNodeGroupDetailsResponse updateNodeGroupDetailsResponse =
          prepareNodeGroupDetailsResponse(sourcingRuleDetailsEntity);
      if (updateNodeGroupDetailsResponse.getNodeGroupId() > 0) {
        nodeGroupDetailsResponse.add(updateNodeGroupDetailsResponse);
      }
    }
    return prepareResponse(
        orgId, sourcingRuleEditRequest, nodeGroupDetailsResponse, existingSourcingRuleConfigDto);
  }

  private void checkForExistingSourcingRule(
      String orgId, UpdateSourcingRuleRequest sourcingRuleEditRequest, String sourcingRule)
      throws PromiseEngineException, CommonServiceException {
    List<SourcingRulesConfigurationDomainDto> rulesConfigurationDomainDto =
        rulesConfigPersistenceService
            .getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
                orgId, sourcingRuleEditRequest.getSourcingAttributesDefinitionId(), sourcingRule);
    if (!rulesConfigurationDomainDto.isEmpty()) {
      validateSourcingRuleRequestByRuleName(
          sourcingRuleEditRequest.getSourcingRuleName(),
          sourcingRule,
          rulesConfigurationDomainDto.getFirst().getSourcingRuleName());
    }
  }

  private List<SourcingRuleDetailsDomainDto> manageCRUDOperationOnSourcingRule(
      String orgId,
      SourcingRulesConfigurationDomainDto rulesConfigurationEntitiesOption,
      UpdateSourcingRuleRequest sourcingRuleEditRequest)
      throws PromiseEngineException {

    List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsEntities =
        sourcingRuleDetailsPersistenceService.fetchBySourcingRuleId(
            orgId, rulesConfigurationEntitiesOption.getId());
    var storedEntityMap = new HashMap<String, SourcingRuleDetailsDomainDto>();
    sourcingRuleDetailsEntities.forEach(
        sourcingRuleDetailsEntity ->
            storedEntityMap.put(
                sourcingRuleDetailsEntity.getNodeGroups(),
                storedEntityMap.getOrDefault(
                    sourcingRuleDetailsEntity.getNodeGroups(), sourcingRuleDetailsEntity)));

    var ruleNodeGroupRequests = sourcingRuleEditRequest.getNodeGroups();
    var requestNodeGroupMap =
        ruleNodeGroupRequests.stream()
            .collect(
                Collectors.toMap(
                    UpdateSourcingRuleNodeGroupRequest::getNodeGroupId,
                    UpdateSourcingRuleNodeGroupRequest::getSequence));

    var createOrUpdateRules =
        ruleNodeGroupRequests.stream()
            .map(
                request -> {
                  SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity =
                      new SourcingRuleDetailsDomainDto();
                  if (storedEntityMap.containsKey(request.getNodeGroupId())) {
                    sourcingRuleDetailsEntity = storedEntityMap.get(request.getNodeGroupId());
                    return prepareSourcingRuleDetailsEntity(
                        orgId, sourcingRuleEditRequest, request, sourcingRuleDetailsEntity);
                  }
                  return prepareSourcingRuleDetailsEntity(
                      orgId, sourcingRuleEditRequest, request, sourcingRuleDetailsEntity);
                })
            .collect(Collectors.toList());

    var deleteRules =
        sourcingRuleDetailsEntities.stream()
            .filter(
                sourcingRuleDetailsEntity ->
                    !requestNodeGroupMap.containsKey(sourcingRuleDetailsEntity.getNodeGroups()))
            .collect(Collectors.toList());

    // perform save / delete operation on both the list.
    List<SourcingRuleDetailsDomainDto> savedSourcingRule = new ArrayList<>();
    if (!createOrUpdateRules.isEmpty()) {
      savedSourcingRule = sourcingRuleDetailsPersistenceService.saveAll(createOrUpdateRules);
    }
    if (!deleteRules.isEmpty()) {
      sourcingRuleDetailsPersistenceService.deleteMultipleSourcingRuleDetails(deleteRules);
    }
    return savedSourcingRule;
  }

  private UpdateSourcingRuleConfigurationResponse prepareResponse(
      String orgId,
      UpdateSourcingRuleRequest sourcingRuleEditRequest,
      List<UpdateNodeGroupDetailsResponse> nodeGroupDetailsResponses,
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationEntity) {
    UpdateSourcingRuleConfigurationResponse updateSourcingRuleConfigurationResponse =
        UpdateSourcingRuleConfigurationResponse.builder()
            .sourcingRuleId(sourcingRuleEditRequest.getSourcingRuleId())
            .sourcingRule(sourcingRulesConfigurationEntity.getSourcingRule())
            .sourcingRuleName(sourcingRuleEditRequest.getSourcingRuleName())
            .sourcingAttributesDefinitionId(
                sourcingRuleEditRequest.getSourcingAttributesDefinitionId())
            .orgId(orgId)
            .requiredAttributes(
                INSTANCE_SOURCING_ATTRIBUTE_MAPPER.convertToUpdateSourcingAttributeResponseList(
                    sourcingRuleEditRequest.getRequiredAttributes()))
            .optionalAttributes(
                INSTANCE_SOURCING_ATTRIBUTE_MAPPER.convertToUpdateSourcingAttributeResponseList(
                    sourcingRuleEditRequest.getOptionalAttributes()))
            .build();
    updateSourcingRuleConfigurationResponse.setNodes(nodeGroupDetailsResponses);
    return updateSourcingRuleConfigurationResponse;
  }

  private UpdateNodeGroupDetailsResponse prepareNodeGroupDetailsResponse(
      SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity) throws PromiseEngineException {
    String id = sourcingRuleDetailsEntity.getNodeGroups();
    if (NumberUtils.isCreatable(id)) {
      long nodeId = Long.parseLong(id);
      NodeGroupDomainDto nodeGroupEntity =
          nodeGroupPersistenceService.fetchNodeGroupById(nodeId).orElse(null);
      if (nodeGroupEntity != null) {
        int nodeCount =
            nodePriorityPersistenceService.countByOrgIdAndNodeGroupId(
                nodeGroupEntity.getOrgId(), nodeGroupEntity.getId());
        return UpdateNodeGroupDetailsResponse.builder()
            .nodeGroupName(nodeGroupEntity.getNodeGroupName())
            .nodeGroupId(nodeId)
            .numberOfNodes(nodeCount)
            .sequence(sourcingRuleDetailsEntity.getSequence())
            .build();
      }
    }
    return new UpdateNodeGroupDetailsResponse();
  }

  private SourcingRuleDetailsDomainDto prepareSourcingRuleDetailsEntity(
      String orgId,
      UpdateSourcingRuleRequest updateSourcingRuleRequest,
      UpdateSourcingRuleNodeGroupRequest ruleNodeGroupRequest,
      SourcingRuleDetailsDomainDto sourcingRuleDetailsEntity) {
    sourcingRuleDetailsEntity.setSequence(ruleNodeGroupRequest.getSequence());
    sourcingRuleDetailsEntity.setSourcingRuleId(updateSourcingRuleRequest.getSourcingRuleId());
    sourcingRuleDetailsEntity.setOrgId(orgId);
    sourcingRuleDetailsEntity.setNodeGroups(ruleNodeGroupRequest.getNodeGroupId());
    return sourcingRuleDetailsEntity;
  }
}
