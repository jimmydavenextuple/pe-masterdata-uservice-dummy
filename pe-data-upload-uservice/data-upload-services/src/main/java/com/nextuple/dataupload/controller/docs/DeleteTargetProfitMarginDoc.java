/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
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
    summary = "Delete target gross profit margin",
    description =
        "Deletes target gross profit margin for given attribute name and attribute values for a given organization ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 status code indicates that target profit margin deleted successfully.",
    content =
        @Content(
            examples = {
              @ExampleObject(
                  summary = "Target profit margin deleted successfully",
                  name =
                      "A 200 status code indicates that target profit margin deleted successfully.",
                  value =
                      """
                                            {
                                               "success": true,
                                               "requestId": "7a57df5d-97e7-4fb3-bea9-2d6822d74373",
                                               "timestamp": 1725528499655,
                                               "message": "Target profit margin(s) deleted successfully"
                                            }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 16777028</b>: Error while deleting target profit margin records</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Error while deleting target profit margin records",
                  value =
                      """
                            {
                                "success": false,
                                "requestId": "744ebad4-8970-4691-9511-ea9eccc96c11",
                                "timestamp": 1725539663569,
                                "message": "Error while deleting target profit margin records",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 16777028
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
public @interface DeleteTargetProfitMarginDoc {}
