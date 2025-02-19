/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents the base structure for an event, containing common fields for tracking and identifying
 * the event within the promising engine.
 *
 * <p>This class includes essential metadata such as organization ID, reference ID, timestamp
 * information, event name/type, and generated ID. It also provides functionality for setting and
 * maintaining event-specific timestamps in microseconds.
 *
 * <p>The fields in this class are populated through constructors, with some fields being
 * automatically set via Lombok annotations for getter/setter generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseEvent {
  /** The unique identifier of the organization associated with the event. */
  private String orgId;

  /** A unique reference identifier for the event. */
  private String referenceId;

  /** The line identifier, which can be used for further granularity in the event context. */
  private String lineId;

  /** The service option associated with the event. */
  private String serviceOption;

  /** The timestamp when the event occurred, in a human-readable format. */
  private String eventTimestamp;

  /** A description providing further details about the event. */
  private String description;

  /** The name of the event. */
  private String eventName;

  /** The type of the event, which may categorize it in the system. */
  private String eventType;

  /** A unique ID generated for the event, typically used for tracking purposes. */
  private String generatedId;

  /**
   * @hidden Not intended for external use
   */
  @Setter(AccessLevel.NONE)
  private String eventTimeStampNanos;

  /**
   * @hidden not intended for external use
   */
  public void setEventTimeStampNanos() {
    Instant i = Instant.now();
    long microsBack = ChronoUnit.MICROS.between(Instant.EPOCH, i);
    this.eventTimeStampNanos = String.valueOf(microsBack);
  }
}
