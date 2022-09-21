package com.hbc.weightage.configuration.service;

import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.AVAILABILITY;

import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.error.FieldError;
import com.hbc.postgres.config.ReaderDS;
import com.hbc.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.domain.WeightageConfigurationDomain;
import com.hbc.weightage.configuration.domain.entity.WeightageConfiguration;
import com.hbc.weightage.configuration.domain.mapper.WeightageConfigurationMapper;
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
  private final WeightageConfigurationDomain weightageConfigurationDomain;

  private static final WeightageConfigurationMapper INSTANCE =
      Mappers.getMapper(WeightageConfigurationMapper.class);

  /**
   * Convert WeightageConfiguration entity to WeightageConfigurationDto with all required processing
   *
   * @param weightageConfiguration WeightageConfiguration entity
   * @return WeightageConfigurationDto dto
   */
  private WeightageConfigurationDto prepareWeightageConfigurationDto(
      WeightageConfiguration weightageConfiguration) {
    return INSTANCE.convertToWeightageConfigurationDto(weightageConfiguration);
  }

  /**
   * Fetch Weightage
   *
   * @param baseRequest Fetch Weightage Request
   * @return Map<String, Float> fetchWeightageResponse
   * @throws PromiseEngineException
   */
  public Map<String, Float> fetchWeightage(FetchWeightageRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("-- inside fetchWeightage service --");
    List<WeightageConfiguration> weightageConfigurationList =
        weightageConfigurationDomain.fetchWeightage(baseRequest);
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
    var weightageConfiguration =
        INSTANCE.convertCreateWeightageConfigurationRequestToWeightageConfigurationEntity(
            baseRequest);

    return prepareWeightageConfigurationDto(
        weightageConfigurationDomain.saveWeightageConfiguration(weightageConfiguration));
  }

  private void validateWeightageConfiguration(String orgId, String type, String key)
      throws PromiseEngineException, CommonServiceException {
    Optional<WeightageConfiguration> weightageConfiguration =
        Optional.ofNullable(
            weightageConfigurationDomain.getWeightageConfiguration(orgId, type, key));
    if (!weightageConfiguration.isEmpty()) {
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
    Optional<WeightageConfiguration> weightageConfiguration =
        Optional.ofNullable(
            weightageConfigurationDomain.getWeightageConfiguration(orgId, type, key));
    if (weightageConfiguration.isEmpty()) {
      logger.error("-- Weightage Configuration not found --");
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Weightage Configuration not found!");
    }
    return prepareWeightageConfigurationDto(weightageConfiguration.get());
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
    List<WeightageConfiguration> weightageConfigurationList =
        weightageConfigurationDomain.getWeightageConfigurationsByKey(key);
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
        INSTANCE.convertToWeightageConfigurationEntity(getWeightageConfiguration(orgId, type, key));
    logger.info(
        "Response before updation of weightage configuration :{}",
        INSTANCE.convertToWeightageConfigurationDto(weightageConfigurationFromDB));
    INSTANCE.insertValuesFromUpdateWeightageConfigurationRequestToEntity(
        baseRequest, weightageConfigurationFromDB);

    return prepareWeightageConfigurationDto(
        weightageConfigurationDomain.saveWeightageConfiguration(weightageConfigurationFromDB));
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
        INSTANCE.convertToWeightageConfigurationEntity(getWeightageConfiguration(orgId, type, key));
    logger.info(
        "Response before deletion of weightage configuration :{}",
        INSTANCE.convertToWeightageConfigurationDto(weightageConfigurationFromDB));
    return prepareWeightageConfigurationDto(
        weightageConfigurationDomain.deleteWeightageConfiguration(weightageConfigurationFromDB));
  }

  public List<WeightageCacheKeyDto> getAllWeightageCacheKeys(Integer limit)
      throws PromiseEngineException {
    List<WeightageConfiguration> weightageConfigurationList =
        weightageConfigurationDomain.getAllWeightageConfiguration(limit);

    return INSTANCE.convertToWeightageCacheKeyDtoList(weightageConfigurationList);
  }
}
