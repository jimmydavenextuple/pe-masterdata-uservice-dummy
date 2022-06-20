package com.nextuple.sourcing.rule.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.sun.tools.javac.util.List;
import java.util.Collections;
import java.util.Set;

public class TestUtil {
  public FetchPromiseSourcingRuleRequest getFetchPromiseSourcingRuleRequest(
      String orgId,
      List<String> serviceOptions,
      String destinationGeoZone,
      String allocationRuleId) {
    return FetchPromiseSourcingRuleRequest.builder()
        .allocationRuleId(allocationRuleId)
        .destinationGeoZone(destinationGeoZone)
        .orgId(orgId)
        .serviceOptions(serviceOptions)
        .build();
  }

  public ServiceOptionInfo getServiceOptionInfo(int priority, Set<String> sourceNodes) {
    return ServiceOptionInfo.builder().priority(priority).sourceNodes(sourceNodes).build();
  }

  public FetchPromiseSourcingRuleResponse getFetchPromiseSourcingRuleResponse() {
    return FetchPromiseSourcingRuleResponse.builder()
        .express(
            Collections.singletonList(getServiceOptionInfo(1, Collections.singleton("Node-1"))))
        .sdnd(Collections.singletonList(getServiceOptionInfo(1, Collections.singleton("Node-1"))))
        .standard(
            Collections.singletonList(getServiceOptionInfo(1, Collections.singleton("Node-1"))))
        .build();
  }

  public SourcingRuleCacheValue getSourcingRuleCacheValue() {
    return SourcingRuleCacheValue.builder()
        .fetchPromiseSourcingRuleResponse(getFetchPromiseSourcingRuleResponse())
        .build();
  }

  public SourcingRuleCacheKey getSourcingRuleCacheKey() {
    return SourcingRuleCacheKey.builder()
        .fetchPromiseSourcingRuleRequest(
            getFetchPromiseSourcingRuleRequest("ABC", List.of("SDND"), "AOA", "DEFAULT"))
        .build();
  }

  public BaseResponse<FetchPromiseSourcingRuleResponse>
      getBaseResponseOfFetchPromiseSourcingRuleResponse() {
    BaseResponse<FetchPromiseSourcingRuleResponse> response = new BaseResponse<>();
    response.setMessage("Promise Sourcing Rule fetched successfully!");
    response.setPayload(getFetchPromiseSourcingRuleResponse());
    return response;
  }
}
