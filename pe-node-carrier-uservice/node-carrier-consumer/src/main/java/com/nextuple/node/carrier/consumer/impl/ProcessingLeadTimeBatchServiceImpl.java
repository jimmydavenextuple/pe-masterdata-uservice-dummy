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
import com.nextuple.node.carrier.consumer.dto.ProcessingLeadTimeFeedDto;
import com.nextuple.node.carrier.consumer.mapper.ProcessingLeadTimeBatchMapper;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.feign.NodeServiceOptionsFeign;
import com.nextuple.node.carrier.repository.NodeServiceOptionRepository;
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
public class ProcessingLeadTimeBatchServiceImpl extends BatchService<ProcessingLeadTimeFeedDto> {

  private final NodeServiceOptionsFeign nodeServiceOptionFeign;
  private final NodeServiceOptionRepository nodeServiceOptionRepository;
  public static final ProcessingLeadTimeBatchMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeBatchMapper.class);
  private final TypeReference<BatchRequest<ProcessingLeadTimeFeedDto>>
      processingLeadTimeTypeReference = new TypeReference<>() {};

  @Override
  public TypeReference<BatchRequest<ProcessingLeadTimeFeedDto>> getTypeReference() {
    return processingLeadTimeTypeReference;
  }

  @Override
  public String createRecordImpl(ProcessingLeadTimeFeedDto payload) {
    return nodeServiceOptionFeign
        .createNodeServiceOption(INSTANCE.toNodeServiceOptionRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(ProcessingLeadTimeFeedDto payload) {
    return nodeServiceOptionFeign
        .updateNodeServiceOption(
            payload.getOrgId(),
            payload.getNodeId(),
            payload.getServiceOption(),
            INSTANCE.toNodeServiceOptionUpdateRequest(payload))
        .getMessage();
  }

  @Override
  public String deleteRecordImpl(ProcessingLeadTimeFeedDto payload) {
    return nodeServiceOptionFeign
        .deleteNodeServiceOption(
            payload.getOrgId(), payload.getNodeId(), payload.getServiceOption())
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<ProcessingLeadTimeFeedDto> processingLeadTimeBatchRequest)
      throws CommonServiceException {
    ProcessingLeadTimeFeedDto processingLeadTimeFeedDto =
        processingLeadTimeBatchRequest.getPayload();
    String nodeId = processingLeadTimeFeedDto.getNodeId();
    String orgId = processingLeadTimeFeedDto.getOrgId();
    String serviceOption = processingLeadTimeFeedDto.getServiceOption();
    if (Objects.nonNull(nodeId) && Objects.nonNull(orgId) && Objects.nonNull(serviceOption)) {
      Optional<NodeServiceOptionEntity> nodeServiceOptionEntity =
          nodeServiceOptionRepository.findByOrgIdAndNodeIdAndServiceOption(
              orgId, nodeId, serviceOption);
      if (nodeServiceOptionEntity.isPresent()
          && (nodeServiceOptionEntity
              .get()
              .getLastModifiedDate()
              .after(processingLeadTimeBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(processingLeadTimeBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(nodeServiceOptionEntity.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.PROCESSING_LEAD_TIME;
  }
}
