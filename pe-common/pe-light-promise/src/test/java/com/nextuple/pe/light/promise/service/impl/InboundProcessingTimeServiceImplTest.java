package com.nextuple.pe.light.promise.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.light.promise.TestUtils;
import com.nextuple.pe.light.promise.inbound.InboundProcessingRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingResponse;
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
    InboundProcessingRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Map.of("key", "value");
    when(ruleEngineApi.evaluateRules(request.getRuleGroup(), request.getRuleEvaluationRequest()))
        .thenReturn(mockResponse);
    InboundProcessingResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);
    assertEquals(mockResponse, response.getInbound());
    verify(ruleEngineApi).evaluateRules(request.getRuleGroup(), request.getRuleEvaluationRequest());
  }
}
