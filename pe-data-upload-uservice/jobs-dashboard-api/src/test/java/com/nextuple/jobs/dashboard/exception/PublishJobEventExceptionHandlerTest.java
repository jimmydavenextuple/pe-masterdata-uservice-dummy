/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.error.ErrorResponse;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PublishJobEventExceptionHandlerTest {

  @InjectMocks private JobDashboardExceptionHandler jobDashboardExceptionHandler;

  @Test
  void handleJobIdNotFoundException() {
    JobIdNotFoundException e = mock(JobIdNotFoundException.class);

    when(e.getMessage()).thenReturn("testMsg");

    ResponseEntity<ErrorResponse> responseEntity =
        jobDashboardExceptionHandler.handleJobIdNotFoundException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
    verify(e, times(1)).getMessage();
  }

  @Test
  void handleFileMetaDataException() {
    FileMetaDataException e = mock(FileMetaDataException.class);

    when(e.getMessage()).thenReturn("testMsg");

    ResponseEntity<ErrorResponse> responseEntity =
        jobDashboardExceptionHandler.handleFileMetaDataException(e);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertEquals(
        ErrorResponse.class.toString(),
        Objects.requireNonNull(responseEntity.getBody()).getClass().toString());
    verify(e, times(1)).getMessage();
  }
}
