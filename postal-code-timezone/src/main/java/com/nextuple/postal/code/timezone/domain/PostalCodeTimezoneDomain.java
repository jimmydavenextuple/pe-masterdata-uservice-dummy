package com.nextuple.postal.code.timezone.domain;

import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.repository.PostalCodeTimezoneRepository;
import com.nextuple.postal.code.timezone.exception.common.ApplicationLayer;
import com.nextuple.postal.code.timezone.exception.common.ExceptionCodeMapping;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
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
}
