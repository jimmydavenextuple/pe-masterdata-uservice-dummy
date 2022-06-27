package com.hbc.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.response.BaseResponse;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.exception.CommonServiceException;
import com.hbc.transit.exception.TransitDomainException;
import com.hbc.transit.service.TransitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TransitControllerTest {

  @InjectMocks private TransitController transitController;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitService transitService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitDataTest() throws TransitDomainException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS);
    when(transitService.addTransitInfo(any(TransitDataCreationRequest.class)))
        .thenReturn(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS));

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.addTransitData(transitDataCreationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS), responseEntity.getBody().getPayload());
    verify(transitService, times(1)).addTransitInfo(any());
  }

  @Test
  void createTransitDataExceptionTest() throws TransitDomainException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS);
    when(transitService.addTransitInfo(any(TransitDataCreationRequest.class)))
        .thenThrow(new RuntimeException("Failed to add transit data"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class, () -> transitController.addTransitData(transitDataCreationRequest));
    Assertions.assertEquals("Failed to add transit data", exception.getMessage());

    verify(transitService, times(1)).addTransitInfo(any());
  }

  @Test
  void getTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitResponse transitResponse = testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS);
    when(transitService.getTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitResponse);

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(transitResponse, responseEntity.getBody().getPayload());

    verify(transitService, times(1)).getTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsExceptionTest() throws TransitDomainException, CommonServiceException {
    when(transitService.getTransitDetails(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch transit details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));
    Assertions.assertEquals("Failed to fetch transit details", exception.getMessage());
    verify(transitService, times(1)).getTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDataUpdationRequest transitDataUpdationRequest = new TransitDataUpdationRequest();
    transitDataUpdationRequest.setTransitDays(13.5F);
    when(transitService.updateTransitDetails(
            any(), any(), any(), any(), any(TransitDataUpdationRequest.class)))
        .thenReturn(testUtil.getTransitResponse(13.5F));

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.updateTransitData(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            transitDataUpdationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitResponse(13.5F), responseEntity.getBody().getPayload());

    verify(transitService, times(1)).updateTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsExceptionTest() throws TransitDomainException, CommonServiceException {
    TransitDataUpdationRequest transitDataUpdationRequest = new TransitDataUpdationRequest();
    transitDataUpdationRequest.setTransitDays(13.5F);
    when(transitService.updateTransitDetails(
            any(), any(), any(), any(), any(TransitDataUpdationRequest.class)))
        .thenThrow(new RuntimeException("Failed to update transit details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.updateTransitData(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    transitDataUpdationRequest));
    Assertions.assertEquals("Failed to update transit details", exception.getMessage());

    verify(transitService, times(1)).updateTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void deleteTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    when(transitService.deleteTransitDetails(any(), any(), any(), any()))
        .thenReturn(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS));

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.deleteTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS), responseEntity.getBody().getPayload());

    verify(transitService, times(1)).deleteTransitDetails(any(), any(), any(), any());
  }

  @Test
  void deleteTransitDetailsExceptionTest() throws TransitDomainException, CommonServiceException {
    when(transitService.deleteTransitDetails(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to delete transit details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.deleteTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Failed to delete transit details", exception.getMessage());

    verify(transitService, times(1)).deleteTransitDetails(any(), any(), any(), any());
  }
}
