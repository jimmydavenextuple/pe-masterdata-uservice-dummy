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
    summary = "Get Sourcing Rules",
    description = "Retrieves the sourcing rules for the given order or line attributes.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a sourcing rules are successfully fetched for the given order or line attributes")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to fetch sourcing rules is invalid"
            + "<ul>"
            + "<li><b>Error code: 6001</b>: When a sourcing rule attributes definition is not valid or is in INACTIVE status</li>"
            + "<li><b>Error code: 6001</b>: When all the required attributes values are not present</li>"
            + "<li><b>Error code: 6001</b>: When default sourcing rules is not configured</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a sourcing rule attributes definition is not valid or is in INACTIVE status",
                  name =
                      "A 400 status code indicates that the sourcing rule attributes definition is not valid or is in INACTIVE status.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "b4e38b04-84b7-44ac-a4a7-5e3cb730fded#21",
                                                    "timestamp": 1704453534601,
                                                    "message": "Invalid sourcing attributes definition for SOURCING_RULE scope/ Sourcing  attributes definition exists but not in ACTIVE status",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "sourcingAttributesDefinitionId": {
                                                                "rejectedValue": 6860
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When all the required attributes values are not present",
                  name =
                      "A 400 status code indicates that all the required attributes values are not present.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "b4926dc8-a667-4bcb-be4c-8e76715fb18f#54",
                                                    "timestamp": 1704453973821,
                                                    "message": "Can't add or fetch the sourcing rule as all the required attributes values are not present",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "sourcingRule": {
                                                                "rejectedValue": "Essentials-Dessert"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When default sourcing rules is not configured",
                  name =
                      "A 400 status code indicates that default sourcing rules is not configured.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "8383be0d-82e3-4aaa-b3ae-a03562e42b55#43",
                                                    "timestamp": 1704454969302,
                                                    "message": "Default sourcing rules not configured",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "sourcingAttributesDefinitionId": {
                                                                "rejectedValue": 686
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
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
public @interface FetchSourcingRulesDoc {}
