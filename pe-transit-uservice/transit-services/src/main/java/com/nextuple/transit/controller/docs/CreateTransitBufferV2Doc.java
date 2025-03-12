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
    summary = "Create transit buffer V2",
    description = "Creates a new transit buffer with the provided details.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the transit buffer is created successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransitBufferV2Response.class),
            examples = {
              @ExampleObject(
                  summary = "Transit buffer created successfully",
                  name =
                      "A 200 response code indicates that transit buffer is created successfully.",
                  value =
                      """
                                                  {
                                                      "success": true,
                                                      "requestId": "50765640-844f-4aaa-a3c5-fb08745a3dfb",
                                                      "timestamp": 1705582767764,
                                                      "message": "Transit buffer created successfully",
                                                      "payload": {
                                                          "id": 55,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "carrierServiceId": "carrier3",
                                                          "sourceGeozone": "111",
                                                          "destinationGeozone": "222",
                                                          "bufferDays": 2.0,
                                                          "bufferStartDate": "2025-03-21T12:00:00.000+00:00",
                                                          "bufferEndDate": "2025-03-22T12:00:00.000+00:00",
                                                          "transitBufferConfigRequestId": null,
                                                          "createdBy": "NEXTUPLE_GR",
                                                          "updatedBy": "NEXTUPLE_GR",
                                                          "createdDate": "2024-01-18T12:59:27.751+00:00",
                                                          "lastModifiedDate": "2024-01-18T12:59:27.751+00:00",
                                                          "customAttributes": {
                                                            "Key": "value"
                                                          }
                                                      }
                                                   }
                                                  """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is an issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Buffer end date must be in future",
                  name =
                      "A 400 response code indicates that the buffer end date must be in the future.",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "2882350e-770f-4560-88d0-384023723e54",
                                                      "timestamp": 1705582794492,
                                                      "message": "Buffer end date must be in future",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "bufferEndDate": {
                                                                  "rejectedValue": "2019-03-22T12:00:00.000+00:00"
                                                              }
                                                          }
                                                      }
                                                  }
                                                  """),
              @ExampleObject(
                  summary = "bufferEndDate should be greater than or equal to bufferStartDate",
                  name =
                      "A 400 response code indicates that bufferEndDate should be greater than or equal to bufferStartDate.",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "6b443752-aef9-46f7-a0af-4a899e31099c",
                                                      "timestamp": 1705582849362,
                                                      "message": "bufferEndDate should be greater than or equal to bufferStartDate",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6005
                                                      }
                                                  }
                                                  """)
            }))
@ApiResponse(
    responseCode = "412",
    description = "A 412 error code indicates that there is a conflict with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transit Buffer window already exists or overlaps",
                  name =
                      "A 412 response code indicates that the transit Buffer window already exists or overlaps.",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "dc3b0de6-0da0-446c-977e-a1c41dc8565d",
                                                      "timestamp": 1705582950851,
                                                      "message": "Transit Buffer window already exists or overlaps",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6004,
                                                          "fields": {
                                                              "bufferStartDate": {
                                                                  "rejectedValue": "2024-03-21T12:00:00.000+00:00"
                                                              },
                                                              "bufferEndDate": {
                                                                  "rejectedValue": "2024-03-22T12:00:00.000+00:00"
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
public @interface CreateTransitBufferV2Doc {}
