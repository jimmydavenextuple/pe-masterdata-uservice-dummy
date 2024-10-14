/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
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
@Operation(summary = "Create Cost Value", description = "Creates a cost value with given details.")
@ApiResponse(
    responseCode = "201",
    description = "When a cost value is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = CostValueResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost value is successfully created.",
                  name = "A 201 response indicates that the cost value is successfully created.",
                  value =
                      """
                                                    {
                                                            "success": true,
                                                            "requestId": "76fae4a9-1044-47f2-be9b-fe80d2ceaaa2",
                                                            "timestamp": 1702034384285,
                                                            "message": "Cost value created successfully.",
                                                            "payload": {
                                                                "id": 4428,
                                                                "orgId": "NEXTUPLE_GR",
                                                                "costItinerary": "TEST_COST_LIKE",
                                                                "costFactorCombinationKey": "FedEx-Ground|Zone16|E3F",
                                                                "costValue": 6.0,
                                                                "prevSlabValue": ""
                                                            }
                                                        }
                                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "When rate card look up for a given cost itinerary is false.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When rate card look up for a given cost itinerary is false.",
                  name =
                      "A 400 error code indicates that the cost value cannot be created because of false rate card look up value.",
                  value =
                      """
                                                  {
                                                          "success": false,
                                                          "requestId": "aecff715-cf54-466a-b182-6ddf55549598",
                                                          "timestamp": 1702024720126,
                                                          "message": "Cost value cannot be added as the rate card look up is not required for the given cost itinerary",
                                                          "payload": {
                                                              "type": "ERROR",
                                                              "code": 6020,
                                                              "fields": {
                                                                  "costItinerary": {
                                                                      "rejectedValue": "TEST_COST_LIKE"
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
    responseCode = "412",
    description = "When Cost value not found for previous slab",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost value not found for previous slab",
                  name = "A 412 response indicates that the Cost value not found for previous slab",
                  value =
                      """
                                            {
                                                 "success": false,
                                                 "requestId": "c1914baf-e664-4fa7-94ea-f6e17f830de2",
                                                 "timestamp": 1702034899329,
                                                 "message": "Cost value not found for previous slab",
                                                 "payload": {
                                                     "type": "ERROR",
                                                     "code": 6001,
                                                     "fields": {
                                                         "orgId": {
                                                             "rejectedValue": "NEXTUPLE_GR"
                                                         },
                                                         "prevSlabValue": {
                                                             "rejectedValue": "UPS-Ground|Zone 4|15<<201"
                                                         },
                                                         "costFactorCombinationKey": {
                                                             "rejectedValue": "UPS-Ground|Zone 4|>20"
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
public @interface CreateCostValueDoc {}
