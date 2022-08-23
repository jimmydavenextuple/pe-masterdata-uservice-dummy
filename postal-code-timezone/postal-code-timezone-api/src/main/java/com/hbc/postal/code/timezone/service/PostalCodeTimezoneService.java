package com.hbc.postal.code.timezone.service;

import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    logger.debug("-- inside createPostalCodeTimezone service --");
    var postalCodeTimezoneEntity =
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
    logger.debug("-- inside getPostalCodeTimezone service --");
    Optional<PostalCodeTimezoneEntity> promiseSourcingRule =
        Optional.ofNullable(
            postalCodeTimezoneDomain.getPostalCodeTimezone(orgId, postalCodePrefix));

    if (promiseSourcingRule.isEmpty()) {
      logger.error("-- Postal Code Timezone not found --");
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
    logger.debug("-- inside updatePostalCodeTimezone service --");
    var postalCodeTimezoneEntityFromDB =
        INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezone(orgId, postalCodePrefix));

    logger.info(
        "Response before updation of postal-code timezone :{}",
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntityFromDB));
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
    logger.debug("-- inside deletePostalCodeTimezone service --");
    var postalCodeTimezoneEntityFromDB =
        INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezone(orgId, postalCodePrefix));
    logger.info(
        "Response before deletion of postal-code timezone :{}",
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntityFromDB));
    return preparePostalCodeTimezoneDto(
        postalCodeTimezoneDomain.deletePostalCodeTimezone(postalCodeTimezoneEntityFromDB));
  }

  public List<PostalCodePrefixDto> fetchPostalCodePrefixList(String orgId)
      throws PromiseEngineException {
    List<PostalCodeTimezoneEntity> postalCodeTimezoneEntities =
        postalCodeTimezoneDomain.getPostalCodeTimezoneForOrgId(orgId);
    Set<String> visitedStates = new HashSet<>();
    return postalCodeTimezoneEntities.stream()
        .filter(pte -> !visitedStates.contains(pte.getState()))
        .map(pte -> this.postalCodePrefixDto(pte, postalCodeTimezoneEntities, visitedStates))
        .collect(Collectors.toList());
  }

  private PostalCodePrefixDto postalCodePrefixDto(
      PostalCodeTimezoneEntity postalCodeTimezoneEntity,
      List<PostalCodeTimezoneEntity> list,
      Set<String> visitedStates) {
    var postalCodePrefixDto = new PostalCodePrefixDto();
    List<String> postalCodePrefixList = new ArrayList<>();
    list.stream()
        .filter(pte -> pte.getState().equals(postalCodeTimezoneEntity.getState()))
        .forEach(pte -> postalCodePrefixList.add(pte.getPostalCodePrefix()));
    postalCodePrefixDto.setState(postalCodeTimezoneEntity.getState());
    postalCodePrefixDto.setPostalCodePrefix(postalCodePrefixList);
    visitedStates.add(postalCodeTimezoneEntity.getState());
    return postalCodePrefixDto;
  }
}
