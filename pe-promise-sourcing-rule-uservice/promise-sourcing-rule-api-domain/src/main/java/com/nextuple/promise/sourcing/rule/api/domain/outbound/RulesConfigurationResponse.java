/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RulesConfigurationResponse extends AdditionalAttributes implements Serializable {
  public static final long serialVersionUID = -2023059190396777563L;

  @Schema(description = "Unique identifier for the organisation.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Rule name.", example = "Rule-1")
  private String ruleName;

  @Schema(description = "Rule", example = "EXPRESS:Store")
  private String rule;

  @Schema(description = "Reference to the attributes definition", example = "1345")
  private Long attributeDefinitionId;

  @Schema(description = "Module name", example = "PROCESSING_TIME")
  private RulesConfigurationModuleNameEnum moduleName;

  @Schema(description = "Scope with respect to the rule", example = "ML_RULE")
  private SourcingAttributesDefinitionScopeEnum scope;
}
