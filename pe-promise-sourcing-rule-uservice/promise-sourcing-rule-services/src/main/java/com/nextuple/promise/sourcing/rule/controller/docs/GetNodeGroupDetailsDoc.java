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
    summary = "Get Node Group",
    description = "Retrieves the node group details for a given ID and organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node group details are successfully fetched for the given ID.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve the node group details are given ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the node group for given ID was not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Node group is not found.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given node group is not found",
                  name =
                      "A 404 error code indicates that the node group for given id was not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "c3f598a2-e158-465a-ac20-66e8e1ed9755#132",
                                                    "timestamp": 1704422322993,
                                                    "message": "Node Group not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": 4
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
public @interface GetNodeGroupDetailsDoc {}
