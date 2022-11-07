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

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierService.class);

  private final NodeCarrierFeign nodeCarrierFeign;

  public List<NodeCarrierResponse> getNodeCarrierResponse(String nodeId, String orgId) {
    logger.debug("Processing get node carriers by nodeId and orgId");

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierResponse =
        nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(nodeId, orgId);

    return nodeCarrierResponse.getPayload();
  }
}
