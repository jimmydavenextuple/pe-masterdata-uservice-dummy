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
    summary = "Add Group Definition",
    description =
        "Adds a group definition for sourcing with given group name and attributes for sourcing attributes definition ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that a group definition is successfully added.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:Combination of organization ID and group name is not unique. </li>"
            + "<li><b>Error code: 6001</b>:Group already exist for given organization ID , sourcing attributes definition ID and required attributes values.</li>"
            + "<li><b>Error code: 6001</b>:Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When combination of orgId and groupName is not unique",
                  name =
                      "A 400 error status indicates that the combination of orgId and groupName is not unique"),
              @ExampleObject(
                  summary =
                      "When a group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue.",
                  name =
                      "A 400 error status indicates that a group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue.",
                  value =
                      """
                                 {
                                                "success": false,
                                                "requestId": "33ab213d-0472-4696-b0f6-9ee5546da949#4171",
                                                "timestamp": 1704176916399,
                                                "message": "Group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "reqAttributesValue": {
                                                            "rejectedValue": "EXPRESS:HOME"
                                                        },
                                                        "sourcingAttributesDefinitionId": {
                                                            "rejectedValue": 1776
                                                        },
                                                        "orgId": {
                                                            "rejectedValue": "NEXTUPLE_GR"
                                                        }
                                                    }
                                                }
                                            }
                              """),
              @ExampleObject(
                  summary =
                      "When there is an invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
                  name =
                      "A 400 error status indicates that there is an invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status.",
                  value =
                      """
                           {
                        "success": false,
                        "requestId": "5a348bfc-b284-4783-9946-e311e0bc9022#1552",
                        "timestamp": 1704174131246,
                        "message": "Invalid sourcing attributes definition for OPTIMIZATION scope/ Sourcing  attributes definition exists but not in ACTIVE status",
                        "payload": {
                            "type": "ERROR",
                            "code": 6001,
                            "fields": {
                                "sourcingAttributesDefinitionId": {
                                    "rejectedValue": 2430
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
public @interface AddGroupDefinitionDoc {}
