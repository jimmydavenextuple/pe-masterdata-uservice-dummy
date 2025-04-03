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
    summary = "Create Tenant Preference for Selector",
    description = "Creates a tenant preference for a given selector and organization ID.")
@ApiResponse(
    responseCode = "201",
    description =
        "A 201 success code indicates that the tenant preference for selector is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = PreferenceSelectorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Tenant Preference for Selector is successfully created",
                  name =
                      "A 201 Created response indicate that Tenant Preference for Selector is successfully created",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "c7d82dbf-1f5b-47c1-8d83-8c346b9ea89a",
                                                     "timestamp": 1701767128015,
                                                     "message": "Preference Selector created successfully!",
                                                     "payload": {
                                                         "id": 117,
                                                         "orgId": "NEXTUPLE_GR",
                                                         "selectorCf": "carrierServiceId",
                                                         "costType": "NP_COST",
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
                  summary = "When a Preference selector already exist for orgId and costType.",
                  name =
                      "A 400 Bad Request indicates that Preference selector already exist for orgId and costType",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "f2dd7a03-0354-49b9-a06b-5a768cf79d1c",
                                                     "timestamp": 1701766843484,
                                                     "message": "Preference selector already exist for orgId and costType",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "orgId": {
                                                                 "rejectedValue": "NEXTUPLE_GR"
                                                             },
                                                             "costType": {
                                                                 "rejectedValue": "SHIPPING_COST"
                                                             }
                                                         }
                                                     }
                                                 }
                                                  """),
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
                                                  """),
              @ExampleObject(
                  summary = "A 400 error code indicates that request contains invalid data.",
                  name = "When a given request contains empty cost type.",
                  value =
                      """
                                                {
                                                       "success": false,
                                                       "requestId": "e1c1e532-3d2d-436b-b8bb-c7f21ac40244",
                                                       "timestamp": 1701769678197,
                                                       "message": "Bad Request",
                                                       "payload": {
                                                           "type": "ERROR",
                                                           "code": 2,
                                                           "fields": {
                                                               "costType": {
                                                                   "rejectedValue": "",
                                                                   "errorMessage": "Cost type can't be empty"
                                                               }
                                                           }
                                                       }
                                                   }
                                                  """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the cost factor is not found"
            + "<li><b>Error code: 6001 </b></li><ul><li> Cost factor not found for given combination of organization ID and selector cost factor.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When a cost factor not found for given orgId and selectorCf combination",
                  name =
                      "A 404 response indicates that the cost factor not found for given orgId and selectorCf combination",
                  value =
                      """
                                                  {
                                                      "success": false,
                                                      "requestId": "41ef92aa-0804-4092-a191-dd1d811ffa27",
                                                      "timestamp": 1701766961361,
                                                      "message": "selectorCf is not a valid cost factor",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "orgId": {
                                                                  "rejectedValue": "NEXTUPLE_GR"
                                                              },
                                                              "selectorCf": {
                                                                  "rejectedValue": "carrierServiceId1"
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
public @interface CreatePreferenceSelector {}
