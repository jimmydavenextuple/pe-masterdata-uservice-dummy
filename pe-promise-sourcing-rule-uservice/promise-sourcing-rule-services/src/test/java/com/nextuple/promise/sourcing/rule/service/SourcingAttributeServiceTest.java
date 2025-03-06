/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SourcingAttributeServiceTest {
  @InjectMocks private SourcingAttributeService sourcingAttributeService;
  @Mock private SourcingAttributePersistenceService sourcingAttributePersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSourcingAttributeTest() throws PromiseEngineException, CommonServiceException {
    CreateSourcingAttributeRequest createSourcingAttributeRequest =
        testUtil.getCreateSourcingAttributeRequest();
    when(sourcingAttributePersistenceService.saveSourcingAttribute(
            any(SourcingAttributeDomainDto.class)))
        .thenReturn(testUtil.getSourcingAttributeEntity());

    SourcingAttributeResponse sourcingAttributeResponse =
        sourcingAttributeService.createSourcingAttribute(createSourcingAttributeRequest);
    assertEquals(testUtil.getSourcingAttributeEntity().getId(), sourcingAttributeResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingAttributeResponse.getCustomAttributes());

    verify(sourcingAttributePersistenceService, times(1))
        .saveSourcingAttribute(any(SourcingAttributeDomainDto.class));
  }

  @Test
  void createSourcingAttributeExceptionTest1() throws CommonServiceException {
    CreateSourcingAttributeRequest request = testUtil.getCreateSourcingAttributeRequest2();

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> sourcingAttributeService.createSourcingAttribute(request));
    assertEquals(
        "Custom Attribute key cannot be empty when isDerived is set to true", ex.getMessage());
  }

  @Test
  void createSourcingAttributeExceptionTest2() throws CommonServiceException {
    CreateSourcingAttributeRequest request = testUtil.getCreateSourcingAttributeRequest3();

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> sourcingAttributeService.createSourcingAttribute(request));
    assertEquals("JsonPath cannot be empty when isDerived is set to false", ex.getMessage());
  }

  @Test
  void createSourcingAttributeValidationTest() throws PromiseEngineException {
    CreateSourcingAttributeRequest request = testUtil.getCreateSourcingAttributeRequestValidation();
    request.setAttributeName("NEXTUPLE:1");

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> sourcingAttributeService.createSourcingAttribute(request));

    assertEquals(
        "Invalid format! All the characters except colon and comma are allowed.", ex.getMessage());
  }

  @Test
  void getSourcingAttributeByIdTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributePersistenceService.getSourcingAttributeByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getSourcingAttributeEntity()));

    SourcingAttributeResponse sourcingAttributeResponse =
        sourcingAttributeService.getSourcingAttributeByIdAndOrgId(
            TestUtil.SOURCING_ATTRIBUTE_ID, TestUtil.ORG_ID);

    assertEquals(testUtil.getSourcingAttributeEntity().getId(), sourcingAttributeResponse.getId());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributeByIdExceptionTest() throws PromiseEngineException {
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributeService.getSourcingAttributeByIdAndOrgId(
                  TestUtil.SOURCING_ATTRIBUTE_ID, TestUtil.ORG_ID);
            });

    assertEquals("Sourcing attribute not found", ex.getMessage());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
  }
}
