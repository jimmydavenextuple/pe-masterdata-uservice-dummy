/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
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
import java.util.List;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Retrieves the list of all the cache keys for Cost Values",
    description = "Retrieves the list of all the cache keys for Cost Values")
@ApiResponse(
    responseCode = "200",
    description = "When all the cache keys for cost value are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary = "When all the cache keys for cost value are retrieved successfully.",
                  name =
                      "A 200 success code indicates that all the cache keys for cost value are retrieved successfully.",
                  value =
                      "{\n"
                          + "    \"success\": true,\n"
                          + "    \"requestId\": \"71eb027e-359d-4a8c-aa90-5fa5a7d52219\",\n"
                          + "    \"timestamp\": 1702038140589,\n"
                          + "    \"message\": \"Cost Value Keys fetched successfully\",\n"
                          + "    \"payload\": [\n"
                          + "        {\n"
                          + "            \"orgId\": \"SIGNET\",\n"
                          + "            \"costItinerary\": \"NODE_PROCESSING_ITINERARY\",\n"
                          + "            \"costFactorCombinationKey\": \"MFC|hazmat|tier2|L\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "            \"orgId\": \"SIGNET\",\n"
                          + "            \"costItinerary\": \"NODE_PROCESSING_ITINERARY\",\n"
                          + "            \"costFactorCombinationKey\": \"DC|hazmat|tier2|XL\"\n"
                          + "        }\n"
                          + "    ]\n"
                          + "}")
            }))
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
public @interface GetCostValueCacheKeyDoc {}
