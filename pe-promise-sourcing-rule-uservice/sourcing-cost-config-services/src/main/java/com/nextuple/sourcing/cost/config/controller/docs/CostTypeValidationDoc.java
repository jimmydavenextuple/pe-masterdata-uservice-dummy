/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
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
    summary = "Get Cost Type Details for Validation",
    description = "Retrieves the details of the cost type for the given organization.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the cost types details are successfully fetched.",
    content =
        @Content(
            schema = @Schema(implementation = CostTypeValidationResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When there is selector CF defined for the cost type",
                  name =
                      "A 200 OK for cost type details for validation with selector CF and its information",
                  value =
                      """
                          {
                              "success": true,
                              "requestId": "54fe981c-4763-4699-81a9-f1734c354676",
                              "timestamp": 1698041368541,
                              "message": "Cost type details fetched successfully!",
                              "payload": {
                                  "costItinerary": "",
                                  "costFactors": [],
                                  "row": null,
                                  "column": null,
                                  "currency": "USD",
                                  "costType": "SHIPPING_COST",
                                  "displayName": "Shipping Cost",
                                  "selectorCf": "carrierServiceId",
                                  "selectorCfDisplayName": "FEDEX UPS and Roadie carriers",
                                  "selectorCfInfo": [
                                      {
                                          "costItinerary": "SHIPPING_COST_UPS_LIKE",
                                          "costFactors": [
                                              {
                                                  "costFactor": "isHazmat",
                                                  "displayName": "Is the line item hazardous material",
                                                  "uom": "",
                                                  "values": [
                                                      "TRUE",
                                                      "FALSE"
                                                  ]
                                              }
                                          ],
                                          "row": {
                                              "costFactor": "Zone",
                                              "displayName": "Zone of shipment ",
                                              "uom": "",
                                              "values": [
                                                  "1",
                                                  "2",
                                                  "3"
                                              ]
                                          },
                                          "column": {
                                              "costFactor": "billWeight",
                                              "displayName": "Bill weight of article",
                                              "uom": "",
                                              "values": [
                                                  "10",
                                                  "50",
                                                  "100"
                                              ]
                                          },
                                          "selectorCfValue": "UPS_GROUND",
                                          "displayName": "UPS_GROUND"
                                      },
                                      {
                                          "costItinerary": "SHIPPING_COST_UPS_LIKE",
                                          "costFactors": [
                                              {
                                                  "costFactor": "isHazmat",
                                                  "displayName": "Is the line item hazardous material",
                                                  "uom": "",
                                                  "values": [
                                                      "TRUE",
                                                      "FALSE"
                                                  ]
                                              }
                                          ],
                                          "row": {
                                              "costFactor": "Zone",
                                              "displayName": "Zone of shipment ",
                                              "uom": "",
                                              "values": [
                                                  "1",
                                                  "2",
                                                  "3"
                                              ]
                                          },
                                          "column": {
                                              "costFactor": "billWeight",
                                              "displayName": "Bill weight of article",
                                              "uom": "",
                                              "values": [
                                                  "10",
                                                  "50",
                                                  "100"
                                              ]
                                          },
                                          "selectorCfValue": "ROADIE_GROUND",
                                          "displayName": "ROADIE_GROUND"
                                      }
                                  ]
                              }
                          }
                          """),
              @ExampleObject(
                  summary = "When no selector CF is defined for the cost type",
                  name =
                      "A 200 OK for cost type details for validation with cost itinerary details",
                  value =
                      """
                          {
                              "success": true,
                              "requestId": "54fe981c-4763-4699-81a9-f1734c354676",
                              "timestamp": 1698041368541,
                              "message": "Cost type details fetched successfully!",
                              "payload": {
                                  "costItinerary": "NODE_PROCESSING_COST_ITINERARY",
                                  "costFactors": [
                                      {
                                          "costFactor": "isHazmat",
                                          "displayName": "Is the line item hazardeous material",
                                          "uom": "",
                                          "values": [
                                              "TRUE",
                                              "FALSE"
                                          ]
                                      }
                                  ],
                                  "row": {
                                      "costFactor": "Zone",
                                      "displayName": "Zone of shipment ",
                                      "uom": "",
                                      "values": [
                                          "1",
                                          "2",
                                          "3"
                                      ]
                                  },
                                  "column": {
                                      "costFactor": "billWeight",
                                      "displayName": "Bill wieght of article",
                                      "uom": "",
                                      "values": [
                                          "10",
                                          "50",
                                          "100"
                                      ]
                                  },
                                  "currency": "USD",
                                  "costType": "NODE_PROCESSING_COST",
                                  "displayName": "Node Processing Cost",
                                  "selectorCf": "",
                                  "selectorCfDisplayName": "",
                                  "selectorCfInfo": []
                              }
                          }
                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve cost types details is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the type details are not found."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Given cost type details are not found.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When the cost type is invalid or cost details are not found",
                  name = "A 404 Not found response when cost type details are not found",
                  value =
                      """
                    {
                        "success": false,
                        "requestId": "c42a7d38-b3bb-45fa-81fa-b0fe71dad5c1",
                        "timestamp": 1698041112964,
                        "message": "Tenant cost type not found for the orgId",
                        "payload": {
                            "type": "ERROR",
                            "code": 6001,
                            "fields": {
                                "costType": {
                                    "rejectedValue": "NODE_PROCESSING_COS"
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
public @interface CostTypeValidationDoc {}
