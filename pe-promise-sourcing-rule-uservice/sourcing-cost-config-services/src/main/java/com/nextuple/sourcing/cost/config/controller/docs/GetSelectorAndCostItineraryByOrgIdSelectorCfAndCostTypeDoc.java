/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;
import java.util.List;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Selector and Cost Itinerary Mapping",
    description =
        "Retrieves a selector and cost itinerary mapping by organization ID, selector cost factor and cost type.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a selector and cost itinerary mapping is successfully fetched for given organization ID, selector cost factor and cost type.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary = "When selector and cost itinerary mapping is fetched successfully.",
                  name =
                      "A 200 OK response indicates that selector and cost itinerary mapping is fetched successfully.",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "68ffe193-ed54-4e8f-b6a4-b9c3766c4c33",
                                            "timestamp": 1699251027678,
                                            "message": "Selector and Cost Itinerary Mapping fetched successfully.",
                                            "payload": [
                                                {
                                                    "id": 4,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "selectorCf": "carrierServiceId",
                                                    "selectorCfValue": null,
                                                    "costItinerary": "SHIPPING_COST_UPS_LIKE5",
                                                    "costType": "SHIPPING_COST"
                                                },
                                                {
                                                    "id": 2,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "selectorCf": "carrierServiceId",
                                                    "selectorCfValue": "FEDEX_GROUND",
                                                    "costItinerary": "SHIPPING_COST_FEDEX_GROUND",
                                                    "costType": "SHIPPING_COST"
                                                }
                                            ]
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve a selector and cost itinerary mapping for given organization ID, selector cost factor and cost type is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the selector and cost itinerary mapping is not found."
            + "<li><b>Error code: 6001 </b></li><ul><li>Selector and cost itinerary mapping is not found for given organization ID, selector cost factor and cost type.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a selector and cost itinerary mapping is not found.",
                  name =
                      "A 404 response indicates that selector and cost itinerary mapping is not found for given details.",
                  value =
                      """
                                {
                                    "success": false,
                                    "requestId": "3d6de76a-51b9-4757-aca7-ecf77c3de6a0",
                                    "timestamp": 1699251058107,
                                    "message": "Selector Itinerary Mapping not found with given details",
                                    "payload": {
                                        "type": "ERROR",
                                        "code": 6001,
                                        "fields": {
                                            "costType": {
                                                "rejectedValue": "SHIPPING_COST1"
                                            },
                                            "selectorCf": {
                                                "rejectedValue": "carrierServiceId"
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
public @interface GetSelectorAndCostItineraryByOrgIdSelectorCfAndCostTypeDoc {}
