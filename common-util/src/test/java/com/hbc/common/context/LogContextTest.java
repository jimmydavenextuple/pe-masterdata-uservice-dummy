package com.hbc.common.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;


import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class LogContextTest {


  @DisplayName("Should set the request parameters to function argument")
  @Test
  void setRequestParametersTest() {
    LogContext logContext = LogContext.builder().build();
    Map<String, String> requestParameters = new HashMap<>();
    requestParameters.put("key", "value");
    logContext.setRequestParameters(requestParameters);
    assertEquals(requestParameters, logContext.getRequestParameters(), "Request parameters");
  }

  @DisplayName(
      "Should return string null if the argument in the getRequestParameter does not exist")
  @Test
  void setRequestParameterTest() {
    LogContext logContext = LogContext.builder().build();
    Map<String, String> requestParameters = new HashMap<>();
    logContext.setRequestHeaders(requestParameters);
    String headerName = "headerName";
    String headerValue = "headValue";
    logContext.setRequestParameter(headerName, headerValue);
    assertEquals(headerValue, logContext.getRequestParameter(headerName));
    assertEquals("null", logContext.getRequestParameter("testing", "null"), "Request Parameter");
  }

  @DisplayName("Should set the request parameters from the set function argument")
  @Test
  void setRequestHeaderTest() {
    LogContext logContext = LogContext.builder().build();
    String headerName = "headerName";
    String headerValue = "headValue";
    logContext.setRequestHeader(headerName, headerValue);
    assertEquals(headerValue, logContext.getRequestHeader(headerName), "Header Value");
  }

  @DisplayName("Should set the kafkaheader parameters from the set function arguments (Strings)")
  @Test
  void setKafkaHeaderTest() {
    LogContext logContext = LogContext.builder().build();
    String headerName = "headerName";
    String headerValue = "headValue";
    logContext.setKafkaHeader(headerName, headerValue);
    assertEquals(headerValue, logContext.getKafkaHeader(headerName), "Header Value");
  }

  @DisplayName("Should set the kafkaHeader parameters from the set function argument(Map)")
  @Test
  void setKafkaHeadersTest() {
    LogContext logContext = LogContext.builder().build();
    Map<String, Object> kafkaHeaders = new HashMap<>();
    logContext.setKafkaHeaders(kafkaHeaders);
    assertEquals(kafkaHeaders, logContext.getKafkaHeaders());
  }

  @DisplayName("Should return string null if the argument in the getkafkaHeader does not exist")
  @Test
  void getKafkaHeaderDefaultTest() {
    LogContext logContext = LogContext.builder().build();
    assertEquals("null", logContext.getKafkaHeader("testing", "null"));
  }

  @DisplayName("Should return string null if the argument in the getRequestHeader does not exist")
  @Test
  void getRequestHeaderDefaultTest() {
    LogContext logContext = LogContext.builder().build();
    assertEquals("null", logContext.getRequestHeader("testing", "null"));
  }

  @DisplayName(
      "Should return string null if the argument in the getRequestParameter does not exist")
  @Test
  void getRequestParameterDefaultTest() {
    LogContext logContext = LogContext.builder().build();
    assertEquals("null", logContext.getRequestParameter("testing", "null"));
  }

  @DisplayName("Should return empty map since logContext does not set anything")
  @Test
  void toMapEmptyTest() {
    LogContext logContext = LogContext.builder().build();
    Map<String, String> actual = logContext.toMap();
    assertTrue(actual.isEmpty(), "Map is empty");
  }

  @DisplayName(
      "Should return map that has all the correct keys and values that were set by the logContext")
  @Test
  void toMapTest() {
    LogContext logContext = LogContext.builder().build();
    String headerName = "headerName";
    String headerValue = "headValue";
    logContext.setKafkaHeader(headerName, headerValue);
    logContext.setRequestHeader(headerName, headerValue);
    logContext.setRequestParameter(headerName, headerValue);
    Map<String, String> actual = logContext.toMap();
    assertEquals(headerValue, actual.get("requestParameter_headerName"), "requestParameter Value");
    assertEquals(headerValue, actual.get("requestHeader_headerName"), "RequestHeader Value");
    assertEquals(headerValue, actual.get("kafkaHeader_headerName"), "KafkaHeader Value");
  }

}
