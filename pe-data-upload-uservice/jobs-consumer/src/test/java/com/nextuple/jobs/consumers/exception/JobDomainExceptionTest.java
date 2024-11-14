/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import static com.nextuple.jobs.consumers.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JobDomainExceptionTest {
  @Test
  void constructTest() {
    JobDomainException e = new JobDomainException(JobId);
    JobDomainException e1 = new JobDomainException(message, JobId);
    JobDomainException e2 = new JobDomainException(message, cause, JobId);
    JobDomainException e3 = new JobDomainException(cause, JobId);
    JobDomainException e4 =
        new JobDomainException(message, cause, enableSuppression, writableStackTrace, JobId);

    // construct1
    assertNotNull(e);
    Assertions.assertEquals(JobId, e.getJobId());
    assertNull(e.getMessage());
    assertNull(e.getCause());

    // construct2
    assertNotNull(e1);
    Assertions.assertEquals(message, e1.getMessage());
    Assertions.assertEquals(JobId, e1.getJobId());
    assertNull(e1.getCause());

    // construct3
    assertNotNull(e2);
    Assertions.assertEquals(message, e2.getMessage());
    Assertions.assertEquals(JobId, e2.getJobId());
    Assertions.assertEquals(cause, e2.getCause());

    // construct4
    assertNotNull(e3);
    Assertions.assertEquals(JobId, e3.getJobId());
    Assertions.assertEquals(cause, e3.getCause());

    // construct5
    assertNotNull(e4);
    Assertions.assertEquals(message, e4.getMessage());
    Assertions.assertEquals(JobId, e4.getJobId());
    Assertions.assertEquals(cause, e4.getCause());
  }
}
