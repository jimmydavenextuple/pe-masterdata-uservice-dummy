/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import com.nextuple.pe.configs.ITenantConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Class responsible for handling the publishing of decision trail events within the application.
 * This service provides methods for publishing various types of events to ensure that decision
 * trail workflows are executed properly.
 *
 * <p>This service may include features such as:
 *
 * <ul>
 *   <li>Publishing events based on dynamic conditions.
 *   <li>Customizable event handling logic.
 *   <li>Managing event-related metadata such as logging and timestamps.
 * </ul>
 *
 * @see ApplicationEventPublisher
 */
@Service
public class PublishEventService {
  /**
   * @hidden
   */
  public static final String ORG_ID = "orgId";

  /**
   * @hidden
   */
  public static final String REFERENCE_ID = "referenceId";

  /**
   * @hidden
   */
  public static final String SERVICE_OPTION = "serviceOption";

  /**
   * @hidden
   */
  public static final String EVENT_TYPE = "eventType";

  /**
   * @hidden
   */
  public static final String LINE_ID = "lineId";

  /**
   * @hidden
   */
  public static final String ORDER_ERROR_DESCRIPTION = "Error occurred while processing order";

  /**
   * @hidden
   */
  public static final String PAGE_NAME = "pageName";

  @Autowired ApplicationEventPublisher applicationEventPublisher;
  @Autowired ITenantConfig iTenantConfig;
  final Logger logger = LogManager.getLogger();

  /**
   * Publishes an event to the application context if event publishing is enabled.
   *
   * <p>This method checks whether the event publishing for the given event type is enabled based on
   * the configuration. If enabled, the event is published using the {@link
   * ApplicationEventPublisher}. The configuration is retrieved from the tenant configuration
   * service.
   *
   * @param event The event to be published. This object will be published to the Spring application
   *     context if the publishing is enabled for its type.
   * @throws IllegalArgumentException if the provided event is {@code null}.
   */
  public void publishEvent(Object event) {
    Map<String, Boolean> publishEnabled = iTenantConfig.getPublishEnabledMap();
    if (Boolean.TRUE.equals(
        publishEnabled.getOrDefault(event.getClass().getSimpleName(), Boolean.TRUE)))
      applicationEventPublisher.publishEvent(event);
  }

  /**
   * Sets common fields for the given event and publishes it if conditions are met.
   *
   * <p>This method performs the following actions:
   *
   * <ul>
   *   <li>Retrieves the log level and publishing configuration for the event.
   *   <li>Checks if event publishing is enabled and if the logger is enabled at the configured log
   *       level.
   *   <li>Verifies whether the event's page is allowed for publishing, based on the configuration.
   *   <li>Sets common fields in the event, including organization ID, reference ID, line ID,
   *       timestamp, service option, event name, event type, and a generated unique ID.
   *   <li>Publishes the event using {@link #publishEvent(Object)} if the conditions are met.
   * </ul>
   *
   * @param event The event object to be processed and published. The event will have common fields
   *     set and will be published if publishing is enabled and the event meets the specified
   *     conditions.
   */
  public void setCommonFieldsAndPublishEvent(BaseEvent event) {
    Map<String, String> logLevel = iTenantConfig.getLogLevelMap();
    Map<String, Boolean> publishEnabledMap = iTenantConfig.getPublishEnabledMap();
    String eventName = event.getClass().getSimpleName();

    var defaultLogLevel = "INFO";
    var eventLogLevel = logLevel.getOrDefault(eventName, defaultLogLevel);

    if (Boolean.FALSE.equals(publishEnabledMap.getOrDefault(eventName, Boolean.TRUE))
        || !logger.isEnabled(Level.getLevel(eventLogLevel))) {
      return;
    }
    Set<String> allowedPages = iTenantConfig.getAllowedPagesListForPublishingEvent();
    String pageName = MDC.get(PAGE_NAME);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss.SSS a");

    if (Objects.nonNull(pageName) && allowedPages.stream().noneMatch(pageName::equalsIgnoreCase))
      return;
    event.setOrgId(MDC.get(ORG_ID));
    event.setReferenceId(MDC.get(REFERENCE_ID));
    if (Objects.isNull(event.getLineId())) event.setLineId(MDC.get(LINE_ID));
    event.setEventTimestamp(formatter.format(new Date()));
    event.setEventTimeStampNanos();
    event.setServiceOption(MDC.get(SERVICE_OPTION));
    String eventNameMDC =
        Objects.nonNull(MDC.get("eventName"))
            ? MDC.get("eventName")
            : event.getClass().getSimpleName();
    event.setEventName(eventNameMDC);
    event.setEventType(MDC.get(EVENT_TYPE));
    event.setGeneratedId(
        MDC.get(ORG_ID)
            + "_"
            + MDC.get(REFERENCE_ID)
            + "_"
            + System.currentTimeMillis()
            + "_"
            + "%05d".formatted(Thread.currentThread().threadId()));
    publishEvent(event);
  }

  /**
   * Creates a generic exception event with the specified error code and message, sets common fields
   * for the event, and publishes it.
   *
   * <p>This method builds an {@link ExceptionEvent} with the provided error code and error message,
   * sets the common fields (such as timestamps and metadata), and then publishes the event using
   * {@link #setCommonFieldsAndPublishEvent(BaseEvent)}.
   *
   * @param errorCode The error code associated with the exception event.
   * @param errorMessage A detailed message explaining the error.
   */
  public void publishGenericExceptionEvent(Integer errorCode, String errorMessage) {
    var genericExceptionEvent =
        ExceptionEvent.builder()
            .description(ORDER_ERROR_DESCRIPTION)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();
    setCommonFieldsAndPublishEvent(genericExceptionEvent);
  }

  public void publishGenericExceptionEventWithLineId(
      Integer errorCode, String errorMessage, String lineId) {
    var genericExceptionEvent =
        ExceptionEvent.builder()
            .description(ORDER_ERROR_DESCRIPTION)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .lineId(lineId)
            .build();
    setCommonFieldsAndPublishEvent(genericExceptionEvent);
  }
}
