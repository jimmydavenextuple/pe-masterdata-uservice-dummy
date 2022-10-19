package com.hbc.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.domain.feign.NodeFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingTimeBufferServiceTest {

  @InjectMocks private ProcessingTimeBufferService processingTimeBufferService;

  @Mock private NodeFeign nodeFeign;

  @Mock private NodeCarrierFeign nodeCarrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getProcessingTimeBuffersTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(
            TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertEquals(
        2.5, response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNotNull(
        response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertEquals(
        "Active", response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getProcessingTimeBuffersStatusInactiveTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(
            TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertEquals(2, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertEquals(
        2.5, response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNotNull(
        response.getData().get((0)).getProcessingTimeBuffers().get(1).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(1).getBufferEndDate());
    assertEquals(
        "Inactive", response.getData().get((0)).getProcessingTimeBuffers().get(1).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getProcessingTimeBuffersNullValuesTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponseWithNullValues());

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(
            TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(1, response.getData().get((0)).getServiceOptions().size());
    assertEquals(1, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getProcessingTimeBuffersPartialNullValuesTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponseWithPartialNullValues());

    PagePayload<ProcessingTimeBufferResponse> response =
        processingTimeBufferService.getProcessingTimeBuffers(
            TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(1, response.getData().get((0)).getServiceOptions().size());
    assertEquals(1, response.getData().get((0)).getProcessingTimeBuffers().size());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferHours());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferStartDate());
    assertNotNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getBufferEndDate());
    assertNull(response.getData().get((0)).getProcessingTimeBuffers().get(0).getStatus());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }
}
