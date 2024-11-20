/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
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
    summary = "Get Node Service Option Buffer",
    description = "Get the node service option buffer by given orgId and Id")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option buffer is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option buffer successfully fetched",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "28fa75c8-b211-4e18-8e47-1253af6c75bf",
                                            "timestamp": 1707891866051,
                                            "message": "Node service option buffer fetched successfully",
                                            "payload": {
                                                "id": 3,
                                                "nodeId": "N104",
                                                "orgId": "XYZINC",
                                                "serviceOption": "EXPRESS",
                                                "bufferHours": 2.0,
                                                "bufferEndDate": "2024-01-24T09:59:01.000+00:00",
                                                "bufferStartDate": "2024-01-23T00:00:01.000+00:00"
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that node service option buffer not found for the given buffer.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option buffer not found for the given orgId and Id",
                  value =
                      """
                                  {
                                                      "success": false,
                                                      "requestId": "036a10d0-768a-4ff8-9226-083ed2cd4029",
                                                      "timestamp": 1707891775262,
                                                      "message": "Node service option buffer not found for given orgId and Id",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6017,
                                                          "fields": {
                                                              "id": {
                                                                  "rejectedValue": 12
                                                              },
                                                              "orgId": {
                                                                  "rejectedValue": "XYZINC"
                                                              }
                                                          }
                                                      }
                                                  }
                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "There was some error on server while processing the request",
                  name =
                      "A 500 error code indicates that there was some error on the server while processing the request.",
                  value =
                      """
                          {
                              "success": false,
                              "timestamp": 1670589273234,
                              "payload": {
                                    "type": "ERROR",
                                    "code": 2
                                }
                          }
                          """)
            }))
public @interface FetchNodeServiceOptionBufferDoc {}
