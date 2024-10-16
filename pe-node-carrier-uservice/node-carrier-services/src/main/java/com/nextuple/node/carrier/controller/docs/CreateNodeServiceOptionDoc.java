/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Create Node Service Option",
    description = "Creates a new node service option based on the provided details")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option is created successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option successfully created",
                  value =
                      """
                                            {
                                                "success": true,
                                                "requestId": "67d1671c-b531-434a-82a8-53c7b8969409",
                                                "timestamp": 1707026972592,
                                                "message": "Node Service Option successfully created",
                                                "payload": {
                                                    "nodeId": "N700",
                                                    "orgId": "NEXTUPLE_GR",
                                                    "serviceOption": "EXPRESS",
                                                    "processingTime": 55.0
                                                }
                                            }
                                            """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is an issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Invalid nodeId and orgId combination",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "4243257d-8c8d-4baf-85de-8fe6ac42a0d3",
                                                "timestamp": 1707026988316,
                                                "message": "Invalid nodeId and orgId combination",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6002,
                                                    "fields": {
                                                        "serviceOption": {
                                                            "rejectedValue": "EXPRESS"
                                                        },
                                                        "nodeId": {
                                                            "rejectedValue": "N7000"
                                                        },
                                                        "orgId": {
                                                            "rejectedValue": "NEXTUPLE_GR"
                                                        }
                                                    }
                                                }
                                            }
                                            """),
              @ExampleObject(
                  summary = "Invalid serviceOption",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "ea624596-1847-4f42-9c87-a965b45e622c",
                                                "timestamp": 1707027014081,
                                                "message": "Invalid serviceOption",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6002,
                                                    "fields": {
                                                        "serviceOption": {
                                                            "rejectedValue": "invalid"
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
                                            """),
              @ExampleObject(
                  summary = "Processing lead time can not be negative or empty",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "3446206f-f16d-4971-98cd-f36b37ac3900",
                                                "timestamp": 1707027036948,
                                                "message": "Processing lead time can not be negative or empty",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001
                                                }
                                            }
                                            """),
              @ExampleObject(
                  summary = "Bad Request - processingTime cannot be null or empty",
                  name = "Bad Request - processingTime",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e20fd12-b8d2-4a1f-a80f-1e5613abc2f0",
                                              "timestamp": 1708002027881,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "processingTime": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "processingTime cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - serviceOption cannot be null or empty",
                  name = "Bad Request - serviceOption",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e20fd12-b8d2-4a1f-a80f-1e5613abc2f0",
                                              "timestamp": 1708002027881,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "serviceOption": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "serviceOption cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - nodeId cannot be null or empty",
                  name = "Bad Request - nodeId",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e20fd12-b8d2-4a1f-a80f-1e5613abc2f0",
                                              "timestamp": 1708002027881,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "nodeId": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "nodeId cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - orgId cannot be null or empty",
                  name = "Bad Request - orgId",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e20fd12-b8d2-4a1f-a80f-1e5613abc2f0",
                                              "timestamp": 1708002027881,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "orgId": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "orgId cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """)
            }))
public @interface CreateNodeServiceOptionDoc {}
