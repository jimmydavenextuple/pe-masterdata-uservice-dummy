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
@Operation(summary = "Deletes Cost Value", description = "Deletes a cost value for given details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that Cost Value for given OrgId, Cost Itinerary and Cost Factor Combination Key is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = CostValueResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost value is successfully deleted.",
                  name = "A 200 OK for delete cost value.",
                  value =
                      """
                        {
                             "success": true,
                             "requestId": "6ae6e115-abd4-4063-b036-7589d9b84d6a#95",
                             "timestamp": 1697806857701,
                             "message": "Cost value deleted successfully.",
                             "payload": {
                                 "id": 12,
                                 "orgId": "NEXTUPLE_GR",
                                 "costItinerary": "SHIPPING_COST_FEDEXLIKE",
                                 "costFactorCombinationKey": "SHIPPING_COST|123",
                                 "costValue": 10.01,
                                 "prevSlabValue": ""
                             }
                         }""")
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that cost value not found for given orgId,costItinerary and costFactorCombinationKey",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost value requested to delete is not found.",
                  name = "A 404 Not Found for delete cost value.",
                  value =
                      """
                        {
                              "success": false,
                              "requestId": "cb195710-77a3-460f-941f-7068835a465a",
                              "timestamp": 1698393437190,
                              "message": "Cost value not found for given details",
                              "payload": {
                                  "type": "ERROR",
                                  "code": 6001,
                                  "fields": {
                                      "costFactorCombinationKey": {
                                          "rejectedValue": "invalid"
                                      },
                                      "costItinerary": {
                                          "rejectedValue": "SHIPPING_COST_FEDEXLIKE"
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
                      }
                    """)
            }))
public @interface DeleteCostValueForCostFactorCombinationKeyDoc {}
