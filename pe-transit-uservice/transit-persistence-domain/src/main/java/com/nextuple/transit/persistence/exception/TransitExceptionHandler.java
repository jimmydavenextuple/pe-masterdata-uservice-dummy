/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class TransitExceptionHandler {

  @ExceptionHandler(TransitDomainException.class)
  public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x1770)
                .message("Internal Server Error")
                .build());
  }

  @ExceptionHandler(TransitBufferReqJobRefDomainException.class)
  public ResponseEntity<ErrorResponse> handleTransitBufferReqJobRefDomainException(
      TransitBufferReqJobRefDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff9)
                .message(e.getMessage())
                .errorField("id", FieldError.builder().rejectedValue(e.getId()).build())
                .errorField(
                    "extReferenceId",
                    FieldError.builder().rejectedValue(e.getExtReferenceId()).build())
                .build());
  }

  @ExceptionHandler(TransitBufferJobException.class)
  public ResponseEntity<ErrorResponse> handleTransitBufferJobException(
      TransitBufferJobException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffffd1)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }
}
