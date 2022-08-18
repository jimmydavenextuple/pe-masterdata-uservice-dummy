package com.hbc.global.configuration.domain;

import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import com.hbc.global.configuration.repository.GlobalConfigurationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalConfigurationDomain {
  private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationDomain.class);
  private final GlobalConfigurationRepository globalConfigurationRepository;

  /**
   * Get Global Configuration
   *
   * @param orgId Organisation Id
   * @param type Type
   * @param key Key
   * @return Fetched Weightage Configuration
   * @throws PromiseEngineException
   */
  public Optional<GlobalConfiguration> getGlobalConfiguration(String orgId, String type, String key)
      throws PromiseEngineException {
    logger.debug("-- inside getGlobalConfiguration  --");
    try {
      return globalConfigurationRepository.findByOrgIdAndTypeAndKey(orgId, type, key);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Global Configuration");
    }
  }

  public List<GlobalConfiguration> findByKeyInAndOrgIdAndType(
      List<String> keys, String orgId, String type) throws PromiseEngineException {
    logger.debug("-- inside findByKeyInAndOrgIdAndType  --");
    try {
      return globalConfigurationRepository.findByKeyInAndOrgIdAndType(keys, orgId, type);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Global Configurations");
    }
  }

  /** Save Global Configuration */
  public GlobalConfiguration saveGlobalConfiguration(GlobalConfiguration globalConfiguration)
      throws PromiseEngineException {
    logger.debug("-- inside saveGlobalConfiguration  --");
    try {
      return globalConfigurationRepository.save(globalConfiguration);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to save Global Configuration");
    }
  }

  /** Save Global Configuration */
  public void deleteGlobalConfiguration(GlobalConfiguration globalConfiguration)
      throws PromiseEngineException {
    logger.debug("-- inside saveGlobalConfiguration  --");
    try {
      globalConfigurationRepository.delete(globalConfiguration);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to delete Global Configuration");
    }
  }
}
