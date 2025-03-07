/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
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
    summary = "Update Tenant Preference for Selector",
    description = "Updates the tenant preference for the given selector details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the tenant preference for selector details are successfully fetched for a given organization ID and cost type",
    content =
        @Content(
            schema = @Schema(implementation = PreferenceSelectorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Tenant Preference for Selector details are fetched successfully",
                  name =
                      "A 200 OK response indicate that tenant preference for selector details are fetched successfully",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "3cbe44d9-79f2-4370-9c2c-7dcb2fb5f4e3",
                                                     "timestamp": 1701769216999,
                                                     "message": "Preference Selector updated successfully!",
                                                     "payload": {
                                                         "id": 119,
                                                         "orgId": "NEXTUPLE_GR",
                                                         "selectorCf": "carrierServiceId",
                                                         "costType": "SHP_COST",
                                                         "customAttributes": {
                                                           "dynamicAtrr1": true
                                                         }
                                                     }
                                                 }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that request contains invalid data.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "A 400 error code indicates that request contains invalid data.",
                  name = "When a given request contains empty selector.",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "42e94a66-5aae-460a-8c94-091d79fea744",
                                                      "timestamp": 1701769399461,
                                                      "message": "Bad Request",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 2,
                                                          "fields": {
                                                              "selectorCf": {
                                                                  "rejectedValue": "",
                                                                  "errorMessage": "Selector for the Cost Factor can’t be empty"
                                                              }
                                                          }
                                                      }
                                                  }
                                                  """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the tenant preference for selector record was not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant preference for selector was record not found and selector cost factor is invalid. </li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Tenant Preference for Selector record not found.",
                  name =
                      "A 404 response indicate that tenant preference for selector records are not found for given details",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "1b924fa1-cd0e-451c-9ec3-9b520c8c25a5#1648",
                                                     "timestamp": 1701767804205,
                                                     "message": "Preference Selector not found",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "selectorId": {
                                                                 "rejectedValue": 1
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
                      "When a cost factor not found for given orgId and selectorCf combination",
                  name =
                      "A 404 response indicates that the cost factor not found for given orgId and selectorCf combination",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "b1020041-e290-40bc-9752-9507f5e128c7",
                                                     "timestamp": 1701769331350,
                                                     "message": "selectorCf is not a valid cost factor",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "selectorCf": {
                                                                 "rejectedValue": "carrierServiceIdd"
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
    responseCode = "412",
    description =
        "A 412 error code indicates that the given preference selector is associated with one or more itinerary for selector ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a given preference selector is associated with one or more itinerary for selector id",
                  name =
                      "A 412 response indicates that preference selector is associated with one or more itinerary for selector id",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "712016e3-3141-4202-9f18-2f1f872e038b#1317",
                                                      "timestamp": 1701768054283,
                                                      "message": "Preference Selector is associated with one or more itinerary",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "selectorId": {
                                                                  "rejectedValue": 117
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
public @interface UpdatePreferenceSelector {}
