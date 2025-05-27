/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.light.promise.TestUtils;
import com.nextuple.pe.light.promise.inbound.InboundProcessingTimeRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingTimeResponse;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InboundProcessingTimeServiceImplTest {

  @InjectMocks private InboundProcessingTimeServiceImpl inboundProcessingTimeService;

  @Mock private RuleEngineApi ruleEngineApi;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void evaluateInboundProcessingTime() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Map.of("key", "value");
    when(ruleEngineApi.evaluateRules(request.getRuleGroup(), request.getRuleEvaluationRequest()))
        .thenReturn(mockResponse);
    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);
    assertEquals(mockResponse, response.getInbound());
    verify(ruleEngineApi).evaluateRules(request.getRuleGroup(), request.getRuleEvaluationRequest());
  }
}
