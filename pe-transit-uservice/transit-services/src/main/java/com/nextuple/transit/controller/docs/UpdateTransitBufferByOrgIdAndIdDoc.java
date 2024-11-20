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
    summary = "Update transit buffer by Organization ID and Buffer ID",
    description = "Updates the transit buffer details for the given organization ID and buffer ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the transit buffer is updated successfully.",
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
                                                      "requestId": "3d0a68f8-1944-4085-803c-e364d51883e5",
                                                      "timestamp": 1705581813640,
                                                      "message": "Transit buffer updated successfully",
                                                      "payload": {
                                                          "id": 49,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "carrierServiceId": "carrier3",
                                                          "sourceGeozone": "111",
                                                          "destinationGeozone": "222",
                                                          "bufferDays": 3200.0,
                                                          "bufferStartDate": "2024-03-26T05:10:00.000+00:00",
                                                          "bufferEndDate": "2024-03-26T05:10:00.000+00:00",
                                                          "transitBufferConfigRequestId": null,
                                                          "createdBy": "NEXTUPLE_GR",
                                                          "updatedBy": "NEXTUPLE_GR",
                                                          "createdDate": "2024-01-18T05:41:08.057+00:00",
                                                          "lastModifiedDate": "2024-01-18T12:43:33.620+00:00"
                                                      }
                                                   }
                                                  """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Buffer end date must be in the future.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Buffer end date must be in the future",
                  name =
                      "A 400 response code indicates that the buffer end date must be in the future.",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "da87de95-f5e0-4fca-84a1-4d56e67ecfa5",
                                                      "timestamp": 1705581903341,
                                                      "message": "Buffer end date must be in the future",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6002,
                                                          "fields": {
                                                              "bufferEndDate": {
                                                                  "rejectedValue": "2023-03-26T05:13:00.000+00:00"
                                                              }
                                                          }
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
                                                        "code": 6006,
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
    responseCode = "412",
    description =
        "A 412 error code indicates that there is some conflict with the input."
            + "<ul>"
            + "<li><b>Error code: 6003</b>: Transit Buffer window already exists or overlaps.</li>"
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
                                                        "requestId": "487ee03c-52c1-4076-8520-3ee3460d6bbd",
                                                        "timestamp": 1705581857620,
                                                        "message": "Transit Buffer window already exists or overlaps",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6004,
                                                            "fields": {
                                                                "bufferStartDate": {
                                                                    "rejectedValue": "2023-03-26T05:10:00.000+00:00"
                                                                },
                                                                "bufferEndDate": {
                                                                    "rejectedValue": "2024-03-26T05:10:00.000+00:00"
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
public @interface UpdateTransitBufferByOrgIdAndIdDoc {}
