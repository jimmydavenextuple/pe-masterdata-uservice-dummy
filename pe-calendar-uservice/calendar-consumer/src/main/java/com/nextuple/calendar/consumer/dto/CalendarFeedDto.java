/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.dto;

import static com.nextuple.calendar.consumer.constants.CalendarConstants.*;

import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalendarFeedDto extends CommonMasterDataFieldsDto implements Serializable {

  private static final long serialVersionUID = 5178591180549006209L;

  @Schema(description = CALENDAR_ID, maxLength = 40, example = CALENDAR_ID_EXAMPLE)
  private String calendarId;

  @Schema(description = DESCRIPTION, example = DESCRIPTION_EXAMPLE)
  private String description;

  @Schema(description = IS_FRIDAY_WORKING, example = "true")
  private Boolean isFridayWorking;

  @Schema(description = IS_MONDAY_WORKING, example = "true")
  private Boolean isMondayWorking;

  @Schema(description = IS_TUESDAY_WORKING, example = "true")
  private Boolean isTuesdayWorking;

  @Schema(description = IS_THURSDAY_WORKING, example = "true")
  private Boolean isThursdayWorking;

  @Schema(description = IS_SATURDAY_WORKING, example = "true")
  private Boolean isSaturdayWorking;

  @Schema(description = IS_SUNDAY_WORKING, example = "true")
  private Boolean isSundayWorking;

  @Schema(description = IS_WEDNESDAY_WORKING, example = "true")
  private Boolean isWednesdayWorking;

  @Schema(description = EXCEPTION_DAYS)
  private List<@Valid ExceptionDays> exceptionDays;
}
