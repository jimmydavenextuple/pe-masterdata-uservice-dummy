/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
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
    summary = "Create Node Carriers",
    description = "Creates new node carriers for the given request")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the node carrier is created successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier created successfully",
                  name = "Node Carrier Created",
                  value =
                      """
                            {
                                "success": true,
                                "requestId": "2f95ba39-2edf-40f4-bd82-9071e4489906",
                                "timestamp": 1707025928063,
                                "message": "Node Carrier successfully created",
                                "payload": {
                                    "nodeId": "N700",
                                    "orgId": "NEXTUPLE_GR",
                                    "carrierServiceId": "carrier4",
                                    "serviceOption": "EXPRESS",
                                    "lastPickupTime": "20:00"
                                }
                            }
                            """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is some issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Invalid LastPickupTime",
                  name = "Invalid LastPickupTime",
                  value =
                      """
                            {
                                "success": false,
                                "requestId": "40774161-bce3-4fad-8910-5389f66fce23",
                                "timestamp": 1707025966484,
                                "message": "LastPickupTime is invalid",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 6001
                                }
                            }
                            """),
              @ExampleObject(
                  summary = "Invalid NodeID and OrgID combination",
                  name = "Invalid NodeID and OrgID combination",
                  value =
                      """
                            {
                                "success": false,
                                "requestId": "dffa5e96-1356-4bb4-9cb9-0accfa78f846",
                                "timestamp": 1707026182734,
                                "message": "Invalid nodeId and orgId combination",
                                "payload": {
                                    "type": "ERROR",
                                    "code": 6002,
                                    "fields": {
                                        "serviceOption": {
                                            "rejectedValue": "EXPRESS"
                                        },
                                        "nodeId": {
                                            "rejectedValue": "N7030"
                                        },
                                        "orgId": {
                                            "rejectedValue": "NEXTUPLE_GR"
                                        }
                                    }
                                }
                            }
                            """),
              @ExampleObject(
                  summary = "Invalid CarrierServiceId",
                  name = "Invalid CarrierServiceId",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "e8c740b5-f1d4-44e6-aaec-7894b6c7b955",
                                              "timestamp": 1707901732478,
                                              "message": "Node carrier data cannot be created with given carrierServiceId and orgId",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 6002,
                                                  "fields": {
                                                      "serviceOption": {
                                                          "rejectedValue": "SDND"
                                                      },
                                                      "nodeId": {
                                                          "rejectedValue": "N700"
                                                      },
                                                      "orgId": {
                                                          "rejectedValue": "NEXTUPLE_GR"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - serviceOption cannot be null or empty",
                  name = "Bad Request",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e9f0f55-8bb1-443e-863a-e77e4fbc8c48#8533",
                                              "timestamp": 1708001273788,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "serviceOption": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "serviceOption cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - carrierServiceId cannot be null or empty",
                  name = "Bad Request",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e9f0f55-8bb1-443e-863a-e77e4fbc8c48#8533",
                                              "timestamp": 1708001273788,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "carrierServiceId": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "carrierServiceId cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - lastPickupTime cannot be null or empty",
                  name = "Bad Request",
                  value =
                      """
                                          {
                                              "success": false,
                                              "requestId": "5e9f0f55-8bb1-443e-863a-e77e4fbc8c48#8533",
                                              "timestamp": 1708001273788,
                                              "message": "Bad Request",
                                              "payload": {
                                                  "type": "ERROR",
                                                  "code": 2,
                                                  "fields": {
                                                      "lastPickupTime": {
                                                          "rejectedValue": "null",
                                                          "errorMessage": "lastPickupTime cannot be null or empty"
                                                      }
                                                  }
                                              }
                                          }
                                          """),
              @ExampleObject(
                  summary = "Bad Request - orgId cannot be null or empty",
                  name = "Bad Request",
                  value =
                      """
                                                        {
                                                            "success": false,
                                                            "requestId": "5e9f0f55-8bb1-443e-863a-e77e4fbc8c48#8533",
                                                            "timestamp": 1708001273788,
                                                            "message": "Bad Request",
                                                            "payload": {
                                                                "type": "ERROR",
                                                                "code": 2,
                                                                "fields": {
                                                                    "orgId": {
                                                                        "rejectedValue": "null",
                                                                        "errorMessage": "orgId cannot be null or empty"
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        """)
            }))
public @interface CreateNodeCarriersDoc {}
