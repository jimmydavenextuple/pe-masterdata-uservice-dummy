package com.nextuple.pe.masterdata.calendar.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CalendarValidationTest {
  @InjectMocks CalendarValidation calendarValidation;
  @InjectMocks TestUtil testUtil;

  @Mock CarrierFeign carrierFeign;
  @Mock NodeFeign nodeFeign;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Valid node")
  void validateNodeIdTest1() {
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.TRUE));
    Assertions.assertDoesNotThrow(() -> calendarValidation.validateNodeId("node1", "BAY"));
  }

  @Test
  @DisplayName("Inactive node")
  void validateNodeIdTest2() {
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.FALSE));
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class, () -> calendarValidation.validateNodeId("node1", "BAY"));
    Assertions.assertEquals(0x1780, e.getErrorCode());
  }

  @Test
  @DisplayName("Unsuccessful node response")
  void validateNodeIdTest3() {
    BaseResponse<NodeResponse> response = testUtil.getNodeDetails(Boolean.TRUE);
    response.setSuccess(false);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(response);
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class, () -> calendarValidation.validateNodeId("node1", "BAY"));
    Assertions.assertEquals(0x1780, e.getErrorCode());
  }

  @Test
  @DisplayName("Null node response")
  void validateNodeIdTest4() {
    BaseResponse<NodeResponse> response = testUtil.getNodeDetails(Boolean.TRUE);
    response.setPayload(null);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(response);
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class, () -> calendarValidation.validateNodeId("node1", "BAY"));
    Assertions.assertEquals(0x1780, e.getErrorCode());
  }

  @Test
  @DisplayName("Valid carrier service id")
  void validateCarrierServiceIdTest1() {
    when(carrierFeign.getCarrierServiceListByOrgId(any()))
        .thenReturn(testUtil.getCarrierServiceResponse());
    Assertions.assertDoesNotThrow(
        () ->
            calendarValidation.validateCarrierServiceId(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
  }

  @Test
  @DisplayName("Invalid carrier service id")
  void validateCarrierServiceIdTest2() {
    when(carrierFeign.getCarrierServiceListByOrgId(any()))
        .thenReturn(testUtil.getCarrierServiceResponse());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> calendarValidation.validateCarrierServiceId(TestUtil.ORG_ID, "INVALID"));
    Assertions.assertEquals(0x1781, e.getErrorCode());
  }
}
