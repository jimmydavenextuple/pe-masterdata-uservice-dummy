package com.nextuple.csvdownload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
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
