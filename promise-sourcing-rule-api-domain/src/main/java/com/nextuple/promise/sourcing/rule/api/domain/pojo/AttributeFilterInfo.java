/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class AttributeFilterInfo implements Serializable {

  public static final long serialVersionUID = 3293216542593016661L;

  @NotBlank(message = "attributeName can't be blank")
  @Schema(description = "Unique name of the sourcing attribute.", example = "nodeType")
  private String attributeName;

  @NotBlank(message = "attributeId can't be blank")
  @Schema(description = "Unique identifier of the sourcing attribute.", example = "1")
  private String attributeId;

  @NotEmpty(message = "attributeFilterValues list can't be empty")
  @Schema(description = "List of attribute values to be searched for.", example = "STORE,FC")
  private List<String> attributeFilterValues;
}
