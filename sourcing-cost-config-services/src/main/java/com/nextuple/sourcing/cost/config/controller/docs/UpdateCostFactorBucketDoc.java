/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
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
    summary = "Update Cost Factors Bucket Type",
    description = "Updates the Cost Factors Bucket Type details.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Factors Bucket type details are successfully updated.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorBucketTypeDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factors Bucket type details are successfully updated.",
                  name =
                      "A 200 response indicates that the Cost Factors Bucket type details are successfully updated.",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "e19f6ed8-df3a-44ff-82dc-5e600e0d9ded#8831",
                                                     "timestamp": 1702275926256,
                                                     "message": "Cost Factor Bucket type updated successfully!",
                                                     "payload": {
                                                         "id": 396,
                                                         "orgId": "NEXTUPLE_GR",
                                                         "costFactor": "billWeightNational1",
                                                         "bucketType": "CONTIGUOUS"
                                                     }
                                                 }
                                            """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "When a bucket type can't updated as values are assigned to existing bucket type.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a bucket type can't updated as values are assigned to existing bucket type.",
                  name =
                      "A 400 response indicates that the bucket type can't updated as values are assigned to existing bucket type.",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "47d3b065-1201-4811-8d6f-16dd86a5df58#1050",
                                                      "timestamp": 1702274670101,
                                                      "message": "Can't update bucket type as values are assigned to existing bucket type",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "costFactor": {
                                                                  "rejectedValue": "BillWeightUps"
                                                              },
                                                              "existingBucketType": {
                                                                  "rejectedValue": "CONTIGUOUS"
                                                              },
                                                              "orgId": {
                                                                  "rejectedValue": "NEXTUPLE_GR"
                                                              },
                                                              "newBucketType": {
                                                                  "rejectedValue": "DISCRETE"
                                                              }
                                                          }
                                                      }
                                                  }
                                            """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "When a given Cost Factor and orgId combination is not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor and orgId combination is not found.",
                  name =
                      "A 404 response indicates that the Cost Factor and orgId combination is not found.",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "e19f6ed8-df3a-44ff-82dc-5e600e0d9ded#8895",
                                                         "timestamp": 1702276721770,
                                                         "message": "OrgId and Cost Factor combination not found.",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "costFactor": {
                                                                     "rejectedValue": "billWeightNational1"
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
            + "<li> When a given Cost Factor is not found for the orgId.</li>"
            + "<li> When a Cost Itinerary for given Cost Factor is in CREATED state.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor is not found for the orgId.",
                  name =
                      "A 412 response indicates that the given Cost Factor is not found for the orgId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "658163bd-7452-4962-9dbc-fa010d246100#193",
                                                    "timestamp": 1702274359336,
                                                    "message": "Cost Factor not found.",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "costFactor": {
                                                                "rejectedValue": "BillWeightNational"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                        """),
              @ExampleObject(
                  summary = "When a Cost Itinerary for given Cost Factor is in CREATED state.",
                  name =
                      "A 412 response indicates that the Cost Itinerary for given Cost Factor is in CREATED state.",
                  value =
                      """
                                        {
                                                "success": false,
                                                "requestId": "e19f6ed8-df3a-44ff-82dc-5e600e0d9ded#8499",
                                                "timestamp": 1702274766349,
                                                "message": "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "costFactor": {
                                                            "rejectedValue": "BillWeightUPS"
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
public @interface UpdateCostFactorBucketDoc {}
