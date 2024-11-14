/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.sourcing.cost.config.pojo.SelectorInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRateCardStatusRequest implements Serializable {
  private static final long serialVersionUID = -6482078974296281018L;

  @Schema(description = "Specifies the cost type.", example = "SHIPPING_COST")
  @NotBlank(message = "Cost type can't be empty")
  private String costType;

  @Schema(description = "Specifies the selector information.")
  private SelectorInfo selector;

  @Schema(
      description = "Specifies a flag to indicate rate card active status.",
      example = "true/false")
  @NotNull(message = "isRateCardActive can't be empty")
  private Boolean isRateCardActive;
}
