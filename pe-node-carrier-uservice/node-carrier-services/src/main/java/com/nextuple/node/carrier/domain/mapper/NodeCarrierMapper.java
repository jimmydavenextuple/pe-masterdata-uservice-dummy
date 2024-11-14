/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.mapper;

import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
import com.nextuple.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCarrierMapper {

  NodeCarrierEntity nodeCarrierRequestToEntity(NodeCarrierRequest nodeCarrierRequest);

  NodeCarrierEntity nodeCarrierBufferRequestToEntity(
      NodeCarrierBufferRequest nodeCarrierBufferRequest);

  NodeCarrierResponse toNodeCarrierDto(NodeCarrierEntity nodeCarrierEntity);

  NodeCarrierEntity updateNodeCarrierEntity(
      NodeCarrierUpdateRequest nodeCarrierUpdateRequest,
      @MappingTarget NodeCarrierEntity nodeCarrierEntity);

  NodeCarrierEntity updateNodeCarrierEntityWithBuffer(
      NodeCarrierBufferRequest nodeCarrierBufferRequest,
      @MappingTarget NodeCarrierEntity nodeCarrierEntity);

  List<NodeCarrierResponse> toNodeCarrierResponseList(
      List<NodeCarrierEntity> nodeCarrierEntityList);

  NodeCarrierSelectionEntity nodeCarrierSelectionRequestToEntity(
      NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  NodeCarrierSelectionResponse toNodeCarrierSelectionResponse(
      NodeCarrierSelectionEntity saveNodeCarrierSelectionEntity);

  List<NodeCarrierSelectionResponse> toNodeCarrierSelectionResponseList(
      List<NodeCarrierSelectionEntity> nodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone);

  List<NodeCarrierListCacheKeyDto> toNodeCarrierListCacheKeyDto(
      List<NodeCarrierEntity> nodeCarrierEntityList);
}
