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
    summary = "Create Sourcing Attributes Definition",
    description =
        "Creates the sourcing attributes definition with the given information about the required and optional attributes configured for sourcing.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the sourcing attributes definition is successfully added.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to create sourcing attributes definition is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:Combination of organization ID and definition name is not unique.</li>"
            + "<li><b>Error code: 6001</b>: Sourcing attributes definition is already active for given organization ID.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When combination of organization ID and definition name is not unique.",
                  name =
                      "A 400 error status indicates that the combination of organization ID and definition name is not unique.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "fadb404f-9936-43c6-8de4-04d073490112#392",
                                                    "timestamp": 1704447175540,
                                                    "message": "Combination of orgId and name should be unique",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "attributesDefinitionName": {
                                                                "rejectedValue": "SO_Product_class"
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
                      "When a sourcing attributes definition is already active for given organization ID.",
                  name =
                      "A 400 error status indicates that the sourcing attributes definition is already active for given orgId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "d4c01e65-7efa-4c05-ac18-29342b3615a3#78",
                                                    "timestamp": 1704446623239,
                                                    "message": "A sourcing attributes definition is already active for given orgId and scope",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            },
                                                            "sourcingAttributesDefinitionScope": {
                                                                "rejectedValue": "SOURCING_RULE"
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the attribute was not found with given attribute id.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When attribute was not found with given attribute ID.",
                  name =
                      "A 404 error status indicates that the attribute was not found with given attribute id.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "624e7ab4-59ca-4799-9afe-f3feef8bacb4#480",
                                                    "timestamp": 1704447022908,
                                                    "message": "Attribute not found with given attribute id",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "attributeId": {
                                                                "rejectedValue": 8113
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
public @interface CreateSourcingAttributesDefinitionDoc {}
