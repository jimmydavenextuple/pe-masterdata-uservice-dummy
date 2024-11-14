/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.entity.TransitEntity;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.mapper.TransitEntityMapper;
import com.nextuple.transit.persistence.repository.TransitRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

class TransitPersistenceServiceImplTest {

  @InjectMocks private TransitPersistenceServiceImpl transitPersistenceService;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitRepository transitRepository;

  @Mock private TransitEntityMapper transitEntityMapper;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(transitPersistenceService, "repository", transitRepository);
    ReflectionTestUtils.setField(transitPersistenceService, "mapper", transitEntityMapper);
  }

  @Test
  void saveTransitDetailsTest() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitRepository.save(any()))
        .thenReturn(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitEntityMapper.toDomain(any(TransitEntity.class)))
        .thenReturn(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    TransitDomainDto domainDto = transitPersistenceService.saveTransitDomainDto(transitDomainDto);
    Assertions.assertEquals(transitDomainDto, domainDto);
    verify(transitRepository, times(1)).save(any());
  }

  @Test
  void saveTransitDetailsExceptionTest() {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitRepository.save(any()))
        .thenThrow(new RuntimeException("Unable to save transit data"));
    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () -> transitPersistenceService.saveTransitDomainDto(transitDomainDto));
    Assertions.assertEquals("Unable to save transit data", exception.getMessage());
    verify(transitRepository, times(1)).save(any());
  }

  @Test
  void getTransitDetailsTest() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);

    when(transitRepository.findById(any()))
        .thenReturn(Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS)));

    when(transitEntityMapper.toDomain(any(TransitEntity.class)))
        .thenReturn(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    Optional<TransitDomainDto> optionalTransitEntity =
        transitPersistenceService.findTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(transitDomainDto, optionalTransitEntity.get());

    verify(transitRepository, times(1)).findById(any());
  }

  @Test
  void fetchDestinationGeozonesTest() throws TransitDomainException {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList()))
        .thenReturn(List.of("B1P", "M1R", "A1F"));
    List<String> response =
        transitPersistenceService.fetchDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, List.of(TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals(3, response.size());
    verify(transitRepository, times(1))
        .findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList());
  }

  @Test
  void fetchDestinationGeozonesExceptionTest() {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndCarrierServiceIds(any(), any(), anyList()))
        .thenThrow(new RuntimeException("Eror while fetching DFSAs"));
    TransitDomainException e =
        assertThrows(
            TransitDomainException.class,
            () -> {
              transitPersistenceService.fetchDestinationGeozones(
                  TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, List.of(TestUtil.CARRIER_SERVICE_ID));
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

    List<TransitDomainDto> transitDomainList = new ArrayList<>();
    transitDomainList.add(testUtil.getTransitDomainDtos("ALL"));
    transitDomainList.add(testUtil.getTransitDomainDtos("ALL-" + TestUtil.SERVICE_OPTION));
    transitDomainList.add(testUtil.getTransitDomainDtos("PURO-EXPRESS"));

    when(transitRepository.findByCarrierServiceIdsWithServiceOption(
            any(), any(), any(), any(), any()))
        .thenReturn(transitEntityList);

    when(transitEntityMapper.toDomain(anyList())).thenReturn(transitDomainList);

    List<TransitDomainDto> transitDomainDtos =
        transitPersistenceService.filterAndGetTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "PURO-EXPRESS",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(transitEntityList.size(), transitDomainDtos.size());

    verify(transitRepository, times(1))
        .findByCarrierServiceIdsWithServiceOption(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTestException() {
    when(transitRepository.findById(any()))
        .thenThrow(new RuntimeException("Error while finding transit details"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.findTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Error while finding transit details", exception.getMessage());
    verify(transitRepository, times(1)).findById(any());
  }

  @Test
  void transitDataDeletionTest() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    doNothing().when(transitRepository).delete(any());
    transitPersistenceService.deleteTransitDetails(transitDomainDto);

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
                transitPersistenceService.deleteTransitDetails(
                    testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS)));
    Assertions.assertEquals("Error while deleting transit details", exception.getMessage());
    verify(transitRepository, times(1)).delete(any());
  }

  @Test
  void getListOfTransitDetailsTest() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitEntityMapper.toDomain(anyList())).thenReturn(List.of(transitDomainDto));

    when(transitRepository.findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any()))
        .thenReturn(List.of(transitEntity));

    List<TransitDomainDto> transitEntityList =
        transitPersistenceService.fetchTransitList(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals(transitDomainDto, transitEntityList.get(0));

    verify(transitRepository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any());
  }

  @Test
  void getListOfTransitDetailsTestException() {
    when(transitRepository.findByOrgIdAndDestinationGeozoneAndSourceGeoZones(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching transit list"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.fetchTransitList(
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
        transitPersistenceService.fetchTransitEntitiesCount(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(2, transitEntitiesCount);
    verify(transitRepository, times(1)).findTransitCountByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  void fetchTransitEntitiesCountTestException() {
    when(transitRepository.findTransitCountByOrgIdAndCarrierServiceId(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching transit entities count"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.fetchTransitEntitiesCount(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Error while fetching transit entities count", exception.getMessage());
    verify(transitRepository, times(1)).findTransitCountByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTest() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    TransitEntity transitEntity = testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS);
    when(transitRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(List.of(transitEntity));
    when(transitEntityMapper.toDomain(anyList())).thenReturn(List.of(transitDomainDto));

    List<TransitDomainDto> transitEntityList =
        transitPersistenceService.fetchTransitListForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(transitDomainDto, transitEntityList.get(0));

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
                transitPersistenceService.fetchTransitListForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Error while fetching transit list", exception.getMessage());

    verify(transitRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void fetchTransitListForDestinationGeoZones() throws TransitDomainException {
    when(transitRepository.findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE)))
        .thenReturn(List.of(testUtil.getProjectedTransitEntity()));

    List<ProjectedTransitEntity> transitEntities =
        transitPersistenceService.fetchTransitListForDestinationGeoZones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE));
    Assertions.assertFalse(CollectionUtils.isEmpty(transitEntities));
    verify(transitRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(any(), any(), any());
  }

  @Test
  void fetchTransitListForDestinationGeoZonesException() {
    when(transitRepository.findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE)))
        .thenThrow(new RuntimeException("Error while fetching transit entities"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.fetchTransitListForDestinationGeoZones(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    List.of(TestUtil.DESTINATION_GEOZONE)));
    Assertions.assertNotNull(exception);
    verify(transitRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(any(), any(), any());
  }

  @Test
  void fetchDistinctSourceGeoZones() throws TransitDomainException {
    when(transitRepository.findDistinctSourceGeoZones(any(), any()))
        .thenReturn(List.of(TestUtil.SOURCE_GEOZONE));

    List<String> sourceGeozones =
        transitPersistenceService.fetchDistinctSourceGeoZones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertFalse(CollectionUtils.isEmpty(sourceGeozones));
  }

  @Test
  void fetchDistinctSourceGeoZonesException() throws TransitDomainException {
    when(transitRepository.findDistinctSourceGeoZones(any(), any()))
        .thenThrow(new RuntimeException("Error"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.fetchDistinctSourceGeoZones(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertNotNull(exception);
  }

  @Test
  void fetchDistinctDestinationGeoZones() throws TransitDomainException {
    when(transitRepository.findDistinctDestinationGeoZones(any(), any()))
        .thenReturn(List.of(TestUtil.DESTINATION_GEOZONE));

    List<String> sourceGeozones =
        transitPersistenceService.fetchDistinctDestinationGeoZones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertFalse(CollectionUtils.isEmpty(sourceGeozones));
  }

  @Test
  void fetchDistinctDestinationGeoZonesException() throws TransitDomainException {
    when(transitRepository.findDistinctDestinationGeoZones(any(), any()))
        .thenThrow(new RuntimeException("Error"));

    Exception exception =
        assertThrows(
            TransitDomainException.class,
            () ->
                transitPersistenceService.fetchDistinctDestinationGeoZones(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertNotNull(exception);
  }
}
