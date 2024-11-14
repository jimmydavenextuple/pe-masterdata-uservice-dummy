/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetProfitMarginRequest implements Serializable {

  private static final long serialVersionUID = -1766230774577746122L;

  @NotBlank(message = "attributeName can't be blank")
  @Schema(
      description = "Name of the attribute for which target profit margins are configured.",
      example = "itemCategory")
  private String attributeName;

  @NotBlank(message = "attributeValue can't be blank")
  @Schema(
      description = "Value of the attribute  for which target profit margins are configured.",
      example = "KITCHEN")
  private String attributeValue;

  @NotNull(message = "targetGrossProfitMargin can't be null")
  @Schema(description = "Target gross profit margin for the attribute value.", example = "10")
  private Integer targetGrossProfitMargin;
}
