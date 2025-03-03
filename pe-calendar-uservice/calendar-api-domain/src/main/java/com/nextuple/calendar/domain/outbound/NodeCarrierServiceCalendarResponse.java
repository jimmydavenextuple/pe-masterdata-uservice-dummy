/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NodeCarrierServiceCalendarResponse extends AdditionalAttributes {

  @Schema(description = "Unique identifier of the calendar.", example = "CALENDAR2023")
  private String calendarId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier of the node.", example = "NODE01")
  private String nodeId;

  @Schema(description = "Unique identifier of the carrier service.", example = "ALL-SDND")
  private String carrierServiceId;

  @Schema(description = "Effective date of the calendar.", example = "2023-01-01")
  private String effectiveDate;

  @Schema(
      description = "Description of node carrier service calendar.",
      example = "Calendar for NODE01/ALL-SDND")
  private String description;
}
