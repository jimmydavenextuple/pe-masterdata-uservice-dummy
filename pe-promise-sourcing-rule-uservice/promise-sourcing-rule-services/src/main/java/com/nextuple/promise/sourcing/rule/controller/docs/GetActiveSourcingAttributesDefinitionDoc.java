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
    summary = " Get Active Sourcing Attributes Definition",
    description =
        "Retrieves the active sourcing attributes definition for a given organisation ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the active sourcing attributes definition for given organization ID is successfully fetched.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that no active sourcing attributes definition found for a given organization ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When sourcing attribute definition is not found with given ID.",
                  name =
                      "A 404 status code indicates that sourcing attributes definition by ID is not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "269a38b2-570e-4526-9a4a-d58df71962d7#3",
                                                    "timestamp": 1704447684833,
                                                    "message": "No active sourcing rule attributes definition exists for given orgId and scope",
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
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface GetActiveSourcingAttributesDefinitionDoc {}
