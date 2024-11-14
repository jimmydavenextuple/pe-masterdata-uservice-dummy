/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobException extends Exception {
  private final String jobId;
  private final Integer pageNo;

  public JobException(String jobId, Integer pageNo) {
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(String message, String jobId, Integer pageNo) {
    super(message);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(String message, Throwable cause, String jobId, Integer pageNo) {
    super(message, cause);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(Throwable cause, String jobId, Integer pageNo) {
    super(cause);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }

  public JobException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobId,
      Integer pageNo) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobId = jobId;
    this.pageNo = pageNo;
  }
}
