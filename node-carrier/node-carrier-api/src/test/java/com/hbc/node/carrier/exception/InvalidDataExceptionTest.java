package com.hbc.node.carrier.exception;

import com.hbc.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InvalidDataExceptionTest {

  @Test
  @DisplayName("Testing invalid data exception")
  void constructTest() {
    InvalidDataException invalidDataException =
        new InvalidDataException("Invalid time format", TestUtil.LAST_PICKUP_TIME);

    Assertions.assertEquals("Invalid time format", invalidDataException.getMessage());
    Assertions.assertEquals(TestUtil.LAST_PICKUP_TIME, invalidDataException.getLastPickupTime());
  }
}
