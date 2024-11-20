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
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OptimizationStrategyUIRequest implements Serializable {
  private static final long serialVersionUID = 16444368329230463L;

  @NotBlank(message = "orgId can't be empty")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "sourcingAttributesDefinition can't be empty")
  @Schema(description = "Reference to the sourcing attributes definition", example = "1")
  private String sourcingAttributesDefinitionId;

  @NotBlank(message = "optimizationRuleName can't be empty")
  @Schema(description = "Name of the optimization rule.", example = "SPLIT_V1")
  private String optimizationRuleName;

  @Valid
  @NotEmpty(message = "requiredAttributes can't be empty")
  @Schema(description = "List of required attributes for a given sourcing attribute definition ID.")
  private List<AttributeDetailsUIRequest> requiredAttributes;

  @Schema(description = "List of required attributes for a given sourcing attribute definition ID.")
  private List<AttributeDetailsUIRequest> optionalAttributes;

  @NotBlank(message = "strategy can't be empty")
  @Schema(description = "Details of the optimization strategy", example = "SPEED,PRIORITY")
  private String strategy;

  @Schema(description = "List of constraint details to be added")
  private List<AllConstraintUIDto> constraints;
}
