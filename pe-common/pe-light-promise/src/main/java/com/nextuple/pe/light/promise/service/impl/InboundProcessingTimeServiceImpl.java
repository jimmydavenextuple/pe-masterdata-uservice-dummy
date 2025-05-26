/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import com.nextuple.pe.light.promise.inbound.InboundProcessingRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingResponse;
import com.nextuple.pe.light.promise.service.InboundProcessingTimeService;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundProcessingTimeServiceImpl implements InboundProcessingTimeService {

  private final RuleEngineApi ruleEngineApi;

  /** For initial testing only — logic will be revised. */
  @Override
  public InboundProcessingResponse evaluateInboundProcessingTime(
      InboundProcessingRequest inboundProcessingRequest) {

    String ruleGroup = inboundProcessingRequest.getRuleGroup();
    Object facts = inboundProcessingRequest.getRuleEvaluationRequest();

    Map<String, Object> mapResponse = ruleEngineApi.evaluateRules(ruleGroup, facts);
    InboundProcessingResponse inboundProcessingResponse =
        InboundProcessingResponse.builder().build();
    inboundProcessingResponse.setInbound(mapResponse);
    return inboundProcessingResponse;
  }
}
