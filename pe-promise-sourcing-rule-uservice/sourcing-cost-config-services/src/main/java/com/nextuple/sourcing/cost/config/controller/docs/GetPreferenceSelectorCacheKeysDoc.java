/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Preference Selector Cache Keys",
    description = "Retrieves the list of all the cache keys for the preference selector.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of all the cache keys for tenant cost type are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When the list of all the cache keys for tenant cost type are retrieved successfully.",
                  name =
                      "A 200 success code indicates that all the cache keys for preference selector are retrieved successfully.",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "fc1f1628-7a76-461b-a88a-a20041a79bf6#366",
                                                       "timestamp": 1701770334569,
                                                       "message": "Preference Selector Keys fetched successfully",
                                                       "payload": [
                                                           {
                                                               "orgId": "NEXTUPLE_GR",
                                                               "costType": "SHIPPING_COST"
                                                           },
                                                           {
                                                               "orgId": "SIGNET",
                                                               "costType": "SHIPPING_COST_1"
                                                           },
                                                           {
                                                               "orgId": "XYZINC",
                                                               "costType": "SHIPPING_COST"
                                                           },
                                                           {
                                                               "orgId": "SIGNET",
                                                               "costType": "SHIPPING_COST"
                                                           }
                                                       ]
                                                   }
                                                          """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve the list of all the cache keys for tenant cost type is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Required request parameter \"limit\" for the method parameter type \"integer\" is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "'limit' parameter not present.",
                  name = "A 404 error code indicates that the 'limit' parameter not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"452d3fc1-27a1-4d2b-aeeb-ba4908a03eee\",\n"
                          + "    \"timestamp\": 1679576331648,\n"
                          + "    \"message\": \"Required request parameter 'limit' for method parameter type Integer is not present\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
                          + "    }\n"
                          + "}")
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
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"timestamp\": \"1670589273234\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2\n"
                          + "    }\n"
                          + "}")
            }))
public @interface GetPreferenceSelectorCacheKeysDoc {}
