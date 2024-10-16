/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.outbound;

import com.nextuple.sourcing.cost.config.pojo.SampleSourcingRequestForFormulaValidation;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingSolutionForFormulaValidation;
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
public class ExpressionValidationResponse implements Serializable {
  private static final long serialVersionUID = -8577044545186970734L;

  @Schema(description = "Calculated value for formula expression", example = "300.0")
  private Double expressionValue;

  @Schema(description = "Pre-loaded sample sourcing solution for formula validation")
  private SampleSourcingSolutionForFormulaValidation sampleSolution;

  @Schema(description = "Pre-loaded sample sourcing request for formula validation")
  private SampleSourcingRequestForFormulaValidation sampleRequest;
}
