/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class NodeCarrierExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks NodeCarrierExceptionHandler nodeCarrierExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling node carrier domain exception")
  void handleNodeCarrierDomainException() {
    NodeCarrierDomainException exception =
        new NodeCarrierDomainException(
            "Internal Server Error",
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        nodeCarrierExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling node carrier selection domain exception")
  void handleNodeCarrierSelectionDomainException() {
    NodeCarrierSelectionDomainException exception =
        new NodeCarrierSelectionDomainException(
            "Internal Server Error",
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        nodeCarrierExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling invalid data exception")
  void handleInvalidDataException() {
    InvalidDataException exception =
        new InvalidDataException("Invalid time format", TestUtil.LAST_PICKUP_TIME, null);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        nodeCarrierExceptionHandler.handleInvalidDataException(exception);

    Assertions.assertEquals(
        "Invalid time format", errorResponseResponseEntity.getBody().getMessage());
  }
}
