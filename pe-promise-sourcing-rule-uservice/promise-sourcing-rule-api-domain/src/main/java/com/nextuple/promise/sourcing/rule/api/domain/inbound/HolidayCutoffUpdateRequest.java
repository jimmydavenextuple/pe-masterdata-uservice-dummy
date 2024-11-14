/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HolidayCutoffUpdateRequest implements Serializable {
  @Serial private static final long serialVersionUID = -3610659332595241996L;

  @Schema(description = "Description of the holiday cutoff.", example = "Christmas Holiday Cutoff")
  private String holidayCutoffDescription;

  @Schema(
      description = "Status of the holiday cutoff.",
      allowableValues = {"ACTIVE", "INACTIVE"})
  private HolidayCutoffStatus status;

  @Schema(
      description = "Datetime from when holiday cutoff should be applied in UTC.",
      example = "2024-12-01T12:00:00Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date startDate;

  @Schema(
      description = "Datetime until when holiday override should be applied in UTC.",
      example = "2024-12-15T23:59:00Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date holidayCutoffDate;

  @Schema(
      description = "Datetime to override in case of holiday cutoff is applied in UTC.",
      example = "2024-12-02T12:00:00Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date holidayDeliveryDate;

  @Schema(
      description =
          "Number of days that forms sub bucket within a start date and holidayCutOffDate.",
      example = "3.0")
  @Min(value = 0, message = "preCutoffDays must be positive")
  @Max(value = 30, message = "preCutoffDays must be less than 30")
  private Double preCutoffDays;

  @Schema(description = "Number of cool down days post holiday delivery date.", example = "3.0")
  @Min(value = 0, message = "deliveryCoolDownDays  must be positive")
  @Max(value = 30, message = "deliveryCoolDownDays must be less than 30")
  private Double deliveryCoolDownDays;

  @Schema(
      description = "Type of pre-cutoff days.",
      allowableValues = {"BUSINESS_DAYS", "CALENDAR_DAYS"})
  private HolidayCutoffDaysType preCutoffDaysType;

  @Schema(
      description = "Type of delivery cooldown days.",
      allowableValues = {"BUSINESS_DAYS", "CALENDAR_DAYS"})
  private HolidayCutoffDaysType deliveryCoolDownDaysType;
}
