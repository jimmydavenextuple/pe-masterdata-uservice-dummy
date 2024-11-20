/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.mapper;

import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodePriorityInfoV1;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodePriorityDomainDto;
import java.util.List;
import org.mapstruct.*;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodePriorityMapper {

  NodePriorityDomainDto toNodePriorityEntity(NodePriorityRequest nodePriorityRequest);

  NodePriorityResponse toNodePriorityResponse(NodePriorityDomainDto nodePriorityEntity);

  List<NodePriorityResponse> toNodePriorityResponseList(
      List<NodePriorityDomainDto> nodePriorityEntities);

  void updateNodePriorityEntity(
      NodePriorityUpdationRequest nodePriorityUpdationRequest,
      @MappingTarget NodePriorityDomainDto nodePriorityEntity);

  @Mapping(target = "sequence", source = "nodePriorityDomainDto.priority")
  NodePriorityInfoV1 toNodePriorityInfo(NodePriorityDomainDto nodePriorityDomainDto);

  List<NodePriorityInfoV1> toNodePriorityInfo(List<NodePriorityDomainDto> nodePriorityDomainDto);
}
