/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.persistence.domain.RulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.RulesConfigurationDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.RulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.RulesConfigurationKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.RulesConfigurationEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.RulesConfigurationRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.RulesConfigurationPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RulesConfigurationPersistenceServiceImpl
    extends CommonPersistenceService<
        RulesConfigurationDomainDto,
        RulesConfigurationDomainKey,
        RulesConfigurationEntity,
        RulesConfigurationKey,
        RulesConfigurationRepository,
        RulesConfigurationEntityMapper>
    implements RulesConfigurationPersistenceService {
  private static final String ERROR_WHILE_FINDING_RULES_CONFIGURATION =
      "Error while finding rules configuration";

  @Override
  public RulesConfigurationDomainDto saveRulesConfiguration(
      RulesConfigurationDomainDto rulesConfigurationDomainDto) throws PromiseEngineException {
    log.debug(
        "-- inside saveRulesConfiguration with rulesConfigurationDomainDto : {}--",
        rulesConfigurationDomainDto);
    try {
      return save(rulesConfigurationDomainDto);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Error while saving rules configuration");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Error while saving rules configuration");
    }
  }

  @Override
  public List<RulesConfigurationDomainDto> findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
      String orgId, Long attributeDefinitionId, String rule) throws PromiseEngineException {
    log.debug(
        "-- inside findByOrgIdAndAttributesDefinitionIdAndRuleStartsWith : {}, {}, {} --",
        orgId,
        attributeDefinitionId,
        rule);
    try {
      return getMapper()
          .toDomain(
              getRepository()
                  .findByOrgIdAndAttributeDefinitionIdAndRuleStartsWith(
                      orgId, attributeDefinitionId, rule));
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING_RULES_CONFIGURATION);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          ERROR_WHILE_FINDING_RULES_CONFIGURATION);
    }
  }

  @Override
  public Optional<RulesConfigurationDomainDto> findByOrgIdAndAttributeDefinitionIdAndRule(
      String orgId, Long attributeDefinitionId, String rule) throws PromiseEngineException {
    log.debug(
        "-- inside findByOrgIdAndAttributeDefinitionIdAndRule : {}, {}, {} --",
        orgId,
        attributeDefinitionId,
        rule);
    try {
      List<RulesConfigurationEntity> rulesConfigurationEntities =
          getRepository()
              .findByOrgIdAndAttributeDefinitionIdAndRule(orgId, attributeDefinitionId, rule);
      if (rulesConfigurationEntities.isEmpty()) return Optional.empty();
      return Optional.of(rulesConfigurationEntities.getFirst()).map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING_RULES_CONFIGURATION);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          ERROR_WHILE_FINDING_RULES_CONFIGURATION);
    }
  }

  @Override
  public Optional<RulesConfigurationDomainDto> findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
      RuleConfigurationParam ruleConfigurationParam) throws PromiseEngineException {
    log.debug(
        "-- inside findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope : {} --",
        ruleConfigurationParam);
    try {
      return getRepository()
          .findByOrgIdAndRuleNameAndRuleAndModuleNameAndScope(
              ruleConfigurationParam.getOrgId(),
              ruleConfigurationParam.getRuleName(),
              ruleConfigurationParam.getRule(),
              ruleConfigurationParam.getModuleName(),
              ruleConfigurationParam.getScope())
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING_RULES_CONFIGURATION);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          ERROR_WHILE_FINDING_RULES_CONFIGURATION);
    }
  }

  @Override
  public void deleteRuleConfiguration(RulesConfigurationDomainDto rulesConfigurationDomainDto)
      throws PromiseEngineException {
    log.debug(
        "-- inside deleteRuleConfiguration with rulesConfigurationDomainDto : {}--",
        rulesConfigurationDomainDto);
    try {
      delete(rulesConfigurationDomainDto);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Error while deleting rule configuration");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Error while deleting rules configuration");
    }
  }
}
