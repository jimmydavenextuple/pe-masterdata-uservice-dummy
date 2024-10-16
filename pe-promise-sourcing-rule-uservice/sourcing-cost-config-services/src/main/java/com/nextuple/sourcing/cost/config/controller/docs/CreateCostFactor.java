/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
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
    summary = "Create Cost Factor",
    description = "Creates a Cost Factor for a given organization ID.")
@ApiResponse(
    responseCode = "201",
    description = "When Cost Factor is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = CostFactorDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Factor is successfully created",
                  name = "A 201 OK response indicates that the Cost Factor is successfully created",
                  value =
                      """
                                                {
                                                       "success": true,
                                                       "requestId": "9392859b-bc06-4e7d-ab2e-5d0c1bc7f155#425",
                                                       "timestamp": 1701772444613,
                                                       "message": "Cost Factor created successfully!",
                                                       "payload": {
                                                           "id": 381,
                                                           "orgId": "NEXTUPLE_GR",
                                                           "costFactor": "BillWeightUps",
                                                           "dataType": "NUMBER",
                                                           "formula": "(l*b*h)/5000",
                                                           "library": "SPEL",
                                                           "costFactorType": "DERIVED",
                                                           "displayName": "Bill Weight Custom UPS",
                                                           "values": "S,M,L,XL",
                                                           "defaultValue": "S",
                                                           "levelApplied": "SHIPMENT",
                                                           "uom": "lbs",
                                                           "isBucketed": true,
                                                            "isRateCardLookUpRequired": true
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
                  summary =
                      "When a combination of orgId and cost factor's display name is not unique",
                  name =
                      "A 400 response indicates that the combination of orgId and cost factor's display name is not unique",
                  value =
                      """
                                                {
                                                       "success": false,
                                                       "requestId": "9392859b-bc06-4e7d-ab2e-5d0c1bc7f155#437",
                                                       "timestamp": 1701772510290,
                                                       "message": "Combination of orgId and cost factor's display name should be unique",
                                                       "payload": {
                                                           "type": "ERROR",
                                                           "code": 6001,
                                                           "fields": {
                                                               "orgId": {
                                                                   "rejectedValue": "NEXTUPLE_GR"
                                                               },
                                                               "displayName": {
                                                                   "rejectedValue": "Bill Weight Custom UPS"
                                                               }
                                                           }
                                                       }
                                                   }
                                                                    """),
              @ExampleObject(
                  summary = "When a combination of orgId and costFactor is not unique",
                  name =
                      "A 400 response indicate that the combination of orgId and costFactor is not unique",
                  value =
                      """
                                                {
                                                        "success": false,
                                                        "requestId": "1dd9552c-4b63-42d4-9017-dced8394b6a7#373",
                                                        "timestamp": 1701773249610,
                                                        "message": "Combination of orgId and costFactor should be unique",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 6001,
                                                            "fields": {
                                                                "costFactor": {
                                                                    "rejectedValue": "BillWeightUps"
                                                                },
                                                                "orgId": {
                                                                    "rejectedValue": "NEXTUPLE_GR"
                                                                }
                                                            }
                                                        }
                                                    }
                                                                    """),
              @ExampleObject(
                  summary = "When derived cost factor type have isBucketed as false",
                  name =
                      "A 400 response indicates that the derived cost factor type have isBucketed as false",
                  value =
                      """
                                                {
                                                         "success": false,
                                                         "requestId": "9392859b-bc06-4e7d-ab2e-5d0c1bc7f155#502",
                                                         "timestamp": 1701773389736,
                                                         "message": "Derived cost factor type cannot have isBucketed as false",
                                                         "payload": {
                                                             "type": "ERROR",
                                                             "code": 6001,
                                                             "fields": {
                                                                 "costFactorType": {
                                                                     "rejectedValue": "DERIVED"
                                                                 },
                                                                 "isBucketed": {
                                                                     "rejectedValue": false
                                                                 },
                                                                 "orgId": {
                                                                     "rejectedValue": "NEXTUPLE_GR"
                                                                 }
                                                             }
                                                         }
                                                     }
                                                                    """),
              @ExampleObject(
                  summary = "When the given request has derived cost factor type and null library",
                  name =
                      "A 400 response indicates that the given request has derived cost factor type and null library",
                  value =
                      """
                                                {
                                                          "success": false,
                                                          "requestId": "1dd9552c-4b63-42d4-9017-dced8394b6a7#494",
                                                          "timestamp": 1701774029625,
                                                          "message": "Derived cost factor type should have library in request",
                                                          "payload": {
                                                              "type": "ERROR",
                                                              "code": 6001,
                                                              "fields": {
                                                                  "costFactorType": {
                                                                      "rejectedValue": "DERIVED"
                                                                  },
                                                                  "library": {},
                                                                  "orgId": {
                                                                      "rejectedValue": "NEXTUPLE_GR"
                                                                  }
                                                              }
                                                          }
                                                      }
                                                                    """),
              @ExampleObject(
                  summary = "When the cost attribute not configured for given formula",
                  name =
                      "A 400 response indicates that the cost attribute not configured for given formula",
                  value =
                      """
                                                {
                                                           "success": false,
                                                           "requestId": "f50e5152-0016-426c-ba6d-32fb1382effc",
                                                           "timestamp": 1701778632091,
                                                           "message": "Cost attribute not configured for given formula",
                                                           "payload": {
                                                               "type": "ERROR",
                                                               "code": 6001,
                                                               "fields": {
                                                                   "orgId": {
                                                                       "rejectedValue": "NEXTUPLE_GR"
                                                                   },
                                                                   "formula": {
                                                                       "rejectedValue": "xyz"
                                                                   }
                                                               }
                                                           }
                                                       }
                                                                    """),
              @ExampleObject(
                  summary = "When rate card look up is disabled and bucketed is enabled.",
                  name =
                      "A 400 response indicates that the cost factor cannot be created if rateCardLookUp is disabled and bucketed is enabled.",
                  value =
                      """
                                    {
                                        "success": false,
                                        "requestId": "02da186f-4dc3-40bf-b77c-ca5c51f6255c",
                                        "timestamp": 1719398194979,
                                        "message": "Cost factor cannot be created if rateCardLookUp is disabled and bucketed is enabled.",
                                        "payload": {
                                            "type": "ERROR",
                                            "code": 6017,
                                            "fields": {
                                                "orgId": {
                                                    "rejectedValue": "NEXTUPLE_GR"
                                                },
                                                "isBucketed": {
                                                    "rejectedValue": true
                                                },
                                                "isRateCardLookUpRequired": {
                                                    "rejectedValue": false
                                                }
                                            }
                                        }
                                    }
                                                                          """),
              @ExampleObject(
                  summary = "when rate card look up is disabled and data type is not a number.",
                  name =
                      "A 400 response indicates that the cost factor cannot be created if rateCardLookUp is disabled and data type is not a number.",
                  value =
                      """
                                    {
                                        "success": false,
                                        "requestId": "28eddec6-c46c-4b9a-ab81-8b17c26286cf",
                                        "timestamp": 1719398707282,
                                        "message": "Cost factor cannot be created if rateCardLookUp is disabled and data type is not a number.",
                                        "payload": {
                                            "type": "ERROR",
                                            "code": 6018,
                                            "fields": {
                                                "orgId": {
                                                    "rejectedValue": "NEXTUPLE_GR"
                                                },
                                                "dataType": {
                                                    "rejectedValue": "STRING"
                                                },
                                                "isRateCardLookUpRequired": {
                                                    "rejectedValue": false
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
public @interface CreateCostFactor {}
