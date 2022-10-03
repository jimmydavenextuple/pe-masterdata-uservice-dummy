package com.hbc.dataupload.service;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domain.dto.NodeListDto;
import com.hbc.dataupload.domain.dto.NodeServiceOptionDto;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionalNodesDetailsServiceTest {

  @InjectMocks private RegionalNodesDetailsService regionalNodesDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierFeign nodeCarrierFeign;
  @Mock private CalendarFeign calendarFeign;

  @Test
  void getNodeServiceOptionTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(calendarFeign.handleGetNodeCalendar(any(),any())).thenReturn((testUtil.getBaseResponseOfNodeCalendarList()));
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
}
