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
                                            "success": true,
                                            "requestId": "761f6f66-e116-4e41-85f9-bf708592cc1b",
                                            "timestamp": 1705580491569,
                                            "message": "Transit buffer deleted successfully",
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
                                                "lastModifiedDate": "2024-01-18T07:03:53.296+00:00"
                                            }
                                        }
                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that transit buffer is not found."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Transit buffer is invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transit buffer is invalid",
                  name = "A 404 response code indicates that the transit Buffer is invalid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "189acbbc-37bc-4bd7-b2ad-e2e810d0c166",
                                                    "timestamp": 1706005483124,
                                                    "message": "Invalid transit buffer",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "transitBufferConfigRequestId": {
                                                                "rejectedValue": 49
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
public @interface DeleteTransitBufferV2Doc {}
