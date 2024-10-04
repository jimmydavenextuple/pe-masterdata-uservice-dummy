/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierDetails;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierSelectionCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeCarriersCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionBufferCacheValue;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionCacheKey;
import com.nextuple.nodecarrier.cache.domain.NodeServiceOptionCacheValue;
import java.util.Date;
import java.util.List;

public class TestUtil {
  public static final String NODE_ID = "Node_Id_01";
  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";
  public static final String SOURCE_GEOZONE = "M1R";
  public static final String DESTINATION_GEOZONE = "T2P";

  public NodeCarrierCacheValue getNodeCarrierCacheValue() {
    NodeCarrierDetails nodeCarrierDetails = getNodeCarrierDetails();
    return NodeCarrierCacheValue.builder().nodeCarrierDetails(nodeCarrierDetails).build();
  }

  public NodeCarrierListCacheValue getNodeCarrierListCacheValue() {
    NodeCarrierDetails nodeCarrierDetails = getNodeCarrierDetails();
    return NodeCarrierListCacheValue.builder()
        .nodeCarrierDetailsList(List.of(nodeCarrierDetails))
        .build();
  }

  public NodeServiceOptionCacheValue getNodeServiceOptionCacheValue() {
    NodeServiceOptionResponse nodeServiceOptionResponse = getNodeServiceOptionDetails();
    return NodeServiceOptionCacheValue.builder()
        .nodeServiceOptionResponse(nodeServiceOptionResponse)
        .build();
  }

  public NodeCarriersCacheValue getNodeCarriersCacheValue() {
    NodeCarriersResponse nodeCarrierDetails = getNodeCarriersResponse();
    return NodeCarriersCacheValue.builder()
        .nodeCarriersResponses(List.of(nodeCarrierDetails))
        .build();
  }

  private NodeCarrierDetails getNodeCarrierDetails() {
    return NodeCarrierDetails.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

  private NodeServiceOptionResponse getNodeServiceOptionDetails() {
    return NodeServiceOptionResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .build();
  }

  private NodeCarriersResponse getNodeCarriersResponse() {
    return NodeCarriersResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

  private NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(PROCESSING_TIME)
        .lastPickupTime(LAST_PICK_UP_TIME)
        .build();
  }

  public BaseResponse<NodeCarrierResponse> getBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .payload(getNodeCarrierResponse())
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getListOfBaseResponseOfNodeCarrierResponse() {
    return BaseResponse.builder()
        .message("Node carrier details fetched successfully")
        .payload(List.of(getNodeCarrierResponse()))
        .build();
  }

  public BaseResponse<NodeServiceOptionResponse> getBaseResponseOfNodeServiceOptionResponse() {
    return BaseResponse.builder()
        .message("Node Service Option fetched successfully")
        .payload(getNodeServiceOptionDetails())
        .build();
  }

  public BaseResponse<List<NodeCarriersResponse>> getListOfBaseResponseOfNodeCarriersResponse() {
    return BaseResponse.builder()
        .message("Node carriers details fetched successfully")
        .payload(List.of(getNodeCarriersResponse()))
        .build();
  }

  public NodeCarrierCacheKey getNodeCarrierCacheKey() {
    return NodeCarrierCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeCarrierListCacheKey getNodeCarrierListCacheKey() {
    return NodeCarrierListCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeServiceOptionCacheKey getNodeServiceOptionCacheKey() {
    return NodeServiceOptionCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeCarriersCacheKey getNodeCarriersCacheKey2() {
    return NodeCarriersCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeCarriersCacheKey getNodeCarriersCacheKey() {
    return NodeCarriersCacheKey.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .build();
  }

  public NodeCarrierSelectionCacheValue getNodeCarrierSelectionCacheValue() {
    NodeCarrierSelectionResponse nodeCarrierSelectionResponse =
        NodeCarrierSelectionResponse.builder()
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .priority("1")
            .destinationGeozone(DESTINATION_GEOZONE)
            .sourceGeozone(SOURCE_GEOZONE)
            .build();
    return NodeCarrierSelectionCacheValue.builder()
        .nodeCarrierSelectionList(List.of(nodeCarrierSelectionResponse))
        .build();
  }

  public NodeServiceOptionBufferCacheValue getNodeServiceOptionBufferCacheValue() {
    NodeServiceOptionBufferResponse nodeServiceOptionBufferResponse =
        NodeServiceOptionBufferResponse.builder()
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .nodeId(NODE_ID)
            .bufferStartDate(new Date(2023, 1, 1))
            .bufferEndDate(new Date(2023, 1, 10))
            .bufferHours(2.0)
            .build();
    return NodeServiceOptionBufferCacheValue.builder()
        .nodeServiceOptionBufferResponse(List.of(nodeServiceOptionBufferResponse))
        .build();
  }

  public BaseResponse<List<NodeCarrierSelectionResponse>> getNodeCarrierSelectionResponse() {
    BaseResponse<List<NodeCarrierSelectionResponse>> response = new BaseResponse<>();
    NodeCarrierSelectionResponse nodeCarrierSelectionResponse =
        NodeCarrierSelectionResponse.builder()
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .priority("1")
            .destinationGeozone(DESTINATION_GEOZONE)
            .sourceGeozone(SOURCE_GEOZONE)
            .build();
    response.setPayload(List.of(nodeCarrierSelectionResponse));
    return response;
  }

  public BaseResponse<List<NodeServiceOptionBufferResponse>> getNodeServiceOptionBufferResponse() {
    BaseResponse<List<NodeServiceOptionBufferResponse>> response = new BaseResponse<>();
    NodeServiceOptionBufferResponse nodeServiceOptionBufferResponse =
        NodeServiceOptionBufferResponse.builder()
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .nodeId(NODE_ID)
            .bufferHours(2.0)
            .bufferStartDate(new Date(2023, 1, 1))
            .bufferEndDate(new Date(2023, 1, 10))
            .build();
    response.setPayload(List.of(nodeServiceOptionBufferResponse));
    return response;
  }

  public NodeCarrierSelectionCacheKey getNodeCarrierSelectionCacheKey() {
    return NodeCarrierSelectionCacheKey.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .destinationGeozone(DESTINATION_GEOZONE)
        .build();
  }

  public NodeServiceOptionBufferCacheKey getNodeServiceOptionBufferCacheKey() {
    return NodeServiceOptionBufferCacheKey.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .horizonDays(100)
        .requestDate(null)
        .build();
  }
}
