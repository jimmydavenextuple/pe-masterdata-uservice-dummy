/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import lombok.Data;

@Data
public class JobRecordDomainException extends Exception {
  private final String jobRecordId;

  public JobRecordDomainException(String jobRecordId) {
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(String message, String jobRecordId) {
    super(message);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(String message, Throwable cause, String jobRecordId) {
    super(message, cause);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(Throwable cause, String jobRecordId) {
    super(cause);
    this.jobRecordId = jobRecordId;
  }

  public JobRecordDomainException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      String jobRecordId) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.jobRecordId = jobRecordId;
  }
}
