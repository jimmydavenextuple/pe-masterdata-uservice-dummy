/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NodePriorityResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 6727800069739427285L;

  @Schema(description = "Unique identifier for node priority record")
  private Long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier for node", example = "DC1")
  private String nodeId;

  @Schema(description = "Priority of the given node within a node group")
  private Integer priority;

  @Schema(description = "Unique identifier for node group")
  private Long nodeGroupId;
}
