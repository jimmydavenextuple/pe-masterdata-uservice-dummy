/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.utils;

import static org.mockito.Mockito.when;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilsTest {

  @Mock FeignException feignException;

  @DisplayName("Should return Error Response that has message 'upstream error'")
  @Test
  void parseNullFeignExceptionTest() {
    ErrorResponse actual = ExceptionUtils.parseFeignException(null);
    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error")
            .build();
    Assertions.assertEquals(actual, expected, "Error Response");
  }

  @DisplayName("Should return Error Response where jsonUtil converts the UFT8 Content to be null ")
  @Test
  void parseNullUTF8FeignExceptionTest() {

    String jsonStr = "fakeStr";

    when(feignException.contentUTF8()).thenReturn(jsonStr);

    ErrorResponse actual = ExceptionUtils.parseFeignException(feignException);

    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error : " + jsonStr)
            .build();
    Assertions.assertEquals(expected, actual);
  }

  @DisplayName(
      "Should return Error Response where jsonUtil converts the UFT8 Content correctly and gets the correct exception message ")
  @Test
  void parseUTF8FeignExceptionTest() {
    String message = "message";

    when(feignException.contentUTF8()).thenReturn("");
    when(feignException.getMessage()).thenReturn(message);

    ErrorResponse actual = ExceptionUtils.parseFeignException(feignException);
    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error : " + feignException.getMessage())
            .build();

    Assertions.assertEquals(expected, actual);
  }
}
