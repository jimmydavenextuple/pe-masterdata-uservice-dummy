package com.nextuple.pe;

import static com.nextuple.pe.ConsoleLogListener.EVENT_TIME_EPOCH_MILLIS;
import static com.nextuple.pe.ConsoleLogListener.EVENT_TIME_STAMP_NANOS;
import static com.nextuple.pe.ConsoleLogListener.GENERATED_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nextuple.pe.configs.ITenantConfig;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

class ConsoleLogListenerTest {
  @InjectMocks ConsoleLogListener consoleLogListener;
  @Mock ITenantConfig iTenantConfig;
  @Mock GsonExclusionStrategy gsonExclusionStrategy;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        consoleLogListener, "gsonExclusionStrategy", gsonExclusionStrategy);
  }

  @Test
  void testReceiveEvent() {
    when(iTenantConfig.getLogLevelMap()).thenReturn(new HashMap<>());
    ExceptionEvent exceptionEvent = new ExceptionEvent();
    exceptionEvent.setGeneratedId("12345");
    Instant i = Instant.now();
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss.SSS a");
    exceptionEvent.setEventTimestamp(formatter.format(new Date()));
    long stampedNanos = ChronoUnit.MICROS.between(Instant.EPOCH, i);
    exceptionEvent.setEventTimeStampNanos();

    consoleLogListener.receiveEvent(exceptionEvent);

    assertNull(MDC.get(GENERATED_ID));
    assertNull(MDC.get(EVENT_TIME_EPOCH_MILLIS));
    Assertions.assertTrue(
        Long.parseLong(exceptionEvent.getEventTimeStampNanos()) - stampedNanos < 5000000);
    assertNull(MDC.get(EVENT_TIME_STAMP_NANOS));
  }

  @Test
  void testParseException() {
    when(iTenantConfig.getLogLevelMap()).thenReturn(new HashMap<>());
    ExceptionEvent exceptionEvent = new ExceptionEvent();
    exceptionEvent.setGeneratedId("12345");
    Instant i = Instant.now();
    exceptionEvent.setEventTimestamp("abc");
    long stampedNanos = ChronoUnit.MICROS.between(Instant.EPOCH, i);
    exceptionEvent.setEventTimeStampNanos();
    when(iTenantConfig.getConsoleLogListenEnabledMap())
        .thenReturn(Map.of("ExceptionEvent", Boolean.FALSE));
    Assertions.assertDoesNotThrow(() -> consoleLogListener.receiveEvent(exceptionEvent));

    Assertions.assertNull(MDC.get(GENERATED_ID));
    assertNull(MDC.get(EVENT_TIME_EPOCH_MILLIS));
    Assertions.assertTrue(
        Long.parseLong(exceptionEvent.getEventTimeStampNanos()) - stampedNanos < 5000000);
    assertNull(MDC.get(EVENT_TIME_STAMP_NANOS));
  }
}
