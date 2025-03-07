/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.sourcing.cost.config.enums.LabelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class TenantCostTypeResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -8245660785034217576L;

  @Schema(description = "Unique identifier for tenant cost type.", example = "1")
  private Long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE_GR")
  private String orgId;

  @Schema(description = "Specifies the cost type.", example = "SHIPPING_COST")
  String costType;

  @Schema(description = "Specifies the display name of cost type.", example = "Shipping cost")
  String displayName;

  @Schema(
      description = "Identifier to distinguish between cost and revenue type.",
      example = "COST")
  LabelEnum label;
}
