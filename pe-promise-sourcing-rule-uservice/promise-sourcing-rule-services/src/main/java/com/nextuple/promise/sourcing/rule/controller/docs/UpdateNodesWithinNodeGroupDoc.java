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
    summary = "Update Nodes Within Node Group",
    description = "Updates the nodes that are present within the node groups.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a nodes that are within the node group updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update nodes that are within the node group is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<li><b>Error code: 6001 </b></li><ul><li> Node Group with the given Node Group Name and Organization ID does not exist.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When node group with given nodeGroupName and orgId does not exist",
                  name =
                      "A 404 error status indicates that the node group with given nodeGroupName and orgId does not exist.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "ae33c560-5469-47f4-9164-27f02baa0ee0#246",
                                                    "timestamp": 1704187487555,
                                                    "message": "Node group with given nodeGroupName and orgId does not exist",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "nodeGroupName": {
                                                                "rejectedValue": "Node Nextuple_g"
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
public @interface UpdateNodesWithinNodeGroupDoc {}
