/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.dto.costtyypesdto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CostItineraryCostFactorsDto implements Serializable {
  private static final long serialVersionUID = -9073989237404423313L;

  @Schema(
      description = "Cost itinerary associated with the cost type",
      example = "SHIPPING_COST_ITN")
  private String costItinerary;

  @Schema(description = "List of descriptions of filter cost factors")
  private List<CostFactorDescriptionDto> costFactors;

  @Schema(description = "Description of row cost factor")
  private CostFactorDescriptionDto row;

  @Schema(description = "Description of column cost factor")
  private CostFactorDescriptionDto column;
}
