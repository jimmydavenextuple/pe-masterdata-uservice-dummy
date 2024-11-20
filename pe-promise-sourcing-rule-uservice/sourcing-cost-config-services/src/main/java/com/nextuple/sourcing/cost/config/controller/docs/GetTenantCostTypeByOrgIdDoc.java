/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Tenant Cost Type by Organization ID",
    description = "Retrieves tenant cost types by organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the tenant cost types are successfully fetched for given organization ID.",
    content =
        @Content(
            schema = @Schema(implementation = TenantCostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When tenant cost types are fetched successfully",
                  name =
                      "A 200 OK response indicate that tenant cost types are fetched successfully",
                  value =
                      """
                                        {
                                            "success": true,
                                            "requestId": "ac2b7960-74ef-4a2d-adb1-d7b91ec203b3",
                                            "timestamp": 1699247867994,
                                            "message": "Tenant cost types fetched successfully.",
                                            "payload": [
                                                {
                                                    "id": 28,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "costType": "SHIPPING_COST",
                                                    "displayName": "Shipping cost",
                                                    "label": "COST"
                                                },
                                                {
                                                    "id": 27,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "costType": "PROCESSING_COST",
                                                    "displayName": "Processing cost",
                                                    "label": "COST"
                                                },
                                                {
                                                    "id": 28,
                                                    "orgId": "NEXTUPLE_GR",
                                                    "costType": "SALES_REVENUE",
                                                    "displayName": "Sales revenue",
                                                    "label": "REVENUE"
                                                }
                                            ]
                                        }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve tenant cost types for given organization ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the tenant cost types are not found."
            + "<li><b>Error code: 6001 </b></li><ul><li>Tenant cost types are not found for given organization ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When tenant cost types are not found",
                  name =
                      "A 404 response indicate that tenant cost types are not found for given details",
                  value =
                      """
                                {
                                    "success": false,
                                    "requestId": "a70fd8ad-aa9f-43e9-98f4-b6c7725a28a9",
                                    "timestamp": 1699248998172,
                                    "message": "Tenant cost type not found with given details",
                                    "payload": {
                                        "type": "ERROR",
                                        "code": 6001,
                                        "fields": {
                                            "orgId": {
                                                "rejectedValue": "NEXTUPLE_GR1"
                                            }
                                        }
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
                                        "requestId": "3a822137-8ad5-4aa6-abe9-11836d06f56f",
                                        "timestamp": 1698044027078,
                                        "payload": {
                                            "type": "ERROR",
                                            "code": 2
                                        }
                                    }""")
            }))
public @interface GetTenantCostTypeByOrgIdDoc {}
