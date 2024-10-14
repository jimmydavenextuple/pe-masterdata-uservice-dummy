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
@Operation(summary = "Get Cost Factor", description = "Retrives the list of all the cost factors")
@ApiResponse(
    responseCode = "200",
    description = "A 200 OK response indicate that Cost Factor details are fetched successfully",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary =
                      "A 200 OK response indicate that Cost Factor details are fetched successfully",
                  name =
                      "A 200 OK response indicate that Cost Factor details are fetched successfully",
                  value =
                      """
                                                {
                                                     "success": true,
                                                     "requestId": "9210467b-f74d-4fe0-a4bb-774aa673fdb8#428",
                                                     "timestamp": 1701923618185,
                                                     "message": "Cost Factor Bucket Type Cache Keys fetched successfully",
                                                     "payload": [
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "carrierServiceId"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "carrierServiceId"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUps"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "billWeightNational"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "itemWeight"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "zone"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "surge"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "nodeType"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "nodeLabourTier"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061043689"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061043694"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0610436942"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061043693"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0610436932"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0612421942"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061242194"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0612421942"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061247812"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061247813"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0612478132"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061247813"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0612478132"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061250282"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061250283"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0612502832"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061501690"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061501691"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0615016912"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061501691"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061503844"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061503845"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0615038452"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061503845"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0615038452"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061507462"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061507462"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0615074622"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061507462"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0615074622"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061510996"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061510997"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0615109972"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061510997"
                                                         },
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "zone"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0618494652"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061108545"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061108546"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0611085462"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061250282"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0612502822"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061256845"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061256845"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0612568452"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061256845"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0612568452"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061259294"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061259295"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0612592952"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061259295"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0612592952"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061303993"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061303993"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0613039932"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061303993"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0613039932"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061308892"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061308892"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0613088922"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061308892"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0613088922"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0615109972"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061514204"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061514205"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0615142052"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061514205"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0615142052"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061849464"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061849465"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0618494652"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061849465"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061854508"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061854508"
                                                         },
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "zones"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0618545082"
                                                         },
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "height"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061854508"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0618545082"
                                                         },
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "billWeight"
                                                         },
                                                         {
                                                             "orgId": "XYZINC",
                                                             "costFactor": "nodeLabourTier"
                                                         },
                                                         {
                                                             "orgId": "SIGNET",
                                                             "costFactor": "BillWeight"
                                                         },
                                                         {
                                                             "orgId": "SIGNET",
                                                             "costFactor": "ItemWeight"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061108546"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0611085462"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061113338"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061113339"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance0611133392"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory061113339"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "ShipCategory0611133392"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "BillWeightUPS061404518"
                                                         },
                                                         {
                                                             "orgId": "NEXTUPLE_GR",
                                                             "costFactor": "Distance061404523"
                                                         }
                                                     ]
                                                 }
                                                                                                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "<li><b>Error code: 6001 </b></li><ul><li> When a given Cost Factor record not found</li></ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When a given Cost Factor record not found",
                  name = "A 404 response indicate that Cost Factor record not found",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "9210467b-f74d-4fe0-a4bb-774aa673fdb8#428",
                                                    "timestamp": 1701923618185,
                                                    "message": "Cost Factor Bucket Type Cache Keys fetched successfully",
                                                    "payload": [
                                                        {
                                                            "orgId": "XYZINC",
                                                            "costFactor": "carrierServiceId"
                                                        },
                                                        {
                                                            "orgId": "XYZINC",
                                                            "costFactor": "nodeLabourTier"
                                                        },
                                                        {
                                                            "orgId": "SIGNET",
                                                            "costFactor": "BillWeight"
                                                        },
                                                        {
                                                            "orgId": "NEXTUPLE_GR",
                                                            "costFactor": "Distance061404523"
                                                        }
                                                    ]
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
public @interface GetCostFactorCacheKeyDoc {}
