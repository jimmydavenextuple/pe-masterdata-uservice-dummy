/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

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
    summary = "Get Node Carriers List",
    description = "Retrieves node carriers based on the provided orgId and nodeId.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node carriers list is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carriers List Fetched Successfully",
                  name = "Node Carriers List",
                  value =
                      """
                    {
                        "success": true,
                        "requestId": "abff5769-7e83-423c-b1f4-8f01c90cad70",
                        "timestamp": 1707822022220,
                        "message": "Node Carriers list fetched successfully",
                        "payload": [
                            {
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "carrierServiceId": "carrier2",
                                "serviceOption": "SDND",
                                "lastPickupTime": "23:00"
                            },
                            {
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "carrierServiceId": "carrier4",
                                "serviceOption": "SDND",
                                "lastPickupTime": "20:00"
                            }
                        ]
                    }
                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 exception code indicates that the Node carrier not found with given orgId and nodeId.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carriers List not found",
                  name = "Node Carriers List not found",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "06a0c7ad-156e-44a4-bd2f-e23d4fd19d4d",
                                                    "timestamp": 1708326533065,
                                                    "message": "Node carrier not found with given orgId and nodeId",
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
public @interface GetNodeCarriersListDoc {}
