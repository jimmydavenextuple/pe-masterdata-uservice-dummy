package com.hbc.transit.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.inbound.DistinctGeozonesResponse;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.inbound.TransitDetailsRequest;
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
import org.springframework.util.CollectionUtils;

class TransitControllerTest {

  @InjectMocks private TransitController transitController;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitService transitService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitDataTest() throws TransitDomainException, CommonServiceException {
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
  void createTransitDataExceptionTest() throws TransitDomainException, CommonServiceException {
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
  void getDistinctDestinationFSAListTest() throws TransitDomainException {
    List<String> dFSAResponse = List.of("A1P", "B1P", "M1R");
    when(transitService.getDistinctDFSA(any(), any(), anyList())).thenReturn(dFSAResponse);

    ResponseEntity<BaseResponse<List<String>>> responseEntity =
        transitController.getDistinctDestinationGeoZones(
            TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, List.of(TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(dFSAResponse, responseEntity.getBody().getPayload());

    verify(transitService, times(1)).getDistinctDFSA(any(), any(), anyList());
  }

  @Test
  void getDistinctDestinationFSAListExceptionTest() throws TransitDomainException {
    when(transitService.getDistinctDFSA(any(), any(), anyList()))
        .thenThrow(new RuntimeException("Failed to fetch DFSAs"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.getDistinctDestinationGeoZones(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    List.of(TestUtil.CARRIER_SERVICE_ID)));
    Assertions.assertEquals("Failed to fetch DFSAs", exception.getMessage());
    verify(transitService, times(1)).getDistinctDFSA(any(), any(), anyList());
  }

  @Test
  void updateTransitBufferDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitBufferCreationRequest transitBufferCreationRequest = new TransitBufferCreationRequest();
    transitBufferCreationRequest.setBufferDays(3.0);
    when(transitService.updateTransitBufferDetails(any(TransitBufferCreationRequest.class)))
        .thenReturn(testUtil.getTransitResponse2(5.0));

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.updateTransitBufferDetails(transitBufferCreationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitResponse2(5.0), responseEntity.getBody().getPayload());

    verify(transitService, times(1)).updateTransitBufferDetails(any());
  }

  @Test
  void updateTransitBufferDetailsExceptionTest()
      throws TransitDomainException, CommonServiceException {
    TransitBufferCreationRequest transitBufferCreationRequest = new TransitBufferCreationRequest();
    transitBufferCreationRequest.setBufferDays(3.0);
    when(transitService.updateTransitBufferDetails(any(TransitBufferCreationRequest.class)))
        .thenThrow(new RuntimeException("Failed to update transit buffer details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> transitController.updateTransitBufferDetails(transitBufferCreationRequest));
    Assertions.assertEquals("Failed to update transit buffer details", exception.getMessage());

    verify(transitService, times(1)).updateTransitBufferDetails(any());
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
  void getTransitDetailsListForDestinationGeoZoneTest() throws CommonServiceException {
    when(transitService.getListOfTransitDetailsForDestinationGeoZone(any(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS)));

    BaseResponse<List<TransitResponse>> responseEntity =
        transitController.getTransitDetailsListForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(1, responseEntity.getPayload().size());
    verify(transitService, times(1)).getListOfTransitDetailsForDestinationGeoZone(any(), any());
  }

  @Test
  void getTransitDetailsListForDestinationGeoZoneTestException() throws CommonServiceException {
    when(transitService.getListOfTransitDetailsForDestinationGeoZone(any(), any()))
        .thenThrow(new RuntimeException("Failed to fetch transit list"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitController.getTransitDetailsListForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals("Failed to fetch transit list", exception.getMessage());

    verify(transitService, times(1)).getListOfTransitDetailsForDestinationGeoZone(any(), any());
  }

  @Test
  void getTransitTimeDetailsForDestinationGeoZonesList() throws TransitDomainException {
    TransitDetailsRequest transitDetailsRequest = new TransitDetailsRequest();
    transitDetailsRequest.setDestinationGeozones(List.of(TestUtil.DESTINATION_GEOZONE));
    when(transitService.getTransitDetailsForDestinationGeozones(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    ResponseEntity<BaseResponse<List<TransitResponse>>> responseEntity =
        transitController.getTransitTimeDetailsForDestinationGeoZonesList(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, transitDetailsRequest);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertFalse(CollectionUtils.isEmpty(responseEntity.getBody().getPayload()));
    verify(transitService, times(1))
        .getTransitDetailsForDestinationGeozones(anyString(), anyString(), any());
  }

  @Test
  void deleteBufferDays() throws TransitDomainException {
    TransitResponse transitResponse = testUtil.getTransitResponse(5F);
    transitResponse.setBufferDays(0D);
    when(transitService.updateTransitBufferDays(any(), any(), any(), any()))
        .thenReturn(transitResponse);

    ResponseEntity<BaseResponse<TransitResponse>> responseEntity =
        transitController.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertNotNull(responseEntity.getBody().getPayload());
    verify(transitService, times(1)).updateTransitBufferDays(any(), any(), any(), any());
  }

  @Test
  void getDistinctSourceAndDestinationGeozones() throws TransitDomainException {
    when(transitService.getDistinctSourceAndDestinationGeoZones(anyString(), anyString()))
        .thenReturn(testUtil.geozonesResponse());

    ResponseEntity<BaseResponse<DistinctGeozonesResponse>> responseEntity =
        transitController.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertNotNull(responseEntity.getBody().getPayload());
    Assertions.assertFalse(
        CollectionUtils.isEmpty(responseEntity.getBody().getPayload().getDestinationGeozones()));
    Assertions.assertFalse(
        CollectionUtils.isEmpty(responseEntity.getBody().getPayload().getSourceGeozones()));
    verify(transitService, times(1))
        .getDistinctSourceAndDestinationGeoZones(anyString(), anyString());
  }
}
