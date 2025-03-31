/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleBatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Batch Transfer Schedule APIs",
    description = "This API is used to create and delete transfer schedule.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the all batch transfer schedule requests are processed successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransferScheduleBatchResponse.class),
            examples = {
              @ExampleObject(
                  summary = "The batch transfer schedule requests are processed successfully.",
                  name =
                      "A 200 response code indicates that all the batch transfer schedule requests are processed successfully.",
                  value =
                      """
                                                          {
                                                            "success": true,
                                                            "requestId": "c6eddeb7-7f81-489b-86d0-a66ed45dffe1",
                                                            "timestamp": 1743445461668,
                                                            "message": "Batch processing completed with errors.",
                                                            "payload": {
                                                              "totalCount": 2,
                                                              "successCount": 1,
                                                              "failureCount": 1,
                                                              "results": [
                                                                {
                                                                  "index": 2,
                                                                  "success": true,
                                                                  "message": "Transfer schedule deleted successfully",
                                                                  "statusCode": 200
                                                                },
                                                                {
                                                                  "index": 1,
                                                                  "success": true,
                                                                  "message": "Transfer schedule created successfully",
                                                                  "statusCode": 200
                                                                }
                                                              ]
                                                            }
                                                          }
                                                  """)
            }))
@ApiResponse(
    responseCode = "207",
    description =
        "A 207 success code indicates that a few batch transfer schedule requests are processed successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransferScheduleBatchResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Batch transfer schedule requests are processed with a few failures.",
                  name = "A 404 response code indicates that the transfer schedule is not found.",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "c6eddeb7-7f81-489b-86d0-a66ed45dffe1",
                                                    "timestamp": 1743445461668,
                                                    "message": "Batch processing completed with a few failures.",
                                                    "payload": {
                                                      "totalCount": 2,
                                                      "successCount": 1,
                                                      "failureCount": 1,
                                                      "results": [
                                                        {
                                                          "index": 1,
                                                          "success": true,
                                                          "message": "Transfer schedule created successfully",
                                                          "statusCode": 200
                                                        },
                                                        {
                                                          "index": 2,
                                                          "success": false,
                                                          "message": "Transfer schedule not found",
                                                          "statusCode": 404
                                                        }
                                                      ]
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
public @interface BatchTransferScheduleDoc {}
