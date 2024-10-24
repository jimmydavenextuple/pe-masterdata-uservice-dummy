/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GroupDefinitionInfo implements Serializable {
  private static final long serialVersionUID = 5889803183936041423L;

  @Schema(description = "Unique identifier for group definition.", example = "1")
  private Long id;

  @Schema(description = "Name of the group.")
  private String groupName;

  @Schema(
      description = "Colon separated values of the required attributes.",
      example = "EXPRESS:T2P")
  private String reqAttributesValue;

  @Schema(
      description = "Colon separated values of the optional attributes.",
      example = "EXPRESS:T2P")
  private String optionalAttributesValue;
}
