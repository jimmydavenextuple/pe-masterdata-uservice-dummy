/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeFilterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
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
public class HolidayCutoffUIRequest implements Serializable {
  @Serial private static final long serialVersionUID = 7317701886300212587L;

  @NotNull(message = "sourcingAttributesDefinitionId can't be null")
  @Schema(description = "Reference to the sourcing attributes definition.", example = "123")
  private Long sourcingAttributesDefinitionId;

  @Schema(
      description = "List of holiday cutoff names to be searched for.",
      example = "CHRISTMAS,THANKSGIVING")
  private List<String> holidayCutoffNames;

  @Valid
  @Schema(description = "List of required attributes along with their values passed in the filter.")
  private List<AttributeFilterInfo> requiredAttributes;

  @Valid
  @Schema(description = "List of optional attributes along with their values passed in the filter.")
  private List<AttributeFilterInfo> optionalAttributes;
}
