/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
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
@Operation(summary = "Update Rate Card Status", description = "Updates the rate card status.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the rate card status updated successfully.",
    content =
        @Content(
            schema = @Schema(implementation = UpdateRateCardStatusResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Rate card status updated successfully",
                  name =
                      "A 200 success code indicates that the Rate card status updated successfully.",
                  value =
                      """
                                                    {
                                                        "success": true,
                                                        "requestId": "b5e9bf52-fb8f-4f84-ae38-8d9ef7ea6907",
                                                        "timestamp": 1702364232850,
                                                        "message": "Rate card status updated successfully!",
                                                        "payload": {
                                                            "costType": "SHIPPING_COST",
                                                            "selector": {
                                                                "selectorCf": "carrierServiceId",
                                                                "selectorCfValue": "UPS-Ground"
                                                            },
                                                            "isRateCardActive": true
                                                        }
                                                    }
                                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update the rate card status is not valid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Cost type is invalid.</li>"
            + "<li><b>Error code: 6001</b>: Selector is invalid.</li>"
            + "<li><b>Error code: 6001</b>: Default itinerary cannot be deactivated.</li>"
            + "<li><b>Error code: 6001</b>: Unable to deactivate only itinerary for the cost type.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost type is invalid.",
                  name = "A 400 response indicates that the given cost type is invalid.",
                  value =
                      """
                                                {
                                                        "success": false,
                                                        "requestId": "af886c67-792c-4f83-9c5e-6bb25e2f0c2a#152",
                                                        "timestamp": 1702039793780,
                                                        "message": "Invalid cost type for given orgId",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "orgId": {
                                                                    "rejectedValue": "NEXTUPLE_GR"
                                                                },
                                                                "costType": {
                                                                    "rejectedValue": "SHIPPING_COST_S1"
                                                                }
                                                            }
                                                        }
                                                    }
                                            """),
              @ExampleObject(
                  summary = "When selector is invalid.",
                  name = "A 400 response indicates that the selector is invalid.",
                  value =
                      """
                                                    {
                                                                  "success": false,
                                                                  "requestId": "af886c67-792c-4f83-9c5e-6bb25e2f0c2a#176",
                                                                  "timestamp": 1702039907028,
                                                                  "message": "Invalid selector provided in the request",
                                                                  "payload": {
                                                                      "type": "ERROR",
                                                                      "code": 6001,
                                                                      "fields": {
                                                                          "orgId": {
                                                                              "rejectedValue": "NEXTUPLE_GR"
                                                                          },
                                                                          "selector": {
                                                                              "rejectedValue": {
                                                                                  "selectorCf": "BillWeightUps",
                                                                                  "selectorCfValue": "UPS-GROUND"
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                              }
                                                """),
              @ExampleObject(
                  summary = "When default itinerary cannot be deactivated.",
                  name =
                      "A 400 response indicates that the default itinerary cannot be deactivated.",
                  value =
                      """
                                                    {
                                                        "success": false,
                                                        "requestId": "3a419eec-16fb-4e7f-8070-10cb3216fa88",
                                                        "timestamp": 1702362538734,
                                                        "message": "Default itinerary cannot be deactivated",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "isRateCardActive": {
                                                                    "rejectedValue": false
                                                                },
                                                                "orgId": {
                                                                    "rejectedValue": "NEXTUPLE_GR"
                                                                }
                                                            }
                                                        }
                                                    }
                                                """),
              @ExampleObject(
                  summary = "When unable to deactivate only itinerary for the cost type.",
                  name =
                      "A 400 response indicates that the only itinerary for the cost type cannot be deactivated",
                  value =
                      """
                                                    {
                                                         "success": false,
                                                         "requestId": "4db89186-0fb5-4f9c-81e5-73c190f8e075",
                                                         "timestamp": 1702363555692,
                                                         "message": "Unable to deactivate only itinerary for the cost type",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "isRateCardActive": {
                                                                     "rejectedValue": false
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
        "A 404 error cod indicates that selector and cost itinerary mapping is not found."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Selector and cost itinerary mapping is not found.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a selector and cost itinerary mapping is not found.",
                  name =
                      "A 404 error code indicates that the selector and cost itinerary mapping is not found",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "6b15cc35-2310-487a-a243-9b55dc93594a",
                                                         "timestamp": 1702039509051,
                                                         "message": "Selector Itinerary Mapping not found with given details",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "costType": {
                                                                     "rejectedValue": "SHIPPING_COST"
                                                                 },
                                                                 "orgId": {
                                                                     "rejectedValue": "NEXTUPLE_GR"
                                                                 },
                                                                 "selector": {
                                                                     "rejectedValue": {
                                                                         "selectorCf": "carrierServiceId",
                                                                         "selectorCfValue": "UPS-GROUND"
                                                                     }
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
public @interface UpdateRateCardStatusDoc {}
