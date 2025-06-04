/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.pe.light.promise.inbound.InboundProcessingTimeRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingTimeResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {

  public static InboundProcessingTimeRequest createInboundProcessingRequest() {
    Map<String, Object> ruleEvaluationFacts = new HashMap<>();
    ruleEvaluationFacts.put("key", "value");
    return InboundProcessingTimeRequest.builder()
        .nodeId("node-123")
        .orgId("org-456")
        .ruleGroup("default-rule-group")
        .ruleFilterStrategy("inbound-processing-time-filter")
        .ruleEvaluationFacts(ruleEvaluationFacts)
        .build();
  }

  public static InboundProcessingTimeResponse createInboundProcessingResponse() {
    List<CalendarDaysStatusInfo> calendarDays =
        Collections.singletonList(
            CalendarDaysStatusInfo.builder().date("2023-01-01").isActive(true).build());

    return InboundProcessingTimeResponse.builder()
        .nodeId("node-123")
        .orgId("org-456")
        .startWorkingTime("09:00")
        .lastWorkingTime("18:00")
        .calendarDays(calendarDays)
        .inboundProcessingTime(10.0)
        .build();
  }
}
