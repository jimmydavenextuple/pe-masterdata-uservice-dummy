package com.hbc.node.carrier;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  private static final String CARRIER_SERVICE_ID_2 = "CarrierServiceId2";
  private static final String NODE_ID_2 = "node-2";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final String SERVICE_OPTION_2 = "serviceOption-2";
  public static final String SOURCE_GEOZONE = "M1R";
  public static final String DESTINATION_GEOZONE = "T2P";
  public static final String PRIORITY = "0";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String LAST_PICKUP_TIME = "5:00";

  public NodeCarrierRequest getNodeCarrierRequest() {
    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption("SDND")
        .processingTime(2.0)
        .lastPickupTime("5:00")
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
        .build();
  }

  public NodeCarrierBufferRequest getNodeCarrierBufferRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    return NodeCarrierBufferRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours(3.0)
        .bufferEndDate(bEndDate)
        .build();
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
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
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
    return NodeCarrierBufferRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .bufferHours(-3.0)
        .bufferEndDate(bEndDate)
        .build();
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
        .build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest() {
    return NodeCarrierUpdateRequest.builder().processingTime(2.0).lastPickupTime("5:00").build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    return NodeCarrierUpdateRequest.builder().processingTime(2.0).lastPickupTime("5:00").build();
  }

  public NodeCarrierEntity getNodeCarrierEntity() {
    NodeCarrierEntity nodeCarrierEntity = new NodeCarrierEntity();
    nodeCarrierEntity.setNodeId(NODE_ID);
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setProcessingTime(2.0);
    nodeCarrierEntity.setLastPickupTime("5:00");

    return nodeCarrierEntity;
  }

  public List<NodeCarrierEntity> getNodeCarrierEntityList() {
    NodeCarrierEntity nodeCarrierEntity1 = new NodeCarrierEntity();
    nodeCarrierEntity1.setNodeId(NODE_ID);
    nodeCarrierEntity1.setOrgId(ORG_ID);
    nodeCarrierEntity1.setCarrierServiceId(CARRIER_SERVICE_ID);
    nodeCarrierEntity1.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity1.setProcessingTime(2.0);
    nodeCarrierEntity1.setLastPickupTime("5:00");

    NodeCarrierEntity nodeCarrierEntity2 = new NodeCarrierEntity();
    nodeCarrierEntity2.setNodeId(NODE_ID);
    nodeCarrierEntity2.setOrgId(ORG_ID);
    nodeCarrierEntity2.setCarrierServiceId(CARRIER_SERVICE_ID_2);
    nodeCarrierEntity2.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity2.setProcessingTime(10.0);
    nodeCarrierEntity2.setLastPickupTime("11:00");

    return Arrays.asList(nodeCarrierEntity1, nodeCarrierEntity2);
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
            .build();

    NodeCarrierResponse nodeCarrierResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .serviceOption(SERVICE_OPTION)
            .processingTime(10.0)
            .lastPickupTime("11:00")
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

    NodeCarrierEntity nodeCarrierEntity2 = new NodeCarrierEntity();
    nodeCarrierEntity2.setNodeId(NODE_ID);
    nodeCarrierEntity2.setOrgId(ORG_ID);
    nodeCarrierEntity2.setCarrierServiceId("");
    nodeCarrierEntity2.setServiceOption(SERVICE_OPTION_2);
    nodeCarrierEntity2.setProcessingTime(10.0);
    nodeCarrierEntity2.setLastPickupTime("11:00");

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
            .build();

    NodeCarrierResponse nodeCarrierResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId("")
            .serviceOption(SERVICE_OPTION_2)
            .processingTime(10.0)
            .lastPickupTime("11:00")
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
        .build();
  }

  public NodeCarrierSelectionRequest getNodeCarrierSelectionRequest() {
    return NodeCarrierSelectionRequest.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .priority("1")
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .build();
  }

  public NodeCarrierSelectionEntity getNodeCarrierSelectionEntity() {
    NodeCarrierSelectionEntity nodeCarrierEntity = new NodeCarrierSelectionEntity();
    nodeCarrierEntity.setOrgId(ORG_ID);
    nodeCarrierEntity.setServiceOption(SERVICE_OPTION);
    nodeCarrierEntity.setPriority(PRIORITY);
    nodeCarrierEntity.setDestinationGeozone(DESTINATION_GEOZONE);
    nodeCarrierEntity.setSourceGeozone(SOURCE_GEOZONE);
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
}
