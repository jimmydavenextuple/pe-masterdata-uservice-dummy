/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

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
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateSourcingAttributeRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SourcingAttributeControllerTest {

  @Mock private SourcingAttributeService sourcingAttributeService;
  @InjectMocks private SourcingAttributeController sourcingAttributeController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSourcingAttributeTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();
    when(sourcingAttributeService.createSourcingAttribute(
            any(CreateSourcingAttributeRequest.class)))
        .thenReturn(sourcingAttributeResponse);

    ResponseEntity<BaseResponse<SourcingAttributeResponse>> responseEntity =
        sourcingAttributeController.createSourcingAttribute(
            testUtil.getCreateSourcingAttributeRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingAttributeResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingAttributeService, times(1))
        .createSourcingAttribute(any(CreateSourcingAttributeRequest.class));
  }

  @Test
  void createSourcingAttributeExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributeService.createSourcingAttribute(
            any(CreateSourcingAttributeRequest.class)))
        .thenThrow(
            new RuntimeException(
                "Custom Attribute key cannot be empty when isDerived is set to true"));
    CreateSourcingAttributeRequest request = testUtil.getCreateSourcingAttributeRequest2();

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () -> sourcingAttributeController.createSourcingAttribute(request));
    assertEquals(
        "Custom Attribute key cannot be empty when isDerived is set to true", ex.getMessage());
  }

  @Test
  void getSourcingAttributeByIdTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeResponse sourcingAttributeResponse = testUtil.getSourcingAttributeResponse();
    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(sourcingAttributeResponse);

    ResponseEntity<BaseResponse<SourcingAttributeResponse>> responseEntity =
        sourcingAttributeController.getSourcingAttributeByOrgIdAndId(
            TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTE_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingAttributeResponse.getId(), responseEntity.getBody().getPayload().getId());
    verify(sourcingAttributeService, times(1))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributeByIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributeService.getSourcingAttributeByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching sourcing attribute"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributeController.getSourcingAttributeByOrgIdAndId(
                  TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTE_ID);
            });

    assertEquals("Error in fetching sourcing attribute", ex.getMessage());
    verify(sourcingAttributeService, times(1))
        .getSourcingAttributeByIdAndOrgId(anyLong(), anyString());
  }
}
