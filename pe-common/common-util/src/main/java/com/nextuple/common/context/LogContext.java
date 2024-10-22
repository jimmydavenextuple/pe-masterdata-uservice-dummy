package com.nextuple.common.context;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.util.CommonUtil;
import com.nextuple.common.util.DateUtil;
import com.nextuple.common.util.KafkaUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
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
  private static final String AUTHORIZATION = "authorization";

  public static final String USER_ROLE = "userRole";
  public static final String USER_LOCALE = "userLocale";
  public static final String TENANT_ID = "tenantId";
  private static final String SECONDARY_TENANT_IDS = "secondary_tenantIds";
  public static final String API_KEY = "apiKey";
  public static final String SERVICE_CORRELATION_ID = "serviceCorrelationId";
  public static final String TENANT_DNS_NAME = "tenantDNSName";
  public static final List<String> IGNORE_HEADERS =
      List.of("api_key", "x-api-key", AUTHORIZATION, API_KEY);

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

  public String getUsername() {
    return get(HOST_NAME);
  }

  public LogContext setUsername(String hostName) {
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
    return this.get(SERVICE_CORRELATION_ID);
  }

  public LogContext setServiceCorrelationId(String serviceCorrelationId) {
    return this.put(SERVICE_CORRELATION_ID, serviceCorrelationId);
  }

  public String getAuthorizationHeader() {
    return get(AUTHORIZATION);
  }

  public LogContext setAuthorizationHeader(String authorizationHeader) {
    return put(AUTHORIZATION, authorizationHeader);
  }

  public String getTenantId() {
    return get(TENANT_ID);
  }

  public LogContext setTenantId(String tenantId) {
    return put(TENANT_ID, tenantId);
  }

  public String getApiKey() {
    return get(API_KEY);
  }

  public LogContext setApiKey(String apiKey) {
    return put(API_KEY, apiKey);
  }

  public String getUserRole() {
    return get(USER_ROLE);
  }

  public LogContext setUserRole(String userRole) {
    return put(USER_ROLE, userRole);
  }

  public String getUserLocale() {
    return !ObjectUtils.isEmpty(get(USER_LOCALE)) ? get(USER_LOCALE) : "en-US";
  }

  public LogContext setUserLocale(String userLocale) {
    return put(USER_LOCALE, userLocale);
  }

  public Long getKafkaEventDate() {
    var epochTimeInString = get(KAFKA_EVENT_DATE);
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
      var parsedDate = DateUtil.getDateAndTime(date);
      if (parsedDate != null) {
        return put(KAFKA_EVENT_DATE, parsedDate.getTime() + "");
      }
    } catch (Exception e) {
      // Skipping logging the exception
    }

    try {
      var parsedDate = DateUtil.getDateUTC(date);
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

  public LogContext setTenantDNSName(String tenantDNSName) {
    put(TENANT_DNS_NAME, tenantDNSName);
    return this;
  }

  public String getTenantDnsName() {
    return get(TENANT_DNS_NAME);
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

  public LogContext initFromRequest(HttpServletRequest httpServletRequest) {
    Enumeration<String> headersIter = httpServletRequest.getHeaderNames();
    while (headersIter.hasMoreElements()) {
      String header = headersIter.nextElement();
      setRequestHeader(header, httpServletRequest.getHeader(header));
      if (header.equalsIgnoreCase("host")) {
        CommonUtil.parseDNSName(httpServletRequest.getHeader(header))
            .ifPresent(this::setTenantDNSName);
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_USER)) {
        setUsername(httpServletRequest.getHeader(header));
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_ROLE)) {
        setUserRole(httpServletRequest.getHeader(CommonConstants.HEADER_ROLE));
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_USER_LOCALE)) {
        setUserLocale(httpServletRequest.getHeader(CommonConstants.HEADER_USER_LOCALE));
      }
    }

    setRequestMethod(httpServletRequest.getMethod());
    setRequestEndpoint(httpServletRequest.getRequestURI());

    Enumeration<String> parametersIter = httpServletRequest.getParameterNames();
    while (parametersIter.hasMoreElements()) {
      String parameter = parametersIter.nextElement();
      setRequestParameter(parameter, httpServletRequest.getParameter(parameter));
    }

    return setTenantId(httpServletRequest.getHeader(CommonConstants.HEADER_TENANT_ID))
        .setApiKey(httpServletRequest.getHeader(CommonConstants.HEADER_API_KEY));
  }

  public LogContext initFromKafka(KafkaMessageHeaders kafkaMessageHeaders) {
    for (Map.Entry<String, Object> headerEntry : kafkaMessageHeaders.entrySet()) {
      String header = headerEntry.getKey();
      setKafkaHeader(header, headerEntry.getValue());
      if (header.equalsIgnoreCase("host")) {
        CommonUtil.parseDNSName(KafkaUtil.parseHeaderValue(headerEntry.getValue()) + "")
            .ifPresent(this::setTenantDNSName);
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_USER)) {
        setUsername(KafkaUtil.parseHeaderValue(headerEntry.getValue()) + "");
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_ROLE)) {
        setUserRole(KafkaUtil.parseHeaderValue(headerEntry.getValue()) + "");
      } else if (header.equalsIgnoreCase(CommonConstants.HEADER_USER_LOCALE)) {
        setUserLocale(KafkaUtil.parseHeaderValue(headerEntry.getValue()) + "");
      }
    }

    if (kafkaMessageHeaders.getId() != null)
      setKafkaMessageID(kafkaMessageHeaders.getId().toString());

    if (kafkaMessageHeaders.containsKey(KafkaHeaders.RECEIVED_TOPIC))
      setKafkaTopic(
          KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(KafkaHeaders.RECEIVED_TOPIC)) + "");

    if (kafkaMessageHeaders.containsKey(CommonConstants.HEADER_TENANT_ID))
      setTenantId(
          KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(CommonConstants.HEADER_TENANT_ID))
              + "");

    if (kafkaMessageHeaders.containsKey(CommonConstants.HEADER_API_KEY))
      setApiKey(
          KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(CommonConstants.HEADER_API_KEY)) + "");

    if (kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_NAME))
      setKafkaEventName(
          KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_NAME))
              + "");

    if (kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_TYPE))
      setKafkaEventType(
          KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_TYPE))
              + "");

    if (kafkaMessageHeaders.containsKey(CommonConstants.HEADER_EVENT_DATE))
      setKafkaEventDate(kafkaMessageHeaders.get(CommonConstants.HEADER_EVENT_DATE));

    return this;
  }

  public Map<String, String> toMap() {
    Map<String, String> headers = new HashMap<>();
    if (!CollectionUtils.isEmpty(requestHeaders)) {
      requestHeaders
          .entrySet()
          .forEach(
              x -> {
                if (!IGNORE_HEADERS.contains(x.getKey().toLowerCase())) {
                  headers.put(REQUEST_HEADERS + x.getKey(), x.getValue());
                }
              });
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
    Map<String, String> updatedCustomFields = new HashMap<>();
    for (Map.Entry<String, String> entry : customFields.entrySet()) {
      if (!entry.getKey().equals(API_KEY)) {
        updatedCustomFields.put(entry.getKey(), entry.getValue());
      }
    }
    result.putAll(updatedCustomFields);
    result.putAll(headers);
    result.putAll(parameters);
    result.putAll(encodedKafkaHeaders);
    return result;
  }
}
