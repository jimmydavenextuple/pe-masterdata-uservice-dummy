package com.hbc.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import org.junit.jupiter.api.Test;

class PromiseEngineExceptionTest {
  @Test
  void constructTest() {
    PromiseEngineException promiseEngineException =
        new PromiseEngineException(
            ApplicationLayer.DAO_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, "test");

    assertEquals("test", promiseEngineException.getMessage());
    assertEquals(ApplicationLayer.DAO_LAYER, promiseEngineException.getApplicationLayer());
    assertEquals(ExceptionCodeMapping.DAO_NOT_FOUND, promiseEngineException.getExceptionCode());
  }
}
