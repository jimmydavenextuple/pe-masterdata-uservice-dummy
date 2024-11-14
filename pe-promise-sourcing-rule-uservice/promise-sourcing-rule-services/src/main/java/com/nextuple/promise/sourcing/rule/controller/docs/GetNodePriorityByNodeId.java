/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
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
    summary = "Get Node Priority Details",
    description = "Fetches the Node Priority Details for a given Node Id and Organization Id.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the Node Priority details are successfully retrieved.",
    content =
        @Content(
            schema = @Schema(implementation = NodePriorityResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When node group details are successfully found for given node id and org id.",
                  name = "Node Group details retrieved",
                  value =
                      """
                                {
                                                            "success": true,
                                                            "requestId": "d3e9b143-4645-405c-af1b-ff1ae788c719",
                                                            "timestamp": 1724322502508,
                                                            "message": "Node priority details successfully fetched",
                                                            "payload": [
                                                                {
                                                                    "id": 18891,
                                                                    "orgId": "NEXTUPLE_GR",
                                                                    "nodeId": "node01",
                                                                    "priority": 9999,
                                                                    "nodeGroupId": 4702
                                                                },
                                                                {
                                                                    "id": 18894,
                                                                    "orgId": "NEXTUPLE_GR",
                                                                    "nodeId": "node01",
                                                                    "priority": 9999,
                                                                    "nodeGroupId": 4703
                                                                }
                                                            ]
                                }""")
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that node priority details for a given node id is not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a node group details are not found for invalid node id.",
                  name = "Node Priority details not found.",
                  value =
                      """
                                {
                                    "success": false,
                                    "requestId": "0c2e88ee-6f74-4759-bf35-b4e5ec02226a",
                                    "timestamp": 1724322207362,
                                    "message": "Node group details not found for given id and orgId",
                                    "payload": {
                                        "type": "ERROR",
                                        "code": 6001,
                                        "fields": {
                                            "nodeId": {
                                                "rejectedValue": "node-invalid"
                                            },
                                            "orgId": {
                                                "rejectedValue": "NEXTUPLE_GR"
                                            }
                                        }
                                    }
                                }""")
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface GetNodePriorityByNodeId {}
