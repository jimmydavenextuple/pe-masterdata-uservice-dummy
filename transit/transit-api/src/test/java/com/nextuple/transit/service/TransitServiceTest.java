package com.nextuple.transit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.TransitDomain;
import com.nextuple.transit.domain.entity.TransitEntity;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.exception.CommonServiceException;
import com.nextuple.transit.exception.TransitDomainException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitServiceTest {

  @InjectMocks private TransitService transitService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitDomain transitDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addTransitDetailsTest() throws TransitDomainException {
    when(transitDomain.saveTransitEntity(any(TransitEntity.class)))
        .thenReturn(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));

    TransitResponse transitResponse =
        transitService.addTransitInfo(
            testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS));
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    verify(transitDomain, times(1)).saveTransitEntity(any(TransitEntity.class));
  }

  @Test
  void updateTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    TransitDataUpdationRequest transitDataUpdationRequest = new TransitDataUpdationRequest();
    transitDataUpdationRequest.setTransitDays(13.5F);
    when(transitDomain.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));
    when(transitDomain.saveTransitEntity(any())).thenReturn(testUtil.getTransitEntity(13.5F));

    TransitResponse transitResponse =
        transitService.updateTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            transitDataUpdationRequest);
    Assertions.assertEquals(testUtil.getTransitResponse(13.5F), transitResponse);

    verify(transitDomain, times(1)).saveTransitEntity(any(TransitEntity.class));
    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsTestException() throws TransitDomainException {

    TransitDataUpdationRequest transitDataUpdationRequest = new TransitDataUpdationRequest();
    transitDataUpdationRequest.setTransitDays(13.5F);
    when(transitDomain.findTransitDetails(any(), any(), any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.updateTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    transitDataUpdationRequest));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());

    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest1() throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitDomain.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(List.of(transitEntity));

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS), transitResponse);
    verify(transitDomain, times(1)).filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest2() throws TransitDomainException, CommonServiceException {
    List<TransitEntity> transitEntityList = new ArrayList<>();
    transitEntityList.add(testUtil.getTransitEntities("ALL"));
    transitEntityList.add(testUtil.getTransitEntities("ALL-" + TestUtil.SERVICE_OPTION));
    when(transitDomain.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "ALL",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(
        "ALL-" + TestUtil.SERVICE_OPTION, transitResponse.getCarrierServiceId());
    verify(transitDomain, times(1)).filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest3() throws TransitDomainException, CommonServiceException {
    List<TransitEntity> transitEntityList = new ArrayList<>();
    transitEntityList.add(testUtil.getTransitEntities("ALL"));
    transitEntityList.add(testUtil.getTransitEntities("ALL-" + TestUtil.SERVICE_OPTION));
    transitEntityList.add(testUtil.getTransitEntities("PURO-EXPRESS"));
    when(transitDomain.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "PURO-EXPRESS",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals("PURO-EXPRESS", transitResponse.getCarrierServiceId());
    verify(transitDomain, times(1)).filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTestException() throws TransitDomainException {
    List<TransitEntity> transitEntityList = Collections.<TransitEntity>emptyList();
    when(transitDomain.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitDomain, times(1)).filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void deleteTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitDomain.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));

    TransitResponse transitResponse =
        transitService.deleteTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS), transitResponse);
    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void deleteTransitDetailsTestException() throws TransitDomainException {
    when(transitDomain.findTransitDetails(any(), any(), any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.deleteTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }
}
