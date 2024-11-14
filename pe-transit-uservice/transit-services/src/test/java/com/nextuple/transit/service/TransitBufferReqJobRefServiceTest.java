/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.persistence.service.impl.TransitBufferReqJobRefPersistenceServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferReqJobRefServiceTest {

  @InjectMocks private TransitBufferReqJobRefService transitBufferReqJobRefService;
  @InjectMocks private TestUtil testUtil;

  @Mock
  private TransitBufferReqJobRefPersistenceServiceImpl transitBufferReqJobRefPersistenceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitBufferReqJobRefTest()
      throws TransitBufferReqJobRefDomainException, CommonServiceException {
    TransitBufferReqJobRefRequest transitBufferReqJobRefRequest =
        testUtil.getTransBufferReqJobRefRequest();
    when(transitBufferReqJobRefPersistenceService.saveTransitBufferReqJobRefRepository(
            any(TransitBufferReqJobRefDomainDto.class)))
        .thenReturn(testUtil.getTransitBufferReqJobRefDomainDto());
    TransitBufferReqJobRefResponse responseEntity =
        transitBufferReqJobRefService.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferReqJobRefResponse().getExtReferenceId(),
        responseEntity.getExtReferenceId());
    verify(transitBufferReqJobRefPersistenceService, times(1))
        .saveTransitBufferReqJobRefRepository(any());
  }

  @Test
  void getTransitBufferReqJobRefByExtRefId()
      throws TransitBufferReqJobRefDomainException, CommonServiceException {
    TransitBufferReqJobRefDomainDto transitBufferReqJobRef =
        testUtil.getTransitBufferReqJobRefDomainDto();
    List<TransitBufferReqJobRefDomainDto> domainDtoList =
        new ArrayList<TransitBufferReqJobRefDomainDto>();
    domainDtoList.add(transitBufferReqJobRef);

    when(transitBufferReqJobRefPersistenceService.findByExtReferenceId(any()))
        .thenReturn(domainDtoList);

    List<TransitBufferReqJobRefResponse> responseEntity =
        transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId("1");

    Assertions.assertEquals(
        testUtil.getTransitBufferReqJobRefResponse().getExtReferenceId(),
        responseEntity.stream().findFirst().get().getExtReferenceId());
    verify(transitBufferReqJobRefPersistenceService, times(1)).findByExtReferenceId(any());
  }

  @Test
  void getTransitBufferReqJobRefByExtRefIdNotExists()
      throws TransitBufferReqJobRefDomainException, CommonServiceException {

    List<TransitBufferReqJobRefDomainDto> domainDtoList = new ArrayList<>();
    domainDtoList.clear();

    when(transitBufferReqJobRefPersistenceService.findByExtReferenceId(any()))
        .thenReturn(domainDtoList);

    TransitBufferReqJobRefDomainException exception =
        assertThrows(
            TransitBufferReqJobRefDomainException.class,
            () -> transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId("1"));

    Assertions.assertEquals(
        "Unable to find transit buffer job references with this ID: 1", exception.getMessage());
    Assertions.assertNull(exception.getId());
    Assertions.assertEquals("1", exception.getExtReferenceId());

    verify(transitBufferReqJobRefPersistenceService, times(1)).findByExtReferenceId(any());
  }
}
