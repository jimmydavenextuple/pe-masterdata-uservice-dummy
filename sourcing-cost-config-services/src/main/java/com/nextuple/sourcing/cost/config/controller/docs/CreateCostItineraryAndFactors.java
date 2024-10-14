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
    summary = "Create Cost Itinerary And Cost Factors Mapping",
    description = "Create Cost Itinerary And Cost Factors Mapping for a given organization ID.")
@ApiResponse(
    responseCode = "201",
    description = "When Create Cost Itinerary And Cost Factors Mapping is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Create Cost Itinerary And Cost Factors Mapping is successfully created.",
                  name =
                      "A 201 Created response indicates that the Cost Itinerary And Cost Factors Mapping is successfully created.",
                  value =
                      """
                                                {
                                                        "success": true,
                                                        "requestId": "b7b145a5-666d-4534-950b-472c31f87eb2",
                                                        "timestamp": 1702019718610,
                                                        "message": "Cost Itinerary & Cost Factors Mapping created successfully!",
                                                        "payload": {
                                                            "id": 323,
                                                            "orgId": "NEXTUPLE_GR",
                                                            "costItinerary": "SHIPPING_COST_UPS_LIKE_T3",
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
        "A 400 error code indicates that the create Create Cost Itinerary And Cost Factors Mapping request is incorrect."
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
                      "If any cost factor has isRateCardLookUp set to false, only one cost factor is allowed.",
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
    description = "A 404 error code indicates that cost factor was not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost factor not found.",
                  name =
                      "A 404 error code indicates that cost factor was not found for given org Id.",
                  value =
                      """
                                                {
                                                        "success": false,
                                                        "requestId": "5b408ea2-9155-44c3-882f-3d747f9c4a25",
                                                        "timestamp": 1702024274279,
                                                        "message": "Cost Factor not found for given orgId.",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "costFactor": {
                                                                    "rejectedValue": "billWeightUps"
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
    responseCode = "409",
    description = "When cost itinerary in the request is already added.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary in the request is already added.",
                  name =
                      "A 409 error code indicates that cost itinerary in the request is already added.",
                  value =
                      """
                                                    {
                                                            "success": false,
                                                            "requestId": "96c88fb0-ff4e-43d3-9c87-7cd77626724c",
                                                            "timestamp": 1702024369299,
                                                            "message": "Duplicate cost itinerary not allowed for given orgId.",
                                                            "payload": {
                                                                "type": "ERROR",
                                                                "code": 6001,
                                                                "fields": {
                                                                    "costItinerary": {
                                                                        "rejectedValue": "SHIPPING_COST_USPS"
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
public @interface CreateCostItineraryAndFactors {}
