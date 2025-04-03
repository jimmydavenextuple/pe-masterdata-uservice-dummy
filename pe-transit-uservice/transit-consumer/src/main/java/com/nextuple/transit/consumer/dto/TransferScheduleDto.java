/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.transit.consumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransferScheduleDto extends CommonMasterDataFieldsDto implements Serializable {

  @Serial private static final long serialVersionUID = 7078170315367233060L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE_GR")
  private String orgId;

  @Schema(description = "Unique identifier of the source node of the transfer.", example = "Node1")
  private String sourceNodeId;

  @Schema(
      description = "Unique identifier of the drop off node of the transfer.",
      example = "Node2")
  private String dropoffNodeId;

  @Schema(
      description = "Start date and time of the transfer.",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", lenient = OptBoolean.FALSE)
  private DateTime startTime;

  @Schema(
      description = "End date and time of the transfer.",
      example = "2024-10-31T01:30:00.000-05:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", lenient = OptBoolean.FALSE)
  private DateTime endTime;

  @Schema(
      description =
          "Rule containing comma-separated values, associated with the transfer schedule.",
      example = "DC:KITCHEN")
  private String rule;

  @Schema(
      description = "Name of the rule associated with the transfer schedule.",
      example = "KitchenItemRule")
  private String ruleName;

  @Schema(description = "The flag to apply validation on the transfer schedule.", example = "true")
  private Boolean applyValidation;
}
