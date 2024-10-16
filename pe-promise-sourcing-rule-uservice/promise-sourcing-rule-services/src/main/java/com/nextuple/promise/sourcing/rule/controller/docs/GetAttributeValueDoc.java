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
    summary = "Get Attribute values.",
    description = "Retrieves the values of the sourcing attribute defined for the orgId.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a values of the attribute is fetched successfully.",
    content =
        @Content(
            examples = {
              @ExampleObject(
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "e69ba79b-03ea-4a07-be3d-e79dc1e7fa6b#6223",
                                                    "timestamp": 1704360612046,
                                                    "message": "Attribute values successfully fetched!",
                                                    "payload": {
                                                        "attributeName": "serviceOptAttr",
                                                        "values": [
                                                            "EXPRESS",
                                                            "ExpressService",
                                                            "AirService",
                                                            "AlienService",
                                                            "Standard",
                                                            "SameDay",
                                                            "NextDay"
                                                        ]
                                                    }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "A 404 status code indicates that the sourcing attribute is not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given sourcing attribute given is not found",
                  name = "A 404 status code indicates that the sourcing attribute is not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "954d83b0-701f-4533-828f-8bf0193fd41e#2219",
                                                    "timestamp": 1704359846165,
                                                    "message": "Sourcing attribute not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "orgId": {
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
public @interface GetAttributeValueDoc {}
