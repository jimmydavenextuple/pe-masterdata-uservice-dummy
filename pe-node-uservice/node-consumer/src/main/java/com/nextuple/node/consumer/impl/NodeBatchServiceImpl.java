/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.consumer.mapper.NodeBatchMapper;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.exception.NodeDomainException;
import com.nextuple.node.persistence.service.NodePersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeBatchServiceImpl extends BatchService<NodeFeedDto> {

  private final NodeFeign nodeFeign;
  private final NodePersistenceService nodePersistenceService;
  public static final NodeBatchMapper INSTANCE = Mappers.getMapper(NodeBatchMapper.class);
  private final TypeReference<BatchRequest<NodeFeedDto>> nodeTypeReference =
      new TypeReference<>() {};

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.NODE_FEED;
  }

  @Override
  public TypeReference<BatchRequest<NodeFeedDto>> getTypeReference() {
    return nodeTypeReference;
  }

  @Override
  public String createRecordImpl(NodeFeedDto payload) {
    return nodeFeign.createNode(INSTANCE.toNodeRequest(payload)).getMessage();
  }

  @Override
  public String updateRecordImpl(NodeFeedDto payload) {
    return nodeFeign
        .updateNodeDetails(
            payload.getNodeId(), payload.getOrgId(), INSTANCE.toNodeUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(NodeFeedDto payload) {
    return nodeFeign.deleteNode(payload.getNodeId(), payload.getOrgId()).getMessage();
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<NodeFeedDto> nodeBatchRequest)
      throws CommonServiceException {
    NodeFeedDto nodeDto = nodeBatchRequest.getPayload();
    String nodeId = nodeDto.getNodeId();
    String orgId = nodeDto.getOrgId();
    if (Objects.nonNull(nodeId) && Objects.nonNull(orgId)) {
      try {
        Optional<NodeDomainDto> nodeDomainDto =
            nodePersistenceService.findNodeByNodeIdAndOrgId(nodeId, orgId);
        if (nodeDomainDto.isPresent()
            && (nodeDomainDto
                .get()
                .getLastModifiedDate()
                .after(nodeBatchRequest.getReceivedTimestamp()))) {
          Map<String, FieldError> errorMap = new HashMap<>();
          errorMap.put(
              "receivedTimestamp",
              FieldError.builder().rejectedValue(nodeBatchRequest.getReceivedTimestamp()).build());
          errorMap.put(
              "lastUpdatedTimestamp",
              FieldError.builder()
                  .rejectedValue(nodeDomainDto.get().getLastModifiedDate())
                  .build());
          throw new CommonServiceException(
              "Can't process the record as it's outdated",
              HttpStatus.BAD_REQUEST,
              0x1771,
              errorMap);
        }
      } catch (NodeDomainException e) {
        log.debug(
            "Cannot check for outdated record as the given node does not exist for the given details orgId:{} nodeId:{}",
            orgId,
            nodeId);
      }
    }
  }
}
