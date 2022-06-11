package com.nextuple.nodecarrier.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierResponse;

public interface NodeCarrierFeignService
    extends GenericFeignService<String, BaseResponse<NodeCarrierResponse>> {}
