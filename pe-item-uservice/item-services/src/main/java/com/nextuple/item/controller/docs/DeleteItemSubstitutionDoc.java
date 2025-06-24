/*
 *  Copyright (c) 2025, Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 *  The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller.docs;

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

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Delete Item Substitution",
    description = "Remove an item substitution relationship")
@ApiResponse(
    responseCode = "200",
    description = "Item substitution successfully deleted",
    content =
        @Content(
            schema = @Schema(implementation = Void.class),
            examples = {
              @ExampleObject(
                  summary = "Item substitution successfully deleted",
                  name = "Delete an item substitution relationship",
                  value =
                      """
                                {
                                    "success": true,
                                    "requestId": "c7d82dbf-1f5b-47c1-8d83-8c346b9ea89a",
                                    "timestamp": 1701767128015,
                                    "message": "Item substitution deleted successfully",
                                    "payload": null
                                }
                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Primary Item ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Primary UOM is not passed.</li>"
            + "<li><b>Error code: 2</b>: Alternate Item ID is not passed.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "OrgId is not passed.",
                  name =
                      "A 400 error code indicates that the orgId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"571473d2-d27f-4f0b-903c-91149805876e\",\n"
                          + "    \"timestamp\": 1679648554379,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"Organisation ID can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}")
            }))
@ApiResponse(
    responseCode = "404",
    description = "Item substitution not found",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item substitution not found",
                  name =
                      "A 404 error code indicates that the substitution to delete was not found.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"571473d2-d27f-4f0b-903c-91149805876e\",\n"
                          + "    \"timestamp\": 1679648554379,\n"
                          + "    \"message\": \"Not Found\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 3,\n"
                          + "        \"message\": \"No substitution found for the item ITEM12345 with UOM EACH and alternate item ALT-ITEM-456\"\n"
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
public @interface DeleteItemSubstitutionDoc {}
