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
    summary = "Create Node Service Option Buffer",
    description = "Creates a node service option buffer based on the provided details")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option buffer is created successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option buffer successfully created",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "edcbaa62-2e5a-46b0-a3ed-1c79b1e34b94",
                                                     "timestamp": 1707888999617,
                                                     "message": "Node service option buffer created successfully",
                                                     "payload": {
                                                         "id": 29,
                                                         "nodeId": "N104",
                                                         "orgId": "XYZINC",
                                                         "serviceOption": "EXPRESS",
                                                         "bufferHours": 2.0,
                                                         "bufferEndDate": "2025-02-15T00:00:00.000+00:00",
                                                         "bufferStartDate": "2025-02-10T00:00:00.000+00:00"
                                                     }
                                                 }
                                                              """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that node service option details not found for the given buffer.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option details not found for the given buffer",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "eafbf1dd-7727-493d-a502-bb114bbf0e5a",
                                                     "timestamp": 1707889831052,
                                                     "message": "Node Service Option details not found for the given buffer",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6009,
                                                         "fields": {
                                                             "serviceOption": {
                                                                 "rejectedValue": "NEXT"
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
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is an issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "BufferEndDate should be greater than or equal to BufferStartDate",
                  value =
                      """
                                  {
                                        "success": false,
                                        "requestId": "fec1f672-c6d3-4087-89a0-368c8d4339b3",
                                        "timestamp": 1707889988008,
                                        "message": "BufferEndDate should be greater than or equal to BufferStartDate",
                                        "payload": {
                                            "type": "ERROR",
                                            "code": 6016
                                        }
                                    }
                """),
              @ExampleObject(
                  summary = "bufferHours cannot be negative",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "fec1f672-c6d3-4087-89a0-368c8d4339b3",
                                                      "timestamp": 1707889988008,
                                                      "message": "bufferHours cannot be negative",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6016
                                                      }
                                                  }
                                                              """),
              @ExampleObject(
                  summary = "bufferHours cannot be empty",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "82fca828-f9cf-4b8c-853d-5df5fe04e081",
                                                "timestamp": 1707890617199,
                                                "message": "Bad Request",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 2,
                                                    "fields": {
                                                        "bufferHours": {
                                                            "rejectedValue": "null",
                                                            "errorMessage": "bufferHours cannot be empty"
                                                        }
                                                    }
                                                }
                                            }
                    """),
              @ExampleObject(
                  summary = "BufferEndDate should be greater than or equal to bufferStartDate",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "658fee0f-c2f5-4cc9-89f1-d26ff512b5b0",
                                                "timestamp": 1707890800933,
                                                "message": "bufferEndDate should be greater than or equal to bufferStartDate",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6005
                                                }
                                            }

                                 """),
              @ExampleObject(
                  summary = "BufferEndDate cannot be empty",
                  value =
                      """
                                    {
                                       "success": false,
                                       "requestId": "cc9984a8-402f-4246-8635-ddad6b442e92",
                                       "timestamp": 1707890893632,
                                       "message": "Bad Request",
                                       "payload": {
                                           "type": "ERROR",
                                           "code": 2,
                                           "fields": {
                                               "bufferEndDate": {
                                                   "rejectedValue": "null",
                                                   "errorMessage": "bufferEndDate cannot be empty"
                                               }
                                           }
                                       }
                                   }
                                 """),
              @ExampleObject(
                  summary = "BufferStartDate cannot be empty",
                  value =
                      """
                                    {
                                       "success": false,
                                       "requestId": "23692538-b563-4179-9c6e-ec0c19ec3344",
                                       "timestamp": 1707890983848,
                                       "message": "Bad Request",
                                       "payload": {
                                           "type": "ERROR",
                                           "code": 2,
                                           "fields": {
                                               "bufferStartDate": {
                                                   "rejectedValue": "null",
                                                   "errorMessage": "bufferStartDate cannot be empty"
                                               }
                                           }
                                       }
                                   }
                                 """),
              @ExampleObject(
                  summary = "BufferStartDate and bufferEndDate cannot be empty",
                  value =
                      """
                                    {
                                                   "success": false,
                                                   "requestId": "79637097-4c24-4dc5-b59e-cd87790cefa8",
                                                   "timestamp": 1707891044790,
                                                   "message": "Bad Request",
                                                   "payload": {
                                                       "type": "ERROR",
                                                       "code": 2,
                                                       "fields": {
                                                           "bufferStartDate": {
                                                               "rejectedValue": "null",
                                                               "errorMessage": "bufferStartDate cannot be empty"
                                                           },
                                                           "bufferEndDate": {
                                                               "rejectedValue": "null",
                                                               "errorMessage": "bufferEndDate cannot be empty"
                                                           }
                                                       }
                                                   }
                                               }
                                 """)
            }))
@ApiResponse(
    responseCode = "412",
    description =
        "A 412 error code indicates that node service option buffer window already exists or overlaps",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option Buffer window already exists or overlaps",
                  value =
                      """
                              {
                                  "success": false,
                                  "requestId": "0e8418e6-f06a-4102-8edf-e98dd4391db1",
                                  "timestamp": 1707890235906,
                                  "message": "Node Service Option Buffer window already exists or overlaps",
                                  "payload": {
                                      "type": "ERROR",
                                      "code": 6019,
                                      "fields": {
                                          "bufferStartDate": {
                                              "rejectedValue": "2025-02-10T00:00:00.000+00:00"
                                          },
                                          "bufferEndDate": {
                                              "rejectedValue": "2025-02-15T00:00:00.000+00:00"
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
public @interface CreateNodeServiceOptionBufferDoc {}
