package com.nextuple.nodecarrier.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.domain.node.NodeCarrierResponse;

public interface NodeCarrierFeignService
    extends GenericFeignService<String, BaseResponse<NodeCarrierResponse>> {}
