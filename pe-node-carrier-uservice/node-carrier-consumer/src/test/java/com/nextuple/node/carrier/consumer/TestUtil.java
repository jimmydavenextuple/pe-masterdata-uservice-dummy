/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.consumer;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.node.carrier.consumer.dto.NodeCarrierFeedDto;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.node.carrier.consumer.dto.ProcessingLeadTimeFeedDto;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import java.util.Date;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service-1";
  public static final String SERVICE_OPTION = "STANDARD";
  public static final String LAST_PICKUP_TIME = "23:00";
  public static final Double BUFFER_HOURS = 2.0;
  public static final Double PROCESSING_TIME = 2.0;

  public NodeCarrierFeedDto createNodeCarrierFeedDto() {
    return NodeCarrierFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICKUP_TIME)
        .build();
  }

  public NodeServiceOptionBufferFeedDto createNodeServiceOptionBufferFeedDto() {
    return NodeServiceOptionBufferFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours(BUFFER_HOURS)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public ProcessingLeadTimeFeedDto createProcessingLeadTimeFeedDto() {
    return ProcessingLeadTimeFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public BatchRequest<NodeCarrierFeedDto> getNodeCarrierFeedRequest(ActionEnum action) {
    BatchRequest<NodeCarrierFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeCarrierFeedDto());
    return batchRequest;
  }

  public BatchRequest<NodeServiceOptionBufferFeedDto> getNodeServiceOptionBufferFeedRequest(
      ActionEnum action) {
    BatchRequest<NodeServiceOptionBufferFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeServiceOptionBufferFeedDto());
    return batchRequest;
  }

  public BatchRequest<ProcessingLeadTimeFeedDto> getProcessingLeadTimeFeedRequest(
      ActionEnum action) {
    BatchRequest<ProcessingLeadTimeFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createProcessingLeadTimeFeedDto());
    return batchRequest;
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getBatchApiResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BaseResponse<NodeCarriersResponse> getBaseResponseOfNodeCarrierFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public NodeCarriersResponse getNodeCarrierResponse() {
    return NodeCarriersResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICKUP_TIME)
        .build();
  }

  public NodeServiceOptionBufferResponse getNodeServiceOptionBufferResponse() {
    return NodeServiceOptionBufferResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferEndDate(new Date())
        .bufferStartDate(new Date())
        .bufferHours(BUFFER_HOURS)
        .build();
  }

  public NodeServiceOptionResponse getNodeServiceOptionResponse() {
    return NodeServiceOptionResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .build();
  }

  public BaseResponse<NodeServiceOptionBufferResponse> getBaseResponseOfNodeServiceOptionBufferFeed(
      String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getNodeServiceOptionBufferResponse())
        .build();
  }

  public BaseResponse<NodeServiceOptionResponse> getBaseResponseOfNodeServiceOptionFeed(
      String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getNodeServiceOptionResponse())
        .build();
  }

  public NodeCarriersEntity getNodeCarriersEntity() {
    NodeCarriersEntity nodeCarriersEntity = new NodeCarriersEntity();
    nodeCarriersEntity.setOrgId(ORG_ID);
    nodeCarriersEntity.setNodeId(NODE_ID);
    nodeCarriersEntity.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarriersEntity.setServiceOption(SERVICE_OPTION);
    nodeCarriersEntity.setLastPickupTime(LAST_PICKUP_TIME);
    return nodeCarriersEntity;
  }

  public NodeServiceOptionEntity getNodeServiceOptionEntity() {
    NodeServiceOptionEntity nodeServiceOptionEntity = new NodeServiceOptionEntity();
    nodeServiceOptionEntity.setOrgId(ORG_ID);
    nodeServiceOptionEntity.setNodeId(NODE_ID);
    nodeServiceOptionEntity.setServiceOption(SERVICE_OPTION);
    nodeServiceOptionEntity.setProcessingTime(PROCESSING_TIME);
    return nodeServiceOptionEntity;
  }

  public NodeServiceOptionBufferEntity getNodeServiceOptionBufferEntity() {
    NodeServiceOptionBufferEntity nodeServiceOptionBufferEntity =
        new NodeServiceOptionBufferEntity();
    nodeServiceOptionBufferEntity.setOrgId(ORG_ID);
    nodeServiceOptionBufferEntity.setNodeId(NODE_ID);
    nodeServiceOptionBufferEntity.setServiceOption(SERVICE_OPTION);
    nodeServiceOptionBufferEntity.setBufferEndDate(new Date());
    nodeServiceOptionBufferEntity.setBufferStartDate(new Date());
    nodeServiceOptionBufferEntity.setBufferHours(BUFFER_HOURS);
    return nodeServiceOptionBufferEntity;
  }
}
