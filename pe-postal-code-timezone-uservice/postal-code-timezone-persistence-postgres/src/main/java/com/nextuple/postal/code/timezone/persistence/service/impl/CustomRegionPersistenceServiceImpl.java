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
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.key.CustomRegionDomainKey;
import com.nextuple.postal.code.timezone.persistence.entity.CustomRegionEntity;
import com.nextuple.postal.code.timezone.persistence.entity.key.CustomRegionKey;
import com.nextuple.postal.code.timezone.persistence.mapper.CustomRegionEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.CustomRegionRepository;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
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
public class CustomRegionPersistenceServiceImpl
    extends CommonPersistenceService<
        CustomRegionDomainDto,
        CustomRegionDomainKey,
        CustomRegionEntity,
        CustomRegionKey,
        CustomRegionRepository,
        CustomRegionEntityMapper>
    implements CustomRegionPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(CustomRegionPersistenceServiceImpl.class);

  private static final String ERROR_MESSAGE = "Error while fetching custom region list";

  @Override
  public CustomRegionDomainDto saveCustomRegion(CustomRegionDomainDto customRegion) {
    return save(customRegion);
  }

  @Override
  public Optional<CustomRegionDomainDto> fetchRegionByOrgIdAndId(
      String orgId, String customRegionId) {
    return findByKey(CustomRegionDomainKey.builder().orgId(orgId).id(customRegionId).build());
  }

  @Override
  public Optional<CustomRegionDomainDto> fetchRegionByOrgIdAndCustomRegionName(
      String orgId, String customRegionName) {
    return getRepository()
        .findByOrgIdAndCustomRegionName(orgId, customRegionName)
        .map(getMapper()::toDomain);
  }

  @Override
  public void deleteCustomRegion(CustomRegionDomainDto customRegion) {
    delete(customRegion);
  }

  @Override
  public Page<CustomRegionDomainDto> getCustomRegionListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws PromiseEngineException {
    try {
      Pageable pageable;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findCustomRegionByOrgId(orgId, pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ERROR_MESSAGE);
    }
  }

  @Override
  public List<CustomRegionDomainDto> fetchCustomRegionByNamesAndOrgId(
      List<String> customRegionNames, String orgId) throws PromiseEngineException {
    try {
      return getRepository()
          .fetchCustomRegionsByCustomRegionNamesAndOrgId(customRegionNames, orgId)
          .stream()
          .map(getMapper()::toDomain)
          .toList();
    } catch (Exception e) {
      logger.error(String.valueOf(e), ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ERROR_MESSAGE);
    }
  }

  @Override
  public Optional<List<CustomRegionDomainDto>> fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(
      List<String> customRegionIds, List<String> customRegionNames, String orgId)
      throws PromiseEngineException {
    try {
      return Optional.of(
          getRepository()
              .fetchCustomRegionByIdAndNameAndCountryAndOrgId(
                  customRegionIds, customRegionNames, orgId)
              .stream()
              .map(getMapper()::toDomain)
              .toList());
    } catch (Exception e) {
      logger.error(String.valueOf(e), ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ERROR_MESSAGE);
    }
  }

  @Override
  public List<CustomRegionDomainDto> fetchCustomRegionsByIdsAndOrgId(
      List<String> customRegionIds, String orgId) throws PromiseEngineException {
    try {
      return getRepository().findByIdInAndOrgId(customRegionIds, orgId).stream()
          .map(getMapper()::toDomain)
          .toList();
    } catch (Exception e) {
      logger.error(String.valueOf(e), ERROR_MESSAGE);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_FIND_FAILED, ERROR_MESSAGE);
    }
  }
}
