/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postal.code.timezone.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.key.PostalCodeTimezoneDomainKey;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.persistence.entity.key.PostalCodeTimezoneKey;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeTimezoneEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.PostalCodeTimezoneRepository;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodeTimezonePersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimezonePersistenceServiceImpl
    extends CommonPersistenceService<
        PostalCodeTimezoneDomainDto,
        PostalCodeTimezoneDomainKey,
        PostalCodeTimezoneEntity,
        PostalCodeTimezoneKey,
        PostalCodeTimezoneRepository,
        PostalCodeTimezoneEntityMapper>
    implements PostalCodeTimezonePersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(PostalCodeTimezonePersistenceServiceImpl.class);
  public static final String ZIP_CODE_TIMEZONE_NOT_FOUND =
      "Zip Code Timezone not found for a given orgId.";

  @Override
  public PostalCodeTimezoneDomainDto savePostalCodeTimezone(
      PostalCodeTimezoneDomainDto postalCodeTimezoneDomainDto) throws PromiseEngineException {
    try {
      return save(postalCodeTimezoneDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save Zip Code Timezone!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save Zip Code Timezone!");
    }
  }

  @Override
  public PostalCodeTimezoneDomainDto getPostalCodeTimezone(String orgId, String postalCodePrefix)
      throws PromiseEngineException {
    try {
      PostalCodeTimezoneEntity postalCodeTimezoneEntity =
          getRepository().findByOrgIdAndZipCodePrefix(orgId, postalCodePrefix);
      return getMapper().toDomain(postalCodeTimezoneEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Zip Code Timezone not found!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "zip Code Timezone not found!");
    }
  }

  @Override
  public PostalCodeTimezoneDomainDto deletePostalCodeTimezone(
      PostalCodeTimezoneDomainDto postalCodeTimezoneDomainDto) throws PromiseEngineException {
    try {
      delete(postalCodeTimezoneDomainDto);
      return postalCodeTimezoneDomainDto;
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete Zip Code Timezone!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Zip Code Timezone!");
    }
  }

  @Override
  public List<PostalCodeTimezoneDomainDto> getPostalCodeTimezoneForOrgId(String orgId)
      throws PromiseEngineException {
    try {
      List<PostalCodeTimezoneEntity> postalCodeTimezoneEntities =
          getRepository().findByOrgId(orgId);
      return getMapper().toDomain(postalCodeTimezoneEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), ZIP_CODE_TIMEZONE_NOT_FOUND);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          ZIP_CODE_TIMEZONE_NOT_FOUND);
    }
  }
}
