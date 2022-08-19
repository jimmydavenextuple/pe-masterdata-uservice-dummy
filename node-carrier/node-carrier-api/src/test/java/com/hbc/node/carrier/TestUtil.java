package com.hbc.node.carrier;

import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  private static final String CARRIER_SERVICE_ID_2 = "CarrierServiceId2";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static final String SERVICE_OPTION = "serviceOption-1";

  public static final String LAST_PICKUP_TIME = "5:00";

  public NodeCarrierRequest getNodeCarrierRequest() {
    return NodeCarrierRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .processingTime(2.0)
        .lastPickupTime("5:00")
        .build();
  }

  public NodeCarrierRequest getNodeCarrierRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    return NodeCarrierRequest.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .serviceOption(SERVICE_OPTION)
            .processingTime(2.0)
            .lastPickupTime("5:00")
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

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest() {
    return NodeCarrierUpdateRequest.builder().processingTime(2.0).lastPickupTime("5:00").build();
  }

  public NodeCarrierUpdateRequest getNodeCarrierUpdateRequest2() {
    Date bEndDate = new Date();
    bEndDate.setTime(1000);
    return NodeCarrierUpdateRequest.builder().processingTime(2.0).lastPickupTime("5:00")
            .bufferEndDate(bEndDate).build();
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
}
