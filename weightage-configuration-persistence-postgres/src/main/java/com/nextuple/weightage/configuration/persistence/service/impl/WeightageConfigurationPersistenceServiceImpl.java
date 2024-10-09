/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.domain.key.WeightageConfigurationDomainKey;
import com.nextuple.weightage.configuration.persistence.entity.WeightageConfigurationEntity;
import com.nextuple.weightage.configuration.persistence.entity.key.WeightageConfigurationKey;
import com.nextuple.weightage.configuration.persistence.mapper.WeightageConfigurationEntityMapper;
import com.nextuple.weightage.configuration.persistence.repository.WeightageConfigurationRepository;
import com.nextuple.weightage.configuration.persistence.service.WeightageConfigurationPersistenceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeightageConfigurationPersistenceServiceImpl
    extends CommonPersistenceService<
        WeightageConfigurationDomainDto,
        WeightageConfigurationDomainKey,
        WeightageConfigurationEntity,
        WeightageConfigurationKey,
        WeightageConfigurationRepository,
        WeightageConfigurationEntityMapper>
    implements WeightageConfigurationPersistenceService {
  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationPersistenceServiceImpl.class);

  /**
   * Fetch Weightage
   *
   * @param baseRequest Fetch Weightage Request
   * @return List of WeightageConfigurationDomainDto
   * @throws PromiseEngineException
   */
  @Override
  public List<WeightageConfigurationDomainDto> fetchWeightage(FetchWeightageRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("-- inside fetchWeightage domain --");
    try {
      List<WeightageConfigurationEntity> weightageConfigurationEntities =
          getRepository()
              .findByKeyInAndOrgIdAndType(
                  baseRequest.getKeys(), baseRequest.getOrgId(), baseRequest.getType());
      return getMapper().toDomain(weightageConfigurationEntities);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find weightage configuration");
    }
  }

  /**
   * Save Weightage Configuration
   *
   * @param weightageConfigurationDomainDto Entity to be saved
   * @return Saved WeightageConfigurationDomainDto
   * @throws PromiseEngineException
   */
  @Override
  public WeightageConfigurationDomainDto saveWeightageConfiguration(
      WeightageConfigurationDomainDto weightageConfigurationDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside saveWeightageConfiguration domain --");
    try {
      return save(weightageConfigurationDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save weightage configuration");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save Weightage Configuration");
    }
  }

  /**
   * Get Weightage Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @return Fetched WeightageConfigurationDomainDto
   * @throws PromiseEngineException
   */
  @Override
  public WeightageConfigurationDomainDto getWeightageConfiguration(
      String orgId, String type, String key) throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfiguration domain --");
    try {
      WeightageConfigurationEntity weightageConfigurationEntity =
          getRepository().findByOrgIdAndTypeAndKey(orgId, type, key);
      return getMapper().toDomain(weightageConfigurationEntity);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Weightage Configuration");
    }
  }

  /**
   * Get Weightage Configuration By Key
   *
   * @param key Key
   * @return Fetched WeightageConfigurationDomainDto
   * @throws PromiseEngineException
   */
  @Override
  public List<WeightageConfigurationDomainDto> getWeightageConfigurationsByKey(String key)
      throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfigurationByKey domain --");
    try {
      List<WeightageConfigurationEntity> weightageConfigurationEntities =
          getRepository().findByKey(key);
      return getMapper().toDomain(weightageConfigurationEntities);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Weightage Configuration");
    }
  }

  /**
   * Delete Weightage Configuration
   *
   * @param weightageConfigurationDomainDto Weightage Configuration Entity to be deleted
   * @return Deleted WeightageConfigurationDomainDto
   * @throws PromiseEngineException
   */
  @Override
  public WeightageConfigurationDomainDto deleteWeightageConfiguration(
      WeightageConfigurationDomainDto weightageConfigurationDomainDto)
      throws PromiseEngineException {
    logger.debug("-- inside deleteWeightageConfiguration domain --");
    try {
      delete(weightageConfigurationDomainDto);
      return weightageConfigurationDomainDto;
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Weightage Configuration");
    }
  }

  @Override
  public List<WeightageConfigurationDomainDto> getAllWeightageConfiguration(Integer limit)
      throws PromiseEngineException {
    try {
      List<WeightageConfigurationEntity> weightageConfigurationEntities =
          getRepository().findAllRecords(limit);
      return getMapper().toDomain(weightageConfigurationEntities);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch all Weightage Configuration records");
    }
  }
}
