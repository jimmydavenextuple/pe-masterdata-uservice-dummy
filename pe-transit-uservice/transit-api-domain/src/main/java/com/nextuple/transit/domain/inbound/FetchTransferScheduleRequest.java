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
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.joda.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FetchTransferScheduleRequest extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 7032221653986642216L;

  @Schema(
      description = "List of Unique identifier of the source node of the transfer.",
      example = "['Node1']")
  private List<String> sourceNodeIds;

  @Schema(
      description = "List of Unique identifier of the drop off node of the transfer.",
      example = "['Node2']")
  private List<String> dropoffNodeIds;

  @Schema(description = "Start/Pickup date of the transfer", example = "2024-10-31")
  @JsonFormat(pattern = "yyyy-MM-dd", lenient = OptBoolean.FALSE)
  private LocalDate startDate;

  @Schema(description = "End/Drop off date of the transfer", example = "2024-10-31")
  @JsonFormat(pattern = "yyyy-MM-dd", lenient = OptBoolean.FALSE)
  private LocalDate endDate;
}
