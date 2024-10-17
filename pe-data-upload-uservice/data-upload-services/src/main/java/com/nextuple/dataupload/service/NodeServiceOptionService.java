/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.util.CommonDashboardUtil.fetchEligibleNodeServiceOption;
import static com.nextuple.dataupload.util.CommonDashboardUtil.fetchNodeProcessingTimeForEligibleServiceOptions;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.domain.dto.NodeServiceOptionDto;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeServiceOptionService {
  private final NodeFeign nodeFeign;
  @Autowired INodeCarrierFeign nodeCarrierFeign;

  @ReaderDS
  public PagePayload<NodeServiceOptionDto> getNodeServiceOption(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
    PagePayload<NodeServiceOptionDto> nodeServiceOptionDtoPagePayload = new PagePayload<>();

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeDto> nodeResponseList = nodeResponse.getData();

    List<NodeServiceOptionDto> responseList = new ArrayList<>();
    for (NodeDto nodeDto : nodeResponseList) {
      responseList.add(getNodeServiceOptionDto(nodeDto));
    }

    nodeServiceOptionDtoPagePayload.setPagination(nodeResponse.getPagination());
    nodeServiceOptionDtoPagePayload.setData(responseList);

    return nodeServiceOptionDtoPagePayload;
  }

  private NodeServiceOptionDto getNodeServiceOptionDto(NodeDto node) {
    List<NodeCarrierResponse> nodeCarrierResponseWithCarrierServiceId =
        nodeCarrierFeign
            .getNodeCarrierListWithLastPickUpTimeDetails(node.getNodeId(), node.getOrgId())
            .getPayload();

    List<NodeCarrierResponse> nodeCarrierResponseWithCarrierServiceIdAsBlank =
        nodeCarrierFeign.getNodeCarrierList(node.getNodeId(), node.getOrgId()).getPayload();

    List<NodeCarrierResponse> combinedList =
        Stream.of(
                nodeCarrierResponseWithCarrierServiceId,
                nodeCarrierResponseWithCarrierServiceIdAsBlank)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    String[] validServiceOptions = fetchEligibleNodeServiceOption(node);

    return setNodeServiceOptionDto(node, combinedList, validServiceOptions);
  }

  private NodeServiceOptionDto setNodeServiceOptionDto(
      NodeDto node, List<NodeCarrierResponse> combinedList, String[] validServiceOptions) {

    return NodeServiceOptionDto.builder()
        .nodeId(node.getNodeId())
        .orgId(node.getOrgId())
        .nodeType(node.getNodeType())
        .street(node.getStreet())
        .city(node.getCity())
        .state(node.getState())
        .isActive(isActive(validServiceOptions))
        .serviceOptions(getDistinctServiceOptions(validServiceOptions))
        .processingTime(
            fetchNodeProcessingTimeForEligibleServiceOptions(combinedList, validServiceOptions))
        .build();
  }

  private Boolean isActive(String[] validServiceOptions) {
    return validServiceOptions.length == 0 ? Boolean.FALSE : Boolean.TRUE;
  }

  private List<String> getDistinctServiceOptions(String[] validServiceOptions) {
    return Arrays.asList(validServiceOptions);
  }
}
