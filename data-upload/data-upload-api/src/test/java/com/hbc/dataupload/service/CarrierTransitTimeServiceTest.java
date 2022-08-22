package com.hbc.dataupload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.transit.domain.feign.TransitFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierTransitTimeServiceTest {

  @InjectMocks private CarrierTransitTimeService carrierTransitTimeService;
  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierFeign carrierFeign;
  @Mock private CalendarFeign calendarFeign;
  @Mock private TransitFeign transitFeign;

  @Test
  void getCarrierTransitTimeListSuccessTest1() {

    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceListWithPaginationBaseResponse());
    when(calendarFeign.getCarrierServiceCalendar(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(testUtil.getCarrierServiceCalendarBaseResponse());
    when(transitFeign.getTransitTimeEntries(any(), any()))
        .thenReturn(testUtil.getTransitTimeEntriesDtoBaseResponse(5));

    PagePayload<CarrierTransitDto> response =
        carrierTransitTimeService.getCarrierTransitTimeList(
            TestUtil.ORG_ID, 1, 1, "carrierId", "DESC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertTrue(response.getData().get((0)).getIsCarrierActive());
    assertTrue(response.getData().get((0)).getIsCalendarAssigned());

    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getCarrierServiceCalendar(any(), any());
    verify(transitFeign, times(2)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getCarrierTransitTimeListSuccessTest2() {

    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceListWithPaginationBaseResponse());
    when(calendarFeign.getCarrierServiceCalendar(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenThrow(new RuntimeException("Carrier Calendar not found"));
    when(transitFeign.getTransitTimeEntries(any(), any()))
        .thenReturn(testUtil.getTransitTimeEntriesDtoBaseResponse(0));

    PagePayload<CarrierTransitDto> response =
        carrierTransitTimeService.getCarrierTransitTimeList(
            TestUtil.ORG_ID, 1, 1, "carrierId", "ASC");

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertFalse(response.getData().get((0)).getIsCarrierActive());
    assertFalse(response.getData().get((0)).getIsCalendarAssigned());

    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(calendarFeign, times(2)).getCarrierServiceCalendar(any(), any());
    verify(transitFeign, times(2)).getTransitTimeEntries(any(), any());
  }
}
