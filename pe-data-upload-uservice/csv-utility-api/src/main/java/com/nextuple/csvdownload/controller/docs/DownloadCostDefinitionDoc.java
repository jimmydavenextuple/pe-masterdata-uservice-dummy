/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller.docs;

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
    summary = "Download Cost Definition",
    description = "Downloads cost definition data in CSV for the organisation")
@ApiResponse(
    responseCode = "200",
    description = "When cost types are successfully fetched",
    content =
        @Content(
            schema = @Schema(implementation = CostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When the cost definition data is in grid form",
                  name = "A 200 OK when cost definition data is in grid form",
                  value =
                      """
                        "Notes: Filled in values will be in USD"
                        "Org Id","BAY"
                        "Cost Type","SHIPPING_COST"
                        "carrierServiceId","UPS-GROUND"
                        "surge","NON-HOLIDAY"
                        "Dynamic - incremental per pound cost bucket ( Bill Weight: 30+)","TRUE"
                        "Bill Weight/Shipping zones","Zone 1","Zone 2","Zone 3"
                        "0<<=5","10","20","30"
                        "5<<=10","50","100","150"
                        "10<<=30","100","200","300"
                        "30+","200","300","400"
                        """),
              @ExampleObject(
                  summary = "When the cost definition data is in grid form without Selector",
                  name = "A 200 OK when cost definition data is in grid form without selector",
                  value =
                      """
                        "Notes: Filled in values will be in USD"
                        "Org Id","BAY"
                        "Cost Type","NODE_PROCESSING_COST"
                        "surge","NON-HOLIDAY"
                        "Dynamic - incremental per pound cost bucket ( Bill Weight: 30+)","TRUE"
                        "Bill Weight/Shipping zones","Zone 1","Zone 2","Zone 3"
                        "0<<=5","10","20","30"
                        "5<<=10","50","100","150"
                        "10<<=30","100","200","300"
                        "30+","200","300","400"
                        """),
              @ExampleObject(
                  summary = "When the cost definition data is in table form ",
                  name = "A 200 OK when cost definition data is in table form",
                  value =
                      """
                        "Notes: Filled in values will be in USD"
                        "Org Id","BAY"
                        "Cost Type","SHIPPING_COST"
                        "carrierServiceId","UPS-GROUND"
                        "Dynamic - incremental per pound cost bucket ( Bill Weight: 0<<=5)","TRUE"
                        "Bill Weight","Shipping Cost"
                        "0<<=5","20"
                        "0<<=5","30"
                        "0<<=5","40"
                        "0<<=5","50"
                        """),
              @ExampleObject(
                  summary = "When the cost definition data is in static form ",
                  name = "A 200 OK when cost definition data is in static form",
                  value =
                      """
                        "Notes: Filled in values will be in USD"
                        "Org Id","BAY"
                        "Cost Type","SHIPPING_COST"
                        "carrierServiceId","UPS-GROUND"
                        "Shipping Cost","20"
                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<ul>" + "<li><b>Error code: 6006</b>: When rate card details are not found</li>" + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When rate card details are not found",
                  name = "A 404 NOT FOUND when rate card details are not found",
                  value =
                      """
                          {
                        "success": false,
                        "requestId": "c42a7d38-b3bb-45fa-81fa-b0fe71dad5c1",
                        "timestamp": 1698041112964,
                        "message": "Cost definition data not found for org NEXTUPLE_GR",
                        "payload": {
                            "type": "ERROR",
                            "code": 6001,
                            "fields": {
                                "orgId": {
                                    "rejectedValue": "NEXTUPLE_GR"
                                }
                            }
                        }
                    }
                          }""")
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "<ul>"
            + "<li><b>Error code: 6006</b>: When cost type in request in invalid.</li>"
            + "<li><b>Error code: 6006</b>: When selector and selector value in request in invalid.</li>"
            + "<li><b>Error code: 6006</b>: When row and column cost factors in request are invalid.</li>"
            + "<li><b>Error code: 6006</b>: When filters in filter list of request are invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  name = "A 400 error code When cost type in request in invalid",
                  summary = "When cost type in request is invalid",
                  value =
                      """
                        {
                            "success": false,
                            "requestId": "49e6487c-3d65-4eab-81ae-4ab5d08205dd",
                            "timestamp": 1698226407728,
                            "message": "Invalid cost type for orgId NEXTUPLE_GR",
                            "payload": {
                                "type": "ERROR",
                                "code": 6001,
                                "fields": {
                                    "costType": {
                                        "rejectedValue": "INVALID_COST"
                                    }
                                }
                            }
                        }
                        """),
              @ExampleObject(
                  name = "A 400 error code When selector in request is invalid.",
                  summary = "When selector in request is invalid.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "4db8ee99-230c-4a6d-bc72-82761617dee1",
                                            "timestamp": 1698226887836,
                                            "message": "Invalid selectorCF for cost type SHIPPING_COST",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001,
                                                "fields": {
                                                    "selectorCf": {
                                                        "rejectedValue": "invalidSelector"
                                                    }
                                                }
                                            }
                                        }
                                """),
              @ExampleObject(
                  name = "A 400 error code When selector value in request is invalid.",
                  summary = "When selector in request is invalid.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "d6924491-a278-43fb-bf01-91fca4431b26",
                                            "timestamp": 1698226990234,
                                            "message": "Invalid selectorCFValue for selectorCf carrierServiceId",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001,
                                                "fields": {
                                                    "selectorCfValue": {
                                                        "rejectedValue": "invalidValue"
                                                    }
                                                }
                                            }
                                        }
                                """),
              @ExampleObject(
                  name = "A 400 error code When row in request is invalid.",
                  summary = "When row in request is invalid.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "2c62b0e2-7376-4b11-8f94-74905272cf71",
                                            "timestamp": 1698227036114,
                                            "message": "Invalid cost factor in row",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001,
                                                "fields": {
                                                    "row": {
                                                        "rejectedValue": "invalidRow"
                                                    }
                                                }
                                            }
                                        }
                                """),
              @ExampleObject(
                  name = "A 400 error code When column in request is invalid.",
                  summary = "When column in request is invalid.",
                  value =
                      """
                                        {
                                             "success": false,
                                             "requestId": "de16bdf7-36c6-481e-88e4-dca0cbb35d59",
                                             "timestamp": 1698227069718,
                                             "message": "Invalid cost factor in column",
                                             "payload": {
                                                 "type": "ERROR",
                                                 "code": 6001,
                                                 "fields": {
                                                     "column": {
                                                         "rejectedValue": "invalidColumn"
                                                     }
                                                 }
                                             }
                                         }
                                """),
              @ExampleObject(
                  name = "A 400 error code when filter in request is invalid.",
                  summary = "When filter in request is invalid.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "87cc73f5-2b62-4a73-bb66-e54620e98f57",
                                            "timestamp": 1698227212337,
                                            "message": "Invalid filter or filter values",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001
                                            }
                                        }
                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description =
        "<ul>"
            + "<li><b>Error code: 2</b>: There was some error on server while processing the request</li>"
            + "<li><b>Error code: 6005</b>: Exception while getting rate card data</li>"
            + "</ul>",
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
                          }"""),
              @ExampleObject(
                  summary = "Exception while getting rate card data",
                  name = "A 500 error code when fetching rate card data",
                  value =
                      """
                          {
                              "success": false,
                              "requestId": "3a822137-8ad5-4aa6-abe9-11836d06f56f",
                              "message": "Exception while getting rate card data for orgId NEXTUPLE_GR",
                              "timestamp": 1698044027078,
                              "payload": {
                                  "type": "ERROR",
                                  "code": 6005,
                                  "fields": {
                                    "orgId": {
                                        "rejectedValue": "NEXTUPLE_GR"
                                    }
                                }
                              }
                          }""")
            }))
public @interface DownloadCostDefinitionDoc {}
