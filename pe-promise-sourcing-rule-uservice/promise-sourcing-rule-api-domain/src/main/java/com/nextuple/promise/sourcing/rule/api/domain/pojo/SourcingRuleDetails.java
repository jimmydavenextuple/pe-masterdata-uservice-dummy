/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class SourcingRuleDetails extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -7080108297445570375L;

  @Schema(description = "Unique identifier for the sourcing rule")
  private Long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Name of the sourcing rule", example = "Sourcing rule 1")
  private String sourcingRuleName;

  @Schema(
      description = "Rule containing the value of the required and optional attributes",
      example = "EXPRESS:T2P")
  private String sourcingRule;

  @Schema(
      description = "Define the sequence in which the sourcing rules should be selected",
      example = "1")
  private Integer sequence;

  @Schema(
      description =
          "Comma separated string that will contain the references of the sourcing node groups for a given rule",
      example = "1,2")
  private String nodeGroups;

  @Schema(description = "Reference to the sourcing attributes definition")
  private Long sourcingAttributesDefinitionId;

  @Schema(description = "Reference to the sourcing rule configuration table")
  private Long sourcingRuleId;
}
