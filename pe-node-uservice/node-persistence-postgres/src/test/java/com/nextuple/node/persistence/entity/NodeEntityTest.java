/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.node.persistence.entity.key.NodeKey;
import org.junit.jupiter.api.Test;

class NodeEntityTest {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";

  @Test
  void nodeEntityTest() {
    NodeEntity nodeEntity = new NodeEntity();
    assertNull(nodeEntity.getNodeId());
    assertNull(nodeEntity.getNodeType());
    assertNull(nodeEntity.getBopisEligible());
    assertNull(nodeEntity.getCity());
    assertNull(nodeEntity.getCountry());
    assertNull(nodeEntity.getIsActive());
    assertNull(nodeEntity.getLatitude());
    assertNull(nodeEntity.getLongitude());
    assertNull(nodeEntity.getOrgId());
    assertNull(nodeEntity.getZipCode());
    assertNull(nodeEntity.getState());
    assertNull(nodeEntity.getTimezone());
    assertNull(nodeEntity.getShipToHome());
    assertNull(nodeEntity.getStreet());
    assertNull(nodeEntity.getServiceOptionEligibilities());
  }

  @Test
  void nodeIdNullTest() {
    NodeKey nodeId = new NodeKey();
    assertNull(nodeId.getNodeId());
    assertNull(nodeId.getOrgId());
  }

  @Test
  void nodeIdTest() {
    NodeKey nodeId = new NodeKey(NODE_ID, ORG_ID);
    assertNotNull(nodeId.getNodeId());
    assertNotNull(nodeId.getOrgId());
  }
}
