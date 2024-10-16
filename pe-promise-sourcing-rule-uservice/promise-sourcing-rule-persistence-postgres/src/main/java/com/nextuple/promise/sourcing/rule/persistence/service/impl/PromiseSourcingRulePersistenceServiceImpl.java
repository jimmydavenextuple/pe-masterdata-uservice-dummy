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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.PromiseSourcingRuleDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.PromiseSourcingRuleEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.PromiseSourcingRuleKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.PromiseSourcingRuleEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.PromiseSourcingRuleRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.PromiseSourcingRulePersistenceService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PromiseSourcingRulePersistenceServiceImpl
    extends CommonPersistenceService<
        PromiseSourcingRuleDomainDto,
        PromiseSourcingRuleDomainKey,
        PromiseSourcingRuleEntity,
        PromiseSourcingRuleKey,
        PromiseSourcingRuleRepository,
        PromiseSourcingRuleEntityMapper>
    implements PromiseSourcingRulePersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(PromiseSourcingRulePersistenceServiceImpl.class);
  public static final String SOURCING_RULE_ERROR_MESSAGE = "Unable to find Promise Sourcing Rule!";

  private static final String EXCEPTION_MESSAGE = "Promise Sourcing Rule not found!";

  /**
   * Fetch Promise Sourcing Rule
   *
   * @param baseRequest Fetch Promise Sourcing Rule Request
   * @return List of Promise Sourcing Rules
   * @throws PromiseEngineException
   */
  @Override
  public List<PromiseSourcingRuleDomainDto> fetchSourcingRule(
      FetchPromiseSourcingRuleRequest baseRequest) throws PromiseEngineException {
    try {
      List<PromiseSourcingRuleEntity> promiseSourcingRuleEntityList =
          getRepository()
              .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                  baseRequest.getServiceOptions(),
                  baseRequest.getOrgId(),
                  baseRequest.getAllocationRuleId(),
                  baseRequest.getDestinationGeoZone());
      return getMapper().toDomain(promiseSourcingRuleEntityList);
    } catch (Exception e) {
      logger.error(String.valueOf(e), SOURCING_RULE_ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          SOURCING_RULE_ERROR_MESSAGE);
    }
  }

  /**
   * @param serviceOption Service option
   * @param orgId Org id
   * @param allocationId Allocation Id
   * @param destinationGeoZone Destination GeoZone
   * @return List of Promise Sourcing Rules
   * @throws PromiseEngineException
   */
  @Override
  public List<PromiseSourcingRuleDomainDto> fetchSourcingRule(
      String serviceOption, String orgId, String allocationId, String destinationGeoZone)
      throws PromiseEngineException {
    logger.debug("-- inside fetchSourcingRule domain --");
    try {
      List<PromiseSourcingRuleEntity> promiseSourcingRuleEntityList =
          getRepository()
              .findByServiceOptionAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
                  serviceOption, orgId, allocationId, destinationGeoZone);
      return getMapper().toDomain(promiseSourcingRuleEntityList);
    } catch (Exception e) {
      logger.error(String.valueOf(e), SOURCING_RULE_ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          SOURCING_RULE_ERROR_MESSAGE);
    }
  }

  /**
   * Save Promise sourcing rule
   *
   * @param promiseSourcingRuleDomainDto Entity to be saved
   * @return Saved Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  @Override
  public PromiseSourcingRuleDomainDto savePromiseSourcingRule(
      PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto) throws PromiseEngineException {
    try {
      return save(promiseSourcingRuleDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save Promise sourcing rule!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save Promise sourcing rule!");
    }
  }

  /**
   * Get Promise Sourcing Rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  @Override
  public PromiseSourcingRuleDomainDto getPromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    try {
      PromiseSourcingRuleEntity promiseSourcingRuleEntity =
          getRepository()
              .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
                  orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
      return getMapper().toDomain(promiseSourcingRuleEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), EXCEPTION_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, EXCEPTION_MESSAGE);
    }
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param orgId Organisation ID
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  @Override
  public List<PromiseSourcingRuleDomainDto> getPromiseSourcingRulesByOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<PromiseSourcingRuleEntity> promiseSourcingRuleEntityList =
          getRepository().findByOrgId(orgId);
      return getMapper().toDomain(promiseSourcingRuleEntityList);
    } catch (Exception e) {
      logger.error(String.valueOf(e), EXCEPTION_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, EXCEPTION_MESSAGE);
    }
  }

  /**
   * Get Promise Sourcing Rules by priority
   *
   * @param priority Priority
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  @Override
  public List<PromiseSourcingRuleDomainDto> getPromiseSourcingRulesByPriority(int priority)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRulesByPriority domain --");
    try {
      List<PromiseSourcingRuleEntity> promiseSourcingRuleEntityList =
          getRepository().findByPriority(priority);
      return getMapper().toDomain(promiseSourcingRuleEntityList);
    } catch (Exception e) {
      logger.error(String.valueOf(e), EXCEPTION_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, EXCEPTION_MESSAGE);
    }
  }

  /**
   * Delete Promise Sourcing Rule
   *
   * @param promiseSourcingRuleDomainDto Promise Sourcing Rule Entity to be deleted
   * @return Deleted Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  @Override
  public PromiseSourcingRuleDomainDto deletePromiseSourcingRule(
      PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto) throws PromiseEngineException {
    logger.debug("-- inside deletePromiseSourcingRule domain --");
    try {
      delete(promiseSourcingRuleDomainDto);
      return promiseSourcingRuleDomainDto;
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete Promise Sourcing Rule!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Promise Sourcing Rule!");
    }
  }
}
