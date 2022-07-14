package com.hbc.core.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LocalCacheUpdateEventExceptionTest {

  @Test
  void constructTest() {
    LocalCacheUpdateEventException localCacheUpdateEventException =
        new LocalCacheUpdateEventException(
            "Error occurred while publishing LocalCacheUpdate Event", "entityName");

    assertEquals(
        "Error occurred while publishing LocalCacheUpdate Event",
        localCacheUpdateEventException.getMessage());
    assertEquals("entityName", localCacheUpdateEventException.getEntityName());
  }
}
