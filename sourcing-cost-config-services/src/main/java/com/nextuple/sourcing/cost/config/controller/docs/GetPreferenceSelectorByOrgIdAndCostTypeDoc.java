/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
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
    summary = "Get Tenant Preference for Selector by Org ID and Cost Type",
    description =
        "Retrieves the tenant preference for a selector based on the given organization ID and cost type.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the tenant preference for selector details are successfully fetched for a given organization ID and cost type.",
    content =
        @Content(
            schema = @Schema(implementation = PreferenceSelectorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Tenant Preference for Selector details are fetched successfully",
                  name =
                      "A 200 OK response indicate that tenant preference for selector details are fetched successfully",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "f7638d33-0b4e-4cca-ae00-1072612ae4dd",
                                            "timestamp": 1699249702639,
                                            "message": "Preference Selector fetched successfully!",
                                            "payload": {
                                                "id": 47,
                                                "orgId": "NEXTUPLE_C5",
                                                "selectorCf": "carrierServiceId",
                                                "costType": "SHIPPING_COST"
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve tenant preference for selector details for a given organization ID and cost type is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the tenant preference for selector is not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant preference for selector record is not found.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Tenant Preference for Selector record not found",
                  name =
                      "A 404 response indicate that tenant preference for selector records are not found for given details",
                  value =
                      """
                                {
                                    "success": false,
                                    "requestId": "dabc80b6-ce91-4d62-9d64-018fd20a3788",
                                    "timestamp": 1699249742289,
                                    "message": "Preference Selector not found",
                                    "payload": {
                                        "type": "ERROR",
                                        "code": 6001,
                                        "fields": {
                                            "costType": {
                                                "rejectedValue": "SHIPPING_COST1"
                                            },
                                            "orgId": {
                                                "rejectedValue": "NEXTUPLE_C5"
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
public @interface GetPreferenceSelectorByOrgIdAndCostTypeDoc {}
