package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionalNodesDetailsServiceTest {

  @InjectMocks private RegionalNodesDetailsService regionalNodesDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierFeign nodeCarrierFeign;
  @Mock private CalendarFeign calendarFeign;

  @Test
  void getNodeServiceOptionTest1() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(calendarFeign.handleGetNodeCalendar(any(), any()))
        .thenReturn((testUtil.getBaseResponseOfNodeCalendarList()));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  void getNodeServiceOptionTest2() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCalendarResponse>> nodeCalendarResponse =
        testUtil.getBaseResponseOfNodeCalendarList();
    nodeCalendarResponse.setPayload(Collections.emptyList());
    when(calendarFeign.handleGetNodeCalendar(any(), any())).thenReturn((nodeCalendarResponse));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(TestUtil.ORG_ID, 1, 1, "nodeId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertNull(response.getData().get(0).getNodeWorkingCalendar());
    assertEquals(2, response.getData().get((0)).getServiceOptions().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }
}
