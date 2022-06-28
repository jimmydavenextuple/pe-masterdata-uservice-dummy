package com.hbc.postal.code.timezone.domain;

import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.domain.repository.PostalCodeTimezoneRepository;
import com.hbc.postal.code.timezone.exception.common.ApplicationLayer;
import com.hbc.postal.code.timezone.exception.common.ExceptionCodeMapping;
import com.hbc.postal.code.timezone.exception.common.PromiseEngineException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimezoneDomain {
  private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneDomain.class);
  private final PostalCodeTimezoneRepository postalCodeTimezoneRepository;
  /**
   * Save Postal Code Timezone
   *
   * @param postalCodeTimezoneEntity Entity to be saved
   * @return Saved Postal Code Timezone
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneEntity savePostalCodeTimezone(
      PostalCodeTimezoneEntity postalCodeTimezoneEntity) throws PromiseEngineException {
    logger.info("-- inside savePostalCodeTimezone domain --");
    try {
      return postalCodeTimezoneRepository.save(postalCodeTimezoneEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save Postal Code Timezone!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save Postal Code Timezone!");
    }
  }

  /**
   * Get Postal Code Timezone
   *
   * @param orgId organisation ID using which service will look for Postal Code Timezone
   * @param postalCodePrefix Postal Code Prefix using which service will look for Postal Code
   *     Timezone
   * @return Fetched Postal Code Timezone
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneEntity getPostalCodeTimezone(String orgId, String postalCodePrefix)
      throws PromiseEngineException {
    logger.info("-- inside getPostalCodeTimezone domain --");
    try {
      return postalCodeTimezoneRepository.findByOrgIdAndPostalCodePrefix(orgId, postalCodePrefix);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Postal Code Timezone not found!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Postal Code Timezone not found!");
    }
  }

  /**
   * Delete Postal Code Timezone
   *
   * @param postalCodeTimezoneEntity Postal Code Timezone
   * @return Deleted Postal Code Timezone
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneEntity deletePostalCodeTimezone(
      PostalCodeTimezoneEntity postalCodeTimezoneEntity) throws PromiseEngineException {
    logger.info("-- inside deletePostalCodeTimezone domain --");
    try {
      postalCodeTimezoneRepository.delete(postalCodeTimezoneEntity);
      return postalCodeTimezoneEntity;
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete Postal Code Timezone!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Postal Code Timezone!");
    }
  }
}
