/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptimizationRuleUpdationUIRequest implements Serializable {
  private static final long serialVersionUID = 6927165320826407072L;

  @Schema(description = "Name of the optimization rule", example = "Optimization Rule-2")
  @NotBlank(message = "optimizationRuleName can't be empty")
  private String optimizationRuleName;

  @Schema(description = "Details of the optimization rule", example = "SPLIT,SPEED,PRIORITY")
  @NotBlank(message = "strategy can't be empty")
  private String strategy;

  @Schema(description = "List of the constraints")
  private List<AllConstraintUIDto> constraints;

  @Valid
  @NotEmpty(message = "requiredAttributes can't be empty")
  @Schema(description = "List of required attributes for the optimization rule")
  private List<AttributeDetailsUIRequest> requiredAttributes;
}
