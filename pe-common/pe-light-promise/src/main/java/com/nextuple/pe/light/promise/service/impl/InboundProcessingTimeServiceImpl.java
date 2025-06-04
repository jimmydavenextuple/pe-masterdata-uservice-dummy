/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_FILTER;
import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_KEY;
import static com.nextuple.pe.light.promise.LightPromiseConstants.NODE_ID_KEY;
import static com.nextuple.pe.light.promise.LightPromiseConstants.ORG_ID_KEY;
import static com.nextuple.pe.light.promise.LightPromiseConstants.RULE_EVALUATION_FACTS_KEY;
import static com.nextuple.pe.light.promise.LightPromiseConstants.RULE_GROUP_KEY;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.service.NodeCalendarNearCacheService;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.cache.service.NodeDataNearCacheService;
import com.nextuple.pe.light.promise.inbound.InboundProcessingTimeRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingTimeResponse;
import com.nextuple.pe.light.promise.pojo.InboundNodeCalendar;
import com.nextuple.pe.light.promise.service.InboundProcessingTimeService;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import com.nextuple.rulecraft.engine.model.ResourceTagEvalRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundProcessingTimeServiceImpl implements InboundProcessingTimeService {

  private final RuleEngineApi ruleEngineApi;
  @Autowired NodeDataNearCacheService nodeDataNearCacheService;
  @Autowired NodeCalendarNearCacheService nodeCalendarNearCacheService;

  /**
   * Evaluates the inbound processing time based on the provided request.
   *
   * @param inboundProcessingTimeRequest The request containing details for evaluation.
   * @return The response containing the evaluated inbound processing time.
   * @throws CommonServiceException If validation or processing fails.
   */
  @Override
  public InboundProcessingTimeResponse evaluateInboundProcessingTime(
      InboundProcessingTimeRequest inboundProcessingTimeRequest) throws CommonServiceException {

    Double inboundProcessingTime = getInboundProcessingTime(inboundProcessingTimeRequest);
    NodeDataCacheValue nodeData = getNodeDataCacheValue(inboundProcessingTimeRequest);
    List<InboundNodeCalendar> calendarDays =
        getNodeCalendarCacheValue(inboundProcessingTimeRequest);

    return InboundProcessingTimeResponse.builder()
        .nodeId(inboundProcessingTimeRequest.getNodeId())
        .orgId(inboundProcessingTimeRequest.getOrgId())
        .inboundProcessingTime(inboundProcessingTime)
        .calendarDays(calendarDays)
        .startWorkingTime(nodeData.getStartWorkingTime())
        .lastWorkingTime(nodeData.getLastWorkingTime())
        .build();
  }

  /**
   * Retrieves the inbound processing time by validating the request and invoking the rule engine.
   *
   * @param inboundProcessingTimeRequest The request containing details for evaluation.
   * @return The evaluated inbound processing time.
   * @throws CommonServiceException If validation fails or the rule engine response is invalid.
   */
  private Double getInboundProcessingTime(InboundProcessingTimeRequest inboundProcessingTimeRequest)
      throws CommonServiceException {

    validateInboundProcessingRequest(inboundProcessingTimeRequest);
    Double inboundProcessingTime = null;
    String tenantId = inboundProcessingTimeRequest.getOrgId();
    String ruleGroup = inboundProcessingTimeRequest.getRuleGroup();

    Map<String, Object> facts = inboundProcessingTimeRequest.getRuleEvaluationFacts();
    facts.put(ORG_ID_KEY, tenantId);
    if (inboundProcessingTimeRequest.getRuleFilterStrategy() == null
        || inboundProcessingTimeRequest.getRuleFilterStrategy().isBlank()) {

      inboundProcessingTimeRequest.setRuleFilterStrategy(INBOUND_PROCESSING_TIME_FILTER);
    }
    ResourceTagEvalRequest resourceTagEvalRequest = new ResourceTagEvalRequest(facts, facts);
    Map<String, Object> mapResponse =
        ruleEngineApi.evaluateRules(
            tenantId,
            ruleGroup,
            List.of(inboundProcessingTimeRequest.getRuleFilterStrategy()),
            resourceTagEvalRequest);

    if (mapResponse.get(INBOUND_PROCESSING_TIME_KEY) != null) {
      Object inboundProcessingTimeObj = mapResponse.get(INBOUND_PROCESSING_TIME_KEY);
      if (inboundProcessingTimeObj instanceof Number number) {
        inboundProcessingTime = number.doubleValue();
        log.debug("Inbound processing time response: {}", inboundProcessingTime);
        return inboundProcessingTime;
      } else {
        log.debug("Inbound processing time key is not a valid number.");
        throw new CommonServiceException(
            "Inbound processing time key is not a valid number.",
            HttpStatus.BAD_REQUEST,
            400,
            Map.of("inboundProcessingTime", new FieldError()));
      }
    } else {
      log.debug("Inbound processing time key is null in the response.");
      throw new CommonServiceException(
          "Inbound processing time key is null in the response.",
          HttpStatus.BAD_REQUEST,
          400,
          Map.of("inboundProcessingTime", new FieldError()));
    }
  }

  /**
   * Validates the inbound processing time request to ensure all required fields are present and
   * valid.
   *
   * @param inboundProcessingTimeRequest The request to validate.
   * @throws CommonServiceException If any validation fails.
   */
  private void validateInboundProcessingRequest(
      InboundProcessingTimeRequest inboundProcessingTimeRequest) throws CommonServiceException {
    validateField(inboundProcessingTimeRequest.getNodeId(), NODE_ID_KEY, "nodeId can't be blank.");
    validateField(inboundProcessingTimeRequest.getOrgId(), ORG_ID_KEY, "orgId can't be blank.");
    validateField(
        inboundProcessingTimeRequest.getRuleGroup(), RULE_GROUP_KEY, "ruleGroup can't be blank.");
    validateMap(
        inboundProcessingTimeRequest.getRuleEvaluationFacts(),
        RULE_EVALUATION_FACTS_KEY,
        "ruleEvaluationFacts can't be null.");
  }

  private void validateField(String fieldValue, String fieldName, String errorMessage)
      throws CommonServiceException {
    if (Objects.isNull(fieldValue) || fieldValue.isBlank()) {
      log.debug("Validation failed: {}", errorMessage);
      throw new CommonServiceException(
          "Validation failed: " + errorMessage,
          HttpStatus.BAD_REQUEST,
          400,
          Map.of(fieldName, new FieldError()));
    }
  }

  private void validateMap(Map<String, Object> map, String fieldName, String errorMessage)
      throws CommonServiceException {
    if (Objects.isNull(map)) {
      log.debug("Validation failed: {}", errorMessage);
      throw new CommonServiceException(
          "Validation failed: " + errorMessage,
          HttpStatus.BAD_REQUEST,
          400,
          Map.of(fieldName, new FieldError()));
    }
  }

  /**
   * Retrieves the node data cache value for the given request.
   *
   * @param request The inbound processing time request.
   * @return The NodeDataCacheValue for the requested node.
   */
  private NodeDataCacheValue getNodeDataCacheValue(InboundProcessingTimeRequest request)
      throws CommonServiceException {
    NodeDataCacheValue nodeDataCacheValue =
        nodeDataNearCacheService.get(
            NodeDataCacheKey.builder()
                .nodeId(request.getNodeId())
                .orgId(request.getOrgId())
                .build());

    if (nodeDataCacheValue == null) {
      throw new CommonServiceException(
          "Node data response is null.",
          HttpStatus.BAD_REQUEST,
          404,
          Map.of("nodeDataCacheValue", new FieldError()));
    }

    return nodeDataCacheValue;
  }

  /**
   * Retrieves and validates the node calendar cache value for the given request.
   *
   * @param request The inbound processing time request.
   * @return The validated node calendar cache value.
   * @throws CommonServiceException If the node calendar response is null.
   */
  private List<InboundNodeCalendar> getNodeCalendarCacheValue(InboundProcessingTimeRequest request)
      throws CommonServiceException {
    var nodeCalendarCacheKey =
        NodeCalendarCacheKey.builder()
            .nodeId(request.getNodeId())
            .orgId(request.getOrgId())
            .fromDate(request.getRequestDate())
            .build();

    var nodeCalendarCacheValue = nodeCalendarNearCacheService.get(nodeCalendarCacheKey);
    if (nodeCalendarCacheValue.getCalendarDaysStatusInfo() == null) {
      throw new CommonServiceException(
          "Node calender response is null.",
          HttpStatus.BAD_REQUEST,
          404,
          Map.of("nodeCalendarCacheValue", new FieldError()));
    }
    return nodeCalendarCacheValue.getCalendarDaysStatusInfo().stream()
        .map(
            info ->
                InboundNodeCalendar.builder()
                    .date(info.getDate())
                    .isActive(info.getIsActive())
                    .build())
        .collect(Collectors.toList());
  }
}
