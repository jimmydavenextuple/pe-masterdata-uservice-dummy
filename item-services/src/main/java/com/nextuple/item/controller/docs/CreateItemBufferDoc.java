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
@Operation(summary = "Create Item Buffer", description = "Creates a buffer for an item.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the item buffer is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = ItemBufferResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item buffer successfully created",
                  name = "Item Buffer Created",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "3a57f505-71b9-4cb8-b045-fac7b7924faf",
                                            "timestamp": 1718029036332,
                                            "message": "Item Buffer successfully created",
                                            "payload": {
                                                "id": 35,
                                                "orgId": "NEXTUPLE_GR",
                                                "itemId": "itemId",
                                                "uom": "in",
                                                "bufferHours": 0.0,
                                                "bufferStartDate": "2024-11-11T22:00:00.000+00:00",
                                                "bufferEndDate": "2024-11-12T21:59:00.000+00:00"
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
                  summary = "Bad request - invalid buffer hours",
                  name = "Bad Request - Invalid Buffer Hours",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "2ea31fff-74f2-4afc-9c52-c51f80dbb478",
                                            "timestamp": 1718029102482,
                                            "message": "bufferHours cannot be negative",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 6016
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "A 404 error code indicates that no item is found for the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item not found for given details",
                  name = "Item Not Found - Invalid Details",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "34a57a67-8752-4130-9341-d7828c4f5d82",
                                            "timestamp": 1718029086569,
                                            "message": "Item not found for given details",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 8192,
                                                "fields": {
                                                    "itemId": {"rejectedValue": "itemId"},
                                                    "uom": {"rejectedValue": "each"},
                                                    "orgId": {"rejectedValue": "NEXTUPLE_GR"}
                                                }
                                            }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "412",
    description = "A 412 error code indicates a precondition failed.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item buffer window already exists or overlaps",
                  name = "Item Buffer Window Overlaps",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "11a4bf4f-c01b-4a47-bd96-65663f6fe683",
                                            "timestamp": 1718029064025,
                                            "message": "Item Buffer window already exists or overlaps",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 8193,
                                                "fields": {
                                                    "bufferStartDate": {"rejectedValue": "2024-11-11T22:00:00.000+00:00"},
                                                    "bufferEndDate": {"rejectedValue": "2024-11-12T21:59:00.000+00:00"}
                                                }
                                            }
                                        }
                                        """)
            }))
public @interface CreateItemBufferDoc {}
