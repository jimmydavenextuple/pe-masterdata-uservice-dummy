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
    summary = "Get Cost Attribute Mapping By Organization ID And Cost Attribute Mapping ID",
    description =
        "Fetches Cost Attribute Mapping details for a given organization ID and Cost Attribute Mapping ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "When Cost Attribute Mapping details fetched successfully for a given org ID and Id.",
    content =
        @Content(
            schema = @Schema(implementation = CostAttributeMappingResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Cost Attribute Mapping details fetched successfully for a given org ID and Id.",
                  name =
                      "A 200 OK response indicates that the Cost Attribute Mapping details fetched successfully for a given org ID and Id.",
                  value =
                      """
                                             {
                                                   "success": true,
                                                   "requestId": "0850509e-9b01-43f1-bdf7-1f6fda27f069#5977",
                                                   "timestamp": 1701949524357,
                                                   "message": "Cost Attribute Mapping details fetched successfully!",
                                                   "payload": {
                                                       "id": 16,
                                                       "orgId": "NEXTUPLE_GR",
                                                       "canonicalName": "breadth",
                                                       "displayName": "Width",
                                                       "attributeName": "width"
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
                                                     "requestId": "0850509e-9b01-43f1-bdf7-1f6fda27f069#5986",
                                                     "timestamp": 1701949543648,
                                                     "message": "Cost attribute mapping details not found",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "costAttributeMappingId": {
                                                                 "rejectedValue": 161
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
public @interface GetCostAttributeMappingByOrgIdAndCostAttributeMappingId {}
