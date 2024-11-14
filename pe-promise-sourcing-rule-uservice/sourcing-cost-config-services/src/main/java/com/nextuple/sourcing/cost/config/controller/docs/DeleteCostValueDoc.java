/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
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
@Operation(summary = "Delete Cost Value", description = "Deletes a cost value with given details.")
@ApiResponse(
    responseCode = "200",
    description = "When a cost value is deleted successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CostValueResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a cost value is deleted successfully.",
                  name = "A 200 response indicates that the cost value is deleted successfully.",
                  value =
                      """
                                            {
                                               "success": true,
                                               "requestId": "ec3a15cd-d340-45dd-a81b-324004b023a8",
                                               "timestamp": 1702037350823,
                                               "message": "Cost value deleted successfully.",
                                               "payload": {
                                                   "id": 4082,
                                                   "orgId": "NEXTUPLE_GR",
                                                   "costItinerary": "TEST_COST_LIKE",
                                                   "costFactorCombinationKey": "FedEx-Ground|Zone6|E09",
                                                   "costValue": 6.0,
                                                   "prevSlabValue": ""
                                               }
                                           }
                                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "When cost value not found for given orgId and Id.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When cost value not found for given orgId and Id.",
                  name =
                      "A 404 response indicates that the cost value not found for given orgId and Id.",
                  value =
                      """
                                           {
                                                "success": false,
                                                "requestId": "02ef6820-a3c0-41df-a03a-88d9948898e3",
                                                "timestamp": 1702037456117,
                                                "message": "Cost value not found for given details",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "costValueId": {
                                                            "rejectedValue": 40822
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
public @interface DeleteCostValueDoc {}
