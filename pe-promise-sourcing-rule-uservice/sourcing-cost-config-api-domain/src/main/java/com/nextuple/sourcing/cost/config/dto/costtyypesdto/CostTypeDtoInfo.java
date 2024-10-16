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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CostTypeDtoInfo extends CostFactorFieldsInfo implements Serializable {
  private static final long serialVersionUID = -7573439237404423873L;

  @Schema(description = "Specifies the cost type name.", example = "SHIPPING_COST")
  private String costType;

  @Schema(description = "Specifies the display name of the cost type", example = "Shipping cost")
  private String displayName;

  @Schema(description = "Specifies the selector for the cost factor", example = "carrierServiceId")
  private String selectorCf;

  @Schema(
      description = "Specifies the display name of the selector for the cost factor.",
      example = "Carrier Service ID")
  private String selectorCfDisplayName;

  @Schema(description = "Specifies the selector cost factor information.")
  private List<SelectorCfUIInfo> selectorCfInfo;

  @Schema(description = "Specifies the optimization strategies which use the cost type.")
  private String optimizationStrategies;
}
