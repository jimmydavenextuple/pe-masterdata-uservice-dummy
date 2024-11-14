/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
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
    summary = "Get Rate Card Table data",
    description = "Get rate card table data associated with the organisation and cost factors.")
@ApiResponse(
    responseCode = "200",
    description = "When cost configuration are successfully fetched",
    content =
        @Content(
            schema = @Schema(implementation = CostDefinitionResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Rate card grid fetched successfully.",
                  name =
                      "A 200 code indicates rate card grid fetched successfully when both row and column is passed in request.",
                  value =
                      "{\n"
                          + "  \"success\": true,\n"
                          + "  \"requestId\": \"2a566888-5103-4b3a-b828-23757ec93d27\",\n"
                          + "  \"timestamp\": 1698040068485,\n"
                          + "  \"message\": \"Cost config fetched successfully!\",\n"
                          + "  \"payload\": {\n"
                          + "    \"columns\": {\n"
                          + "      \"title\": \"Zone of shipment \",\n"
                          + "      \"headers\": [\n"
                          + "        {\n"
                          + "          \"columnName\": \"Bill Weight Custom UPS\",\n"
                          + "          \"columnMeta\": \"billWeightCustomUps\",\n"
                          + "          \"costFactor\": true\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"columnName\": \"1\",\n"
                          + "          \"columnMeta\": \"1\",\n"
                          + "          \"costFactor\": false\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"columnName\": \"2\",\n"
                          + "          \"columnMeta\": \"2\",\n"
                          + "          \"costFactor\": false\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"columnName\": \"3\",\n"
                          + "          \"columnMeta\": \"3\",\n"
                          + "          \"costFactor\": false\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rows\": {\n"
                          + "      \"data\": [\n"
                          + "        {\n"
                          + "          \"1\": \"\",\n"
                          + "          \"2\": \"\",\n"
                          + "          \"3\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"S\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"1\": \"\",\n"
                          + "          \"2\": \"\",\n"
                          + "          \"3\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"M\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"1\": \"\",\n"
                          + "          \"2\": \"\",\n"
                          + "          \"3\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"L\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"1\": \"\",\n"
                          + "          \"2\": \"\",\n"
                          + "          \"3\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"XL\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rateCardActive\": true\n"
                          + "  }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Rate card table fetched successfully.",
                  name =
                      "A 200 code indicates rate card table fetched successfully when only row is passed in request.",
                  value =
                      "{\n"
                          + "  \"success\": true,\n"
                          + "  \"requestId\": \"0041798f-849c-437a-ae41-dae43107c292\",\n"
                          + "  \"timestamp\": 1698040140135,\n"
                          + "  \"message\": \"Cost config fetched successfully!\",\n"
                          + "  \"payload\": {\n"
                          + "    \"columns\": {\n"
                          + "      \"title\": \"\",\n"
                          + "      \"headers\": [\n"
                          + "        {\n"
                          + "          \"columnName\": \"Bill Weight Custom UPS\",\n"
                          + "          \"columnMeta\": \"billWeightCustomUps\",\n"
                          + "          \"costFactor\": true\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"columnName\": \"SHIPPING_COST\",\n"
                          + "          \"columnMeta\": \"shippingCost\",\n"
                          + "          \"costFactor\": false\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rows\": {\n"
                          + "      \"data\": [\n"
                          + "        {\n"
                          + "          \"SHIPPING_COST\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"S\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"SHIPPING_COST\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"M\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"SHIPPING_COST\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"L\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        },\n"
                          + "        {\n"
                          + "          \"SHIPPING_COST\": \"\",\n"
                          + "          \"Bill Weight Custom UPS\": \"XL\",\n"
                          + "          \"isDynamicBucket\": \"false\"\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rateCardActive\": true\n"
                          + "  }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Static cost table fetched successfully.",
                  name =
                      "A 200 code indicates static rate card table fetched successfully when both row and column is not passed in request.",
                  value =
                      "{\n"
                          + "  \"success\": true,\n"
                          + "  \"requestId\": \"5ad27990-af0b-495d-abbf-f6707bb74c23\",\n"
                          + "  \"timestamp\": 1698040191417,\n"
                          + "  \"message\": \"Cost config fetched successfully!\",\n"
                          + "  \"payload\": {\n"
                          + "    \"columns\": {\n"
                          + "      \"title\": \"\",\n"
                          + "      \"headers\": [\n"
                          + "        {\n"
                          + "          \"columnName\": \"SHIPPING_COST\",\n"
                          + "          \"columnMeta\": \"shippingCost\",\n"
                          + "          \"costFactor\": true\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rows\": {\n"
                          + "      \"data\": [\n"
                          + "        {\n"
                          + "          \"SHIPPING_COST\": \"\"\n"
                          + "        }\n"
                          + "      ]\n"
                          + "    },\n"
                          + "    \"rateCardActive\": true\n"
                          + "  }\n"
                          + "}")
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is an issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a tenant cost type is not valid for a given orgId",
                  name =
                      "A 400 error code indicates that cost type is not valid for a given orgId.",
                  value =
                      "{\n"
                          + "  \"success\": false,\n"
                          + "  \"requestId\": \"fcf82c39-c246-4f69-ac1a-0bec71231874\",\n"
                          + "  \"timestamp\": 1698040261781,\n"
                          + "  \"message\": \"Invalid cost type for given orgId.\",\n"
                          + "  \"payload\": {\n"
                          + "    \"type\": \"ERROR\",\n"
                          + "    \"code\": 1048561\n"
                          + "  }\n"
                          + "}"),
            }))
@ApiResponse(
    responseCode = "403",
    description = "A 403 code indicates that the client is forbidden to send the request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Organization id mismatched",
                  name =
                      "A 403 error code indicates that wrong organization id is passed for given tenant.",
                  value =
                      "{\n"
                          + "  \"success\": false,\n"
                          + "  \"requestId\": \"c83fa1b0-6d04-4660-bb33-83afad825137\",\n"
                          + "  \"timestamp\": 1698044047055,\n"
                          + "  \"message\": \"OrgId mismatch!\",\n"
                          + "  \"payload\": {\n"
                          + "    \"type\": \"ERROR\",\n"
                          + "    \"code\": 1011\n"
                          + "  }\n"
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
                  summary = "There was some error on server while processing the request",
                  name =
                      "A 500 error code indicates that there was some error on the server while processing the request.",
                  value =
                      "{\n"
                          + "  \"success\": false,\n"
                          + "  \"requestId\": \"75d5537d-60a6-4a2c-999f-07e68d8a36d4\",\n"
                          + "  \"timestamp\": 1698040474473,\n"
                          + "  \"message\": \"Request method 'GET' is not supported\",\n"
                          + "  \"payload\": {\n"
                          + "    \"type\": \"ERROR\",\n"
                          + "    \"code\": 2\n"
                          + "  }\n"
                          + "}")
            }))
public @interface RateCardDoc {}
