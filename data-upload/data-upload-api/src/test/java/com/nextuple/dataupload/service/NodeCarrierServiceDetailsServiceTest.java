package com.nextuple.dataupload.service;

import static org.mockito.Mockito.*;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceDetailsServiceTest {

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierFeign nodeCarrierFeign;
  @Mock private CalendarFeign calendarFeign;
  @InjectMocks private NodeCarrierServiceDetailsService nodeCarrierServiceDetailsService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getNodeCarrierServiceDetails() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getUniqueNodeCarrierServiceList(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(TestUtil.CARRIER_SERVICE_ID, "")).build());
    when(calendarFeign.getNodeCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of(testUtil.getNodeCarrierServiceCalendarResponse()))
                .build());

    PagePayload<NodeCarrierServiceResponse> nodeCarrierServiceResponsePagePayload =
        nodeCarrierServiceDetailsService.getNodeCarrierServiceDetails(
            TestUtil.ORG_ID, 1, 15, "nodeId", "ASC");

    Assertions.assertNotNull(nodeCarrierServiceResponsePagePayload);
    Assertions.assertFalse(
        CollectionUtils.isEmpty(nodeCarrierServiceResponsePagePayload.getData()));
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getUniqueNodeCarrierServiceList(any(), any());
    verify(calendarFeign, times(2)).getNodeCarrierServiceCalendar(any(), any(), any(), any());
  }
}
