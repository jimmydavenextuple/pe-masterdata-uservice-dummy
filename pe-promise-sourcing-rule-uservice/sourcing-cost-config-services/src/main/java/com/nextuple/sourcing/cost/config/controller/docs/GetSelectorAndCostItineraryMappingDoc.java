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
    summary = "Get Selector and Cost Itinerary Mapping ",
    description =
        "Retrieves the selector and cost itinerary mapping using the organization ID and the mapping ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a selector and cost itinerary mapping is successfully fetched for given organization ID and mapping Id.",
    content =
        @Content(
            schema = @Schema(implementation = SelectorAndCostItineraryMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a selector and cost itinerary mapping is successfully fetched for given orgId and Id.",
                  name =
                      "A 200 indicates the selector and cost itinerary mapping is successfully fetched for given orgId and Id.",
                  value =
                      """
                                                {
                                                        "success": true,
                                                        "requestId": "5b95326e-1862-4e6b-ae94-f1fed111da2b#519",
                                                        "timestamp": 1702020120992,
                                                        "message": "Selector and Cost Itinerary Mapping fetched successfully.",
                                                        "payload": {
                                                            "id": 229,
                                                            "orgId": "NEXTUPLE_GR",
                                                            "selectorCf": "carrierServiceId",
                                                            "selectorCfValue": "USPS-Standard",
                                                            "costItinerary": "SHIPPING_COST_USPS",
                                                            "costType": "SHIPPING_COST"
                                                        }
                                                    }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve selector and cost itinerary mapping is not valid."
            + "<li><b>Error code: 6001 </b></li><ul><li>Selector and cost itinerary mapping is not found for given organization ID and mapping Id.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a selector and cost itinerary mapping is not found for given orgId and Id.",
                  name =
                      "A 404 indicates that the selector and cost itinerary mapping is not found for given orgId and Id.",
                  value =
                      """
                                                           {
                                                                 "success": false,
                                                                 "requestId": "5b95326e-1862-4e6b-ae94-f1fed111da2b#503",
                                                                 "timestamp": 1702020017056,
                                                                 "message": "Selector Itinerary Mapping not found with given details",
                                                                 "payload": {
                                                                     "type": "ERROR",
                                                                     "code": 6001,
                                                                     "fields": {
                                                                         "id": {
                                                                             "rejectedValue": 7
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
public @interface GetSelectorAndCostItineraryMappingDoc {}
