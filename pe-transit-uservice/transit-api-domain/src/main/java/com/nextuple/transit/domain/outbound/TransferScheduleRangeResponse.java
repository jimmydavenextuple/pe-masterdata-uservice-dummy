/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.outbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransferScheduleRangeResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 5824767482767795620L;

  @Schema(description = "Unique identifier of the transfer schedule record.", example = "1")
  private Long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier of the source node of the transfer.", example = "Node1")
  private String sourceNodeId;

  @Schema(
      description = "Unique identifier of the drop off node of the transfer.",
      example = "Node2")
  private String dropoffNodeId;

  @Schema(
      description = "Start date and time of the transfer",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", lenient = OptBoolean.FALSE)
  private OffsetDateTime startTime;

  @Schema(
      description = "End date and time of the transfer",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", lenient = OptBoolean.FALSE)
  private OffsetDateTime endTime;

  @Schema(description = "Rule associated with the transfer schedule", example = "DC:KITCHEN")
  @JsonInclude(Include.NON_NULL)
  private String rule;

  @Schema(description = "Rule name associated with the transfer schedule", example = "KitchenRule")
  @JsonInclude(Include.NON_NULL)
  private String ruleName;
}
