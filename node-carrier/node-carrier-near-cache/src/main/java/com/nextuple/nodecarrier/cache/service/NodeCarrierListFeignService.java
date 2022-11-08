package com.nextuple.nodecarrier.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;

public interface NodeCarrierListFeignService
    extends GenericFeignService<String, BaseResponse<List<NodeCarrierResponse>>> {}
