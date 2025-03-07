/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOptimizationAndCostTypesMappingRequest extends AdditionalAttributes
    implements Serializable {
  @Serial private static final long serialVersionUID = -3004629087120852960L;

  @NotBlank(message = "Unique identifier for organisation can't be empty.")
  @Schema(description = "Unique identifier for organisation", example = "NEXTUPLE_GR")
  private String orgId;

  @NotBlank(message = "Name of optimization strategy can't be empty.")
  @Schema(description = "Name of optimization strategy", example = "COST")
  private String optimizationStrategy;

  @NotBlank(
      message =
          "Comma separated list of cost types to be considered for given optimization strategy can't be empty.")
  @Schema(
      description =
          "Comma separated list of cost types to be considered for given optimization strategy.",
      example = "BUYING_COST,SHIPPING_COST")
  private String costTypes;

  @NotBlank(message = "Description for a given optimization strategy can't be empty.")
  @Schema(
      description = "Description for a given optimization strategy.",
      example = "(salesRevenue + shipRevenue) - (shippingCost + nodeProcessingCost + buyingCost)")
  private String description;

  @Schema(
      description = "Java class that needs to be invoked for calculation.",
      example = "com.nextuple.promise.sourcing.impl.ProfitCalculationImpl")
  private String javaClassName;
}
