/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_FILTER;
import static com.nextuple.pe.light.promise.LightPromiseConstants.INBOUND_PROCESSING_TIME_KEY;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.pe.light.promise.inbound.InboundProcessingTimeRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingTimeResponse;
import com.nextuple.pe.light.promise.service.InboundProcessingTimeService;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import com.nextuple.rulecraft.engine.model.ResourceTagEvalRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundProcessingTimeServiceImpl implements InboundProcessingTimeService {

  private final RuleEngineApi ruleEngineApi;

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
    return InboundProcessingTimeResponse.builder()
        .nodeId(inboundProcessingTimeRequest.getNodeId())
        .orgId(inboundProcessingTimeRequest.getOrgId())
        .inboundProcessingTime(inboundProcessingTime)
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
          404,
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
    if (inboundProcessingTimeRequest.getNodeId() == null
        || inboundProcessingTimeRequest.getNodeId().isBlank()) {
      log.debug("Validation failed: nodeId can't be blank.");
      throw new CommonServiceException(
          "Validation failed: nodeId can't be blank.",
          HttpStatus.BAD_REQUEST,
          400,
          Map.of("nodeId", new FieldError()));
    }

    if (inboundProcessingTimeRequest.getOrgId() == null
        || inboundProcessingTimeRequest.getOrgId().isBlank()) {
      log.debug("Validation failed: orgId can't be blank.");
      throw new CommonServiceException(
          "Validation failed: orgId can't be blank.",
          HttpStatus.BAD_REQUEST,
          400,
          Map.of("orgId", new FieldError()));
    }

    if (inboundProcessingTimeRequest.getRuleGroup() == null
        || inboundProcessingTimeRequest.getRuleGroup().isBlank()) {
      log.debug("Validation failed: ruleGroup can't be blank.");
      throw new CommonServiceException(
          "Validation failed: ruleGroup can't be blank.",
          HttpStatus.BAD_REQUEST,
          400,
          Map.of("ruleGroup", new FieldError()));
    }

    if (inboundProcessingTimeRequest.getRuleEvaluationFacts() == null) {
      log.debug("Validation failed: ruleEvaluationFacts can't be null.");
      throw new CommonServiceException(
          "Validation failed: ruleEvaluationFacts can't be null.",
          HttpStatus.BAD_REQUEST,
          400,
          Map.of("ruleEvaluationFacts", new FieldError()));
    }
  }
}
