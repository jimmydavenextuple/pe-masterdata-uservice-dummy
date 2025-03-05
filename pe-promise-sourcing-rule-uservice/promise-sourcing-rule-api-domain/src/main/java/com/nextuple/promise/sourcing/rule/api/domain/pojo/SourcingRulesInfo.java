/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingRulesInfo extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -8347206232847801126L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Name of sourcing rule", example = "Sourcing Rule 1")
  private String sourcingRuleName;

  @Schema(
      description = "Rule containing the value of the required and optional attributes",
      example = "EXPRESS:T2P")
  private String sourcingRule;

  @Schema(
      description = "Define the sequence in which the sourcing rules should be selected",
      example = "1")
  private Integer sequence;

  @Schema(description = "List of sourcing node groups with their details")
  private List<NodeGroupDetailsInfo> nodeGroupDetailsInfo;

  @Schema(description = "Reference to the sourcing attributes definition")
  private Long sourcingAttributesDefinitionId;

  @Schema(description = "List of required sourcing attributes with their details")
  private List<AttributeInfo> requiredAttributes;

  @Schema(description = "List of optional sourcing attributes with their details")
  private List<AttributeInfo> optionalAttributes;
}
