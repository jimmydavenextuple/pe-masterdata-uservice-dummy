/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {
  public static final String ORG_ID = "NEXTUPLE_GR";
  public static final String NODE_ID = "Node_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";
  private static final String SERVICE_OPTION_2 = "SDND";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  private NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse2() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(false)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierListResponse() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(
            Arrays.asList(
                getNodeCarrierResponse2(
                    SERVICE_OPTION, 2.5, getBufferDate(2022, 10, 23), getBufferDate(2024, 01, 17)),
                getNodeCarrierResponse2(
                    SERVICE_OPTION_2,
                    4.5,
                    getBufferDate(2021, 10, 23),
                    getBufferDate(2021, 11, 20))))
        .build();
  }

  private NodeCarrierResponse getNodeCarrierResponse2(
      String serviceOption, Double bufferHours, Date bufferStartDate, Date bufferEndDate) {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId("")
        .serviceOption(serviceOption)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .bufferHours(bufferHours)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public Date getBufferDate(int year, int month, int date) {
    Calendar bufferDate = Calendar.getInstance();
    bufferDate.set(year, month, date);
    return bufferDate.getTime();
  }

  public BaseResponse<List<NodeCarrierResponse>>
      getBaseResponseOfNodeCarrierListResponseWithNullValues() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(Arrays.asList(getNodeCarrierResponse2(SERVICE_OPTION, null, null, null)))
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>>
      getBaseResponseOfNodeCarrierListResponseWithPartialNullValues() {
    return BaseResponse.builder()
        .message("Node Carrier List fetched successfully")
        .success(true)
        .payload(
            Arrays.asList(
                getNodeCarrierResponse2(SERVICE_OPTION, 5.5, null, getBufferDate(2023, 11, 20))))
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrierResponseList() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .success(true)
        .payload(List.of(getNodeCarrierResponse()))
        .build();
  }

  public NodeCarrierRequest getNodeCarrierRequest() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 5);
    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .bufferHours(1.0)
        .bufferStartDate(new Date())
        .bufferEndDate(calendar.getTime())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest() {
    return NodeCarrierUpdateRequest.builder()
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierBufferRequest getNodeCarrierBufferRequest() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 5);
    var buffer = new NodeCarrierBufferRequest();
    buffer.setNodeId(NODE_ID);
    buffer.setOrgId(ORG_ID);
    buffer.setServiceOption(SERVICE_OPTION);
    buffer.setBufferHours(1.0);
    buffer.setBufferStartDate(new Date());
    buffer.setBufferEndDate(calendar.getTime());
    buffer.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return buffer;
  }

  public BaseResponse<NodeServiceOptionBufferResponse> getNodeServiceOptionBufferResponse() {
    return BaseResponse.builder()
        .payload(
            NodeServiceOptionBufferResponse.builder()
                .id(1L)
                .nodeId(NODE_ID)
                .orgId(ORG_ID)
                .bufferHours(2D)
                .serviceOption(SERVICE_OPTION)
                .bufferStartDate(new Date())
                .bufferEndDate(new Date())
                .customAttributes(CUSTOM_ATTRIBUTES)
                .build())
        .build();
  }

  public NodeServiceOptionBufferDeleteRequest getNodeServiceOptionBufferDeleteRequest() {
    return NodeServiceOptionBufferDeleteRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<List<NodeServiceOptionBufferResponse>>
      getListOfNodeServiceOptionBufferResponse() {
    return BaseResponse.builder()
        .payload(
            List.of(
                NodeServiceOptionBufferResponse.builder()
                    .id(1L)
                    .nodeId(NODE_ID)
                    .orgId(ORG_ID)
                    .bufferHours(2D)
                    .serviceOption(SERVICE_OPTION)
                    .bufferStartDate(new Date())
                    .bufferEndDate(new Date())
                    .customAttributes(CUSTOM_ATTRIBUTES)
                    .build()))
        .build();
  }

  public BaseResponse<NodeCarriersResponse> getNodeCarriersResponse() {
    return BaseResponse.builder()
        .payload(
            NodeCarriersResponse.builder()
                .nodeId(NODE_ID)
                .orgId(ORG_ID)
                .carrierServiceId(CARRIER_SERVICE_ID)
                .serviceOption(SERVICE_OPTION)
                .lastPickupTime("5:00")
                .customAttributes(CUSTOM_ATTRIBUTES)
                .build())
        .build();
  }

  public BaseResponse<List<NodeCarriersResponse>> getListOfNodeCarriersResponse() {
    return BaseResponse.builder()
        .payload(
            List.of(
                NodeCarriersResponse.builder()
                    .nodeId(NODE_ID)
                    .orgId(ORG_ID)
                    .carrierServiceId(CARRIER_SERVICE_ID)
                    .serviceOption(SERVICE_OPTION)
                    .lastPickupTime("5:00")
                    .customAttributes(CUSTOM_ATTRIBUTES)
                    .build()))
        .build();
  }

  public BaseResponse<NodeServiceOptionResponse> getNodeServiceOptionResponse() {
    return BaseResponse.builder()
        .payload(
            NodeServiceOptionResponse.builder()
                .nodeId(NODE_ID)
                .orgId(ORG_ID)
                .serviceOption(SERVICE_OPTION)
                .processingTime(2.0)
                .customAttributes(CUSTOM_ATTRIBUTES)
                .build())
        .build();
  }

  public BaseResponse<List<NodeServiceOptionResponse>> getListOfNodeServiceOptionResponse() {
    return BaseResponse.builder()
        .payload(
            List.of(
                NodeServiceOptionResponse.builder()
                    .nodeId(NODE_ID)
                    .orgId(ORG_ID)
                    .serviceOption(SERVICE_OPTION)
                    .processingTime(2.0)
                    .customAttributes(CUSTOM_ATTRIBUTES)
                    .build()))
        .build();
  }
}
