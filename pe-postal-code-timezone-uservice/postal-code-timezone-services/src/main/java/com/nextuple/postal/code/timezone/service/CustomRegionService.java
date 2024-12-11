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
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.enums.ErrorCodesEnum;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.DeleteCustomRegionGeozonesRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.projection.CustomRegionProjection;
import com.nextuple.postal.code.timezone.domain.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  public static final String ZIP_PREFIX_MULTIPLE_COUNTRIES =
      "Zip code prefix associated with multiple countries";
  private static final String ZIP_PREFIX_NOT_FOUND_CUSTOM_REGION =
      "Zip code prefixes are not part of custom region";
  private static final String ZIP_CODE_NOT_FOUND = "Zip Codes cannot be blank";
  private static final String CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE =
      "Custom Region already exists for the given code";
  private static final String ID = "id";
  private static final String ORG_ID = "org_id";
  public static final String COUNTRY = "country";
  private static final String CODES = "codes";
  private static final String UNAVAILABLE_CODES = "unavailable_codes";
  public static final String REGION_ID = "region_id";
  private static final String BLANK_CODES = "blank_codes";
  private static final CustomRegionMapper INSTANCE = Mappers.getMapper(CustomRegionMapper.class);
  private static final String SORT_ORDER = "sortOrder";

  @Transactional
  public CustomRegionResponse createCustomRegion(CustomRegionRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    logger.debug("--inside createCustomRegion service--");
    String orgId = baseRequest.getOrgId();
    String customRegionId = baseRequest.getId();
    List<String> codes = baseRequest.getCodes();
    Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap = validateCodes(codes, orgId);
    validateBlankCodes(codes);
    addCustomRegionToZipCodeDetails(codes, postalCodeEntityMap, customRegionId);
    Optional<CustomRegionDomainDto> existingCustomRegionDtoOptional =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(orgId, customRegionId);
    CustomRegionDomainDto customRegionDomainDto =
        existingCustomRegionDtoOptional.isPresent()
            ? updateExistingCustomRegion(baseRequest, existingCustomRegionDtoOptional.get())
            : createNewCustomRegion(baseRequest);
    return INSTANCE.toCustomRegionResponse(
        customRegionPersistenceService.saveCustomRegion(customRegionDomainDto));
  }

  private void validateCodesAlreadyHasCustomRegions(
      List<String> codes, Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap)
      throws CommonServiceException {
    for (String code : codes) {
      List<PostalCodeDomainDto> postalCodeEntityList = postalCodeEntityMap.get(code);
      for (PostalCodeDomainDto postalCodeDomainDto : postalCodeEntityList) {
        if (Objects.nonNull(postalCodeDomainDto.getCustomRegion())) {
          logger.error(CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE);
          Map<String, FieldError> errorMap = new HashMap<>();
          errorMap.put("CODE", FieldError.builder().rejectedValue(code).build());
          throw new CommonServiceException(
              CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE,
              HttpStatus.BAD_REQUEST,
              ErrorCodesEnum.CUSTOM_REGION_ALREADY_EXISTS_FOR_GIVEN_CODE.getErrorCode(),
              errorMap);
        }
      }
    }
  }

  private CustomRegionDomainDto updateExistingCustomRegion(
      CustomRegionRequest baseRequest, CustomRegionDomainDto existingCustomRegionDomainDto) {
    List<String> updatedGeozonePrefixes =
        checkDuplicateGeozonePrefixes(
            existingCustomRegionDomainDto.getCodes(), baseRequest.getCodes());
    CustomRegionDomainDto customRegionDomainDto = createNewCustomRegion(baseRequest);
    customRegionDomainDto.setCodes(updatedGeozonePrefixes);
    return customRegionDomainDto;
  }

  private CustomRegionDomainDto createNewCustomRegion(CustomRegionRequest baseRequest) {
    return INSTANCE.toCustomRegionEntity(baseRequest);
  }

  private List<String> checkDuplicateGeozonePrefixes(
      List<String> existingCodes, List<String> newCodes) {
    return Stream.of(existingCodes, newCodes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet())
        .stream()
        .toList();
  }

  private void addCustomRegionToZipCodeDetails(
      List<String> codes, Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap, String id)
      throws PromiseEngineException, CommonServiceException {
    validateCodesAlreadyHasCustomRegions(codes, postalCodeEntityMap);
    for (String code : codes) {
      List<PostalCodeDomainDto> postalCodeEntityList = postalCodeEntityMap.get(code);
      for (PostalCodeDomainDto postalCodeEntity : postalCodeEntityList) {
        postalCodeEntity.setCustomRegion(id);
        postalCodePersistenceService.savePostalCode(postalCodeEntity);
      }
    }
  }

  @Transactional
  public CustomRegionResponse updateCustomRegion(CustomRegionRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {

    logger.debug("--inside updateCustomRegion service--");
    var existingCustomRegionEntity =
        validateCustomRegionRequestExist(baseRequest.getOrgId(), baseRequest.getId());
    String orgId = baseRequest.getOrgId();
    List<String> codes = baseRequest.getCodes();
    validateBlankCodes(codes);
    List<String> existingCodesList = existingCustomRegionEntity.getCodes();
    List<String> codesForUpdate =
        codes.stream().filter(code -> !existingCodesList.contains(code)).toList();
    Map<String, List<PostalCodeDomainDto>> postalCodeEntityMap =
        validateCodes(codesForUpdate, orgId);
    List<String> updatedGeoZones = checkDuplicateGeozonePrefixes(existingCodesList, codesForUpdate);
    var customRegionRequestDetails = INSTANCE.toCustomRegionEntity(baseRequest);
    customRegionRequestDetails.setCodes(updatedGeoZones);
    if (!codesForUpdate.isEmpty()) {
      addCustomRegionToZipCodeDetails(
          codesForUpdate, postalCodeEntityMap, existingCustomRegionEntity.getId());
    }
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

  @Transactional
  public CustomRegionResponse deleteCustomRegionGeozones(
      String orgId, String id, DeleteCustomRegionGeozonesRequest deleteGeozonesRequest)
      throws CommonServiceException, PromiseEngineException {
    logger.debug("--inside deleteCustomRegionGeozones service--");
    var customRegion = validateCustomRegionRequestExist(orgId, id);
    List<String> geozonesToDelete = deleteGeozonesRequest.getCodes();
    List<String> existingCodes = customRegion.getCodes();
    validateGeozonesToDeleteArePartOfCustomRegion(
        customRegion.getId(), existingCodes, geozonesToDelete);
    validateCodes(geozonesToDelete, orgId);
    removeCustomRegionIdFromPostalCodes(geozonesToDelete, orgId);
    existingCodes.removeAll(geozonesToDelete);
    if (existingCodes.isEmpty()) {
      customRegionPersistenceService.deleteCustomRegion(customRegion);
      return INSTANCE.toCustomRegionResponse(customRegion);
    }
    customRegion.setCodes(existingCodes);
    customRegionPersistenceService.saveCustomRegion(customRegion);
    CustomRegionResponse customRegionResponse = INSTANCE.toCustomRegionResponse(customRegion);
    customRegionResponse.setCodes(geozonesToDelete);
    return customRegionResponse;
  }

  private void validateGeozonesToDeleteArePartOfCustomRegion(
      String regionId, List<String> existingCodes, List<String> geozonesToDelete)
      throws CommonServiceException {
    List<String> invalidCodes = new ArrayList<>();
    geozonesToDelete.forEach(
        code -> {
          if (!existingCodes.contains(code)) invalidCodes.add(code);
        });
    if (!invalidCodes.isEmpty()) {
      logger.error(ZIP_PREFIX_NOT_FOUND_CUSTOM_REGION + " {}", invalidCodes);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          UNAVAILABLE_CODES, FieldError.builder().rejectedValue(invalidCodes.toString()).build());
      errorMap.put(REGION_ID, FieldError.builder().rejectedValue(regionId).build());
      throw new CommonServiceException(
          ZIP_PREFIX_NOT_FOUND_CUSTOM_REGION,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.ZIP_PREFIX_NOT_FOUND_FOR_CUSTOM_REGION.getErrorCode(),
          errorMap);
    }
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
      throws PromiseEngineException, CommonServiceException {
    Set<String> countrySet = new HashSet<>();
    for (String postalCodePrefix : codes) {
      List<PostalCodeDomainDto> postalCodeEntityListByPrefix =
          postalCodePersistenceService.fetchPostalCodeList(orgId, postalCodePrefix);
      validateSameCountryPrefix(postalCodeEntityListByPrefix, countrySet);
      if (!postalCodeEntityListByPrefix.isEmpty()) {
        postalCodeEntityMap.put(postalCodePrefix, postalCodeEntityListByPrefix);
      } else {
        unavailableCodes.add(postalCodePrefix);
      }
    }
    if (countrySet.size() > 1) {
      logger.error(ZIP_PREFIX_MULTIPLE_COUNTRIES);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COUNTRY, FieldError.builder().rejectedValue(countrySet.toArray()).build());
      throw new CommonServiceException(
          ZIP_PREFIX_MULTIPLE_COUNTRIES,
          HttpStatus.BAD_REQUEST,
          ErrorCodesEnum.ZIP_PREFIX_FROM_MULTIPLE_COUNTRY_FOR_CUSTOM_REGION.getErrorCode(),
          errorMap);
    }
  }

  private void validateSameCountryPrefix(
      List<PostalCodeDomainDto> postalCodeEntityListByPrefix, Set<String> countrySet) {
    postalCodeEntityListByPrefix.forEach(postalCode -> countrySet.add(postalCode.getCountry()));
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

  public Page<CustomRegionInfo> getCustomRegionByCountryRegionIdAndName(
      String orgId,
      String country,
      String customRegionIds,
      String customRegionNames,
      PageParams pageParams)
      throws CommonServiceException, PromiseEngineException {
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      Page<CustomRegionProjection> customRegionList;
      List<String> customRegionIdsList =
          !ObjectUtils.isEmpty(customRegionIds) ? List.of(customRegionIds.split(",")) : null;
      List<String> customRegionNamesList =
          !ObjectUtils.isEmpty(customRegionNames) ? List.of(customRegionNames.split(",")) : null;
      if (ObjectUtils.isEmpty(customRegionIdsList) && !ObjectUtils.isEmpty(customRegionNamesList)) {
        customRegionList = fetchInfoByRegionNames(orgId, customRegionNamesList, pageParams);
      } else if (!ObjectUtils.isEmpty(customRegionIdsList)
          && ObjectUtils.isEmpty(customRegionNamesList)) {
        customRegionList =
            postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(
                orgId, customRegionIdsList, pageParams);
      } else if (ObjectUtils.isEmpty(customRegionNamesList)
          && ObjectUtils.isEmpty(customRegionIdsList)) {
        customRegionList =
            postalCodePersistenceService.getCustomRegionInfoByOrgIdAndCountry(
                orgId, country, pageParams);
      } else {
        customRegionList =
            fetchInfoByRegionIdsAndNamesAndOrgId(
                orgId, customRegionIdsList, customRegionNamesList, pageParams);
      }
      return getCustomRegionInfos(customRegionList, orgId);
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

  private Page<CustomRegionInfo> getCustomRegionInfos(
      Page<CustomRegionProjection> customRegionList, String orgId) throws PromiseEngineException {
    if (ObjectUtils.isEmpty(customRegionList)) {
      return Page.empty();
    }
    List<CustomRegionInfo> customRegionInfoList =
        INSTANCE.toCustomRegionInfo(customRegionList.getContent());
    List<String> customRegionIdsList =
        customRegionInfoList.stream().map(CustomRegionInfo::getCustomRegionId).toList();
    Map<String, CustomRegionDomainDto> customRegionDtos =
        customRegionPersistenceService
            .fetchCustomRegionsByIdsAndOrgId(customRegionIdsList, orgId)
            .stream()
            .collect(Collectors.toMap(CustomRegionDomainDto::getId, dto -> dto));
    customRegionInfoList.forEach(
        info -> updateCustomRegionInfo(info, customRegionDtos.get(info.getCustomRegionId())));
    return new PageImpl<>(
        customRegionInfoList, customRegionList.getPageable(), customRegionList.getTotalElements());
  }

  private void updateCustomRegionInfo(CustomRegionInfo info, CustomRegionDomainDto dto) {
    info.setCustomRegionDescription(dto.getCustomRegionDescription());
    info.setCustomRegionName(dto.getCustomRegionName());
    info.setZipCodes(dto.getCodes());
  }

  private Page<CustomRegionProjection> fetchInfoByRegionNames(
      String orgId, List<String> customRegionNames, PageParams pageParams)
      throws PromiseEngineException {
    List<CustomRegionDomainDto> regionDomainList =
        customRegionPersistenceService.fetchCustomRegionByNamesAndOrgId(customRegionNames, orgId);

    if (!ObjectUtils.isEmpty(regionDomainList)) {
      List<String> regionIdList =
          regionDomainList.stream().map(CustomRegionDomainDto::getId).toList();
      return postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(
          orgId, regionIdList, pageParams);
    }
    return Page.empty();
  }

  private Page<CustomRegionProjection> fetchInfoByRegionIdsAndNamesAndOrgId(
      String orgId,
      List<String> customRegionIds,
      List<String> customRegionNames,
      PageParams pageParams)
      throws PromiseEngineException {
    Optional<List<CustomRegionDomainDto>> customRegions =
        customRegionPersistenceService.fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(
            customRegionIds, customRegionNames, orgId);
    if (customRegions.isPresent()) {
      List<String> updatedRegionIdList =
          customRegions.get().stream().map(CustomRegionDomainDto::getId).toList();
      return postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(
          orgId, updatedRegionIdList, pageParams);
    }
    return Page.empty();
  }
}
