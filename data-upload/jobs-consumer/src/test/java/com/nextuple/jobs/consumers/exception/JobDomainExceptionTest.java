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
