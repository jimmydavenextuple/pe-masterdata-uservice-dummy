/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.domain.outbound;

import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse extends AdditionalAttributes implements Serializable {

  @Schema(description = "Unique identifier of the calendar.", example = "CALENDAR2023")
  private String calendarId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(
      description = "Description of the calendar to be created.",
      example = "Calendar for year 2023")
  private String description;

  @Schema(
      description = "Flag indicating if the node or carrier is working on monday.",
      example = "true")
  private Boolean isMondayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on tuesday.",
      example = "true")
  private Boolean isTuesdayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on wednesday.",
      example = "true")
  private Boolean isWednesdayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on thursday.",
      example = "true")
  private Boolean isThursdayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on friday.",
      example = "true")
  private Boolean isFridayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on saturday.",
      example = "true")
  private Boolean isSaturdayWorking;

  @Schema(
      description = "Flag indicating if the node or carrier is working on sunday.",
      example = "true")
  private Boolean isSundayWorking;

  @Schema(description = "List of the exception days or dates.")
  private List<ExceptionDays> exceptionDays;
}
