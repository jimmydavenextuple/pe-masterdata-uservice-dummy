/*Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
*/
package com.nextuple.postal.code.timezone.controller.docs;

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

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Deleting custom region details",
    description = "Deletes the custom region details of given custom region ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that custom region details of the given custom region ID are successfully deleted.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to delete custom region details of the given custom region ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6004</b>: Custom region is not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Custom Region not found.",
                  name =
                      "A 404 error code indicates that the custom region is not found for the given details.",
                  value =
                      """
        {
            "success": false,
            "requestId": "db8b5231-9ee1-4014-bc0e-c4b55e4d5c5c#2697",
            "timestamp": 1679922274917,
            "message": "Custom Region not found",
            "payload": {
                "type": "ERROR",
                "code": 6004,
                "fields": {
                    "org_id": {
                        "rejectedValue": "NEXTUPLE_GR"
                    },
                    "id": {
                        "rejectedValue": "CRID1"
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
            "timestamp": "1670589273234",
            "payload": {
                "type": "ERROR",
                "code": 2
            }
        }
        """)
            }))
public @interface DeleteCustomRegionGeozones {}
