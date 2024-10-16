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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingRulesConfigurationDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingRulesConfigurationKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRulesConfigurationEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRulesConfigurationRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingRulesConfigurationPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingRulesConfigurationPersistenceServiceImpl
    extends CommonPersistenceService<
        SourcingRulesConfigurationDomainDto,
        SourcingRulesConfigurationDomainKey,
        SourcingRulesConfigurationEntity,
        SourcingRulesConfigurationKey,
        SourcingRulesConfigurationRepository,
        SourcingRulesConfigurationEntityMapper>
    implements SourcingRulesConfigurationPersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRulesConfigurationPersistenceServiceImpl.class);

  @Override
  public SourcingRulesConfigurationDomainDto saveSourcingRule(
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto)
      throws PromiseEngineException {
    try {
      return save(sourcingRulesConfigurationDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save sourcing rule", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing rule");
    }
  }

  @Override
  public Optional<SourcingRulesConfigurationDomainDto> getSourcingRuleById(Long id)
      throws PromiseEngineException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule by id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule by id");
    }
  }

  @Override
  public Optional<SourcingRulesConfigurationDomainDto> getSourcingRuleByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule by id and orgId");
    }
  }

  @Override
  public Optional<List<SourcingRulesConfigurationDomainDto>> getSourcingRuleByOrgIdAndSourcingRule(
      String orgId, String sourcingRule) throws PromiseEngineException {
    try {
      return getRepository()
          .findByOrgIdAndSourcingRule(orgId, sourcingRule)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule by orgId and sourcing rule", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule by orgId and sourcing rule");
    }
  }

  @Override
  public List<SourcingRulesConfigurationDomainDto>
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRule(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRule)
          throws PromiseEngineException {
    try {
      List<SourcingRulesConfigurationEntity> sourcingRulesConfigurationEntities =
          getRepository()
              .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleStartsWith(
                  orgId, sourcingAttributesDefinitionId, sourcingRule);
      return getMapper().toDomain(sourcingRulesConfigurationEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to find sourcing rule list by orgId and sourcing attributes definition id and sourcing rule",
          e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule list by orgId and sourcing attributes definition id and sourcing rule");
    }
  }

  @Override
  public SourcingRulesConfigurationDomainDto getSourcingRulesByOrgIdAndSourcingRuleName(
      String orgId, String sourcingRuleName) throws PromiseEngineException {
    try {
      SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
          getRepository().findByOrgIdAndSourcingRuleName(orgId, sourcingRuleName);
      return getMapper().toDomain(sourcingRulesConfigurationEntity);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule by orgId and sourcing rule name", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule by orgId and sourcing rule name");
    }
  }

  @Override
  public SourcingRulesConfigurationDomainDto
      getSourcingRulesByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
          String orgId, Long sourcingAttributesDefinitionId, String sourcingRuleName)
          throws PromiseEngineException {
    try {
      SourcingRulesConfigurationEntity sourcingRulesConfigurationEntity =
          getRepository()
              .findByOrgIdAndSourcingAttributesDefinitionIdAndSourcingRuleName(
                  orgId, sourcingAttributesDefinitionId, sourcingRuleName);
      return getMapper().toDomain(sourcingRulesConfigurationEntity);
    } catch (Exception e) {
      logger.error(
          "Unable to find sourcing rule by orgId, sourcingAttributesDefinitionId and sourcing rule name",
          e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule by orgId, sourcingAttributesDefinitionId and sourcing rule name");
    }
  }

  @Override
  public void deleteSourcingRule(
      SourcingRulesConfigurationDomainDto sourcingRulesConfigurationDomainDto)
      throws PromiseEngineException {
    try {
      delete(sourcingRulesConfigurationDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete sourcing rule", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete sourcing rule");
    }
  }

  @Override
  public void deleteMultipleSourcingRules(
      List<SourcingRulesConfigurationDomainDto> sourcingRulesConfigurationDomainDtos)
      throws PromiseEngineException {
    try {
      getRepository().deleteAll(getMapper().toEntity(sourcingRulesConfigurationDomainDtos));
    } catch (Exception e) {
      logger.error("Unable to delete multiple sourcing rule", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete multiple sourcing rule");
    }
  }
}
