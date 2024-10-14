/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
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
    summary = "Edit Node Group",
    description = "Updates the node group for the given organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node group details are successfully updated.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update the node group detail is invalid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: The combination of the organization ID and the node group name is not unique.</li>"
            + "<li><b>Error code: 6001</b>: The node group name format is incorrect.</li>"
            + "<li><b>Error code: 6001</b>: The node ID is not valid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When combination of orgId and nodeGroupName is not unique",
                  name =
                      "A 400 error status indicates that the combination of orgId and nodeGroupName is not unique.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3b7d7693-29fb-488b-9a71-fd9cb69f98c8#183",
                                                    "timestamp": 1704422157383,
                                                    "message": "Combination of orgId and nodeGroupName should be unique",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupName": {
                                                                "rejectedValue": "nodeGroupName041549779"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When nodeGroupName format is incorrect",
                  name =
                      "A 400 error status indicates that the format of the node group is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#101",
                                                    "timestamp": 1704421927985,
                                                    "message": "Invalid format! Only alphanumeric characters, underscore and whitespace allowed.",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "name": {
                                                                "rejectedValue": "989-jfaklsj"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When node ID is invalid",
                  name =
                      "A 400 error status indicates that the nodes associating to node group is not valid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "c340cfa9-b13e-4b6f-a5ca-4400c356c30b#655",
                                                    "timestamp": 1704431147541,
                                                    "message": "Invalid node id",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeId": {
                                                                "rejectedValue": "100958"
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
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface EditNodeGroupDashboardDoc {}
