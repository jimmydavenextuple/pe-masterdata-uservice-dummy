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
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeResponseServiceTest {

  @InjectMocks private NodeResponseService nodeResponseService;

  @Mock private NodeFeign nodeFeign;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeResponseService, "pageSize", 2);
  }

  @Test
  void getNodeListTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodePage());

    List<NodeDto> responseList = nodeResponseService.getNodeList(TestUtil.ORG_ID, null, null);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(nodeFeign, times(2)).getNodeList(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get node details when nodeIds and nodeTypes are not null")
  void getNodeListByNodeIdsAndNodeTypeAndOrgIdTest() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodePage());

    List<NodeDto> responseList =
        nodeResponseService.getNodeList(TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.NODE_TYPE);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(nodeFeign, times(2)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get node details when nodeIds is not null and nodeTypes is null")
  void getNodeListByNodeIdsAndOrgIdTest() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodePage());

    List<NodeDto> responseList =
        nodeResponseService.getNodeList(TestUtil.ORG_ID, TestUtil.NODE_ID, null);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(nodeFeign, times(2)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Get node details when nodeIds is null and nodeTypes is valid value")
  void getNodeListByNodeTypeAndOrgIdTest() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodePage());

    List<NodeDto> responseList =
        nodeResponseService.getNodeList(TestUtil.ORG_ID, null, TestUtil.NODE_TYPE);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(nodeFeign, times(2)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
  }
}
