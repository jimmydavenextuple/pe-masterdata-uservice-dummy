/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeValueRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AddAttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.AttributeValuesPersistenceService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AttributeServiceTest {

  @InjectMocks private AttributeService attributeService;

  @Mock private AttributeValuesPersistenceService attributeValuesPersistenceService;

  @Mock private SourcingAttributeService sourcingAttributeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAttributeValuesTest() throws PromiseEngineException, CommonServiceException {
    List<AttributeValuesDomainDto> entityList =
        Arrays.asList(
            new AttributeValuesDomainDto(1L, 1L, "abc"),
            new AttributeValuesDomainDto(2L, 2L, "def"),
            new AttributeValuesDomainDto(3L, 3L, "xyz"));
    SourcingAttributeResponse sourcingAttributeResponse = new SourcingAttributeResponse();
    sourcingAttributeResponse.setId(1L);
    when(attributeValuesPersistenceService.getAttributeValues(anyLong())).thenReturn(entityList);
    when(sourcingAttributeService.getSourcingAttributeByOrgIdAndName(anyString(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    AttributeValueResponse response = attributeService.getAttributeValues("NEXTUPLE", "SDND");
    assertEquals(entityList.size(), response.getValues().size());
    verify(attributeValuesPersistenceService, times(1)).getAttributeValues(anyLong());
    verify(sourcingAttributeService, times(1))
        .getSourcingAttributeByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void getAttributeValuesTest2() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse = new SourcingAttributeResponse();
    sourcingAttributeResponse.setId(1L);
    when(attributeValuesPersistenceService.getAttributeValues(anyLong())).thenReturn(List.of());
    when(sourcingAttributeService.getSourcingAttributeByOrgIdAndName(anyString(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    AttributeValueResponse response = attributeService.getAttributeValues("NEXTUPLE", "SDND");
    assertEquals(0, response.getValues().size());
    verify(attributeValuesPersistenceService, times(1)).getAttributeValues(anyLong());
    verify(sourcingAttributeService, times(1))
        .getSourcingAttributeByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void addValueToAttributeTest() throws PromiseEngineException, CommonServiceException {
    AttributeValueRequest attributeValueRequest = new AttributeValueRequest("SDND");
    AttributeValuesDomainDto entity = new AttributeValuesDomainDto(1L, 1L, "abc");
    SourcingAttributeResponse sourcingAttributeResponse = new SourcingAttributeResponse();
    sourcingAttributeResponse.setId(1L);
    when(attributeValuesPersistenceService.addValueToAttribute(anyLong(), anyString()))
        .thenReturn(entity);
    when(sourcingAttributeService.getSourcingAttributeByOrgIdAndName(anyString(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    AddAttributeValueResponse response =
        attributeService.addValueToAttribute("NEXTUPLE", "SDND", attributeValueRequest);
    assertEquals(entity.getValue(), response.getValue());
    verify(attributeValuesPersistenceService, times(1)).addValueToAttribute(anyLong(), anyString());
  }

  @Test
  void deleteValueOfAttributeTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse = new SourcingAttributeResponse();
    sourcingAttributeResponse.setId(1L);
    when(sourcingAttributeService.getSourcingAttributeByOrgIdAndName(anyString(), anyString()))
        .thenReturn(sourcingAttributeResponse);
    AddAttributeValueResponse response =
        attributeService.deleteValueOfAttribute("NEXTUPLE", "SDND", "abc");
    assertEquals("abc", response.getValue());
    verify(attributeValuesPersistenceService, times(1))
        .deleteValueForAttribute(anyLong(), anyString());
  }
}
