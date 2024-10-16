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
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Cost Factor By Org Id And Cost Factor Id",
    description = "Get Cost Factor for a given Org Id And Cost Factor Id")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Factor is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factor is fetched successfully.",
                  name =
                      "A 200 OK response indicates that the Cost Factor is fetched successfully.",
                  value =
                      """
                                                {
                                                        "success": true,
                                                        "requestId": "3f6c1431-a45d-4192-a25f-a7a1951b9a86#209",
                                                        "timestamp": 1701779589498,
                                                        "message": "Cost Factor fetched successfully!",
                                                        "payload": {
                                                            "id": 381,
                                                            "orgId": "NEXTUPLE_GR",
                                                            "costFactor": "BillWeightUps",
                                                            "dataType": "NUMBER",
                                                            "formula": "(l*b*h)/5000",
                                                            "library": "SPEL",
                                                            "costFactorType": "DERIVED",
                                                            "displayName": "Bill Weight Custom UPS",
                                                            "values": "S,M,L,XL",
                                                            "defaultValue": "S",
                                                            "levelApplied": "SHIPMENT",
                                                            "uom": "lbs",
                                                            "isBucketed": true,
                                                            "isRateCardLookUpRequired": true
                                                        }
                                                    }
                                                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "When a given Cost Factor record not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor record not found.",
                  name = "A 404 response indicates that the given Cost Factor record not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3f6c1431-a45d-4192-a25f-a7a1951b9a86#112",
                                                    "timestamp": 1701779383974,
                                                    "message": "Cost Factor not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "costFactorId": {
                                                                "rejectedValue": 1
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
public @interface GetCostFactorByOrgIdAndCostFactorId {}
