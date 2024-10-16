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
    summary = "Get Cost Itinerary And Cost Factors Mapping By Org Id And Id",
    description = "Get Cost Itinerary And Cost Factors Mapping for a given Org Id And Id")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Itinerary And Cost Factors Mapping details are successfully fetched for a given org Id and Id.",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Itinerary And Cost Factors Mapping details are successfully fetched for a given org Id and Id.",
                  name =
                      "A 200 OK response indicates that the Cost Itinerary And Cost Factors Mapping details are successfully fetched for a given org Id and Id.",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "63c90121-7fee-4228-9902-856db99719b7#211",
                                                     "timestamp": 1702024983008,
                                                     "message": "Cost Itinerary & Cost Factors fetched successfully!",
                                                     "payload": {
                                                         "id": 319,
                                                         "orgId": "NEXTUPLE_GR",
                                                         "costItinerary": "SHIPPING_COST_UPS_LIKE_T2",
                                                         "costFactors": "carrierServiceId,surge,zone,billWeightNational",
                                                         "itineraryStatus": "CREATED",
                                                         "isActive": false,
                                                         "levelApplied": "SHIPMENT"
                                                     }
                                                 }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Itinerary And Cost Factors Mapping record not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a given Cost Itinerary And Cost Factors Mapping record not found.",
                  name =
                      "A 404 error code indicates that the given Cost Itinerary And Cost Factors Mapping record not found.",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "e2c2f9e2-7c36-45a6-ba61-39a2f0f5c196#152",
                                                         "timestamp": 1702025127934,
                                                         "message": "Cost Itinerary And Cost Factors Mapping not found",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "id": {
                                                                     "rejectedValue": 3191
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
public @interface GetCostItineraryAndFactorsByOrgIdAndId {}
