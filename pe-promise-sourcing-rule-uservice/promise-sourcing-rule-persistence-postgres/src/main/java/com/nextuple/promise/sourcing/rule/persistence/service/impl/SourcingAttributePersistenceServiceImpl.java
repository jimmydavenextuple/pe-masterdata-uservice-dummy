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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingAttributeDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingAttributeEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingAttributeKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingAttributeEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingAttributeRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingAttributePersistenceServiceImpl
    extends CommonPersistenceService<
        SourcingAttributeDomainDto,
        SourcingAttributeDomainKey,
        SourcingAttributeEntity,
        SourcingAttributeKey,
        SourcingAttributeRepository,
        SourcingAttributeEntityMapper>
    implements SourcingAttributePersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributePersistenceServiceImpl.class);

  /*Save Sourcing Attribute
   *
   * @param sourcingAttributeEntity to be saved
   * @return Save sourcing attribute
   * @throws PromiseEngineException
   */
  @Override
  public SourcingAttributeDomainDto saveSourcingAttribute(
      SourcingAttributeDomainDto sourcingAttributeDomainDto) throws PromiseEngineException {
    try {
      return save(sourcingAttributeDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save sourcing attribute", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing attribute");
    }
  }

  /*Fetch Sourcing Attribute By Id
   *
   * @param id Primary key
   * return sourcing attribute if found
   * throws PromiseEngineException
   */
  @Override
  public Optional<SourcingAttributeDomainDto> getSourcingAttributeById(Long id)
      throws PromiseEngineException {
    try {
      return getRepository().findById(id).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute");
    }
  }

  @Override
  public Optional<SourcingAttributeDomainDto> getSourcingAttributeByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute by id and orgId");
    }
  }

  @Override
  public List<SourcingAttributeDomainDto> getSourcingAttributeListByOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<SourcingAttributeEntity> sourcingAttributeEntities = getRepository().findByOrgId(orgId);
      return getMapper().toDomain(sourcingAttributeEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute list by orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute list by orgId");
    }
  }

  @Override
  public List<SourcingAttributeDomainDto> getSourcingAttributeListByOrgIdAndAttributeName(
      String orgId, String attributeName) throws PromiseEngineException {
    try {
      List<SourcingAttributeEntity> sourcingAttributeEntities =
          getRepository().findByOrgIdAndAttributeName(orgId, attributeName);
      return getMapper().toDomain(sourcingAttributeEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute list by orgId and attribute name", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute list by orgId and attribute name");
    }
  }

  @Override
  public List<SourcingAttributeDomainDto> getSourcingAttributeListByOrgIdAndAttributeNames(
      String orgId, List<String> attributeName) throws PromiseEngineException {
    try {
      List<SourcingAttributeEntity> sourcingAttributeEntities =
          getRepository().findByOrgIdAndAttributeNameIn(orgId, attributeName);
      return getMapper().toDomain(sourcingAttributeEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute list by orgId and attribute name", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute list by orgId and attribute name");
    }
  }

  @Override
  public List<SourcingAttributeDomainDto> fetchSourcingAttributeListByOrgIdAndAttributeIds(
      String orgId, List<Long> ids) throws PromiseEngineException {
    try {
      List<SourcingAttributeEntity> sourcingAttributeEntities =
          getRepository().findByOrgIdAndIdIn(orgId, ids);
      return getMapper().toDomain(sourcingAttributeEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing attribute list by orgId and id", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing attribute list by orgId and ids");
    }
  }
}
