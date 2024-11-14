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
    summary = "Delete Node Service Option",
    description = "Deletes an existing node service option for the given request")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option is deleted successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option deleted successfully",
                  name = "Node Service Option Deleted",
                  value =
                      """
                                              {
                                                  "success": true,
                                                  "requestId": "80e5b41a-20c9-4b96-8ea9-92ac9cee322f",
                                                  "timestamp": 1707027476493,
                                                  "message": "Node Service Option deleted successfully",
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
                                                  "requestId": "94e03013-7bb7-4a8e-942b-c7c59661bf3e",
                                                  "timestamp": 1707027496460,
                                                  "message": "Node Carrier Service Option not found for given details",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6003,
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
                                              """)
            }))
public @interface DeleteNodeServiceOptionDoc {}
