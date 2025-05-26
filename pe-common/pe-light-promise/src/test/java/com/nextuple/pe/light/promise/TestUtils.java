package com.nextuple.pe.light.promise;

import com.nextuple.pe.light.promise.inbound.InboundProcessingRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingResponse;
import com.nextuple.pe.light.promise.pojo.CalendarDaysStatus;
import java.util.Collections;
import java.util.List;

public class TestUtils {

  public static InboundProcessingRequest createInboundProcessingRequest() {
    return InboundProcessingRequest.builder()
        .nodeId("node-123")
        .orgId("org-456")
        .ruleGroup("default-rule-group")
        .ruleFilterStrategy("inbound-processing-time-filter")
        .ruleEvaluationRequest(Collections.singletonMap("key", "value"))
        .build();
  }

  public static InboundProcessingResponse createInboundProcessingResponse() {
    List<CalendarDaysStatus> calendarDays =
        Collections.singletonList(
            CalendarDaysStatus.builder().date("2023-01-01").isActive(true).build());

    return InboundProcessingResponse.builder()
        .nodeId("node-123")
        .orgId("org-456")
        .startWorkingTime("09:00")
        .lastWorkingTime("18:00")
        .calendarDays(calendarDays)
        .inbound(Collections.singletonMap("processingTime", 10.0))
        .build();
  }
}
