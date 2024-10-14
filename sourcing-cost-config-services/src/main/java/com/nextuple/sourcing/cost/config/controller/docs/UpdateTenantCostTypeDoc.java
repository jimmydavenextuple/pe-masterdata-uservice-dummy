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
    summary = "Update Tenant Cost Type",
    description = "Updates a tenant cost type for the organization.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates tha a tenant cost type is successfully updated for the given organization ID and ID.",
    content =
        @Content(
            schema = @Schema(implementation = TenantCostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "WheWhenn a tenant cost type is successfully updated for the given orgId and id.",
                  name =
                      "A 200 OK response indicates that tenant cost type is successfully updated",
                  value =
                      """
                                                {
                                                      "success": true,
                                                      "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#1342",
                                                      "timestamp": 1701762572229,
                                                      "message": "Tenant cost type updated successfully.",
                                                      "payload": {
                                                          "id": 100,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "costType": "NP_COST",
                                                          "displayName": "NP Cost 11",
                                                          "label": "COST"
                                                      }
                                                  }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request oto update tenant cost type is not valid."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant cost type is not found for given organization ID and ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is not found for given orgId and id.",
                  name =
                      "A 400 response indicates that tenant cost type not found for given details",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#1357",
                                                      "timestamp": 1701762658004,
                                                      "message": "Tenant cost type not found with given details",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "id": {
                                                                  "rejectedValue": 111
                                                              },
                                                              "orgId": {
                                                                  "rejectedValue": "NEXTUPLE_GR"
                                                              }
                                                          }
                                                      }
                                                  }
                                                  """),
              @ExampleObject(
                  summary =
                      "When a tenant cost type display name already exists for given orgId and id.",
                  name =
                      "A 400 response indicate that tenant cost type display name already exists for given details",
                  value =
                      """
                                                {
                                                       "success": false,
                                                       "requestId": "712016e3-3141-4202-9f18-2f1f872e038b#692",
                                                       "timestamp": 1701762936068,
                                                       "message": "Tenant cost type display name already exists for given orgId and id",
                                                       "payload": {
                                                           "type": "ERROR",
                                                           "code": 6001,
                                                           "fields": {
                                                               "orgId": {
                                                                   "rejectedValue": "NEXTUPLE_GR"
                                                               },
                                                               "displayName": {
                                                                   "rejectedValue": "NP Cost 11"
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
public @interface UpdateTenantCostTypeDoc {}
