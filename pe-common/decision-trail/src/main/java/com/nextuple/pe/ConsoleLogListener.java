/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.nextuple.pe.config.gson.LocalDateAdapter;
import com.nextuple.pe.config.gson.LocalDateTimeAdapter;
import com.nextuple.pe.config.gson.LocalTimeAdapter;
import com.nextuple.pe.configs.ITenantConfig;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConsoleLogListener {
  @Autowired ITenantConfig iTenantConfig;
  @Autowired GsonExclusionStrategy gsonExclusionStrategy;

  public static final String GENERATED_ID = "generatedId";
  public static final String EVENT_TIME_EPOCH_MILLIS = "eventTimeEpochMillis";
  public static final String EVENT_TIME_STAMP_NANOS = "eventTimeStampNanos";

  @Value("${sourcing-date-format:yyyy-MM-dd'T'HH:mm:ss}")
  public String dateFormat;

  final Logger logger = LogManager.getLogger();

  @Async(value = "listenerThreadPoolTaskExecutor")
  @EventListener
  public void receiveEvent(BaseEvent baseEvent) {
    try {
      Map<String, String> logLevel = iTenantConfig.getLogLevelMap();
      Map<String, Boolean> consoleLogListenEnabled = iTenantConfig.getConsoleLogListenEnabledMap();
      String event = baseEvent.getClass().getSimpleName();
      var defaultLogLevel = "INFO";
      var eventLogLevel = logLevel.getOrDefault(event, defaultLogLevel);

      if (Boolean.TRUE.equals(consoleLogListenEnabled.getOrDefault(event, Boolean.TRUE))
          && logger.isEnabled(Level.getLevel(eventLogLevel))) {
        var eventString =
            new GsonBuilder()
                .serializeNulls()
                .addSerializationExclusionStrategy(gsonExclusionStrategy)
                .serializeSpecialFloatingPointValues()
                .setDateFormat(dateFormat)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(
                    Instant.class,
                    (JsonSerializer<Instant>)
                        (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString()) // Or format as needed
                    )
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create()
                .toJson(baseEvent);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss.SSS a");

        MDC.put(GENERATED_ID, baseEvent.getGeneratedId());
        MDC.put(
            EVENT_TIME_EPOCH_MILLIS,
            String.valueOf(
                formatter.parse(baseEvent.getEventTimestamp()).toInstant().toEpochMilli()));
        MDC.put(EVENT_TIME_STAMP_NANOS, String.valueOf(baseEvent.getEventTimeStampNanos()));

        logger.log(Level.getLevel(eventLogLevel), "{}", eventString);
        MDC.remove(GENERATED_ID);
        MDC.remove(EVENT_TIME_EPOCH_MILLIS);
        MDC.remove(EVENT_TIME_STAMP_NANOS);
      }
    } catch (ParseException e) {
      logger.debug(
          "Error while converting the base event timestamp: {}", baseEvent.getEventTimestamp());
    }
  }
}
