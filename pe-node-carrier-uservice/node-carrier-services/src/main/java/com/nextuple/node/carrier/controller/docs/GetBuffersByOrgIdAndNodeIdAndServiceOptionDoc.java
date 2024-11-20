/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nimbusds.oauth2.sdk.ErrorResponse;
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
    summary = "Get Node Service Option Buffers",
    description =
        "Retrieves node service option buffers for a specific orgId, nodeId, and serviceOption.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option buffers list is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option Buffers List Fetched Successfully",
                  name = "Node Service Option Buffers List",
                  value =
                      """
                    {
                        "success": true,
                        "requestId": "c1b45bc4-23e6-4e11-8171-167c03d8f852",
                        "timestamp": 1707823475123,
                        "message": "Node Service option buffer list fetched successfully",
                        "payload": [
                            {
                                "id": 2,
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "SDND",
                                "bufferHours": 1.0,
                                "bufferEndDate": "2024-02-01T00:00:00.000+00:00",
                                "bufferStartDate": "2024-01-01T00:00:00.000+00:00"
                            },
                            {
                                "id": 3,
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "SDND",
                                "bufferHours": 1.0,
                                "bufferEndDate": "2023-04-01T00:00:00.000+00:00",
                                "bufferStartDate": "2023-03-01T00:00:00.000+00:00"
                            },
                            {
                                "id": 4,
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "SDND",
                                "bufferHours": 1.0,
                                "bufferEndDate": "2023-06-01T00:00:00.000+00:00",
                                "bufferStartDate": "2023-05-01T00:00:00.000+00:00"
                            },
                            {
                                "id": 5,
                                "nodeId": "N700",
                                "orgId": "NEXTUPLE_GR",
                                "serviceOption": "SDND",
                                "bufferHours": 1.0,
                                "bufferEndDate": "2020-06-01T00:00:00.000+00:00",
                                "bufferStartDate": "2020-05-01T00:00:00.000+00:00"
                            }
                        ]
                    }
                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the node service option buffers list not found with given details.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option Buffers not found",
                  name = "Node Service Option Buffers Not Found",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "744d2742-599c-410a-9b9e-b0a2c9104758",
                                                     "timestamp": 1708326205194,
                                                     "message": "Node service option buffer not found for given orgId, nodeId and serviceOption",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6021,
                                                         "fields": {
                                                             "serviceOption": {
                                                                 "rejectedValue": "EXPRES"
                                                             },
                                                             "nodeId": {
                                                                 "rejectedValue": "N104"
                                                             },
                                                             "orgId": {
                                                                 "rejectedValue": "XYZINC"
                                                             }
                                                         }
                                                     }
                                                 }
                                      """)
            }))
public @interface GetBuffersByOrgIdAndNodeIdAndServiceOptionDoc {}
