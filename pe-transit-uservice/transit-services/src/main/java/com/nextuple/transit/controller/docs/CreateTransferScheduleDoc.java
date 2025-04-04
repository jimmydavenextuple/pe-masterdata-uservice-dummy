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
    summary = "Create transfer schedule",
    description = "Creates a new transfer schedule with the provided details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transfer schedule is created successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransferScheduleResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transfer schedules created successfully",
                  name =
                      "A 200 response code indicates that the transfer schedule is created successfully.",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "2889b24d-2532-45d2-acbd-347157d65573",
                                            "timestamp": 1723527641428,
                                            "message": "Transfer Schedule created successfully.",
                                            "payload": {
                                                "id": 14,
                                                "orgId": "NEXTUPLE_GR",
                                                "sourceNodeId": "Node1",
                                                "dropoffNodeId": "Node4",
                                                "startTime": "2024-06-30T20:30:00.000Z",
                                                "endTime": "2024-06-30T22:30:00.000Z",
                                                "customAttributes": {
                                                    "dynamicAtrr": true
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
                  summary = "DropOff node id can't be blank",
                  name = "A 400 response code indicates that the droppff node is blank",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "0dac7ccf-ec80-40fc-92f0-450761e5ef47",
                                            "timestamp": 1723527850776,
                                            "message": "Bad Request",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2,
                                                "fields": {
                                                    "dropoffNodeId": {
                                                        "rejectedValue": "null",
                                                        "errorMessage": "DropOff node id can't be blank"
                                                    }
                                                }
                                            }
                                        }
                                        """),
              @ExampleObject(
                  summary =
                      "Combination of orgId, source node id, drop off node and start time is not unique",
                  name =
                      "A 400 response code indicates that the combination of orgId, source node id, drop off node id and start time is not unique.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "f8d7e01d-98c6-4751-b00b-1f344fda4d5d",
                                            "timestamp": 1723527928632,
                                            "message": "Unable to save transfer schedule could not execute statement [ERROR: duplicate key value violates unique constraint \\"uk9utfu5qorjrpipap2uga7kmt0\\"\\n  Detail: Key (org_id, source_node_id, dropoff_node_id, start_time)=(NEXTUPLE_GR, Node1, Node4, 2024-06-30 20:30:00) already exists.] [insert into transfer_schedules (created_by,created_date,dropoff_node_id,end_time,last_modified_date,org_id,source_node_id,start_time,updated_by) values (?,?,?,?,?,?,?,?,?)]; SQL [insert into transfer_schedules (created_by,created_date,dropoff_node_id,end_time,last_modified_date,org_id,source_node_id,start_time,updated_by) values (?,?,?,?,?,?,?,?,?)]; constraint [uk9utfu5qorjrpipap2uga7kmt0][400.100]",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2
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
                  summary = "There was some error on the server while processing the request.",
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
public @interface CreateTransferScheduleDoc {}
