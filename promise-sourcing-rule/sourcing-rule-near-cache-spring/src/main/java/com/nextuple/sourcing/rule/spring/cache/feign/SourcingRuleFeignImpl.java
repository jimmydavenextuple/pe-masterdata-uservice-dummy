package com.nextuple.sourcing.rule.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-promise-sourcing-rule",
    url =
        "${spring.application.dependencies.sourcing:http://pe-config-promise-sourcing-rule:8080/}")
public interface SourcingRuleFeignImpl
    extends GenericFeignService<
        FetchPromiseSourcingRuleRequest, BaseResponse<FetchPromiseSourcingRuleResponse>> {
  @PostMapping("/promise-sourcing-rule/fetch-rules")
  BaseResponse<FetchPromiseSourcingRuleResponse> get(
      @RequestBody FetchPromiseSourcingRuleRequest baseRequest);
}
