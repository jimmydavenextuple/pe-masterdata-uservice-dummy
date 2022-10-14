package com.hbc.transit.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.exception.TransitDomainException;
import com.hbc.transit.repository.TransitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class TransitDomainTest {

  @InjectMocks private TransitDomain transitDomain;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitRepository transitRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveTransitDetailsTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.save(any())).thenReturn(transitEntity);

    TransitEntity entity = transitDomain.saveTransitEntity(transitEntity);
    Assertions.assertEquals(transitEntity, entity);

    verify(transitRepository, times(1)).save(any());
  }

  @Test
  void saveTransitDetailsExceptionTest() {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.save(any()))
        .thenThrow(new RuntimeException("Unable to save transit data"));

    Exception exception =
        assertThrows(
            TransitDomainException.class, () -> transitDomain.saveTransitEntity(transitEntity));
    Assertions.assertEquals("Unable to save transit data", exception.getMessage());
    verify(transitRepository, times(1)).save(any());
  }

  @Test
  void getTransitDetailsTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));

    Optional<TransitEntity> optionalTransitEntity =
        transitDomain.findTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(transitEntity, optionalTransitEntity.get());

    verify(transitRepository, times(1))
        .findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void fetchDestinationGeozonesTest() throws TransitDomainException {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList()))
            .thenReturn(List.of("B1P", "M1R", "A1F"));
    List<String> response = transitDomain.fetchDestinationGeozones(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            List.of(TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals(3, response.size());
    verify(transitRepository, times(1))
            .findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList());
  }

  @Test
  void fetchDestinationGeozonesExceptionTest(){
    when(transitRepository.findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList()))
            .thenThrow(new RuntimeException("Eror while fetching DFSAs"));
    TransitDomainException e = Assertions.assertThrows(TransitDomainException.class,
            ()-> {
              transitDomain.fetchDestinationGeozones(
                      TestUtil.ORG_ID,
                      TestUtil.SOURCE_GEOZONE,
                      List.of(TestUtil.CARRIER_SERVICE_ID));
            });
    Assertions.assertEquals(TestUtil.ORG_ID, e.getOrgId());
    Assertions.assertEquals(TestUtil.SOURCE_GEOZONE, e.getSourceGeozone());
    verify(transitRepository, times(1))
            .findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList());
  }

  @Test
  void filterAndGetTransitDetailsTest() throws TransitDomainException {
    List<TransitEntity> transitEntityList = new ArrayList<>();
    transitEntityList.add(testUtil.getTransitEntities("ALL"));
    transitEntityList.add(testUtil.getTransitEntities("ALL-" + TestUtil.SERVICE_OPTION));
    transitEntityList.add(testUtil.getTransitEntities("PURO-EXPRESS"));
    when(transitRepository.findByCarrierServiceIdsWithServiceOption(
            any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    List<TransitEntity> transitEntities =
        transitDomain.filterAndGetTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "PURO-EXPRESS",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(transitEntityList.size(), transitEntities.size());

    verify(transitRepository, times(1))
        .findByCarrierServiceIdsWithServiceOption(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTestException() {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error while finding transit details"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.findTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Error while finding transit details", exception.getMessage());
    verify(transitRepository, times(1))
        .findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void transitDataDeletionTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    doNothing().when(transitRepository).delete(any());
    transitDomain.deleteTransitDetails(transitEntity);

    verify(transitRepository, times(1)).delete(any());
  }

  @Test
  void transitDeletionTestException() {
    doThrow(new RuntimeException("Error while deleting transit details"))
        .when(transitRepository)
        .delete(any());

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.deleteTransitDetails(
                    testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS)));
    Assertions.assertEquals("Error while deleting transit details", exception.getMessage());
    verify(transitRepository, times(1)).delete(any());
  }

  @Test
  void getListOfTransitDetailsTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any()))
        .thenReturn(List.of(transitEntity));

    List<TransitEntity> transitEntityList =
        transitDomain.fetchTransitList(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals(transitEntity, transitEntityList.get(0));

    verify(transitRepository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any());
  }

  @Test
  void getListOfTransitDetailsTestException() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching transit list"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.fetchTransitList(
                    TestUtil.ORG_ID,
                    TestUtil.DESTINATION_GEOZONE,
                    List.of(TestUtil.SOURCE_GEOZONE)));
    Assertions.assertEquals("Error while fetching transit list", exception.getMessage());

    verify(transitRepository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any());
  }

  @Test
  void fetchTransitEntitiesCountTest() throws TransitDomainException {
    when(transitRepository.findTransitCountByOrgIdAndCarrierServiceId(any(), any())).thenReturn(2);

    Integer transitEntitiesCount =
        transitDomain.fetchTransitEntitiesCount(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(2, transitEntitiesCount);
    verify(transitRepository, times(1)).findTransitCountByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  void fetchTransitEntitiesCountTestException() throws TransitDomainException {
    when(transitRepository.findTransitCountByOrgIdAndCarrierServiceId(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching transit entities count"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.fetchTransitEntitiesCount(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Error while fetching transit entities count", exception.getMessage());
    verify(transitRepository, times(1)).findTransitCountByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTest() throws TransitDomainException {
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(List.of(transitEntity));

    List<TransitEntity> transitEntityList =
        transitDomain.fetchTransitListForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(transitEntity, transitEntityList.get(0));

    verify(transitRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTestException() {
    when(transitRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching transit list"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.fetchTransitListForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Error while fetching transit list", exception.getMessage());

    verify(transitRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void fetchTransitListForDestinationGeoZones() throws TransitDomainException {
    when(transitRepository.findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE)))
        .thenReturn(List.of(testUtil.getTransitEntities(TestUtil.CARRIER_SERVICE_ID)));

    List<TransitEntity> transitEntities =
        transitDomain.fetchTransitListForDestinationGeoZones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE));
    Assertions.assertFalse(CollectionUtils.isEmpty(transitEntities));
    verify(transitRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(any(), any(), any());
  }

  @Test
  void fetchTransitListForDestinationGeoZonesException() throws TransitDomainException {
    when(transitRepository.findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE)))
        .thenThrow(new RuntimeException("Error while fetching transit entities"));

    Exception exception =
        Assertions.assertThrows(
            TransitDomainException.class,
            () ->
                transitDomain.fetchTransitListForDestinationGeoZones(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    List.of(TestUtil.DESTINATION_GEOZONE)));
    Assertions.assertNotNull(exception);
    verify(transitRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(any(), any(), any());
  }
}
