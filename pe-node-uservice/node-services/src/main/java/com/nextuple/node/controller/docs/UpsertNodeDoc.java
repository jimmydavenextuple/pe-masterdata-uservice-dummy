/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Create or update Node",
    description = "Creates a new Node or updates it if already exists")
@ApiResponse(
    responseCode = "200",
    description = "Node saved successfully",
    content =
        @Content(
            schema = @Schema(implementation = NodeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node created successfully",
                  value =
                      """
                    {
                        "success": true,
                        "requestId": "8ac86814-e73c-40c0-8ff2-0babe8c112b4",
                        "timestamp": 1744811457533,
                        "message": "Node saved successfully",
                        "payload": {
                            "nodeId": "node2261",
                            "orgId": "NEXTUPLE_GR",
                            "street": "Lombard Street",
                            "nodeType": "MFC",
                            "nodeLabourTier": "tier1",
                            "isActive": true,
                            "city": "San Fransisco",
                            "state": "AB",
                            "zipCode": "10012",
                            "country": "CA",
                            "latitude": "52.1977",
                            "startWorkingTime": "08:00",
                            "lastWorkingTime": "16:00",
                            "serviceOptionEligibilities": {
                                "expressEligible": true,
                                " primeEligible": false,
                                "standardEligible": true,
                                " regularEligible": false,
                                "sdndEligible": false,
                                "nextdayEligible": false
                            },
                            "shipToHome": true,
                            "bopisEligible": false,
                            "longitude": "-113.8721",
                            "timezone": "America/Edmonton"
                        }
                    }
                    """),
              @ExampleObject(
                  summary = "Node updated successfully",
                  name = "Updated lastWorkingTime",
                  value =
                      """
                    {
                        "success": true,
                        "requestId": "3e580d3e-8aed-4b8d-ba0c-f31335078a33",
                        "timestamp": 1744811527811,
                        "message": "Node saved successfully",
                        "payload": {
                            "nodeId": "node2261",
                            "orgId": "NEXTUPLE_GR",
                            "street": "Lombard Street",
                            "nodeType": "MFC",
                            "nodeLabourTier": "tier1",
                            "isActive": true,
                            "city": "San Fransisco",
                            "state": "AB",
                            "zipCode": "10012",
                            "country": "CA",
                            "latitude": "52.1977",
                            "startWorkingTime": "08:00",
                            "lastWorkingTime": "20:00",
                            "serviceOptionEligibilities": {
                                "expressEligible": true,
                                " primeEligible": false,
                                "standardEligible": true,
                                " regularEligible": false,
                                "sdndEligible": false,
                                "nextdayEligible": false
                            },
                            "shipToHome": true,
                            "bopisEligible": false,
                            "longitude": "-113.8721",
                            "timezone": "America/Edmonton"
                        }
                    }
                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "Bad Request due to validation or business rule failure",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Validation failure - Missing required fields",
                  value =
                      """
                    {
                        "success": false,
                        "requestId": "6f36b881-04b7-4ae4-aac3-62bff3b761a1",
                        "timestamp": 1744811484563,
                        "message": "Bad Request",
                        "payload": {
                            "type": "ERROR",
                            "code": 2,
                            "fields": {
                                "zipCode": { "errorMessage": "zipCode can't be blank" },
                                "country": { "errorMessage": "country can't be blank" },
                                "shipToHome": { "errorMessage": "shipToHome can't be null" },
                                "city": { "errorMessage": "city can't be blank" },
                                "timezone": { "errorMessage": "timezone can't be blank" },
                                "latitude": { "errorMessage": "latitude can't be blank" },
                                "isActive": { "errorMessage": "isActive can't be null" },
                                "nodeType": { "errorMessage": "nodeType can't be blank" },
                                "nodeLabourTier": { "errorMessage": "nodeLabourTier can't be blank" },
                                "serviceOptionEligibilities": { "errorMessage": "serviceOptionEligibilities can't be null" },
                                "bopisEligible": { "errorMessage": "bopisEligible can't be null" },
                                "state": { "errorMessage": "state can't be blank" },
                                "longitude": { "errorMessage": "longitude can't be blank" }
                            }
                        }
                    }
                    """),
              @ExampleObject(
                  summary = "Business validation failure - Missing paired times",
                  value =
                      """
                    {
                        "success": false,
                        "requestId": "66b78389-7295-45e3-b693-779d2c414b5c",
                        "timestamp": 1744811511679,
                        "message": "Either both startWorkingTime and lastWorkingTime should be present or neither should be present.",
                        "payload": {
                            "type": "ERROR",
                            "code": 6001,
                            "fields": {
                                "lastWorkingTime": {},
                                "startWorkingTime": { "rejectedValue": "08:00" }
                            }
                        }
                    }
                    """)
            }))
public @interface UpsertNodeDoc {}
