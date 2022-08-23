package com.hbc.jobs.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
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
