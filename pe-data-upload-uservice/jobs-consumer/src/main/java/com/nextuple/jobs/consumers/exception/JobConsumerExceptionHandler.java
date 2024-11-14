/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
@SuppressWarnings("squid:S1192")
public class JobConsumerExceptionHandler {

  @ExceptionHandler(DuplicateJobException.class)
  public ResponseEntity<Object> handleDuplicateJobException(DuplicateJobException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(InvalidDateException.class)
  public ResponseEntity<Object> handleInvalidDateException(InvalidDateException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff1)
                .message(e.getMessage())
                .errorField("date", FieldError.builder().rejectedValue(e.getDate()).build())
                .build());
  }

  @ExceptionHandler(InvalidJobTypeException.class)
  public ResponseEntity<Object> handleJobIdNotFoundException(InvalidJobTypeException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff4)
                .message(e.getMessage())
                .errorField("jobType", FieldError.builder().rejectedValue(e.getJobType()).build())
                .build());
  }

  @ExceptionHandler(JobRecordDomainException.class)
  public ResponseEntity<Object> handleJobRecordDomainException(JobRecordDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff6)
                .message(e.getMessage())
                .errorField(
                    "jobRecordId", FieldError.builder().rejectedValue(e.getJobRecordId()).build())
                .build());
  }

  @ExceptionHandler(PublishJobEventException.class)
  public ResponseEntity<Object> handleJobDashboardException(PublishJobEventException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff7)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(JobDomainException.class)
  public ResponseEntity<Object> handleJobDomainException(JobDomainException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffffd1)
                .message(e.getMessage())
                .errorField("jobId", FieldError.builder().rejectedValue(e.getJobId()).build())
                .build());
  }

  @ExceptionHandler(JobException.class)
  public ResponseEntity<Object> handleJobException(JobException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xffffd2)
                .message(e.getMessage())
                .errorField("jobType", FieldError.builder().rejectedValue(e.getJobId()).build())
                .errorField("pageNo", FieldError.builder().rejectedValue(e.getPageNo()).build())
                .build());
  }

  @ExceptionHandler(FeignClientMapperException.class)
  public ResponseEntity<ErrorResponse> handleFeignClientMapperException(
      FeignClientMapperException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffd3).message(e.getMessage()).build());
  }

  @ExceptionHandler(TransitMapperException.class)
  public ResponseEntity<ErrorResponse> handleTransitMapperException(TransitMapperException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffd4).message(e.getMessage()).build());
  }

  @ExceptionHandler(NodeCarrierMapperException.class)
  public ResponseEntity<ErrorResponse> handleNodeCarrierMapperException(
      NodeCarrierMapperException e) {
    return ResponseEntity.badRequest()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xffffd5).message(e.getMessage()).build());
  }
}
