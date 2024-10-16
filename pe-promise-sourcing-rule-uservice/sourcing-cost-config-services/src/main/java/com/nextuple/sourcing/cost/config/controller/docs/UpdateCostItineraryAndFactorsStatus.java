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
    summary = "Update Cost Itinerary And Cost Factors Mapping's Itinerary Status",
    description = "Updates the Cost Itinerary And Cost Factors Mapping's Itinerary Status")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Itinerary And Cost Factors Mapping's Itinerary Status is successfully updated.",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Itinerary And Cost Factors Mapping's Itinerary Status is successfully updated.",
                  name =
                      "A 200 indicates that the Cost Itinerary And Cost Factors Mapping's Itinerary Status is successfully updated.",
                  value =
                      """
                                                {
                                                      "success": true,
                                                      "requestId": "f73e4262-7608-4df5-bac4-cc1150e49e89#152",
                                                      "timestamp": 1702027986771,
                                                      "message": "Cost Itinerary & Cost Factors itinerary status updated successfully!",
                                                      "payload": {
                                                          "id": 324,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "costItinerary": "SHIPPING_COST_UPS_LIKE_T1",
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
    description = "When Cost Itinerary And Cost Factors Mapping not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Itinerary And Cost Factors Mapping not found.",
                  name =
                      "A 404 error code indicates that Cost Itinerary And Cost Factors Mapping not found.",
                  value =
                      """
                                                  {
                                                           "success": false,
                                                           "requestId": "63c90121-7fee-4228-9902-856db99719b7#512",
                                                           "timestamp": 1702028314478,
                                                           "message": "Cost Itinerary And Cost Factors Mapping not found",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6001,
                                                               "fields": {
                                                                   "id": {
                                                                       "rejectedValue": 3244
                                                                   }
                                                               }
                                                           }
                                                       }
                                                """)
            }))
@ApiResponse(
    responseCode = "409",
    description =
        "When Cost Itinerary And Cost Factors Mapping is already in CREATED state and hence cannot be updated.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Cost itinerary is in CREATED state and cannot be updated.",
                  name =
                      "A 409 error code indicates that given Cost Itinerary And Cost Factors Mapping is already in CREATED state and hence cannot be updated.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "63c90121-7fee-4228-9902-856db99719b7#497",
                                                    "timestamp": 1702028083772,
                                                    "message": "Cost itinerary is in CREATED state and cannot be updated.",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            },
                                                            "costItinerary": {
                                                                "rejectedValue": "SHIPPING_COST_UPS_LIKE_T1"
                                                            }
                                                        }
                                                    }
                                                }
                                            """)
            }))
@ApiResponse(
    responseCode = "412",
    description = "When cost itinerary requested to update status has issues in request",
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
                                                   "requestId": "076b3ef0-3b86-4248-811a-38915422c493#10",
                                                   "timestamp": 1702028727240,
                                                   "message": "Bucket type not found for orgId and Cost Factor combination.",
                                                   "payload": {
                                                       "type": "ERROR",
                                                       "code": 6001,
                                                       "fields": {
                                                           "costFactor": {
                                                               "rejectedValue": "BillWeightUps"
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
public @interface UpdateCostItineraryAndFactorsStatus {}
