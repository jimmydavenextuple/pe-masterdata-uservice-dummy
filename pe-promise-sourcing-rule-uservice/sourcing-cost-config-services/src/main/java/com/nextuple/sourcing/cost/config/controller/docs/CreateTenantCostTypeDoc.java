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
    summary = "Create Tenant Cost Type",
    description = "Creates a tenant cost type with the given details.")
@ApiResponse(
    responseCode = "201",
    description = "A 201 success code indicates that a tenant cost type is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = TenantCostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is successfully created.",
                  name = "A 201 OK for creating tenant cost type.",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "712016e3-3141-4202-9f18-2f1f872e038b#467",
                                                       "timestamp": 1701760604474,
                                                       "message": "Tenant cost type created successfully.",
                                                       "payload": {
                                                           "id": 101,
                                                           "orgId": "NEXTUPLE_GR",
                                                           "costType": "SHIPPING_COST",
                                                           "displayName": "Shipping cost",
                                                           "label": "COST",
                                                           "additionalAttributes": {"key1": "value1"}
                                                       }
                                                   }""")
            }))
@ApiResponse(
    responseCode = "409",
    description =
        "A 409 error code indicates that tenant cost type already exist."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant cost type already exist for given organization ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When tenant cost type already exist for given orgId.",
                  name =
                      "A 409 conflict indicates that tenant cost type which already exist for given orgId.",
                  value =
                      """
                                           {
                                                "success": false,
                                                "requestId": "712016e3-3141-4202-9f18-2f1f872e038b#499",
                                                "timestamp": 1701760673488,
                                                "message": "Tenant cost type already exist for given orgId",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "orgId": {
                                                            "rejectedValue": "NEXTUPLE_GR"
                                                        }
                                                    }
                                                }
                                            }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the display name already exist."
            + "<li><b>Error code: 6001 </b></li><ul><li> Display name already exist for given orgId.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When display name already exist for given orgId.",
                  name =
                      "A 400 bad request indicates that display name already exist for given orgId.",
                  value =
                      """
                                                   {
                                                                 "success": false,
                                                                 "requestId": "1b924fa1-cd0e-451c-9ec3-9b520c8c25a5#945",
                                                                 "timestamp": 1701761538380,
                                                                 "message": "Tenant cost type display name already exists for given orgId",
                                                                 "payload": {
                                                                     "type": "ERROR",
                                                                     "code": 6001,
                                                                     "fields": {
                                                                         "orgId": {
                                                                             "rejectedValue": "NEXTUPLE_GR"
                                                                         },
                                                                         "displayName": {
                                                                             "rejectedValue": "Shipping cost"
                                                                         }
                                                                     }
                                                                 }
                                                             }
                                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong",
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
                                          }
                                        """)
            }))
public @interface CreateTenantCostTypeDoc {}
