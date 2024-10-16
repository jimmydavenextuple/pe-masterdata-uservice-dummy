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
    summary = "Get list of all the cache keys for Cost Factor Contiguous Bucket",
    description = "Retrieves the list of all the cache keys for Cost Factor Contiguous Bucket")
@ApiResponse(
    responseCode = "200",
    description =
        "When list of all the cache keys for Cost Factor Contiguous Bucket are fetched successfully",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When list of all the cache keys for Cost Factor Contiguous Bucket are fetched successfully",
                  name =
                      "A 200 OK response indicates that the list of all the cache keys for Cost Factor Contiguous Bucket are fetched successfully",
                  value =
                      """
                              {
                                   "success": true,
                                   "requestId": "33a519d8-ec73-45a6-a5e5-037324da62db#5441",
                                   "timestamp": 1702294692875,
                                   "message": "Cost Factor Contiguous Bucket Keys fetched successfully",
                                   "payload": [
                                       {
                                           "orgId": "XYZINC",
                                           "costFactor": "billWeight"
                                       },
                                       {
                                           "orgId": "XYZINC",
                                           "costFactor": "billWeight"
                                       }
                                   ]
                               }
                      """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor Contiguous Bucket record not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor Contiguous Bucket record not found",
                  name =
                      "A 404 response indicates that the Cost Factor Contiguous Bucket records are not found for given details.",
                  value =
                      """
                          {
                              "success": false,
                              "requestId": "dabc80b6-ce91-4d62-9d64-018fd20a3788",
                              "timestamp": 1699249742289,
                              "message": "Cost Factor Contiguous Bucket not found",
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
public @interface GetCostFactorContiguousBucketCacheKeyDoc {}
