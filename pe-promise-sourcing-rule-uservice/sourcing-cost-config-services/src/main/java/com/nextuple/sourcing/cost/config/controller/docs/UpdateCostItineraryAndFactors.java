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
    summary = "Update Cost Itinerary And Cost Factors Mapping",
    description = "Updates the Cost Itinerary And Cost Factors Mapping details")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Itinerary And Cost Factors Mapping details are successfully updated.",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Itinerary And Cost Factors Mapping details are successfully updated.",
                  name =
                      "A 200 indicates that the Cost Itinerary And Cost Factors Mapping details are successfully updated.",
                  value =
                      """
                                                {
                                                         "success": true,
                                                         "requestId": "63c90121-7fee-4228-9902-856db99719b7#401",
                                                         "timestamp": 1702026492801,
                                                         "message": "Cost Itinerary & Cost Factors details updated successfully!",
                                                         "payload": {
                                                             "id": 326,
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costItinerary": "SHIPPING_COST_UPS_LIKE_T6",
                                                             "costFactors": "carrierServiceId,surge,zone,billWeightNational",
                                                             "itineraryStatus": "DRAFT",
                                                             "isActive": false,
                                                             "levelApplied": "SHIPMENT"
                                                         }
                                                     }
                                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the Update Cost Itinerary And Cost Factors Mapping request is incorrect."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: When Level applied of cost factor is not valid.</li>"
            + "<li><b>Error code: 6513</b>: If any cost factor associated to a given itinerary has isRateCardLookUp set to false, then other cost factors can't be associated to the same itinerary.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When level applied of cost factor is not valid.",
                  name =
                      "A 400 response indicates that the level applied of cost factor is not valid.",
                  value =
                      """
                                                {
                                                        "success": false,
                                                        "requestId": "aecff715-cf54-466a-b182-6ddf55549598",
                                                        "timestamp": 1702024720126,
                                                        "message": "Level applied of cost factor is not valid",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "levelAppliedOfCostItineraryAndFactors": {
                                                                    "rejectedValue": "ITEM"
                                                                },
                                                                "levelAppliedOfCostFactor": {
                                                                    "rejectedValue": "SHIPMENT"
                                                                },
                                                                "costFactor": {
                                                                    "rejectedValue": "BillWeightUps"
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
                      "If any cost factor associated to a given itinerary has isRateCardLookUp set to false, then other cost factors can't be associated to the same itinerary.",
                  name =
                      "A 400 response indicates that one or more cost factors have isRateCardLookUp set to false.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "ce0b72d0-eb99-44db-a4ac-40efa741f66b",
                                                "timestamp": 1719766519819,
                                                "message": "If any cost factor associated to a given itinerary has isRateCardLookUp set to false, then other cost factors can't be associated to the same itinerary.",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6513,
                                                    "fields": {
                                                        "costFactors": {
                                                            "rejectedValue": "carrierServiceId,zone"
                                                        },
                                                        "costItinerary": {
                                                            "rejectedValue": "SHIPPING_COST_UPS"
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
        "A 404 error code indicates that the record not found with given details."
            + "<ul>"
            + "<li><b>Error code: 2</b>: When cost Factor not found for given orgId.</li>"
            + "<li><b>Error code: 2</b>: When Cost Itinerary And Cost Factors Mapping not found.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost Factor not found for given orgId.",
                  name =
                      "A 404 error code indicates that cost factor was not found for given org Id.",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "e2c2f9e2-7c36-45a6-ba61-39a2f0f5c196#297",
                                                         "timestamp": 1702026135616,
                                                         "message": "Cost Factor not found for given orgId.",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "costFactor": {
                                                                     "rejectedValue": "BillWeightUPS"
                                                                 },
                                                                 "orgId": {
                                                                     "rejectedValue": "NEXTUPLE_GR"
                                                                 }
                                                             }
                                                         }
                                                     }
                                                                    """),
              @ExampleObject(
                  summary = "When Cost Itinerary And Cost Factors Mapping not found.",
                  name =
                      "A 404 error code indicates that Cost Itinerary And Cost Factors Mapping not found.",
                  value =
                      """
                                              {
                                                  "success": false,
                                                  "requestId": "e79e892d-3b42-4936-8cf1-e7b302dc07f0#612",
                                                  "timestamp": 1702026318772,
                                                  "message": "Cost Itinerary And Cost Factors Mapping not found",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6001,
                                                      "fields": {
                                                          "id": {
                                                              "rejectedValue": 3267
                                                          }
                                                      }
                                                  }
                                              }
                                            """)
            }))
@ApiResponse(
    responseCode = "409",
    description =
        "A 404 error code indicates that the record not found with given details."
            + "<ul>"
            + "<li><b>Error code: 2</b>: When Cost Itinerary And Cost Factors Mapping is already in CREATED state and hence cannot be updated.</li>"
            + "<li><b>Error code: 2</b>: When Cost itinerary is associated in cost value table and cannot be updated.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Itinerary And Cost Factors Mapping is already in CREATED state and hence cannot be updated.",
                  name =
                      "A 409 error code indicates that given Cost Itinerary And Cost Factors Mapping is already in CREATED state and hence cannot be updated.",
                  value =
                      """
                                                    {
                                                                 "success": false,
                                                                 "requestId": "f73e4262-7608-4df5-bac4-cc1150e49e89#50",
                                                                 "timestamp": 1702025952713,
                                                                 "message": "Cost itinerary is in CREATED state and cannot be updated.",
                                                                 "payload": {
                                                                     "type": "ERROR",
                                                                     "code": 6001,
                                                                     "fields": {
                                                                         "orgId": {
                                                                             "rejectedValue": "NEXTUPLE_GR"
                                                                         },
                                                                         "costItinerary": {
                                                                             "rejectedValue": "SHIPPING_COST_UPS_LIKE_T2"
                                                                         }
                                                                     }
                                                                 }
                                                             }
                                                """),
              @ExampleObject(
                  summary =
                      "When Cost itinerary is associated in cost value table and cannot be updated.",
                  name =
                      "A 409 error code indicates that the Cost itinerary is associated in cost value table and cannot be updated.",
                  value =
                      """
                                                    {
                                                        "success": false,
                                                        "requestId": "198f7e95-2be9-4170-a482-c5c9904f6a9e",
                                                        "timestamp": 1702027129872,
                                                        "message": "Cost itinerary is associated in cost value table and cannot be updated",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "costItinerary": {
                                                                    "rejectedValue": "SHIPPING_COST_FEDEX_GROUND"
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
public @interface UpdateCostItineraryAndFactors {}
