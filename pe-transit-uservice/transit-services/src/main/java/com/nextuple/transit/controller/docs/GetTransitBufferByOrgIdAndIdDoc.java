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
    summary = "Get transit buffer by Organization ID and Buffer ID",
    description = "Retrieves transit buffer details for the given organization ID and buffer ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transit buffer details are fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransitBufferV2Response.class),
            examples = {
              @ExampleObject(
                  summary = "Transit buffer details fetched successfully",
                  name =
                      "A 200 response code indicates that transit buffer details are fetched successfully.",
                  value =
                      """
                                                  {
                                                      "success": true,
                                                      "requestId": "a32490c9-79c0-42cd-8990-89b83dfea4f6",
                                                      "timestamp": 1705582291361,
                                                      "message": "Transit buffer details fetched successfully",
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
                                                          "lastModifiedDate": "2024-01-18T12:43:33.620+00:00",
                                                          "customAttributes": {
                                                            "Key": "value"
                                                          }
                                                      }
                                                   }
                                                  """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the transit buffer is not found for the requested organization ID and buffer ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transit Buffer Not found for requested orgId and id",
                  name =
                      "A 404 response code indicates that the transit buffer is not found for the requested organization ID and buffer ID.",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "22eb35fa-04b8-41e4-8b43-781ef2fb3e3d",
                                                      "timestamp": 1705582316211,
                                                      "message": "Transit Buffer Not found for requested orgId and id",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6006,
                                                          "fields": {
                                                              "id": {
                                                                  "rejectedValue": 100
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
public @interface GetTransitBufferByOrgIdAndIdDoc {}
