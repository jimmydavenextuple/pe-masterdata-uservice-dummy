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
    summary = "Update Optimization Rule",
    description = "Updates the optimization rule with the given details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the optimization rule is successfully updated.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update the optimization rule is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:Optimization rule is not found for the given optimization rule ID.</li>"
            + "<li><b>Error code: 6001</b>:Optimization rule name is empty.</li>"
            + "<li><b>Error code: 6001</b>:Strategy is invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a optimization rule is not found for given optimizationRuleId",
                  name =
                      "A 400 error status indicates that the optimization rule is not found for given optimizationRuleId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "6fa2dc66-c2f7-44f9-8ad9-fed8d1501280#92",
                                                    "timestamp": 1704437496849,
                                                    "message": "Optimization Rule not found for given optimizationRuleId",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "optimizationRuleId": {
                                                                "rejectedValue": 55
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When optimizationRuleName is empty",
                  name = "A 400 error status indicates that the optimization rule name is empty.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "05f7eacd-8fff-46b9-9068-977dd061260c#25",
                                                    "timestamp": 1704437915140,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "optimizationRuleName": {
                                                                "rejectedValue": "",
                                                                "errorMessage": "optimizationRuleName can't be empty"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When strategy is invalid",
                  name = "A 400 error status indicates that optimization strategy name is invalid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "107989b4-a6b5-47ab-9ab4-71acce37e145#40",
                                                    "timestamp": 1704437772014,
                                                    "message": "Invalid named optimization strategy detail",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "optimizationStrategyDetail": {
                                                                "rejectedValue": "JK"
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
public @interface EditOptimizationRuleDoc {}
