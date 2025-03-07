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
    summary = "Create Selector and Cost Itinerary Mapping.",
    description = "Creates a selector and cost itinerary mapping with given details.")
@ApiResponse(
    responseCode = "201",
    description = "When a selector and cost itinerary mapping is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = SelectorAndCostItineraryMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a selector and cost itinerary mapping is successfully created.",
                  name =
                      "A 201 OK indicates that the selector and cost itinerary mapping is successfully created.",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "c015d55d-3184-4e8e-be39-abe1d90baf5d",
                                                       "timestamp": 1702017733199,
                                                       "message": "Selector and Cost Itinerary Mapping created successfully.",
                                                       "payload": {
                                                           "id": 239,
                                                           "orgId": "NEXTUPLE_GR",
                                                           "selectorCf": "carrierServiceId",
                                                           "selectorCfValue": "UPS-Ground",
                                                           "costItinerary": "SHIPPING_COST_UPS_LIKE_T2",
                                                           "costType": "SHP_COST_T2",
                                                           "customAttributes": {
                                                             "dynamicAtrr1": true
                                                           }
                                                       }
                                                   }""")
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: When SelectorCf cannot be empty string.</li>"
            + "<li><b>Error code: 2</b>: When SelectorCfValue cannot be empty string.</li>"
            + "<li><b>Error code: 2</b>: When selectorCf is null, SelectorCfValue must also be null.</li>"
            + "<li><b>Error code: 2</b>: When Cost Itinerary provided in the request is not valid.</li>"
            + "<li><b>Error code: 2</b>: When Selector And CostItinerary Mapping Request is not valid.</li>"
            + "<li><b>Error code: 2</b>: When Selector value in the request is not part of selector's possible values.</li>"
            + "<li><b>Error code: 2</b>: When itinerary cannot associate with DRAFT status.</li>"
            + "<li><b>Error code: 2</b>: When selector value is associated with an itinerary.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When SelectorCf cannot be empty string.",
                  name = "A 400 indicates that the SelectorCf cannot be empty string.",
                  value =
                      """
                                                           {
                                                                "success": false,
                                                                "requestId": "e392f346-eca7-4ab3-a67d-3afab406e0d3#52",
                                                                "timestamp": 1702015458332,
                                                                "message": "Selector should be null or a valid string",
                                                                "payload": {
                                                                    "type": "ERROR",
                                                                    "code": 6001,
                                                                    "fields": {
                                                                        "selectorCf": {
                                                                            "rejectedValue": ""
                                                                        },
                                                                        "orgId": {
                                                                            "rejectedValue": "NEXTUPLE_GR"
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                """),
              @ExampleObject(
                  summary = "When SelectorCfValue cannot be empty string.",
                  name = "A 400 indicates that the SelectorCfValue cannot be empty string.",
                  value =
                      """
                                                           {
                                                                 "success": false,
                                                                 "requestId": "e392f346-eca7-4ab3-a67d-3afab406e0d3#59",
                                                                 "timestamp": 1702015515252,
                                                                 "message": "Selector value should be null or one of the values defined for selector",
                                                                 "payload": {
                                                                     "type": "ERROR",
                                                                     "code": 6001,
                                                                     "fields": {
                                                                         "selectorCfValue": {
                                                                             "rejectedValue": ""
                                                                         },
                                                                         "orgId": {
                                                                             "rejectedValue": "NEXTUPLE_GR"
                                                                         }
                                                                     }
                                                                 }
                                                             }
                                                """),
              @ExampleObject(
                  summary = "When selectorCf is null, SelectorCfValue must also be null.",
                  name =
                      "A 400 indicates that the selectorCf is null, SelectorCfValue must also be null.",
                  value =
                      """
                                                           {
                                                                  "success": false,
                                                                  "requestId": "e0e38e43-c5d0-4c84-9932-d6965b0ea3b5#259",
                                                                  "timestamp": 1702015664321,
                                                                  "message": "Allowed value for selector should be null when selector is null",
                                                                  "payload": {
                                                                      "type": "ERROR",
                                                                      "code": 6001,
                                                                      "fields": {
                                                                          "orgId": {
                                                                              "rejectedValue": "NEXTUPLE_GR"
                                                                          },
                                                                          "selectorCfValue": {
                                                                              "rejectedValue": "UPS-Ground"
                                                                          }
                                                                      }
                                                                  }
                                                              }
                                                """),
              @ExampleObject(
                  summary = "When Cost Itinerary provided in the request is not valid.",
                  name =
                      "A 400 indicates that the Cost Itinerary provided in the request is not valid.",
                  value =
                      """
                                                           {
                                                                   "success": false,
                                                                   "requestId": "e4107ad0-ec80-41cb-862c-7fcbf9c4abc3#33",
                                                                   "timestamp": 1702015785983,
                                                                   "message": "Cost Itinerary provided in the request is not valid",
                                                                   "payload": {
                                                                       "type": "ERROR",
                                                                       "code": 6001,
                                                                       "fields": {
                                                                           "costItinerary": {
                                                                               "rejectedValue": "SHP_COST_ITR"
                                                                           },
                                                                           "orgId": {
                                                                               "rejectedValue": "NEXTUPLE_GR"
                                                                           }
                                                                       }
                                                                   }
                                                               }
                                                """),
              @ExampleObject(
                  summary = "When Selector And CostItinerary Mapping Request is not valid.",
                  name =
                      "A 400 indicates that the Selector And CostItinerary Mapping Request is not valid.",
                  value =
                      """
                                                           {
                                                                    "success": false,
                                                                    "requestId": "e0e38e43-c5d0-4c84-9932-d6965b0ea3b5#294",
                                                                    "timestamp": 1702015971087,
                                                                    "message": "Selector And CostItinerary Mapping Request is not valid.",
                                                                    "payload": {
                                                                        "type": "ERROR",
                                                                        "code": 6001,
                                                                        "fields": {
                                                                            "costType": {
                                                                                "rejectedValue": "SHP_COST_A1"
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
                      "When Selector value in the request is not part of selector's possible values.",
                  name =
                      "A 400 indicates that the Selector value in the request is not part of selector's possible values.",
                  value =
                      """
                                                           {
                                                               "success": false,
                                                               "requestId": "49017e71-a5e2-46f6-8ebb-4851b10be4ab",
                                                               "timestamp": 1702017678151,
                                                               "message": "Selector value in the request is not part of selector's possible values",
                                                               "payload": {
                                                                   "type": "ERROR",
                                                                   "code": 6001,
                                                                   "fields": {
                                                                       "selectorCf": {
                                                                           "rejectedValue": "carrierServiceId"
                                                                       },
                                                                       "selectorCfValue": {
                                                                           "rejectedValue": "UPSGround"
                                                                       },
                                                                       "orgId": {
                                                                           "rejectedValue": "NEXTUPLE_GR"
                                                                       }
                                                                   }
                                                               }
                                                           }
                                                """),
              @ExampleObject(
                  summary = "When itinerary cannot associate with DRAFT status.",
                  name = "A 400 indicates that the itinerary cannot associate with DRAFT status.",
                  value =
                      """
                                                           {
                                                                "success": false,
                                                                "requestId": "42bdba43-67f0-4f4a-81ea-776dbe04b34c",
                                                                "timestamp": 1702019059449,
                                                                "message": "Can't associate itinerary with DRAFT status",
                                                                "payload": {
                                                                    "type": "ERROR",
                                                                    "code": 6001,
                                                                    "fields": {
                                                                        "costItinerary": {
                                                                            "rejectedValue": "SHP_COST_ITR_T3"
                                                                        },
                                                                        "orgId": {
                                                                            "rejectedValue": "NEXTUPLE_GR"
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                """),
              @ExampleObject(
                  summary = "When selector value is associated with an itinerary.",
                  name = "A 400 indicates that the Selector value is associated with an itinerary.",
                  value =
                      """
                                                           {
                                                                 "success": false,
                                                                 "requestId": "fa59140a-26b2-4c56-bd2d-3fa1f4ceff9b",
                                                                 "timestamp": 1702019430450,
                                                                 "message": "Selector value is associated with an itinerary",
                                                                 "payload": {
                                                                     "type": "ERROR",
                                                                     "code": 6001,
                                                                     "fields": {
                                                                         "selectorCfValue": {
                                                                             "rejectedValue": "UPS-Ground"
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
public @interface CreateSelectorAndCostItineraryMappingDoc {}
