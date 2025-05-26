/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InboundProcessingRequest {

  @Schema(description = "Node identifier", example = "node-123", required = true)
  @NotBlank(message = "nodeId can't be blank")
  private String nodeId;

  @Schema(description = "Organization identifier", example = "org-456", required = true)
  @NotBlank(message = "orgId can't be blank")
  private String orgId;

  @Schema(
      description = "Rule group used to categorize or group related rules",
      example = "default-rule-group")
  @NotBlank(message = "ruleGroup can't be blank")
  private String ruleGroup;

  @Schema(
      description =
          "Strategy used for filtering rules. Optional. Defaults to 'inbound-processing-time-filter' if not provided.",
      example = "inbound-processing-time-filter")
  private String ruleFilterStrategy = "inbound-processing-time-filter";

  @Schema(
      description =
          "Request payload containing data required for rule evaluation. Flexible structure depending on the rule group.")
  @NotNull(message = "ruleEvaluationRequest can't be null")
  private Object ruleEvaluationRequest;
}
