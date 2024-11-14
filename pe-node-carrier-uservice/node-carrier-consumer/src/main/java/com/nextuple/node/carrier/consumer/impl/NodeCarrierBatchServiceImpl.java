/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.node.carrier.consumer.dto.NodeCarrierFeedDto;
import com.nextuple.node.carrier.consumer.mapper.NodeCarrierBatchMapper;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.feign.NodeCarriersFeign;
import com.nextuple.node.carrier.repository.NodeCarriersRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierBatchServiceImpl extends BatchService<NodeCarrierFeedDto> {

  private final NodeCarriersFeign nodeCarrierFeign;
  private final NodeCarriersRepository nodeCarrierRepository;
  public static final NodeCarrierBatchMapper INSTANCE =
      Mappers.getMapper(NodeCarrierBatchMapper.class);
  private final TypeReference<BatchRequest<NodeCarrierFeedDto>> nodeCarrierTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.NODE_CARRIER_FEED;
  }

  @Override
  public TypeReference<BatchRequest<NodeCarrierFeedDto>> getTypeReference() {
    return nodeCarrierTypeReference;
  }

  @Override
  public String createRecordImpl(NodeCarrierFeedDto payload) {
    return nodeCarrierFeign.createNodeCarrier(INSTANCE.toNodeCarrierRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(NodeCarrierFeedDto payload) {
    return nodeCarrierFeign
        .updateNodeCarrier(
            payload.getOrgId(),
            payload.getNodeId(),
            payload.getCarrierServiceId(),
            payload.getServiceOption(),
            INSTANCE.toNodeCarrierUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(NodeCarrierFeedDto payload) {
    return nodeCarrierFeign
        .deleteNodeCarrier(
            payload.getOrgId(),
            payload.getNodeId(),
            payload.getCarrierServiceId(),
            payload.getServiceOption())
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<NodeCarrierFeedDto> nodeCarrierBatchRequest)
      throws CommonServiceException {
    NodeCarrierFeedDto nodeCarrierDto = nodeCarrierBatchRequest.getPayload();
    String nodeId = nodeCarrierDto.getNodeId();
    String orgId = nodeCarrierDto.getOrgId();
    String carrierServiceId = nodeCarrierDto.getCarrierServiceId();
    String serviceOption = nodeCarrierDto.getServiceOption();
    if (Objects.nonNull(nodeId)
        && Objects.nonNull(orgId)
        && Objects.nonNull(carrierServiceId)
        && Objects.nonNull(serviceOption)) {
      Optional<NodeCarriersEntity> nodeCarrierEntity =
          nodeCarrierRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
              orgId, nodeId, carrierServiceId, serviceOption);
      if (nodeCarrierEntity.isPresent()
          && (nodeCarrierEntity
              .get()
              .getLastModifiedDate()
              .after(nodeCarrierBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeCarrierBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeCarrierEntity.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
