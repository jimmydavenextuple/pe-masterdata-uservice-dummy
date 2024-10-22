package com.nextuple.common.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nextuple.common.constants.CommonConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class LogContextTest {

  @Mock KafkaMessageHeaders kafkaHeaders;

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

  @DisplayName("Should ignore the requestHeader with x-api-key")
  @Test
  void toMapTestToIgnoreApiKey() {
    LogContext logContext = LogContext.builder().build();
    String headerName = "headerName";
    String headerValue = "headValue";
    String apiKey = "x-api-key";
    logContext.setKafkaHeader(headerName, headerValue);
    logContext.setRequestHeader(apiKey, headerValue);
    logContext.setRequestParameter(headerName, headerValue);
    Map<String, String> actual = logContext.toMap();
    assertEquals(headerValue, actual.get("requestParameter_headerName"), "requestParameter Value");
    assertNull(actual.get("requestHeader_headerName"));
    assertEquals(headerValue, actual.get("kafkaHeader_headerName"), "KafkaHeader Value");
  }

  @DisplayName(
      "Should return logContext that has the variables that were set from httpServletRequest")
  @Test
  void initFromRequestTest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String username = "username";
    String userRole = "userRole";
    String userLocale = "userLocale";
    String tenantId = "tenantId";
    String apiKey = "apiKey";
    String host = "host";
    String paramName = "paramName";
    String paramValue = "nextuple";
    String secondaryTenantIds = "secondaryTenantIds";

    request.addHeader(CommonConstants.HEADER_USER, username);
    request.addHeader(CommonConstants.HEADER_ROLE, userRole);
    request.addHeader(CommonConstants.HEADER_USER_LOCALE, userLocale);
    request.addHeader(CommonConstants.HEADER_TENANT_ID, tenantId);
    request.addHeader(CommonConstants.HEADER_API_KEY, apiKey);
    request.addHeader(host, host);
    request.addParameter(paramName, paramValue);
    LogContext logContext = LogContext.builder().build();

    LogContext actual = logContext.initFromRequest(request);

    assertEquals(username, actual.getUsername(), "Username");
    assertEquals(userLocale, actual.getUserLocale(), "User Locale");
    assertEquals(userRole, actual.getUserRole(), "User Role");
    assertEquals(tenantId, actual.getTenantId(), "Tenant Id");
    assertEquals(apiKey, actual.getApiKey(), "Api Key");
    assertEquals(host, actual.getTenantDnsName(), "Tenant DNS Name");
    assertEquals(paramValue, actual.getRequestParameter(paramName), "Parameter Value");
  }

  @DisplayName("Should return logContext that has the variables that were set from KafkaHeaders")
  @Test
  void initKafkaHeaderTest() {
    String username = "username";
    String userRole = "userRole";
    String userLocale = "userLocale";
    String tenantId = "tenantId";
    String apiKey = "apiKey";
    String host = "host";
    String receivedTopic = "receivedTopic";
    String eventType = "eventType";
    String eventName = "eventName";
    long eventDate = 1L;
    UUID id = new UUID(123, 123);

    Map<String, Object> headers = new HashMap<>();
    headers.put(CommonConstants.HEADER_USER, username);
    headers.put(CommonConstants.HEADER_ROLE, userRole);
    headers.put(CommonConstants.HEADER_USER_LOCALE, userLocale);
    headers.put(CommonConstants.HEADER_TENANT_ID, tenantId);
    headers.put(host, host);
    Set<Entry<String, Object>> set = headers.entrySet();

    when(kafkaHeaders.entrySet()).thenReturn(set);
    when(kafkaHeaders.containsKey(KafkaHeaders.RECEIVED_TOPIC)).thenReturn(true);
    when(kafkaHeaders.get(KafkaHeaders.RECEIVED_TOPIC)).thenReturn(receivedTopic);
    when(kafkaHeaders.containsKey(CommonConstants.HEADER_TENANT_ID)).thenReturn(true);
    when(kafkaHeaders.get(CommonConstants.HEADER_TENANT_ID)).thenReturn(tenantId);
    when(kafkaHeaders.containsKey(CommonConstants.HEADER_API_KEY)).thenReturn(true);
    when(kafkaHeaders.get(CommonConstants.HEADER_API_KEY)).thenReturn(apiKey);
    when(kafkaHeaders.containsKey(CommonConstants.HEADER_EVENT_NAME)).thenReturn(true);
    when(kafkaHeaders.get(CommonConstants.HEADER_EVENT_NAME)).thenReturn(eventName);
    when(kafkaHeaders.containsKey(CommonConstants.HEADER_EVENT_TYPE)).thenReturn(true);
    when(kafkaHeaders.get(CommonConstants.HEADER_EVENT_TYPE)).thenReturn(eventType);
    when(kafkaHeaders.containsKey(CommonConstants.HEADER_EVENT_DATE)).thenReturn(true);
    when(kafkaHeaders.get(CommonConstants.HEADER_EVENT_DATE)).thenReturn(eventDate);
    when(kafkaHeaders.getId()).thenReturn(id);
    LogContext logContext = LogContext.builder().build();
    LogContext actual = logContext.initFromKafka(kafkaHeaders);

    assertEquals(username, actual.getUsername(), "Username");
    assertEquals(userLocale, actual.getUserLocale(), "User Locale");
    assertEquals(userRole, actual.getUserRole(), "User Role");
    assertEquals(tenantId, actual.getTenantId(), "Tenant Id");
    assertEquals(apiKey, actual.getApiKey(), "Api Key");
    assertEquals(host, actual.getTenantDnsName(), "Tenant DNS Name");
    assertEquals(receivedTopic, actual.getKafkaTopic(), "Kafka Topic");
    assertEquals(tenantId, actual.getTenantId(), "Tenant Id");
    assertEquals(eventName, actual.getKafkaEventName(), "Kafka Event Name");
    assertEquals(eventType, actual.getKafkaEventType(), "Kafka Event Type");
    assertEquals(eventDate, actual.getKafkaEventDate(), "Kafka Event Date");
    assertEquals(id.toString(), actual.getKafkaMessageId(), "Kafka Message Id");
  }
}
