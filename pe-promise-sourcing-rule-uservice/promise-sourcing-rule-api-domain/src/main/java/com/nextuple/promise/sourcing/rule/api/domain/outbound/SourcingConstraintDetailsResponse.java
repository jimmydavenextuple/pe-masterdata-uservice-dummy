/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
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
public class SourcingConstraintDetailsResponse extends AdditionalAttributes
    implements Serializable {

  private static final long serialVersionUID = 34073110539251798L;

  @Schema(description = "Unique identifier for the sourcing constraint.")
  private Long id;

  @Schema(description = "Unique identifier for organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(
      description =
          " Unique identifier for the group for which the constraints have to be applied.")
  private String groupId;

  @Schema(
      description = "Defines the constraint details to be added.",
      allowableValues = {"PRE_ORDER_PROMISE_DATE", "SHIP_COMPLETE_LINE"})
  private SourcingConstraintEnum sourcingConstraint;

  @Schema(description = "Defines the applicability of the given constraint.", example = "0")
  private String sourcingConstraintValue;
}
