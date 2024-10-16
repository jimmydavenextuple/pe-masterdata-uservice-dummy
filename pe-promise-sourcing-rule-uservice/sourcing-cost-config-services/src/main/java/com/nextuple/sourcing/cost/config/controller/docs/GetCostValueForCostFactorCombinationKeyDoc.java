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
@Operation(
    summary = "Get Cost Value by orgId, costItinerary and costFactorCombinationKey",
    description = "Get cost value by orgId, costItinerary and costFactorCombinationKey")
@ApiResponse(
    responseCode = "200",
    description =
        "When a cost value is successfully fetched for given orgId,costItinerary and costFactorCombinationKey.",
    content =
        @Content(
            schema = @Schema(implementation = CostValueResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a cost value is successfully fetched for given orgId,costItinerary and costFactorCombinationKey.",
                  name =
                      "A 200 response indicates that the cost value is successfully fetched for given orgId,costItinerary and costFactorCombinationKey.",
                  value =
                      """
                                                {
                                                      "success": true,
                                                      "requestId": "672b4a12-8371-440b-b26e-36f1b9e7d8bc",
                                                      "timestamp": 1702036391364,
                                                      "message": "Cost value fetched successfully.",
                                                      "payload": {
                                                          "id": 246,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "costItinerary": "SHIPPING_COST_UPS_GROUND",
                                                          "costFactorCombinationKey": "UPS-Ground|Zone 4|>20",
                                                          "costValue": 6.0,
                                                          "prevSlabValue": "UPS-Ground|Zone 4|15<<20"
                                                      }
                                                  }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "When cost value not found for given orgId,costItinerary and costFactorCombinationKey.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When cost value not found for given orgId,costItinerary and costFactorCombinationKey.",
                  name =
                      "A 404 response indicates that the cost value not found for given orgId,costItinerary and costFactorCombinationKey.",
                  value =
                      """
                                            {
                                                   "success": false,
                                                   "requestId": "e6de60af-5314-4cbc-8059-96245b80be83",
                                                   "timestamp": 1702036305298,
                                                   "message": "Cost value not found for given details",
                                                   "payload": {
                                                       "type": "ERROR",
                                                       "code": 6001,
                                                       "fields": {
                                                           "costFactorCombinationKey": {
                                                               "rejectedValue": "FEDEX_GROUND|NON_HOLIDAYS|Z3|XL"
                                                           },
                                                           "costItinerary": {
                                                               "rejectedValue": "SHIPPING_COST_UPS_GROUND"
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
public @interface GetCostValueForCostFactorCombinationKeyDoc {}
