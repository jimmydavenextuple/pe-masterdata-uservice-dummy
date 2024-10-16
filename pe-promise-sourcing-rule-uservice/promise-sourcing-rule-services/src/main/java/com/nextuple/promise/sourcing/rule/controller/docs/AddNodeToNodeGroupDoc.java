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
    summary = "Add Node and Node Priority to Node Group",
    description = "Adds a node and node priority to a given node group.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node and its priority are added to a given node group.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to add the node and its priority to node group is invalid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:Node is invalid or is not in the active status.</li>"
            + "<li><b>Error code: 6001</b>: Node is already a part of the given node group.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When given node is not valid or not in active status.",
                  name = "A 400 error status indicates that a not valid or not in active status.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "a9437279-0a0b-4b57-a141-0fbae2a8a8cf#221",
                                                    "timestamp": 1704184962324,
                                                    "message": "Invalid node id",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeId": {
                                                                "rejectedValue": "ST1sf"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When node is already a part of the given node group",
                  name =
                      "A 400 error status indicates that node is already a part of the given node group.")
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input. "
            + "<li><b>Error code: 6001 </b></li><ul><li>Combination of the Node Group and Organization ID does not exist.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When combination of Node Group and OrgId does not exist.",
                  name =
                      "A 400 error status indicates that the combination of Node Group and OrgId does not exist.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "dd190a9e-f6d3-4518-94a3-5a7978fcdf06#951",
                                                    "timestamp": 1704180498122,
                                                    "message": "Node Group and OrgId does not exist",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupId": {
                                                                "rejectedValue": 2345
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
public @interface AddNodeToNodeGroupDoc {}
