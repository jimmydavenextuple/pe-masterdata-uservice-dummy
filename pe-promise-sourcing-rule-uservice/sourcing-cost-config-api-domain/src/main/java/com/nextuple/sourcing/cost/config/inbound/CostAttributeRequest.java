/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.sourcing.cost.config.enums.LookupContextEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostAttributeRequest implements Serializable {
  private static final long serialVersionUID = -383086608306156270L;

  @NotBlank(message = "Attribute name cannot be blank")
  @Schema(description = "Name of the attribute", example = "length")
  private String attributeName;

  @Schema(description = "Display name of the attribute", example = "Item Length")
  private String displayName;

  @Schema(description = "Description of the attribute", example = "Length of an item")
  private String attributeDescription;

  @NotNull(message = "isPublished attribute cannot be null")
  @Schema(
      description = "Flag indicating if the attribute is exposed to tenants",
      example = "true/false")
  private Boolean isPublished;

  @NotBlank(message = "Path cannot be blank")
  @Schema(
      description = "Path of the attribute in context map",
      example = "orderLines/[*]/item/productClass")
  private String path;

  @NotNull(message = "Lookup context cannot be null")
  @Schema(description = "Context map that should be referred", example = "Length of an item")
  private LookupContextEnum lookupContext;
}
