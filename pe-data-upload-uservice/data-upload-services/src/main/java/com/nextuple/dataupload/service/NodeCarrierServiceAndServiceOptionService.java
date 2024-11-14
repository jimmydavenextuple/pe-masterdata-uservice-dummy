/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.pojo.ActiveCombination;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceAndServiceOptionService {

  private final NodeFeign nodeFeign;
  @Autowired INodeCarrierFeign nodeCarrierFeign;

  @ReaderDS
  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeCarrierServiceAndServiceOptionResponse> responseList =
        nodeResponse.getData().stream()
            .map(this::createNodeCarrierServiceAndServiceOptionResponse)
            .collect(Collectors.toList());

    PagePayload<NodeCarrierServiceAndServiceOptionResponse> payload = new PagePayload<>();
    payload.setData(responseList);
    payload.setPagination(nodeResponse.getPagination());
    return payload;
  }

  private NodeCarrierServiceAndServiceOptionResponse
      createNodeCarrierServiceAndServiceOptionResponse(NodeDto nodeDto) {
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierFeign
            .getNodeCarrierListWithLastPickUpTimeDetails(nodeDto.getNodeId(), nodeDto.getOrgId())
            .getPayload();

    Set<String> carrierServiceIdsSet =
        nodeCarrierResponseList.stream()
            .map(NodeCarrierResponse::getCarrierServiceId)
            .collect(Collectors.toSet());

    Set<String> serviceOptionsSet =
        nodeCarrierResponseList.stream()
            .map(NodeCarrierResponse::getServiceOption)
            .collect(Collectors.toSet());

    List<ActiveCombination> activeCombinations =
        nodeCarrierResponseList.stream()
            .map(
                nodeCarrierResponse ->
                    ActiveCombination.builder()
                        .nodeId(nodeCarrierResponse.getNodeId())
                        .carrierServiceId(nodeCarrierResponse.getCarrierServiceId())
                        .serviceOption(nodeCarrierResponse.getServiceOption())
                        .isActive(true)
                        .build())
            .collect(Collectors.toList());

    return NodeCarrierServiceAndServiceOptionResponse.builder()
        .nodeId(nodeDto.getNodeId())
        .orgId(nodeDto.getOrgId())
        .street(nodeDto.getStreet())
        .city(nodeDto.getCity())
        .state(nodeDto.getState())
        .zipCode(nodeDto.getZipCode())
        .nodeType(nodeDto.getNodeType())
        .carrierServices(new ArrayList<>(carrierServiceIdsSet))
        .serviceOptions(new ArrayList<>(serviceOptionsSet))
        .activeCombination(activeCombinations)
        .build();
  }
}
