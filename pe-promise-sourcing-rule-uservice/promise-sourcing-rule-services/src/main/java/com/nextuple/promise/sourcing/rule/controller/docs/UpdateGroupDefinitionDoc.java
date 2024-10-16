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
    summary = "Update Group Definition Details",
    description =
        "Updates the group definition for sourcing with required attributes for sourcing attributes definition ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the group definition is successfully updated.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<li><b>Error code: 6001 </b></li><ul><li> group definition is not found for the given organization ID, sourcing attributes definition ID and required attributes value.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a group definition is not found for given orgId , sourcingAttributesDefinitionId and reqAttributesValue.",
                  name =
                      "A 404 error status indicates that the group definition is not found for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
                  value =
                      """
                                                 {
                                                             "success": false,
                                                             "requestId": "df1c9f6d-603c-458f-a2d9-9791a6ef07ef#2560",
                                                             "timestamp": 1704177888202,
                                                             "message": "Group definition not found for given orgId , sourcingAttributesDefinitionId and reqAttributesValue",
                                                             "payload": {
                                                                 "type": "ERROR",
                                                                 "code": 6001,
                                                                 "fields": {
                                                                     "reqAttributesValue": {
                                                                         "rejectedValue": "RV1:RV2"
                                                                     },
                                                                     "sourcingAttributesDefinitionId": {
                                                                         "rejectedValue": 10
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
public @interface UpdateGroupDefinitionDoc {}
