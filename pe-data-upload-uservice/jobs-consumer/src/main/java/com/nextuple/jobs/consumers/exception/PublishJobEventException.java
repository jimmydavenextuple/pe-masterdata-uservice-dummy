/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PublishJobEventException extends Exception {

  private final String jobId;

  public PublishJobEventException(String jobId) {
    this.jobId = jobId;
  }

  public PublishJobEventException(String message, String jobId) {
    super(message);
    this.jobId = jobId;
  }

  public PublishJobEventException(String message, Throwable cause, String jobId) {
    super(message, cause);
    this.jobId = jobId;
  }

  public PublishJobEventException(Throwable cause, String jobId) {
    super(cause);
    this.jobId = jobId;
  }

  public PublishJobEventException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
  }
}
