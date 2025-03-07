/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.dto;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CostFactorContiguousBucketDto extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -7284520140726445251L;

  @Schema(description = "Unique identifier of the cost factor bucket type", example = "1")
  private Long id;

  @Schema(description = "Unique identifier of the organization", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Name of the cost factor", example = "BillWeight")
  private String costFactor;

  @Schema(description = "Name of the bucket", example = "M")
  private String notation;

  @Schema(description = "Name of the bucket displayed on UI", example = "Medium")
  private String notationDisplayName;

  @Schema(description = "Lower bound of the bucket range", example = "1.0")
  private Double fromValue;

  @Schema(
      description = "Flag indicating whether the lower bound is inclusive in the range",
      example = "True/False")
  private Boolean isFromValueInclusive;

  @Schema(description = "Upper bound of the bucket range", example = "10.0")
  private Double toValue;

  @Schema(
      description = "Flag indicating whether the uppper bound is inclusive in the range",
      example = "True/False")
  private Boolean isToValueInclusive;
}
