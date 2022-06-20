package com.nextuple.sourcing.rule.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleRequest;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleResponse;

public interface SourcingRuleFeignService
    extends GenericFeignService<
        FetchPromiseSourcingRuleRequest, BaseResponse<FetchPromiseSourcingRuleResponse>> {}
