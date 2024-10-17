/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
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
    summary = "Get Holiday cutoff Table data",
    description = "Get Holiday cutoff table data associated with the organisation.")
@ApiResponse(
    responseCode = "200",
    description = "When Holiday cutoff table data are successfully fetched",
    content =
        @Content(
            schema = @Schema(implementation = PageResponseForHolidayCutoff.class),
            examples = {
              @ExampleObject(
                  summary = "When Holiday cutoff table data fetched successfully",
                  name =
                      "A 200 OK for Holiday cutoff table data with no active sourcing definition",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "965b6bfe-549b-4bef-88e5-81e6abf9b539",
                                                    "timestamp": 1711082683185,
                                                    "message": "Holiday cutoff details List fetched successfully",
                                                    "payload": {
                                                        "data": null,
                                                        "pagination": null
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "When Holiday cutoff table data fetched successfully",
                  name =
                      "A 200 OK for Holiday cutoff table data with active sourcing definition and data in holiday cutoff table",
                  value =
                      """
                                            {
                                                 "success": true,
                                                 "requestId": "283ff11c-686b-41ba-8f9e-c2cc3bcd4e3c",
                                                 "timestamp": 1711083673045,
                                                 "message": "Holiday cutoff details List fetched successfully",
                                                 "payload": {
                                                     "data": {
                                                         "columns": [
                                                             {
                                                                 "columnName": "Rule Name",
                                                                 "columnMeta": "ruleName",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Rule Description",
                                                                 "columnMeta": "ruleDescription",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156291",
                                                                 "columnMeta": "reqAtt2210156291",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156292",
                                                                 "columnMeta": "reqAtt2210156292",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156293",
                                                                 "columnMeta": "reqAtt2210156293",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Opt Att2210156291",
                                                                 "columnMeta": "optAtt2210156291",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Start Date",
                                                                 "columnMeta": "startDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "End Date",
                                                                 "columnMeta": "endDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Overwrite Date",
                                                                 "columnMeta": "overwriteDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Pre-cutoff Days",
                                                                 "columnMeta": "preCutoffDays",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Holiday Cooldown Days",
                                                                 "columnMeta": "holidayCoolDownDays",
                                                                 "isSortable": true
                                                             }
                                                         ],
                                                         "rows": [
                                                             {
                                                                 "reqAtt2210156293": "Store",
                                                                 "endDate": "2024-04-01T12:00:00.000+00:00",
                                                                 "reqAtt2210156291": "EXPRESS",
                                                                 "preCutoffDays": 4.0,
                                                                 "reqAtt2210156292": "R1221015630",
                                                                 "optAtt2210156291": null,
                                                                 "ruleName": "holidayCutoffName221015630",
                                                                 "ruleDescription": "holidayCutoffDesc221015630",
                                                                 "startDate": "2024-03-27T12:00:00.000+00:00",
                                                                 "holidayCoolDownDays": 8.0,
                                                                 "overwriteDate": "2024-04-06T12:00:00.000+00:00"
                                                             },
                                                             {
                                                                 "reqAtt2210156293": "Store",
                                                                 "endDate": "2024-04-11T12:00:00.000+00:00",
                                                                 "reqAtt2210156291": "EXPRESS",
                                                                 "preCutoffDays": 4.0,
                                                                 "reqAtt2210156292": "R1221015630",
                                                                 "optAtt2210156291": null,
                                                                 "ruleName": "holidayCutoffName2210156301",
                                                                 "ruleDescription": "holidayCutoffDesc221015630",
                                                                 "startDate": "2024-03-27T12:00:00.000+00:00",
                                                                 "holidayCoolDownDays": 8.0,
                                                                 "overwriteDate": "2024-04-16T12:00:00.000+00:00"
                                                             }
                                                         ]
                                                     },
                                                     "pagination": {
                                                         "currentPage": 1,
                                                         "totalPages": 1,
                                                         "totalRecords": 2,
                                                         "sortOrder": "ASC",
                                                         "sortBy": "ruleName"
                                                     }
                                                 }
                                             }
                                                              """),
              @ExampleObject(
                  summary = "When Holiday cutoff table data fetched successfully",
                  name =
                      "A 200 OK for Holiday cutoff table data with active sourcing definition and no data in holiday cutoff table",
                  value =
                      """
                                            {
                                                 "success": true,
                                                 "requestId": "283ff11c-686b-41ba-8f9e-c2cc3bcd4e3c",
                                                 "timestamp": 1711083673045,
                                                 "message": "Holiday cutoff details List fetched successfully",
                                                 "payload": {
                                                     "data": {
                                                         "columns": [
                                                             {
                                                                 "columnName": "Rule Name",
                                                                 "columnMeta": "ruleName",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Rule Description",
                                                                 "columnMeta": "ruleDescription",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156291",
                                                                 "columnMeta": "reqAtt2210156291",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156292",
                                                                 "columnMeta": "reqAtt2210156292",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Req Att2210156293",
                                                                 "columnMeta": "reqAtt2210156293",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Opt Att2210156291",
                                                                 "columnMeta": "optAtt2210156291",
                                                                 "isSortable": false
                                                             },
                                                             {
                                                                 "columnName": "Start Date",
                                                                 "columnMeta": "startDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "End Date",
                                                                 "columnMeta": "endDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Overwrite Date",
                                                                 "columnMeta": "overwriteDate",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Pre-cutoff Days",
                                                                 "columnMeta": "preCutoffDays",
                                                                 "isSortable": true
                                                             },
                                                             {
                                                                 "columnName": "Holiday Cooldown Days",
                                                                 "columnMeta": "holidayCoolDownDays",
                                                                 "isSortable": true
                                                             }
                                                         ],
                                                         "rows": []
                                                     },
                                                     "pagination": null
                                                 }
                                             }
                                                              """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "There was some error on server while processing the request",
                  name =
                      "A 500 error code indicates that there was some error on the server while processing the request.",
                  value =
                      """
                                        {
                                          "success": false,
                                          "timestamp": "1670589273234",
                                          "payload": {
                                            "type": "ERROR",
                                            "code": 2
                                          }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "403",
    description = "A 403 code indicates that the client is forbidden to send the request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Organization id mismatched",
                  name =
                      "A 403 error code indicates that wrong organization id is passed for given tenant.",
                  value =
                      """
                                        {
                                          "success": false,
                                          "requestId": "4b608a08-e86a-448b-bea1-3bfbbda083ee#13098",
                                          "timestamp": 1710323530881,
                                          "message": "OrgId mismatch!",
                                          "payload": {
                                            "type": "ERROR",
                                            "code": 1011
                                          }
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that the query params are of invalid type",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "The specified blob already exists",
                  name = "A 400 status code indicates that the query params are of invalid type",
                  value =
                      """
                                                {
                                                   "success": false,
                                                   "requestId": "19181556-a588-4b2c-a839-289589427e4d#9220",
                                                   "timestamp": 1710413999549,
                                                   "message": "Bad Request",
                                                   "payload": {
                                                     "type": "ERROR",
                                                     "code": 2,
                                                     "fields": {
                                                       "pageNo": {
                                                         "rejectedValue": "abc",
                                                         "errorMessage": "Failed to convert property value of type 'java.lang.String' to required type 'java.util.Optional' for property 'pageNo'; Failed to convert from type [java.lang.String] to type [java.lang.Integer] for value [abc]"
                                                       }
                                                     }
                                                   }
                                                 }
                                                                  """)
            }))
public @interface HolidayCutoffDashboardDoc {}
