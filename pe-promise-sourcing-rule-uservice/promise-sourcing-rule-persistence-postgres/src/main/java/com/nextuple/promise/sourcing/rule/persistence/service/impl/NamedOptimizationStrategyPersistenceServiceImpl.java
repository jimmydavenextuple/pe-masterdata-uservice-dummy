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
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.NamedOptimizationStrategyDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.NamedOptimizationStrategyEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.NamedOptimizationStrategyKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.NamedOptimizationStrategyEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.NamedOptimizationStrategyRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.NamedOptimizationStrategyPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NamedOptimizationStrategyPersistenceServiceImpl
    extends CommonPersistenceService<
        NamedOptimizationStrategyDomainDto,
        NamedOptimizationStrategyDomainKey,
        NamedOptimizationStrategyEntity,
        NamedOptimizationStrategyKey,
        NamedOptimizationStrategyRepository,
        NamedOptimizationStrategyEntityMapper>
    implements NamedOptimizationStrategyPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(NamedOptimizationStrategyPersistenceServiceImpl.class);

  @Override
  public NamedOptimizationStrategyDomainDto saveOptimizationStrategy(
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto)
      throws PromiseEngineException {
    try {
      return save(namedOptimizationStrategyDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save optimization strategy", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save optimization strategy");
    }
  }

  @Override
  public Optional<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyById(Long id)
      throws PromiseEngineException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find optimization strategy by id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find optimization strategy by id");
    }
  }

  @Override
  public Optional<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find optimization strategy by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find optimization strategy by id and orgId");
    }
  }

  @Override
  public List<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId) throws PromiseEngineException {
    try {
      List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntities =
          getRepository().findByOrgIdAndGroupId(orgId, groupId);
      return getMapper().toDomain(namedOptimizationStrategyEntities);
    } catch (Exception e) {
      logger.error("Unable to fetch optimization strategy by orgId and groupId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch optimization strategy by orgId and groupId");
    }
  }

  @Override
  public List<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndStrategyName(
      String orgId, String optimizationStrategyName) throws PromiseEngineException {
    try {
      List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntities =
          getRepository().findByOrgIdAndOptimizationStrategyName(orgId, optimizationStrategyName);
      return getMapper().toDomain(namedOptimizationStrategyEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch optimization strategy by orgId and optimizationStrategyName", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch optimization strategy by orgId and optimizationStrategyName");
    }
  }

  @Override
  public void deleteOptimizationStrategy(
      NamedOptimizationStrategyDomainDto namedOptimizationStrategyDomainDto)
      throws PromiseEngineException {
    try {
      delete(namedOptimizationStrategyDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete optimization strategy", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to delete optimization strategy");
    }
  }

  @Override
  public Page<NamedOptimizationStrategyDomainDto> fetchOptimizationStrategyByOrgIdAndGroupId(
      String orgId, String groupId, Pageable pageable) throws PromiseEngineException {
    logger.debug("-- inside fetchOptimizationStrategyByOrgIdAndGroupId domain --");
    try {
      return getRepository()
          .findByOrgIdAndGroupId(orgId, groupId, pageable)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to fetch optimization strategies by orgId and groupId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch optimization strategies by orgId and groupId");
    }
  }

  @Override
  public void deleteByIdIn(List<Long> optimizationRuleIds) throws PromiseEngineException {
    try {
      getRepository().deleteByIdIn(optimizationRuleIds);
    } catch (Exception e) {

      logger.error("Unable to delete optimization rules", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to delete optimization rules");
    }
  }

  @Override
  public List<NamedOptimizationStrategyDomainDto>
      fetchOptimizationStrategyByOrgIdAndOptimizationStrategyDetails(
          String orgId, String optimizationStrategyDetails) throws PromiseEngineException {
    try {
      List<NamedOptimizationStrategyEntity> namedOptimizationStrategyEntities =
          getRepository()
              .findByOrgIdAndOptimizationStrategyDetails(orgId, optimizationStrategyDetails);
      return getMapper().toDomain(namedOptimizationStrategyEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch optimization strategy by orgId and optimizationStrategyDetails", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch optimization strategy by orgId and optimizationStrategyDetails");
    }
  }
}
