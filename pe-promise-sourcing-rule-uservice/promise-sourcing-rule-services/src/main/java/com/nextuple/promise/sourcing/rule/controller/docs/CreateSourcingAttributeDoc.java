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
    summary = "Create Sourcing Attribute",
    description = "Creates a Sourcing Attribute with for given Organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a  sourcing attribute  was created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to create sourcing attribute is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:When Attribute already exists for given orgId\" </li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Attribute already exists for given orgId",
                  name =
                      "A 400 error status indicates that the attribute already exist for the given orgId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "2be6133a-7eab-4d49-bd9e-db9ea0036a1e#84",
                                                    "timestamp": 1704445941514,
                                                    "message": "Attribute already exists for given orgId",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "attributeName": {
                                                                "rejectedValue": "Banner Id"
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
public @interface CreateSourcingAttributeDoc {}
