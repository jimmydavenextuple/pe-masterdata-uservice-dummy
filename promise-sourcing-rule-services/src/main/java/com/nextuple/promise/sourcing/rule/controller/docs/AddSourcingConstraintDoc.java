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
    summary = "Add Sourcing Constraint",
    description = "Adds a sourcing constraint for a given organization ID and group ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the sourcing constraint is successfully added.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to add a sourcing constraint is invalid."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:A constraint is already defined for the given organization ID and group ID.</li>"
            + "<li><b>Error code: 6001</b>:The constraint value is invalid.</li>"
            + "<li><b>Error code: 6001</b>:The Group Definition details are not found for the given group ID and organization ID.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a constraint is already defined for given orgId and groupId",
                  name =
                      "A 400 error status indicates that the constraint is already defined for given orgId and groupId.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "b19177a9-ae6c-4313-8582-8b491d094544#15",
                                                "timestamp": 1704450280043,
                                                "message": "This constraint is already defined for given orgId",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "orgId": {
                                                            "rejectedValue": "NEXTUPLE_GR"
                                                        }
                                                    }
                                                }
                                            }
                                            """),
              @ExampleObject(
                  summary = "When constraint value is invalid",
                  name = "A 400 error status indicates that the constraint value is invalid.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "b19177a9-ae6c-4313-8582-8b491d094544#12",
                                                "timestamp": 1704450254722,
                                                "message": "Bad Request",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 2,
                                                    "fields": {
                                                        "sourcingConstraint": {
                                                            "rejectedValue": "SHIP_COMPLETE_LIN",
                                                            "errorMessage": "values accepted for Enum class: [SHIP_COMPLETE_LINE, PRE_ORDER_PROMISE_DATE]"
                                                        }
                                                    }
                                                }
                                            }
                                            """),
              @ExampleObject(
                  summary =
                      "When Group Definition details are not found for the given groupId and orgId",
                  name =
                      "A 400 error status indicates that the Group Definition details are not found for the given groupId and orgId.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "3ba91bec-850c-4cbf-8eba-abd2ff269d8c#9",
                                                "timestamp": 1704449947897,
                                                "message": "Group Definition details not found for the given groupId and orgId",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "groupId": {
                                                            "rejectedValue": "1176"
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
public @interface AddSourcingConstraintDoc {}
