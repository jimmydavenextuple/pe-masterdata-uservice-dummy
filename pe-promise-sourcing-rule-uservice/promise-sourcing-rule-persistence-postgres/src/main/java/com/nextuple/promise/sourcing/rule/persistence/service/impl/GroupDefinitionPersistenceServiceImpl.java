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
import com.nextuple.promise.sourcing.rule.persistence.domain.GroupDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.GroupDefinitionDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.GroupDefinitionEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.GroupDefinitionKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.GroupDefinitionEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.GroupDefinitionRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupDefinitionPersistenceServiceImpl
    extends CommonPersistenceService<
        GroupDefinitionDomainDto,
        GroupDefinitionDomainKey,
        GroupDefinitionEntity,
        GroupDefinitionKey,
        GroupDefinitionRepository,
        GroupDefinitionEntityMapper>
    implements GroupDefinitionPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(GroupDefinitionPersistenceServiceImpl.class);

  @Override
  public GroupDefinitionDomainDto saveGroupDefinition(
      GroupDefinitionDomainDto groupDefinitionDomainDto) throws PromiseEngineException {
    logger.debug(
        "-- inside saveGroupDefinition domain -- with groupDefinitionDomainDto:{}",
        groupDefinitionDomainDto);
    try {
      return save(groupDefinitionDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save group definition", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save group definition");
    }
  }

  @Override
  public Optional<GroupDefinitionDomainDto> fetchGroupDefinitionById(Long id)
      throws PromiseEngineException {
    logger.debug("-- inside fetchGroupDefinitionById domain -- with id:{}", id);
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find group definition by id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find group definition by id");
    }
  }

  @Override
  public Optional<GroupDefinitionDomainDto> fetchGroupDefinitionByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException {
    logger.debug("-- inside fetchGroupDefinitionById domain -- with id:{} orgId:{}", id, orgId);
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find group definition by id and org id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find group definition by id and org id");
    }
  }

  @Override
  public List<GroupDefinitionDomainDto> fetchGroupDefinitionListByOrgIdAndName(
      String orgId, String name) throws PromiseEngineException {
    logger.debug(
        "-- inside fetchGroupDefinitionListByOrgIdAndName domain -- with orgId:{} name:{}",
        orgId,
        name);
    try {
      List<GroupDefinitionEntity> groupDefinitionEntities =
          getRepository().findByOrgIdAndGroupName(orgId, name);
      return getMapper().toDomain(groupDefinitionEntities);
    } catch (Exception e) {
      logger.error("Unable to fetch group definition list by orgId and name", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch group definition list by orgId and name");
    }
  }

  @Override
  public List<GroupDefinitionDomainDto>
      fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
          String orgId, Long sourcingAttributesDefinitionId, String reqAttributesValue)
          throws PromiseEngineException {
    logger.debug(
        "-- inside fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue domain -- with orgId:{} sourcingAttributesDefinitionId:{} reqAttributesValue:{} ",
        orgId,
        sourcingAttributesDefinitionId,
        reqAttributesValue);
    try {
      List<GroupDefinitionEntity> groupDefinitionEntities =
          getRepository()
              .findByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValue(
                  orgId, sourcingAttributesDefinitionId, reqAttributesValue);
      return getMapper().toDomain(groupDefinitionEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch group definition list by orgId , sourcingAttributesDefinitionId and reqAttributesValue",
          e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch group definition list by orgId , sourcingAttributesDefinitionId and reqAttributesValue");
    }
  }

  @Override
  public List<GroupDefinitionDomainDto>
      fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValueAndOptionalAttributeValue(
          String orgId,
          Long sourcingAttributesDefinitionId,
          String reqAttributesValue,
          String optionalAttributeValue)
          throws PromiseEngineException {
    logger.debug(
        "-- inside fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionIdAndReqAttributesValueAndOptionalAttributeValue domain -- with orgId:{} sourcingAttributesDefinitionId:{} reqAttributesValue:{} optionalAttributeValue: {}",
        orgId,
        sourcingAttributesDefinitionId,
        reqAttributesValue,
        optionalAttributeValue);
    try {
      List<GroupDefinitionEntity> groupDefinitionEntities =
          getRepository()
              .findBySourcingAttributesDefinitionIdAndReqAttributesValueAndOptionalAttributesValueAndOrgId(
                  sourcingAttributesDefinitionId,
                  reqAttributesValue,
                  optionalAttributeValue,
                  orgId);
      return getMapper().toDomain(groupDefinitionEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch group definition list by orgId , sourcingAttributesDefinitionId, reqAttributesValue and optionalAttributesValue",
          e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch group definition list by orgId , sourcingAttributesDefinitionId, reqAttributesValue and optionalAttributesValue");
    }
  }

  @Override
  public List<GroupDefinitionDomainDto>
      fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
          String orgId, Long sourcingAttributesDefinitionId) throws PromiseEngineException {
    logger.debug(
        "-- inside fetchGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId domain -- with orgId:{} sourcingAttributesDefinitionId:{} ",
        orgId,
        sourcingAttributesDefinitionId);
    try {
      List<GroupDefinitionEntity> groupDefinitionEntities =
          getRepository()
              .findByOrgIdAndSourcingAttributesDefinitionId(orgId, sourcingAttributesDefinitionId);
      return getMapper().toDomain(groupDefinitionEntities);
    } catch (Exception e) {
      logger.error(
          "Unable to fetch group definition list by orgId and sourcingAttributesDefinitionId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch group definition list by orgId and sourcingAttributesDefinitionId");
    }
  }

  @Override
  public void deleteGroupDefinition(GroupDefinitionDomainDto groupDefinitionDomainDto)
      throws PromiseEngineException {
    logger.debug(
        "-- inside deleteGroupDefinition domain -- with groupDefinitionDomainDto:{}",
        groupDefinitionDomainDto);
    try {
      delete(groupDefinitionDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete group definition", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to delete group definition");
    }
  }

  public void deleteByIdIn(List<Long> groupIdsToDelete) throws PromiseEngineException {
    logger.debug("-- inside deleteByIdIn domain -- with groupIdsToDelete:{}", groupIdsToDelete);
    try {
      getRepository().deleteByIdIn(groupIdsToDelete);
    } catch (Exception e) {
      logger.error("Unable to delete group definitions", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to delete group definitions");
    }
  }
}
