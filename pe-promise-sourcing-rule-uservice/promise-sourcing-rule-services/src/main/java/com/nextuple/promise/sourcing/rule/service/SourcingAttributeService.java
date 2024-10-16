/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingAttributeMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SourcingAttributeService {

  private static final Logger logger = LoggerFactory.getLogger(SourcingAttributeService.class);
  private static final String ID = "id";
  private static final String ORG_ID = "orgId";
  private static final String ATTRIBUTE_NAME = "attributeName";
  private static final String SOURCING_ATTRIBUTE_EXCEPTION_MESSAGE = "Sourcing attribute not found";

  private static final SourcingAttributeMapper INSTANCE =
      Mappers.getMapper(SourcingAttributeMapper.class);

  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;

  public SourcingAttributeResponse createSourcingAttribute(
      CreateSourcingAttributeRequest createSourcingAttributeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside createSourcingAttribute service --");
    PromiseSourcingRuleUtil.validateAttributeValueFormat(
        createSourcingAttributeRequest.getAttributeName());
    List<SourcingAttributeDomainDto> sourcingAttributeDomainDtoList =
        sourcingAttributePersistenceService.getSourcingAttributeListByOrgIdAndAttributeName(
            createSourcingAttributeRequest.getOrgId(),
            createSourcingAttributeRequest.getAttributeName());
    if (!sourcingAttributeDomainDtoList.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(createSourcingAttributeRequest.getOrgId()).build());
      errorMap.put(
          ATTRIBUTE_NAME,
          FieldError.builder()
              .rejectedValue(createSourcingAttributeRequest.getAttributeName())
              .build());
      throw new CommonServiceException(
          "Attribute already exists for given orgId", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    if (Boolean.TRUE.equals(createSourcingAttributeRequest.getIsDerived())
        && !(StringUtils.hasLength(createSourcingAttributeRequest.getCustomAttributeKey()))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          "customAttributeKey",
          FieldError.builder()
              .rejectedValue(createSourcingAttributeRequest.getCustomAttributeKey())
              .build());
      throw new CommonServiceException(
          "Custom Attribute key cannot be empty when isDerived is set to true",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    if (Boolean.FALSE.equals(createSourcingAttributeRequest.getIsDerived())
        && !(StringUtils.hasLength(createSourcingAttributeRequest.getJsonPath()))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          "jsonPath",
          FieldError.builder()
              .rejectedValue(createSourcingAttributeRequest.getCustomAttributeKey())
              .build());
      throw new CommonServiceException(
          "JsonPath cannot be empty when isDerived is set to false",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    var sourcingAttributeRequest =
        INSTANCE.toSourcingAttributeEntity(createSourcingAttributeRequest);
    return INSTANCE.toSourcingAttributeResponse(
        sourcingAttributePersistenceService.saveSourcingAttribute(sourcingAttributeRequest));
  }

  public SourcingAttributeResponse getSourcingAttributeByIdAndOrgId(Long id, String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside getSourcingAttributeByIdAndOrgId service --");
    Optional<SourcingAttributeDomainDto> sourcingAttributeDomainDto =
        sourcingAttributePersistenceService.getSourcingAttributeByIdAndOrgId(id, orgId);
    if (sourcingAttributeDomainDto.isEmpty()) {
      logger.error(SOURCING_ATTRIBUTE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          SOURCING_ATTRIBUTE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toSourcingAttributeResponse(sourcingAttributeDomainDto.get());
  }

  public SourcingAttributeResponse getSourcingAttributeByOrgIdAndName(
      String orgId, String attributeName) throws PromiseEngineException, CommonServiceException {
    logger.debug("-- inside getSourcingAttributeByOrgIdAndName service --");
    List<SourcingAttributeDomainDto> sourcingAttributeDomainDto =
        sourcingAttributePersistenceService.getSourcingAttributeListByOrgIdAndAttributeName(
            orgId, attributeName);
    if (sourcingAttributeDomainDto.isEmpty()) {
      logger.error(SOURCING_ATTRIBUTE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          SOURCING_ATTRIBUTE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toSourcingAttributeResponse(sourcingAttributeDomainDto.get(0));
  }
}
