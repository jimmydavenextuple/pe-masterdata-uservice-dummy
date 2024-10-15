/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller.docs;

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
    summary = "Get Item Buffer",
    description = "Retrieves an item buffer by organization ID and buffer ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the item buffer is successfully fetched.",
    content =
        @Content(
            schema = @Schema(implementation = ItemBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item buffer successfully fetched",
                  name = "Item Buffer Fetched",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "cbeb7f92-854d-48d1-87c9-17fe65640e9c",
                                            "timestamp": 1718176079071,
                                            "message": "Item Buffer successfully fetched",
                                            "payload": {
                                                "id": 3,
                                                "orgId": "NEXTUPLE_GR",
                                                "itemId": "itemId2",
                                                "uom": "in",
                                                "bufferHours": 1.0,
                                                "bufferStartDate": "2025-02-21T22:00:00.000+00:00",
                                                "bufferEndDate": "2025-02-23T21:59:00.000+00:00"
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that the request to get item buffer is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that no item buffer is found for the given organization ID and buffer ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item buffer not found for given orgId and Id",
                  name = "Item Buffer Not Found",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "5ab7980d-72f1-427d-b3bf-3b53a9f979ed",
                                            "timestamp": 1718176113715,
                                            "message": "Item buffer not found for given orgId and Id",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6017,
                                                "fields": {
                                                    "id": {"rejectedValue": 33},
                                                    "orgId": {"rejectedValue": "NEXTUPLE_GR"}
                                                }
                                            }
                                        }
                                        """)
            }))
public @interface GetItemBufferByOrgIdAndIdDoc {}
