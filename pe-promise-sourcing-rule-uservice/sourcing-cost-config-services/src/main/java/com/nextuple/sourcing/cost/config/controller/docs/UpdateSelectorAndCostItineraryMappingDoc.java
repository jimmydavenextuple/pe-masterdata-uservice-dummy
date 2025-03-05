/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
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
    summary = "Update Selector and Cost Itinerary Mapping.",
    description = "Updates a selector and cost itinerary mapping with given details.")
@ApiResponse(
    responseCode = "200",
    description = "When a selector and cost itinerary mapping is successfully updated",
    content =
        @Content(
            schema = @Schema(implementation = SelectorAndCostItineraryMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a selector and cost itinerary mapping is successfully updated",
                  name = "A 200 OK for updating cost itinerary.",
                  value =
                      """
                        {
                              "success": true,
                              "requestId": "31cc1820-3c4a-4ab7-8eb8-fda5c593f0e3",
                              "timestamp": 1701070336741,
                              "message": "Selector and cost itinerary updated successfully!",
                              "payload": {
                                  "id": 218,
                                  "orgId": "NEXTUPLE_GR_T1",
                                  "selectorCf": "BillWeightUps1",
                                  "selectorCfValue": "M",
                                  "costItinerary": "SHIPPING_COST_NATIONAL_LIKE_T3",
                                  "costType": "SHIPPING_COST",
                                   "customAttributes": {
                                        "dynamicAtrr1": true
                                    }
                              }
                          }""")
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that request contains invalid data.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When update selector and cost itinerary has issues in request.",
                  name = "A 400 issues when updating selector and cost itinerary for DRAFT status",
                  value =
                      """
                {
                    "success": false,
                    "requestId": "ae825d42-467a-4f4e-bbc2-fa04ab13ac1b",
                    "timestamp": 1701074644714,
                    "message": "Can't associate itinerary with DRAFT status",
                    "payload": {
                        "type": "ERROR",
                        "code": 6001,
                        "fields": {
                            "costItinerary": {
                                "rejectedValue": "SHIPPING_COST_NATIONAL_LIKE_T4"
                            },
                            "orgId": {
                                "rejectedValue": "NEXTUPLE_GR_T1"
                            }
                        }
                    }
                }
                   """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that record not found for the given details."
            + "<ul>"
            + "<li><b>Error code: 2</b>: When cost itinerary not found for given org id and cost itinerary</li>"
            + "<li><b>Error code: 2</b>: When selector Itinerary Mapping not found with given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary not found for given org id and cost itinerary",
                  name = "A 404 Not Found for given org id and cost itinerary",
                  value =
                      """
                {
                    "success": false,
                    "requestId": "f5baf207-d82c-4948-a200-0b29870742ec",
                    "timestamp": 1701074420482,
                    "message": "Cost Itinerary and factors not found with given details",
                    "payload": {
                        "type": "ERROR",
                        "code": 6001,
                        "fields": {
                            "orgId": {
                                "rejectedValue": "NEXTUPLE_GR_T1"
                            },
                            "costItinerary": {
                                "rejectedValue": "SHIPPING_COST_NATIONAL_LIKE_T4"
                            }
                        }
                    }
                }
              """),
              @ExampleObject(
                  summary = "Selector Itinerary Mapping not found with given details.",
                  name =
                      "A 404 indicates that the Selector Itinerary Mapping not found with given details.",
                  value =
                      """
                                              {
                                                                  "success": false,
                                                                  "requestId": "417c0487-dff3-45e8-b735-af921907a374#340",
                                                                  "timestamp": 1702021882471,
                                                                  "message": "Selector Itinerary Mapping not found with given details",
                                                                  "payload": {
                                                                      "type": "ERROR",
                                                                      "code": 6001,
                                                                      "fields": {
                                                                          "id": {
                                                                              "rejectedValue": 230
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
public @interface UpdateSelectorAndCostItineraryMappingDoc {}
