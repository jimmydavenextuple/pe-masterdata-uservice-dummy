/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Custom Region List",
    description =
        "Retrieves the details of all the custom regions associated with a specific organization ID and country and custom region ID(s) or custom region name(s). For example, for the organization ID “NXT”, the API will display all the custom regions that are associated with the organization NXT.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the custom region details for the given filters are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CustomRegionInfo.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates custom region details fetched successfully",
                  name =
                      "A 200 success code indicates that the custom region details for the given filters are retrieved successfully.",
                  value =
                      """
                                        {
                                             "success": true,
                                             "requestId": "b4911b3a-b07f-4b53-8b7e-e390569b0996",
                                             "timestamp": 1728911096824,
                                             "message": "Custom Regions fetched successfully",
                                             "payload": {
                                                "data": [
                                                   {
                                                      "orgId": "NEXTUPLE_GR",
                                                      "customRegionId": "CR2",
                                                      "zipCodes": [
                                                         "T3P",
                                                         "S1P"
                                                      ],
                                                      "customRegionName": "CRName2",
                                                      "customRegionDescription": "Custom region 1 desc",
                                                      "statesCount": 1,
                                                      "citiesCount": 1,
                                                      "zipCodePrefixesCount": 1,
                                                      "uploadDate": "2024-10-10"
                                                   }
                                                ],
                                                "pagination": {
                                                   "next": "",
                                                   "previous": "",
                                                   "totalPages": 1,
                                                   "currentPage": 1,
                                                   "totalRecords": 1,
                                                   "sortOrder": "ASC",
                                                   "sortBy": "customRegionId"
                                                },
                                                "aggregation": null
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
                  summary = "There was some error on server while processing the request.",
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
public @interface GetCustomRegionInfoDoc {}
