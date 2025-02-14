/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents an event that captures information about an exception, including the associated error
 * code and message. This class extends {@link BaseEvent} to inherit common event fields and
 * functionality while adding additional fields specific to exception-related events.
 *
 * <p>The {@link #errorCode} field stores the error code associated with the exception, and the
 * {@link #errorMessage} field contains a descriptive message about the exception.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ExceptionEvent extends BaseEvent {
  /** The error code that identifies the type or cause of the exception. */
  private Integer errorCode;

  /** A message providing details about the exception or error that occurred. */
  private String errorMessage;
}
