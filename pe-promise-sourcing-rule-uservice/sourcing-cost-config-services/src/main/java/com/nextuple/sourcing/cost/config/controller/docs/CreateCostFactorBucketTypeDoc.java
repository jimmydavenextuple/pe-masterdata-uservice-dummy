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
    summary = "Create Cost Factor Bucket Type",
    description = "Creates a Cost Factor Bucket Type for a given organization ID.")
@ApiResponse(
    responseCode = "201",
    description = "When Cost Factor Bucket Type is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorBucketTypeDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factor Bucket Type is successfully created.",
                  name =
                      "A 201 response indicates that the Cost Factor Bucket Type is successfully created.",
                  value =
                      """
                                            {
                                                "success": true,
                                                "requestId": "db0ca977-a91c-40b0-8f42-e827a6e58634#1572",
                                                "timestamp": 1702272417827,
                                                "message": "Cost Factor Bucket type created successfully!",
                                                "payload": {
                                                    "id": 392,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "costFactor": "BillWeightUps",
                                                    "bucketType": "CONTIGUOUS"
                                                }
                                            }
                                            """)
            }))
@ApiResponse(
    responseCode = "412",
    description =
        "<ul>"
            + "<li><b>Error code: 6001</b>: When combination of org ID and cost factor is not found.</li>"
            + "<li><b>Error code: 6001</b>: When combination of org ID and cost factor is not unique.</li>"
            + "<li><b>Error code: 6001</b>: When a cost itinerary for the cost factor is in CREATED state.</li>"
            + "<li><b>Error code: 6019</b>: When rate card lookup is disabled for cost factor.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When combination of org ID and cost factor is not found.",
                  name =
                      "A 412 response indicates that the combination of org ID and cost factor is not found.",
                  value =
                      """
                                                {
                                                        "success": false,
                                                        "requestId": "db0ca977-a91c-40b0-8f42-e827a6e58634#1560",
                                                        "timestamp": 1702272245946,
                                                        "message": "Cost Factor not found.",
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
                                        """),
              @ExampleObject(
                  summary = "When combination of org ID and cost factor is not unique.",
                  name =
                      "A 412 response indicates that the combination of org ID and cost factor is not unique.",
                  value =
                      """
                                        {
                                                  "success": false,
                                                  "requestId": "47d3b065-1201-4811-8d6f-16dd86a5df58#542",
                                                  "timestamp": 1702272725846,
                                                  "message": "OrgId and Cost Factor combination should be unique.",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6001,
                                                      "fields": {
                                                          "costFactor": {
                                                              "rejectedValue": "BillWeightUps"
                                                          },
                                                          "orgId": {
                                                              "rejectedValue": "NEXTUPLE_GR"
                                                          }
                                                      }
                                                  }
                                              }
                                       """),
              @ExampleObject(
                  summary = "When a cost itinerary for the cost factor is in CREATED state.",
                  name =
                      "A 412 response indicates that the cost itinerary for the cost factor is in CREATED state.",
                  value =
                      """
                                        {
                                                  "success": false,
                                                  "requestId": "db0ca977-a91c-40b0-8f42-e827a6e58634#2031",
                                                  "timestamp": 1702273739524,
                                                  "message": "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6001,
                                                      "fields": {
                                                          "costFactor": {
                                                              "rejectedValue": "billWeightNational"
                                                          },
                                                          "orgId": {
                                                              "rejectedValue": "NEXTUPLE_GR"
                                                          }
                                                      }
                                                  }
                                              }
                                       """),
              @ExampleObject(
                  summary = "When rate card lookup is disabled for cost factor.",
                  name =
                      "A 412 response indicates that the cost factor bucket type cannot be created as rate card lookup is disabled for cost factor.",
                  value =
                      """
                                              {
                                         "success": false,
                                         "requestId": "3b04ab64-f2e2-44b0-9b03-f9b8f1cdebce",
                                         "timestamp": 1719398133327,
                                         "message": "Cost factor bucket type cannot be created as rate card lookup is disabled for cost factor.",
                                         "payload": {
                                             "type": "ERROR",
                                             "code": 6019,
                                             "fields": {
                                                 "costFactor": {
                                                     "rejectedValue": "BillWeightDoordash4"
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
public @interface CreateCostFactorBucketTypeDoc {}
