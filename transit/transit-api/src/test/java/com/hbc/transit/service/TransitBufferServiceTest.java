package com.hbc.transit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitBufferEntity;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.repository.TransitBufferRepository;
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

  @Test
  void saveTransitBufferTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(transitBufferRepository.save(any())).thenReturn(transitBufferEntity);

    TransitBufferResponse response =
        transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void saveTransitBufferTestException() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(transitBufferRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Unable to create transit buffer", ex.getMessage());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void saveExistingTransitBufferExceptionTest() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));

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
