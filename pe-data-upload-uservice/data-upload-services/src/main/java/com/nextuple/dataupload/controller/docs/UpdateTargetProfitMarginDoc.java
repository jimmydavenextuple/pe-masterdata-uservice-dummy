/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
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
    summary = "Update target gross profit margin",
    description =
        "Updates target gross profit margin for given attribute name and attribute value for a given organization ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 status code indicates that target profit margin updated successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TargetProfitMarginResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Target profit margin updated successfully",
                  name =
                      "A 200 status code indicates that target profit margin updated successfully.",
                  value =
                      """
                                                              {
                                                                 "success": true,
                                                                 "requestId": "7a57df5d-97e7-4fb3-bea9-2d6822d74373",
                                                                 "timestamp": 1725528499655,
                                                                 "message": "Target profit margin updated successfully",
                                                                 "payload": {
                                                                    "orgId": "NEXTUPLE_GR",
                                                                    "attributeName": "itemCategory",
                                                                    "attributeValue": "ELECTRONICS",
                                                                    "targetGrossProfitMargin": 20
                                                                 }
                                                              }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6004</b>: Target profit margin not configured for attribute.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Target profit margin not configured",
                  name =
                      "A 400 status code indicates that target profit margin not configured for given orgId, attributeName and attributeValue.",
                  value =
                      """
                                                              {
                                                                 "success": false,
                                                                 "requestId": "85ee319b-815d-445f-8319-8cb8cb2aabdf",
                                                                 "timestamp": 1725528715791,
                                                                 "message": "Target profit margin not configured for given orgId , attributeName and attributeValue.",
                                                                 "payload": {
                                                                    "type": "ERROR",
                                                                    "code": 6004,
                                                                    "fields": {
                                                                       "configKey": {
                                                                          "rejectedValue": "target-gross-profit-margins-itemCategory"
                                                                       },
                                                                       "attributeValue": {
                                                                          "rejectedValue": "ELECTRONICS"
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
                                                                "requestId": "75d5537d-60a6-4a2c-999f-07e68d8a36d4",
                                                                "timestamp": 1698040474473,
                                                                "payload": {
                                                                  "type": "ERROR",
                                                                  "code": 2
                                                                }
                                                              }
                                                          """)
            }))
public @interface UpdateTargetProfitMarginDoc {}
