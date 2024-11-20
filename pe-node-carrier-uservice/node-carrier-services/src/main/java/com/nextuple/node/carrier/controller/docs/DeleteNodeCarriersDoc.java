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
    summary = "Delete Node Carriers",
    description = "Deletes node carriers based on the provided criteria")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the node carrier is deleted successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier deleted successfully",
                  name = "Node Carrier Deleted",
                  value =
                      """
                            {
                                "success": true,
                                "requestId": "b9c28189-2034-42e2-9ad3-460ef7aacb58",
                                "timestamp": 1707026733068,
                                "message": "Node Carrier deleted successfully",
                                "payload": {
                                    "nodeId": "N700",
                                    "orgId": "NEXTUPLE_GR",
                                    "carrierServiceId": "carrier4",
                                    "serviceOption": "EXPRESS",
                                    "lastPickupTime": "23:59"
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
                                "requestId": "a3d8ee48-1d38-4d97-9548-272c4f7ce32a",
                                "timestamp": 1707026741862,
                                "message": "Node Carrier not found for given details",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 6003,
                                    "fields": {
                                        "serviceOption": {
                                            "rejectedValue": "EXPRESS"
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
public @interface DeleteNodeCarriersDoc {}
