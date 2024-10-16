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
    summary = "Update Node Priority",
    description = "Updates the node's priority within the given node group.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the priority of the given node within the given node group is updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update a priority of the given node within given node group is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li>The node priority details are not found the for the given Organization ID, Node Group ID, and Node ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When node priority details not found for given orgId , nodeGroupId and nodeId.",
                  name =
                      "A 404 error status indicates that the node priority details not found for given orgId , nodeGroupId and nodeId.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3f6a103e-794c-46b8-b41c-f25e7b51df5b#89",
                                                    "timestamp": 1704186818943,
                                                    "message": "Node group details not found for given orgId , nodeGroupId and nodeId",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupId": {
                                                                "rejectedValue": 2404
                                                            },
                                                            "nodeId": {
                                                                "rejectedValue": "Node_JK_4"
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
public @interface UpdateNodePriorityDoc {}
