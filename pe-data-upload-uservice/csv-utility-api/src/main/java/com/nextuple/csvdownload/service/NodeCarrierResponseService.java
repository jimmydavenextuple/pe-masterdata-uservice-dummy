/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class NodeCarrierResponseService {

  private final Logger logger = LoggerFactory.getLogger(NodeCarrierResponseService.class);

  @Autowired INodeCarrierFeign nodeCarrierFeign;

  public List<NodeCarrierResponse> getNodeCarrierResponse(String nodeId, String orgId) {
    logger.debug("Processing get node carriers by nodeId and orgId");

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierResponse =
        nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(nodeId, orgId);

    return nodeCarrierResponse.getPayload();
  }

  public List<NodeCarrierResponse> getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId) {
    logger.debug("Processing get node carriers by nodeId and orgId");
    BaseResponse<List<NodeCarrierResponse>> nodeCarrierResponse =
        nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            orgId, nodeId, carrierServiceId);
    if (nodeCarrierResponse != null && !CollectionUtils.isEmpty(nodeCarrierResponse.getPayload())) {
      return nodeCarrierResponse.getPayload();
    } else {
      return Collections.emptyList();
    }
  }
}
