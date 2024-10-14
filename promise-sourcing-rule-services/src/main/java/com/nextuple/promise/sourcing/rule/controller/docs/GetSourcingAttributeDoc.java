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
    summary = "Get Sourcing Attribute",
    description = "Retrieves a Sourcing Attribute for given Organization ID and Attribute ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a  sourcing attribute  was fetched successfully.")
@ApiResponse(
    responseCode = "404",
    description = "A 404 error code indicates that the sourcing attribute was not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Sourcing Attribute is not found",
                  name = "A 404 error code indicates that the sourcing attribute was not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "f624be2a-5645-491f-94e6-d5dc76eda71b#275",
                                                    "timestamp": 1704446378402,
                                                    "message": "Sourcing attribute not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": 3171
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface GetSourcingAttributeDoc {}
