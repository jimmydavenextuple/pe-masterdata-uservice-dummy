package com.hbc.jobs.consumers.exception;

import static com.hbc.jobs.consumers.util.Constants.JobId;
import static com.hbc.jobs.consumers.util.Constants.cause;
import static com.hbc.jobs.consumers.util.Constants.enableSuppression;
import static com.hbc.jobs.consumers.util.Constants.writableStackTrace;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.jobs.consumers.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DuplicateJobExceptionTest {
  @Test
  void constructTest() {
    DuplicateJobException e = new DuplicateJobException(JobId);
    DuplicateJobException e1 = new DuplicateJobException(Constants.message, JobId);
    DuplicateJobException e2 = new DuplicateJobException(Constants.message, cause, JobId);
    DuplicateJobException e3 = new DuplicateJobException(cause, JobId);
    DuplicateJobException e4 =
        new DuplicateJobException(
            Constants.message, cause, enableSuppression, writableStackTrace, JobId);

    // construct1
    assertNotNull(e);
    Assertions.assertEquals(JobId, e.getJobId());
    assertNull(e.getMessage());
    assertNull(e.getCause());

    // construct2
    assertNotNull(e1);
    Assertions.assertEquals(Constants.message, e1.getMessage());
    Assertions.assertEquals(JobId, e1.getJobId());
    assertNull(e1.getCause());

    // construct3
    assertNotNull(e2);
    Assertions.assertEquals(Constants.message, e2.getMessage());
    Assertions.assertEquals(JobId, e2.getJobId());
    Assertions.assertEquals(cause, e2.getCause());

    // construct4
    assertNotNull(e3);
    Assertions.assertEquals(JobId, e3.getJobId());
    Assertions.assertEquals(cause, e3.getCause());

    // construct5
    assertNotNull(e4);
    Assertions.assertEquals(Constants.message, e4.getMessage());
    Assertions.assertEquals(JobId, e4.getJobId());
    Assertions.assertEquals(cause, e4.getCause());
  }
}
