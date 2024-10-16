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
    summary = "Delete Tenant Cost Type",
    description = "Deletes a tenant cost type for the give ID.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that a tenant cost type is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = TenantCostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is successfully deleted.",
                  name =
                      "A 200 success code indicates that tenant cost type is successfully deleted",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#1426",
                                                       "timestamp": 1701763665929,
                                                       "message": "Tenant Cost Type Details deleted successfully!",
                                                       "payload": {
                                                           "id": 101,
                                                           "orgId": "NEXTUPLE_GR",
                                                           "costType": "NP_COST1",
                                                           "displayName": "NP Cost1",
                                                           "label": "COST"
                                                       }
                                                   }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to delete a tenant cost type is not valid.")
@ApiResponse(
    responseCode = "412",
    description =
        "A 412 error code indicates that th tenant cost type is associated with active cost itineraries."
            + "<li><b>Error code: 6001 </b></li><ul><li>Tenant cost type is associated with active cost itineraries.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a Tenant cost type is associated with active cost itineraries.",
                  name =
                      "A 412 Precondition failed response indicates that tenant cost type is associated with active cost itineraries.",
                  value =
                      """
                                                {
                                                       "success": false,
                                                       "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#1418",
                                                       "timestamp": 1701763609471,
                                                       "message": "Tenant cost type associated with active cost itineraries. Remove those itineraries before performing this delete.",
                                                       "payload": {
                                                           "type": "ERROR",
                                                           "code": 6001,
                                                           "fields": {
                                                               "id": {
                                                                   "rejectedValue": 95
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
    responseCode = "404",
    description =
        "A 404 error code indicates that the tenant cost type is not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant Cost type is not found for given ID and organization ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is not found for given orgId and id.",
                  name =
                      "A 404 Not Found response indicates that tenant cost type not found for given details",
                  value =
                      """
                                                {
                                                       "success": false,
                                                       "requestId": "1385e62b-c1ed-45cf-a00d-d9433619ea89#1084",
                                                       "timestamp": 1701763516992,
                                                       "message": "Tenant cost type not found with given details",
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
public @interface DeleteTenantCostTypeDoc {}
