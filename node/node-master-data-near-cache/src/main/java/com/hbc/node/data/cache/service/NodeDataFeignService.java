package com.hbc.node.data.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.node.domain.outbound.NodeResponse;

public interface NodeDataFeignService
    extends GenericFeignService<String, BaseResponse<NodeResponse>> {}
