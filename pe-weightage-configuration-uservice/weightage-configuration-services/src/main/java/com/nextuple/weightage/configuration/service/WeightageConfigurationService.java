/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.service;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.AVAILABILITY;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.domain.mapper.WeightageConfigurationMapper;
import com.nextuple.weightage.configuration.persistence.domain.WeightageConfigurationDomainDto;
import com.nextuple.weightage.configuration.persistence.service.WeightageConfigurationPersistenceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeightageConfigurationService {
  @Value("#{'${availability.keys}'.split(',')}")
  private Set<String> availabilityKeys;

  private static final Logger logger = LoggerFactory.getLogger(WeightageConfigurationService.class);
  private final WeightageConfigurationPersistenceService weightageConfigurationPersistenceService;

  private static final WeightageConfigurationMapper INSTANCE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  /**
   * Convert WeightageConfigurationDomainDto domainDto to WeightageConfigurationDto with all
   * required processing
   *
   * @param weightageConfigurationDomainDto WeightageConfigurationDomainDto domainDto
   * @return WeightageConfigurationDto dto
   */
  private WeightageConfigurationDto prepareWeightageConfigurationDto(
      WeightageConfigurationDomainDto weightageConfigurationDomainDto) {
    return INSTANCE.convertToWeightageConfigurationDto(weightageConfigurationDomainDto);
  }

  /**
   * Fetch Weightage
   *
   * @param baseRequest Fetch Weightage Request
   * @return Map&lt;String, Float&gt; fetchWeightageResponse
   * @throws PromiseEngineException
   */
  public Map<String, Float> fetchWeightage(FetchWeightageRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchWeightage service --");
    validateKeys(baseRequest.getKeys());
    List<WeightageConfigurationDomainDto> weightageConfigurationList =
        weightageConfigurationPersistenceService.fetchWeightage(baseRequest);
    if (weightageConfigurationList.isEmpty()) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Weightage Configurations not found!");
    }

    Map<String, Float> fetchWeightageResponse = new HashMap<>();
    weightageConfigurationList.forEach(
        weightageConfiguration ->
            fetchWeightageResponse.put(
                weightageConfiguration.getKey(), weightageConfiguration.getWeightage()));

    return fetchWeightageResponse;
  }

  /**
   * Create Weightage Configuration
   *
   * @param baseRequest for Creating WeightageConfiguration
   * @return Created Weightage Configuration Dto
   * @throws PromiseEngineException
   */
  public WeightageConfigurationDto createWeightageConfiguration(
      CreateWeightageConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createWeightageConfiguration service --");
    if (Objects.equals(baseRequest.getType(), AVAILABILITY)
        && !availabilityKeys.contains(baseRequest.getKey())) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_UNAUTHORIZED_ACTION,
          "Invalid key for AVAILABILITY type");
    }
    String orgId = baseRequest.getOrgId();
    String type = baseRequest.getType();
    String key = baseRequest.getKey();

    validateWeightageConfiguration(orgId, type, key);
    var weightageConfigurationDomainDto =
        INSTANCE.convertCreateWeightageConfigurationRequestToWeightageConfigurationDomainDto(
            baseRequest);

    return prepareWeightageConfigurationDto(
        weightageConfigurationPersistenceService.saveWeightageConfiguration(
            weightageConfigurationDomainDto));
  }

  private void validateWeightageConfiguration(String orgId, String type, String key)
      throws PromiseEngineException, CommonServiceException {
    Optional<WeightageConfigurationDomainDto> weightageConfigurationDomainDto =
        Optional.ofNullable(
            weightageConfigurationPersistenceService.getWeightageConfiguration(orgId, type, key));
    if (weightageConfigurationDomainDto.isPresent()) {
      logger.error("-- Weightage Configuration already exists! --");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put("org-id", FieldError.builder().rejectedValue(orgId).build());
      errorMap.put("type", FieldError.builder().rejectedValue(type).build());
      errorMap.put("key", FieldError.builder().rejectedValue(key).build());
      throw new CommonServiceException(
          "Weightage Configuration already exists!", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  /**
   * Get Weightage Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @return Fetched Weightage Configuration Dto
   * @throws PromiseEngineException
   */
  @ReaderDS
  public WeightageConfigurationDto getWeightageConfiguration(String orgId, String type, String key)
      throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfiguration service --");
    Optional<WeightageConfigurationDomainDto> weightageConfigurationDomainDto =
        Optional.ofNullable(
            weightageConfigurationPersistenceService.getWeightageConfiguration(orgId, type, key));
    if (weightageConfigurationDomainDto.isEmpty()) {
      logger.error("-- Weightage Configuration not found --");
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Weightage Configuration not found!");
    }
    return prepareWeightageConfigurationDto(weightageConfigurationDomainDto.get());
  }

  /**
   * Get Weightage Configuration by key
   *
   * @param key Key
   * @return Fetched List of Weightage Configuration Dto
   * @throws PromiseEngineException
   */
  @ReaderDS
  public List<WeightageConfigurationDto> getWeightageConfigurationsByKey(String key)
      throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfigurationByKey service --");
    List<WeightageConfigurationDomainDto> weightageConfigurationList =
        weightageConfigurationPersistenceService.getWeightageConfigurationsByKey(key);
    return weightageConfigurationList.stream()
        .map(this::prepareWeightageConfigurationDto)
        .collect(Collectors.toList());
  }

  /**
   * Updates Weightage Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @param baseRequest for Updating WeightageConfiguration
   * @return Returns and evicts data for updated promise sourcing rule entity
   * @throws PromiseEngineException
   */
  public WeightageConfigurationDto updateWeightageConfiguration(
      String orgId, String type, String key, UpdateWeightageConfigurationRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("-- inside updateWeightageConfiguration service --");
    var weightageConfigurationFromDB =
        INSTANCE.convertToWeightageConfigurationDomainDto(
            getWeightageConfiguration(orgId, type, key));
    logger.info(
        "Response before updation of weightage configuration :{}",
        INSTANCE.convertToWeightageConfigurationDto(weightageConfigurationFromDB));
    INSTANCE.insertValuesFromUpdateWeightageConfigurationRequestToDomainDto(
        baseRequest, weightageConfigurationFromDB);

    return prepareWeightageConfigurationDto(
        weightageConfigurationPersistenceService.saveWeightageConfiguration(
            weightageConfigurationFromDB));
  }

  /**
   * Delete Weightage Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @return Returns and deletes data entity
   * @throws PromiseEngineException
   */
  public WeightageConfigurationDto deleteWeightageConfiguration(
      String orgId, String type, String key) throws PromiseEngineException {
    logger.debug("-- inside deleteWeightageConfiguration service --");
    var weightageConfigurationFromDB =
        INSTANCE.convertToWeightageConfigurationDomainDto(
            getWeightageConfiguration(orgId, type, key));
    logger.info(
        "Response before deletion of weightage configuration :{}",
        INSTANCE.convertToWeightageConfigurationDto(weightageConfigurationFromDB));
    return prepareWeightageConfigurationDto(
        weightageConfigurationPersistenceService.deleteWeightageConfiguration(
            weightageConfigurationFromDB));
  }

  public List<WeightageCacheKeyDto> getAllWeightageCacheKeys(Integer limit)
      throws PromiseEngineException {
    List<WeightageConfigurationDomainDto> weightageConfigurationDomainDtoList =
        weightageConfigurationPersistenceService.getAllWeightageConfiguration(limit);

    return INSTANCE.convertToWeightageCacheKeyDtoList(weightageConfigurationDomainDtoList);
  }

  public void validateKeys(List<String> keys) throws CommonServiceException {
    if (keys.stream().anyMatch(String::isBlank)) {
      throw new CommonServiceException(
          "Keys cannot contain null or an empty string", HttpStatus.BAD_REQUEST, 0x1771, null);
    }
  }
}
