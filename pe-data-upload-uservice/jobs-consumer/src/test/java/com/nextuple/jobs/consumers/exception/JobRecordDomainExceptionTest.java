/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import static com.nextuple.jobs.consumers.util.Constants.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JobRecordDomainExceptionTest {
  final String jobRecordId = "jobRecordId";

  @Test
  void constructTest() {
    JobRecordDomainException e = new JobRecordDomainException(jobRecordId);
    JobRecordDomainException e1 = new JobRecordDomainException(message, jobRecordId);
    JobRecordDomainException e2 = new JobRecordDomainException(message, cause, jobRecordId);
    JobRecordDomainException e3 = new JobRecordDomainException(cause, jobRecordId);
    JobRecordDomainException e4 =
        new JobRecordDomainException(
            message, cause, enableSuppression, writableStackTrace, jobRecordId);

    // construct1
    Assertions.assertNotNull(e);
    Assertions.assertEquals(jobRecordId, e.getJobRecordId());
    Assertions.assertNull(e.getMessage());
    Assertions.assertNull(e.getCause());

    // construct2
    Assertions.assertNotNull(e1);
    Assertions.assertEquals(message, e1.getMessage());
    Assertions.assertEquals(jobRecordId, e1.getJobRecordId());
    Assertions.assertNull(e1.getCause());

    // construct3
    Assertions.assertNotNull(e2);
    Assertions.assertEquals(message, e2.getMessage());
    Assertions.assertEquals(jobRecordId, e2.getJobRecordId());
    Assertions.assertEquals(cause, e2.getCause());

    // construct4
    Assertions.assertNotNull(e3);
    Assertions.assertEquals(jobRecordId, e3.getJobRecordId());
    Assertions.assertEquals(cause, e3.getCause());

    // construct5
    Assertions.assertNotNull(e4);
    Assertions.assertEquals(message, e4.getMessage());
    Assertions.assertEquals(jobRecordId, e4.getJobRecordId());
    Assertions.assertEquals(cause, e4.getCause());
  }
}
