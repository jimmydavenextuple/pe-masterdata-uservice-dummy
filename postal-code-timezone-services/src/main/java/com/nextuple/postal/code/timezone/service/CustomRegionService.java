/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.enums.ErrorCodesEnum;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.domain.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CustomRegionService {

  private static final Logger logger = LoggerFactory.getLogger(CustomRegionService.class);
  private final CustomRegionPersistenceService customRegionPersistenceService;
  private final PostalCodePersistenceService postalCodePersistenceService;
  private static final String CUSTOM_REGION_NOT_FOUND = "Custom Region not found";
  private static final String ZIP_CODE_ASSOCIATION_NOT_FOUND =
      "No Zip Code association found for the given codes";
  private static final String ZIP_CODE_NOT_FOUND = "Zip Codes cannot be blank";
  private static final String CUSTOM_REGION_EXISTS = "Custom Region already exists";
  private static final String CUSTOM_REGION_NAME_EXISTS = "Custom Region Name already exists";
  private static final String CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE =
      "Custom Region already exists for the given code";
  private static final String ID = "id";
  private static final String ORG_ID = "org_id";
  private static final String CUSTOM_REGION_NAME = "custom_region_name";
  private static final String CODES = "codes";
  private static final String UNAVAILABLE_CODES = "unavailable_codes";
  private static final String BLANK_CODES = "blank_codes";
  private static final CustomRegionMapper INSTANCE = Mappers.getMapper(CustomRegionMapper.class);
  private static final String SORT_ORDER = "sortOrder";

  public CustomRegionResponse createCustomRegion(CustomRegionRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    logger.debug("--inside createCustomRegion service--");
    validateCustomRegionRequestNotExist(baseRequest);
    String orgId = baseRequest.getOrgId();
    String customRegionName = baseRequest.getCustomRegionName();
    validateCustomRegionNameExist(baseRequest, orgId, customRegionName);
    List<String> codes = baseRequest.getCodes();
    Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap = validateCodes(codes, orgId);
    validateBlankCodes(codes);
    var customRegionRequestDetails = INSTANCE.toCustomRegionEntity(baseRequest);
    addCustomRegionToZipCodeDetails(codes, postalCodeEntityMap, baseRequest.getId());
    return INSTANCE.toCustomRegionResponse(
        customRegionPersistenceService.saveCustomRegion(customRegionRequestDetails));
  }

  private void addCustomRegionToZipCodeDetails(
      List<String> codes, Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap, String id)
      throws CommonServiceException {
    for (String code : codes) {
      List<PostalCodeDomainDto> postalCodeEntityList = postalCodeEntityMap.get(code);
      for (PostalCodeDomainDto postalCodeEntity : postalCodeEntityList) {
        if (!ObjectUtils.isEmpty(postalCodeEntity.getCustomRegion())) {
          logger.error(CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE);
          Map<String, FieldError> errorMap = new HashMap<>();
          errorMap.put("CODE", FieldError.builder().rejectedValue(code).build());
          throw new CommonServiceException(
              CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE,
              HttpStatus.BAD_REQUEST,
              ErrorCodesEnum.CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE.getErrorCode(),
              errorMap);
        }
        postalCodeEntity.setCustomRegion(id);
      }
    }
  }

  private void validateCustomRegionNameExist(
      CustomRegionRequest baseRequest, String orgId, String customRegionName)
      throws CommonServiceException {
    Optional<CustomRegionDomainDto> customRegion =
        customRegionPersistenceService.fetchRegionByOrgIdAndCustomRegionName(
            orgId, customRegionName);
    if (customRegion.isPresent()) {
      logger.error(CUSTOM_REGION_NAME_EXISTS);
      Map<String, FieldError> errorMap = new HashMap<>();

      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(baseRequest.getOrgId()).build());
      errorMap.put(
          CUSTOM_REGION_NAME,
          FieldError.builder().rejectedValue(baseRequest.getCustomRegionName()).build());
      throw new CommonServiceException(
          CUSTOM_REGION_NAME_EXISTS,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.CUSTOM_REGION_NAME_EXISTS.getErrorCode(),
          errorMap);
    }
  }

  public CustomRegionResponse updateCustomRegion(CustomRegionRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    logger.debug("--inside updateCustomRegion service--");
    var existingCustomRegionEntity =
        validateCustomRegionRequestExist(baseRequest.getOrgId(), baseRequest.getId());
    String orgId = baseRequest.getOrgId();
    List<String> codes = baseRequest.getCodes();
    List<String> existingCodesList = existingCustomRegionEntity.getCodes();
    List<String> codesForUpdate =
        codes.stream()
            .filter(code -> !existingCodesList.contains(code))
            .collect(Collectors.toList());
    List<String> codesForDelete =
        existingCodesList.stream()
            .filter(existingCode -> !codes.contains(existingCode))
            .collect(Collectors.toList());
    var customRegionRequestDetails = INSTANCE.toCustomRegionEntity(baseRequest);
    Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap =
        validateCodes(codesForUpdate, orgId);
    validateBlankCodes(codes);
    if (!codesForUpdate.isEmpty()) {
      updatePostalCodeDetails(codesForUpdate, postalCodeEntityMap, baseRequest.getId());
    }
    removeCustomRegionIdFromPostalCodes(codesForDelete, orgId);
    return INSTANCE.toCustomRegionResponse(
        customRegionPersistenceService.saveCustomRegion(customRegionRequestDetails));
  }

  private static void validateBlankCodes(List<String> codes) throws CommonServiceException {
    if (codes.isEmpty()) {
      logger.error(ZIP_CODE_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(CODES, FieldError.builder().rejectedValue(BLANK_CODES).build());
      throw new CommonServiceException(
          ZIP_CODE_NOT_FOUND,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.ZIP_CODE_NOT_FOUND.getErrorCode(),
          errorMap);
    }
  }

  public CustomRegionResponse deleteCustomRegion(String orgId, String id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("--inside deleteCustomRegion service--");
    var customRegion = validateCustomRegionRequestExist(orgId, id);
    List<String> codes = customRegion.getCodes();
    removeCustomRegionIdFromPostalCodes(codes, orgId);
    customRegionPersistenceService.deleteCustomRegion(customRegion);
    return INSTANCE.toCustomRegionResponse(customRegion);
  }

  private void removeCustomRegionIdFromPostalCodes(List<String> codes, String orgId)
      throws PromiseEngineException {
    for (String postalCodePrefix : codes) {
      List<PostalCodeDomainDto> postalCodeEntityList =
          postalCodePersistenceService.fetchPostalCodeList(orgId, postalCodePrefix);
      for (PostalCodeDomainDto postalCodeEntity : postalCodeEntityList) {
        removeIdFromPostalCodeEntity(postalCodeEntity);
      }
    }
  }

  private void removeIdFromPostalCodeEntity(PostalCodeDomainDto postalCodeEntity)
      throws PromiseEngineException {
    postalCodeEntity.setCustomRegion(null);
    postalCodePersistenceService.savePostalCode(postalCodeEntity);
  }

  private void updatePostalCodeDetails(
      List<String> codes, Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap, String id)
      throws PromiseEngineException {
    for (String code : codes) {
      List<PostalCodeDomainDto> postalCodeEntityList = postalCodeEntityMap.get(code);
      for (PostalCodeDomainDto postalCodeEntity : postalCodeEntityList) {
        postalCodeEntity.setCustomRegion(id);
        postalCodePersistenceService.savePostalCode(postalCodeEntity);
      }
    }
  }

  private Map<String, List<PostalCodeDomainDto>> validateCodes(List<String> codes, String orgId)
      throws CommonServiceException, PromiseEngineException {
    Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap = new HashMap<>();
    List<String> unavailableCodes = new ArrayList<>();
    processPartialCustomRegions(codes, orgId, postalCodeEntityMap, unavailableCodes);
    if (!unavailableCodes.isEmpty()) {
      logger.error(ZIP_CODE_ASSOCIATION_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          UNAVAILABLE_CODES,
          FieldError.builder().rejectedValue(unavailableCodes.toString()).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          ZIP_CODE_ASSOCIATION_NOT_FOUND,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.ZIP_CODE_ASSOCIATION_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    return postalCodeEntityMap;
  }

  private void processPartialCustomRegions(
      List<String> codes,
      String orgId,
      Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap,
      List<String> unavailableCodes)
      throws PromiseEngineException {
    for (String postalCodePrefix : codes) {
      List<PostalCodeDomainDto> postalCodeEntityListByPrefix =
          postalCodePersistenceService.fetchPostalCodeList(orgId, postalCodePrefix);
      if (!postalCodeEntityListByPrefix.isEmpty()) {
        postalCodeEntityMap.put(postalCodePrefix, postalCodeEntityListByPrefix);
      } else {
        unavailableCodes.add(postalCodePrefix);
      }
    }
  }

  private void validateCustomRegionRequestNotExist(CustomRegionRequest baseRequest)
      throws CommonServiceException {
    Optional<CustomRegionDomainDto> customRegion =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(
            baseRequest.getOrgId(), baseRequest.getId());
    if (customRegion.isPresent()) {
      logger.error(CUSTOM_REGION_EXISTS);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(baseRequest.getId()).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(baseRequest.getOrgId()).build());
      throw new CommonServiceException(
          CUSTOM_REGION_EXISTS,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.CUSTOM_REGION_EXISTS.getErrorCode(),
          errorMap);
    }
  }

  private CustomRegionDomainDto validateCustomRegionRequestExist(String orgId, String id)
      throws CommonServiceException {
    Optional<CustomRegionDomainDto> customRegion =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(orgId, id);
    if (customRegion.isEmpty()) {
      logger.error(CUSTOM_REGION_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          CUSTOM_REGION_NOT_FOUND,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.CUSTOM_REGION_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    return customRegion.get();
  }

  public CustomRegionResponse fetchRegionByOrgIdAndId(String orgId, String id)
      throws CommonServiceException {
    logger.debug("--inside fetchRegionByOrgIdAndId service");
    Optional<CustomRegionDomainDto> customRegion =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(orgId, id);
    if (customRegion.isEmpty()) {
      logger.error(CUSTOM_REGION_NOT_FOUND);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(id, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          CUSTOM_REGION_NOT_FOUND,
          HttpStatus.NOT_FOUND,
          ErrorCodesEnum.CUSTOM_REGION_NOT_FOUND.getErrorCode(),
          errorMap);
    }
    return INSTANCE.toCustomRegionResponse(customRegion.get());
  }

  @ReaderDS
  public Page<CustomRegionDto> getCustomRegionListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws PromiseEngineException, CommonServiceException {
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      Page<CustomRegionDomainDto> customRegionDomainDtos =
          customRegionPersistenceService.getCustomRegionListByOrgId(
              orgId, pageNo, pageSize, sortBy, sortOrder);
      return customRegionDomainDtos.map(INSTANCE::toCustomRegionDto);
    } else {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }
}
