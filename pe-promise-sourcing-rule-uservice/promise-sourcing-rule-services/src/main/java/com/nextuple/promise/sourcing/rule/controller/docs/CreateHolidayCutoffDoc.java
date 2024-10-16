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
    summary = "Create Holiday Cutoff",
    description = "Creates holiday cutoff with given details.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the holiday cutoff is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = HolidayCutoffRulesResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Holiday cutoff created successfully",
                  name = "Holiday cutoff Created",
                  value =
                      """
                                                      {
                                                          "success": true,
                                                          "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                                          "timestamp": 1707025928063,
                                                          "message": "Holiday cutoff override data created successfully!",
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
    responseCode = "412",
    description = "A 412 error code indicates that the primary key combination is not unique",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When orgId, rule name and rule value combination is not unique.",
                  name =
                      "A 412 error status indicates that the primary key combination of orgId, rule name and rule value is not unique.",
                  value =
                      """
                                                                {
                                                                      "success": false,
                                                                      "requestId": "3b7d7693-29fb-488b-9a71-fd9cb69f98c8#183",
                                                                      "timestamp": 1704422157383,
                                                                      "message": "Holiday cutoff with given orgId, holidayCutoffName and holidayCutoffRule is already configured",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7002,
                                                                          "fields": {
                                                                              "orgId": {
                                                                                  "rejectedValue": "NEXTUPLE_GR"
                                                                              },
                                                                              "holidayCutoffName": {
                                                                                  "rejectedValue": "CHRISTMAS"
                                                                              },
                                                                              "holidayCutoffRule": {
                                                                                   "rejectedValue": "EXPRESS:R1:Store"
                                                                          }
                                                                      }
                                                                }
                                                """),
              @ExampleObject(
                  summary = "When holiday cutoff rule with same cutoff date is already configured.",
                  name =
                      "A 412 error status indicates that the holiday cutoff rule with same cutoff date is already configured.",
                  value =
                      """
                                          {
                                               "success": false,
                                               "requestId": "44b30c2e-a72d-4551-af51-21eca380f23c",
                                               "timestamp": 1709815329819,
                                               "message": "Holiday cutoff rule with same cutoff date is already configured.",
                                               "payload": {
                                                   "type": "ERROR",
                                                   "code": 7005,
                                                   "fields": {
                                                       "holidayCutoffDate": {
                                                           "rejectedValue": "2024-12-18T23:59:00.000+00:00"
                                                       },
                                                       "holidayCutoffRule": {
                                                           "rejectedValue": "EXPRESS:R1:Store"
                                                       }
                                                   }
                                               }
                                           }
                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to create a holiday cutoff is incorrect.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When sourcing attributes definition for HOLIDAY_CUTOFF scope is invalid",
                  name =
                      "A 400 error status indicates that the sourcing attributes definition for HOLIDAY_CUTOFF scope is invalid",
                  value =
                      """
                                                                  {
                                                                      "success": false,
                                                                      "requestId": "3b7d7693-29fb-488b-9a71-fd9cb69f98c8#183",
                                                                      "timestamp": 1704422157383,
                                                                      "message": "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope/ Sourcing attributes definition exists but not in ACTIVE status",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7001,
                                                                          "fields": {
                                                                              "sourcingAttributesDefinitionId": {
                                                                                  "rejectedValue": 123
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """),
              @ExampleObject(
                  summary = "When Sourcing attributes definition exists but not in ACTIVE status",
                  name =
                      "A 400 error status indicates that the Sourcing attributes definition exists but not in ACTIVE status",
                  value =
                      """
                                                                  {
                                                                      "success": false,
                                                                      "requestId": "3b7d7693-29fb-488b-9a71-fd9cb69f98c8#183",
                                                                      "timestamp": 1704422157383,
                                                                      "message": "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope / Sourcing attributes definition exists but not in ACTIVE status",
                                                                      "payload": {
                                                                          "type": "ERROR",
                                                                          "code": 7001,
                                                                          "fields": {
                                                                              "sourcingAttributesDefinitionId": {
                                                                                  "rejectedValue": 123
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """),
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
                  summary = "When holidayCutoffRule is null or empty.",
                  name =
                      "A 400 error status indicates that the holidayCutoffRule is null or empty.",
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
                                                                              "orgId": {
                                                                                  "rejectedValue": "",
                                                                                  "errorMessage": "holidayCutoffRule can't be empty"
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """),
              @ExampleObject(
                  summary = "When holidayCutoffName is null or empty.",
                  name =
                      "A 400 error status indicates that the holidayCutoffName is null or empty.",
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
                                                                              "holidayCutoffName": {
                                                                                  "rejectedValue": "",
                                                                                  "errorMessage": "holidayCutoffName can't be empty"
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                                                  """),
              @ExampleObject(
                  summary = "When pre-cutoff days are more than holiday cutoff window.",
                  name =
                      "A 400 error status indicates that the pre-cutoff days configured are more than holiday cutoff window days.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "411893cb-a2cc-4546-851e-5d67c6aa0854",
                                                "timestamp": 1709811955368,
                                                "message": "Pre-cutoff days configured should be less than holiday cutoff window days",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 7006,
                                                    "fields": {
                                                        "hoidayCutoffWindowsInDays": {
                                                            "rejectedValue": 13
                                                        },
                                                        "preCutoffDays": {
                                                            "rejectedValue": 15.0
                                                        }
                                                    }
                                                }
                                            }
                                    """),
              @ExampleObject(
                  summary = "When holiday cutoff start date is after the holiday cutoff date.",
                  name =
                      "A 400 error status indicates that the holiday cutoff start date is after the holiday cutoff date.",
                  value =
                      """
                                            {
                                                 "success": false,
                                                 "requestId": "041c4131-5a5b-4cde-b9c4-fac273c833b1",
                                                 "timestamp": 1709815433559,
                                                 "message": "Overlap in start date and holiday cutoff date.",
                                                 "payload": {
                                                     "type": "ERROR",
                                                     "code": 7007,
                                                     "fields": {
                                                         "holidayCutoffStartDate": {
                                                             "rejectedValue": "2024-12-05T12:00:00.000+00:00"
                                                         },
                                                         "holidayCutoffDate": {
                                                             "rejectedValue": "2024-12-04T23:59:00.000+00:00"
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
public @interface CreateHolidayCutoffDoc {}
