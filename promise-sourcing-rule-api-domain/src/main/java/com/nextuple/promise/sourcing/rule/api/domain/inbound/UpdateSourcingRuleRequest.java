/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSourcingRuleRequest implements Serializable {
  private static final long serialVersionUID = 4679533750213061279L;

  @NotNull(message = "Sourcing rule id can not be null.")
  private Long sourcingRuleId;

  @NotEmpty(message = "Sourcing rule name can not be empty.")
  private String sourcingRuleName;

  @NotNull(message = "Sourcing rule attributes definition id not be empty.")
  private Long sourcingAttributesDefinitionId;

  private List<AttributeInfo> requiredAttributes;
  private List<AttributeInfo> optionalAttributes;

  @NotEmpty(message = "Node groups list can not be empty.")
  private List<UpdateSourcingRuleNodeGroupRequest> nodeGroups;
}
