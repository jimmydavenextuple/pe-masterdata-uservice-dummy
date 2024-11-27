/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
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
    summary = "Fetch Group Definition",
    description =
        "Fetched a group definition for optimization with given group name and attributes values for sourcing attributes definition ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that a group definition is successfully fetched.",
    content =
        @Content(
            schema = @Schema(implementation = FetchGroupDefinitionByScoring.class),
            examples = {
              @ExampleObject(
                  summary = "When a group definition is successfully found.",
                  name =
                      "A 200 code status indicates that a group definition is successfully found.",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "84eee47c-92ea-4f0e-9f20-249872a958df",
                                            "timestamp": 1729839551934,
                                            "message": "Group Definition fetched successfully",
                                            "payload": {
                                                "id": 1,
                                                "orgId": "NEXTUPLE_GR",
                                                "groupName": "testingRule1",
                                                "reqAttributesValue": "STANDARD:KITCHEN",
                                                "optionalAttributesValue": "SHIP:CART",
                                                "sourcingAttributesDefinitionId": 6621
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that request to retrieve a group definition is not valid.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When the sourcing attribute definition id is not passed.",
                  name =
                      "A 400 bad request indicates the sourcing attribute definition id is not passed.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "d1484774-3591-47d8-b6bc-05a11dcc7c40",
                                            "timestamp": 1729844078838,
                                            "message": "Bad Request",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2,
                                                "fields": {
                                                    "sourcingAttributeDefinitionId": {
                                                        "rejectedValue": "null",
                                                        "errorMessage": "sourcingAttributeDefinitionId can't be null"
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
public @interface FetchGroupDefinitionByScoring {}
