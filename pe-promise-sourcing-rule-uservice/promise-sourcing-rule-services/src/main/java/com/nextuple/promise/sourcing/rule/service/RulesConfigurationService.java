/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService.COLON_DELIMITER;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.COLON_SPLIT_REGEX;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.RULE_NOT_FOUND_ERROR_MESSAGE;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.SPLIT_REGEX;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchRuleConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.api.domain.services.RulesRetrievalService;
import com.nextuple.promise.sourcing.rule.domain.mapper.RulesConfigurationMapper;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingAttributesDefinitionMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.RulesConfigurationPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class RulesConfigurationService {
  private final RulesConfigurationPersistenceService rulesConfigurationPersistenceService;
  private final SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;
  @Autowired RulesRetrievalService<RulesConfigurationDomainDto> rulesRetrievalService;

  private static final RulesConfigurationMapper INSTANCE =
      Mappers.getMapper(RulesConfigurationMapper.class);
  private static final SourcingAttributesDefinitionMapper ATTRIBUTES_DEFINITION_MAPPER =
      Mappers.getMapper(SourcingAttributesDefinitionMapper.class);

  private static final String ATTRIBUTES_DEFINITION_ID = "attributeDefinitionId";
  private static final String RULE = "rule";
  private static final String RULE_NAME = "ruleName";
  private static final String MODULE_NAME = "moduleName";
  private static final String SCOPE = "scope";

  public RulesConfigurationResponse createRulesConfiguration(RulesConfigurationsRequest request)
      throws PromiseEngineException, CommonServiceException {
    validateRulesConfigurationRequest(request);
    var rulesConfigurationDomainDto = INSTANCE.toRulesConfigurationDomainDto(request);
    return INSTANCE.toRulesConfigurationResponse(
        rulesConfigurationPersistenceService.saveRulesConfiguration(rulesConfigurationDomainDto));
  }

  private void validateRulesConfigurationRequest(
      RulesConfigurationsRequest rulesConfigurationsRequest)
      throws PromiseEngineException, CommonServiceException {
    validateSourcingAttributesDefinitionIdForRulesConfiguration(
        rulesConfigurationsRequest.getOrgId(),
        rulesConfigurationsRequest.getRule(),
        rulesConfigurationsRequest.getAttributeDefinitionId(),
        rulesConfigurationsRequest.getScope());
    validateBlankRuleValues(
        rulesConfigurationsRequest.getOrgId(),
        rulesConfigurationsRequest.getRule(),
        rulesConfigurationsRequest.getAttributeDefinitionId(),
        rulesConfigurationsRequest.getScope());
  }

  private void validateSourcingAttributesDefinitionIdForRulesConfiguration(
      String orgId,
      String rule,
      Long sourcingAttributesDefinitionId,
      SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDomainDto =
        fetchSourcingAttributeDefinition(orgId, sourcingAttributesDefinitionId, scope);
    String[] requiredAttributeReferencesList =
        existingSourcingAttributesDefinitionDomainDto.getReqAttributes().split(SPLIT_REGEX);
    int optionalAttributesLength =
        StringUtils.hasLength(existingSourcingAttributesDefinitionDomainDto.getOptAttributes())
            ? existingSourcingAttributesDefinitionDomainDto
                .getOptAttributes()
                .split(SPLIT_REGEX)
                .length
            : 0;
    String[] attributeValuesList = rule.split(COLON_SPLIT_REGEX, -1);
    PromiseSourcingRuleUtil.checkForRequiredAttributesLength(
        rule,
        requiredAttributeReferencesList,
        attributeValuesList,
        REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        RULE,
        0x1B5B);
    PromiseSourcingRuleUtil.checkForTotalAttributesLength(
        rule,
        requiredAttributeReferencesList,
        optionalAttributesLength,
        attributeValuesList,
        TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE,
        RULE,
        0x1B5C);
  }

  private SourcingAttributesDefinitionDomainDto fetchSourcingAttributeDefinition(
      String orgId,
      Long sourcingAttributesDefinitionId,
      SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto> existingSourcingAttributesDefinitionDomainDto =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(
                sourcingAttributesDefinitionId, orgId, scope);

    if (existingSourcingAttributesDefinitionDomainDto.isEmpty()
        || !(existingSourcingAttributesDefinitionDomainDto
            .get()
            .getStatus()
            .equals(SourcingAttributesDefinitionStatus.ACTIVE))) {
      log.error(
          "Invalid sourcing rule attributes definition for given scope / Sourcing attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : {}",
          sourcingAttributesDefinitionId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ATTRIBUTES_DEFINITION_ID,
          FieldError.builder().rejectedValue(sourcingAttributesDefinitionId).build());
      throw new CommonServiceException(
          "Invalid sourcing attributes definition for given scope / Sourcing attributes definition exists but not in ACTIVE status",
          HttpStatus.BAD_REQUEST,
          0x1B59,
          errorMap);
    }

    return existingSourcingAttributesDefinitionDomainDto.get();
  }

  private void validateBlankRuleValues(
      String orgId,
      String rule,
      Long sourcingAttributesDefinitionId,
      SourcingAttributesDefinitionScopeEnum scope)
      throws CommonServiceException, PromiseEngineException {
    SourcingAttributesDefinitionDomainDto existingSourcingAttributesDefinitionDomainDto =
        fetchSourcingAttributeDefinition(orgId, sourcingAttributesDefinitionId, scope);
    int requiredAttributesLength =
        StringUtils.hasLength(existingSourcingAttributesDefinitionDomainDto.getReqAttributes())
            ? existingSourcingAttributesDefinitionDomainDto
                .getReqAttributes()
                .split(SPLIT_REGEX)
                .length
            : 0;
    long colonCount = rule.chars().filter(ch -> ch == ':').count();
    String[] attributes = rule.split(COLON_SPLIT_REGEX, -1);
    boolean blankCheck =
        Arrays.stream(attributes, 0, requiredAttributesLength)
            .anyMatch(value -> !StringUtils.hasLength(value));
    if (blankCheck || colonCount >= attributes.length) {
      log.error("Can't add the rule as rule contains blank values");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("rule", FieldError.builder().rejectedValue(rule).build());
      throw new CommonServiceException(
          "Can't add the rule as rule contains blank values",
          HttpStatus.BAD_REQUEST,
          0x1B5D,
          errorMap);
    }
  }

  public RulesConfigurationResponse processFetchRulesConfiguration(
      FetchRuleConfigurationRequest request) throws PromiseEngineException, CommonServiceException {
    var requiredAttrVal = request.getAttributeValuesInfo().getRequiredAttributesValue();
    var optionalAttrVal = request.getAttributeValuesInfo().getOptionalAttributesValue();
    var attributeDefinition =
        fetchAttributeDefinition(
            request.getOrgId(), request.getAttributeDefinitionId(), request.getScope());
    var attributeDefinitionResponse =
        ATTRIBUTES_DEFINITION_MAPPER.toSourcingRuleAttributesDefinitionResponse(
            attributeDefinition);
    var optionalAttrFromDefinitionSize =
        StringUtils.hasLength(attributeDefinition.getOptAttributes())
            ? attributeDefinition.getOptAttributes().split(SPLIT_REGEX).length
            : 0;
    var generatedRule =
        PromiseSourcingRuleUtil.getRuleFromRequiredAndOptionalAttributeValues(
            request.getAttributeValuesInfo().getRequiredAttributesValue(),
            request.getAttributeValuesInfo().getOptionalAttributesValue());

    FetchRulesUtil.validateAttributesDefinitionIdForScope(
        attributeDefinition.getReqAttributes(),
        attributeDefinition.getOptAttributes(),
        generatedRule);
    List<RulesConfigurationDomainDto> rulesList =
        fetchRulesFromDB(request, attributeDefinition.getOptAttributes());

    // Scoring/Filtering Logic
    List<RulesConfigurationDomainDto> bestRules;
    bestRules = rulesList.stream().filter(rule -> generatedRule.equals(rule.getRule())).toList();
    if (bestRules.isEmpty()) {
      bestRules =
          rulesRetrievalService.filterAllMatchingRulesByScoring(
              rulesList,
              requiredAttrVal,
              optionalAttrVal,
              optionalAttrFromDefinitionSize,
              attributeDefinitionResponse);
    }
    validateNoRulesFound(request, bestRules, generatedRule);

    return INSTANCE.toRulesConfigurationResponse(bestRules.getFirst());
  }

  private SourcingAttributesDefinitionDomainDto fetchAttributeDefinition(
      String orgId, Long attributesDefinitionId, SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto> existingAttributesDefinition =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(attributesDefinitionId, orgId);

    if (existingAttributesDefinition.isEmpty()
        || !(existingAttributesDefinition
                .get()
                .getStatus()
                .equals(SourcingAttributesDefinitionStatus.ACTIVE)
            && existingAttributesDefinition.get().getScope().equals(scope))) {
      log.error(
          "Invalid attributes definition for given scope: %s / Sourcing attributes definition exists but not in ACTIVE status with given sourcingAttributesDefinitionId : %s"
              .formatted(scope, attributesDefinitionId));
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ATTRIBUTES_DEFINITION_ID,
          FieldError.builder().rejectedValue(attributesDefinitionId).build());
      throw new CommonServiceException(
          "Invalid attributes definition for given scope / attributes definition exists but not in ACTIVE status",
          HttpStatus.BAD_REQUEST,
          0x1B59,
          errorMap);
    }

    return existingAttributesDefinition.get();
  }

  private List<RulesConfigurationDomainDto> fetchRulesFromDB(
      FetchRuleConfigurationRequest request, String optAttributesFromDefinition)
      throws PromiseEngineException {
    List<RulesConfigurationDomainDto> rulesList = new ArrayList<>();

    if (StringUtils.hasLength(optAttributesFromDefinition)) {
      var requiredAttrValue =
          request.getAttributeValuesInfo().getRequiredAttributesValue().concat(COLON_DELIMITER);
      rulesList.addAll(
          rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
              request.getOrgId(), request.getAttributeDefinitionId(), requiredAttrValue));
    }
    Optional<RulesConfigurationDomainDto> ruleEntity =
        rulesConfigurationPersistenceService.findByOrgIdAndAttributeDefinitionIdAndRule(
            request.getOrgId(),
            request.getAttributeDefinitionId(),
            request.getAttributeValuesInfo().getRequiredAttributesValue());

    ruleEntity.ifPresent(rulesList::add);

    return rulesList;
  }

  private static void validateNoRulesFound(
      FetchRuleConfigurationRequest request,
      List<RulesConfigurationDomainDto> bestRules,
      String generatedRule)
      throws CommonServiceException {
    if (bestRules.isEmpty()) {
      String errorMessage =
          "Rule not found for a given %s scope and %s rule"
              .formatted(request.getScope().name(), generatedRule);
      throw new CommonServiceException(
          errorMessage,
          HttpStatus.NOT_FOUND,
          0x1B60,
          Map.of(
              ATTRIBUTES_DEFINITION_ID,
              FieldError.builder().rejectedValue(request.getAttributeDefinitionId()).build(),
              RULE,
              FieldError.builder().rejectedValue(generatedRule).build(),
              ORG_ID,
              FieldError.builder().rejectedValue(request.getOrgId()).build(),
              SCOPE,
              FieldError.builder().rejectedValue(request.getScope()).build()));
    }
  }

  @Transactional
  public RulesConfigurationResponse deleteRuleConfiguration(
      RuleConfigurationParam ruleConfigurationParam)
      throws PromiseEngineException, CommonServiceException {
    Optional<RulesConfigurationDomainDto> rulesConfigurationDomainDto =
        rulesConfigurationPersistenceService.findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            ruleConfigurationParam);
    if (rulesConfigurationDomainDto.isEmpty()) {
      throw new CommonServiceException(
          RULE_NOT_FOUND_ERROR_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1B61,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(ruleConfigurationParam.getOrgId()).build(),
              RULE_NAME,
              FieldError.builder().rejectedValue(ruleConfigurationParam.getRuleName()).build(),
              RULE,
              FieldError.builder().rejectedValue(ruleConfigurationParam.getRule()).build(),
              MODULE_NAME,
              FieldError.builder().rejectedValue(ruleConfigurationParam.getModuleName()).build(),
              SCOPE,
              FieldError.builder().rejectedValue(ruleConfigurationParam.getScope()).build()));
    }
    rulesConfigurationPersistenceService.deleteRuleConfiguration(rulesConfigurationDomainDto.get());
    return INSTANCE.toRulesConfigurationResponse(rulesConfigurationDomainDto.get());
  }

  public Optional<RulesConfigurationResponse>
      fetchRuleByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
          RuleConfigurationParam ruleConfigurationParam) throws PromiseEngineException {
    Optional<RulesConfigurationDomainDto> rulesConfigurationDomainDtoOptional =
        rulesConfigurationPersistenceService.findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
            ruleConfigurationParam);
    return rulesConfigurationDomainDtoOptional.map(INSTANCE::toRulesConfigurationResponse);
  }

  public List<RulesConfigurationResponse>
      fetchRuleByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
          String orgId,
          Long attributeDefinitionId,
          RulesConfigurationModuleNameEnum moduleName,
          SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException {
    List<RulesConfigurationDomainDto> rulesConfigurationDomainDtoList =
        rulesConfigurationPersistenceService
            .findByOrgIdAndAttributeDefinitionIdAndModuleNameAndScope(
                orgId, attributeDefinitionId, moduleName, scope);
    return rulesConfigurationDomainDtoList.stream()
        .map(INSTANCE::toRulesConfigurationResponse)
        .toList();
  }
}
