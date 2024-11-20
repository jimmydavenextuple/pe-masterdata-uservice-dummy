/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.dataupload.common.outbound.AttributeAndValuesTGMResponse;
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
    summary = "Fetch attribute details",
    description = "Fetches attribute details for given organization ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 status code indicates that attribute details fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = AttributeAndValuesTGMResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Attribute details fetched successfully",
                  name = "A 200 status code indicates that attribute details fetched successfully.",
                  value =
                      """
                                 {
                                     "success": true,
                                     "requestId": "146a238c-7587-4f7d-9729-b663b07a3528",
                                     "timestamp": 1725605152431,
                                     "message": "Attribute fetched successfully",
                                     "payload": {
                                             "orgId": "NEXTUPLE_GR",
                                             "attributeName": "itemCategory",
                                             "attributeValue": ["KITCHEN"]
                                     }
                                 }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 16777031</b>: Error while fetching attribute and it's values</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Error while fetching attribute and it's values",
                  value =
                      """
                            {
                                "success": false,
                                "requestId": "744ebad4-8970-4691-9511-ea9eccc96c11",
                                "timestamp": 1725539663569,
                                "message": "Error while fetching attribute and it's values",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 16777031
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
public @interface FetchAttributeDetailsDoc {}
