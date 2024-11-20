/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
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
    summary = "Get Cost Types",
    description =
        "Retrieves cost types associated with the organization. The cost type basically refers to the costs that are included as part of the order for eg. shipping cost.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the cost types are successfully fetched.",
    content =
        @Content(
            schema = @Schema(implementation = CostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost types and details fetched successfully",
                  name = "A 200 OK for get cost types and details",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "f8895767-5c8b-46f5-a12d-5cad302b12f9#44",
                                                    "timestamp": 1698041772827,
                                                    "message": "Cost Types and it’s information fetched successfully!",
                                                    "payload": {
                                                        "currency": "USD",
                                                        "costTypeList": [
                                                            {
                                                                "costFactors": [],
                                                                "row": null,
                                                                "column": null,
                                                                "costType": "SHIPPING_COST",
                                                                "displayName": "Shipping Cost",
                                                                "selectorCf": "carrierServiceId",
                                                                "selectorCfDisplayName": "FEDEX UPS and Roadie carriers",
                                                                "optimizationStrategies": "PROFIT",
                                                                "selectorCfInfo": [
                                                                    {
                                                                        "costFactors": [
                                                                            {
                                                                                "costFactor": "isHazmat",
                                                                                "displayName": "Is the line item hazardous material",
                                                                                "uom": "STRING",
                                                                                "values": [
                                                                                    {
                                                                                        "value": "TRUE",
                                                                                        "displayName": "TRUE"
                                                                                    },
                                                                                    {
                                                                                        "value": "FALSE",
                                                                                        "displayName": "FALSE"
                                                                                    }
                                                                                ]
                                                                            }
                                                                        ],
                                                                        "row": {
                                                                            "costFactor": "Zone",
                                                                            "displayName": "Zone of shipment ",
                                                                            "uom": "NUMBER"
                                                                        },
                                                                        "column": {
                                                                            "costFactor": "billWeight",
                                                                            "displayName": "Bill weight of article",
                                                                            "uom": "LBS"
                                                                        },
                                                                        "selectorCfValue": "UPS_GROUND",
                                                                        "displayName": "UPS_GROUND"
                                                                    },
                                                                    {
                                                                        "costFactors": [
                                                                            {
                                                                                "costFactor": "isHazmat",
                                                                                "displayName": "Is the line item hazardous material",
                                                                                "uom": "STRING",
                                                                                "values": [
                                                                                    {
                                                                                        "value": "TRUE",
                                                                                        "displayName": "TRUE"
                                                                                    },
                                                                                    {
                                                                                        "value": "FALSE",
                                                                                        "displayName": "FALSE"
                                                                                    }
                                                                                ]
                                                                            }
                                                                        ],
                                                                        "row": {
                                                                            "costFactor": "Zone",
                                                                            "displayName": "Zone of shipment ",
                                                                            "uom": "NUMBER"
                                                                        },
                                                                        "column": {
                                                                            "costFactor": "billWeight",
                                                                            "displayName": "Bill weight of article",
                                                                            "uom": "LBS"
                                                                        },
                                                                        "selectorCfValue": "ROADIE_GROUND",
                                                                        "displayName": "ROADIE_GROUND"
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "costFactors": [],
                                                                "row": {
                                                                            "costFactor": "billWeight",
                                                                            "displayName": "Bill weight of article",
                                                                            "uom": "LBS"
                                                                        },
                                                                "column": {
                                                                            "costFactor": "quantity",
                                                                            "displayName": "Quantity of article",
                                                                            "uom": "NUMBER"
                                                                        },
                                                                "costType": "NODE_PROCESSING_COST",
                                                                "displayName": "Node processing cost",
                                                                "selectorCf": "",
                                                                "selectorCfDisplayName": "",
                                                                "selectorCfInfo": [],
                                                                "optimizationStrategies": "PROFIT"
                                                            }
                                                        ]
                                                    }
                                                }"""),
              @ExampleObject(
                  summary = "When cost types have no cost itinerary details associated with them",
                  name = "A 200 OK for get cost types when itinerary are not configured for them",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "f8895767-5c8b-46f5-a12d-5cad302b12f9#44",
                                                    "timestamp": 1698041772827,
                                                    "message": "Cost Types and it’s information fetched successfully!",
                                                    "payload": {
                                                        "currency": "USD",
                                                        "costTypeList": [
                                                            {
                                                                "costFactors": [],
                                                                "row": null,
                                                                "column": null,
                                                                "costType": "SHIPPING_COST",
                                                                "displayName": "Shipping Cost",
                                                                "selectorCf": "carrierServiceId",
                                                                "selectorCfDisplayName": "FEDEX UPS and Roadie carriers",
                                                                "optimizationStrategies": "PROFIT",
                                                                "selectorCfInfo": [
                                                                    {
                                                                        "costFactors": [],
                                                                        "row": null,
                                                                        "column": null,
                                                                        "selectorCfValue": "UPS_GROUND",
                                                                        "displayName": "UPS_GROUND"
                                                                    },
                                                                    {
                                                                        "costFactors": [],
                                                                        "row": null,
                                                                        "column": null,
                                                                        "selectorCfValue": "ROADIE_GROUND",
                                                                        "displayName": "ROADIE_GROUND"
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "costFactors": [],
                                                                "row": null,
                                                                "column": null,
                                                                "costType": "NODE_PROCESSING_COST",
                                                                "displayName": "Node processing cost",
                                                                "selectorCf": "",
                                                                "selectorCfDisplayName": "",
                                                                "selectorCfInfo": [],
                                                                "optimizationStrategies": "PROFIT"
                                                            }
                                                        ]
                                                    }
                                                }"""),
              @ExampleObject(
                  summary = "When no cost type is configured for the organisation",
                  name = "A 200 OK for get cost types when itinerary are not configured for them",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "f8895767-5c8b-46f5-a12d-5cad302b12f9#44",
                                                    "timestamp": 1698041772827,
                                                    "message": "Cost Types and it’s information fetched successfully!",
                                                    "payload": {
                                                        "currency": "USD",
                                                        "costTypeList": []
                                                    }
                                                }""")
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
public @interface CostTypeDashboardDoc {}
