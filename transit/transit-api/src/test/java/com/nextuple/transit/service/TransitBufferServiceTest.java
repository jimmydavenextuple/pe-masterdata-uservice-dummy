package com.nextuple.transit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.entity.TransitBufferEntity;
import com.nextuple.transit.domain.entity.TransitEntity;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.repository.TransitBufferRepository;
import com.nextuple.transit.repository.TransitRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitBufferServiceTest {

  @InjectMocks private TransitBufferService transitBufferService;

  @Mock private TransitBufferRepository transitBufferRepository;

  @InjectMocks private TestUtil testUtil;
  @Mock private TransitRepository transitRepository;
  @Mock private CarrierFeign carrierFeign;
  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @Test
  void saveTransitBufferTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    when(transitBufferRepository.save(any())).thenReturn(transitBufferEntity);
    TransitBufferResponse response =
        transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1)).save(any());
    verify(transitRepository, times(1))
        .findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void saveTransitBufferTestException() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(transitBufferRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Unable to create transit buffer", ex.getMessage());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void saveTransitBufferEmptyTransitEntityTest() {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit details not found", ex.getMessage());
  }

  @Test
  void saveExistingTransitBufferExceptionTest() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit Buffer details already present", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).save(any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));
    when(transitBufferRepository.save(any())).thenReturn(transitBufferEntity);

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    TransitBufferResponse response =
        transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void updateTransitBufferExceptionTest() {
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).save(any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneTest() throws CommonServiceException {
    List<TransitBufferEntity> transitBufferEntities =
        List.of(testUtil.getTransitBufferEntity(TestUtil.ORG_ID));
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferEntities);

    List<TransitBufferResponse> responses =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferEntities.size(), responses.size());
    assertEquals(transitBufferEntities.get(0).getOrgId(), responses.get(0).getOrgId());
    assertEquals(
        transitBufferEntities.get(0).getCarrierServiceId(), responses.get(0).getCarrierServiceId());
    assertEquals(
        transitBufferEntities.get(0).getSourceGeozone(), responses.get(0).getSourceGeozone());
    assertEquals(
        transitBufferEntities.get(0).getDestinationGeozone(),
        responses.get(0).getDestinationGeozone());
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneExceptionTest() {
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching"));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));

    assertEquals("Unable to fetch transit buffer details", ex.getMessage());
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void deleteTransitBufferDetailsTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));
    doNothing().when(transitBufferRepository).delete(any());

    TransitBufferResponse response =
        transitBufferService.deleteTransitBufferDetails(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(1)).delete(any());
  }

  @Test
  void deleteTransitBufferDetailsExceptionTest() {
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.deleteTransitBufferDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).delete(any());
  }
}
