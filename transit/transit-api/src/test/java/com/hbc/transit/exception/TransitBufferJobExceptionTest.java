package com.hbc.transit.exception;

import com.hbc.transit.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransitBufferJobExceptionTest {

  @Test
  @DisplayName("Testing TransitBufferJobException")
  void constructTest() {
    TransitBufferJobException transitBufferJobException =
        new TransitBufferJobException("error", null, TestUtil.JOB_ID);
    Assertions.assertEquals("error", transitBufferJobException.getMessage());
    Assertions.assertEquals(TestUtil.JOB_ID, transitBufferJobException.getJobId());
  }
}
