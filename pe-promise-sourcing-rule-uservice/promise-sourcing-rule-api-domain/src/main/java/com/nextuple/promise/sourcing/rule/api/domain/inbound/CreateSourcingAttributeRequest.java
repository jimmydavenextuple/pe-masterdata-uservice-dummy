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
public class CreateSourcingAttributeRequest extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = 8409656220653409884L;

  @NotBlank(message = "orgId can't be empty.")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "attributeName can't be empty.")
  @Schema(description = "Unique name for sourcing attribute")
  private String attributeName;

  @NotNull(message = "isDerived can't be empty.")
  @Schema(
      description =
          "If isDerived = true , then customAttributeKey is a required attribute , else jsonPath is a required attribute")
  private Boolean isDerived;

  @Schema(
      description = "customer want to configure rules based custom attributes/ key value defined ")
  private String customAttributeKey;

  @Schema(description = "json path for given orders/serviceOptions")
  private String jsonPath;
}
