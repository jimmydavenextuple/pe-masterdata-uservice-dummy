/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
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
    summary = "Get Item List with Configured Buffers",
    description = "Retrieves buffer IDs list for which item buffers are configured.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the item IDs with configured buffers are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = PageResponseForItemBuffer.class),
            examples = {
              @ExampleObject(
                  summary = "Item IDs with configured buffers fetched successfully",
                  name =
                      "A 200 success code indicates that item IDs with configured buffers are fetched successfully.",
                  value =
                      """
                                                              {
                                                                  "success": true,
                                                                  "requestId": "43bd7fd3-d6c1-40e6-ab68-2ad2960fd72e",
                                                                  "timestamp": 1718098503821,
                                                                  "message": "Item Details fetched successfully",
                                                                  "payload": {
                                                                      "data": [
                                                                          {
                                                                              "orgId": "NEXTUPLE_GR",
                                                                              "itemId": "itemId",
                                                                              "uom": "in"
                                                                          },
                                                                          {
                                                                              "orgId": "NEXTUPLE_GR",
                                                                              "itemId": "itemId2",
                                                                              "uom": "in"
                                                                          }
                                                                      ],
                                                                      "pagination": {
                                                                          "currentPage": 1,
                                                                          "totalPages": 1,
                                                                          "totalRecords": 2
                                                                      }
                                                                  }
                                                              }
                                                                                """),
              @ExampleObject(
                  summary = "No items with configured buffers",
                  name =
                      "A 200 success code indicates that no items are present with configured buffers",
                  value =
                      """
                                                              {
                                                                  "success": true,
                                                                  "requestId": "17fbc940-e4ab-447f-a4bc-5e27d047a707",
                                                                  "timestamp": 1718098587830,
                                                                  "message": "Item Details fetched successfully",
                                                                  "payload": {
                                                                      "data": [],
                                                                      "pagination": {
                                                                          "currentPage": 1,
                                                                          "totalPages": 0,
                                                                          "totalRecords": 0
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
    responseCode = "400",
    description = "A 400 error code indicates that the request to get items list is not valid.")
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
public @interface GetItemListWithBufferConfiguredDoc {}
