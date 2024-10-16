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
    summary = "Configure Sourcing Rule",
    description = "Adds a sourcing rule with the sequence and node groups associated to it.")
@ApiResponse(responseCode = "200", description = "When a sourcing rule is successfully configured")
@ApiResponse(
    responseCode = "400",
    description =
        "<ul>"
            + "<li><b>Error code: 6001</b>:When a Sourcing Rule exists with given sourcingRuleName</li>"
            + "<li><b>Error code: 6001</b>: When a sourcing rule attributes definition is not valid or is in INACTIVE status</li>"
            + "<li><b>Error code: 6001</b>: When a given node group doesn't exists</li>"
            + "<li><b>Error code: 6001</b>: When all the required attributes values are not present</li>"
            + "<li><b>Error code: 6001</b>: When combination of sourcing rule and node group already exists</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a Sourcing Rule exists with given sourcingRuleName",
                  name =
                      "A 400 status code indicates that sourcing rule already exists with given sourcingRuleName.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "c799a6db-1e1b-4b74-8544-ec788ad2abc0#1",
                                                    "timestamp": 1704453662569,
                                                    "message": "Sourcing Rule exists with given sourcingRuleName",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "sourcingRuleName": {
                                                                "rejectedValue": "Rule_t_1"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
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
                  summary = "When a given node group doesn't exists",
                  name = "A 400 status code indicates that a given node group doesn't exists.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "2b4c69d5-d8dc-4539-92f9-72d6dd2bf24a#153",
                                                    "timestamp": 1704453244207,
                                                    "message": "Node group doesn't exist",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupId": {
                                                                "rejectedValue": "2155"
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
                  summary = "When combination of sourcing rule and node group already exists",
                  name =
                      "A 400 status code indicates that the combination of sourcing rule and node group already exists.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "12557783-d596-426b-a48b-bc4daa6c957d#2",
                                                    "timestamp": 1704453776631,
                                                    "message": "Combination of sourcing rule and node group already exists",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupId": {
                                                                "rejectedValue": "2171"
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
public @interface ConfigureSourcingRuleDoc {}
