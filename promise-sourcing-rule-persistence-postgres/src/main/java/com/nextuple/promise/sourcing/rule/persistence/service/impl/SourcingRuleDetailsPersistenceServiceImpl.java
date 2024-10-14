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
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRuleDetailsDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingRuleDetailsDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRuleDetailsEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingRuleDetailsKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.SourcingRuleDetailsEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.SourcingRuleDetailsRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingRuleDetailsPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SourcingRuleDetailsPersistenceServiceImpl
    extends CommonPersistenceService<
        SourcingRuleDetailsDomainDto,
        SourcingRuleDetailsDomainKey,
        SourcingRuleDetailsEntity,
        SourcingRuleDetailsKey,
        SourcingRuleDetailsRepository,
        SourcingRuleDetailsEntityMapper>
    implements SourcingRuleDetailsPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRuleDetailsPersistenceServiceImpl.class);

  public SourcingRuleDetailsDomainDto saveSourcingNodes(
      SourcingRuleDetailsDomainDto sourcingRuleDetailsDomainDto) throws PromiseEngineException {
    try {
      return save(sourcingRuleDetailsDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save sourcing nodes info", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save sourcing nodes info");
    }
  }

  public Optional<SourcingRuleDetailsDomainDto> getSourcingRuleDetailsByIdAndOrgId(
      Long id, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdAndOrgId(id, orgId).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule details by id and orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule details by id and orgId");
    }
  }

  public List<SourcingRuleDetailsDomainDto> fetchBySourcingRuleId(String orgId, Long sourcingRuleId)
      throws PromiseEngineException {
    try {
      List<SourcingRuleDetailsEntity> sourcingRuleDetailsEntities =
          getRepository().findByOrgIdAndSourcingRuleId(orgId, sourcingRuleId);
      return getMapper().toDomain(sourcingRuleDetailsEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rule details by sourcingRuleId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rule details by sourcingRuleId");
    }
  }

  public void deleteMultipleSourcingRuleDetails(
      List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsDomainDtoList)
      throws PromiseEngineException {
    try {
      getRepository().deleteAll(getMapper().toEntity(sourcingRuleDetailsDomainDtoList));
    } catch (Exception e) {
      logger.error("Unable to delete multiple sourcing rule details", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete multiple sourcing rule details");
    }
  }

  public List<SourcingRuleDetailsDomainDto> saveAll(
      List<SourcingRuleDetailsDomainDto> sourcingRuleDetailsDomainDtoList) {
    List<SourcingRuleDetailsEntity> sourcingRuleDetailsEntities =
        getRepository().saveAll(getMapper().toEntity(sourcingRuleDetailsDomainDtoList));
    return getMapper().toDomain(sourcingRuleDetailsEntities);
  }

  public List<SourcingRuleDetailsDomainDto> getAllSourcingRuleByOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<SourcingRuleDetailsEntity> sourcingRuleDetailsEntities =
          getRepository().findByOrgId(orgId);
      return getMapper().toDomain(sourcingRuleDetailsEntities);
    } catch (Exception e) {
      logger.error("Unable to find sourcing rules by orgId", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find sourcing rules by orgId");
    }
  }
}
