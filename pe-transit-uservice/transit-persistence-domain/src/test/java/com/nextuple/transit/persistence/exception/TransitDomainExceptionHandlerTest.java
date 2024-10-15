/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.persistence.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class TransitDomainExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks TransitExceptionHandler transitExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling transit domain exception")
  void handleTransitDomainException() {
    TransitDomainException exception =
        new TransitDomainException(
            "Internal Server Error",
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        transitExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling transit buffer req job ref domain exception")
  void handleTransitBufferReqJobRefDomainException() {
    TransitBufferReqJobRefDomainException exception =
        new TransitBufferReqJobRefDomainException(
            "Internal Server Error",
            TestUtil.TRANS_BUFFER_REQ_JOB_REF_ID,
            TestUtil.TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        transitExceptionHandler.handleTransitBufferReqJobRefDomainException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling transit buffer job exception")
  void handleTransitBufferJobException() {
    TransitBufferJobException exception =
        new TransitBufferJobException("Error", null, TestUtil.JOB_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        transitExceptionHandler.handleTransitBufferJobException(exception);

    Assertions.assertEquals("Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
