/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
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
    summary = "Delete Rule Configuration",
    description = "Deletes the rule for the given order or line attributes.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 status code indicates that rule is successfully deleted for the given order or line attributes",
    content =
        @Content(
            schema = @Schema(implementation = RulesConfigurationResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Rule configuration deleted successfully",
                  name = "Rules Deleted",
                  value =
                      """
                                          {
                                            "success": true,
                                            "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                            "timestamp": 1707025928063,
                                            "message": "Rule configuration deleted successfully!",
                                            "payload": {
                                              "orgId": "NEXTUPLE",
                                              "ruleName": "ml-rule1",
                                              "rule": "SDND:RRR:CA:AB",
                                              "moduleName": "PROCESSING_TIME",
                                              "attributeDefinitionId": 1538,
                                              "scope": "ML_RULE"
                                            }
                                          }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the request to delete rules didn't find any rule in the database"
            + "<ul>"
            + "<li><b>Error code: 7009</b>: Error in deleting the rule, rule not found with given details</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When no rule is found with the given request",
                  name = "A 404 status code indicates that the rule is not found.",
                  value =
                      """
                                                                 {
                                                                             "success": false,
                                                                             "requestId": "b4e38b04-84b7-44ac-a4a7-5e3cb730fded#21",
                                                                             "timestamp": 1704453534601,
                                                                             "message": "Error in deleting the rule, rule not found with given details",
                                                                             "payload": {
                                                                               "type": "ERROR",
                                                                               "code": 7009,
                                                                               "fields": {
                                                                                  "moduleName": {
                                                                                      "rejectedValue": "PROCESSING_TIME"
                                                                                  },
                                                                                  "ruleName": {
                                                                                      "rejectedValue": "MLRule1"
                                                                                  },
                                                                                  "orgId": {
                                                                                      "rejectedValue": "NEXTUPLE"
                                                                                  },
                                                                                  "rule": {
                                                                                      "rejectedValue": "Node1:Item1"
                                                                                  },
                                                                                  "scope": {
                                                                                      "rejectedValue": "ML_RULE"
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
public @interface DeleteRuleConfigurationDoc {}
