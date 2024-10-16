/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
import com.nextuple.sourcing.cost.config.service.ExpressionValidationService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

@DisplayName("ExpressionValidationController Test Cases")
class ExpressionValidationControllerTest {

  @Mock private ExpressionValidationService expressionValidationService;

  @InjectMocks private ExpressionValidationController expressionValidationController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void validateExpressionTest() throws CommonServiceException {
    when(expressionValidationService.validateExpression(anyString(), anyString(), any()))
        .thenReturn(testUtil.getExpressionValidationResponse());
    ResponseEntity<BaseResponse<ExpressionValidationResponse>> response =
        expressionValidationController.validateExpression(
            TestUtil.ORG_ID, TestUtil.LIBRARY_NAME, testUtil.getExpressionValidationRequest());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(TestUtil.EXPRESSION_VALUE, response.getBody().getPayload().getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getBody().getPayload().getSampleRequest().getOrgId());
    verify(expressionValidationService, times(1))
        .validateExpression(anyString(), anyString(), any());
  }
}
