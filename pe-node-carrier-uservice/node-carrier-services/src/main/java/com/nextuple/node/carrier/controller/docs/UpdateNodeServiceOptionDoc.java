/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
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
    summary = "Update Node Service Option",
    description = "Updates an existing node service option for the given request")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option is updated successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option updated successfully",
                  name = "Node Service Option Updated",
                  value =
                      """
                                              {
                                                  "success": true,
                                                  "requestId": "c2c1c353-8e14-46cc-8397-6fad15155aaf",
                                                  "timestamp": 1707027292153,
                                                  "message": "Node Service Option updated successfully",
                                                  "payload": {
                                                      "nodeId": "N700",
                                                      "orgId": "NEXTUPLE_GR",
                                                      "serviceOption": "EXPRESS",
                                                      "processingTime": 3.0
                                                  }
                                              }
                                              """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the node service option is not found for the given details.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier Service Option not found",
                  name = "Node Carrier Service Option Not Found",
                  value =
                      """
                                              {
                                                  "success": false,
                                                  "requestId": "dd980a85-1c62-4390-b83e-da9dccf3133c",
                                                  "timestamp": 1707027304265,
                                                  "message": "Node Carrier Service Option not found for given details",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6003,
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
                                              """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates a bad request with issues in the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Bad Request - Invalid processingTime",
                  name = "Bad Request - Invalid processingTime",
                  value =
                      """
                                              {
                                                  "success": false,
                                                  "requestId": "1580e430-a28f-4e7d-86ac-940a26185b63",
                                                  "timestamp": 1707027325115,
                                                  "message": "Bad Request",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 2,
                                                      "fields": {
                                                          "processingTime": {
                                                              "rejectedValue": "-3.0",
                                                              "errorMessage": "processingTime must be greater than or equal to 0.0"
                                                          }
                                                      }
                                                  }
                                              }
                                              """)
            }))
public @interface UpdateNodeServiceOptionDoc {}
