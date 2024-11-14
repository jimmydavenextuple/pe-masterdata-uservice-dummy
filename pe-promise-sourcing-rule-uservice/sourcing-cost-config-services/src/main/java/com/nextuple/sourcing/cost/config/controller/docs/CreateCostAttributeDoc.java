/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
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
@Operation(summary = "Create Cost Attribute", description = "Creates a Cost Attribute.")
@ApiResponse(
    responseCode = "201",
    description = "When Cost Attribute is successfully created.",
    content =
        @Content(
            schema = @Schema(implementation = CostAttributeDto.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Attribute is successfully created.",
                  name = "A 201 response indicates that the Cost Attribute is successfully created",
                  value =
                      """
                              {
                                      "success": true,
                                      "requestId": "5c8ee03e-abcf-4399-90f3-b133aaf8be06#5237",
                                      "timestamp": 1701944501617,
                                      "message": "Cost Attribute created successfully!",
                                      "payload": {
                                          "id": 33,
                                          "attributeName": "itemWidth",
                                          "displayName": "Item Width",
                                          "attributeDescription": "Width of the item",
                                          "isPublished": true,
                                          "path": "/width",
                                          "lookupContext": "SOLUTION"
                                      }
                                  }
                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: When Cost Attribute name is invalid.</li>"
            + "<li><b>Error code: 2</b>: When Cost attribute already exist for given attribute name.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When Cost Attribute name is invalid.",
                  name = "A 400 response indicates that the Cost Attribute name is invalid.",
                  value =
                      """
                              {
                                  "success": false,
                                  "requestId": "5c8ee03e-abcf-4399-90f3-b133aaf8be06#5346",
                                  "timestamp": 1701944632089,
                                  "message": "Invalid format! Only alphanumeric characters allowed.",
                                  "payload": {
                                      "type": "ERROR",
                                      "code": 6001,
                                      "fields": {
                                          "name": {
                                              "rejectedValue": "item-Width"
                                          }
                                      }
                                  }
                              }
                        """),
              @ExampleObject(
                  summary = "When Cost attribute already exist for given attribute name.",
                  name =
                      "A 400 response indicates that the Cost attribute already exist for given attribute name.",
                  value =
                      """
                                            {
                                                    "success": false,
                                                    "requestId": "5c8ee03e-abcf-4399-90f3-b133aaf8be06#5275",
                                                    "timestamp": 1701944522289,
                                                    "message": "Cost attribute already exist for given attribute name",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6001,
                                                        "fields": {
                                                            "attributeName": {
                                                                "rejectedValue": "itemWidth"
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
public @interface CreateCostAttributeDoc {}
