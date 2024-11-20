/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
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
    summary = "Get Cost Factor Contiguous Bucket By Organization ID And Cost Factor.",
    description =
        "Fetches Cost Factor Contiguous Bucket details for a given organization ID and Cost Factor.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Factor Contiguous Bucket Type is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorContiguousBucketDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factor Contiguous Bucket is fetched successfully.",
                  name =
                      "A 200 response indicates that the Cost Factor Contiguous Bucket is fetched successfully.",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "29d6ec91-0ead-4bdb-aeb9-904f73f451a4#11394",
                                                       "timestamp": 1702293528771,
                                                       "message": "Cost Factor Bucket type fetched successfully!",
                                                       "payload": {
                                                           "id": 397,
                                                           "orgId": "NEXTUPLE_GR",
                                                           "costFactor": "BillWeightUPS",
                                                           "bucketType": "CONTIGUOUS"
                                                       }
                                                   }
                                         """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "When Bucket type of cost factor is not CONTIGUOUS.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor Contiguous Bucket is not found</li></ul>",
                  name =
                      "A 404 response indicates that the given Cost Factor Contiguous Bucket is not found.",
                  value =
                      """
                                                {
                                                          "success": false,
                                                          "requestId": "29d6ec91-0ead-4bdb-aeb9-904f73f451a4#11707",
                                                          "timestamp": 1702293581397,
                                                          "message": "OrgId and Cost Factor combination not found.",
                                                          "payload": {
                                                              "type": "ERROR",
                                                              "code": 6001,
                                                              "fields": {
                                                                  "costFactor": {
                                                                      "rejectedValue": "BillWeightUPS22"
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
public @interface GetCostFactorContiguousBucketDoc {}
