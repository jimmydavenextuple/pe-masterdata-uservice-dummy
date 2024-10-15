/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.service;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodeTimezonePersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimezoneService {
  private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneService.class);
  private static final PostalCodeTimezoneMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneMapper.class);
  private final PostalCodeTimezonePersistenceService postalCodeTimezonePersistenceService;
  private static final String TIMEZONE_EXCEPTION_MESSAGE = "Invalid Timezone Found";
  private static final String COUNTRY_EXCEPTION_MESSAGE = "Invalid Country Found";

  /**
   * Convert PostalCodeTimezone Entity to PostalCodeTimezone Dto with all required processing
   *
   * @param postalCodeTimezoneEntity PostalCodeTimezone Entity
   * @return PostalCodeTimezoneDto dto
   */
  private PostalCodeTimezoneDto preparePostalCodeTimezoneDto(
      PostalCodeTimezoneDomainDto postalCodeTimezoneEntity) {
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
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createPostalCodeTimezone service --");
    validateTimezoneAndCountry(baseRequest.getTimeZone(), baseRequest.getCountry());
    var postalCodeTimezoneEntity =
        INSTANCE.convertFromCreatePostalCodeTimezoneRequestToEntity(baseRequest);
    return preparePostalCodeTimezoneDto(
        postalCodeTimezonePersistenceService.savePostalCodeTimezone(postalCodeTimezoneEntity));
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
  @ReaderDS
  public PostalCodeTimezoneDto getPostalCodeTimezone(String orgId, String postalCodePrefix)
      throws PromiseEngineException {
    logger.debug("-- inside getPostalCodeTimezone service --");
    Optional<PostalCodeTimezoneDomainDto> promiseSourcingRule =
        Optional.ofNullable(
            postalCodeTimezonePersistenceService.getPostalCodeTimezone(orgId, postalCodePrefix));

    if (promiseSourcingRule.isEmpty()) {
      logger.error("-- Zip Code Timezone not found --");
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Zip Code Timezone not found!");
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
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside updatePostalCodeTimezone service --");
    validateTimezoneAndCountry(baseRequest.getTimeZone(), baseRequest.getCountry());
    var postalCodeTimezoneEntityFromDB =
        INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezone(orgId, postalCodePrefix));

    logger.info(
        "Response before updation of postal-code timezone :{}",
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntityFromDB));
    INSTANCE.insertValuesFromUpdatePostalCodeTimezoneRequestToEntity(
        baseRequest, postalCodeTimezoneEntityFromDB);

    return preparePostalCodeTimezoneDto(
        postalCodeTimezonePersistenceService.savePostalCodeTimezone(
            postalCodeTimezoneEntityFromDB));
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
        "Response before deletion of zip-code timezone :{}",
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntityFromDB));
    return preparePostalCodeTimezoneDto(
        postalCodeTimezonePersistenceService.deletePostalCodeTimezone(
            postalCodeTimezoneEntityFromDB));
  }

  @ReaderDS
  public List<PostalCodePrefixDto> fetchPostalCodePrefixList(String orgId)
      throws PromiseEngineException {
    List<PostalCodeTimezoneDomainDto> postalCodeTimezoneEntities =
        postalCodeTimezonePersistenceService.getPostalCodeTimezoneForOrgId(orgId);
    Set<String> visitedStates = new HashSet<>();
    return postalCodeTimezoneEntities.stream()
        .filter(pte -> !visitedStates.contains(pte.getState()))
        .map(pte -> this.postalCodePrefixDto(pte, postalCodeTimezoneEntities, visitedStates))
        .collect(Collectors.toList());
  }

  private PostalCodePrefixDto postalCodePrefixDto(
      PostalCodeTimezoneDomainDto postalCodeTimezoneEntity,
      List<PostalCodeTimezoneDomainDto> list,
      Set<String> visitedStates) {
    var postalCodePrefixDto = new PostalCodePrefixDto();
    List<String> postalCodePrefixList = new ArrayList<>();
    list.stream()
        .filter(pte -> pte.getState().equals(postalCodeTimezoneEntity.getState()))
        .forEach(pte -> postalCodePrefixList.add(pte.getZipCodePrefix()));
    postalCodePrefixDto.setState(postalCodeTimezoneEntity.getState());
    postalCodePrefixDto.setZipCodePrefix(postalCodePrefixList);
    visitedStates.add(postalCodeTimezoneEntity.getState());
    return postalCodePrefixDto;
  }

  private void validateTimezoneAndCountry(String timezone, String country)
      throws CommonServiceException {
    if (!DateTimeZone.getAvailableIDs().contains(timezone))
      throwCommonServiceException("timezone", timezone, TIMEZONE_EXCEPTION_MESSAGE);
    if (!Set.of(Locale.getISOCountries()).contains(country))
      throwCommonServiceException("country", country, COUNTRY_EXCEPTION_MESSAGE);
  }

  private void throwCommonServiceException(String field, String fieldValue, String errorMessage)
      throws CommonServiceException {
    logger.error(errorMessage);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }
}
