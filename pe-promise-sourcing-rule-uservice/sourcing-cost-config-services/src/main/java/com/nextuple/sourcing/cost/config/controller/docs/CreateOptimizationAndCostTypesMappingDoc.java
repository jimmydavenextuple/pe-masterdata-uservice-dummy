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
    summary = "Create Optimization and Cost Types Mapping",
    description = "Creates a Optimization and Cost Types Mapping for a given Organization ID.")
@ApiResponse(
    responseCode = "201",
    description =
        "A 201 success code indicates that Optimization and Cost Types Mapping is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = OptimizationAndCostTypesMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Optimization and Cost Types Mapping is successfully created.",
                  name = "Optimization and Cost Types Mapping created.",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "c7d82dbf-1f5b-47c1-8d83-8c346b9ea89a",
                                                     "timestamp": 1701767128015,
                                                     "message": "Optimization And Cost Types Mapping created successfully!",
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
        "A 400 error code indicates that the create Optimization and Cost Types Mapping request is incorrect."
            + "<ul>"
            + "<li><b>Error code: 6259</b>:An Optimization and Cost Types Mapping already exists for the given Organization ID and Optimization Strategy.</li>"
            + "<li><b>Error code: 6260</b>:Optimization strategy does not exist with given Organization ID.</li>"
            + "<li><b>Error code: 6261</b>:Cost Type(s) do not exist with given Organization ID.</li>"
            + "<li><b>Error code: 6262</b>:Only cost types can be mapped to COST based optimization strategy.</li>"
            + "<li><b>Error code: 6263</b>:Both cost and revenue types should be mapped to PROFIT based optimization strategy.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Given Organization ID and Optimization Strategy are not unique.",
                  name =
                      "A 400 error code indicates that the Optimization And Cost Types Mapping exists for given Organization ID and Optimization Strategy.",
                  value =
                      """
                                                    {
                                                           "success": false,
                                                           "requestId": "614bc78d-541f-4cf4-99e6-26059ab1a12a",
                                                           "timestamp": 1702036565091,
                                                           "message": "Optimization and cost type mapping exists with given org id and optimization strategy",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6259,
                                                               "fields": {
                                                                   "optimizationStrategy": {
                                                                       "rejectedValue": "COST"
                                                                   },
                                                                   "orgId": {
                                                                       "rejectedValue": "NEXTUPLE_GR"
                                                                   }
                                                               }
                                                           }
                                                       }
                                                """),
              @ExampleObject(
                  summary = "Optimization strategy does not exist with given Organization ID.",
                  name =
                      "A 400 error code indicates that the Optimization strategy does not exist with given Organization ID.",
                  value =
                      """
                                                    {
                                                           "success": false,
                                                           "requestId": "614bc78d-541f-4cf4-99e6-26059ab1a12a",
                                                           "timestamp": 1702036565091,
                                                           "message": "Optimization strategy does not exist with given org id",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6260,
                                                               "fields": {
                                                                   "optimizationStrategy": {
                                                                       "rejectedValue": "COST"
                                                                   },
                                                                   "orgId": {
                                                                       "rejectedValue": "NEXTUPLE_GR"
                                                                   }
                                                               }
                                                           }
                                                       }
                                                """),
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
                                                        "requestId": "d2c275c6-8284-461c-a512-ce888d200b55",
                                                        "timestamp": 1720723526201,
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
                                                        "requestId": "f654f32a-a0cc-4a36-8617-335521916434",
                                                        "timestamp": 1720723558138,
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
public @interface CreateOptimizationAndCostTypesMappingDoc {}
