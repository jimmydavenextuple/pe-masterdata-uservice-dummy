/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
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
    summary = "Get Cost Factor Discrete Bucket By Organization ID And Cost Factor.",
    description =
        "Fetches Cost Factor Discrete Bucket details for a given organization ID and Cost Factor.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Factor Discrete Bucket details are fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDiscreteBucketDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factor Discrete Bucket details are fetched successfully.",
                  name =
                      "A 200 OK response indicates that the Cost Factor Discrete Bucket details are fetched successfully.",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "5161e827-6782-475e-869b-8bfeee4a3b18#5016",
                                                     "timestamp": 1702288389535,
                                                     "message": "Cost Factor Discrete Bucket fetched successfully!",
                                                     "payload": [
                                                         {
                                                             "id": 137,
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS",
                                                             "notation": "XL",
                                                             "notationDisplayName": "Able to ship",
                                                             "valueList": "Kitchen,Garder,Electronics"
                                                         }
                                                     ]
                                                 }
                                             """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor Discrete Bucket is not found.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor Discrete Bucket is not found.",
                  name =
                      "A 404 response indicates that the given Cost Factor Discrete Bucket is not found.",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "5161e827-6782-475e-869b-8bfeee4a3b18#5034",
                                                      "timestamp": 1702288456127,
                                                      "message": "Buckets not found for orgId, Cost Factor combination.",
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
public @interface GetCostFactorDiscreteBucketDoc {}
