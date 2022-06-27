package com.hbc.sourcing.rule.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;

public interface SourcingRuleFeignService
    extends GenericFeignService<
        FetchPromiseSourcingRuleRequest, BaseResponse<FetchPromiseSourcingRuleResponse>> {}
