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
    summary = "Get Group Definition List",
    description =
        "Retrieves a list of group definitions for the given organization ID and sourcing attributes definition ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of group definitions for the given organization ID and sourcing attributes definition ID is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that the list of group definitions is not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a group definition is not found.",
                  name = "A 400 error status indicates that the group definition is not found.",
                  value =
                      """
                                             {
                                                 "success": false,
                                                 "requestId": "76096c7d-7511-4354-b001-c4b8090587e7#660",
                                                 "timestamp": 1704178128468,
                                                 "message": "Group definition not found",
                                                 "payload": {
                                                     "type": "ERROR",
                                                     "code": 6001,
                                                     "fields": {
                                                         "id": {
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
public @interface GetGroupDefinitionListDoc {}
