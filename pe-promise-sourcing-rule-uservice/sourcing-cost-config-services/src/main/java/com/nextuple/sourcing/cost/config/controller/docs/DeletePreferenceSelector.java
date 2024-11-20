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
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Delete Preference Selector",
    description = "Deletes a tenant preference for selector.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a preference selector is successfully deleted.",
    content =
        @Content(
            schema = @Schema(implementation = PreferenceSelectorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When a preference selector is successfully deleted.",
                  name =
                      "A 200 OK response indicates that the preference selector is successfully deleted",
                  value =
                      """
                                                {
                                                      "success": true,
                                                      "requestId": "b4989734-d622-4319-bd2e-a542adaffae2#152",
                                                      "timestamp": 1701770087935,
                                                      "message": "Preference Selector deleted successfully!",
                                                      "payload": {
                                                          "id": 121,
                                                          "orgId": "NEXTUPLE_GR",
                                                          "selectorCf": "zone",
                                                          "costType": "SHP_COST"
                                                      }
                                                  }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to delete a preference selector is not valid.")
@ApiResponse(
    responseCode = "412",
    description =
        "A 412 error code indicates that a preference selector is associated with one or more itinerary.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a Preference Selector is associated with one or more itinerary",
                  name =
                      "A 12 indicates that the Preference Selector is associated with one or more itinerary",
                  value =
                      """
                                                {
                                                      "success": false,
                                                      "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#2318",
                                                      "timestamp": 1701770179036,
                                                      "message": "Preference Selector is associated with one or more itinerary",
                                                      "payload": {
                                                          "type": "ERROR",
                                                          "code": 6001,
                                                          "fields": {
                                                              "selectorId": {
                                                                  "rejectedValue": 114
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
        "A 404 error code indicates that the preference selector is not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Preference selector is not found for given ID.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a Preference Selector is not found for given id",
                  name =
                      "A 404 response indicates that the Preference Selector is not found for given id",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "298a4abf-b0d3-410f-bda6-97bebff3c5cb#2295",
                                                     "timestamp": 1701769860763,
                                                     "message": "Preference Selector not found",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "selectorId": {
                                                                 "rejectedValue": 121
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
public @interface DeletePreferenceSelector {}
