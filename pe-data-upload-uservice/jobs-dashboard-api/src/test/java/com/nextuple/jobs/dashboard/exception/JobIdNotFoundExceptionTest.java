/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class JobIdNotFoundExceptionTest {

  @Test
  void construct() {
    String message = "message";
    String jobId = "jobId";

    JobIdNotFoundException e1 = new JobIdNotFoundException(jobId);
    JobIdNotFoundException e2 = new JobIdNotFoundException(message, jobId);

    assertNotNull(e1);
    assertNotNull(e1.getJobId());
    assertNotNull(e2);
    assertNotNull(e2.getJobId());
    assertNotNull(e2.getMessage());
  }
}
