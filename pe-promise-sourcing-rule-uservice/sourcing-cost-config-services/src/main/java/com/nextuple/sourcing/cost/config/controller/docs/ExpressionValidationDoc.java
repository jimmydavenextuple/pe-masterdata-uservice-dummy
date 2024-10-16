/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Validate formula expression", description = "Validate mathematical formula")
@ApiResponse(
    responseCode = "200",
    description = "When a cost value is successfully validated",
    content =
        @Content(
            schema = @Schema(implementation = CostTypeResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When parsii library used for validation.",
                  name =
                      "A 200 OK response indicate that validation is successful via parsii library.",
                  value =
                      """
                                {
                                    "success": true,
                                    "requestId": "ddcc7c57-7ca5-4589-9346-8617b9ad5af3",
                                    "timestamp": 1698635116681,
                                    "message": "Formula expression validated successfully.",
                                    "payload": {
                                        "expressionValue": 125.0,
                                        "sampleSolution": {
                                            "serviceOption": "EXPRESS",
                                            "orderNo": "SO-100201",
                                            "orderDate": "2023-03-15",
                                            "destinationAddress": {
                                                "zipCode": "RRR 1B5",
                                                "region": "RRR",
                                                "state": "Ontario",
                                                "country": "CA",
                                                "timezone": "UTC"
                                            },
                                            "customAttributes": {},
                                            "totalNoOfShipments": 1,
                                            "totalLines": 1,
                                            "totalCost": 10.0,
                                            "shipments": [
                                                {
                                                    "items": [
                                                        {
                                                            "itemId": "OPT-ITEM-4",
                                                            "uom": "",
                                                            "length": "10",
                                                            "width": "15",
                                                            "height": "20",
                                                            "weight": "50",
                                                            "weightUom": "g",
                                                            "dimensionUom": "m",
                                                            "productCategory": "ELECTRONICS",
                                                            "handlingType": "MANUAL",
                                                            "requestedQuantity": "8",
                                                            "fulfilledQuantity": "5",
                                                            "customAttributes": {}
                                                        }
                                                    ],
                                                    "nodeDetails": {
                                                        "nodeId": "nodeId1",
                                                        "nodeType": "nodeType2",
                                                        "nodeZipCode": "nodeZipCode2",
                                                        "nodeLabourTier": ""
                                                    },
                                                    "carrierServiceId": "carrierServiceId1",
                                                    "zone": "zone1",
                                                    "cost": "10"
                                                }
                                            ]
                                        },
                                        "sampleRequest": {
                                            "orgId": "NEXTUPLE_GR",
                                            "serviceOption": "EXPRESS",
                                            "fulfillmentType": null,
                                            "orderDate": "2023-03-15",
                                            "orderNo": "SO-100201",
                                            "cartId": null,
                                            "pageName": null,
                                            "orderType": "MARKETPLACE",
                                            "shipDestinationDetails": {
                                                "zipCode": "RRR 1B5",
                                                "region": "RRR",
                                                "state": "Ontario",
                                                "country": "CA",
                                                "timezone": "UTC"
                                            },
                                            "estimatedDeliveryDate": null,
                                            "carrierServiceId": "FEDEX_GROUND",
                                            "reserveInventoryFlag": null,
                                            "reserveCapacity": null,
                                            "shipNode": null,
                                            "customAttributes": {},
                                            "orderLines": [
                                                {
                                                    "orderedQuantity": 6.0,
                                                    "shipNode": null,
                                                    "lineId": null,
                                                    "reservationId": null,
                                                    "carrierServiceId": "FEDEX_GROUND",
                                                    "shipDestinationDetails": {
                                                        "zipCode": "RRR 1B5",
                                                        "region": "RRR",
                                                        "state": "Ontario",
                                                        "country": "Canada",
                                                        "timezone": "UTC"
                                                    },
                                                    "serviceOption": null,
                                                    "item": {
                                                        "itemId": "OPT-ITEM-4",
                                                        "productClass": "ELECTRONICS",
                                                        "unitOfMeasure": "EACH"
                                                    },
                                                    "customAttributes": {}
                                                }
                                            ],
                                            "sessionId": null
                                        }
                                    }
                                }
                                """),
              @ExampleObject(
                  summary = "When spel library used for validation.",
                  name =
                      "A 200 OK response indicate that validation is successful via spel library.",
                  value =
                      """

                                                {
                                                    "success": true,
                                                    "requestId": "d494b4dc-0f50-4763-8640-32b8ae7f05cd",
                                                    "timestamp": 1698635101446,
                                                    "message": "Formula expression validated successfully.",
                                                    "payload": {
                                                        "expressionValue": 125.0,
                                                        "sampleSolution": {
                                                            "serviceOption": "EXPRESS",
                                                            "orderNo": "SO-100201",
                                                            "orderDate": "2023-03-15",
                                                            "destinationAddress": {
                                                                "zipCode": "RRR 1B5",
                                                                "region": "RRR",
                                                                "state": "Ontario",
                                                                "country": "CA",
                                                                "timezone": "UTC"
                                                            },
                                                            "customAttributes": {},
                                                            "totalNoOfShipments": 1,
                                                            "totalLines": 1,
                                                            "totalCost": 10.0,
                                                            "shipments": [
                                                                {
                                                                    "items": [
                                                                        {
                                                                            "itemId": "OPT-ITEM-4",
                                                                            "uom": "",
                                                                            "length": "10",
                                                                            "width": "15",
                                                                            "height": "20",
                                                                            "weight": "50",
                                                                            "weightUom": "g",
                                                                            "dimensionUom": "m",
                                                                            "productCategory": "ELECTRONICS",
                                                                            "handlingType": "MANUAL",
                                                                            "requestedQuantity": "8",
                                                                            "fulfilledQuantity": "5",
                                                                            "customAttributes": {}
                                                                        }
                                                                    ],
                                                                    "nodeDetails": {
                                                                        "nodeId": "nodeId1",
                                                                        "nodeType": "nodeType2",
                                                                        "nodeZipCode": "nodeZipCode2",
                                                                        "nodeLabourTier": ""
                                                                    },
                                                                    "carrierServiceId": "carrierServiceId1",
                                                                    "zone": "zone1",
                                                                    "cost": "10"
                                                                }
                                                            ]
                                                        },
                                                        "sampleRequest": {
                                                            "orgId": "NEXTUPLE_GR",
                                                            "serviceOption": "EXPRESS",
                                                            "fulfillmentType": null,
                                                            "orderDate": "2023-03-15",
                                                            "orderNo": "SO-100201",
                                                            "cartId": null,
                                                            "pageName": null,
                                                            "orderType": "MARKETPLACE",
                                                            "shipDestinationDetails": {
                                                                "zipCode": "RRR 1B5",
                                                                "region": "RRR",
                                                                "state": "Ontario",
                                                                "country": "CA",
                                                                "timezone": "UTC"
                                                            },
                                                            "estimatedDeliveryDate": null,
                                                            "carrierServiceId": "FEDEX_GROUND",
                                                            "reserveInventoryFlag": null,
                                                            "reserveCapacity": null,
                                                            "shipNode": null,
                                                            "customAttributes": {},
                                                            "orderLines": [
                                                                {
                                                                    "orderedQuantity": 6.0,
                                                                    "shipNode": null,
                                                                    "lineId": null,
                                                                    "reservationId": null,
                                                                    "carrierServiceId": "FEDEX_GROUND",
                                                                    "shipDestinationDetails": {
                                                                        "zipCode": "RRR 1B5",
                                                                        "region": "RRR",
                                                                        "state": "Ontario",
                                                                        "country": "Canada",
                                                                        "timezone": "UTC"
                                                                    },
                                                                    "serviceOption": null,
                                                                    "item": {
                                                                        "itemId": "OPT-ITEM-4",
                                                                        "productClass": "ELECTRONICS",
                                                                        "unitOfMeasure": "EACH"
                                                                    },
                                                                    "customAttributes": {}
                                                                }
                                                            ],
                                                            "sessionId": null
                                                        }
                                                    }
                                                }
                                        """),
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that request contains invalid data.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When bad request passed for validation via parsii library",
                  name =
                      "A 400 response indicate that passed request for validation via parsii library is invalid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "5e4e71a9-bb39-4f62-acba-ce13f933fe8c",
                                                    "timestamp": 1698635045791,
                                                    "message": "Exception while parsing formula via parsii :   1: 5: Unknown variable: 'itemLength2'",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "libraryName": {
                                                                "rejectedValue": "parsii"
                                                            },
                                                            "formulaExpression": {
                                                                "rejectedValue": "max(itemLength2*itemWidth*length,1+height)"
                                                            }
                                                        }
                                                    }
                                                }
                                        """),
              @ExampleObject(
                  summary = "When bad request passed for validation via spel library",
                  name =
                      "A 400 response indicate that passed request for validation via spel library is invalid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "0bbb29a5-99c7-46cf-b229-387feea241ca",
                                                    "timestamp": 1698635080380,
                                                    "message": "Exception while parsing formula via spel : EL1030E: The operator 'MULTIPLY' is not supported between objects of type 'null' and 'java.lang.Double'",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "libraryName": {
                                                                "rejectedValue": "spel"
                                                            },
                                                            "formulaExpression": {
                                                                "rejectedValue": "max(itemLength2*itemWidth*length,1+height)"
                                                            }
                                                        }
                                                    }
                                                }
                                        """),
              @ExampleObject(
                  summary = "When bad request passed for validation with unknown library",
                  name = "A 400 response indicate that unknown library is not supported",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "5db3f08b-e1c0-4e53-8abf-aaec6925e23f",
                                                    "timestamp": 1698634853704,
                                                    "message": "Passed library is not not supported",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "libraryName": {
                                                                "rejectedValue": "parsi"
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
public @interface ExpressionValidationDoc {}
