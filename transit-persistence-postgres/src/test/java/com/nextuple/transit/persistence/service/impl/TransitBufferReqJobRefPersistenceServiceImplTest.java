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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.entity.TransitBufferReqJobRefEntity;
import com.nextuple.transit.persistence.mapper.TransitBufferReqJobRefEntityMapper;
import com.nextuple.transit.persistence.repository.TransitBufferReqJobRefRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class TransitBufferReqJobRefPersistenceServiceImplTest {
  @InjectMocks
  TransitBufferReqJobRefPersistenceServiceImpl transitBufferReqJobRefPersistenceService;

  @InjectMocks TestUtil testUtil;

  @Mock TransitBufferReqJobRefRepository transitBufferReqJobRefRepository;
  @Mock TransitBufferReqJobRefEntityMapper transitBufferReqJobRefEntityMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        transitBufferReqJobRefPersistenceService, "repository", transitBufferReqJobRefRepository);
    ReflectionTestUtils.setField(
        transitBufferReqJobRefPersistenceService, "mapper", transitBufferReqJobRefEntityMapper);
  }

  @Test
  @DisplayName("Save Transit Buffer Request Job Ref: Happy Path")
  void saveTransitBufferRequestJobRefHappyPath() throws CommonServiceException {
    when(transitBufferReqJobRefRepository.save(any()))
        .thenReturn(testUtil.getTransitBufferReqJobRefEntity());
    when(transitBufferReqJobRefEntityMapper.toDomain(any(TransitBufferReqJobRefEntity.class)))
        .thenReturn(testUtil.getTransitBufferReqJobRefDomainDto());
    when(transitBufferReqJobRefEntityMapper.toEntity(any(TransitBufferReqJobRefDomainDto.class)))
        .thenReturn(testUtil.getTransitBufferReqJobRefEntity());
    TransitBufferReqJobRefDomainDto receivedResponse =
        transitBufferReqJobRefPersistenceService.saveTransitBufferReqJobRefRepository(
            testUtil.getTransitBufferReqJobRefDomainDto());
    assertEquals(testUtil.getTransitBufferReqJobRefDomainDto(), receivedResponse);
    verify(transitBufferReqJobRefRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Save Transit Buffer Request Job Ref: Exception")
  void saveTransitBufferRequestJobRefException() throws CommonServiceException {
    when(transitBufferReqJobRefRepository.save(any())).thenThrow(new RuntimeException("error"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferReqJobRefPersistenceService.saveTransitBufferReqJobRefRepository(
                    testUtil.getTransitBufferReqJobRefDomainDto()));
    assertEquals("Error while creating transit buffer req job ref", cse.getMessage());
    verify(transitBufferReqJobRefRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("findByExtReferenceId: Happy Path")
  void findByExtReferenceIdHappyPath() throws CommonServiceException {
    when(transitBufferReqJobRefRepository.findByExtReferenceId(any()))
        .thenReturn(List.of(testUtil.getTransitBufferReqJobRefEntity()));
    when(transitBufferReqJobRefEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getTransitBufferReqJobRefDomainDto()));

    List<TransitBufferReqJobRefDomainDto> receivedResponse =
        transitBufferReqJobRefPersistenceService.findByExtReferenceId(
            TestUtil.TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID);
    assertEquals(List.of(testUtil.getTransitBufferReqJobRefDomainDto()), receivedResponse);
    verify(transitBufferReqJobRefRepository, times(1)).findByExtReferenceId(any());
  }

  @Test
  @DisplayName("findByExtReferenceId: Exception")
  void findByExtReferenceIdException() throws CommonServiceException {
    when(transitBufferReqJobRefRepository.findByExtReferenceId(any()))
        .thenThrow(new RuntimeException("error"));
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferReqJobRefPersistenceService.findByExtReferenceId(
                    TestUtil.TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID));
    assertEquals("Error while creating transit buffer req job ref", cse.getMessage());
    verify(transitBufferReqJobRefRepository, times(1)).findByExtReferenceId(any());
  }
}
