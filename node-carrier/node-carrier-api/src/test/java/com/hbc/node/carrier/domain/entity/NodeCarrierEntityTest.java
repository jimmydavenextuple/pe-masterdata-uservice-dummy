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
}
