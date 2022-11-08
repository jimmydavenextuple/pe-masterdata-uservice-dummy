package com.nextuple.weightage.configuration.domain;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.domain.entity.WeightageConfiguration;
import com.nextuple.weightage.configuration.domain.repository.WeightageConfigurationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeightageConfigurationDomain {
  private static final Logger logger = LoggerFactory.getLogger(WeightageConfigurationDomain.class);
  private final WeightageConfigurationRepository weightageConfigurationRepository;

  /**
   * Fetch Weightage
   *
   * @param baseRequest Fetch Weightage Request
   * @return List of WeightageConfiguration
   * @throws PromiseEngineException
   */
  public List<WeightageConfiguration> fetchWeightage(FetchWeightageRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("-- inside fetchWeightage domain --");
    try {
      return weightageConfigurationRepository.findByKeyInAndOrgIdAndType(
          baseRequest.getKeys(), baseRequest.getOrgId(), baseRequest.getType());
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
   * @param weightageConfiguration Entity to be saved
   * @return Saved Weightage Configuration
   * @throws PromiseEngineException
   */
  public WeightageConfiguration saveWeightageConfiguration(
      WeightageConfiguration weightageConfiguration) throws PromiseEngineException {
    logger.debug("-- inside saveWeightageConfiguration domain --");
    try {
      return weightageConfigurationRepository.save(weightageConfiguration);
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
   * @return Fetched Weightage Configuration
   * @throws PromiseEngineException
   */
  public WeightageConfiguration getWeightageConfiguration(String orgId, String type, String key)
      throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfiguration domain --");
    try {
      return weightageConfigurationRepository.findByOrgIdAndTypeAndKey(orgId, type, key);
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
   * @return Fetched Weightage Configuration
   * @throws PromiseEngineException
   */
  public List<WeightageConfiguration> getWeightageConfigurationsByKey(String key)
      throws PromiseEngineException {
    logger.debug("-- inside getWeightageConfigurationByKey domain --");
    try {
      return weightageConfigurationRepository.findByKey(key);
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
   * @param weightageConfiguration Weightage Configuration Entity to be deleted
   * @return Deleted Weightage Configuration
   * @throws PromiseEngineException
   */
  public WeightageConfiguration deleteWeightageConfiguration(
      WeightageConfiguration weightageConfiguration) throws PromiseEngineException {
    logger.debug("-- inside deleteWeightageConfiguration domain --");
    try {
      weightageConfigurationRepository.delete(weightageConfiguration);
      return weightageConfiguration;
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Weightage Configuration");
    }
  }

  public List<WeightageConfiguration> getAllWeightageConfiguration(Integer limit)
      throws PromiseEngineException {
    try {
      return weightageConfigurationRepository.findAllRecords(limit);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to fetch all Weightage Configuration records");
    }
  }
}
