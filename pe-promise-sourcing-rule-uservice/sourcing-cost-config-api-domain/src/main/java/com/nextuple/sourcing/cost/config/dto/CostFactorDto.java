/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.dto;

import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CostFactorDto implements Serializable {
  private static final long serialVersionUID = -1049168714404677675L;

  @Schema(description = "Id of the cost factor", example = "1")
  private Long id;

  @Schema(description = "Organisation Id", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Cost factor to determine the cost", example = "BillWeightUps")
  private String costFactor;

  @Schema(description = "Data type of cost factor", example = "NUMBER")
  private DataTypeEnum dataType;

  @Schema(description = "Formula to determine cost factor", example = "(l*b*h)/5000")
  private String formula;

  @Schema(
      description = "Library which should be used to evaluate derived cost factor",
      example = "MVEL")
  private ExpressionLibraryEnum library;

  @Schema(
      description = "Identifier to determine whether the cost factor is derived or regular",
      example = "DERIVED")
  private CostFactorTypeEnum costFactorType;

  @Schema(description = "Display name of cost factor", example = "SHIPPING_COST")
  private String displayName;

  @Schema(description = "values of cost factor", example = "S,M,L")
  private String values;

  @Schema(description = "Default value of cost factor", example = "S")
  private String defaultValue;

  @Schema(description = "Levels where cost factor is applied", example = "SHIPMENT")
  private LevelAppliedEnum levelApplied;

  @Schema(description = "Unit of measure of cost factor", example = "lbs")
  private String uom;

  @Schema(
      description = "Flag indicating whether the cost factor is bucketed or not",
      example = "true")
  private Boolean isBucketed;

  @Schema(description = "Flag indicating whether to look up rate card or not.", example = "true")
  private Boolean isRateCardLookUpRequired;
}
