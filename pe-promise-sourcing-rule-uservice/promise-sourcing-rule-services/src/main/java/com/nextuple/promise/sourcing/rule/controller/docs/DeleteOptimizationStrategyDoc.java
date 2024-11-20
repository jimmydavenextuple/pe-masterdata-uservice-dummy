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
    summary = "Delete Named Optimization Strategy",
    description = "Deletes the named optimization strategy.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a optimization strategy is successfully deleted.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to delete optimization strategy is invalid."
            + "<li><b>Error code: 6001 </b></li><ul><li>Optimization strategy is not found for given organization ID and group ID.</li></ul>",
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
                                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface DeleteOptimizationStrategyDoc {}
