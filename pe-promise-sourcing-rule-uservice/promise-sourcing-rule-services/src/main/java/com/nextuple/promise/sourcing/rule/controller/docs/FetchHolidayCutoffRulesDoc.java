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
    summary = "Get Holiday Cutoff Rules",
    description = "Retrieves the holiday cutoff rules for the given order or line attributes.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that holiday cutoff rules are successfully fetched for the given order or line attributes",
    content =
        @Content(
            schema = @Schema(implementation = HolidayCutoffRulesResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Holiday cutoff rules fetched successfully",
                  name = "Holiday Cutoff Rules Fetched",
                  value =
                      """
                                                               [
                                                                  {
                                                                    "success": true,
                                                                    "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                                                    "timestamp": 1707025928063,
                                                                    "message": "Holiday cutoff rules fetched successfully!",
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
                                                               ]
                                                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to fetch sourcing rules is invalid"
            + "<ul>"
            + "<li><b>Error code: 7001</b>: When a sourcing rule attributes definition is not valid or is in INACTIVE status</li>"
            + "<li><b>Error code: 7003</b>: When all the required attributes values are not present</li>"
            + "<li><b>Error code: 7004</b>: When length of attributes is more than optional and required attributes combined</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a sourcing rule attributes definition is not valid or is in INACTIVE status",
                  name =
                      "A 400 status code indicates that the sourcing rule attributes definition is not valid or is in INACTIVE status.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "b4e38b04-84b7-44ac-a4a7-5e3cb730fded#21",
                                                    "timestamp": 1704453534601,
                                                    "message": "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope / Sourcing  attributes definition exists but not in ACTIVE status",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 7001,
                                                        "fields": {
                                                            "sourcingAttributesDefinitionId": {
                                                                "rejectedValue": 6860
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When all the required attributes values are not present",
                  name =
                      "A 400 status code indicates that all the required attributes values are not present.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "b4926dc8-a667-4bcb-be4c-8e76715fb18f#54",
                                                    "timestamp": 1704453973821,
                                                    "message": "Can't add or fetch the holiday cutoff rules as all the required attributes values are not present",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 7003,
                                                        "fields": {
                                                            "holidayCutoffRule": {
                                                                "rejectedValue": "EXPRESS:R1"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary =
                      "When length of attributes is more than optional and required attributes combined",
                  name =
                      "A 400 status code indicates that length of attributes is more than optional and required attributes combined.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "b4926dc8-a667-4bcb-be4c-8e76715fb18f#54",
                                                    "timestamp": 1704453973821,
                                                    "message": "Can't add or fetch the holiday cutoff rules as length of attributes is more than optional and required attributes combined",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 7004,
                                                        "fields": {
                                                            "holidayCutoffRule": {
                                                                "rejectedValue": "EXPRESS:R1:Store"
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
public @interface FetchHolidayCutoffRulesDoc {}
