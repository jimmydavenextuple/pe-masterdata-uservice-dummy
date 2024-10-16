/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
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
    summary = "Update Optimization and Cost Types Mapping",
    description =
        "Updates Optimization and Cost Types Mapping by Organization ID and ID with given details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the Optimization and Cost Types Mapping is successfully updated.",
    content =
        @Content(
            schema = @Schema(implementation = OptimizationAndCostTypesMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Optimization and Cost Types Mapping is successfully updated.",
                  name = "Optimization and Cost Types Mapping updated.",
                  value =
                      """
                                                        {
                                                              "success": true,
                                                              "requestId": "c28d67ca-3f66-4317-93a6-26c18b072bc6",
                                                              "timestamp": 1702037199904,
                                                              "message": "Optimization And Cost Types Mapping updated successfully!",
                                                              "payload": {
                                                                  "id": 246,
                                                                  "orgId": "NEXTUPLE_GR",
                                                                  "optimizationStrategy": "COST",
                                                                  "costTypes": "BUYING_COST,SHIPPING_COST,NODE_PROCESSING_COST ,SALES_REVENUE,SHIP_REVENUE",
                                                                  "description": "(salesRevenue+shipRevenue)- (shippingCost + nodeProcessingCost + buyingCost)",
                                                                  "javaClassName": "com.nextuple.promise.sourcing.impl.ProfitCalculationImpl"
                                                              }
                                                          }
                                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the update Optimization and Cost Types Mapping request is incorrect."
            + "<ul>"
            + "<li><b>Error code: 6261</b>:Cost Type(s) do not exist with given Organization ID.</li>"
            + "<li><b>Error code: 6262</b>:Only cost types can be mapped to COST based optimization strategy.</li>"
            + "<li><b>Error code: 6263</b>:Both cost and revenue types should be mapped to PROFIT based optimization strategy.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Cost Type(s) do not exist with given Organization ID.",
                  name =
                      "A 400 error code indicates that the Cost Type(s) do not exist with given Organization ID.",
                  value =
                      """
                                                    {
                                                           "success": false,
                                                           "requestId": "614bc78d-541f-4cf4-99e6-26059ab1a12a",
                                                           "timestamp": 1702036565091,
                                                           "message": "Cost types do not exist with given org id",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6261,
                                                               "fields": {
                                                                   "costTypes": {
                                                                       "rejectedValue": "BUYING_COST"
                                                                   },
                                                                   "orgId": {
                                                                       "rejectedValue": "NEXTUPLE_GR"
                                                                   }
                                                               }
                                                           }
                                                       }
                                                """),
              @ExampleObject(
                  summary = "Only cost types can be mapped to COST based optimization strategy.",
                  name =
                      "A 400 error code indicates that only cost types can be mapped to COST based optimization strategy.",
                  value =
                      """
                                                      {
                                                        "success": false,
                                                        "requestId": "7bd885fe-6518-4f61-b4a6-364be1dfed2f",
                                                        "timestamp": 1720723804263,
                                                        "message": "Only cost types can be mapped to COST based optimization strategy.",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6262,
                                                            "fields": {
                                                                "costTypes": {
                                                                    "rejectedValue": "SHIP_REVENUE,SHIPPING_COST"
                                                                },
                                                                "orgId": {
                                                                    "rejectedValue": "NEXTUPLE_GR"
                                                                }
                                                            }
                                                        }
                                                    }
                                                              """),
              @ExampleObject(
                  summary =
                      "Both cost and revenue types should be mapped to PROFIT based optimization strategy.",
                  name =
                      "A 400 error code indicates that both cost and revenue types should be mapped to PROFIT based optimization strategy.",
                  value =
                      """
                                            {
                                                    "success": false,
                                                    "requestId": "8689abe4-2dd4-4bcb-9188-bfc3ea9ded12",
                                                    "timestamp": 1720723973660,
                                                    "message": "Both cost and revenue types should be mapped to PROFIT based optimization strategy.",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6263,
                                                        "fields": {
                                                            "costTypes": {
                                                                "rejectedValue": "SHIP_REVENUE"
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
    responseCode = "404",
    description =
        "A 404 error code indicates that the Optimization and Cost Types Mapping not found for given Organization ID and ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Optimization and Cost Types Mapping not found for given Organization ID and ID.",
                  name = "Optimization and Cost Types Mapping not found.",
                  value =
                      """
                                                    {
                                                           "success": false,
                                                           "requestId": "614bc78d-541f-4cf4-99e6-26059ab1a12a",
                                                           "timestamp": 1702036565091,
                                                           "message": "Optimization and cost type mapping not found with org id and id",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6257,
                                                               "fields": {
                                                                   "id": {
                                                                       "rejectedValue": 40822
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
                                                    "requestId": "3a822137-8ad5-4aa6-abe9-11836d06f56f",
                                                    "timestamp": 1698044027078,
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2
                                                    }
                                                }""")
            }))
public @interface UpdateOptimizationAndCostTypesMappingByIdAndOrgIdDoc {}
