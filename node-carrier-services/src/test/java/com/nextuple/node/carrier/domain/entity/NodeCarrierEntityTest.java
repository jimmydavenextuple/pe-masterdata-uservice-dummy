/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.node.carrier.TestUtil;
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
    assertNull(nodeCarrierSelectionEntity.getPriority());
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
    assertNotNull(nodeCarrierSelectionEntity.getPriority());
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
