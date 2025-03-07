/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransferScheduleRequest extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -3115486612222454583L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  @NotBlank(message = "OrgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @Schema(description = "Unique identifier of the source node of the transfer.", example = "Node1")
  @NotBlank(message = "Source node id can't be blank")
  @Length(max = 50)
  private String sourceNodeId;

  @Schema(
      description = "Unique identifier of the drop off node of the transfer.",
      example = "Node2")
  @NotBlank(message = "DropOff node id can't be blank")
  @Length(max = 50)
  private String dropoffNodeId;

  @Schema(
      description = "Start date and time of the transfer",
      example = "2024-10-31T01:30:00.000-05:00")
  @DateTimeFormat(iso = ISO.DATE_TIME)
  @NotNull(message = "startTime can't be null")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", lenient = OptBoolean.FALSE)
  private DateTime startTime;
}
