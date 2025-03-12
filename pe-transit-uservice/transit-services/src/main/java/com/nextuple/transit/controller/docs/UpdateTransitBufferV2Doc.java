/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
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
    summary = "Update transit buffer",
    description = "Updates the transit buffer details for the given transit buffer request")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the is transit buffer is updated successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransitBufferV2Response.class),
            examples = {
              @ExampleObject(
                  summary = "Transit buffer updated successfully",
                  name =
                      "A 200 response code indicates that transit buffer is updated successfully.",
                  value =
                      """
                                {
                                    {
                                             "success": true,
                                             "requestId": "41ea3a4b-754b-4748-a846-017f2145fb4a",
                                             "timestamp": 1705579607571,
                                             "message": "Transit buffer updated successfully",
                                             "payload": {
                                                 "id": 8,
                                                 "orgId": "XYZINC",
                                                 "carrierServiceId": "ALL-EXPRESS",
                                                 "sourceGeozone": "104",
                                                 "destinationGeozone": "10E",
                                                 "bufferDays": 3.0,
                                                 "bufferStartDate": "2024-02-18T12:00:00.000+00:00",
                                                 "bufferEndDate": "2024-02-23T12:00:00.000+00:00",
                                                 "transitBufferConfigRequestId": null,
                                                 "createdBy": "NEXTUPLE_GR",
                                                 "updatedBy": "NEXTUPLE_GR",
                                                 "createdDate": "2024-01-18T06:49:17.609+00:00",
                                                 "lastModifiedDate": "2024-01-18T07:03:53.296+00:00",
                                                 "customAttributes": {
                                                    "Key": "value"
                                                 }
                                             }
                                         }
                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Invalid transitBufferConfigRequestId is passed in the body.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Invalid transitBufferConfigRequestId is passed",
                  name =
                      "A 400 response code indicates that the invalid transitBufferConfigRequestId is passed.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "a34faabf-29e4-4848-8d62-4e9a825c7f2f",
                                            "timestamp": 1705579815388,
                                            "message": "Invalid transit buffer config request Id",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001,
                                                "fields": {
                                                    "transitBufferConfigRequestId": {
                                                        "rejectedValue": 89
                                                    }
                                                }
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "412",
    description =
        "A 412 error code indicates that there is some conflict with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Transit Buffer window already exists or overlaps.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transit Buffer window already exists or overlaps",
                  name =
                      "A 400 response code indicates that the transit Buffer window already exists or overlaps.",
                  value =
                      """
                                        {
                                              "success": false,
                                              "requestId": "928bb829-5403-40bb-a629-1819125f319f",
                                              "timestamp": 1705580231183,
                                              "message": "Transit Buffer window already exists or overlaps",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 6004,
                                                  "fields": {
                                                      "bufferStartDate": {
                                                          "rejectedValue": "2024-02-18T12:00:00.000+00:00"
                                                      },
                                                      "bufferEndDate": {
                                                          "rejectedValue": "2024-02-25T12:00:00.000+00:00"
                                                      },
                                                      "orgId": {
                                                          "rejectedValue": "XYZINC"
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
                  summary = "There was some error on server while processing the request.",
                  name =
                      "A 500 error code indicates that there was some error on the server while processing the request.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "3b8b4852-93a6-4570-b685-1d2771ebce56",
                                            "timestamp": 1705579225540,
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2
                                            }
                                        }
                                        """)
            }))
public @interface UpdateTransitBufferV2Doc {}
