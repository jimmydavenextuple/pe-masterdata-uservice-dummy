/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AttributeDetailsUIRequest implements Serializable {

  private static final long serialVersionUID = -4587768587145723951L;

  @NotNull(message = "attributeId can't be null")
  @Schema(description = "Unique identifier for an attribute.", example = "12")
  private Long attributeId;

  @NotNull(message = "attributeName can't be empty")
  @Schema(description = "Name of the attribute.", example = "serviceOption")
  private String attributeName;

  @NotNull(message = "attributeValue can't be empty")
  @Schema(description = "Value of the attribute as a service option.", example = "SDND")
  private String attributeValue;
}
