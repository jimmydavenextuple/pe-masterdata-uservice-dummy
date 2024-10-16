/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postgres.service.CommonPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.AttributeValuesDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.AttributeValuesEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.AttributeValuesKey;
import com.nextuple.promise.sourcing.rule.persistence.mapper.AttributeValuesEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.AttributeValuesRepository;
import com.nextuple.promise.sourcing.rule.persistence.service.AttributeValuesPersistenceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttributeValuesPersistenceServiceImpl
    extends CommonPersistenceService<
        AttributeValuesDomainDto,
        AttributeValuesDomainKey,
        AttributeValuesEntity,
        AttributeValuesKey,
        AttributeValuesRepository,
        AttributeValuesEntityMapper>
    implements AttributeValuesPersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(AttributeValuesPersistenceServiceImpl.class);

  @Override
  public List<AttributeValuesDomainDto> getAttributeValues(Long attributeId)
      throws PromiseEngineException {
    try {
      List<AttributeValuesEntity> attributeValuesEntities =
          getRepository().findByNameId(attributeId);
      return getMapper().toDomain(attributeValuesEntities);
    } catch (Exception e) {
      logger.error("Unable to find the attribute values", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find the attribute values");
    }
  }

  @Override
  public AttributeValuesDomainDto addValueToAttribute(Long attributeId, String value)
      throws PromiseEngineException {
    AttributeValuesDomainDto attributeValuesDomainDto = new AttributeValuesDomainDto();
    attributeValuesDomainDto.setNameId(attributeId);
    attributeValuesDomainDto.setValue(value);
    try {
      return save(attributeValuesDomainDto);
    } catch (Exception e) {
      logger.error("Unable to add value to attribute", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to add value to attribute");
    }
  }

  @Override
  public void deleteValueForAttribute(Long attributeId, String value)
      throws PromiseEngineException {
    try {
      getRepository().deleteByNameIdAndValue(attributeId, value);
    } catch (Exception e) {
      logger.error("Unable to delete value of attribute", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete value of attribute");
    }
  }

  @Override
  public List<AttributeValuesDomainDto> getAllAttributeValues(List<Long> attributeIds)
      throws PromiseEngineException {
    try {
      List<AttributeValuesEntity> attributeValuesEntities =
          getRepository().findByNameIdIn(attributeIds);
      return getMapper().toDomain(attributeValuesEntities);
    } catch (Exception e) {
      logger.error("Unable to find the attribute values list", e);
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find the attribute values list");
    }
  }
}
