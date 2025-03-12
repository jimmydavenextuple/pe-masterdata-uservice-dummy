/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
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
    summary = "Fetch transfer schedules list",
    description = "Retrieves transfer schedules list")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transfer schedules are fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransferScheduleResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transfer schedules fetched successfully",
                  name =
                      "A 200 response code indicates that transfer schedules are fetched successfully.",
                  value =
                      """
                               {
                                   "success": true,
                                   "requestId": "85e6ba48-65b4-415f-b889-0f4d9be94ca6",
                                   "timestamp": 1725006810795,
                                   "message": "Transfer Schedule details fetched successfully",
                                   "payload": {
                                       "data": [
                                           {
                                               "id": 6,
                                               "orgId": "NEXTUPLE",
                                               "sourceNodeId": "NODE4",
                                               "dropoffNodeId": "CAPNode1",
                                               "startTime": "2024-08-30T12:00:00.000Z",
                                               "endTime": "2024-08-30T15:00:00.000Z",
                                                  "customAttributes": {
                                                    "dynamicAtrr": true
                                                  }
                                           },
                                           {
                                               "id": 7,
                                               "orgId": "NEXTUPLE",
                                               "sourceNodeId": "NODE4",
                                               "dropoffNodeId": "CAPNode1",
                                               "startTime": "2024-08-29T12:00:00.000Z",
                                               "endTime": "2024-08-29T15:00:00.000Z",
                                                    "customAttributes": {
                                                        "dynamicAtrr": true
                                                    }
                                           }
                                       ],
                                       "pagination": {
                                           "next": "transfer-schedule/orgId/NEXTUPLE?pageNo=2&pageSize=2",
                                           "previous": "",
                                           "totalPages": 2,
                                           "currentPage": 1,
                                           "totalRecords": 4,
                                           "sortOrder": "DESC",
                                           "sortBy": "sourceNodeId"
                                       },
                                       "aggregation": null
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
public @interface FetchTransferScheduleListDoc {}
