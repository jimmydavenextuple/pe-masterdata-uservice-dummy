/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Upsert Zip Code",
    description =
        "Creates a new zip code if not already present or updates an existing one using orgId and zipCode as the unique key.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the zip code was successfully created or updated.",
    content =
        @Content(
            schema = @Schema(implementation = PostalCodeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates zip code successfully created.",
                  name = "Zip code created response",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "38320238-e52a-4884-963d-9e741ce8d1bc",
                                            "timestamp": 1745405303834,
                                            "message": "Zip Code successfully saved!",
                                            "payload": {
                                                "customAttributes": null,
                                                "orgId": "NEXTUPLE_GR",
                                                "zipCode": "XYZABC",
                                                "zipCodePrefix": "XYZ",
                                                "country": "CA",
                                                "state": "NY",
                                                "city": "New york",
                                                "latitude": "90.3",
                                                "longitude": "973.3",
                                                "timeZone": "EST"
                                            }
                                        }
                                        """),
              @ExampleObject(
                  summary = "Indicates zip code successfully updated.",
                  name = "Zip code updated response",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "70c152d8-44a1-46c7-97de-8fb22cad7f95",
                                            "timestamp": 1745405336807,
                                            "message": "Zip Code successfully saved!",
                                            "payload": {
                                                "customAttributes": null,
                                                "orgId": "NEXTUPLE_GR",
                                                "zipCode": "XYZABC",
                                                "zipCodePrefix": "XYZ",
                                                "country": "CA",
                                                "state": "NY",
                                                "city": "New york",
                                                "latitude": "90.3",
                                                "longitude": "93.3",
                                                "timeZone": "EST"
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates validation issues with the provided fields. One or more required fields may be missing or invalid.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Field validation failure.",
                  name = "Bad request example",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "49662eff-61c1-4d7e-baa1-01fbcd0a1458",
                                            "timestamp": 1745405365698,
                                            "message": "Bad Request",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2,
                                                "fields": {
                                                    "zipCodePrefix": {
                                                        "rejectedValue": "null",
                                                        "errorMessage": "zipCodePrefix can't be empty"
                                                    }
                                                }
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "500",
    description =
        "A 500 error code indicates an internal server error occurred while processing the request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Unexpected server error.",
                  name = "Internal server error",
                  value =
                      """
                                        {
                                            "success": false,
                                            "timestamp": "1670589273234",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2
                                            }
                                        }
                                        """)
            }))
public @interface UpsertPostalCodeDoc {}
