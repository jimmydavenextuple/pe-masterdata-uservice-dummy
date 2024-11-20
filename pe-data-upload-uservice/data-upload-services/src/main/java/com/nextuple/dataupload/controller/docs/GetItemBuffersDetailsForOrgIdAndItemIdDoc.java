/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.item.domain.outbound.ItemBufferResponse;
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
import org.springframework.web.ErrorResponse;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Item Buffers Details",
    description = "Retrieves item buffers for a specific organization ID, item ID and UOM.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the item buffers details are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = ItemBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item buffers details fetched successfully",
                  name =
                      "A 200 success code indicates successful retrieval of item buffers details",
                  value =
                      """
                                                              {
                                                                  "success": true,
                                                                  "requestId": "f9b28fb5-3bc0-4efd-900a-39acc8776c92",
                                                                  "timestamp": 1718098802716,
                                                                  "message": "Item buffer list fetched successfully",
                                                                  "payload": [
                                                                      {
                                                                          "id": 3,
                                                                          "orgId": "NEXTUPLE_GR",
                                                                          "itemId": "itemId2",
                                                                          "uom": "in",
                                                                          "bufferHours": 5.0,
                                                                          "bufferStartDate": "2025-02-11T22:00:00.000+00:00",
                                                                          "bufferEndDate": "2025-02-12T21:59:00.000+00:00"
                                                                      },
                                                                      {
                                                                          "id": 4,
                                                                          "orgId": "NEXTUPLE_GR",
                                                                          "itemId": "itemId2",
                                                                          "uom": "in",
                                                                          "bufferHours": 5.0,
                                                                          "bufferStartDate": "2025-02-13T22:00:00.000+00:00",
                                                                          "bufferEndDate": "2025-02-14T21:59:00.000+00:00"
                                                                      }
                                                                  ]
                                                              }
                                                                                """),
              @ExampleObject(
                  summary = "No item buffers present",
                  name =
                      "A 200 success code indicates successful retrieval of item buffers details",
                  value =
                      """
                                                              {
                                                                  "success": true,
                                                                  "requestId": "345f5edc-09ed-43b3-841a-8ad67bc56afd",
                                                                  "timestamp": 1718098825669,
                                                                  "message": "Item buffer list fetched successfully",
                                                                  "payload": []
                                                              }
                                                                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to get items buffers list is not valid.")
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
                                                            "timestamp": "1670589273234",
                                                            "payload": {
                                                              "type": "ERROR",
                                                              "code": 2
                                                            }
                                                          }
                                                          """)
            }))
@ApiResponse(
    responseCode = "403",
    description = "A 403 code indicates that the client is forbidden to send the request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Organization ID mismatched",
                  name =
                      "A 403 error code indicates that wrong organization ID is passed for given tenant.",
                  value =
                      """
                                                          {
                                                            "success": false,
                                                            "requestId": "4b608a08-e86a-448b-bea1-3bfbbda083ee#13098",
                                                            "timestamp": 1710323530881,
                                                            "message": "OrgId mismatch!",
                                                            "payload": {
                                                              "type": "ERROR",
                                                              "code": 1011
                                                            }
                                                          }
                                                          """)
            }))
public @interface GetItemBuffersDetailsForOrgIdAndItemIdDoc {}
