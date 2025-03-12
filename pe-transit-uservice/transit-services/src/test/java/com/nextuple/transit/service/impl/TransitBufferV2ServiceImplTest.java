/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.impl.TransitBufferConfigRequestPersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitBufferV2PersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitPersistenceServiceImpl;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class TransitBufferV2ServiceImplTest {
  private static final String INVALID_TRANSIT_BUFFER = "Invalid transit buffer";
  @InjectMocks TransitBufferV2ServiceImpl transitBufferV2Service;
  @InjectMocks TestUtil testUtil;
  @Mock TransitBufferV2PersistenceServiceImpl transitBufferV2PersistenceService;
  @Mock TransitPersistenceServiceImpl transitPersistenceService;
  @Mock DateValidationUtil dateValidationUtil;

  @Mock
  TransitBufferConfigRequestPersistenceServiceImpl transitBufferConfigRequestPersistenceService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Get Transit Buffers By OrgId And DestinationGeozone - HappyPath")
  void getTransitBuffersByOrgIdAndDestinationGeozoneHappyPath() throws CommonServiceException {
    List<TransitBufferV2DomainDto> entities = testUtil.getTransitBufferV2DomainDtos(5);
    entities.get(2).setSourceGeozone(TestUtil.SOURCE_GEOZONE + "2");
    entities.get(3).setSourceGeozone(TestUtil.SOURCE_GEOZONE + "2");
    entities.get(4).setCarrierServiceId(TestUtil.CARRIER_SERVICE_ID + "3");
    when(transitBufferV2PersistenceService.fetchTransitBuffersByOrgIdAndDestinationGeozone(
            any(), any(), any(), any()))
        .thenReturn(entities);
    List<TransitBufferDetailsResponse> responses =
        transitBufferV2Service.getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, LocalDate.now(), 2);
    assertEquals(3, responses.size());
    assertEquals(
        5, responses.stream().flatMap(r -> r.getTransitBuffers().stream()).toList().size());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBuffersByOrgIdAndDestinationGeozone(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception - Get Transit Buffers By OrgId And DestinationGeozone")
  void getTransitBuffersByOrgIdAndDestinationGeozoneNotFoundException()
      throws CommonServiceException {
    when(transitBufferV2PersistenceService.fetchTransitBuffersByOrgIdAndDestinationGeozone(
            any(), any(), any(), any()))
        .thenReturn(List.of());
    CommonServiceException e =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2Service
                    .getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
                        TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, LocalDate.now(), 2));
    assertEquals("Transit buffer details not found with given details", e.getMessage());
    assertEquals(4, e.getFieldInfo().size());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBuffersByOrgIdAndDestinationGeozone(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Update Transit Buffer by request - Happy Path")
  void updateTransitBufferHappyPathTest() throws CommonServiceException, TransitDomainException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(2L);
    Optional<TransitDomainDto> transitDomainDto =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDto);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestEntity.setId(2L);
    transitBufferConfigRequestEntity.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2Entities =
        testUtil.getTransitBufferV2DomainDtos(3);
    transitBufferV2Entities.get(2).setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(transitBufferV2Entities);
    when(transitBufferV2PersistenceService.updateTransitBuffer(any()))
        .thenReturn(transitBufferV2Entities.get(2));

    TransitBufferV2Response response =
        transitBufferV2Service.updateTransitBuffer(transitBufferRequest);

    assertEquals(transitBufferV2Entities.get(2).getId(), response.getId());
    assertEquals(2L, response.getTransitBufferConfigRequestId());
    assertEquals(transitBufferRequest.getBufferStartDate(), response.getBufferStartDate());
    assertEquals(transitBufferRequest.getBufferEndDate(), response.getBufferEndDate());
    assertEquals(transitBufferRequest.getCustomAttributes(), response.getCustomAttributes());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
    verify(transitBufferV2PersistenceService, times(1)).updateTransitBuffer(any());
  }

  @Test
  @DisplayName(
      "Update Transit Buffer Exception - sum of transit and buffer days is less or equal to 0")
  void updateTransitBufferHappyExceptionTest()
      throws CommonServiceException, TransitDomainException {
    Optional<TransitDomainDto> transitEntities =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntities);
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferDays(-10.0);

    TransitBufferConfigRequestDomainDto transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestEntity.setId(2L);
    transitBufferConfigRequestEntity.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2Entities =
        testUtil.getTransitBufferV2DomainDtos(3);
    transitBufferV2Entities.get(2).setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(transitBufferV2Entities);
    when(transitBufferV2PersistenceService.updateTransitBuffer(any()))
        .thenReturn(transitBufferV2Entities.get(2));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals("The sum of transit and buffer days is less or equal to 0", ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Null config request Id")
  void updateTransitBufferNullConfigRequestIdExceptionTest() throws TransitDomainException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(null);
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Empty config request Entity")
  void updateTransitBufferEmptyConfigRequestEntityExceptionTest()
      throws TransitDomainException, CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(1L);
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.empty());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Empty Parent config request Id")
  void updateTransitBufferEmptyParentRequestIdExceptionTest()
      throws TransitDomainException, CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(1L);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(null);
    Optional<TransitDomainDto> transitEntities =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntities);
    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos =
        testUtil.getTransitBufferV2DomainDtos(3);
    transitBufferV2DomainDtos.get(2).setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception: historic date")
  void updateTransitBufferHistoricDateExceptionTest()
      throws TransitDomainException, CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(1L);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos =
        testUtil.getTransitBufferV2DomainDtos(3);
    transitBufferV2DomainDtos.get(2).setTransitBufferConfigRequestId(1L);
    Optional<TransitDomainDto> transitDomainDto =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDto);
    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals("Buffer end date must be in future", ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception : Invalid config request Id")
  void updateTransitBufferInvalidConfigRequestIdTest()
      throws CommonServiceException, TransitDomainException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(2L);
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos =
        testUtil.getTransitBufferV2DomainDtos(3);
    ;

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(transitBufferV2DomainDtos);
    doNothing()
        .when(transitBufferV2PersistenceService)
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception : Overlapping buffer")
  void updateTransitBufferOverlappingBufferExceptionTest()
      throws CommonServiceException, TransitDomainException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(new Date());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(2L);
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos =
        testUtil.getTransitBufferV2DomainDtos(3);
    ;
    transitBufferV2DomainDtos.get(2).setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(transitBufferV2DomainDtos);

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.updateTransitBuffer(transitBufferRequest));
    assertEquals("Transit Buffer window already exists or overlaps", ex.getMessage());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  @DisplayName("Delete Transit Buffer - Happy Path")
  void deleteTransitBufferHappyPathTest() throws CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    transitBufferRequest.setTransitBufferConfigRequestId(2L);

    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(1L);

    Optional<TransitBufferV2DomainDto> transitBufferV2Entities =
        Optional.of(testUtil.getTransitBufferV2DomainDto(4));
    transitBufferV2Entities.get().setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                any(), any(), any(), any(), any()))
        .thenReturn(transitBufferV2Entities);
    doNothing()
        .when(transitBufferV2PersistenceService)
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());

    TransitBufferV2Response response =
        transitBufferV2Service.deleteTransitBuffer(transitBufferRequest);
    assertEquals(transitBufferV2Entities.get().getId(), response.getId());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
            any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Null config request Id")
  void deleteTransitBufferNullConfigRequestIdExceptionTest() throws CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(null);
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.deleteTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Exception: Empty config request Entity")
  void deleteTransitBufferEmptyConfigRequestEntityExceptionTest() throws CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(1L);
    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.empty());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.deleteTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Exception: Empty Parent config request Id")
  void deleteTransitBufferEmptyParentRequestIdExceptionTest() throws CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(1L);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(null);

    List<TransitBufferV2DomainDto> transitBufferV2Entities =
        testUtil.getTransitBufferV2DomainDtos(3);
    transitBufferV2Entities.get(2).setTransitBufferConfigRequestId(1L);

    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.deleteTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Exception : Invalid config request Id")
  void deleteTransitBufferInvalidConfigRequestIdTest() throws CommonServiceException {
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequest();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 1);
    transitBufferRequest.setBufferStartDate(calendar.getTime());
    transitBufferRequest.setBufferEndDate(calendar.getTime());
    transitBufferRequest.setTransitBufferConfigRequestId(2L);

    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    transitBufferConfigRequestDomainDto.setId(2L);
    transitBufferConfigRequestDomainDto.setParentRequestId(1L);

    List<TransitBufferV2DomainDto> transitBufferV2Entities =
        testUtil.getTransitBufferV2DomainDtos(3);
    ;
    when(transitBufferConfigRequestPersistenceService.findById(any(Long.class)))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(transitBufferV2Entities);
    doNothing()
        .when(transitBufferV2PersistenceService)
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.deleteTransitBuffer(transitBufferRequest));
    assertEquals(INVALID_TRANSIT_BUFFER, ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Delete Transit Buffer By Id - HappyPath")
  void deleteTransitBufferByIdHappyPath() throws CommonServiceException {
    TransitBufferV2DomainDto entityToDelete = new TransitBufferV2DomainDto();
    entityToDelete.setId(123L);

    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(entityToDelete));

    doNothing()
        .when(transitBufferV2PersistenceService)
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());

    TransitBufferV2Response response =
        transitBufferV2Service.deleteTransitBufferById(TestUtil.ORG_ID, 123L);

    assertNotNull(response);
    assertEquals(entityToDelete.getId(), response.getId());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L);
    verify(transitBufferV2PersistenceService, times(1))
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("Exception - Delete Transit Buffer")
  void deleteTransitBufferById_EntityNotFound() throws CommonServiceException {
    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.deleteTransitBufferById(TestUtil.ORG_ID, 123L));

    assertEquals("Transit Buffer Not found for requested orgId and id", ex.getMessage());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(any(), any());
    verify(transitBufferV2PersistenceService, times(0))
        .deleteTransitBufferEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("Get Transit Buffer By OrgId And Id - HappyPath")
  void getTransitBufferByOrgIdAndIdHappyPathTest() throws CommonServiceException {
    TransitBufferV2DomainDto transitBufferEntity = new TransitBufferV2DomainDto();
    transitBufferEntity.setId(123L);

    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(transitBufferEntity));

    TransitBufferV2Response response =
        transitBufferV2Service.getTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L);

    assertNotNull(response);
    assertEquals(transitBufferEntity.getId(), response.getId());

    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L);
  }

  @Test
  @DisplayName("Exception - Get Transit Buffer By OrgId And Id")
  void getTransitBufferByOrgIdAndId_EntityNotFound() throws CommonServiceException {
    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferV2Service.getTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L));

    assertEquals("Transit Buffer Not found for requested orgId and id", ex.getMessage());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName("Update Transit Buffer By OrgId And Id - HappyPath")
  void updateTransitBufferByOrgIdAndIdHappyPath()
      throws CommonServiceException, TransitDomainException {
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    DateValidationUtil dateValidationUtil = mock(DateValidationUtil.class);

    TransitBufferV2UpdationRequest request = new TransitBufferV2UpdationRequest();
    request.setBufferDays(5.0);
    TransitBufferV2DomainDto existingEntity = new TransitBufferV2DomainDto();
    existingEntity.setId(123L);
    existingEntity.setOrgId(TestUtil.ORG_ID);

    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(existingEntity));
    when(transitBufferV2PersistenceService.updateTransitBuffer(existingEntity))
        .thenReturn(existingEntity);
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());

    TransitBufferV2Response response =
        transitBufferV2Service.updateTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L, request);

    assertNotNull(response);
    assertEquals(123L, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(5, response.getBufferDays());

    verify(transitBufferV2PersistenceService).fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L);
    verify(transitBufferV2PersistenceService).updateTransitBuffer(existingEntity);
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Update Transit Buffer Exception - sum of transit and buffer days is less or equal to 0")
  void updateTransitBufferByOrgIdAndIdSumException()
      throws CommonServiceException, TransitDomainException {
    DateValidationUtil dateValidationUtil = mock(DateValidationUtil.class);
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    TransitBufferV2UpdationRequest request = new TransitBufferV2UpdationRequest();
    request.setBufferDays(-10.0);
    TransitBufferV2DomainDto existingEntity = new TransitBufferV2DomainDto();
    existingEntity.setId(123L);
    existingEntity.setOrgId(TestUtil.ORG_ID);

    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(TestUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(existingEntity));
    when(transitBufferV2PersistenceService.updateTransitBuffer(existingEntity))
        .thenReturn(existingEntity);
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2Service.updateTransitBufferByOrgIdAndId(
                    TestUtil.ORG_ID, 123L, request));
    assertEquals("The sum of transit and buffer days is less or equal to 0", ex.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(any(), any());
    verify(transitPersistenceService).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception - Update Transit Buffer By OrgId And Id")
  void updateTransitBufferByOrgIdAndIdNotFoundException() throws CommonServiceException {
    TransitBufferV2UpdationRequest request = new TransitBufferV2UpdationRequest();

    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(testUtil.ORG_ID, 123L))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2Service.updateTransitBufferByOrgIdAndId(
                    testUtil.ORG_ID, 123L, request));

    assertEquals("Transit Buffer Not found for requested orgId and id", exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1776, exception.getErrorCode());

    verify(dateValidationUtil, never()).validateBufferStartAndEndDate(any(), any());

    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(testUtil.ORG_ID, 123L);
  }

  @Test
  @DisplayName("Exception - Update Transit Buffer By OrgId And Id overlap")
  void updateTransitBufferByOrgIdAndIdOverlapException()
      throws CommonServiceException, TransitDomainException {
    TransitBufferV2UpdationRequest request = testUtil.getTransitBufferUpdateRequest();

    TransitBufferV2DomainDto exisitingTransitBufferV2DomainDto = new TransitBufferV2DomainDto();
    exisitingTransitBufferV2DomainDto.setId(123L);
    exisitingTransitBufferV2DomainDto.setOrgId(testUtil.ORG_ID);
    when(transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(testUtil.ORG_ID, 123L))
        .thenReturn(Optional.of(exisitingTransitBufferV2DomainDto));
    Optional<TransitDomainDto> transitDomainDtos =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);
    TransitBufferV2DomainDto overlappingDomainDto =
        testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID);
    when(transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                any(), any(), any(), any()))
        .thenReturn(List.of(overlappingDomainDto));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferV2Service.updateTransitBufferByOrgIdAndId(
                    testUtil.ORG_ID, 123L, request));

    assertEquals("Transit Buffer window already exists or overlaps", exception.getMessage());
    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals(0x1774, exception.getErrorCode());

    verify(dateValidationUtil, times(1)).validateBufferStartAndEndDate(any(), any());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndId(testUtil.ORG_ID, 123L);
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            exisitingTransitBufferV2DomainDto.getOrgId(),
            exisitingTransitBufferV2DomainDto.getDestinationGeozone(),
            exisitingTransitBufferV2DomainDto.getSourceGeozone(),
            exisitingTransitBufferV2DomainDto.getCarrierServiceId());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Save Transit Buffer HappyPath")
  void saveTransitBufferHappyPath() throws CommonServiceException, TransitDomainException {
    TransitBufferV2DomainDto transitBufferEntity =
        testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID);
    TransitBufferRequest transitBufferRequest = testUtil.getTransitBufferRequestV2();

    Optional<TransitDomainDto> transitEntities =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntities);
    when(transitBufferV2PersistenceService.saveTransitBuffer(any()))
        .thenReturn(transitBufferEntity);
    TransitBufferV2Response response =
        transitBufferV2Service.saveTransitBuffer(transitBufferRequest);
    verify(dateValidationUtil, times(1))
        .validateBufferStartAndEndDate(
            transitBufferRequest.getBufferStartDate(), transitBufferRequest.getBufferEndDate());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getDestinationGeozone(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getCarrierServiceId());
    verify(transitBufferV2PersistenceService, times(1)).saveTransitBuffer(any());
    verify(transitBufferV2PersistenceService, times(1))
        .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
    verify(transitBufferV2PersistenceService, times(1)).saveTransitBuffer(any());
    assertNotNull(response);
    assertEquals(TestUtil.CARRIER_SERVICE_ID, response.getCarrierServiceId());
    verify(transitPersistenceService).findTransitDetails(any(), any(), any(), any());
  }
}
