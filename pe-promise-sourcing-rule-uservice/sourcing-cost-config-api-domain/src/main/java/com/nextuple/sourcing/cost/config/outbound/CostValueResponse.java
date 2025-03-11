/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.outbound;

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
public class CostValueResponse extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -5004205627459887859L;

  @Schema(description = "Unique identifier for cost attribute mapping", example = "1")
  private long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE_GR")
  private String orgId;

  @Schema(
      description = "Cost itinerary to determine the cost.",
      example = "SHIPPING_COST_FEDEXLIKE")
  private String costItinerary;

  @Schema(
      description = "Cost factors values combination",
      example = "FEDEX_GROUND|NON_HOLIDAYS|Z1|XL")
  private String costFactorCombinationKey;

  @Schema(description = "Cost for itinerary", example = "0.63")
  private Double costValue;

  @Schema(
      description = "Cost factor combination values of previous slab",
      example = "UPS_GROUND|Z2|L")
  private String prevSlabValue;
}
