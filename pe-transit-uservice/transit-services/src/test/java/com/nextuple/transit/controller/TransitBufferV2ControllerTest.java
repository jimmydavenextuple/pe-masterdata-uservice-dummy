/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.impl.TransitBufferV2ServiceImpl;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TransitBufferV2ControllerTest {
  @InjectMocks private TransitBufferV2Controller transitBufferV2Controller;

  @Mock private TransitBufferV2ServiceImpl transitBufferV2Service;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.saveTransitBuffer(any())).thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.createTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).saveTransitBuffer(any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.updateTransitBuffer(any())).thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.updateTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).updateTransitBuffer(any());
  }

  @Test
  void deleteTransitBufferTest() throws CommonServiceException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.deleteTransitBuffer(any())).thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.deleteTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).deleteTransitBuffer(any());
  }

  @Test
  void getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDaysTest()
      throws CommonServiceException {
    List<TransitBufferDetailsResponse> transitBufferDetailsResponses =
        List.of(testUtil.getTransitBufferDetailsResponse());

    when(transitBufferV2Service.getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
            any(), any(), any(), any()))
        .thenReturn(transitBufferDetailsResponses);

    ResponseEntity<BaseResponse<List<TransitBufferDetailsResponse>>> response =
        transitBufferV2Controller
            .getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
                TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, LocalDate.now(), 7);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        transitBufferDetailsResponses.size(), response.getBody().getPayload().size());
    verify(transitBufferV2Service, times(1))
        .getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
            any(), any(), any(), any());
  }

  @Test
  void getTransitBufferTest() throws CommonServiceException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.getTransitBufferByOrgIdAndId(any(), any()))
        .thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.getByOrgIdAndId(TestUtil.ORG_ID, TestUtil.ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).getTransitBufferByOrgIdAndId(any(), any());
  }

  @Test
  void updateTransitBufferByIdTest() throws CommonServiceException, TransitDomainException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.updateTransitBufferByOrgIdAndId(any(), any(), any()))
        .thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.updateByOrgIdAndId(
            TestUtil.ORG_ID, TestUtil.ID, new TransitBufferV2UpdationRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).updateTransitBufferByOrgIdAndId(any(), any(), any());
  }

  @Test
  void deleteTransitBufferByIdTest() throws CommonServiceException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.deleteTransitBufferById(any(), any()))
        .thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.deleteTransitBufferById(TestUtil.ORG_ID, TestUtil.ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).deleteTransitBufferById(any(), any());
  }

  @Test
  void deleteTransitBufferFeedTest() throws CommonServiceException {
    TransitBufferV2Response transitBufferV2Response = testUtil.getTransitBufferV2Response(1L);

    when(transitBufferV2Service.deleteTransitBufferRecord(any()))
        .thenReturn(transitBufferV2Response);

    ResponseEntity<BaseResponse<TransitBufferV2Response>> response =
        transitBufferV2Controller.deleteTransitBufferRecord(
            testUtil.getTransitBufferDeletionRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferV2Response, response.getBody().getPayload());
    verify(transitBufferV2Service, times(1)).deleteTransitBufferRecord(any());
  }
}
