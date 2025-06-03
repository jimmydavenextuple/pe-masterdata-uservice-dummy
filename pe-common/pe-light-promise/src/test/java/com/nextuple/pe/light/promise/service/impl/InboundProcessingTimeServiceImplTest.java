/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.service.NodeCalendarNearCacheService;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.cache.service.NodeDataNearCacheService;
import com.nextuple.pe.light.promise.TestUtils;
import com.nextuple.pe.light.promise.inbound.InboundProcessingTimeRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingTimeResponse;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import com.nextuple.rulecraft.engine.model.ResourceTagEvalRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InboundProcessingTimeServiceImplTest {

  @InjectMocks private InboundProcessingTimeServiceImpl inboundProcessingTimeService;

  @Mock private RuleEngineApi ruleEngineApi;

  @Mock private NodeDataNearCacheService nodeDataNearCacheService;

  @Mock private NodeCalendarNearCacheService nodeCalendarNearCacheService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup default mock responses for the services
    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime("09:00").lastWorkingTime("17:00").build();
    when(nodeDataNearCacheService.get(any(NodeDataCacheKey.class))).thenReturn(mockNodeData);

    List<CalendarDaysStatusInfo> calendarDays = new ArrayList<>();
    CalendarDaysStatusInfo info = new CalendarDaysStatusInfo();
    info.setDate("2023-01-01");
    info.setIsActive(true);
    calendarDays.add(info);

    NodeCalendarCacheValue mockCalendarValue =
        NodeCalendarCacheValue.builder().calendarDaysStatusInfo(calendarDays).build();
    when(nodeCalendarNearCacheService.get(any(NodeCalendarCacheKey.class)))
        .thenReturn(mockCalendarValue);
  }

  @Test
  @DisplayName("Validate request with blank nodeId throws exception")
  void validateInboundProcessingRequest_withBlankNodeId_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setNodeId(" "); // Set nodeId to blank

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: nodeId can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with null orgId throws exception")
  void validateInboundProcessingRequest_withNullOrgId_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setOrgId(null); // Set orgId to null

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: orgId can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with blank ruleGroup throws exception")
  void validateInboundProcessingRequest_withBlankRuleGroup_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleGroup(" "); // Set ruleGroup to blank

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: ruleGroup can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with null ruleEvaluationFacts throws exception")
  void validateInboundProcessingRequest_withNullRuleEvaluationFacts_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleEvaluationFacts(null); // Set ruleEvaluationFacts to null

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: ruleEvaluationFacts can't be null.", exception.getMessage());
  }

  @Test
  @DisplayName("Get inbound processing time with null response key throws exception")
  void getInboundProcessingTime_withNullResponseKey_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Collections.emptyMap(); // Simulate null response key
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Inbound processing time key is null in the response.", exception.getMessage());
  }

  @Test
  @DisplayName("Evaluate inbound processing time with valid request")
  void evaluateInboundProcessingTime_withValidRequest() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Map.of("inboundProcessingTime", 10.0);
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    verify(ruleEngineApi)
        .evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest);
  }

  @Test
  @DisplayName("Evaluate inbound processing time with null ruleFilterStrategy")
  void evaluateInboundProcessingTime_withNullRuleFilterStrategy() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleFilterStrategy(null); // Set ruleFilterStrategy to null
    Map<String, Object> mockResponse = Map.of("inboundProcessingTime", 10.0);
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals("inbound-processing-time-filter", request.getRuleFilterStrategy());
    verify(ruleEngineApi)
        .evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest);
  }

  @Test
  @DisplayName("Evaluate inbound processing time with blank ruleFilterStrategy")
  void evaluateInboundProcessingTime_withBlankRuleFilterStrategy() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleFilterStrategy(" "); // Set ruleFilterStrategy to blank
    Map<String, Object> mockResponse = Map.of("inboundProcessingTime", 10.0);
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals("inbound-processing-time-filter", request.getRuleFilterStrategy());
    verify(ruleEngineApi)
        .evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest);
  }

  @Test
  @DisplayName("Get inbound processing time with invalid response key type throws exception")
  void getInboundProcessingTime_withInvalidResponseKeyType_throwsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse =
        Map.of("inboundProcessingTime", "invalidType"); // Simulate invalid type
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Inbound processing time key is not a valid number.", exception.getMessage());
  }
}
