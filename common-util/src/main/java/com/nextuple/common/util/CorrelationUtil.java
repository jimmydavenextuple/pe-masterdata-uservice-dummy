package com.nextuple.common.util;

import com.nextuple.controltower.common.logging.CurrentThreadContext;
import com.nextuple.controltower.common.util.KafkaUtil;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static com.nextuple.controltower.common.logging.LogContext.CORRELATION_ID;

/**
 * Correlation Utility to generate, extract, validate and modify correlation id(s)
 *
 * <p>It manages two correlation id(s). One as a request id which will be same across multiple
 * micro-services and another one will be generated at each micro-service that will be unique within
 * particular micro-service
 */
public class CorrelationUtil {

  private CorrelationUtil() {
    // Everyone should be using static methods only
  }

  /**
   * Generate a random correlation id
   *
   * <p>It uses {@link UUID} to generate new id
   *
   * @return It returns newly generated correlation id
   */
  public static String generateId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Extract correlation id from http request's header
   *
   * @param httpServletRequest Http Request object from which correlation id will be extracted
   *     {@link HttpServletRequest}
   * @return It returns correlation id if exists else it will return null string. If multiple
   *     correlation ids are passed in request headers then it will return first one as correlation
   *     id
   */
  public static String extractCorrelationId(HttpServletRequest httpServletRequest) {
    return httpServletRequest.getHeader(CORRELATION_ID);
  }

  /**
   * Extract correlation if from Kafka Headers
   *
   * @param kafkaMessageHeaders Kafka Headers object
   * @return It returns correlation id if exists else it will return null string. If multiple
   *     correlation ids are passed in kafka headers then it will return first one as correlation id
   */
  public static String extractCorrelationId(KafkaMessageHeaders kafkaMessageHeaders) {
    if (kafkaMessageHeaders.containsKey(CORRELATION_ID)) {
      return KafkaUtil.parseHeaderValue(kafkaMessageHeaders.get(CORRELATION_ID)) + "";
    }
    return null;
  }

  /**
   * Checks whether correlation id is already set for current context
   *
   * @return It returns true if current context already has correlation id else it returns false
   */
  public static boolean isCorrelationIdSet() {
    return !ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getCorrelationId());
  }

  /**
   * Checks whether service correlation id is already set for current context
   *
   * @return It returns true if current context already has service correlation id else it returns
   *     false
   */
  public static boolean isServiceCorrelationIdSet() {
    return !ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getServiceCorrelationId());
  }

  /**
   * Get a correlation id from current context
   *
   * @return It returns correlation id from current context if current context has it else it
   *     returns null
   */
  public static String getCurrentCorrelationId() {
    return CurrentThreadContext.getLogContext().getCorrelationId();
  }

  /**
   * Get a service correlation id from current context
   *
   * @return It returns service correlation id from current context if current context has it else
   *     it returns null
   */
  public static String getCurrentServiceCorrelationId() {
    return CurrentThreadContext.getLogContext().getServiceCorrelationId();
  }

  /**
   * Sets correlation id to given id
   *
   * @param correlationId New correlation id
   */
  public static void setCorrelationId(String correlationId) {
    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
  }

  /**
   * Sets service correlation id to given id
   *
   * @param serviceCorrelationId New service correlation id
   */
  public static void setServiceCorrelationId(String serviceCorrelationId) {
    CurrentThreadContext.getLogContext().setServiceCorrelationId(serviceCorrelationId);
  }

  /** Sets new correlation id, it will use {@link #generateId()} to generate new id */
  public static void setNewCorrelationId() {
    setCorrelationId(generateId());
  }

  /** Sets new service correlation id, it will use {@link #generateId()} to generate new id */
  public static void setNewServiceCorrelationId() {
    setServiceCorrelationId(generateId());
  }

  /**
   * Set correlation id as well as service id in context If correlation id or service id is already
   * set in context then it won't change that. If passed correlation id is empty or null then method
   * will set correlation id randomly. Service id will be set newly generated id
   *
   * @param correlationId Correlation Id to be set
   */
  public static void processCorrelationId(String correlationId) {
    // Set correlation id
    if (!CorrelationUtil.isCorrelationIdSet()) {
      // If passed correlation id is null or empty we will set new
      if (ObjectUtils.isEmpty(correlationId)) {
        CorrelationUtil.setNewCorrelationId();
      } else {
        CorrelationUtil.setCorrelationId(correlationId);
      }
    } else if (!CorrelationUtil.getCurrentCorrelationId().equalsIgnoreCase(correlationId)
        && !ObjectUtils.isEmpty(correlationId)) {
      CorrelationUtil.setCorrelationId(correlationId);
    }

    // Set service correlation id
    CorrelationUtil.setNewServiceCorrelationId();
  }

  /** Generate and set new correlation id as well as service id */
  public static void processCorrelationId() {
    processCorrelationId(null);
  }
}
