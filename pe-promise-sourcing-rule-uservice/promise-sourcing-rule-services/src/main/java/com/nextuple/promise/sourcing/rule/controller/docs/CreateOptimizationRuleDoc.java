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
    summary = "Create an Optimization Rule",
    description =
        "Creates an optimization rule for a given org ID and sourcing attribute definition ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that an optimization rule is successfully created.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error status indicates that the request to create an optimization rule is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:A group definition is already associated with the given org ID sourcing attribute definition ID and required attributes value.</li>"
            + "<li><b>Error code: 6001</b>:The combination of org ID and group name is not unique.</li>"
            + "<li><b>Error code: 6001</b>:All required attributes are not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a group definition already associated for given orgId and sourcingAttributeDefinitionId and reqAttributesValue",
                  name =
                      "A 400 error status indicates that the a group definition already associated for given orgId and sourcingAttributeDefinitionId and reqAttributesValue.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "46cc8edf-b0de-4ec4-89ee-1f95b7a981f8#14",
                                                    "timestamp": 1704435189930,
                                                    "message": "Group already exist for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "reqAttributesValue": {
                                                                "rejectedValue": "EXPRESS"
                                                            },
                                                            "sourcingAttributesDefinitionId": {
                                                                "rejectedValue": 805
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
                      "When an optimization rule already associated for given orgId and groupId",
                  name =
                      "A 400 error status indicates that the optimization rule already associated for given orgId and groupId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "cd30448f-df92-49b4-93c7-fd938020676b#245",
                                                    "timestamp": 1704436591339,
                                                    "message": "Combination of orgId and groupName should be unique",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "groupName": {
                                                                "rejectedValue": "OptRule_1 group"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When all required attributes values are not present.",
                  name =
                      "A 400 error status indicates that all required attributes values are not present.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "65b4d391-b42f-4b0b-918b-625bb84c4665#143",
                                                    "timestamp": 1704435676101,
                                                    "message": "Can't add the optimization strategy as all the required attributes values are not present",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "reqAttributesValue": {
                                                                "rejectedValue": "EXPRESS"
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
public @interface CreateOptimizationRuleDoc {}
