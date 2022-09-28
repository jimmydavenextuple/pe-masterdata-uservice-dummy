package com.hbc.jobs.dashboard.exception;

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
