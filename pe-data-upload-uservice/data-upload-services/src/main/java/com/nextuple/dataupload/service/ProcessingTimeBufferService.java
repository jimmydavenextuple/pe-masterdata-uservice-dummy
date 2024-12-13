/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;
import static com.nextuple.dataupload.util.CommonDashboardUtil.fetchEligibleNodeServiceOption;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingTimeBufferService {

  private static final String ACTIVE = "Active";
  private static final String INACTIVE = "Inactive";
  private final NodeFeign nodeFeign;
  private final PageProperties pageProperties;
  private final INodeCarrierFeign nodeCarrierFeign;

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBuffers(
      String orgId, String nodeIds, PageParams pageParams) {
    Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    String sortBy = pageParams.getSortBy().orElse(NODE_DEFAULT_SORT_BY);
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();
    List<ProcessingTimeBufferResponse> responseList = new ArrayList<>();

    PagePayload<NodeDto> nodeDtoPagePayload =
        ObjectUtils.isEmpty(nodeIds)
            ? nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload()
            : nodeFeign
                .getNodeListV2(orgId, nodeIds, null, pageNo, pageSize, sortBy, sortOrder)
                .getPayload();

    List<NodeDto> nodeDtoList = nodeDtoPagePayload.getData();

    nodeDtoList.forEach(
        nodeDto -> {
          List<String> validServiceOptions = Arrays.asList(fetchEligibleNodeServiceOption(nodeDto));

          responseList.add(setProcessingTimeBuffers(nodeDto, validServiceOptions));
        });

    processingTimeBufferDtoPagePayload.setPagination(nodeDtoPagePayload.getPagination());
    processingTimeBufferDtoPagePayload.setData(responseList);

    return processingTimeBufferDtoPagePayload;
  }

  private ProcessingTimeBufferResponse setProcessingTimeBuffers(
      NodeDto nodeDto, List<String> validServiceOptions) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeDto.getNodeId())
        .orgId(nodeDto.getOrgId())
        .nodeType(nodeDto.getNodeType())
        .street(nodeDto.getStreet())
        .city(nodeDto.getCity())
        .state(nodeDto.getState())
        .zipCode(nodeDto.getZipCode())
        .serviceOptions(validServiceOptions)
        .processingTimeBuffers(getProcessingTimeBufferList(nodeDto, validServiceOptions))
        .build();
  }

  private List<ProcessingTimeBuffer> getProcessingTimeBufferList(
      NodeDto nodeDto, List<String> validServiceOptions) {
    return validServiceOptions.stream()
        .flatMap(
            serviceOption -> {
              List<NodeCarrierResponse> nodeCarrierResponses =
                  nodeCarrierFeign
                      .getBuffersByOrgIdAndNodeIdAndServiceOption(
                          nodeDto.getOrgId(), nodeDto.getNodeId(), serviceOption)
                      .getPayload();

              if (nodeCarrierResponses.isEmpty()) {
                return Stream.of(createProcessingTimeBuffer(serviceOption, null, null, null, null));
              } else {
                return nodeCarrierResponses.stream()
                    .map(
                        bufferResponse ->
                            createProcessingTimeBuffer(
                                bufferResponse.getServiceOption(),
                                bufferResponse.getBufferHours(),
                                bufferResponse.getBufferStartDate(),
                                bufferResponse.getBufferEndDate(),
                                computeStatus(bufferResponse)));
              }
            })
        .toList();
  }

  private ProcessingTimeBuffer createProcessingTimeBuffer(
      String serviceOption,
      Double bufferHours,
      Date bufferStartDate,
      Date bufferEndDate,
      String status) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(bufferHours);
    processingTimeBuffer.setBufferStartDate(bufferStartDate);
    processingTimeBuffer.setBufferEndDate(bufferEndDate);
    processingTimeBuffer.setStatus(status);
    return processingTimeBuffer;
  }

  private String computeStatus(NodeCarrierResponse nodeServiceResponse) {
    var currentDate = new Date();
    if (nodeServiceResponse.getBufferHours() != null
        && nodeServiceResponse.getBufferStartDate() != null
        && nodeServiceResponse.getBufferEndDate() != null) {
      if (currentDate.compareTo(nodeServiceResponse.getBufferStartDate()) >= 0
          && currentDate.compareTo(nodeServiceResponse.getBufferEndDate()) <= 0) return ACTIVE;
      else return INACTIVE;
    }
    return null;
  }
}
