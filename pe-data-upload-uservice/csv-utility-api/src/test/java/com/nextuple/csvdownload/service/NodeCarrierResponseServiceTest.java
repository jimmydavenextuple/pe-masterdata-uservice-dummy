/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierResponseServiceTest {

  @InjectMocks private NodeCarrierResponseService nodeCarrierResponseService;

  @Mock private NodeCarrierV2Feign nodeCarrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getNodeCarrierResponseTest() {
    ReflectionTestUtils.setField(nodeCarrierResponseService, "nodeCarrierFeign", nodeCarrierFeign);
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrier());

    List<NodeCarrierResponse> response =
        nodeCarrierResponseService.getNodeCarrierResponse(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(2, response.size());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, response.get(0).getNodeId());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
  }

  @Test
  @Description("Test get Node carrier by orgId, NodeId and Carrier Service ID- Happy Path")
  void getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId() {
    ReflectionTestUtils.setField(nodeCarrierResponseService, "nodeCarrierFeign", nodeCarrierFeign);
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrier());
    List<NodeCarrierResponse> response =
        nodeCarrierResponseService.getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(2, response.size());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, response.get(0).getNodeId());
    verify(nodeCarrierFeign, times(1))
        .getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any());
  }

  @Test
  @Description("Test get Node carrier by orgId, NodeId and Carrier Service ID- Null Scenario")
  void getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceIdException() {
    ReflectionTestUtils.setField(nodeCarrierResponseService, "nodeCarrierFeign", nodeCarrierFeign);
    when(nodeCarrierFeign.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenReturn(null);
    List<NodeCarrierResponse> response =
        nodeCarrierResponseService.getNodeCarrierResponseByOrgIdNodeIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(0, response.size());
    verify(nodeCarrierFeign, times(1))
        .getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(any(), any(), any());
  }
}
