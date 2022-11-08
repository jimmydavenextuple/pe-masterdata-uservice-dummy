package com.nextuple.sourcing.rule.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;

public interface SourcingRuleFeignService
    extends GenericFeignService<
        FetchPromiseSourcingRuleRequest, BaseResponse<FetchPromiseSourcingRuleResponse>> {}
