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
    summary = "Get Node Service Option",
    description = "Fetches details of an existing node service option for the given request")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node service option is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeServiceOptionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Service Option fetched successfully",
                  name = "Node Service Option Fetched",
                  value =
                      """
                                              {
                                                  "success": true,
                                                  "requestId": "4affafdb-eada-41ac-a52f-fba422c6d05a",
                                                  "timestamp": 1707027426324,
                                                  "message": "Node Service Option fetched successfully",
                                                  "payload": {
                                                      "nodeId": "N700",
                                                      "orgId": "NEXTUPLE_GR",
                                                      "serviceOption": "SDND",
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
                                                  "requestId": "b99fc8df-8c66-43ae-aa8d-4fec0a6f81da",
                                                  "timestamp": 1707027415599,
                                                  "message": "Node Carrier Service Option not found for given details",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6003,
                                                      "fields": {
                                                          "serviceOption": {
                                                              "rejectedValue": "SDND"
                                                          },
                                                          "nodeId": {
                                                              "rejectedValue": "N7070"
                                                          },
                                                          "orgId": {
                                                              "rejectedValue": "NEXTUPLE_GR"
                                                          }
                                                      }
                                                  }
                                              }
                                              """)
            }))
public @interface GetNodeServiceOptionDoc {}
