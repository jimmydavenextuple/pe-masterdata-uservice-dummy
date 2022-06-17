package com.nextuple.node.data.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.domain.outbound.NodeResponse;

public interface NodeDataFeignService
    extends GenericFeignService<String, BaseResponse<NodeResponse>> {}
