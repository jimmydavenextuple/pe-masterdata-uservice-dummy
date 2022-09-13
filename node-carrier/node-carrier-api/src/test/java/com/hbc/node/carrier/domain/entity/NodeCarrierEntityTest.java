package com.hbc.node.carrier.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.node.carrier.TestUtil;
import org.junit.jupiter.api.Test;

class NodeCarrierEntityTest {

  @Test
  void nodeCarrierEntityTest() {
    NodeCarrierEntity nodeCarrierEntity = new NodeCarrierEntity();
    assertNull(nodeCarrierEntity.getOrgId());
    assertNull(nodeCarrierEntity.getNodeId());
    assertNull(nodeCarrierEntity.getCarrierServiceId());
    assertNull(nodeCarrierEntity.getServiceOption());
    assertNull(nodeCarrierEntity.getLastPickupTime());
    assertNull(nodeCarrierEntity.getProcessingTime());
  }

  @Test
  void nodeCarrierIdNullDataTest() {
    NodeCarrierId nodeCarrierId = new NodeCarrierId();
    assertNull(nodeCarrierId.getNodeId());
    assertNull(nodeCarrierId.getCarrierServiceId());
    assertNull(nodeCarrierId.getOrgId());
    assertNull(nodeCarrierId.getServiceOption());
  }

  @Test
  void nodeCarrierIdTest() {
    NodeCarrierId nodeCarrierId =
        new NodeCarrierId(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    assertNotNull(nodeCarrierId.getNodeId());
    assertNotNull(nodeCarrierId.getCarrierServiceId());
    assertNotNull(nodeCarrierId.getOrgId());
    assertNotNull(nodeCarrierId.getServiceOption());
  }

  @Test
  void nodeCarrierSelectionNullDataTest() {
    NodeCarrierSelectionEntity nodeCarrierSelectionEntity = new NodeCarrierSelectionEntity();
    assertNull(nodeCarrierSelectionEntity.getNodeCarrierSelectionPriority());
    assertNull(nodeCarrierSelectionEntity.getServiceOption());
    assertNull(nodeCarrierSelectionEntity.getOrgId());
    assertNull(nodeCarrierSelectionEntity.getDestinationGeozone());
    assertNull(nodeCarrierSelectionEntity.getSourceGeozone());
  }

  @Test
  void nodeCarrierSelectionTest() {
    NodeCarrierSelectionEntity nodeCarrierSelectionEntity =
        new NodeCarrierSelectionEntity(
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.PRIORITY);
    assertNotNull(nodeCarrierSelectionEntity.getNodeCarrierSelectionPriority());
    assertNotNull(nodeCarrierSelectionEntity.getSourceGeozone());
    assertNotNull(nodeCarrierSelectionEntity.getOrgId());
    assertNotNull(nodeCarrierSelectionEntity.getServiceOption());
  }

  @Test
  void nodeCarrierSelectionIDNullDataTest() {
    NodeCarrierSelectionId nodeCarrierSelectionId = new NodeCarrierSelectionId();
    assertNull(nodeCarrierSelectionId.getServiceOption());
    assertNull(nodeCarrierSelectionId.getOrgId());
    assertNull(nodeCarrierSelectionId.getDestinationGeozone());
    assertNull(nodeCarrierSelectionId.getSourceGeozone());
  }

  @Test
  void nodeCarrierSelectionIDTest() {
    NodeCarrierSelectionId nodeCarrierSelectionId =
        new NodeCarrierSelectionId(
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);
    assertNotNull(nodeCarrierSelectionId.getDestinationGeozone());
    assertNotNull(nodeCarrierSelectionId.getSourceGeozone());
    assertNotNull(nodeCarrierSelectionId.getOrgId());
    assertNotNull(nodeCarrierSelectionId.getServiceOption());
  }
}
