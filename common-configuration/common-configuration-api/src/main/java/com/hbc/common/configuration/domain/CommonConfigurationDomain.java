package com.hbc.common.configuration.domain;

import com.hbc.common.configuration.domain.entity.CommonConfiguration;
import com.hbc.common.configuration.repository.CommonConfigurationRepository;
import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.exception.PromiseEngineException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonConfigurationDomain {
  private static final Logger logger = LoggerFactory.getLogger(CommonConfigurationDomain.class);
  private final CommonConfigurationRepository configurationRepository;

  /**
   * Get Common Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @return Fetched Common Configuration
   * @throws PromiseEngineException Error while fetching common configuration
   */
  public Optional<CommonConfiguration> getCommonConfiguration(String orgId, String type, String key)
      throws PromiseEngineException {
    logger.debug("-- inside getCommonConfiguration  --");
    try {
      return configurationRepository.findByOrgIdAndTypeAndKey(orgId, type, key);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Common Configuration");
    }
  }

  /**
   * Save Common Configuration
   *
   * @param commonConfiguration Common Configuration
   * @return Saved Common Configuration
   * @throws PromiseEngineException Error while saving common configuration
   */
  public CommonConfiguration saveCommonConfiguration(CommonConfiguration commonConfiguration)
      throws PromiseEngineException {
    logger.debug("-- inside saveCommonConfiguration  --");
    try {
      return configurationRepository.save(commonConfiguration);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to save Common Configuration");
    }
  }

  /**
   * Delete Common Configuration
   *
   * @param commonConfiguration Common Configuration to be deleted
   * @throws PromiseEngineException Error while deleting common configuration
   */
  public void deleteCommonConfiguration(CommonConfiguration commonConfiguration)
      throws PromiseEngineException {
    logger.debug("-- inside deleteCommonConfiguration  --");
    try {
      configurationRepository.delete(commonConfiguration);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to delete Common Configuration");
    }
  }
}
