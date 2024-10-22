package com.nextuple.common.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.nextuple.common.exception.CommonServiceException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LoggerFactoryTest {

  private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

  private static final String ERROR = "error";
  @InjectMocks Logger logger;
  @Mock org.slf4j.Logger slf4jLogger;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("Test parameters to function argument in logger factory")
  @Test
  void parametersTestForLoggerFactory() {
    Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
    Logger logger_temp1 = LoggerFactory.getLogger("LoggerTest");
    org.slf4j.Logger logger_temp2 = LoggerFactory.getSlf4jLogger("Logger");
    org.slf4j.Logger logger_temp3 = LoggerFactory.getSlf4jLogger(LoggerFactoryTest.class);
    Map<String, String> metaData = new HashMap<>();
    metaData.put("key", "value");
    CommonServiceException commonServiceException =
        new CommonServiceException(ERROR, HttpStatus.BAD_REQUEST, 400, new HashMap<>());
    logger.error(ERROR, EMPTY_MAP);
    logger.error(commonServiceException);
    logger.error(commonServiceException, EMPTY_MAP);
    logger.error(metaData, ERROR, EMPTY_MAP);
    logger.error(commonServiceException, EMPTY_MAP, ERROR, EMPTY_MAP);
    logger.error(commonServiceException, ERROR, EMPTY_MAP);
    logger.debug("debug", EMPTY_MAP);
    logger.debug(EMPTY_MAP, "debug", EMPTY_MAP);
    logger.info(EMPTY_MAP, "info", EMPTY_MAP);
    logger.info("info", EMPTY_MAP);
    logger_temp3.info("info");
    logger.warn("warn", EMPTY_MAP);
    logger.warn(EMPTY_MAP, "warn", EMPTY_MAP);
    logger_temp1.warn("warn");
    logger.warn("warn", EMPTY_MAP);
    logger.trace("trace", EMPTY_MAP);
    logger.trace(EMPTY_MAP, "tract", EMPTY_MAP);
    logger_temp2.trace("trace");
    assertEquals(ERROR, commonServiceException.getMessage());
  }

  @Test
  void isDebugEnabledTest() {
    Mockito.when(slf4jLogger.isDebugEnabled()).thenReturn(true);
    Assertions.assertTrue(logger.isDebugEnabled());
    Mockito.verify(slf4jLogger).isDebugEnabled();
  }

  @Test
  void isDebugDisabledTest() {
    Mockito.when(slf4jLogger.isDebugEnabled()).thenReturn(false);
    Assertions.assertFalse(logger.isDebugEnabled());
    Mockito.verify(slf4jLogger).isDebugEnabled();
  }

  @Test
  void isInfoEnabledTest() {
    Mockito.when(slf4jLogger.isInfoEnabled()).thenReturn(true);
    Assertions.assertTrue(logger.isInfoEnabled());
    Mockito.verify(slf4jLogger).isInfoEnabled();
  }

  @Test
  void isInfoDisabledTest() {
    Mockito.when(slf4jLogger.isInfoEnabled()).thenReturn(false);
    Assertions.assertFalse(logger.isInfoEnabled());
    Mockito.verify(slf4jLogger).isInfoEnabled();
  }

  @Test
  void isLogLevelEnabledTest() {
    Mockito.when(slf4jLogger.isEnabledForLevel(any())).thenReturn(false);
    Assertions.assertFalse(logger.isLogLevelEnabled(Level.DEBUG));
    Mockito.verify(slf4jLogger).isEnabledForLevel(any());
  }
}
