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
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Tenant Cost Type",
    description =
        "Retrieves the tenant cost type by the organization ID and the tenant cost type ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a tenant cost type is successfully fetched for given organization ID and ID.",
    content =
        @Content(
            schema = @Schema(implementation = TenantCostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a tenant cost type is successfully fetched for given orgId and id",
                  name = "A 200 OK response indicate that tenant cost type fetched successfully",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#1253",
                                                     "timestamp": 1701761862468,
                                                     "message": "Tenant cost type fetched successfully.",
                                                     "payload": {
                                                         "id": 95,
                                                         "orgId": "NEXTUPLE_GR",
                                                         "costType": "SHIPPING_COST",
                                                         "displayName": "Shipping Cost",
                                                         "label": "COST"
                                                     }
                                                 }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that request to retrieve a tenant cost type is for given organization ID and ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that tenant cost type is not found."
            + "<li><b>Error code: 6001 </b></li><ul><li>Tenant cost type is not found for given orgnaizationID and ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is not found for given orgId and id.",
                  name =
                      "A 404 response indicate that tenant cost type not found for given details",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "1b924fa1-cd0e-451c-9ec3-9b520c8c25a5#1041",
                                                     "timestamp": 1701761991615,
                                                     "message": "Tenant cost type not found with given details",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "id": {
                                                                 "rejectedValue": 99
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
public @interface GetTenantCostTypeDoc {}
