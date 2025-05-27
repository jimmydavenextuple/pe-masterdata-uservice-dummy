/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.outbound;

import com.nextuple.pe.light.promise.pojo.InboundNodeCalendar;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InboundProcessingTimeResponse {

  @Schema(description = "Unique identifier of the node.", example = "NODE01")
  private String nodeId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Describes the opening time of the given node.", example = "09:00")
  private String startWorkingTime;

  @Schema(description = "Describes the last working time of the given node.", example = "18:00")
  private String lastWorkingTime;

  @Schema(
      description =
          "List of calendar day statuses indicating which days are considered working days.")
  private List<InboundNodeCalendar> calendarDays;

  @Schema(
      description =
          "Object containing the Rule Craft Engine (RCE) response, including processing time and other evaluation data.",
      example = "{ \"processingTime\": 10.0 }")
  private Object inbound;
}
