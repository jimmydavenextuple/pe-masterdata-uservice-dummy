/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import org.junit.jupiter.api.Test;

class JobExceptionTest {

  @Test
  void constructTest() {
    String jobId = "jobId";
    JobTypeEnum jobType = JobTypeEnum.UPLOAD_TRANSIT_TIMES;

    JobException jobException = new JobException(jobId, null);
    JobException jobException1 = new JobException(new Throwable(), jobId, jobType);
    JobException jobException2 = new JobException("", null, false, false, jobId, jobType);

    assertNotNull(jobException);
    assertNotNull(jobException.getJobId());
    assertNull(jobException.getJobType());
    assertNull(jobException.getMessage());

    assertNotNull(jobException1);
    assertNotNull(jobException1.getJobType());
    assertNotNull(jobException1.getJobId());
    assertNotNull(jobException1.getCause());

    assertNotNull(jobException2);
    assertNotNull(jobException2.getJobType());
    assertNotNull(jobException2.getJobId());
    assertNull(jobException2.getCause());
  }
}
