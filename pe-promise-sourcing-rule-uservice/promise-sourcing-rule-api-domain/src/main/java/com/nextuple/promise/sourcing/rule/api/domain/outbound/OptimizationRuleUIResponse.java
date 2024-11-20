/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class OptimizationRuleUIResponse implements Serializable {

  private static final long serialVersionUID = -1187186026737171873L;

  @Schema(description = "Unique identifier for optimizationRuleId.")
  private Long optimizationRuleId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier for sourcingAttributesDefinitionId.")
  private String sourcingAttributesDefinitionId;

  @Schema(description = "Optimization rule name.", example = "Rule-1")
  private String optimizationRuleName;

  @Schema(description = "List of the attributes.")
  private List<AttributeDetailsUIResponse> requiredAttributes;

  @Schema(description = "List of the attributes.")
  private List<AttributeDetailsUIResponse> optionalAttributes;

  @Schema(description = "Optimization strategy.", example = "SPEED,PRIORITY")
  private String strategy;

  @Schema(description = "List of the constraints.")
  private List<AllConstraintUIDto> constraints;

  @Schema(description = "Reference to the group.")
  private String groupId;
}
