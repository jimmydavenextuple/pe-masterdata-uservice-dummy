package com.hbc.dataupload.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domain.dto.CarrierTransitDto;
import com.hbc.dataupload.service.CarrierTransitTimeService;
import com.hbc.dataupload.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CarrierTransitTimeControllerTest {

  @InjectMocks private CarrierTransitTimeController carrierTransitTimeController;

  @Mock private CarrierTransitTimeService carrierTransitTimeService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(carrierTransitTimeController, "defaultPageNo", 1);
    ReflectionTestUtils.setField(carrierTransitTimeController, "defaultPageSize", 15);
    ReflectionTestUtils.setField(carrierTransitTimeController, "defaultSortBy", "carrierId");
  }

  @Test
  void getCarrierTransitTimeListTest() {
    when(carrierTransitTimeService.getCarrierTransitTimeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierTransitPagePayload(2));

    ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> response =
        carrierTransitTimeController.getCarrierTransitTimeList(
            TestUtil.ORG_ID,
            Optional.of(2),
            Optional.of(2),
            Optional.of("carrierId"),
            Optional.of("DESC"));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().getPayload().getData().size());
    assertEquals(4, response.getBody().getPayload().getPagination().getTotalRecords());
    assertEquals(2, response.getBody().getPayload().getPagination().getCurrentPage());
    assertEquals(2, response.getBody().getPayload().getPagination().getTotalPages());
    assertNotNull(response.getBody().getPayload().getPagination().getPrevious());
    assertNull(response.getBody().getPayload().getPagination().getNext());

    verify(carrierTransitTimeService, times(1))
        .getCarrierTransitTimeList(any(), any(), any(), any(), any());
  }

  @Test
  void getCarrierTransitTimeListDefaultTest() {
    when(carrierTransitTimeService.getCarrierTransitTimeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getCarrierTransitPagePayload(1));

    ResponseEntity<BaseResponse<PagePayload<CarrierTransitDto>>> response =
        carrierTransitTimeController.getCarrierTransitTimeList(
            TestUtil.ORG_ID,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().getPayload().getData().size());
    assertEquals(4, response.getBody().getPayload().getPagination().getTotalRecords());
    assertEquals(1, response.getBody().getPayload().getPagination().getCurrentPage());
    assertEquals(2, response.getBody().getPayload().getPagination().getTotalPages());
    assertNotNull(response.getBody().getPayload().getPagination().getNext());
    assertNull(response.getBody().getPayload().getPagination().getPrevious());

    verify(carrierTransitTimeService, times(1))
        .getCarrierTransitTimeList(any(), any(), any(), any(), any());
  }

  @Test
  void getCarrierTransitTimeListExceptionTest() {
    when(carrierTransitTimeService.getCarrierTransitTimeList(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error in fetching carrier transit time list"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierTransitTimeController.getCarrierTransitTimeList(
                    TestUtil.ORG_ID,
                    Optional.of(1),
                    Optional.of(1),
                    Optional.empty(),
                    Optional.empty()));

    Assertions.assertEquals("Error in fetching carrier transit time list", exception.getMessage());
    verify(carrierTransitTimeService, VerificationModeFactory.times(1))
        .getCarrierTransitTimeList(any(), any(), any(), any(), any());
  }
}
