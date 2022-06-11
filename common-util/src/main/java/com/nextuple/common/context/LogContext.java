package com.nextuple.common.context;

import com.nextuple.common.util.DateUtil;
import com.nextuple.common.util.KafkaUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/** This class will be used to form a context for logging which will be exposed to log file */
@Builder(toBuilder = true)
public class LogContext {
  public static final String KAFKA_EVENT_DATE = "kafkaEventDate";
  public static final String CORRELATION_ID = "correlationId";
  public static final String HOST_NAME = "host";
  public static final String APPLICATION_NAME = "applicationName";
  public static final String APPLICATION_VERSION = "applicationVersion";
  public static final String REQUEST_HEADERS = "requestHeader_";
  public static final String KAFKA_HEADERS = "kafkaHeader_";
  public static final String REQUEST_ENDPOINT = "requestEndpoint";
  public static final String REQUEST_METHOD = "requestMethod";
  public static final String KAFKA_MESSAGE_ID = "kafkaMessageId";
  public static final String KAFKA_TOPIC = "kafkaTopic";
  public static final String KAFKA_EVENT_NAME = "kafkaEventName";
  public static final String KAFKA_EVENT_TYPE = "kafkaEventType";
  public static final String REQUEST_PARAMETERS = "requestParameter_";
  public static final String RESPONSE_CODE = "responseCode";
  public static final String TRACE_ID = "traceId";
  private static final Logger logger = LoggerFactory.getLogger(LogContext.class);
  private Map<String, String> requestHeaders;
  private Map<String, String> requestParameters;
  private Map<String, String> customFields;
  private Map<String, Object> kafkaHeaders;

  public LogContext put(String key, String value) {
    if (customFields == null) {
      customFields = new HashMap<>();
    }
    customFields.put(key, value);
    return this;
  }

  public String get(String key) {
    return get(key, null);
  }

  public String get(String key, String defaultIfNotFound) {
    if (customFields == null) {
      customFields = new HashMap<>();
    }
    return customFields.getOrDefault(key, defaultIfNotFound);
  }

  public String getTraceId() {
    return get(TRACE_ID);
  }

  public LogContext setTraceId(String traceId) {
    put(TRACE_ID, traceId);
    return this;
  }

  public String getCorrelationId() {
    return get(CORRELATION_ID);
  }

  public LogContext setCorrelationId(String correlationId) {
    return put(CORRELATION_ID, correlationId);
  }

  public String getApplicationName() {
    return get(APPLICATION_NAME);
  }

  public LogContext setApplicationName(String applicationName) {
    return put(APPLICATION_NAME, applicationName);
  }

  public String getHostName() {
    return get(HOST_NAME);
  }

  public LogContext setHostName(String hostName) {
    return put(HOST_NAME, hostName);
  }

  public String getApplicationVersion() {
    return get(APPLICATION_VERSION);
  }

  public LogContext setApplicationVersion(String applicationVersion) {
    return put(APPLICATION_VERSION, applicationVersion);
  }

  public String getRequestMethod() {
    return get(REQUEST_METHOD);
  }

  public LogContext setRequestMethod(String requestMethod) {
    return put(REQUEST_METHOD, requestMethod);
  }

  public String getKafkaMessageId() {
    return get(KAFKA_MESSAGE_ID);
  }

  public LogContext setKafkaMessageID(String messageID) {
    return put(KAFKA_MESSAGE_ID, messageID);
  }

  public String getKafkaTopic() {
    return get(KAFKA_TOPIC);
  }

  public LogContext setKafkaTopic(String kafkaTopic) {
    return put(KAFKA_TOPIC, kafkaTopic);
  }

  public String getKafkaEventName() {
    return get(KAFKA_EVENT_NAME);
  }

  public LogContext setKafkaEventName(String kafkaEventName) {
    return put(KAFKA_EVENT_NAME, kafkaEventName);
  }

  public String getKafkaEventType() {
    return get(KAFKA_EVENT_TYPE);
  }

  public LogContext setKafkaEventType(String kafkaEventType) {
    return put(KAFKA_EVENT_TYPE, kafkaEventType);
  }

  public String getServiceCorrelationId() {
    return this.get("serviceCorrelationId");
  }

  public LogContext setServiceCorrelationId(String serviceCorrelationId) {
    return this.put("serviceCorrelationId", serviceCorrelationId);
  }

  public Long getKafkaEventDate() {
    String epochTimeInString = get(KAFKA_EVENT_DATE);
    if (!ObjectUtils.isEmpty(epochTimeInString)) {
      return Long.parseLong(epochTimeInString);
    }
    return null;
  }

  public LogContext setKafkaEventDate(Object kafkaEventDate) {
    if (kafkaEventDate == null) return this;

    Optional<String> parsedDateString = Optional.empty();
    if (kafkaEventDate instanceof Date) {
      return put(KAFKA_EVENT_DATE, ((Date) kafkaEventDate).getTime() + "");
    } else if (kafkaEventDate instanceof Long) {
      return put(KAFKA_EVENT_DATE, kafkaEventDate + "");
    } else if (kafkaEventDate instanceof String) {
      parsedDateString = Optional.ofNullable((String) kafkaEventDate);
    } else if (kafkaEventDate instanceof byte[]) {
      try {
        parsedDateString = Optional.ofNullable(new String((byte[]) kafkaEventDate));
      } catch (Exception e) {
        logger.error("Unable to parse kafkaEventDate when it is type of byte[]", e);
      }
    }

    if (parsedDateString.isPresent()) {
      return parseDateWithoutLogs(parsedDateString.get());
    }

    return this;
  }

