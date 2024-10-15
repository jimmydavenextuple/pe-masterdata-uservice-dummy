/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferConfigRequestService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

class TransitBufferConfigRequestControllerTest {

  @InjectMocks private TransitBufferConfigRequestController transitBufferConfigRequestController;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTransitBufferConfigRequestTest()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(transitBufferConfigRequestService.processTransitBufferRequest(
            any(TransitBufferConfigRequest.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED));

    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> responseEntity =
        transitBufferConfigRequestController.processTransitBufferConfigRequest(
            transitBufferConfigRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        responseEntity.getBody().getPayload());
    verify(transitBufferConfigRequestService, times(1)).processTransitBufferRequest(any());
  }

  @Test
  void createTransitBufferConfigRequestExceptionTest()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(transitBufferConfigRequestService.processTransitBufferRequest(
            any(TransitBufferConfigRequest.class)))
        .thenThrow(new RuntimeException("Failed to create transit buffer config request"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.processTransitBufferConfigRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Failed to create transit buffer config request", exception.getMessage());

    verify(transitBufferConfigRequestService, times(1)).processTransitBufferRequest(any());
  }

  @Test
  void updateTransitBufferConfigRequestStatusTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            any(), any(TransitBufferConfigRequestStatusEnum.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigResponse(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));

    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> responseEntity =
        transitBufferConfigRequestController.updateTransitBufferConfigRequestStatus(
            TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        responseEntity.getBody().getPayload());
    verify(transitBufferConfigRequestService, times(1))
        .updateTransitBufferRequestStatus(any(), any());
  }

  @Test
  void updateTransitBufferConfigRequestStatusExceptionTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            any(), any(TransitBufferConfigRequestStatusEnum.class)))
        .thenThrow(
            new RuntimeException(
                "Failed to process transit buffer config status updation request"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.updateTransitBufferConfigRequestStatus(
                    TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS));
    Assertions.assertEquals(
        "Failed to process transit buffer config status updation request", exception.getMessage());
    verify(transitBufferConfigRequestService, times(1))
        .updateTransitBufferRequestStatus(any(), any());
  }

  @Test
  void getTransitBufferConfigRequestsTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.fetchTransitBufferRequests(anyString(), anyString()))
        .thenReturn(
            List.of(
                testUtil.getTransitBufferConfigResponse(
                    TransitBufferConfigRequestStatusEnum.INPROGRESS)));

    ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>> responseEntity =
        transitBufferConfigRequestController.getTransitBufferConfigRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        responseEntity.getBody().getPayload().get(0));
    verify(transitBufferConfigRequestService, times(1))
        .fetchTransitBufferRequests(anyString(), anyString());
  }

  @Test
  void getTransitBufferConfigRequestsExceptionTest() throws CommonServiceException {
    when(transitBufferConfigRequestService.fetchTransitBufferRequests(anyString(), anyString()))
        .thenThrow(new RuntimeException("Failed to get transit buffer config requests"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                transitBufferConfigRequestController.getTransitBufferConfigRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Failed to get transit buffer config requests", exception.getMessage());
    verify(transitBufferConfigRequestService, times(1))
        .fetchTransitBufferRequests(anyString(), anyString());
  }

  @Test
  void deleteTransitBufferRequestTest()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
          CsvException {
    when(transitBufferConfigRequestService.deleteTransitBufferRequest(any(), any()))
        .thenReturn(
            testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED));
    ResponseEntity<BaseResponse<TransitBufferConfigResponse>> res =
        transitBufferConfigRequestController.deleteTransitBufferConfigRequest(
            TestUtil.ID, TestUtil.CREATED_BY);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }
}
