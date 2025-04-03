/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.node.carrier.consumer.mapper.NodeServiceOptionBufferBatchMapper;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionBufferFeign;
import com.nextuple.node.carrier.repository.NodeServiceOptionBufferRepository;
import java.util.Date;
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
public class NodeServiceOptionBufferBatchServiceImpl
    extends BatchService<NodeServiceOptionBufferFeedDto> {

  private final NodeServiceOptionBufferFeign nodeServiceOptionBufferFeign;
  private final NodeServiceOptionBufferRepository nodeServiceOptionBufferRepository;
  public static final NodeServiceOptionBufferBatchMapper INSTANCE =
      Mappers.getMapper(NodeServiceOptionBufferBatchMapper.class);
  private final TypeReference<BatchRequest<NodeServiceOptionBufferFeedDto>>
      nodeServiceOptionBufferTypeReference = new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.NODE_SERVICE_OPTION_BUFFER_FEED;
  }

  @Override
  public TypeReference<BatchRequest<NodeServiceOptionBufferFeedDto>> getTypeReference() {
    return nodeServiceOptionBufferTypeReference;
  }

  @Override
  public String createRecordImpl(NodeServiceOptionBufferFeedDto payload) {
    return nodeServiceOptionBufferFeign
        .createNodeServiceOptionBuffer(INSTANCE.toNodeServiceOptionBufferRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(NodeServiceOptionBufferFeedDto payload)
      throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(NodeServiceOptionBufferFeedDto payload) {
    return nodeServiceOptionBufferFeign
        .deleteNodeServiceOptionBuffer(INSTANCE.toNodeServiceOptionBufferDeleteRequest(payload))
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<NodeServiceOptionBufferFeedDto> nodeServiceOptionBufferBatchRequest)
      throws CommonServiceException {
    NodeServiceOptionBufferFeedDto nodeServiceOptionBufferFeedDto =
        nodeServiceOptionBufferBatchRequest.getPayload();
    String nodeId = nodeServiceOptionBufferFeedDto.getNodeId();
    String orgId = nodeServiceOptionBufferFeedDto.getOrgId();
    String serviceOption = nodeServiceOptionBufferFeedDto.getServiceOption();
    Date bufferStartDate = nodeServiceOptionBufferFeedDto.getBufferStartDate();
    Date bufferEndDate = nodeServiceOptionBufferFeedDto.getBufferEndDate();
    if (Objects.nonNull(nodeId)
        && Objects.nonNull(orgId)
        && Objects.nonNull(serviceOption)
        && Objects.nonNull(bufferEndDate)
        && Objects.nonNull(bufferStartDate)) {
      Optional<NodeServiceOptionBufferEntity> nodeServiceOptionBufferEntity =
          nodeServiceOptionBufferRepository
              .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                  orgId, nodeId, serviceOption, bufferStartDate, bufferEndDate);
      if (nodeServiceOptionBufferEntity.isPresent()
          && (nodeServiceOptionBufferEntity
              .get()
              .getLastModifiedDate()
              .after(nodeServiceOptionBufferBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeServiceOptionBufferBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeServiceOptionBufferEntity.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
