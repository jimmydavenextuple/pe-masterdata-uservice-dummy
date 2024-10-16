/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
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
    summary = "Get Cost Factor By Org Id And Cost Factor",
    description = "Get Cost Factor for a given Org Id And Cost Factor")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Factor details are successfully fetched for a given org id and cost factor",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When cost factor details fetched successfully",
                  name =
                      "A 200 OK response indicate that cost factor details are fetched successfully",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "6bc43c2c-f66d-4ddf-a9fc-6ce73c9d8367",
                                            "timestamp": 1699252716840,
                                            "message": "Cost Factor fetched successfully!",
                                            "payload": {
                                                "id": 1,
                                                "orgId": "NEXTUPLE_GR",
                                                "costFactor": "BillWeightUPS",
                                                "dataType": "NUMBER",
                                                "formula": "(l*b*h)/6000",
                                                "library": null,
                                                "costFactorType": null,
                                                "displayName": "Bill Weight Custom UPS1",
                                                "values": "S,M,L,XL",
                                                "defaultValue": "S",
                                                "levelApplied": "Shipment,Item,Item-Qty",
                                                "uom": null,
                                                "isBucketed": null,
                                                "isRateCardLookUpRequired": true
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor record not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost factor is not found",
                  name = "A 404 response indicate that cost factor is not found for given details",
                  value =
                      """
                          {
                              "success": false,
                              "requestId": "4e44c682-18b6-4685-a556-79580e69d7b3",
                              "timestamp": 1699252895501,
                              "message": "Cost Factor not found",
                              "payload": {
                                  "type": "ERROR",
                                  "code": 6001,
                                  "fields": {
                                      "costFactor": {
                                          "rejectedValue": "BillWeightUPS1"
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
public @interface GetCostFactorByOrgIdAndCostFactorDoc {}
