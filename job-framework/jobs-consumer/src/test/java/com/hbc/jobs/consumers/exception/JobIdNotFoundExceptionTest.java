package com.hbc.jobs.consumers.exception;

import static com.hbc.jobs.consumers.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JobIdNotFoundExceptionTest {
  @Test
  void constructTest() {
    JobIdNotFoundException e = new JobIdNotFoundException(JobId);
    JobIdNotFoundException e1 = new JobIdNotFoundException(message, JobId);
    JobIdNotFoundException e2 = new JobIdNotFoundException(message, cause, JobId);
    JobIdNotFoundException e3 = new JobIdNotFoundException(cause, JobId);
    JobIdNotFoundException e4 =
        new JobIdNotFoundException(message, cause, enableSuppression, writableStackTrace, JobId);

    // construct1
    assertNotNull(e);
    Assertions.assertEquals(JobId, e.getJobId());
    Assertions.assertNull(e.getMessage());
    Assertions.assertNull(e.getCause());

    // construct2
    assertNotNull(e1);
    Assertions.assertEquals(message, e1.getMessage());
    Assertions.assertEquals(JobId, e1.getJobId());
    Assertions.assertNull(e1.getCause());

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
