/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postal.code.timezone.persistence.service.impl;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.postal.code.timezone.api.domain.projection.CustomRegionProjection;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.key.PostalCodeDomainKey;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeEntity;
import com.nextuple.postal.code.timezone.persistence.entity.key.PostalCodeKey;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.PostalCodeRepository;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodePersistenceServiceImpl
    extends CommonPersistenceService<
        PostalCodeDomainDto,
        PostalCodeDomainKey,
        PostalCodeEntity,
        PostalCodeKey,
        PostalCodeRepository,
        PostalCodeEntityMapper>
    implements PostalCodePersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(PostalCodePersistenceServiceImpl.class);

  private final PageProperties pageProperties;

  private static final String ERROR_MESSAGE = "Unable to find zip code";
  public static final String ZIP_CODE_NOT_FOUND = "Zip Code not found for a given orgId.";

  private static final String ERROR_MESSAGE_FOR_ZIP_CODE_PREFIX =
      "Unable to find zip code by prefix";

  @Override
  public PostalCodeDomainDto savePostalCode(PostalCodeDomainDto postalCodeDomainDto)
      throws PromiseEngineException {
    try {
      return save(postalCodeDomainDto);
    } catch (Exception e) {
      logger.error("Unable to save zip code", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save zip code");
    }
  }

  @Override
  public Optional<PostalCodeDomainDto> fetchPostalCode(String orgId, String zipCode)
      throws PromiseEngineException {
    try {
      return findByKey(PostalCodeDomainKey.builder().orgId(orgId).zipCode(zipCode).build());
    } catch (Exception e) {
      logger.error(ERROR_MESSAGE, e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ERROR_MESSAGE);
    }
  }

  @Override
  public void deletePostalCodeEntity(PostalCodeDomainDto postalCodeDomainDto)
      throws PromiseEngineException {
    try {
      delete(postalCodeDomainDto);
    } catch (Exception e) {
      logger.error("Unable to delete zip code details entity", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete zip code details entity");
    }
  }

  @Override
  public List<PostalCodeDomainDto> fetchPostalCodeList(String orgId, String postalCodePrefix)
      throws PromiseEngineException {
    try {
      List<PostalCodeEntity> postalCodeEntities =
          getRepository().findByOrgIdAndZipCodePrefix(orgId, postalCodePrefix);
      return getMapper().toDomain(postalCodeEntities);
    } catch (Exception e) {
      logger.error(ERROR_MESSAGE_FOR_ZIP_CODE_PREFIX, e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          ERROR_MESSAGE_FOR_ZIP_CODE_PREFIX);
    }
  }

  @Override
  public List<MarketRegionProjection> getRecordsForOrgId(String orgId)
      throws PromiseEngineException {
    try {
      return getRepository().findRecordsByOrgId(orgId);
    } catch (Exception e) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ZIP_CODE_NOT_FOUND);
    }
  }

  @Override
  public List<PostalCodeDomainDto> getPostCodeTimeZoneByOrgIdAndCountry(
      String orgId, String country) throws PromiseEngineException {
    try {
      List<PostalCodeEntity> postalCodeEntities =
          getRepository().findByOrgIdAndCountry(orgId, country);
      return getMapper().toDomain(postalCodeEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), ZIP_CODE_NOT_FOUND);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ZIP_CODE_NOT_FOUND);
    }
  }

  @Override
  public List<String> getPostalCodePrefixForOrgIdAndState(String orgId, String state)
      throws PromiseEngineException {
    try {
      return getRepository().findByOrgIdAndState(orgId, state);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Error while fetching list of zip code prefix for given orgId: {} state: {}",
          orgId,
          state);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Error while fetching list of zip code prefix for given orgId and state");
    }
  }

  @Override
  public List<PostalCodeDomainDto> getPostalCodeForOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<PostalCodeEntity> postalCodeEntities =
          getRepository().findByOrgIdOrderByStateAscZipCodePrefixAsc(orgId);
      return getMapper().toDomain(postalCodeEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), ZIP_CODE_NOT_FOUND);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ZIP_CODE_NOT_FOUND);
    }
  }

  @Override
  public Page<CustomRegionProjection> fetchCustomRegionInfoByOrgIdAndRegionId(
      String orgId, List<String> customRegionIdList, PageParams pageParams)
      throws PromiseEngineException {
    try {
      Pageable pageable = getPageable(pageParams);
      return getRepository().findByCustomRegionInAndOrgId(customRegionIdList, orgId, pageable);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Error while fetching custom region details for orgID {} and custom region ID(s) {}",
          orgId,
          customRegionIdList);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Error while fetching custom region details");
    }
  }

  @Override
  public Page<CustomRegionProjection> getCustomRegionInfoByOrgIdAndCountry(
      String orgId, String country, PageParams pageParams) throws PromiseEngineException {
    try {
      Pageable pageable = getPageable(pageParams);
      return getRepository().findRecordsByCountryAndOrgId(orgId, country, pageable);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Error while fetching custom region details for orgID {} and country {}",
          orgId,
          country);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Error while fetching custom region details");
    }
  }

  private Pageable getPageable(PageParams pageParams) {
    Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    String sortBy = pageParams.getSortBy().orElse("custom_region");
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    Pageable pageable;
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
      pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
    } else {
      pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
    }
    return pageable;
  }
}
