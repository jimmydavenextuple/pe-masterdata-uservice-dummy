/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllSourcingRulesResponse {
  private static final long serialVersionUID = 7466516347729401530L;

  @Schema(description = "Unique identifier of the organisation.", example = "NEXTUPLE")
  private String orgId;

  @Schema(
      description = "Rule containing the value of the required and optional attributes.",
      example = "EXPRESS:T2P")
  private String sourcingRule;

  @Schema(description = "Unique identifier for the sourcing rule.")
  private Long sourcingRuleId;

  @Schema(description = "Unique identifier for sourcing rule attributes definition", example = "1")
  private Long sourcingAttributesDefinitionId;

  @Schema(description = "Unique name of the sourcing rule", example = "EXPRESS-T2P-1")
  private String sourcingRuleName;

  private List<AttributeInfo> requiredAttributes;
  private List<AttributeInfo> optionalAttributes;
  private List<NodeGroupInfo> nodes;
}
