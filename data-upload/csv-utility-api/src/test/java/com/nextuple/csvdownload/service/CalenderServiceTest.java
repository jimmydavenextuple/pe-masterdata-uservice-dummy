package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalenderServiceTest {

  @Mock private CalendarFeign calendarFeign;

  @InjectMocks private CalenderService calenderService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCarrierServiceCalenderTest() {
    CarrierServiceCalendarResponse response = testUtil.getCarrierServiceCalendarResponse();
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(List.of(response)).build());
    Assertions.assertDoesNotThrow(
        () ->
            calenderService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getCarrierServiceCalenderTestNullResponse() {
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString())).thenReturn(null);
    Assertions.assertThrows(
        CarrierServiceException.class,
        () ->
            calenderService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getCarrierServiceCalenderTestEmptyResponse() {
    when(calendarFeign.getCarrierServiceCalendar(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(new ArrayList<>()).build());
    Assertions.assertThrows(
        CarrierServiceException.class,
        () ->
            calenderService.getCarrierServiceCalender(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(calendarFeign, times(1)).getCarrierServiceCalendar(anyString(), anyString());
  }

  @Test
  void getNodeCalendarTest() {
    when(calendarFeign.handleGetNodeCalendar(any(), any()))
        .thenReturn(testUtil.getNodeCalendarBaseResponse());

    List<NodeCalendarResponse> responses =
        calenderService.getNodeCalendar(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertEquals(1, responses.size());
    assertEquals(TestUtil.ORG_ID, responses.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responses.get(0).getNodeId());
    verify(calendarFeign, times(1)).handleGetNodeCalendar(any(), any());
  }
}
