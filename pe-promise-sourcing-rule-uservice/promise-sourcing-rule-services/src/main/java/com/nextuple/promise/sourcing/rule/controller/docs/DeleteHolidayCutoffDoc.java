/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
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
    summary = "Delete Holiday Cutoff",
    description = "Deletes holiday cutoff with given details.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 status code indicates that the holiday cutoff is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = HolidayCutoffRulesResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Holiday cutoff deleted successfully",
                  name = "Holiday cutoff deleted",
                  value =
                      """
                                                                        {
                                                                            "success": true,
                                                                            "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                                                            "timestamp": 1707025928063,
                                                                            "message": "Holiday cutoff data deleted successfully!",
                                                                            "payload": {
                                                                                "orgId": "NEXTUPLE_GR",
                                                                                "holidayCutoffName": "CHRISTMAS",
                                                                                "holidayCutoffDescription": "Christmas Holiday Cutoff",
                                                                                "holidayCutoffRule": "EXPRESS:R1:Store",
                                                                                "sourcingAttributesDefinitionId": 123,
                                                                                "status": "ACTIVE",
                                                                                "startDate": "2024-12-01T12:00:00.000+00:00",
                                                                                "holidayCutoffDate": "2024-12-15T23:59:00.000+00:00",
                                                                                "holidayDeliveryDate": "2024-12-02T12:00:00.000+00:00",
                                                                                "preCutoffDays": 20.0,
                                                                                "deliveryCoolDownDays": 10.0,
                                                                                "preCutoffDaysType": "BUSINESS_DAYS",
                                                                                "deliveryCoolDownDaysType": "BUSINESS_DAYS"
                                                                            }
                                                                        }
                                                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 status code indicates that the primary key combination of orgId, holidayCutoffRule and holidayCutoffRule is not found",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Holiday cutoff data for a given input not found.",
                  name =
                      "A 404 error status indicates that the primary key combination of orgId, holidayCutoffName and holidayCutoffRule is not found.",
                  value =
                      """
                                                                                  {
                                                                                     "success": false,
                                                                                     "requestId": "71ec861c-534d-45a4-8322-83f466aa6ccd",
                                                                                     "timestamp": 1709798565732,
                                                                                     "message": "Holiday cutoff data for a given input not found",
                                                                                     "payload": {
                                                                                         "type": "ERROR",
                                                                                         "code": 7008,
                                                                                         "fields": {
                                                                                             "holidayCutoffName": {
                                                                                                 "rejectedValue": "Chirstmasall"
                                                                                             },
                                                                                             "orgId": {
                                                                                                 "rejectedValue": "NEXTUPLE_GR"
                                                                                             },
                                                                                             "holidayCutoffRule": {
                                                                                                 "rejectedValue": "EXPRESS"
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
public @interface DeleteHolidayCutoffDoc {}
