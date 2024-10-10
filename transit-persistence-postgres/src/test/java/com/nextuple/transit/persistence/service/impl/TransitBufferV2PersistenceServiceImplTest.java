/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.entity.TransitBufferV2Entity;
import com.nextuple.transit.persistence.mapper.TransitBufferV2EntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferV2Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class TransitBufferV2PersistenceServiceImplTest {
  @InjectMocks TransitBufferV2PersistenceServiceImpl transitBufferV2PersistenceService;
  @InjectMocks TestUtil testUtil;
  @Mock TransitBufferV2Repository transitBufferV2Repository;

  @Mock TransitBufferV2EntityMapper transitBufferV2EntityMapper;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        transitBufferV2PersistenceService, "repository", transitBufferV2Repository);
    ReflectionTestUtils.setField(
        transitBufferV2PersistenceService, "mapper", transitBufferV2EntityMapper);
  }

  @Test
  void fetchTransitBuffersByOrgIdAndDestinationGeozoneHappyPath() throws CommonServiceException {
    when(transitBufferV2Repository.findApplicableBuffers(any(), any(), any(), any()))
        .thenReturn(testUtil.getTransitBufferV2Entities(2));
    when(transitBufferV2EntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getTransitBufferV2DomainDtos(2));
    List<TransitBufferV2DomainDto> response =
        transitBufferV2PersistenceService.fetchTransitBuffersByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID,
            TestUtil.DESTINATION_GEOZONE,
            LocalDate.now(),
            LocalDate.now().plusDays(2));
    assertEquals(2, response.size());
    verify(transitBufferV2Repository, times(1)).findApplicableBuffers(any(), any(), any(), any());
  }

  @Test
  void fetchTransitBuffersByOrgIdAndDestinationGeozoneException() {
    when(transitBufferV2Repository.findApplicableBuffers(any(), any(), any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2PersistenceService.fetchTransitBuffersByOrgIdAndDestinationGeozone(
                    TestUtil.ORG_ID,
                    TestUtil.DESTINATION_GEOZONE,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2)));
    assertEquals("Error while fetching transit buffers", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1)).findApplicableBuffers(any(), any(), any(), any());
  }

  @Test
  void fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdHappyPath()
      throws CommonServiceException {
    when(transitBufferV2Repository
            .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(testUtil.getTransitBufferV2Entities(3));
    when(transitBufferV2EntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getTransitBufferV2DomainDtos(3));
    List<TransitBufferV2DomainDto> response =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                TestUtil.ORG_ID,
                TestUtil.DESTINATION_GEOZONE,
                TestUtil.SOURCE_GEOZONE,
                TestUtil.CARRIER_SERVICE_ID);
    assertEquals(3, response.size());
    verify(transitBufferV2Repository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdException() {
    when(transitBufferV2Repository
            .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenThrow(new RuntimeException());

    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2PersistenceService
                    .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                        TestUtil.ORG_ID,
                        TestUtil.DESTINATION_GEOZONE,
                        TestUtil.SOURCE_GEOZONE,
                        TestUtil.CARRIER_SERVICE_ID));
    assertEquals("Error while fetching transit buffers", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void saveTransitBufferHappyPath() throws CommonServiceException {
    TransitBufferV2DomainDto transitBufferV2DomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository.save(any())).thenReturn(testUtil.getTransitBufferV2Entity(2));
    when(transitBufferV2EntityMapper.toDomain(any(TransitBufferV2Entity.class)))
        .thenReturn(transitBufferV2DomainDto);
    TransitBufferV2DomainDto response =
        transitBufferV2PersistenceService.saveTransitBuffer(transitBufferV2DomainDto);
    assertNotNull(response);
    verify(transitBufferV2Repository, times(1)).save(any());
  }

  @Test
  void saveTransitBufferException() {
    TransitBufferV2DomainDto transitBufferV2DomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository.save(any())).thenThrow(new RuntimeException());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2PersistenceService.saveTransitBuffer(transitBufferV2DomainDto));

    assertEquals("Error while creating transit buffer", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1)).save(any());
  }

  @Test
  void deleteTransitBufferEntityHappyPath() throws CommonServiceException {
    TransitBufferV2DomainDto testTransitBuffer = new TransitBufferV2DomainDto();
    assertDoesNotThrow(
        () ->
            transitBufferV2PersistenceService.deleteTransitBufferEntityByIdAndOrgId(
                testTransitBuffer.getId(), testTransitBuffer.getOrgId()));
    verify(transitBufferV2Repository, times(1))
        .deleteByIdAndOrgId(testTransitBuffer.getId(), testTransitBuffer.getOrgId());
  }

  @Test
  void deleteTransitBufferEntityException() {
    TransitBufferV2Entity testTransitBuffer = new TransitBufferV2Entity();
    doThrow(new RuntimeException())
        .when(transitBufferV2Repository)
        .deleteByIdAndOrgId(any(), any());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2PersistenceService.deleteTransitBufferEntityByIdAndOrgId(
                    testTransitBuffer.getId(), testTransitBuffer.getOrgId()));

    assertEquals("Error while deleting transit buffer", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1))
        .deleteByIdAndOrgId(testTransitBuffer.getId(), testTransitBuffer.getOrgId());
  }

  @Test
  void fetchTransitBufferByOrgIdAndIdHappyPath() throws CommonServiceException {
    TransitBufferV2Entity testTransitBuffer = new TransitBufferV2Entity();
    TransitBufferV2DomainDto testTransitBufferDomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository.findByOrgIdAndId(testUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(testTransitBuffer));
    when(transitBufferV2EntityMapper.toDomain(any(TransitBufferV2Entity.class)))
        .thenReturn(testTransitBufferDomainDto);
    Optional<TransitBufferV2DomainDto> response =
        transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(testUtil.ORG_ID, 123L);
    assertTrue(response.isPresent());
    assertEquals(testTransitBufferDomainDto, response.get());
    verify(transitBufferV2Repository, times(1)).findByOrgIdAndId(testUtil.ORG_ID, 123L);
  }

  @Test
  void fetchTransitBufferByOrgIdAndIdException() {
    when(transitBufferV2Repository.findByOrgIdAndId(any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(
                    testUtil.ORG_ID, 123L));
    assertEquals("Error while fetching transit buffers", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1)).findByOrgIdAndId(any(), any());
  }

  @Test
  void updateTransitBufferHappyPath() throws CommonServiceException {
    TransitBufferV2DomainDto transitBufferV2DomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository.save(any())).thenReturn(testUtil.getTransitBufferV2Entity(2));
    when(transitBufferV2EntityMapper.toDomain(any(TransitBufferV2Entity.class)))
        .thenReturn(transitBufferV2DomainDto);
    TransitBufferV2DomainDto response =
        transitBufferV2PersistenceService.updateTransitBuffer(transitBufferV2DomainDto);
    assertNotNull(response);
    verify(transitBufferV2Repository, times(1)).save(any());
  }

  @Test
  void updateTransitBufferException() {
    TransitBufferV2DomainDto transitBufferV2DomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository.save(any())).thenThrow(new RuntimeException());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2PersistenceService.updateTransitBuffer(transitBufferV2DomainDto));
    assertEquals("Error while updating transit buffers", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1)).save(any());
  }

  @Test
  void
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestIdHappyPath()
          throws CommonServiceException {
    TransitBufferV2Entity testTransitBufferEntity = new TransitBufferV2Entity();
    TransitBufferV2DomainDto expectedTransitBufferDomainDto = new TransitBufferV2DomainDto();
    when(transitBufferV2Repository
            .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testTransitBufferEntity));
    when(transitBufferV2EntityMapper.toDomain(any(TransitBufferV2Entity.class)))
        .thenReturn(expectedTransitBufferDomainDto);
    Optional<TransitBufferV2DomainDto> response =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                testTransitBufferEntity.getOrgId(),
                testTransitBufferEntity.getDestinationGeozone(),
                testTransitBufferEntity.getSourceGeozone(),
                testTransitBufferEntity.getCarrierServiceId(),
                testTransitBufferEntity.getTransitBufferConfigRequestId());

    assertTrue(response.isPresent());
    assertEquals(expectedTransitBufferDomainDto, response.get());
    verify(transitBufferV2Repository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
            any(), any(), any(), any(), any());
  }

  @Test
  void
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestIdException()
          throws CommonServiceException {
    when(transitBufferV2Repository
            .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException());
    TransitBufferV2Entity testTransitBuffer = new TransitBufferV2Entity();
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2PersistenceService
                    .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                        testTransitBuffer.getOrgId(),
                        testTransitBuffer.getDestinationGeozone(),
                        testTransitBuffer.getSourceGeozone(),
                        testTransitBuffer.getCarrierServiceId(),
                        testTransitBuffer.getTransitBufferConfigRequestId()));

    assertEquals("Error while fetching transit buffers", e.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    verify(transitBufferV2Repository, times(1))
        .findByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
            any(), any(), any(), any(), any());
  }

  @Test
  void transitBufferV2DeletionTest() throws CommonServiceException {
    TransitBufferV2DomainDto transitDomainDto = new TransitBufferV2DomainDto();
    doNothing().when(transitBufferV2Repository).delete(any());
    transitBufferV2PersistenceService.deleteTransitBuffer(transitDomainDto);

    verify(transitBufferV2Repository, times(1)).delete(any());
  }

  @Test
  void transitBufferV2DeletionTestException() {
    TransitBufferV2DomainDto transitDomainDto = new TransitBufferV2DomainDto();
    doThrow(new RuntimeException("Error while deleting transit buffer"))
        .when(transitBufferV2Repository)
        .delete(any());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2PersistenceService.deleteTransitBuffer(transitDomainDto));
    Assertions.assertEquals("Error while deleting transit buffer", exception.getMessage());
    verify(transitBufferV2Repository, times(1)).delete(any());
  }
}
