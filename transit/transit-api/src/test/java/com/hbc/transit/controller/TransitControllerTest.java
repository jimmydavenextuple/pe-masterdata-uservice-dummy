package com.hbc.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.exception.TransitDomainException;
import com.hbc.transit.service.TransitService;
import java.util.List;
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

  @Test
  void getTransitDetailsListTest() throws TransitDomainException {
    when(transitService.getListOfTransitDetails(any(), any(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS)));

    ResponseEntity<BaseResponse<List<TransitResponse>>> responseEntity =
        transitController.getTransitDetailsList(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE));

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS),
        responseEntity.getBody().getPayload().get(0));

    verify(transitService, times(1)).getListOfTransitDetails(any(), any(), any());
  }

  @Test
  void getTransitDetailsListTestException() throws TransitDomainException {
    when(transitService.getListOfTransitDetails(any(), any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch transit list"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.getTransitDetailsList(
                    TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE)));
    Assertions.assertEquals("Failed to fetch transit list", exception.getMessage());

    verify(transitService, times(1)).getListOfTransitDetails(any(), any(), any());
  }

  @Test
  void getTransitTimeEntriesTest() throws TransitDomainException {
    when(transitService.getTransitTimeEntries(any(), any()))
        .thenReturn(
            testUtil.getTransitTimeEntriesDto(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    ResponseEntity<BaseResponse<TransitTimeEntriesDto>> response =
        transitController.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());

    verify(transitService, times(1)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getTransitDetailsListWithoutSourcingNodesTest()
      throws TransitDomainException, CommonServiceException {
    when(transitService.getListOfTransitDetailsWithoutSourcingNodes(any(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS)));

    List<TransitResponse> responseEntity =
        transitController.getTransitDetailsListWithoutSourceNodes(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(1, responseEntity.size());
    verify(transitService, times(1)).getListOfTransitDetailsWithoutSourcingNodes(any(), any());
  }

  @Test
  void getTransitDetailsListWithoutSourcingNodesTestException()
      throws TransitDomainException, CommonServiceException {
    when(transitService.getListOfTransitDetailsWithoutSourcingNodes(any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch transit list"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.getTransitDetailsListWithoutSourceNodes(
                    TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals("Failed to fetch transit list", exception.getMessage());

    verify(transitService, times(1)).getListOfTransitDetailsWithoutSourcingNodes(any(), any());
  }
}
