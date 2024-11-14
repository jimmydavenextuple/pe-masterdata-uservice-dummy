/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
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
    summary = "Update Cost Itinerary And Cost Factors Mapping's Itinerary Status By Cost Itinerary",
    description =
        "Updates the Cost Itinerary And Cost Factors Mapping's Itinerary Status By Cost Itinerary")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Itinerary And Cost Factors Mapping's Itinerary Status is successfully updated",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary status is successfully updated.",
                  name = "A 200 OK for updating itinerary status.",
                  value =
                      """
                          {
                               "success": true,
                               "requestId": "6ae6e115-abd4-4063-b036-7589d9b84d6a#95",
                               "timestamp": 1697806857701,
                               "message": "Cost Itinerary & Cost Factors itinerary status updated successfully!",
                               "payload": {
                                   "id": 12,
                                   "orgId": "NEXTUPLE_GR",
                                   "costItinerary": "SHIPPING_COST_FEDEXLIKE",
                                   "costFactors": "BillWeightUps",
                                   "itineraryStatus": "CREATED",
                                   "isActive": "false"
                               }
                           }""")
            }))
@ApiResponse(
    responseCode = "412",
    description = "When cost itinerary requested to update status has issues in request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary requested to update status has issues in request.",
                  name = "A 412 issues in request for updating itinerary status.",
                  value =
                      """
                          {
                                "success": false,
                                "requestId": "cb195710-77a3-460f-941f-7068835a465a",
                                "timestamp": 1698393437190,
                                "message": "Bucket configuration not found for cost factor value.",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 6001,
                                    "fields": {
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
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Itinerary And Cost Factors Mapping is not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary requested to update status is not found.",
                  name = "A 404 Not Found for update cost itinerary status.",
                  value =
                      """
                            {
                                  "success": false,
                                  "requestId": "cb195710-77a3-460f-941f-7068835a465a",
                                  "timestamp": 1698393437190,
                                  "message": "Cost Itinerary And Cost Factors Mapping not found",
                                  "payload": {
                                      "type": "ERROR",
                                      "code": 6001,
                                      "fields": {
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
    description = "A 500 error code indicates that something went wrong",
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
public @interface UpdateCostItineraryAndFactorsStatusByCostItinerary {}
