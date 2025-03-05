/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RulesConfigurationRequest extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -7080108297445570375L;

  @NotBlank(message = "orgId can't be empty")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "sourcingRuleName can't be empty")
  @Schema(description = "Name of sourcing rule.", example = "Sourcing Rule 1")
  private String sourcingRuleName;

  @NotBlank(message = "sourcingRule can't be empty")
  @Schema(
      description = "Rule containing the value of the required and optional attributes",
      example = "EXPRESS:T2P")
  private String sourcingRule;

  @NotNull(message = "sequence can't be null")
  @Schema(
      description = "Define the sequence in which the sourcing rules should be selected",
      example = "1")
  private Integer sequence;

  @NotBlank(message = "nodeGroups can't be empty")
  @Schema(
      description =
          "Unique identifier for the reference of the sourcing node group for a given rule",
      example = "1")
  private String nodeGroups;

  @NotNull(message = "sourcingAttributesDefinitionId can't be null")
  @Schema(description = "Reference to the sourcing attributes definition")
  private Long sourcingAttributesDefinitionId;
}
