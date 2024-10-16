/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
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
    summary = "Update Cost Attribute Mapping",
    description =
        "Updates a Cost Attribute Mapping for a given organization ID and Cost Attribute Mapping ID.")
@ApiResponse(
    responseCode = "200",
    description = "When Cost Attribute Mapping is successfully updated",
    content =
        @Content(
            schema = @Schema(implementation = CostAttributeMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Attribute Mapping is successfully updated.",
                  name =
                      "A 200 OK response indicates that the Cost Attribute Mapping is successfully updated.",
                  value =
                      """
                             {
                                           "success": true,
                                           "requestId": "0850509e-9b01-43f1-bdf7-1f6fda27f069#6094",
                                           "timestamp": 1701950221609,
                                           "message": "Cost Attribute Mapping updated successfully!",
                                           "payload": {
                                               "id": 16,
                                               "orgId": "NEXTUPLE_GR",
                                               "canonicalName": "width",
                                               "displayName": "Width",
                                               "attributeName": "width"
                                           }
                                       }
                         """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "<ul>"
            + "<li><b>Error code: 6001</b>: When combination of org ID and canonical name is not unique</li>"
            + "<li><b>Error code: 6001</b>: When combination of org ID and product attribute name is not unique</li>"
            + "<li><b>Error code: 6001</b>: When the canonical name is invalid</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When combination of org ID and canonical name is not unique.",
                  name =
                      "A 400 response indicates that the combination of org ID and canonical name is not unique",
                  value =
                      """
                        {
                            "success": false,
                            "requestId": "44bf20fe-abde-45b2-9f7d-f3ba767d5292#5884",
                            "timestamp": 1701948640095,
                            "message": "Combination of orgId and canonicalName should be unique",
                            "payload": {
                                "type": "ERROR",
                                "code": 6001,
                                "fields": {
                                    "orgId": {
                                        "rejectedValue": "NEXTUPLE_GR"
                                    },
                                    "canonicalName": {
                                        "rejectedValue": "length"
                                    }
                                }
                            }
                        }
                  """),
              @ExampleObject(
                  summary = "When combination of org ID and product attribute name is not unique",
                  name =
                      "A 400 response indicate that the combination of org ID and product attribute name is not unique",
                  value =
                      """
                              {
                                      "success": false,
                                      "requestId": "0850509e-9b01-43f1-bdf7-1f6fda27f069#5751",
                                      "timestamp": 1701948535240,
                                      "message": "Combination of orgId and attributeName should be unique",
                                      "payload": {
                                          "type": "ERROR",
                                          "code": 6001,
                                          "fields": {
                                              "attributeName": {
                                                  "rejectedValue": "height"
                                              },
                                              "orgId": {
                                                  "rejectedValue": "NEXTUPLE_GR"
                                              }
                                          }
                                      }
                                  }
                        """),
              @ExampleObject(
                  summary = "When the canonical name is invalid",
                  name = "A 400 response indicates that the canonical name is invalid",
                  value =
                      """
                        {
                          "success": false,
                          "requestId": "02d5406f-5269-40a6-abf7-d9b5d1c1701d#4690",
                          "timestamp": 1701948902687,
                          "message": "Invalid format! Only alphanumeric characters allowed.",
                          "payload": {
                              "type": "ERROR",
                              "code": 6001,
                              "fields": {
                                  "name": {
                                      "rejectedValue": "item-Height"
                                  }
                              }
                          }
                      }
                      """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Attribute Mapping is not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Attribute Mapping is not found.",
                  name =
                      "A 404 response indicates that the given Cost Attribute Mapping is not found.",
                  value =
                      """
                      {
                         "success": false,
                         "requestId": "02d5406f-5269-40a6-abf7-d9b5d1c1701d#4917",
                         "timestamp": 1701949947754,
                         "message": "Cost attribute mapping details not found",
                         "payload": {
                             "type": "ERROR",
                             "code": 6001,
                             "fields": {
                                 "costAttributeMappingId": {
                                     "rejectedValue": 1600
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
        "When the cost attribute for which mapping is added is not configured in the cost attribute db.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When the cost attribute for which mapping is added is not configured in the cost attribute db.",
                  name =
                      "A 412 response indicates that the cost attribute for which mapping is added is not configured in the cost attribute db.",
                  value =
                      """
                            {
                                    "success": false,
                                    "requestId": "44bf20fe-abde-45b2-9f7d-f3ba767d5292#5892",
                                    "timestamp": 1701948713532,
                                    "message": "Can't add cost attribute mapping as the cost attribute doesn't exist",
                                    "payload": {
                                        "type": "ERROR",
                                        "code": 6001,
                                        "fields": {
                                            "costAttributeName": {
                                                "rejectedValue": "length1"
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
public @interface UpdateCostAttributeMapping {}
