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
import org.junit.jupiter.api.Assertions;
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

    inboundProcessingTimeService = new InboundProcessingTimeServiceImpl(ruleEngineApi);
    inboundProcessingTimeService.nodeDataNearCacheService = nodeDataNearCacheService;
    inboundProcessingTimeService.nodeCalendarNearCacheService = nodeCalendarNearCacheService;

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
  void validateInboundProcessingRequestwithBlankNodeIdthrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setNodeId(" ");

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: nodeId can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with null orgId throws exception")
  void validateInboundProcessingRequestwithNullOrgIdthrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setOrgId(null);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: orgId can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with blank ruleGroup throws exception")
  void validateInboundProcessingRequestwithBlankRuleGroupthrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleGroup(" ");

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: ruleGroup can't be blank.", exception.getMessage());
  }

  @Test
  @DisplayName("Validate request with null ruleEvaluationFacts throws exception")
  void validateInboundProcessingRequestwithNullRuleEvaluationFactsthrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleEvaluationFacts(null);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Validation failed: ruleEvaluationFacts can't be null.", exception.getMessage());
  }

  @Test
  @DisplayName("Get inbound processing time with null response key throws exception")
  void getInboundProcessingTimewithNullResponseKeythrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Collections.emptyMap();
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
  void evaluateInboundProcessingTimewithValidRequest() throws CommonServiceException {
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
  void evaluateInboundProcessingTimewithNullRuleFilterStrategy() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleFilterStrategy(null);
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
  void evaluateInboundProcessingTimewithBlankRuleFilterStrategy() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setRuleFilterStrategy(" ");

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

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId(request.getNodeId()).orgId(request.getOrgId()).build();
    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime("09:00").lastWorkingTime("17:00").build();
    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(mockNodeData);

    NodeCalendarCacheKey expectedCalendarKey =
        NodeCalendarCacheKey.builder()
            .nodeId(request.getNodeId())
            .orgId(request.getOrgId())
            .fromDate(request.getRequestDate())
            .build();
    List<CalendarDaysStatusInfo> calendarDays = new ArrayList<>();
    CalendarDaysStatusInfo info = new CalendarDaysStatusInfo();
    info.setDate("2023-01-01");
    info.setIsActive(true);
    calendarDays.add(info);
    NodeCalendarCacheValue mockCalendarValue =
        NodeCalendarCacheValue.builder().calendarDaysStatusInfo(calendarDays).build();
    when(nodeCalendarNearCacheService.get(expectedCalendarKey)).thenReturn(mockCalendarValue);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals("inbound-processing-time-filter", request.getRuleFilterStrategy());
    assertEquals("09:00", response.getStartWorkingTime());
    assertEquals("17:00", response.getLastWorkingTime());
    assertNotNull(response.getCalendarDays());
    assertEquals(1, response.getCalendarDays().size());
    assertEquals("2023-01-01", response.getCalendarDays().get(0).getDate());

    verify(ruleEngineApi)
        .evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList("inbound-processing-time-filter"),
            resourceTagEvalRequest);
    verify(nodeDataNearCacheService).get(any(NodeDataCacheKey.class));
    verify(nodeCalendarNearCacheService).get(any(NodeCalendarCacheKey.class));
  }

  @Test
  @DisplayName("Get inbound processing time with invalid response key type throws exception")
  void getInboundProcessingTimewithInvalidResponseKeyTypethrowsException() {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    Map<String, Object> mockResponse = Map.of("inboundProcessingTime", "invalidType");
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

  @Test
  @DisplayName("Test with custom rule filter strategy")
  void evaluateInboundProcessingTimewithCustomRuleFilterStrategy() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    String customFilterStrategy = "custom-filter-strategy";
    request.setRuleFilterStrategy(customFilterStrategy);

    Map<String, Object> mockResponse = Map.of("inboundProcessingTime", 15.5);
    ResourceTagEvalRequest resourceTagEvalRequest =
        new ResourceTagEvalRequest(
            request.getRuleEvaluationFacts(), request.getRuleEvaluationFacts());
    when(ruleEngineApi.evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList(customFilterStrategy),
            resourceTagEvalRequest))
        .thenReturn(mockResponse);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(15.5, response.getInboundProcessingTime());
    assertEquals(customFilterStrategy, request.getRuleFilterStrategy());
    verify(ruleEngineApi)
        .evaluateRules(
            request.getOrgId(),
            request.getRuleGroup(),
            Collections.singletonList(customFilterStrategy),
            resourceTagEvalRequest);
  }

  @Test
  @DisplayName("Test with multiple calendar days")
  void evaluateInboundProcessingTimewithMultipleCalendarDays() throws CommonServiceException {
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

    List<CalendarDaysStatusInfo> calendarDays = new ArrayList<>();

    CalendarDaysStatusInfo day1 = new CalendarDaysStatusInfo();
    day1.setDate("2023-01-01");
    day1.setIsActive(true);
    calendarDays.add(day1);

    CalendarDaysStatusInfo day2 = new CalendarDaysStatusInfo();
    day2.setDate("2023-01-02");
    day2.setIsActive(false);
    calendarDays.add(day2);

    CalendarDaysStatusInfo day3 = new CalendarDaysStatusInfo();
    day3.setDate("2023-01-03");
    day3.setIsActive(true);
    calendarDays.add(day3);

    NodeCalendarCacheValue mockCalendarValue =
        NodeCalendarCacheValue.builder().calendarDaysStatusInfo(calendarDays).build();
    when(nodeCalendarNearCacheService.get(any(NodeCalendarCacheKey.class)))
        .thenReturn(mockCalendarValue);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals(3, response.getCalendarDays().size());
    assertEquals("2023-01-01", response.getCalendarDays().get(0).getDate());
    assertEquals(true, response.getCalendarDays().get(0).getIsActive());
    assertEquals("2023-01-02", response.getCalendarDays().get(1).getDate());
    assertEquals(false, response.getCalendarDays().get(1).getIsActive());
    assertEquals("2023-01-03", response.getCalendarDays().get(2).getDate());
    assertEquals(true, response.getCalendarDays().get(2).getIsActive());
  }

  @Test
  @DisplayName("Test null Node Data Cache response throws exception")
  void getNodeDataCacheValuewithNullResponsethrowsException() {
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

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId(request.getNodeId()).orgId(request.getOrgId()).build();
    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(null);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> inboundProcessingTimeService.evaluateInboundProcessingTime(request));

    assertEquals("Node data response is null.", exception.getMessage());
    verify(nodeDataNearCacheService).get(any(NodeDataCacheKey.class));
  }

  @Test
  @DisplayName("Test Node Data Cache retrieval with specific properties")
  void getNodeDataCacheValuewithSpecificWorkingTimeProperties() throws CommonServiceException {
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

    String startTime = "08:30";
    String endTime = "18:30";

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId(request.getNodeId()).orgId(request.getOrgId()).build();
    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime(startTime).lastWorkingTime(endTime).build();
    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(mockNodeData);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals(startTime, response.getStartWorkingTime());
    assertEquals(endTime, response.getLastWorkingTime());
    verify(nodeDataNearCacheService).get(expectedNodeDataKey);
  }

  @Test
  @DisplayName("Test Node Data Cache with empty working time values")
  void getNodeDataCacheValuewithEmptyWorkingTimeValues() throws CommonServiceException {
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

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId(request.getNodeId()).orgId(request.getOrgId()).build();
    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime("").lastWorkingTime("").build();
    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(mockNodeData);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    assertEquals("", response.getStartWorkingTime());
    assertEquals("", response.getLastWorkingTime());
    verify(nodeDataNearCacheService).get(expectedNodeDataKey);
  }

  @Test
  @DisplayName("Test Node Data Cache with null working time values")
  void getNodeDataCacheValuewithNullWorkingTimeValues() throws CommonServiceException {
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

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId(request.getNodeId()).orgId(request.getOrgId()).build();
    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime(null).lastWorkingTime(null).build();
    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(mockNodeData);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals(10.0, response.getInboundProcessingTime());
    Assertions.assertNull(response.getStartWorkingTime());
    Assertions.assertNull(response.getLastWorkingTime());
    verify(nodeDataNearCacheService).get(expectedNodeDataKey);
  }

  @Test
  @DisplayName("Test Node Data Cache call with different node and org IDs")
  void getNodeDataCacheValuewithDifferentNodeAndOrgIds() throws CommonServiceException {
    InboundProcessingTimeRequest request = TestUtils.createInboundProcessingRequest();
    request.setNodeId("specificNodeId");
    request.setOrgId("specificOrgId");

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

    NodeDataCacheKey expectedNodeDataKey =
        NodeDataCacheKey.builder().nodeId("specificNodeId").orgId("specificOrgId").build();

    NodeDataCacheValue mockNodeData =
        NodeDataCacheValue.builder().startWorkingTime("10:00").lastWorkingTime("20:00").build();

    when(nodeDataNearCacheService.get(expectedNodeDataKey)).thenReturn(mockNodeData);

    InboundProcessingTimeResponse response =
        inboundProcessingTimeService.evaluateInboundProcessingTime(request);

    assertNotNull(response);
    assertEquals("specificNodeId", response.getNodeId());
    assertEquals("specificOrgId", response.getOrgId());
    assertEquals("10:00", response.getStartWorkingTime());
    assertEquals("20:00", response.getLastWorkingTime());

    verify(nodeDataNearCacheService).get(expectedNodeDataKey);
  }
}
