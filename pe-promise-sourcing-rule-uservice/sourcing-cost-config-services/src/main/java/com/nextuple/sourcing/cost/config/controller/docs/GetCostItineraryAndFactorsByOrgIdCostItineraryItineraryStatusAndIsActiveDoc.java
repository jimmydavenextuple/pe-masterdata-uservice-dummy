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
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary =
        "Get Cost Itinerary And Cost Factors Mapping By OrgId, CostItinerary, ItineraryStatus and IsActive",
    description =
        "Get Cost Itinerary And Cost Factors Mapping for given orgId, costItinerary, itineraryStatus and isActive")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Itinerary And Cost Factors Mapping details are successfully fetched for given orgId, costItinerary, itineraryStatus and isActive",
    content =
        @Content(
            schema = @Schema(implementation = CostItineraryAndFactorsDto.class),
            examples = {
              @ExampleObject(
                  summary = "When cost itinerary and cost factors mapping fetched successfully",
                  name =
                      "A 200 OK response indicate that cost itinerary and cost factors mapping are fetched successfully",
                  value =
                      """
                {
                    "success": true,
                    "requestId": "912c80ea-b371-435a-a721-85a60de0bfb4",
                    "timestamp": 1699252217393,
                    "message": "Cost Itinerary & Cost Factors fetched successfully!",
                    "payload": {
                        "id": 74,
                        "orgId": "NEXTUPLE_GR",
                        "costItinerary": "costItineraryParam17142937",
                        "costFactors": "costFactorParam17142937",
                        "itineraryStatus": "DRAFT",
                        "isActive": false
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
                  summary = "When a cost itinerary and cost factors mapping record not found",
                  name =
                      "A 404 response indicate that cost itinerary and cost factors mapping are not found for given details",
                  value =
                      """

                              {
                                  "success": false,
                                  "requestId": "fa935574-e36d-4183-bd54-6a417b867c89",
                                  "timestamp": 1699252241485,
                                  "message": "Cost Itinerary And Cost Factors Mapping not found",
                                  "payload": {
                                      "type": "ERROR",
                                      "code": 6001,
                                      "fields": {
                                          "itineraryStatus": {
                                              "rejectedValue": "DRAFT"
                                          },
                                          "costItinerary": {
                                              "rejectedValue": "costItineraryParam17142937"
                                          },
                                          "isActive": {
                                              "rejectedValue": true
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
public @interface GetCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveDoc {}
