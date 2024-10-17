/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nextuple.common.exception.CommonServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class JobsFrameworkUtilTest {

  @DisplayName("Test Valid File Name: Happy path")
  @Test
  void testValidateFileName_WithValidFileName() {
    assertDoesNotThrow(
        () -> JobsFrameworkUtil.validateFileName("test.csv"),
        "No exception should be thrown for a valid file name");
  }

  @DisplayName("Test Valid File Name: Exception scenarios")
  @ParameterizedTest
  @CsvSource(
      value = {
        ", another param because the @CSVSource doesn't accept empty string",
        "null",
        "test.txt",
        "tests.test.txt"
      },
      nullValues = {"null"})
  void testValidateFileName_WithInvalidFileName(String fileName) {
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> JobsFrameworkUtil.validateFileName(fileName));
    assertEquals("Invalid file uploaded, upload a csv file", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(400, exception.getHttpStatus().value());
    assertEquals(0x1778, exception.getErrorCode());
  }
}
