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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeValueRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AddAttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.AttributeValuesPersistenceService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttributeService {
  private static final Logger logger = LoggerFactory.getLogger(AttributeService.class);
  private static final String ORG_ID = "orgId";
  private static final String ATTRIBUTE_NAME = "attribute_name";

  private final SourcingAttributeService sourcingAttributeService;

  private final AttributeValuesPersistenceService attributeValuesPersistenceService;

  public AttributeValueResponse getAttributeValues(String orgId, String attributeName)
      throws PromiseEngineException, CommonServiceException {

    SourcingAttributeResponse sourcingAttributeResponse =
        sourcingAttributeService.getSourcingAttributeByOrgIdAndName(orgId, attributeName);
    Long attributeId = sourcingAttributeResponse.getId();
    List<AttributeValuesDomainDto> attributeValuesDomainDtos =
        attributeValuesPersistenceService.getAttributeValues(attributeId);
    AttributeValueResponse response = new AttributeValueResponse();
    response.setAttributeName(attributeName);
    response.setValues(new ArrayList<>());
    if (!attributeValuesDomainDtos.isEmpty()) {
      for (AttributeValuesDomainDto valuesDomainDto : attributeValuesDomainDtos) {
        response.getValues().add(valuesDomainDto.getValue());
      }
    }
    return response;
  }

  public AddAttributeValueResponse addValueToAttribute(
      String orgId, String attributeName, AttributeValueRequest attributeValueRequest)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse =
        sourcingAttributeService.getSourcingAttributeByOrgIdAndName(orgId, attributeName);
    Long attributeId = sourcingAttributeResponse.getId();
    AttributeValuesDomainDto entity =
        attributeValuesPersistenceService.addValueToAttribute(
            attributeId, attributeValueRequest.getValue());
    return AddAttributeValueResponse.builder()
        .attributeName(attributeName)
        .value(entity.getValue())
        .build();
  }

  @Transactional
  public AddAttributeValueResponse deleteValueOfAttribute(
      String orgId, String attributeName, String value)
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse =
        sourcingAttributeService.getSourcingAttributeByOrgIdAndName(orgId, attributeName);
    Long attributeId = sourcingAttributeResponse.getId();
    attributeValuesPersistenceService.deleteValueForAttribute(attributeId, value);
    return AddAttributeValueResponse.builder().attributeName(attributeName).value(value).build();
  }
}
