/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.exception;

import static com.nextuple.jobs.consumers.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class JobExceptionTest {

  @Test
  void constructTest() {
    Integer pageNo = 10;
    JobException e = new JobException(JobId, pageNo);
    JobException e1 = new JobException(message, JobId, pageNo);
    JobException e2 = new JobException(message, cause, JobId, pageNo);
    JobException e3 = new JobException(cause, JobId, pageNo);
    JobException e4 =
        new JobException(message, cause, enableSuppression, writableStackTrace, JobId, pageNo);

    // construct1
    assertNotNull(e);
    assertEquals(JobId, e.getJobId());
    assertEquals(pageNo, e.getPageNo());
    assertNull(e.getMessage());
    assertNull(e.getCause());

    // construct2
    assertNotNull(e1);
    assertEquals(message, e1.getMessage());
    assertEquals(JobId, e1.getJobId());
    assertEquals(pageNo, e1.getPageNo());
    assertNull(e1.getCause());

    // construct3
    assertNotNull(e2);
    assertEquals(message, e2.getMessage());
    assertEquals(JobId, e2.getJobId());
    assertEquals(pageNo, e2.getPageNo());
    assertEquals(cause, e2.getCause());

    // construct4
    assertNotNull(e3);
    assertEquals(JobId, e3.getJobId());
    assertEquals(pageNo, e3.getPageNo());
    assertEquals(cause, e3.getCause());

    // construct5
    assertNotNull(e4);
    assertEquals(message, e4.getMessage());
    assertEquals(JobId, e4.getJobId());
    assertEquals(pageNo, e4.getPageNo());
    assertEquals(cause, e4.getCause());
  }
}
