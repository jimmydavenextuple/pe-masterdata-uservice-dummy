/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.enums.ErrorCodesEnum;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.domain.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeMapper;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class PostalCodeService {

  private static final Logger logger = LoggerFactory.getLogger(PostalCodeService.class);
  private static final String ZIP_CODE = "zip_code";
  private static final String ZIP_CODE_PREFIX = "zip_code_prefix";
  private static final String ZIP_CODE_EXCEPTION_MESSAGE = "Zip Code not found";
  private static final String CUSTOM_REGION_FOR_GIVEN_ZIP_CODE_MESSAGE =
      "Invalid Custom Region / No Custom Region found for given zip code";
  private static final String ZIP_CODE_LIST_EXCEPTION_MESSAGE =
      "Zip Codes not found for given orgId and Zip Code Prefix";
  private static final String ZIP_CODE_EXISTS_EXCEPTION_MESSAGE = "Zip Code already exists";
  private static final PostalCodeMapper INSTANCE = Mappers.getMapper(PostalCodeMapper.class);
  private static final CustomRegionMapper CUSTOM_REGION_MAPPER_INSTANCE =
      Mappers.getMapper(CustomRegionMapper.class);
  private final PostalCodePersistenceService postalCodePersistenceService;
  private final CustomRegionPersistenceService customRegionPersistenceService;

  public PostalCodeResponse upsertPostalCode(PostalCodeRequest postalCodeRequest)
      throws PromiseEngineException, CommonServiceException {
    Optional<PostalCodeDomainDto> existingPostalCode =
        postalCodePersistenceService.fetchPostalCode(
            postalCodeRequest.getOrgId(), postalCodeRequest.getZipCode());
    if (existingPostalCode.isPresent()) {
      return updatePostalCode(postalCodeRequest);
    } else {
      return createPostalCode(postalCodeRequest);
    }
  }

  public PostalCodeResponse createPostalCode(PostalCodeRequest postalCodeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createPostalCode service --");
    Optional<PostalCodeDomainDto> fetchPostalCode =
        postalCodePersistenceService.fetchPostalCode(
            postalCodeRequest.getOrgId(), postalCodeRequest.getZipCode());
    if (fetchPostalCode.isPresent()) {
      logger.error(ZIP_CODE_EXISTS_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ZIP_CODE, FieldError.builder().rejectedValue(postalCodeRequest.getZipCode()).build());
      throw new CommonServiceException(
          ZIP_CODE_EXISTS_EXCEPTION_MESSAGE,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.ZIP_CODE_EXISTS.getErrorCode(),
          errorMap);
    }
    var postalCodeEntity = INSTANCE.convertToPostalCodeEntity(postalCodeRequest);
    return INSTANCE.convertToPostalCodeResponse(
        postalCodePersistenceService.savePostalCode(postalCodeEntity));
  }

  public PostalCodeResponse updatePostalCode(PostalCodeRequest postalCodeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside updatePostalCodeDetails service --");
    Optional<PostalCodeDomainDto> fetchPostalCode =
        postalCodePersistenceService.fetchPostalCode(
            postalCodeRequest.getOrgId(), postalCodeRequest.getZipCode());
    if (fetchPostalCode.isEmpty()) {
      logger.error(ZIP_CODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ZIP_CODE, FieldError.builder().rejectedValue(postalCodeRequest.getZipCode()).build());
      throw new CommonServiceException(
          ZIP_CODE_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    INSTANCE.updatePostalCodeEntity(postalCodeRequest, fetchPostalCode.get());
    return INSTANCE.convertToPostalCodeResponse(
        postalCodePersistenceService.savePostalCode(fetchPostalCode.get()));
  }

  public PostalCodeResponse fetchPostalCode(String orgId, String postalCode)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchPostalCode service --");
    Optional<PostalCodeDomainDto> postalCodeEntity =
        postalCodePersistenceService.fetchPostalCode(orgId, postalCode);
    if (postalCodeEntity.isEmpty()) {
      logger.error(ZIP_CODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ZIP_CODE, FieldError.builder().rejectedValue(postalCode).build());
      throw new CommonServiceException(
          ZIP_CODE_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    return INSTANCE.convertToPostalCodeResponse(postalCodeEntity.get());
  }

  public PostalCodeResponse processRemovePostalCode(String orgId, String postalCode)
      throws PromiseEngineException, CommonServiceException {
    Optional<PostalCodeDomainDto> postalCodeEntity =
        postalCodePersistenceService.fetchPostalCode(orgId, postalCode);
    if (postalCodeEntity.isEmpty()) {
      logger.error(ZIP_CODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ZIP_CODE, FieldError.builder().rejectedValue(postalCode).build());
      throw new CommonServiceException(
          ZIP_CODE_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    var postalCodeResponsePojo = INSTANCE.convertToPostalCodeResponse(postalCodeEntity.get());
    postalCodePersistenceService.deletePostalCodeEntity(postalCodeEntity.get());
    return postalCodeResponsePojo;
  }

  public List<PostalCodeResponse> fetchByPostalCodePrefix(String orgId, String postalCodePrefix)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside fetchByPostalCodePrefix service --");
    List<PostalCodeDomainDto> postalCodeEntityList =
        postalCodePersistenceService.fetchPostalCodeList(orgId, postalCodePrefix);
    if (postalCodeEntityList.isEmpty()) {
      logger.error(ZIP_CODE_LIST_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ZIP_CODE_PREFIX, FieldError.builder().rejectedValue(postalCodePrefix).build());
      throw new CommonServiceException(
          ZIP_CODE_LIST_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    return INSTANCE.convertToPostalCodeResponseList(postalCodeEntityList);
  }

  public CustomRegionResponse fetchCustomRegionIdByPostalCode(String orgId, String postalCode)
      throws PromiseEngineException, CommonServiceException {
    Optional<PostalCodeDomainDto> postalCodeEntity =
        postalCodePersistenceService.fetchPostalCode(orgId, postalCode);
    if (postalCodeEntity.isEmpty()) {
      logger.error(ZIP_CODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ZIP_CODE, FieldError.builder().rejectedValue(postalCode).build());
      throw new CommonServiceException(
          ZIP_CODE_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    String customRegion = postalCodeEntity.get().getCustomRegion();
    Optional<CustomRegionDomainDto> customRegionEntity =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(orgId, customRegion);

    if (ObjectUtils.isEmpty(customRegion) || customRegionEntity.isEmpty()) {
      logger.error(CUSTOM_REGION_FOR_GIVEN_ZIP_CODE_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ZIP_CODE, FieldError.builder().rejectedValue(postalCode).build());
      throw new CommonServiceException(
          CUSTOM_REGION_FOR_GIVEN_ZIP_CODE_MESSAGE,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.CUSTOM_REGION_FOR_GIVEN_ZIP_CODE_MESSAGE.getErrorCode(),
          errorMap);
    }
    return CUSTOM_REGION_MAPPER_INSTANCE.toCustomRegionResponse(customRegionEntity.get());
  }

  @ReaderDS
  public List<PostalCodeResponse> fetchPostalCodeTimezoneByOrgIdAndCountry(
      String orgId, String country) throws PromiseEngineException {
    logger.debug("-- inside getPostalCodeTimezone service --");
    List<PostalCodeDomainDto> postalCodeTimezoneEntities =
        postalCodePersistenceService.getPostCodeTimeZoneByOrgIdAndCountry(orgId, country);

    return postalCodeTimezoneEntities.stream()
        .map(this::preparePostalCodeResponse)
        .collect(Collectors.toList());
  }

  private PostalCodeResponse preparePostalCodeResponse(PostalCodeDomainDto postalCodeEntity) {
    return INSTANCE.convertToPostalCodeResponse(postalCodeEntity);
  }

  @ReaderDS
  public List<MarketRegionInfo> getMarketRegionForOrgId(String orgId)
      throws PromiseEngineException {
    logger.debug("-- Inside get market region for orgId : {}", orgId);

    List<MarketRegionProjection> records = postalCodePersistenceService.getRecordsForOrgId(orgId);

    if (Objects.isNull(records) || records.isEmpty()) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "No Market Regions Found");
    }
    return INSTANCE.convertToMarketRegionInfo(records);
  }

  @ReaderDS
  public List<String> fetchPostalCodePrefixForOrgIdAndState(String orgId, String state)
      throws PromiseEngineException {
    logger.debug("-- Inside fetch postal_code_prefix for orgId: {} and state: {} --", orgId, state);
    return postalCodePersistenceService.getPostalCodePrefixForOrgIdAndState(orgId, state);
  }

  @ReaderDS
  public List<PostalCodePrefixDto> getPostalCodePrefixList(String orgId)
      throws PromiseEngineException {
    List<PostalCodeDomainDto> postalCodeEntities =
        postalCodePersistenceService.getPostalCodeForOrgId(orgId);
    Set<String> visitedStates = new HashSet<>();
    return postalCodeEntities.stream()
        .filter(pte -> !visitedStates.contains(pte.getState()))
        .map(pte -> this.postalCodePrefixDto(pte, postalCodeEntities, visitedStates))
        .collect(Collectors.toList());
  }

  private PostalCodePrefixDto postalCodePrefixDto(
      PostalCodeDomainDto postalCodeEntity,
      List<PostalCodeDomainDto> list,
      Set<String> visitedStates) {
    var postalCodePrefixDto = new PostalCodePrefixDto();
    List<String> postalCodePrefixList = new ArrayList<>();
    list.stream()
        .filter(pte -> pte.getState().equals(postalCodeEntity.getState()))
        .forEach(pte -> postalCodePrefixList.add(pte.getZipCodePrefix()));
    postalCodePrefixDto.setState(postalCodeEntity.getState());
    postalCodePrefixDto.setZipCodePrefix(postalCodePrefixList);
    visitedStates.add(postalCodeEntity.getState());
    return postalCodePrefixDto;
  }
}
