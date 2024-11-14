/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.TransitBufferService;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TransitBufferControllerTest {

  @InjectMocks private TransitBufferController transitBufferController;

  @Mock private TransitBufferService transitBufferService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void createTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.saveTransitBuffer(any())).thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.createTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).saveTransitBuffer(any());
  }

  @Test
  void getByOrgIdAndDestinationGeozoneTest() throws CommonServiceException {
    List<TransitBufferResponse> transitBufferResponses =
        List.of(testUtil.getTransitBufferResponse());

    when(transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferResponses);

    ResponseEntity<BaseResponse<List<TransitBufferResponse>>> response =
        transitBufferController.getByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponses.size(), response.getBody().getPayload().size());
    verify(transitBufferService, times(1))
        .getTransitBuffersByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.updateTransitBuffer(any())).thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.updateTransitBuffer(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).updateTransitBuffer(any());
  }

  @Test
  void deleteTransitBufferDetailsTest() throws CommonServiceException {
    TransitBufferResponse transitBufferResponse = testUtil.getTransitBufferResponse();

    when(transitBufferService.deleteTransitBufferDetails(any(), any(), any(), any()))
        .thenReturn(transitBufferResponse);

    ResponseEntity<BaseResponse<TransitBufferResponse>> response =
        transitBufferController.deleteTransitBufferDetails(testUtil.getTransitBufferRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(transitBufferResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).deleteTransitBufferDetails(any(), any(), any(), any());
  }

  @Test
  void getTransitBufferDetailsTest() throws CommonServiceException, IOException {
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferService.getTransitBufferDetails(any(), any()))
        .thenReturn(preSignedUrlResponse);

    ResponseEntity<BaseResponse<PreSignedUrlResponse>> response =
        transitBufferController.getTransitBufferDetails(1L, "user1");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(preSignedUrlResponse, response.getBody().getPayload());
    verify(transitBufferService, times(1)).getTransitBufferDetails(any(), any());
  }
}
