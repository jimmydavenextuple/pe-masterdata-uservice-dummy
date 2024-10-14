/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourcingAttributesDefinitionUpdationRequest implements Serializable {

  private static final long serialVersionUID = 427281486993356179L;

  @Schema(description = "Unique name for sourcing rule attributes definition", example = "def1")
  private String name;

  @Schema(
      description =
          "Comma separated string that will contain the references of the required attributes",
      example = "1,2")
  private String reqAttributes;

  @Schema(
      description =
          "Comma separated string that will contain the references of the optional attributes")
  private String optAttributes;

  @Schema(
      description = "Status of the sourcing rule attributes definition",
      allowableValues = {"ACTIVE", "INACTIVE", "DRAFT"})
  private SourcingAttributesDefinitionStatus status;

  private String module;
}
