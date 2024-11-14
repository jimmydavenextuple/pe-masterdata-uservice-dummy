/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.entity.TransitBufferEntity;
import com.nextuple.transit.persistence.entity.key.TransitKey;
import com.nextuple.transit.persistence.mapper.TransitBufferEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TransitBufferPersistenceServiceImplTest {
  @InjectMocks TransitBufferPersistenceServiceImpl transitBufferPersistenceService;
  @InjectMocks TestUtil testUtil;

  @Mock TransitBufferRepository transitBufferRepository;

  @Mock TransitBufferEntityMapper transitBufferEntityMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        transitBufferPersistenceService, "repository", transitBufferRepository);
    ReflectionTestUtils.setField(
        transitBufferPersistenceService, "mapper", transitBufferEntityMapper);
  }

  @Test
  @DisplayName("Save Transit Buffer: Happy Path")
  void saveTransitBufferHappyPath() throws CommonServiceException {
    when(transitBufferRepository.save(any())).thenReturn(testUtil.getTransitBufferEntity());
    when(transitBufferEntityMapper.toDomain(any(TransitBufferEntity.class)))
        .thenReturn(testUtil.getTransitBufferDomainDto());
    when(transitBufferEntityMapper.toEntity(any(TransitBufferDomainDto.class)))
        .thenReturn(testUtil.getTransitBufferEntity());
    assertEquals(
        testUtil.getTransitBufferDomainDto(),
        transitBufferPersistenceService.saveTransitBuffer(testUtil.getTransitBufferDomainDto()));
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Save Transit Buffer: Exception Path")
  void saveTransitBufferException() throws CommonServiceException {
    when(transitBufferRepository.save(any())).thenThrow(new RuntimeException("error"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () -> {
              transitBufferPersistenceService.saveTransitBuffer(
                  testUtil.getTransitBufferDomainDto());
            });
    assertEquals("ERROR_WHILE_CREATING", cse.getMessage());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("findByOrgIdAndDestinationGeozone: Happy Path")
  void findByOrgIdAndDestinationGeozoneHappyPath() throws CommonServiceException {
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(List.of(testUtil.getTransitBufferEntity()));
    when(transitBufferEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getTransitBufferDomainDto()));
    transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(
        TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  @DisplayName("findByOrgIdAndDestinationGeozone: Exception Path")
  void findByOrgIdAndDestinationGeozoneException() throws CommonServiceException {
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("error"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () -> {
              transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(
                  TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
            });
    assertEquals("ERROR_WHILE_FETCHING", cse.getMessage());
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  @DisplayName("findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone: Happy Path")
  void findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozoneHappyPath()
      throws CommonServiceException {
    when(transitBufferRepository.findById(any()))
        .thenReturn(Optional.of(testUtil.getTransitBufferEntity()));
    when(transitBufferEntityMapper.toEntityKey(any()))
        .thenReturn(
            TransitKey.builder()
                .orgId(TestUtil.ORG_ID)
                .carrierServiceId(TestUtil.CARRIER_SERVICE_ID)
                .sourceGeozone(TestUtil.SOURCE_GEOZONE)
                .destinationGeozone(TestUtil.DESTINATION_GEOZONE)
                .build());
    when(transitBufferEntityMapper.toDomain(any(TransitBufferEntity.class)))
        .thenReturn(testUtil.getTransitBufferDomainDto());
    Optional<TransitBufferDomainDto> responseDto =
        transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                TestUtil.ORG_ID,
                TestUtil.CARRIER_SERVICE_ID,
                TestUtil.SOURCE_GEOZONE,
                TestUtil.DESTINATION_GEOZONE);
    assertEquals(testUtil.getTransitBufferDomainDto(), responseDto.get());
    verify(transitBufferRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName(
      "findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone: Exception Path")
  void findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozoneException()
      throws CommonServiceException {
    when(transitBufferRepository.findById(any())).thenThrow(new RuntimeException("error"));
    when(transitBufferEntityMapper.toEntityKey(any()))
        .thenReturn(
            TransitKey.builder()
                .orgId(TestUtil.ORG_ID)
                .carrierServiceId(TestUtil.CARRIER_SERVICE_ID)
                .sourceGeozone(TestUtil.SOURCE_GEOZONE)
                .destinationGeozone(TestUtil.DESTINATION_GEOZONE)
                .build());
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferPersistenceService
                    .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                        TestUtil.ORG_ID,
                        TestUtil.CARRIER_SERVICE_ID,
                        TestUtil.SOURCE_GEOZONE,
                        TestUtil.DESTINATION_GEOZONE));
    assertEquals("ERROR_WHILE_FETCHING", cse.getMessage());
    verify(transitBufferRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("deleteTransitBuffer: Happy Path")
  void deleteTransitBufferHappyPath() throws CommonServiceException {
    doNothing().when(transitBufferRepository).delete(any());
    when(transitBufferEntityMapper.toEntity(any(TransitBufferDomainDto.class)))
        .thenReturn(testUtil.getTransitBufferEntity());
    TransitBufferDomainDto responseDto =
        transitBufferPersistenceService.deleteTransitBuffer(testUtil.getTransitBufferDomainDto());
    assertEquals(testUtil.getTransitBufferDomainDto(), responseDto);
    verify(transitBufferRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("deleteTransitBuffer: Exception Path")
  void deleteTransitBufferException() throws CommonServiceException {
    doThrow(new RuntimeException("error")).when(transitBufferRepository).delete(any());
    when(transitBufferEntityMapper.toEntity(any(TransitBufferDomainDto.class)))
        .thenReturn(testUtil.getTransitBufferEntity());
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferPersistenceService.deleteTransitBuffer(
                    testUtil.getTransitBufferDomainDto()));
    assertEquals("ERROR_WHILE_DELETING", cse.getMessage());
    verify(transitBufferRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("findByTransitBufferConfigRequestId: Happy Path")
  void findByTransitBufferConfigRequestIdHappyPath() throws CommonServiceException {
    when(transitBufferRepository.findByTransitBufferConfigRequestId(any()))
        .thenReturn(List.of(testUtil.getTransitBufferEntity()));
    when(transitBufferEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getTransitBufferDomainDto()));
    transitBufferPersistenceService.findByTransitBufferConfigRequestId(
        TestUtil.TRANS_BUFFER_CONFIG_REQUEST_ID);
    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(any());
  }

  @Test
  @DisplayName("findByTransitBufferConfigRequestId: Exception Path")
  void findByTransitBufferConfigRequestIdException() throws CommonServiceException {
    when(transitBufferRepository.findByTransitBufferConfigRequestId(any()))
        .thenThrow(new RuntimeException("error"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () -> {
              transitBufferPersistenceService.findByTransitBufferConfigRequestId(
                  TestUtil.TRANS_BUFFER_CONFIG_REQUEST_ID);
            });
    assertEquals("ERROR_WHILE_FETCHING", cse.getMessage());
    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(any());
  }
}
