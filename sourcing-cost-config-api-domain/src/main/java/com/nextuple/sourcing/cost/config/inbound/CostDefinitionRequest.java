/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostDefinitionRequest implements Serializable {
  private static final long serialVersionUID = -8310970153369017726L;

  @NotBlank(message = "Cost type cannot be blank")
  @Schema(description = "Cost type", example = "SHIPPING_COST")
  private String costType;

  private SelectorCostFactorInfoDto selector;

  private List<FilterCostFactorInfoDto> filters;

  @Schema(description = "Row cost factor", example = "billWeightUps")
  private String row;

  @Schema(description = "Column cost factor", example = "zones")
  private String column;
}
