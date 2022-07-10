package com.hbc.node.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.node.TestUtil;
import org.junit.jupiter.api.Test;

class NodeEntityTest {

  @Test
  void nodeEntityTest() {
    NodeEntity nodeEntity = new NodeEntity();
    assertNull(nodeEntity.getNodeId());
    assertNull(nodeEntity.getNodeType());
    assertNull(nodeEntity.getBopisEligible());
    assertNull(nodeEntity.getCity());
    assertNull(nodeEntity.getCountry());
    assertNull(nodeEntity.getExpressEligible());
    assertNull(nodeEntity.getIsActive());
    assertNull(nodeEntity.getLatitude());
    assertNull(nodeEntity.getLongitude());
    assertNull(nodeEntity.getOrgId());
    assertNull(nodeEntity.getPostalCode());
    assertNull(nodeEntity.getProvince());
    assertNull(nodeEntity.getSdndEligible());
    assertNull(nodeEntity.getTimezone());
    assertNull(nodeEntity.getShipToHome());
    assertNull(nodeEntity.getStreet());
  }

  @Test
  void nodeIdNullTest() {
    NodePK nodeId = new NodePK();
    assertNull(nodeId.getNodeId());
    assertNull(nodeId.getOrgId());
  }

  @Test
  void nodeIdTest() {
    NodePK nodeId = new NodePK(TestUtil.NODE_ID, TestUtil.ORG_ID);
    assertNotNull(nodeId.getNodeId());
    assertNotNull(nodeId.getOrgId());
  }
}
