package com.hbc.nodecarrier.cache.service;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;

public interface NodeCarrierListFeignService
    extends GenericFeignService<String, BaseResponse<List<NodeCarrierResponse>>> {}
