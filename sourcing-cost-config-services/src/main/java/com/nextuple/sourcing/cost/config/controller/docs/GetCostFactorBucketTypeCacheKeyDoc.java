/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;
import java.util.List;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Cost Factor Bucket Type Cache Keys",
    description = "Retrieves the list of all the cache keys of Cost Factor Bucket Type")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that all the cache keys for Cost Factor Bucket Type are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When all the cache keys for Cost Factor Bucket Type are retrieved successfully.",
                  name =
                      "A 200 success code indicates that all the cache keys for Cost Factor Bucket Type are retrieved successfully.",
                  value =
                      """
                              {
                                   "success": true,
                                   "requestId": "db0ca977-a91c-40b0-8f42-e827a6e58634#2665",
                                   "timestamp": 1702277023680,
                                   "message": "Cost Factor Bucket Type Cache Keys fetched successfully",
                                   "payload": [
                                       {
                                           "orgId": "XYZINC",
                                           "costFactor": "carrierServiceId"
                                       },
                                       {
                                           "orgId": "NEXTUPLE_GR",
                                           "costFactor": "carrierServiceId"
                                       }
                                   ]
                               }
                            """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor Bucket Type record not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor Bucket Type record not found",
                  name =
                      "A 404 response indicate that Cost Factor Bucket Type records are not found for given details",
                  value =
                      """
                                                                    {
                                                                        "success": false,
                                                                        "requestId": "dabc80b6-ce91-4d62-9d64-018fd20a3788",
                                                                        "timestamp": 1699249742289,
                                                                        "message": "Cost Factor Bucket Type not found",
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
public @interface GetCostFactorBucketTypeCacheKeyDoc {}
