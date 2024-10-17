/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProcessingTimeBufferServiceTest {

  @InjectMocks private ProcessingTimeBufferService processingTimeBufferService;

  @Mock private NodeFeign nodeFeign;

  @Mock private NodeCarrierV2Feign nodeCarrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Mock private PageProperties pageProperties;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(processingTimeBufferService, "nodeCarrierFeign", nodeCarrierFeign);
  }

  @Test
  @DisplayName("Get processing time buffers: Happy Path")
  void getProcessingTimeBuffersTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));
    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(TestUtil.ORG_ID, null, pageParams);

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(4, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertEquals(
        2.5, response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNotNull(
        response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertEquals(
        "Active", response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(4))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Get processing time buffers: Happy Path - Inactive Status")
  void getProcessingTimeBuffersStatusInactiveTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(TestUtil.ORG_ID, null, pageParams);

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(4, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertEquals(
        2.5, response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNotNull(
        response.getData().get((0)).getProcessingTimeBuffers().get(1).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(1).getBufferEndDate());
    assertEquals(
        "Inactive", response.getData().get((0)).getProcessingTimeBuffers().get(1).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(4))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Get processing time buffers: Buffers with null values")
  void getProcessingTimeBuffersNullValuesTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListEmptyResponse());

    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(TestUtil.ORG_ID, null, pageParams);

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(4))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Get processing time buffers: Buffers with partial null values")
  void getProcessingTimeBuffersPartialNullValuesTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponseWithPartialNullValues());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(TestUtil.ORG_ID, null, pageParams);

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(4))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Get processing time buffers given the node Ids")
  void getProcessingTimeBuffersNodeIdsTest() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));
    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(
            TestUtil.ORG_ID, TestUtil.NODE_ID, pageParams);

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(4, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertEquals(
        2.5, response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNotNull(
        response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertEquals(
        "Active", response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(4))
        .getBuffersByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }
}
