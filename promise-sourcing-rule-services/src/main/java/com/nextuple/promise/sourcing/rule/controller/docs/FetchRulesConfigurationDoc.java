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
    summary = "Fetch Rules Configuration",
    description = "Retrieves the rule for the given order or line attributes.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 status code indicates that rule is successfully fetched for the given order or line attributes",
    content =
        @Content(
            schema = @Schema(implementation = RulesConfigurationResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Rule configuration fetched successfully",
                  name = "Rules Fetched",
                  value =
                      """
                        {
                          "success": true,
                          "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                          "timestamp": 1707025928063,
                          "message": "Rule configuration fetched successfully!",
                          "payload": {
                            "orgId": "SIGNET",
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
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to fetch rules is invalid"
            + "<ul>"
            + "<li><b>Error code: 7001</b>: Invalid attributes definition for given scope / attributes definition exists but not in ACTIVE status</li>"
            + "<li><b>Error code: 7003</b>: Can’t add or fetch the rules as all the required attributes are not present</li>"
            + "<li><b>Error code: 7004</b>: Can't add or fetch the rules as length of attributes is more than optional and required attributes combined</li>"
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
                                                                      "message": "Invalid attributes definition for given scope / attributes definition exists but not in ACTIVE status",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7001,
                                                                          "fields": {
                                                                              "attributeDefinitionId": {
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
                                                                      "message": "Can’t add or fetch the rules as all the required attributes are not present",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7003,
                                                                          "fields": {
                                                                              "rule": {
                                                                                  "rejectedValue": "EXPRESS:R1"
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """),
              @ExampleObject(
                  summary =
                      "When length of attributes is more than optional and required attributes combined",
                  name =
                      "A 400 status code indicates that length of attributes is more than optional and required attributes combined.",
                  value =
                      """
                                                                  {
                                                                      "success": false,
                                                                      "requestId": "b4926dc8-a667-4bcb-be4c-8e76715fb18f#54",
                                                                      "timestamp": 1704453973821,
                                                                      "message": "Can't add or fetch the rules as length of attributes is more than optional and required attributes combined",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7004,
                                                                          "fields": {
                                                                              "rule": {
                                                                                  "rejectedValue": "EXPRESS:R1:Store"
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the request to fetch rules didn't find any rule in the database"
            + "<ul>"
            + "<li><b>Error code: 7008</b>: Rule not found with given details</li>"
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
                                                           "message": "Rule not found for a given ML_RULE scope and SDND:RRR:CA:AB rule",
                                                           "payload": {
                                                             "type": "ERROR",
                                                             "code": 7001,
                                                             "fields": {
                                                               "attributesDefinitionId": {
                                                                 "rejectedValue": 1538
                                                               },
                                                               "rule": {
                                                                 "rejectedValue": "SDND:RRR:CA:AB"
                                                               },
                                                               "orgId": {
                                                                 "rejectedValue": "SIGNET"
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
public @interface FetchRulesConfigurationDoc {}
