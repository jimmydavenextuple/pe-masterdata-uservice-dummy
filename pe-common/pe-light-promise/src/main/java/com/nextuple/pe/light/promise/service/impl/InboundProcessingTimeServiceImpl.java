package com.nextuple.pe.light.promise.service.impl;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.cache.service.NodeDataNearCacheService;
import com.nextuple.pe.light.promise.inbound.InboundProcessingRequest;
import com.nextuple.pe.light.promise.outbound.InboundProcessingResponse;
import com.nextuple.pe.light.promise.service.InboundProcessingTimeService;
import com.nextuple.rulecraft.engine.api.RuleEngineApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundProcessingTimeServiceImpl implements InboundProcessingTimeService {

  private final RuleEngineApi ruleEngineApi;

  private final NodeDataNearCacheService nodeDataNearCacheService;


  @Override
  public Map<String, Object> lpEvaluateRules(String ruleGroup, Map<String, Object> facts)
  {
    return ruleEngineApi.evaluateRules(ruleGroup,facts);
  }

  @Override
  public InboundProcessingResponse evaluateInboundProcessingTime(InboundProcessingRequest inboundProcessingRequest)
  {

    String ruleGroup= inboundProcessingRequest.getRuleGroup();
    Object facts= inboundProcessingRequest.getRuleEvaluationRequest();

    Map<String, Object> mapResponse= ruleEngineApi.evaluateRules(ruleGroup,facts);

    NodeDataCacheValue nodeDataCacheValue =
            nodeDataNearCacheService.get(
                    NodeDataCacheKey.builder()
                            .nodeId(inboundProcessingRequest.getNodeId())
                            .orgId(inboundProcessingRequest.getOrgId())
                            .build());

    InboundProcessingResponse inboundProcessingResponse= InboundProcessingResponse.builder().build();
    inboundProcessingResponse.setInbound(nodeDataCacheValue);

    return inboundProcessingResponse;
  }


}
