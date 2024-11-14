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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.projection.SourcingRuleByNodeGroupCountProjection;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingAttributesDefinitionDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributesDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingAttributesDefinitionKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributesDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingAttributesDefinitionRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingAttributesDefinitionPersistenceServiceImpl
    extends CommonPersistenceService<
        SourcingAttributesDefinitionDomainDto,
        SourcingAttributesDefinitionDomainKey,
        SourcingAttributesDefinitionEntity,
        SourcingAttributesDefinitionKey,
        SourcingAttributesDefinitionRepository,
        SourcingAttributesDefinitionEntityMapper>
    implements SourcingAttributesDefinitionPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributesDefinitionPersistenceServiceImpl.class);

  @Override
  public SourcingAttributesDefinitionDomainDto saveSourcingRuleAttributesDefinitionEntity(
      SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto)
      throws PromiseEngineException {
    try {
      return save(sourcingAttributesDefinitionDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save sourcing rule attributes definition entity", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing rule attributes definition entity");
    }
  }

  @Override
  public Optional<SourcingAttributesDefinitionDomainDto>
      getSourcingRuleAttributesDefinitionEntityById(Long id) throws PromiseEngineException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule attributes definition entity by id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule attributes definition entity by id");
    }
  }

  @Override
  public Optional<SourcingAttributesDefinitionDomainDto>
      getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(Long id, String orgId)
          throws PromiseEngineException {
    logger.debug("-- inside getSourcingRuleAttributesDefinitionEntityById domain --");
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule attributes definition entity by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule attributes definition entity by id and orgId");
    }
  }

  @Override
  public Optional<SourcingAttributesDefinitionDomainDto>
      getSourcingRuleAttributesDefinitionEntityByIdAndOrgIdAndScope(
          Long id, String orgId, SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException {
    logger.debug(
        "getSourcingRuleAttributesDefinitionEntity By id : {},orgId : {},scope : {}",
        id,
        orgId,
        scope);
    try {
      return getRepository().findByIdAndOrgIdAndScope(id, orgId, scope).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(
          "Unable to find sourcing rule attributes definition entity by id and orgId and scope", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule attributes definition entity by id and orgId and scope");
    }
  }

  @Override
  public List<SourcingAttributesDefinitionDomainDto>
      fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(String orgId, String name)
          throws PromiseEngineException {
    try {
      List<SourcingAttributesDefinitionEntity> sourcingAttributesDefinitionEntities =
          getRepository().findByOrgIdAndName(orgId, name);
      return getMapper().toDomain(sourcingAttributesDefinitionEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch sourcing rule attributes definition entity list by orgId and name", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch sourcing rule attributes definition entity list by orgId and name");
    }
  }

  @Override
  public List<SourcingAttributesDefinitionDomainDto>
      fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
          String orgId,
          SourcingAttributesDefinitionStatus status,
          SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException {
    try {
      List<SourcingAttributesDefinitionEntity> sourcingAttributesDefinitionEntities =
          getRepository().findByOrgIdAndStatusAndScope(orgId, status, scope);
      return getMapper().toDomain(sourcingAttributesDefinitionEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch sourcing rule attributes definition entity list by orgId and status and scope",
          e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch sourcing rule attributes definition entity list by orgId and status and scope");
    }
  }

  @Override
  public List<SourcingRuleByNodeGroupCountProjection> fetchActiveSourcingRuleCountByNodeGroupIds(
      List<String> nodeGroupEntityIds, Long sourcingAttributesDefinitionId) {
    return getRepository()
        .fetchActiveSourcingRuleCountByNodeGroupIds(
            nodeGroupEntityIds, sourcingAttributesDefinitionId);
  }
}
