package com.hbc.transit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.TransitDomain;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.exception.TransitDomainException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class TransitServiceTest {

  @InjectMocks private TransitService transitService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitDomain transitDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(testUtil.TRANSIT_DAYS);
    when(transitDomain.saveTransitEntity(any(TransitEntity.class)))
        .thenReturn(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));

    TransitResponse transitResponse =
        transitService.addTransitInfo(
            testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS));
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    Assertions.assertEquals(
        transitDataCreationRequest.getBufferDays(), transitResponse.getBufferDays());
    verify(transitDomain, times(1)).saveTransitEntity(any(TransitEntity.class));
  }

  @Test
  void addTransitDetailsNullBufferDaysTest() throws TransitDomainException, CommonServiceException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS);
    transitDataCreationRequest.setBufferDays(null);
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    transitEntity.setBufferDays(null);
    when(transitDomain.saveTransitEntity(any(TransitEntity.class))).thenReturn(transitEntity);

    TransitResponse transitResponse = transitService.addTransitInfo(transitDataCreationRequest);
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    Assertions.assertNull(transitResponse.getBufferDays());
    verify(transitDomain, times(1)).saveTransitEntity(any(TransitEntity.class));
  }

  @Test
  void updateTransitBufferDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity3(TestUtil.BUFFER_DAYS);
    TransitBufferCreationRequest transitBufferCreationRequest =
        testUtil.getTransitBufferCreationRequest(5.0);
    when(transitDomain.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));
    when(transitDomain.saveTransitEntity(any())).thenReturn(testUtil.getTransitEntity3(5.0));

    TransitResponse transitResponse =
        transitService.updateTransitBufferDetails(transitBufferCreationRequest);
    Assertions.assertEquals(testUtil.getTransitResponse2(5.0), transitResponse);

    verify(transitDomain, times(1)).saveTransitEntity(any(TransitEntity.class));
    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitBufferDetailsTestException() throws TransitDomainException {

    TransitBufferCreationRequest transitBufferCreationRequest = new TransitBufferCreationRequest();
    transitBufferCreationRequest.setBufferDays(5.0);
    when(transitDomain.findTransitDetails(any(), any(), any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitService.updateTransitBufferDetails(transitBufferCreationRequest));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());

    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitBufferDetailsNegativeTransitSumTestException() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity3(TestUtil.BUFFER_DAYS);
    TransitBufferCreationRequest transitBufferCreationRequest =
        testUtil.getTransitBufferCreationRequest(-15.0);
    when(transitDomain.findTransitDetails(any(), any(), any(), any()))
        .thenReturn((Optional.of(transitEntity)));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitService.updateTransitBufferDetails(transitBufferCreationRequest));
    Assertions.assertEquals(
        "The sum of transit and buffer days is less than 0", exception.getMessage());

    verify(transitDomain, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    TransitDataUpdationRequest transitDataUpdationRequest =
        testUtil.getTransitDataUpdationRequest(13.5F);
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
    TransitEntity transitEntity = testUtil.getTransitEntity2(TestUtil.TRANSIT_DAYS);
    when(transitDomain.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));

    TransitResponse transitResponse =
        transitService.deleteTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(testUtil.getTransitResponse2(TestUtil.TRANSIT_DAYS), transitResponse);
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

  @Test
  void getListOfTransitDetailsTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitDomain.fetchTransitList(any(), any(), any())).thenReturn(List.of(transitEntity));

    List<TransitResponse> transitResponse =
        transitService.getListOfTransitDetails(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals(
        transitEntity.getTransitDays(), transitResponse.get(0).getTransitDays());
    verify(transitDomain, times(1)).fetchTransitList(any(), any(), any());
  }

  @Test
  void getTransitTimeEntriesTest() throws TransitDomainException {
    when(transitDomain.fetchTransitEntitiesCount(any(), any())).thenReturn(5);

    TransitTimeEntriesDto transitResponse =
        transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(TestUtil.ORG_ID, transitResponse.getOrgId());
    Assertions.assertEquals(5, transitResponse.getTotalRecords());

    verify(transitDomain, times(1)).fetchTransitEntitiesCount(any(), any());
  }

  @Test
  void getTransitTimeZeroEntriesTest() throws TransitDomainException {
    when(transitDomain.fetchTransitEntitiesCount(any(), any())).thenReturn(0);

    TransitTimeEntriesDto transitResponse =
        transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(TestUtil.ORG_ID, transitResponse.getOrgId());
    Assertions.assertEquals(0, transitResponse.getTotalRecords());

    verify(transitDomain, times(1)).fetchTransitEntitiesCount(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTest()
      throws TransitDomainException, CommonServiceException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitDomain.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(List.of(transitEntity));

    List<TransitResponse> transitResponse =
        transitService.getListOfTransitDetailsForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(
        transitEntity.getTransitDays(), transitResponse.get(0).getTransitDays());
    verify(transitDomain, times(1)).fetchTransitListForDestinationGeoZone(any(), any());
  }

  @Test
  void getTransitDetailsForDestinationGeoZoneTestException() throws TransitDomainException {
    List<TransitEntity> transitEntityList = Collections.<TransitEntity>emptyList();
    when(transitDomain.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(transitEntityList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.getListOfTransitDetailsForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitDomain, times(1)).fetchTransitListForDestinationGeoZone(any(), any());
  }

  @Test
  void getTransitDetailsForDestinationGeozones() throws TransitDomainException {
    when(transitDomain.fetchTransitListForDestinationGeoZones(any(), any(), any()))
        .thenReturn(List.of(testUtil.getTransitEntities(TestUtil.CARRIER_SERVICE_ID)));

    List<TransitResponse> responses =
        transitService.getTransitDetailsForDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE));
    Assertions.assertFalse(CollectionUtils.isEmpty(responses));
    verify(transitDomain, Mockito.times(1))
        .fetchTransitListForDestinationGeoZones(any(), any(), any());
  }
}
