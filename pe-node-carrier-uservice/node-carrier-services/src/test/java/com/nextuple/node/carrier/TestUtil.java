/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
import com.nextuple.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_SERVICE_ID_2 = "CarrierServiceId2";
  private static final String NODE_ID_2 = "node-2";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final String SERVICE_OPTION_2 = "serviceOption-2";
  public static final String SOURCE_GEOZONE = "M1R";
  public static final String DESTINATION_GEOZONE = "T2P";
  public static final String PRIORITY = "0";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String STATE = "state-1";
  public static final String ZIP_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String LAST_PICKUP_TIME = "5:00";
  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String CARRIER_ID = "carrier-1";
  public static final String NODE_CARRIER_NOT_FOUND_ERROR_MSG =
      "Node Carrier Service Option not found for given details";
  public static final String INVALID_SERVICE_OPTION = "Invalid serviceOption";
  public static final String CONFIG_KEY = "service-options";
  public static final String CONFIG_VALUE = "SDND, EXPRESS, STANDARD";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public NodeCarrierRequest getNodeCarrierRequest() {
    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("SDND")
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersRequest getNodeCarriersRequest() {
    return NodeCarriersRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("SDND")
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersRequest getNodeCarriersRequest2() {
    return NodeCarriersRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionRequest getNodeServiceOptionRequest() {
    return NodeServiceOptionRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(5.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionRequest getNodeServiceOptionRequest2() {
    return NodeServiceOptionRequest.builder()
        .nodeId("id")
        .orgId("idid")
        .serviceOption(SERVICE_OPTION)
        .processingTime(5.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierRequest getNodeCarrierRequest5() {
    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("ABC")
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersRequest getNodeCarriersRequest5() {
    return NodeCarriersRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("ABC")
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierBufferRequest getNodeCarrierBufferRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    NodeCarrierBufferRequest nodeCarrierBufferRequest = new NodeCarrierBufferRequest();
    nodeCarrierBufferRequest.setNodeId(NODE_ID);
    nodeCarrierBufferRequest.setOrgId(ORG_ID);
    nodeCarrierBufferRequest.setServiceOption(SERVICE_OPTION);
    nodeCarrierBufferRequest.setBufferHours(3.0);
    nodeCarrierBufferRequest.setBufferEndDate(bEndDate);
    nodeCarrierBufferRequest.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeCarrierBufferRequest;
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
        .success(true)
        .payload(getNodeResponse())
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNode1() {
    return BaseResponse.builder()
        .message("Calendar details added successfully")
        .success(false)
        .payload(getNodeResponse())
        .build();
  }

  public NodeCarrierBufferRequest getNodeCarrierBufferRequest3() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    NodeCarrierBufferRequest nodeCarrierBufferRequest = new NodeCarrierBufferRequest();
    nodeCarrierBufferRequest.setNodeId(NODE_ID);
    nodeCarrierBufferRequest.setOrgId(ORG_ID);
    nodeCarrierBufferRequest.setServiceOption(SERVICE_OPTION);
    nodeCarrierBufferRequest.setBufferHours(-3.0);
    nodeCarrierBufferRequest.setBufferEndDate(bEndDate);
    nodeCarrierBufferRequest.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeCarrierBufferRequest;
  }

  public NodeCarrierRequest getNodeCarrierRequest3() {
    Calendar c1 = Calendar.getInstance();
    c1.set(Calendar.MONTH, 11);
    c1.set(Calendar.DATE, 05);
    c1.set(Calendar.YEAR, 2023);
    Date bEndDate = c1.getTime();

    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("STANDARD")
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .bufferEndDate(bEndDate)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierRequest getNodeCarrierRequest4() {
    Calendar c1 = Calendar.getInstance();
    c1.set(Calendar.MONTH, 11);
    c1.set(Calendar.DATE, 05);
    c1.set(Calendar.YEAR, 2023);
    Date bEndDate = c1.getTime();

    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .bufferHours(-5.0)
        .bufferEndDate(bEndDate)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierResponse getNodeCarrierResponse() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersResponse getNodeCarriersResponse() {
    return NodeCarriersResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionResponse getNodeServiceOptionResponse() {
    return NodeServiceOptionResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(2.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierResponse getNodeCarrierResponse2() {
    return NodeCarrierResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .bufferHours(3.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersResponse getNodeCarriersResponse2() {
    return NodeCarriersResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest() {
    return NodeCarrierUpdateRequest.builder()
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarriersUpdateRequest getNodeCarriersUpdateRequest() {
    return NodeCarriersUpdateRequest.builder()
        .lastPickupTime("5:00")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionUpdateRequest getNodeServiceOptionUpdateRequest() {
    return NodeServiceOptionUpdateRequest.builder()
        .processingTime(5.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    return NodeCarrierUpdateRequest.builder()
        .processingTime(2.0)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .lastPickupTime("5:00")
        .build();
  }

  public NodeCarrierEntity getNodeCarrierEntity() {
    NodeCarrierEntity nodeCarrierEntity = new NodeCarrierEntity();
    nodeCarrierEntity.setNodeId(NODE_ID);
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setProcessingTime(2.0);
    nodeCarrierEntity.setLastPickupTime("5:00");
    nodeCarrierEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return nodeCarrierEntity;
  }

  public NodeCarriersEntity getNodeCarriersEntity() {
    NodeCarriersEntity nodeCarrierEntity = new NodeCarriersEntity();
    nodeCarrierEntity.setNodeId(NODE_ID);
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setLastPickupTime("5:00");
    nodeCarrierEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeCarrierEntity;
  }

  public NodeCarriersEntity getNodeCarriersEntity2() {
    NodeCarriersEntity nodeCarrierEntity = new NodeCarriersEntity();
    nodeCarrierEntity.setNodeId(NODE_ID);
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setLastPickupTime("10:00");
    nodeCarrierEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeCarrierEntity;
  }

  public NodeServiceOptionEntity getNodeServiceOptionEntity() {
    NodeServiceOptionEntity nodeServiceOptionEntity = new NodeServiceOptionEntity();
    nodeServiceOptionEntity.setNodeId(NODE_ID);
    nodeServiceOptionEntity.setOrgId(ORG_ID);
    nodeServiceOptionEntity.setServiceOption(SERVICE_OPTION);
    nodeServiceOptionEntity.setProcessingTime(2.0);
    nodeServiceOptionEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeServiceOptionEntity;
  }

  public List<NodeCarrierEntity> getNodeCarrierEntityList() {
    NodeCarrierEntity nodeCarrierEntity1 = new NodeCarrierEntity();
    nodeCarrierEntity1.setNodeId(NODE_ID);
    nodeCarrierEntity1.setOrgId(ORG_ID);
    nodeCarrierEntity1.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity1.setProcessingTime(2.0);
    nodeCarrierEntity1.setLastPickupTime("5:00");
    nodeCarrierEntity1.setCustomAttributes(CUSTOM_ATTRIBUTES);

    NodeCarrierEntity nodeCarrierEntity2 = new NodeCarrierEntity();
    nodeCarrierEntity2.setNodeId(NODE_ID);
    nodeCarrierEntity2.setOrgId(ORG_ID);
    nodeCarrierEntity2.setCarrierServiceId(CARRIER_SERVICE_ID_2);
    nodeCarrierEntity2.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity2.setProcessingTime(10.0);
    nodeCarrierEntity2.setLastPickupTime("11:00");
    nodeCarrierEntity2.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return Arrays.asList(nodeCarrierEntity1, nodeCarrierEntity2);
  }

  public List<NodeCarriersEntity> getNodeCarriersEntityList() {
    NodeCarriersEntity nodeCarriersEntity1 = new NodeCarriersEntity();
    nodeCarriersEntity1.setNodeId(NODE_ID);
    nodeCarriersEntity1.setOrgId(ORG_ID);
    nodeCarriersEntity1.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarriersEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarriersEntity1.setLastPickupTime("5:00");
    nodeCarriersEntity1.setCustomAttributes(CUSTOM_ATTRIBUTES);

    NodeCarriersEntity nodeCarriersEntity2 = new NodeCarriersEntity();
    nodeCarriersEntity2.setNodeId(NODE_ID);
    nodeCarriersEntity2.setOrgId(ORG_ID);
    nodeCarriersEntity2.setCarrierServiceId(CARRIER_SERVICE_ID_2);
    nodeCarriersEntity2.setServiceOption(SERVICE_OPTION);
    nodeCarriersEntity2.setLastPickupTime("11:00");
    nodeCarriersEntity2.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return Arrays.asList(nodeCarriersEntity1, nodeCarriersEntity2);
  }

  public List<NodeCarrierResponse> getNodeCarrierDtoList() {
    NodeCarrierResponse nodeCarrierResponse1 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .serviceOption(SERVICE_OPTION)
            .processingTime(2.0)
            .lastPickupTime("5:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCarrierResponse nodeCarrierResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .serviceOption(SERVICE_OPTION)
            .processingTime(10.0)
            .lastPickupTime("11:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return Arrays.asList(nodeCarrierResponse1, nodeCarrierResponse2);
  }

  public List<NodeCarrierEntity> getNodeCarrierEntityList2() {
    NodeCarrierEntity nodeCarrierEntity1 = new NodeCarrierEntity();
    nodeCarrierEntity1.setNodeId(NODE_ID);
    nodeCarrierEntity1.setOrgId(ORG_ID);
    nodeCarrierEntity1.setCarrierServiceId("");
    nodeCarrierEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity1.setProcessingTime(2.0);
    nodeCarrierEntity1.setLastPickupTime("5:00");
    nodeCarrierEntity1.setCustomAttributes(CUSTOM_ATTRIBUTES);

    NodeCarrierEntity nodeCarrierEntity2 = new NodeCarrierEntity();
    nodeCarrierEntity2.setNodeId(NODE_ID);
    nodeCarrierEntity2.setOrgId(ORG_ID);
    nodeCarrierEntity2.setCarrierServiceId("");
    nodeCarrierEntity2.setServiceOption(SERVICE_OPTION_2);
    nodeCarrierEntity2.setProcessingTime(10.0);
    nodeCarrierEntity2.setLastPickupTime("11:00");
    nodeCarrierEntity2.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return Arrays.asList(nodeCarrierEntity1, nodeCarrierEntity2);
  }

  public List<NodeCarrierResponse> getNodeCarrierDtoList2() {
    NodeCarrierResponse nodeCarrierResponse1 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId("")
            .serviceOption(SERVICE_OPTION)
            .processingTime(2.0)
            .lastPickupTime("5:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCarrierResponse nodeCarrierResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId("")
            .serviceOption(SERVICE_OPTION_2)
            .processingTime(10.0)
            .lastPickupTime("11:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return Arrays.asList(nodeCarrierResponse1, nodeCarrierResponse2);
  }

  public NodeCarrierSelectionResponse getNodeCarrierSelectionResponse() {
    return NodeCarrierSelectionResponse.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .priority("1")
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierSelectionRequest getNodeCarrierSelectionRequest() {
    return NodeCarrierSelectionRequest.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .priority("1")
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeCarrierSelectionEntity getNodeCarrierSelectionEntity() {
    NodeCarrierSelectionEntity nodeCarrierEntity = new NodeCarrierSelectionEntity();
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setPriority(PRIORITY);
    nodeCarrierEntity.setDestinationGeozone(DESTINATION_GEOZONE);
    nodeCarrierEntity.setSourceGeozone(SOURCE_GEOZONE);
    nodeCarrierEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return nodeCarrierEntity;
  }

  public List<NodeCarrierListCacheKeyDto> getNodeCarrierListCacheKeyDtoList() {
    NodeCarrierListCacheKeyDto nodeCarrierListCacheKeyDto1 =
        NodeCarrierListCacheKeyDto.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .build();

    NodeCarrierListCacheKeyDto nodeCarrierListCacheKeyDto2 =
        NodeCarrierListCacheKeyDto.builder()
            .nodeId(NODE_ID_2)
            .orgId(ORG_ID)
            .serviceOption(SERVICE_OPTION)
            .build();

    return List.of(nodeCarrierListCacheKeyDto1, nodeCarrierListCacheKeyDto2);
  }

  public BaseResponse<List<CarrierServiceResponse>> getCarrierServiceUpdateResponse() {
    var carrierResponse =
        List.of(
            CarrierServiceResponse.builder()
                .orgId(ORG_ID)
                .carrierId(CARRIER_ID)
                .carrierServiceId(CARRIER_SERVICE_ID)
                .carrierName(CARRIER_NAME)
                .serviceName(SERVICE_NAME)
                .serviceOptions(SERVICE_OPTIONS)
                .build());
    return BaseResponse.builder().payload(carrierResponse).build();
  }

  public List<NodeCarrierResponse> getNodeCarrierList() {
    NodeCarrierResponse nodeCarrierResponse1 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .serviceOption(SERVICE_OPTION)
            .processingTime(2.0)
            .lastPickupTime("5:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCarrierResponse nodeCarrierResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .serviceOption(SERVICE_OPTION_2)
            .processingTime(10.0)
            .lastPickupTime("11:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return Arrays.asList(nodeCarrierResponse1, nodeCarrierResponse2);
  }

  public List<NodeCarriersResponse> getNodeCarriersList() {
    NodeCarriersResponse nodeCarriersResponse1 =
        NodeCarriersResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .serviceOption(SERVICE_OPTION)
            .lastPickupTime("5:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    NodeCarriersResponse nodeCarriersResponse2 =
        NodeCarriersResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .serviceOption(SERVICE_OPTION_2)
            .lastPickupTime("11:00")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();

    return Arrays.asList(nodeCarriersResponse1, nodeCarriersResponse2);
  }

  public List<NodeCarrierEntity> getNodeCarrierEntityListWithPickupDetails(
      String carrierServiceId) {
    NodeCarrierEntity nodeCarrierEntity1 = new NodeCarrierEntity();
    nodeCarrierEntity1.setNodeId(NODE_ID);
    nodeCarrierEntity1.setOrgId(ORG_ID);
    nodeCarrierEntity1.setCarrierServiceId(carrierServiceId);
    nodeCarrierEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity1.setLastPickupTime("5:00");
    nodeCarrierEntity1.setCustomAttributes(CUSTOM_ATTRIBUTES);

    NodeCarrierEntity nodeCarrierEntity2 = new NodeCarrierEntity();
    nodeCarrierEntity2.setNodeId(NODE_ID);
    nodeCarrierEntity2.setOrgId(ORG_ID);
    nodeCarrierEntity2.setCarrierServiceId(CARRIER_SERVICE_ID_2);
    nodeCarrierEntity2.setServiceOption(SERVICE_OPTION_2);
    nodeCarrierEntity2.setLastPickupTime("11:00");
    nodeCarrierEntity2.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return Arrays.asList(nodeCarrierEntity1, nodeCarrierEntity2);
  }

  public List<NodeCarriersEntity> getNodeCarriersEntity(String carrierServiceId) {
    NodeCarriersEntity nodeCarriersEntity1 = new NodeCarriersEntity();
    nodeCarriersEntity1.setNodeId(NODE_ID);
    nodeCarriersEntity1.setOrgId(ORG_ID);
    nodeCarriersEntity1.setCarrierServiceId(carrierServiceId);
    nodeCarriersEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarriersEntity1.setLastPickupTime("5:00");
    nodeCarriersEntity1.setCustomAttributes(CUSTOM_ATTRIBUTES);

    NodeCarriersEntity nodeCarriersEntity2 = new NodeCarriersEntity();
    nodeCarriersEntity2.setNodeId(NODE_ID);
    nodeCarriersEntity2.setOrgId(ORG_ID);
    nodeCarriersEntity2.setCarrierServiceId(CARRIER_SERVICE_ID_2);
    nodeCarriersEntity2.setServiceOption(SERVICE_OPTION_2);
    nodeCarriersEntity2.setLastPickupTime("11:00");
    nodeCarriersEntity2.setCustomAttributes(CUSTOM_ATTRIBUTES);

    return Arrays.asList(nodeCarriersEntity1, nodeCarriersEntity2);
  }

  public BaseResponse<TenantConfigdataResponse> getTenantConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder().configKey(CONFIG_KEY).configValue(CONFIG_VALUE).build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }

  public NodeServiceOptionBufferResponse getNodeServiceOptionBufferResponse() {
    return NodeServiceOptionBufferResponse.builder()
        .id(1L)
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .bufferHours(2D)
        .serviceOption(SERVICE_OPTION)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionBufferRequest getNodeServiceOptionBufferRequest() {
    return NodeServiceOptionBufferRequest.builder()
        .nodeId(NODE_ID)
        .serviceOption(SERVICE_OPTION)
        .orgId(ORG_ID)
        .bufferHours(2D)
        .bufferStartDate(new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000L))
        .bufferEndDate(new Date(new Date().getTime() + 10 * 24 * 60 * 60 * 1000L))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionBufferDeleteRequest getNodeServiceOptionBufferDeleteRequest() {
    return NodeServiceOptionBufferDeleteRequest.builder()
        .nodeId(NODE_ID)
        .serviceOption(SERVICE_OPTION)
        .orgId(ORG_ID)
        .bufferStartDate(new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000L))
        .bufferEndDate(new Date(new Date().getTime() + 10 * 24 * 60 * 60 * 1000L))
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionBufferUpdateRequest getNodeServiceOptionBufferUpdateRequest() {
    return NodeServiceOptionBufferUpdateRequest.builder()
        .bufferHours(2D)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public NodeServiceOptionBufferUpdateRequest
      getNodeServiceOptionBufferUpdateRequestWithoutDates() {
    return NodeServiceOptionBufferUpdateRequest.builder()
        .bufferHours(2D)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<NodeServiceOptionBufferEntity> getNodeServiceOptionBufferEntities(int no) {
    List<NodeServiceOptionBufferEntity> entities = new ArrayList<>();
    while (no-- > 0) {
      entities.add(
          NodeServiceOptionBufferEntity.builder()
              .id(((long) no))
              .nodeId(NODE_ID)
              .serviceOption(SERVICE_OPTION)
              .orgId(ORG_ID)
              .bufferHours(2D)
              .bufferStartDate(new Date(new Date().getTime() + no * 24 * 60 * 60 * 1000L))
              .bufferEndDate(new Date(new Date().getTime() + (no + 1) * 24 * 60 * 60 * 1000L))
              .customAttributes(CUSTOM_ATTRIBUTES)
              .build());
    }
    return entities;
  }

  public NodeServiceOptionBufferEntity getNodeServiceOptionBufferEntity() {
    return getNodeServiceOptionBufferEntities(1).get(0);
  }
}
