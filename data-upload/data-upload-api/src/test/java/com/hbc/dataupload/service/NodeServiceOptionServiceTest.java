package com.hbc.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domian.dto.NodeServiceOptionDto;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionServiceTest {

  @InjectMocks private NodeServiceOptionService nodeServiceOptionService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;

  @Mock private NodeCarrierFeign nodeCarrierFeign;

  @Test
  void getNodeServiceOptionTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsActive());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());
    assertNotNull(response.getData().get(0).getProcessingTime());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }

  @Test
  void getNodeServiceOptionTest2() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    BaseResponse<List<NodeCarrierResponse>> nodeCarrierListResponse =
        testUtil.getBaseResponseOfNodeCarrierListResponse();
    nodeCarrierListResponse.setPayload(Collections.emptyList());

    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());

    when(nodeCarrierFeign.getNodeCarrierList(any(), any())).thenReturn(nodeCarrierListResponse);

    PagePayload<NodeServiceOptionDto> response =
        nodeServiceOptionService.getNodeServiceOption(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsActive());
    assertEquals(0, response.getData().get((0)).getServiceOptions().size());
    assertEquals(0, response.getData().get(0).getProcessingTime().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
  }
}
