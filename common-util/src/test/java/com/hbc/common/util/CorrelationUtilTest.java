package com.hbc.common.util;

import static com.hbc.common.context.LogContext.CORRELATION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hbc.common.context.CurrentThreadContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class CorrelationUtilTest {

  @Mock KafkaMessageHeaders kafKaheaders;

  @DisplayName("Should return the accurate header that was placed in the HTTPServletRequest")
  @Test
  void extractCorrelationIdTest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(CORRELATION_ID, "test");
    String expected = "test";
    String actual = CorrelationUtil.extractCorrelationId(request);
    assertEquals(expected, actual, "Correlation Id");

    MockHttpServletRequest request2 = new MockHttpServletRequest();
    request2.addHeader(CORRELATION_ID, "nextuple");
    String expected2 = "test";
    String actual2 = CorrelationUtil.extractCorrelationId(request2);
    assertNotEquals(expected2, actual2, "Correlation Id");
  }

  @DisplayName("Should return the accurate header value that was placed in the kafkaMessageHeader")
  @Test
  void extractCorrelationIdKafkaTest() {
    String expected = "TestID";
    when(kafKaheaders.containsKey(CORRELATION_ID)).thenReturn(true);
    when(kafKaheaders.get(CORRELATION_ID)).thenReturn(expected);

    String actual = CorrelationUtil.extractCorrelationId(kafKaheaders);

    assertEquals(expected, actual, "Correlation Id");

    when(kafKaheaders.containsKey(CORRELATION_ID)).thenReturn(false);
    actual = CorrelationUtil.extractCorrelationId(kafKaheaders);
    assertNull(actual, "Correlation Id");
  }

  @DisplayName("Should return whether if the correlationId has been set or not")
  @Test
  void isCorrelationIdSetTest() {
    boolean expected = true;
    String correlationId = "correlationId";
    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
    boolean actual = CorrelationUtil.isCorrelationIdSet();
    assertEquals(expected, actual, "Correlation Id");

    boolean expected2 = false;
    correlationId = "";
    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
    boolean actual2 = CorrelationUtil.isCorrelationIdSet();

    assertEquals(expected2, actual2, "Correlation Id");
  }

  @DisplayName("Should return whether if the service correlationId has been set or not")
  @Test
  void isServiceCorrelationIdSetTest() {
    boolean expected = true;
    String correlationId = "correlationId";
    CurrentThreadContext.getLogContext().setServiceCorrelationId(correlationId);
    boolean actual = CorrelationUtil.isServiceCorrelationIdSet();
    assertEquals(expected, actual, "Service Correlation Id");

    boolean expected2 = false;
    correlationId = "";
    CurrentThreadContext.getLogContext().setServiceCorrelationId(correlationId);
    boolean actual2 = CorrelationUtil.isServiceCorrelationIdSet();
    assertEquals(expected2, actual2, "Service Correlation Id");
  }

  @DisplayName("Should return null or the service correlation Id depending if it was set")
  @Test
  void getCurrentServiceCorrelationIdTest() {
    String expected = "correlationId";
    CurrentThreadContext.getLogContext().setServiceCorrelationId(expected);
    String actual = CorrelationUtil.getCurrentServiceCorrelationId();
    assertEquals(expected, actual, "Service correlation Id");

    CurrentThreadContext.getLogContext().setServiceCorrelationId("temp");
    actual = CorrelationUtil.getCurrentServiceCorrelationId();
    assertNotEquals(expected, actual, "Service correlation Id");
  }

  @DisplayName("Should return the current correlation Id if it was set")
  @Test
  void getCurrentCorrelationIdTest() {
    String expected1 = "correlationId";
    CurrentThreadContext.getLogContext().setCorrelationId(expected1);
    String actual1 = CorrelationUtil.getCurrentCorrelationId();
    assertEquals(expected1, actual1, "Correlation id");

    String expected2 = "test";
    CurrentThreadContext.getLogContext().setCorrelationId(expected1);
    String actual2 = CorrelationUtil.getCurrentCorrelationId();
    assertNotEquals(expected2, actual2, "Correlation id");
  }

  @DisplayName("Should set the correlationId to the argument in the function")
  @Test
  void setCorrelationIdTest() {
    String expected1 = "correlationId";
    CorrelationUtil.setCorrelationId(expected1);
    String actual1 = CorrelationUtil.getCurrentCorrelationId();
    assertEquals(expected1, actual1, "Correlation id");

    String expected2 = "testing";
    CorrelationUtil.setCorrelationId(expected1);
    String actual2 = CorrelationUtil.getCurrentCorrelationId();
    assertNotEquals(expected2, actual2, "Correlation id");
  }

  @DisplayName("Should set the service correlationId to the argument in the function")
  @Test
  void setServiceCorrelationIdTest() {
    String expected1 = "correlationId";
    CorrelationUtil.setServiceCorrelationId(expected1);
    String actual1 = CorrelationUtil.getCurrentServiceCorrelationId();
    assertEquals(expected1, actual1, "Service Correlation Id ");

    String expected2 = "testing";
    CorrelationUtil.setServiceCorrelationId(expected1);
    String actual2 = CorrelationUtil.getCurrentServiceCorrelationId();
    assertNotEquals(expected2, actual2, "Service Correlation Id");
  }

  @DisplayName("Should set the correlationId to another random correlation Id")
  @Test
  void setNewCorrelationIdTest() {
    // Nothing is set yet
    CorrelationUtil.setCorrelationId(null);
    assertFalse(CorrelationUtil.isCorrelationIdSet());

    CorrelationUtil.setNewCorrelationId();
    assertTrue(CorrelationUtil.isCorrelationIdSet());
  }

  @DisplayName("Should set the service correaltionId to another random service correlation Id")
  @Test
  void setNewServiceCorrelationIdTest() {
    // Nothing is set yet
    assertFalse(CorrelationUtil.isServiceCorrelationIdSet());

    CorrelationUtil.setNewServiceCorrelationId();
    assertTrue(CorrelationUtil.isServiceCorrelationIdSet());
  }

  @DisplayName(
      "Should set the service correlationId to a random Id and correlationId to the value in the argument of the function")
  @Test
  void processCorrelationIdNotSetTest() {
    String correlationId = "correlationId";
    CorrelationUtil.processCorrelationId(correlationId);
    assertTrue(CorrelationUtil.isCorrelationIdSet());
    assertEquals(correlationId, CorrelationUtil.getCurrentCorrelationId());
    assertTrue(CorrelationUtil.isServiceCorrelationIdSet());
  }

  @DisplayName(
      "Should set the correlationId to a new correlationId and service correlationId to a random Id ")
  @Test
  void processCorrelationIdSetNotServiceTest() {
    String correlationId = "correlationId";
    CorrelationUtil.setCorrelationId("testing");
    CorrelationUtil.processCorrelationId(correlationId);
    assertTrue(CorrelationUtil.isCorrelationIdSet());
    assertEquals(CorrelationUtil.getCurrentCorrelationId(), correlationId);
    assertTrue(CorrelationUtil.isServiceCorrelationIdSet());
  }

  @DisplayName("Should set the correlationId and service CorrelationId to random Ids ")
  @Test
  void processCorrelationIdNullTest() {
    CorrelationUtil.processCorrelationId();
    assertTrue(CorrelationUtil.isCorrelationIdSet());
    assertTrue(CorrelationUtil.isServiceCorrelationIdSet());
  }
}
