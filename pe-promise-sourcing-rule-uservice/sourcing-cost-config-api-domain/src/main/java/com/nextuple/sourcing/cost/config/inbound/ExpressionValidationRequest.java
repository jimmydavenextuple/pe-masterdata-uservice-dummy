/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingRequestForFormulaValidation;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingSolutionForFormulaValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionValidationRequest extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 6802743896585496248L;

  @NotBlank(message = "Formula expression can't be empty")
  @Schema(description = "Formula expression", example = "(length*width*height)/5000")
  private String expression;

  @NotNull(message = "Data type for Formula expression can't be empty")
  @Schema(description = "Return data type of Formula expression", example = "NUMBER")
  private DataTypeEnum expressionValueDataType;

  @Schema(description = "Sample sourcing request for formula validation")
  private SampleSourcingRequestForFormulaValidation sampleRequest;

  @Schema(description = "Sample sourcing solution for formula validation")
  private SampleSourcingSolutionForFormulaValidation sampleSolution;
}
