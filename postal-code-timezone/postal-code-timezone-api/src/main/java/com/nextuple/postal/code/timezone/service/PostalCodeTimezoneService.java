package com.nextuple.postal.code.timezone.service;

import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import com.nextuple.postal.code.timezone.exception.common.ApplicationLayer;
import com.nextuple.postal.code.timezone.exception.common.ExceptionCodeMapping;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimezoneService {
  private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneService.class);
  private static final PostalCodeTimezoneMapper INSTANCE =
          Mappers.getMapper(PostalCodeTimezoneMapper.class);
  private final PostalCodeTimezoneDomain postalCodeTimezoneDomain;

  /**
   * Convert PostalCodeTimezone Entity to PostalCodeTimezone Dto with all required processing
   *
   * @param postalCodeTimezoneEntity PostalCodeTimezone Entity
   * @return PostalCodeTimezoneDto dto
   */
  private PostalCodeTimezoneDto preparePostalCodeTimezoneDto(
          PostalCodeTimezoneEntity postalCodeTimezoneEntity) {
    return INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntity);
  }

  /**
   * Creates new PostalCodeTimezone entity
   *
   * @param baseRequest for Creating Postal Code Timezone
   * @return Created Postal Code Timezone dto
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneDto createPostalCodeTimezone(CreatePostalCodeTimezoneRequest baseRequest)
          throws PromiseEngineException {
    logger.info("-- inside createPostalCodeTimezone service --");
    PostalCodeTimezoneEntity postalCodeTimezoneEntity =
            INSTANCE.convertFromCreatePostalCodeTimezoneRequestToEntity(baseRequest);
    return preparePostalCodeTimezoneDto(
            postalCodeTimezoneDomain.savePostalCodeTimezone(postalCodeTimezoneEntity));
  }

  /**
   * Get Postal Code Timezone
   *
   * @param orgId organisation ID using which service will look for Postal Code Timezone
   * @param postalCodePrefix Postal Code Prefix using which service will look for Postal Code
   *     Timezone
   * @return Fetched Postal Code Timezone dto
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneDto getPostalCodeTimezone(String orgId, String postalCodePrefix)
          throws PromiseEngineException {
    logger.info("-- inside getPostalCodeTimezone service --");
    Optional<PostalCodeTimezoneEntity> promiseSourcingRule =
            Optional.ofNullable(
                    postalCodeTimezoneDomain.getPostalCodeTimezone(orgId, postalCodePrefix));

    if (promiseSourcingRule.isEmpty()) {
      logger.info("-- Postal Code Timezone not found --");
      throw new PromiseEngineException(
              ApplicationLayer.SERVICE_LAYER,
              ExceptionCodeMapping.SERVICE_FIND_FAILED,
              "Postal Code Timezone not found!");
    }

    return preparePostalCodeTimezoneDto(promiseSourcingRule.get());
  }

  /**
   * Update Postal Code Timezone
   *
   * @param orgId organisation ID using which service will look for Postal Code Timezone
   * @param postalCodePrefix Postal Code Prefix using which service will look for Postal Code
   *     Timezone
   * @param baseRequest for Updating Postal Code Timezone
   * @return Updated Postal Code Timezone dto
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneDto updatePostalCodeTimezone(
          String orgId, String postalCodePrefix, UpdatePostalCodeTimezoneRequest baseRequest)
          throws PromiseEngineException {
    logger.info("-- inside updatePostalCodeTimezone service --");
    PostalCodeTimezoneEntity postalCodeTimezoneEntityFromDB =
            INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezone(orgId, postalCodePrefix));

    INSTANCE.insertValuesFromUpdatePostalCodeTimezoneRequestToEntity(
            baseRequest, postalCodeTimezoneEntityFromDB);

    return preparePostalCodeTimezoneDto(
            postalCodeTimezoneDomain.savePostalCodeTimezone(postalCodeTimezoneEntityFromDB));
  }

  /**
   * Delete Postal Code Timezone
   *
   * @param orgId organisation ID using which service will look for Postal Code Timezone
   * @param postalCodePrefix Postal Code Prefix using which service will look for Postal Code
   *     Timezone
   * @return Deleted Postal Code Timezone dto
   * @throws PromiseEngineException
   */
  public PostalCodeTimezoneDto deletePostalCodeTimezone(String orgId, String postalCodePrefix)
          throws PromiseEngineException {
    logger.info("-- inside deletePostalCodeTimezone service --");
    PostalCodeTimezoneEntity postalCodeTimezoneEntityFromDB =
            INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezone(orgId, postalCodePrefix));
    return preparePostalCodeTimezoneDto(
            postalCodeTimezoneDomain.deletePostalCodeTimezone(postalCodeTimezoneEntityFromDB));
  }
}
