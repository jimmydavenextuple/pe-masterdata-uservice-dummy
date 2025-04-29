/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.controller.docs;

import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
    summary = "Create or update a Carrier Service",
    description =
        "Creates a new Carrier Service or updates an existing one based on the request data.")
@ApiResponses(
    value = {
      @ApiResponse(
          responseCode = "200",
          description = "Carrier Service saved successfully",
          content =
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CarrierServiceResponse.class),
                  examples = {
                    @ExampleObject(
                        name = "Carrier Service Created",
                        value =
                            """
                        {
                          "success": true,
                          "requestId": "8c1d98dc-6557-4b0d-8e83-b2c5dbcd1521",
                          "timestamp": 1744871943119,
                          "message": "Carrier Service saved successfully",
                          "payload": {
                            "customAttributes": {
                              "key": "value"
                            },
                            "orgId": "NEXTUPLE_GR",
                            "carrierId": "UPS",
                            "carrierServiceId": "UPS-EXPRESS",
                            "carrierName": "ALL",
                            "serviceName": "UPS service 1",
                            "serviceOptions": "STANDARD"
                          }
                        }
                    """),
                    @ExampleObject(
                        name = "Carrier Service Updated",
                        value =
                            """
                        {
                          "success": true,
                          "requestId": "edccfabe-3b5f-4a0a-961f-3415d36871e4",
                          "timestamp": 1744872014970,
                          "message": "Carrier Service saved successfully",
                          "payload": {
                            "customAttributes": {
                              "key": "value"
                            },
                            "orgId": "NEXTUPLE_GR",
                            "carrierId": "UPS",
                            "carrierServiceId": "UPS-EXPRESS",
                            "carrierName": "ALL",
                            "serviceName": "UPS service 2",
                            "serviceOptions": "STANDARD"
                          }
                        }
                    """)
                  })),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request - bad input",
          content =
              @Content(
                  mediaType = "application/json",
                  examples =
                      @ExampleObject(
                          name = "Invalid Service Option",
                          value =
                              """
                    {
                      "success": false,
                      "requestId": "ebd5a228-65fa-4a11-840d-a2b86a86ae7a",
                      "timestamp": 1744872413011,
                      "message": "Invalid service option",
                      "payload": {
                        "type": "ERROR",
                        "code": 6001,
                        "fields": {
                          "serviceOptions": {
                            "rejectedValue": "INVALID"
                          }
                        }
                      }
                    }
                """)))
    })
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpsertCarrierServiceDoc {}
