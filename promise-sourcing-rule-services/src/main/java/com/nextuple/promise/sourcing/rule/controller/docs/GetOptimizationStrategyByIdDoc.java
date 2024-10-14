/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Named Optimization Strategy by ID",
    description = "Retrieves the named optimization strategy with given ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a optimization strategy is successfully fetched for given ID.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve a optimization strategy for given ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that a named optimization strategy is not not found for given ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a named optimization strategy is not found for given id",
                  name =
                      "A 404 error status indicates that a named optimization strategy is not not found for given id.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3b7d7693-29fb-488b-9a71-fd9cb69f98c8#172",
                                                    "timestamp": 1704420754117,
                                                    "message": "Named optimization strategy not found for given id",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": 3
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface GetOptimizationStrategyByIdDoc {}