  private LogContext parseDateWithoutLogs(String date) {
    try {
      return put(KAFKA_EVENT_DATE, Long.parseLong(date) + "");
    } catch (Exception e) {
      // Skipping logging the exception
    }

    try {
      Date parsedDate = DateUtil.getDateAndTime(date);
      if (parsedDate != null) {
        return put(KAFKA_EVENT_DATE, parsedDate.getTime() + "");
      }
    } catch (Exception e) {
      // Skipping logging the exception
    }

    try {
      Date parsedDate = DateUtil.getDateUTC(date);
      if (parsedDate != null) {
        return put(KAFKA_EVENT_DATE, parsedDate.getTime() + "");
      }
    } catch (Exception e) {
      // Skipping logging the exception
    }

    return this;
  }

  public String getRequestEndpoint() {
    return get(REQUEST_ENDPOINT);
  }

  public LogContext setRequestEndpoint(String requestEndpoint) {
    return put(REQUEST_ENDPOINT, requestEndpoint);
  }

  public String getResponseCode() {
    return get(RESPONSE_CODE);
  }

  public LogContext setResponseCode(String responseCode) {
    return put(RESPONSE_CODE, responseCode);
  }

  public Map<String, String> getRequestParameters() {
    return requestParameters;
  }

  public LogContext setRequestParameters(Map<String, String> requestParameters) {
    this.requestParameters = requestParameters;
    return this;
  }

  public LogContext setRequestParameter(String parameterName, String parameterValue) {
    if (this.requestParameters == null) {
      this.requestParameters = new HashMap<>();
    }
    this.requestParameters.put(parameterName, parameterValue);
    return this;
  }

  public String getRequestParameter(String parameterName) {
    return getRequestParameter(parameterName, null);
  }

  public String getRequestParameter(String parameterName, String defaultIfNotFound) {
    if (getRequestParameters() != null) {
      return getRequestParameters().getOrDefault(parameterName, defaultIfNotFound);
    }
    return defaultIfNotFound;
  }

  public Map<String, String> getRequestHeaders() {
    return requestHeaders;
  }

  public LogContext setRequestHeaders(Map<String, String> requestHeaders) {
    this.requestHeaders = requestHeaders;
    return this;
  }

  public LogContext setRequestHeader(String headerName, String headerValue) {
    if (this.requestHeaders == null) {
      this.requestHeaders = new HashMap<>();
    }
    this.requestHeaders.put(headerName, headerValue);
    return this;
  }

  public LogContext setKafkaHeader(String headerName, Object headerValue) {
    if (this.kafkaHeaders == null) {
      this.kafkaHeaders = new HashMap<>();
    }
    this.kafkaHeaders.put(headerName, headerValue);
    return this;
  }

  public Map<String, Object> getKafkaHeaders() {
    return this.kafkaHeaders;
  }

  public LogContext setKafkaHeaders(Map<String, Object> kafkaHeaders) {
    this.kafkaHeaders = kafkaHeaders;
    return this;
  }

  public String getRequestHeader(String headerName) {
    return getRequestHeader(headerName, null);
  }

  public String getRequestHeader(String headerName, String defaultIfNotFound) {
    if (getRequestHeaders() != null) {
      return getRequestHeaders().getOrDefault(headerName, defaultIfNotFound);
    }
    return defaultIfNotFound;
  }

  public Object getKafkaHeader(String headerName) {
    return getKafkaHeader(headerName, null);
  }

  public Object getKafkaHeader(String headerName, Object defaultIfNotFound) {
    if (getKafkaHeaders() != null) {
      return getKafkaHeaders().getOrDefault(headerName, defaultIfNotFound);
    }
    return defaultIfNotFound;
  }

  public Map<String, String> toMap() {
    Map<String, String> headers = new HashMap<>();
    if (!CollectionUtils.isEmpty(requestHeaders)) {
      requestHeaders
          .entrySet()
          .forEach(x -> headers.put(REQUEST_HEADERS + x.getKey(), x.getValue()));
    }

    Map<String, String> parameters = new HashMap<>();
    if (!CollectionUtils.isEmpty(requestParameters)) {
      requestParameters
          .entrySet()
          .forEach(x -> parameters.put(REQUEST_PARAMETERS + x.getKey(), x.getValue()));
    }

    Map<String, String> encodedKafkaHeaders = new HashMap<>();
    if (!CollectionUtils.isEmpty(kafkaHeaders)) {
      kafkaHeaders
          .entrySet()
          .forEach(
              x ->
                  encodedKafkaHeaders.put(
                      KAFKA_HEADERS + x.getKey(), KafkaUtil.parseHeaderValue(x.getValue()) + ""));
    }

    if (customFields == null) {
      customFields = new HashMap<>();
    }

    Map<String, String> result = new HashMap<>();
    result.putAll(customFields);
    result.putAll(headers);
    result.putAll(parameters);
    result.putAll(encodedKafkaHeaders);
    return result;
  }
}
