package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierService {
  private final NodeCarrierFeign nodeCarrierFeign;

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierService.class);

  public List<NodeCarrierResponse> getNodeCarrierList(String orgId) {
    logger.debug("Processing get Node Carrier service by orgId");

    BaseResponse<List<NodeCarrierResponse>> response =
        nodeCarrierFeign.getAllNodeCarriersByOrgId(orgId);

    return response.getPayload();
  }
}
