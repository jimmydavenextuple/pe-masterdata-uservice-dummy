package com.nextuple.pe.masterdata.service.domain;

import com.nextuple.pe.masterdata.domain.TransitDomain;
import com.nextuple.pe.masterdata.domain.entity.TransitEntity;
import com.nextuple.pe.masterdata.domain.repository.TransitRepository;
import com.nextuple.pe.masterdata.exception.TransitDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
  void filterAndGetTransitDetailsTest1() throws TransitDomainException {
    List<TransitEntity> transitEntityList = new ArrayList<>();
    transitEntityList.add(testUtil.getTransitEntities("ALL"));
    transitEntityList.add(testUtil.getTransitEntities("ALL-" + TestUtil.SERVICE_OPTION));
    when(transitRepository.findByCarrierServiceIdWithServiceOption(
            any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    List<TransitEntity> transitEntities =
        transitDomain.filterAndGetTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "ALL",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(transitEntityList.size(), transitEntities.size());

    verify(transitRepository, times(1))
        .findByCarrierServiceIdWithServiceOption(any(), any(), any(), any(), any());
  }

  @Test
  void filterAndGetTransitDetailsTest2() throws TransitDomainException {
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
  void nodeDeletionTestException() {
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
}
