/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.ExpressionValidationDoc;
import com.nextuple.sourcing.cost.config.inbound.ExpressionValidationRequest;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
import com.nextuple.sourcing.cost.config.service.ExpressionValidationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost-config")
@RequiredArgsConstructor
@Tag(name = "Formula Expression Validation APIs")
@Slf4j
public class ExpressionValidationController {

  private final ExpressionValidationService expressionValidationService;

  @ExpressionValidationDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/expression/validation/{orgId}/{libraryName}")
  public ResponseEntity<BaseResponse<ExpressionValidationResponse>> validateExpression(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable
          @Parameter(
              description = "Unique identifier of the organization.",
              example = "NEXTUPLE_GR")
          String orgId,
      @NotBlank(message = "Formula expression library name can't be empty")
          @PathVariable
          @Parameter(description = "Formula expression library name", example = "SPEL")
          String libraryName,
      @RequestBody @Valid ExpressionValidationRequest expressionValidationRequest)
      throws CommonServiceException {

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Formula expression validated successfully.")
            .payload(
                expressionValidationService.validateExpression(
                    orgId, libraryName, expressionValidationRequest))
            .build());
  }
}
