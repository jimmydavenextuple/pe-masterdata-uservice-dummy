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
    summary = "Delete Cost Factors Discrete Bucket",
    description = "Delete Cost Factors Discrete Bucket for a given organization ID.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Factors Discrete Bucket is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDiscreteBucketDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factors Discrete Bucket is successfully deleted.",
                  name =
                      "A 200 OK response indicates that the Cost Factors Discrete Bucket is successfully deleted.",
                  value =
                      """
                                                {
                                                      "success": true,
                                                      "requestId": "29d6ec91-0ead-4bdb-aeb9-904f73f451a4#8141",
                                                      "timestamp": 1702289488058,
                                                      "message": "Cost Factor Bucket deleted successfully!",
                                                      "payload": {
                                                          "id": 137,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "costFactor": "BillWeightUPS",
                                                          "notation": "Shippable",
                                                          "notationDisplayName": "Able to ship",
                                                          "valueList": "Kitchen,Garden,Electronics"
                                                      }
                                                  }
                                                    """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul>"
            + "<li> When a discrete bucket is not found for given Id and orgId combination.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a discrete bucket is not found for given Id and orgId combination.",
                  name =
                      "A 404 response indicates that the discrete bucket record is not found for the orgId and Id.",
                  value =
                      """
                                                {
                                                           "success": false,
                                                           "requestId": "29d6ec91-0ead-4bdb-aeb9-904f73f451a4#8139",
                                                           "timestamp": 1702289468298,
                                                           "message": "Bucket not found for given id and orgId.",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6001,
                                                               "fields": {
                                                                   "id": {
                                                                       "rejectedValue": 1378
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
    responseCode = "412",
    description =
        "<li><b>Error code: 6001 </b></li><ul>"
            + "<li> When a Cost Itinerary for given Cost Factor is in CREATED state.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a Cost Itinerary for given Cost Factor is in CREATED state.",
                  name =
                      "A 412 response indicates that the Cost Itinerary for given Cost Factor is in CREATED state.",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "29d6ec91-0ead-4bdb-aeb9-904f73f451a4#7958",
                                                         "timestamp": 1702289076282,
                                                         "message": "Bucket type not found for orgId and Cost Factor combination.",
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
public @interface DeleteCostFactorDiscreteBucketDoc {}
