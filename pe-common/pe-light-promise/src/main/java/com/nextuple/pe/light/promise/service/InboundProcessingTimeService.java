package com.nextuple.pe.light.promise.service;


import com.nextuple.pe.light.promise.inbound.InboundProcessingRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingResponse;

import java.util.Map;

public interface InboundProcessingTimeService {



    InboundProcessingResponse evaluateInboundProcessingTime(InboundProcessingRequest inboundProcessingRequest);

    Map<String, Object> lpEvaluateRules(String ruleGroup, Map<String, Object> facts);

}
