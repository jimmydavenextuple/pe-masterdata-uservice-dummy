/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CostItineraryAndFactorsRequest extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 7877441978919884032L;

  @NotBlank(message = "Cost itinerary to determine the cost can't be empty")
  @Schema(description = "Cost itinerary to determine the cost", example = "SHIPPING_COST_UPSLIKE")
  private String costItinerary;

  @NotBlank(message = "Cost factors to determine the cost can't be empty")
  @Schema(description = "Cost factors to determine the cost", example = "BillWeightUps")
  private String costFactors;

  @NotNull(message = "Level where cost factor is applied can't be null")
  @Schema(description = "Level where cost factor is applied", example = "SHIPMENT")
  private LevelAppliedEnum levelApplied;
}
