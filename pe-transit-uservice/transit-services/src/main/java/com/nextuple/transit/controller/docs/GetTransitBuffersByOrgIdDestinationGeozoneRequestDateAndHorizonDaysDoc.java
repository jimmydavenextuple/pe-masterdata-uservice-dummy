/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
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
    summary = "Get Transit Buffers by OrgId and Destination Geozone",
    description =
        "Retrieves the transit buffer list for the given organization ID, destination geo zone, request date and horizon days.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transit buffer list is retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransitBufferDetailsResponse.class),
            examples = {
              @ExampleObject(
                  value =
                      """
                                {
                                    "success": true,
                                    "requestId": "70086089-e49f-4697-8d3f-d51fec92e2b5",
                                    "timestamp": 1705577212378,
                                    "message": "Transit buffers fetched successfully",
                                    "payload": [
                                        {
                                            "orgId": "XYZINC",
                                            "carrierServiceId": "ALL-EXPRESS",
                                            "sourceGeozone": "104",
                                            "destinationGeozone": "10E",
                                            "transitBuffers": [
                                                {
                                                    "bufferDays": 3.0,
                                                    "bufferStartDate": "2024-01-23T00:00:00.000+00:00",
                                                    "bufferEndDate": "2024-01-24T10:00:00.000+00:00",
                                                    "customAttributes": {
                                                        "Key": "value"
                                                    }
                                                },
                                                {
                                                    "bufferDays": 3.0,
                                                    "bufferStartDate": "2024-01-17T12:00:01.000+00:00",
                                                    "bufferEndDate": "2024-01-18T12:00:00.000+00:00",
                                                    "customAttributes": {
                                                        "Key": "value"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Request date is not passed.</li>"
            + "<li><b>Error code: 4</b>: Horizon days is not passed.</li>"
            + "<li><b>Error code: 2</b>: Horizon days is negative.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Request date is not passed",
                  name = "A 400 response code indicates that the the request date is not passed.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "82d88f8e-6aa6-4bf3-98d0-4e1d0953920c",
                                            "timestamp": 1705579104443,
                                            "message": "Required request parameter 'requestDate' for method parameter type LocalDate is not present",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 4
                                            }
                                        }
                                        """),
              @ExampleObject(
                  summary = "Horizon days is not passed",
                  name = "A 400 response code indicates that the the horizon days is not passed.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "c278aced-f40e-4aee-8cb2-93dfef108308",
                                            "timestamp": 1705579125894,
                                            "message": "Required request parameter 'horizonDays' for method parameter type Integer is not present",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 4
                                            }
                                        }
                                        """),
              @ExampleObject(
                  summary = "Horizon days is negative",
                  name = "A 400 response code indicates that the the horizon days is negative.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "d85bd6b6-f1dc-4322-9f2b-434f79c4e94c",
                                            "timestamp": 1705579138355,
                                            "message": "Bad Request",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2,
                                                "fields": {
                                                    "horizonDays": {
                                                        "rejectedValue": "-10",
                                                        "errorMessage": "horizonDays can't be negative"
                                                    }
                                                }
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that data is not found ."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Transit buffer details not found with given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transit buffer details not found with given details",
                  name =
                      "A 404 error code indicates that transit buffer details not found with given details",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "cfa979ee-de3b-4e1a-b0ea-38585fb6a886",
                                            "timestamp": 1705579352805,
                                            "message": "Transit buffer details not found with given details",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6001,
                                                "fields": {
                                                    "destinationGeozone": {
                                                        "rejectedValue": "10E"
                                                    },
                                                    "horizonDays": {
                                                        "rejectedValue": 10
                                                    },
                                                    "requestDate": {
                                                        "rejectedValue": "2024-03-23"
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
public @interface GetTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDaysDoc {}
