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
@Operation(summary = "Delete Cost Factor", description = "Deletes a cost factor.")
@ApiResponse(
    responseCode = "200",
    description = "When a cost factor is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost factor is successfully deleted.",
                  name =
                      "A 200 OK response indicates that the Cost Factor details are successfully deleted.",
                  value =
                      """
                                                {
                                                          "success": true,
                                                          "requestId": "24adb7f5-24fe-4424-83fd-f25029af6bf0#106",
                                                          "timestamp": 1701781077272,
                                                          "message": "Cost Factor Details deleted successfully!",
                                                          "payload": {
                                                              "id": 382,
                                                              "orgId": "NEXTUPLE_GR",
                                                              "costFactor": "zone_ups",
                                                              "dataType": "STRING",
                                                              "formula": "zone",
                                                              "library": null,
                                                              "costFactorType": "REGULAR",
                                                              "displayName": "Zone UPS",
                                                              "values": "z1,z2,z3,z4",
                                                              "defaultValue": "z2",
                                                              "levelApplied": "SHIPMENT",
                                                              "uom": "lbs",
                                                              "isBucketed": false,
                                                              "isRateCardLookUpRequired": true
                                                          }
                                                      }
                                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a cost factor is associated with one or more cost itinerary.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost factor is associated with one or more cost itinerary.",
                  name =
                      "A 400 response indicates that the cost factor is associated with one or more cost itinerary.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "424ee9d1-e0ae-47b2-9b7b-44adc4d1b216#16",
                                                    "timestamp": 1701780911431,
                                                    "message": "Cost factor is associated with one or more cost itinerary",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "costFactorId": {
                                                                "rejectedValue": 368
                                                            }
                                                        }
                                                    }
                                                }
                                                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "When a cost factor is not found for given id.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost factor is not found for given id.",
                  name = "A 404 response indicates that the cost factor is not found for given id",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "c14e866c-6e4b-4b91-8164-a656a5ccac9b#235",
                                                      "timestamp": 1701780991970,
                                                      "message": "Cost Factor not found",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "costFactorId": {
                                                                  "rejectedValue": 3600
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
public @interface DeleteCostFactor {}
