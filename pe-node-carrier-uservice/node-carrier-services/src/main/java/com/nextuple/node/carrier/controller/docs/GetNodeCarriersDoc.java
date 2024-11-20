/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
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
    summary = "Get Node Carriers",
    description = "Retrieves details of node carriers based on the provided criteria")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node carrier details are fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier details fetched successfully",
                  name = "Node Carrier Details Fetched",
                  value =
                      """
                            {
                                "success": true,
                                "requestId": "aa5276b9-f16f-4e70-85ab-0ae99d70d6d9",
                                "timestamp": 1707026474219,
                                "message": "Node Carrier details fetched successfully",
                                "payload": {
                                    "nodeId": "N700",
                                    "orgId": "NEXTUPLE_GR",
                                    "carrierServiceId": "carrier4",
                                    "serviceOption": "EXPRESS",
                                    "lastPickupTime": "20:00"
                                }
                            }
                            """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "A 404 error code indicates that no node carrier is found for the given details.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier not found for given details",
                  name = "Node Carrier Not Found",
                  value =
                      """
                            {
                                "success": false,
                                "requestId": "1e154702-978f-4ec7-9d63-2bb0bc59e118",
                                "timestamp": 1707026489139,
                                "message": "Node Carrier not found for given details",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 6003,
                                    "fields": {
                                        "serviceOption": {
                                            "rejectedValue": "SDND"
                                        },
                                        "carrierServiceId": {
                                            "rejectedValue": "carrier4"
                                        },
                                        "nodeId": {
                                            "rejectedValue": "N700"
                                        },
                                        "orgId": {
                                            "rejectedValue": "NEXTUPLE_GR"
                                        }
                                    }
                                }
                            }
                            """)
            }))
public @interface GetNodeCarriersDoc {}
