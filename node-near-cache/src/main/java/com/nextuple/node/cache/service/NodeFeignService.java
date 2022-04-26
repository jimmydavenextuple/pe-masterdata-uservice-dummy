package com.nextuple.node.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.node.cache.domain.NodeValidationRequest;

public interface NodeFeignService
    extends GenericFeignService<NodeValidationRequest, BaseResponse<NodeValidationResponse>> {}
