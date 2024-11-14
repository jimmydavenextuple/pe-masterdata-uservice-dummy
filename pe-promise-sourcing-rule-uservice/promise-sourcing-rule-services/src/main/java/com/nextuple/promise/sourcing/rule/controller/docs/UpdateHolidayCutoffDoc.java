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
    summary = "Update Holiday Cutoff",
    description = "Updates holiday cutoff with given details.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the holiday cutoff is successfully updated.",
    content =
        @Content(
            schema = @Schema(implementation = HolidayCutoffRulesResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Holiday cutoff updated successfully",
                  name = "Holiday cutoff Updated",
                  value =
                      """
                                                                        {
                                                                            "success": true,
                                                                            "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                                                            "timestamp": 1707025928063,
                                                                            "message": "Holiday cutoff override data updated successfully!",
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
        "A 404 error code indicates that the primary key combination of orgId, holidayCutoffRule and holidayCutoffRule is not found",
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
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update a holiday cutoff is incorrect.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When the Number of days configured for pre-cutoff days is more than 30 days.",
                  name =
                      "A 400 error status indicates that the Number of days configured for pre-cutoff days is more than 30 days.",
                  value =
                      """
                                                                                    {
                                                                                        "success": false,
                                                                                        "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#101",
                                                                                        "timestamp": 1704421927985,
                                                                                        "message": "Bad Request",
                                                                                        "payload": {
                                                                                            "type": "ERROR",
                                                                                            "code": 2,
                                                                                            "fields": {
                                                                                                "preCutoffDays": {
                                                                                                    "rejectedValue": "35.0",
                                                                                                    "errorMessage": "preCutoffDays must be less than 30"
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    """),
              @ExampleObject(
                  summary =
                      "When the Number of days configured for holiday cool down days is more than 30 days.",
                  name =
                      "A 400 error status indicates that the Number of days configured for holiday cool down days is more than 30 days.",
                  value =
                      """
                                                                                    {
                                                                                        "success": false,
                                                                                        "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#101",
                                                                                        "timestamp": 1704421927985,
                                                                                        "message": "Bad Request",
                                                                                        "payload": {
                                                                                            "type": "ERROR",
                                                                                            "code": 2,
                                                                                            "fields": {
                                                                                                "deliveryCoolDownDays": {
                                                                                                    "rejectedValue": 40,
                                                                                                    "errorMessage": "deliveryCoolDownDays must be less than 30"
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    """),
              @ExampleObject(
                  summary = "When the pre-cutoff days configured days are not positive",
                  name = "A 400 error status indicates that the configured days are not positive.",
                  value =
                      """
                                                                                    {
                                                                                        "success": false,
                                                                                        "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#101",
                                                                                        "timestamp": 1704421927985,
                                                                                        "message": "Bad Request",
                                                                                        "payload": {
                                                                                            "type": "ERROR",
                                                                                            "code": 2,
                                                                                            "fields": {
                                                                                                "name": {
                                                                                                    "rejectedValue": "-100",
                                                                                                    "errorMessage": "preCutoffDays must be positive"
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    """),
              @ExampleObject(
                  summary = "When the holiday cool down days are not positive.",
                  name =
                      "A 400 error status indicates that the holiday cool down days are not positive.",
                  value =
                      """
                                                                                    {
                                                                                        "success": false,
                                                                                        "requestId": "014495b7-88dc-4ebd-be1b-9a5b7ef922ab#101",
                                                                                        "timestamp": 1704421927985,
                                                                                        "message": "Bad Request",
                                                                                        "payload": {
                                                                                            "type": "ERROR",
                                                                                            "code": 2,
                                                                                            "fields": {
                                                                                                "name": {
                                                                                                    "rejectedValue": "-100",
                                                                                                    "errorMessage": "deliveryCoolDownDays  must be positive"
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    """),
              @ExampleObject(
                  summary = "Pre-cutoff days configured is more than holiday cutoff window days",
                  name =
                      "A 400 error status indicates that the Pre-cutoff days configured is more than holiday cutoff window days",
                  value =
                      """
                                                                                    {
                                                                                        "success": false,
                                                                                        "requestId": "e593fbf1-2d3b-4a90-9122-87a1d5aa8f59",
                                                                                        "timestamp": 1709892210650,
                                                                                        "message": "Pre-cutoff days configured should be less than holiday cutoff window days",
                                                                                        "payload": {
                                                                                            "type": "ERROR",
                                                                                            "code": 7006,
                                                                                            "fields": {
                                                                                                "holidayCutoffWindowsInDays": {
                                                                                                    "rejectedValue": 7
                                                                                                },
                                                                                                "preCutoffDays": {
                                                                                                    "rejectedValue": 9.0
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    """),
              @ExampleObject(
                  summary = "Overlap in start date and holiday cutoff date.",
                  name =
                      "A 400 error status indicates that there is an Overlap in start date and holiday cutoff date.",
                  value =
                      """
                                                                                    {
                                                                                         "success": false,
                                                                                         "requestId": "b7127a98-6371-4317-8d4c-3d9dd934435e",
                                                                                         "timestamp": 1709892416971,
                                                                                         "message": "Overlap in start date and holiday cutoff date.",
                                                                                         "payload": {
                                                                                             "type": "ERROR",
                                                                                             "code": 7007,
                                                                                             "fields": {
                                                                                                 "holidayCutoffStartDate": {
                                                                                                     "rejectedValue": "2024-12-08T12:00:00.000+00:00"
                                                                                                 },
                                                                                                 "holidayCutoffDate": {
                                                                                                     "rejectedValue": "2024-03-15T23:59:00.000+00:00"
                                                                                                 }
                                                                                             }
                                                                                         }
                                                                                     }
                                                                                    """),
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface UpdateHolidayCutoffDoc {}
