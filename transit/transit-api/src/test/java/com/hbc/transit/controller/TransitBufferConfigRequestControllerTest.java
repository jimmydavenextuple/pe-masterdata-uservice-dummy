package com.hbc.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.service.TransitBufferConfigRequestService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TransitBufferConfigRequestControllerTest {

  @InjectMocks private TransitBufferConfigRequestController transitBufferConfigRequestController;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitBufferConfigRequestTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest();
    when(transitBufferConfigRequestService.createTransitBufferRequest(
            any(TransitBufferConfigRequest.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED));

    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> responseEntity =
        transitBufferConfigRequestController.createTransitBufferConfigRequest(
            transitBufferConfigRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        responseEntity.getBody().getPayload());
    verify(transitBufferConfigRequestService, times(1)).createTransitBufferRequest(any());
  }

  @Test
  void createTransitBufferConfigRequestExceptionTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest();
    when(transitBufferConfigRequestService.createTransitBufferRequest(
            any(TransitBufferConfigRequest.class)))
        .thenThrow(new RuntimeException("Failed to create transit buffer config request"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.createTransitBufferConfigRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Failed to create transit buffer config request", exception.getMessage());

    verify(transitBufferConfigRequestService, times(1)).createTransitBufferRequest(any());
  }

  @Test
  void updateTransitBufferConfigRequestStatusTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            any(), any(TransitBufferConfigRequestStatusEnum.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigResponse(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));

    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> responseEntity =
        transitBufferConfigRequestController.updateTransitBufferConfigRequestStatus(
            TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        responseEntity.getBody().getPayload());
    verify(transitBufferConfigRequestService, times(1))
        .updateTransitBufferRequestStatus(any(), any());
  }

  @Test
  void updateTransitBufferConfigRequestStatusExceptionTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            any(), any(TransitBufferConfigRequestStatusEnum.class)))
        .thenThrow(
            new RuntimeException(
                "Failed to process transit buffer config status updation request"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.updateTransitBufferConfigRequestStatus(
                    TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS));
    Assertions.assertEquals(
        "Failed to process transit buffer config status updation request", exception.getMessage());
    verify(transitBufferConfigRequestService, times(1))
        .updateTransitBufferRequestStatus(any(), any());
  }

  @Test
  void getTransitBufferConfigRequestsTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.fetchTransitBufferRequests(anyString(), anyString()))
        .thenReturn(
            List.of(
                testUtil.getTransitBufferConfigResponse(
                    TransitBufferConfigRequestStatusEnum.INPROGRESS)));

    ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>> responseEntity =
        transitBufferConfigRequestController.getTransitBufferConfigRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        responseEntity.getBody().getPayload().get(0));
    verify(transitBufferConfigRequestService, times(1))
        .fetchTransitBufferRequests(anyString(), anyString());
  }

  @Test
  void getTransitBufferConfigRequestsExceptionTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.fetchTransitBufferRequests(anyString(), anyString()))
        .thenThrow(new RuntimeException("Failed to get transit buffer config requests"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.getTransitBufferConfigRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Failed to get transit buffer config requests", exception.getMessage());
    verify(transitBufferConfigRequestService, times(1))
        .fetchTransitBufferRequests(anyString(), anyString());
  }
}
