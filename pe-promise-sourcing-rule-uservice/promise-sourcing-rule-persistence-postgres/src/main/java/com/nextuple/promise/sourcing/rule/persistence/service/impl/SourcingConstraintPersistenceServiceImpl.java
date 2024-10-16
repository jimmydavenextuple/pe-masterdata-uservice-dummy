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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingConstraintDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingConstraintEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingConstraintKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingConstraintEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingConstraintRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingConstraintPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingConstraintPersistenceServiceImpl
    extends CommonPersistenceService<
        SourcingConstraintDomainDto,
        SourcingConstraintDomainKey,
        SourcingConstraintEntity,
        SourcingConstraintKey,
        SourcingConstraintRepository,
        SourcingConstraintEntityMapper>
    implements SourcingConstraintPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingConstraintPersistenceServiceImpl.class);

  @Override
  public SourcingConstraintDomainDto saveSourcingConstraintEntity(
      SourcingConstraintDomainDto sourcingConstraintDomainDto) throws PromiseEngineException {
    try {
      return save(sourcingConstraintDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save sourcing constraint entity entity", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing constraint entity");
    }
  }

  @Override
  public List<SourcingConstraintDomainDto> saveSourcingConstraintEntities(
      List<SourcingConstraintDomainDto> sourcingConstraintDomainDtos)
      throws PromiseEngineException {
    try {
      List<SourcingConstraintEntity> sourcingConstraintEntities =
          getRepository().saveAll(getMapper().toEntity(sourcingConstraintDomainDtos));
      return getMapper().toDomain(sourcingConstraintEntities);
    } catch (Exception e) {
      logger.error("Unable to save sourcing constraint entities", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing constraint entities");
    }
  }

  @Override
  public Optional<SourcingConstraintDomainDto> getSourcingConstraintEntityByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing constraint entity by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing constraint entity by id and orgId");
    }
  }

  @Override
  public List<SourcingConstraintDomainDto> fetchByOrgIdAndGroupId(String orgId, String groupId)
      throws PromiseEngineException {
    try {
      List<SourcingConstraintEntity> sourcingConstraintEntities =
          getRepository().findByOrgIdAndGroupId(orgId, groupId);
      return getMapper().toDomain(sourcingConstraintEntities);
    } catch (Exception e) {
      logger.error("Unable to fetch sourcing constraint entity list by orgId and groupId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch sourcing constraint entity list by orgId and groupId");
    }
  }

  @Override
  public List<SourcingConstraintDomainDto> fetchByOrgIdAndGroupIdAndConstraint(
      String orgId, String groupId, SourcingConstraintEnum constraint)
      throws PromiseEngineException {
    try {
      List<SourcingConstraintEntity> sourcingConstraintEntityList =
          getRepository().findByOrgIdAndGroupIdAndSourcingConstraint(orgId, groupId, constraint);
      return getMapper().toDomain(sourcingConstraintEntityList);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch sourcing constraint entity list by orgId , groupId and constraint", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch sourcing constraint entity list by orgId , groupId and constraint");
    }
  }

  @Override
  public void deleteSourcingConstraint(SourcingConstraintDomainDto sourcingConstraintDomainDto)
      throws PromiseEngineException {
    try {
      delete(sourcingConstraintDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete sourcing constraint", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete sourcing constraint");
    }
  }

  @Override
  public void deleteByIdIn(List<Long> sourcingConstraintIdsToDelete) throws PromiseEngineException {
    try {
      getRepository().deleteByIdIn(sourcingConstraintIdsToDelete);
    } catch (Exception e) {
      logger.error("Unable to delete sourcing constraints", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete sourcing constraints");
    }
  }
}
