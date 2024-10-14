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
    summary = "Add Named Optimization Strategy",
    description = "Adds a named optimization strategy for a given group.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a optimization strategy is successfully added.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to add optimization strategy is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:Optimization strategy already associated for given organization ID and group ID. </li>"
            + "<li><b>Error code: 6001</b>:Group ID is invalid.</li>"
            + "<li><b>Error code: 6001</b>:Optimization strategy detail is invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a named optimization strategy already associated for given orgId and groupId",
                  name =
                      "A 400 error status indicates that a named optimization strategy already associated for given orgId and groupId",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "7cd8c14d-4a98-4b86-8109-d2ec4d344a02#119",
                                                    "timestamp": 1704420294246,
                                                    "message": "Named optimization strategy already associated for given orgId and groupId",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "groupId": {
                                                                "rejectedValue": "DEFAULT"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When groupId is invalid",
                  name = "A 400 error status indicates that the groupId is invalid.",
                  value =
                      """
                                                      {
                                                          "success": false,
                                                          "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#94",
                                                          "timestamp": 1704419698639,
                                                          "message": "Invalid groupId",
                                                          "payload": {
                                                              "type": "ERROR",
                                                              "code": 6001,
                                                              "fields": {
                                                                  "groupId": {
                                                                      "rejectedValue": "4"
                                                                  },
                                                                  "orgId": {
                                                                      "rejectedValue": "NEXTUPLE_GR"
                                                                  }
                                                              }
                                                          }
                                                      }
                                                """),
              @ExampleObject(
                  summary = "When named optimization strategy detail is invalid",
                  name =
                      "A 400 error status indicates that the named optimization strategy detail is invalid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#97",
                                                    "timestamp": 1704420210711,
                                                    "message": "Invalid named optimization strategy detail",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "optimizationStrategyDetail": {
                                                                "rejectedValue": "JKL"
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
public @interface AddOptimizationStrategyDoc {}
