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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.entity.TransitBufferConfigRequestEntity;
import com.nextuple.transit.persistence.entity.key.TransitBufferConfigRequestKey;
import com.nextuple.transit.persistence.mapper.TransitBufferConfigRequestEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferConfigRequestRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TransitBufferConfigRequestPersistenceServiceImplTest {
  @InjectMocks TransitBufferConfigRequestPersistenceServiceImpl transitBufferConfigRequestService;

  @InjectMocks TestUtil testUtil;
  @Mock TransitBufferConfigRequestRepository transitBufferConfigRequestRepository;
  @Mock TransitBufferConfigRequestEntityMapper transitBufferConfigRequestEntityMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        transitBufferConfigRequestService, "repository", transitBufferConfigRequestRepository);
    ReflectionTestUtils.setField(
        transitBufferConfigRequestService, "mapper", transitBufferConfigRequestEntityMapper);
  }

  @Test
  @DisplayName("Save Transit Buffer Config Request: Happy Path")
  void saveTransitBufferConfigRequestHappyPath() throws CommonServiceException {
    when(transitBufferConfigRequestRepository.save(any()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    when(transitBufferConfigRequestEntityMapper.toDomain(
            any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    when(transitBufferConfigRequestEntityMapper.toEntity(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigRequestDomainDto responseDto =
        transitBufferConfigRequestService.saveTransitBufferConfigRequest(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    assertEquals(
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED),
        responseDto);
    verify(transitBufferConfigRequestRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Save Transit Buffer Config Request: Exception")
  void saveTransitBufferConfigRequestException() throws CommonServiceException {
    when(transitBufferConfigRequestRepository.save(any()))
        .thenThrow(new RuntimeException("ERROR_WHILE_SAVING"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.saveTransitBufferConfigRequest(
                    testUtil.getTransitBufferConfigRequestDomainDto(
                        TransitBufferConfigRequestStatusEnum.CREATED)));
    assertEquals("ERROR_WHILE_SAVING", cse.getMessage());
    verify(transitBufferConfigRequestRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("findById Transit Buffer Config Request: Happy Path")
  void findByIdHappyPath() throws CommonServiceException {
    when(transitBufferConfigRequestRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    when(transitBufferConfigRequestEntityMapper.toDomain(
            any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    Optional<TransitBufferConfigRequestDomainDto> responseDto =
        transitBufferConfigRequestService.findById(TestUtil.ID);
    assertEquals(
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED),
        responseDto.get());
    verify(transitBufferConfigRequestRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("findById Transit Buffer Config Request: Exception Path")
  void findByIdExceptionPath() throws CommonServiceException {
    when(transitBufferConfigRequestEntityMapper.toEntityKey(any()))
        .thenReturn(TransitBufferConfigRequestKey.builder().id(TestUtil.ID).build());
    when(transitBufferConfigRequestRepository.findById(anyLong()))
        .thenThrow(new RuntimeException("ERROR_WHILE_FETCHING"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferConfigRequestService.findById(TestUtil.ID));
    assertEquals("ERROR_WHILE_FETCHING", cse.getMessage());
    verify(transitBufferConfigRequestRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("findByOrgIdAndCarrierServiceId Transit Buffer Config Request: Happy Path")
  void findByOrgIdAndCarrierServiceIddHappyPath() throws CommonServiceException {
    when(transitBufferConfigRequestRepository.findByOrgIdAndCarrierServiceId(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    when(transitBufferConfigRequestEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    List<TransitBufferConfigRequestDomainDto> responseDto =
        transitBufferConfigRequestService.findByOrgIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of("CREATED"));
    assertEquals(
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED),
        responseDto.getFirst());
    verify(transitBufferConfigRequestRepository, times(1))
        .findByOrgIdAndCarrierServiceId(any(), any(), any());
  }

  @Test
  @DisplayName("findByOrgIdAndCarrierServiceId Transit Buffer Config Request: Exception Path")
  void findByOrgIdAndCarrierServiceIdExceptionPath() throws CommonServiceException {
    when(transitBufferConfigRequestRepository.findByOrgIdAndCarrierServiceId(any(), any(), any()))
        .thenThrow(new RuntimeException("ERROR_WHILE_FETCHING"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.findByOrgIdAndCarrierServiceId(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of("CREATED")));
    assertEquals("ERROR_WHILE_FETCHING", cse.getMessage());
    verify(transitBufferConfigRequestRepository, times(1))
        .findByOrgIdAndCarrierServiceId(any(), any(), any());
  }
}
