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
    summary = "Delete Cost Itinerary And Cost Factors Mapping",
    description = "Delete Cost Itinerary And Cost Factors Mapping for a given organization ID.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Itinerary And Cost Factors Mapping is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Itinerary And Cost Factors Mapping is successfully deleted.",
                  name =
                      "A 200 indicates that the Cost Itinerary And Cost Factors Mapping is successfully deleted.",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "e2c2f9e2-7c36-45a6-ba61-39a2f0f5c196#688",
                                                       "timestamp": 1702029408813,
                                                       "message": "Cost Itinerary & Cost Factors details deleted successfully!",
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
    description = "When Cost Itinerary And Cost Factors Mapping not found for given OrgId and ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Itinerary And Cost Factors Mapping not found for given OrgId and ID.",
                  name =
                      "A 404 error code indicates that Cost Itinerary And Cost Factors Mapping not found.",
                  value =
                      """
                                              {
                                                    "success": false,
                                                    "requestId": "e2c2f9e2-7c36-45a6-ba61-39a2f0f5c196#692",
                                                    "timestamp": 1702029449911,
                                                    "message": "Cost Itinerary And Cost Factors Mapping not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": 3197
                                                            }
                                                        }
                                                    }
                                                }
                                        """)
            }))
@ApiResponse(
    responseCode = "409",
    description = "When Cost itinerary is associated in cost value table and cannot be deleted.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Cost itinerary is associated in cost value table and cannot be deleted.",
                  name =
                      "A 409 error code indicates that the Cost itinerary is associated in cost value table and cannot be deleted.",
                  value =
                      """
                                            {
                                                 "success": false,
                                                 "requestId": "e2c2f9e2-7c36-45a6-ba61-39a2f0f5c196#697",
                                                 "timestamp": 1702029489293,
                                                 "message": "Cost itinerary is associated in cost value table and cannot be deleted",
                                                 "payload": {
                                                     "type": "ERROR",
                                                     "code": 6001,
                                                     "fields": {
                                                         "orgId": {
                                                             "rejectedValue": "NEXTUPLE_GR"
                                                         },
                                                         "costItinerary": {
                                                             "rejectedValue": "SHIPPING_COST_FEDEX_GROUND"
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
public @interface DeleteCostItineraryAndFactors {}
