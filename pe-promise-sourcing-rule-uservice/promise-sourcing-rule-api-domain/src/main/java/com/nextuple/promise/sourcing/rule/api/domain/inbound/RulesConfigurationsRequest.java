/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
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
public class RulesConfigurationsRequest implements Serializable {

  private static final long serialVersionUID = -6325568934574732674L;

  @NotBlank(message = "orgId can't be empty or null")
  @Schema(description = "Unique identifier for organisation", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "ruleName can't be empty or null")
  @Schema(description = "Unique name for rule", example = "NG1")
  private String ruleName;

  @NotBlank(message = "rule can't be empty or null")
  @Schema(description = "Rule for given rule configuration", example = "EXPRESS:Store")
  private String rule;

  @NotNull(message = "attributeDefinitionId can't be null")
  @Schema(description = "Attribute Definition Id for given rule configuration", example = "1345")
  private Long attributeDefinitionId;

  @NotNull(message = "moduleName can't be empty or null")
  @Schema(description = "Module name for given rule configuration", example = "PROCESSING_TIME")
  private RulesConfigurationModuleNameEnum moduleName;

  @NotNull(message = "scope can't be empty or null")
  @Schema(description = "Scope for given rule configuration", example = "ML_RULE")
  private SourcingAttributesDefinitionScopeEnum scope;
}
