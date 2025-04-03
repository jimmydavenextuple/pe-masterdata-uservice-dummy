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
    summary = "Get Tenant Preference for Selector by Org ID and Selector ID",
    description =
        "Retrieves the tenant preference for a selector for the given organization ID and selector ID. Selector refers to the factor which can be associated with the cost, for eg. carrier service ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the tenant preference for selector details are successfully fetched for a given organization ID and selector ID.",
    content =
        @Content(
            schema = @Schema(implementation = PreferenceSelectorDto.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When Tenant Preference for Selector details are successfully fetched for a given org id and id.",
                  name =
                      "A 200 response indicate that Tenant Preference for Selector details are fetched successfully.",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "1385e62b-c1ed-45cf-a00d-d9433619ea89#1985",
                                                    "timestamp": 1701767553421,
                                                    "message": "Preference Selector fetched successfully!",
                                                    "payload": {
                                                        "id": 114,
                                                        "orgId": "NEXTUPLE_GR",
                                                        "selectorCf": "carrierServiceId",
                                                        "costType": "SHIPPING_COST",
                                                        "customAttributes": {
                                                          "dynamicAtrr1": true
                                                        }
                                                    }
                                                }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve tenant preference for selector details is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the tenant preference for selector record was not found."
            + "<li><b>Error code: 6001 </b></li><ul><li> Tenant Preference for Selector record not found.</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Tenant Preference for Selector record not found.",
                  name =
                      "A 404 response indicates that the Tenant Preference for Selector record not found.",
                  value =
                      """
                                                {
                                                     "success": false,
                                                     "requestId": "1385e62b-c1ed-45cf-a00d-d9433619ea89#1986",
                                                     "timestamp": 1701767570847,
                                                     "message": "Preference Selector not found",
                                                     "payload": {
                                                         "type": "ERROR",
                                                         "code": 6001,
                                                         "fields": {
                                                             "selectorId": {
                                                                 "rejectedValue": 1141
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
public @interface GetPreferenceSelectorByOrgIdAndSelectorId {}
