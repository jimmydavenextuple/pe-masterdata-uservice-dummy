/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
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
    summary = "Get Node Service Options List",
    description = "Retrieves node service options based on the provided orgId and nodeId.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service options list is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Options List Fetched Successfully",
                  name = "Node Service Options List",
                  value =
                      """
                    {
                        "success": true,
                        "requestId": "e3593cd3-0984-4004-b825-9f32996c0229",
                        "timestamp": 1707823238254,
                        "message": "Node Service Option list fetched successfully",
                        "payload": [
                            {
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "SDND",
                                "processingTime": 3.0
                            },
                            {
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "EXPRESS",
                                "processingTime": 4.0
                            }
                        ]
                    }
                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "A 404 error code indicates that the node service options is not found.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Options List Not Found",
                  name = "Node Service Options List Not Found",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "90df5763-c50c-4567-903a-fa50ba54c405",
                                                    "timestamp": 1708326754785,
                                                    "message": "Node Service Option not found for given details",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6018,
                                                        "fields": {
                                                            "nodeId": {
                                                                "rejectedValue": "N10"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "XYZINC"
                                                            }
                                                        }
                                                    }
                                                }
                                      """)
            }))
public @interface GetNodeServiceOptionsListDoc {}
