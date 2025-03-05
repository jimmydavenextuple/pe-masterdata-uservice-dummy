/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HolidayCutoffInfo extends AdditionalAttributes implements Serializable {
  @Serial private static final long serialVersionUID = -1204895093300884497L;

  @Schema(description = "Unique identifier for organisation.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Name of the holiday cutoff.", example = "Christmas")
  private String holidayCutoffName;

  @Schema(description = "Description of the holiday cutoff.", example = "Christmas Holiday Cutoff")
  private String holidayCutoffDescription;

  @Schema(description = "Colon separated values of attributes.", example = "EXPRESS:T2P")
  private String holidayCutoffRule;

  @Schema(description = "Reference to the sourcing attributes definition.", example = "4")
  private Long sourcingAttributesDefinitionId;

  @Schema(
      description = "Status of the holiday cutoff.",
      allowableValues = {"ACTIVE", "INACTIVE"})
  private HolidayCutoffStatus status;

  @Schema(
      description = "The holiday cutoff will be applied from a specified timestamp in UTC.",
      example = "2024-12-01T12:00:00Z")
  private Date startDate;

  @Schema(
      description = "The holiday cutoff will be applied until a specified timestamp in UTC.",
      example = "2024-12-15T12:00:00Z")
  private Date holidayCutoffDate;

  @Schema(
      description = "Holiday delivery date in UTC that overrides the calculated delivery date.",
      example = "2024-12-25T12:00:00Z")
  private Date holidayDeliveryDate;

  @Schema(
      description =
          "Number of days that forms sub-bucket within start date and holiday cutoff date.",
      example = "5.0")
  private Double preCutoffDays;

  @Schema(description = "Number of cool down days post holiday delivery date.", example = "3.0")
  private Double deliveryCoolDownDays;

  @Schema(
      description = "Type of pre-cutoff days.",
      allowableValues = {"BUSINESS_DAYS", "CALENDAR_DAYS"})
  private HolidayCutoffDaysType preCutoffDaysType;

  @Schema(
      description = "Type of delivery cooldown days.",
      allowableValues = {"BUSINESS_DAYS", "CALENDAR_DAYS"})
  private HolidayCutoffDaysType deliveryCoolDownDaysType;

  @JsonIgnore
  public boolean isInActive() {
    return HolidayCutoffStatus.INACTIVE.equals(status);
  }
}
