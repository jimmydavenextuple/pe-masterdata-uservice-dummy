package com.nextuple.sourcing.rule.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleRequest;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "promising-engine-uservice",
    url = "${spring.application.dependencies.sourcing:http://localhost:8081/}")
public interface SourcingRuleFeignImpl
    extends GenericFeignService<
        FetchPromiseSourcingRuleRequest, BaseResponse<FetchPromiseSourcingRuleResponse>> {
  @PostMapping("/promiseSourcingRule/fetchRules")
  BaseResponse<FetchPromiseSourcingRuleResponse> get(
      @RequestBody FetchPromiseSourcingRuleRequest baseRequest);
}
