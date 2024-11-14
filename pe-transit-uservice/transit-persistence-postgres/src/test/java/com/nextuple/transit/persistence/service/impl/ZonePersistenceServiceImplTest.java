/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.entity.ZoneEntity;
import com.nextuple.transit.persistence.mapper.ZoneEntityMapper;
import com.nextuple.transit.persistence.repository.ZoneRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.util.ReflectionTestUtils;

class ZonePersistenceServiceImplTest {

  @InjectMocks ZonePersistenceServiceImpl zonePersistenceService;

  @InjectMocks TestUtil testUtil;
  @Mock ZoneRepository zoneRepository;

  @Mock ZoneEntityMapper zoneEntityMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(zonePersistenceService, "repository", zoneRepository);
    ReflectionTestUtils.setField(zonePersistenceService, "mapper", zoneEntityMapper);
  }

  @Test
  void saveZoneTest() throws PromiseEngineException {
    ZoneEntity zoneEntity = new ZoneEntity();
    ZoneDomainDto zoneDomainDto = new ZoneDomainDto();
    when(zoneRepository.save(any())).thenReturn(zoneEntity);
    when(zoneEntityMapper.toEntity(any(ZoneDomainDto.class))).thenReturn(zoneEntity);
    when(zoneEntityMapper.toDomain(any(ZoneEntity.class))).thenReturn(zoneDomainDto);
    ZoneDomainDto entity = zonePersistenceService.saveZone(zoneDomainDto);
    Assertions.assertEquals(zoneDomainDto, entity);

    verify(zoneRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Save Zone Test - Exception")
  void saveZoneExceptionTest() {
    ZoneDomainDto zoneDomainDto = new ZoneDomainDto();
    when(zoneRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));
    when(zoneEntityMapper.toEntity(any(ZoneDomainDto.class))).thenReturn(new ZoneEntity());
    Exception exception =
        assertThrows(
            PromiseEngineException.class, () -> zonePersistenceService.saveZone(zoneDomainDto));
    assertEquals("Unable to save zone", exception.getMessage());
    verify(zoneRepository, VerificationModeFactory.times(1)).save(any());
  }

  @Test
  void fetchZoneDetailsTest() throws PromiseEngineException {
    ZoneDomainDto zoneDomainDto = testUtil.getZoneDomainDto();
    ZoneEntity zoneEntity = new ZoneEntity();
    when(zoneRepository.findById(any())).thenReturn(Optional.of(zoneEntity));
    when(zoneEntityMapper.toDomain(any(ZoneEntity.class))).thenReturn(zoneDomainDto);

    Optional<ZoneDomainDto> foundZoneDetails =
        zonePersistenceService.fetchZoneDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertTrue(foundZoneDetails.isPresent());
    assertEquals(zoneDomainDto, foundZoneDetails.get());
    verify(zoneRepository, VerificationModeFactory.times(1)).findById(any());
  }

  @Test
  void fetchZoneDetailsExceptionTest() {
    when(zonePersistenceService.findByKey(any()))
        .thenThrow(new RuntimeException("Unable to find zone"));
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              zonePersistenceService.fetchZoneDetails(
                  TestUtil.ORG_ID,
                  TestUtil.SOURCE_GEOZONE,
                  TestUtil.DESTINATION_GEOZONE,
                  TestUtil.CARRIER_SERVICE_ID);
            });
    assertEquals("Unable to find zone", exception.getMessage());
  }

  @Test
  void deleteZoneDetailsTest() throws PromiseEngineException {
    ZoneDomainDto zoneDomainDto = testUtil.getZoneDomainDto();
    doNothing().when(zoneRepository).delete(any());
    zonePersistenceService.deleteZoneDetails(zoneDomainDto);

    verify(zoneRepository, times(1)).delete(any());
  }

  @Test
  void deleteZoneDetailsExceptionTest() {
    doThrow(new RuntimeException("Unable to delete zone")).when(zoneRepository).delete(any());
    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () -> zonePersistenceService.deleteZoneDetails(testUtil.getZoneDomainDto()));
    Assertions.assertEquals("Unable to delete zone", exception.getMessage());
    verify(zoneRepository, times(1)).delete(any());
  }

  @Test
  void fetchZoneByOrgIdAndDestGeozoneTest() throws PromiseEngineException {
    List<ZoneEntity> zoneEntities = List.of(testUtil.getZoneEntity());
    List<ZoneDomainDto> zoneDomainDto = List.of(testUtil.getZoneDomainDto());
    when(zoneRepository.findByOrgIdAndDestinationGeozone(any(), any())).thenReturn(zoneEntities);
    when(zoneEntityMapper.toDomain(anyList())).thenReturn(zoneDomainDto);

    List<ZoneDomainDto> response =
        zonePersistenceService.fetchZoneByOrgIdAndDestGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(zoneDomainDto.get(0), response.get(0));

    verify(zoneRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void fetchZoneByOrgIdAndDestGeozoneExceptionTest() {
    when(zoneRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch zones list"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                zonePersistenceService.fetchZoneByOrgIdAndDestGeozone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Unable to fetch zones list", exception.getMessage());
    verify(zoneRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }
}
