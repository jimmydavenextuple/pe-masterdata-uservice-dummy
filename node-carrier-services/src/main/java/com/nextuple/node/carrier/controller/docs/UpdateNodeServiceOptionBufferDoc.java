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
    summary = "Update Node Service Option Buffer",
    description = "Update a node service option buffer by given orgId and Id")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option buffer is updated successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option buffer successfully updated",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "3625abfa-81d4-4e89-ace5-cff8149df86c",
                                                    "timestamp": 1707892350077,
                                                    "message": "Node service option buffer updated successfully",
                                                    "payload": {
                                                        "id": 3,
                                                        "nodeId": "N104",
                                                        "orgId": "XYZINC",
                                                        "serviceOption": "EXPRESS",
                                                        "bufferHours": 4.0,
                                                        "bufferEndDate": "2024-01-24T09:59:01.000+00:00",
                                                        "bufferStartDate": "2024-01-23T00:00:01.000+00:00"
                                                    }
                                                }

                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that node service option buffer details not found for the given buffer.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option buffer details not found for the given buffer",
                  value =
                      """
                                  {
                                      "success": false,
                                      "requestId": "fedd32b8-925e-475e-b805-5c11b1386636",
                                      "timestamp": 1707892407351,
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
    responseCode = "400",
    description = "A 400 error code indicates that there is an issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
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
                  summary =
                      "Either both bufferStartDate and bufferEndDate should be null else both should have some value defined",
                  value =
                      """
                                        {
                                                       "success": false,
                                                       "requestId": "162828c6-e656-410c-ba54-0395da029c2c",
                                                       "timestamp": 1707892882573,
                                                       "message": "Either both bufferStartDate and bufferEndDate should be null else both should have some value defined",
                                                       "payload": {
                                                           "type": "ERROR",
                                                           "code": 6005
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
public @interface UpdateNodeServiceOptionBufferDoc {}
