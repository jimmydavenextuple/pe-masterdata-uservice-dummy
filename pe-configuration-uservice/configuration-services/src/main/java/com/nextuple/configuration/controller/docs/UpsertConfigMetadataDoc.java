/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
    summary = "Upsert Configuration Metadata",
    description =
        "Creates new configuration metadata if it does not exist, or updates the existing one if found using configKey.")
@ApiResponse(
    responseCode = "200",
    description = "Configuration metadata created or updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "<ul>"
            + "<li><b>Error code: 6001</b>: When configuration key format is invalid</li>"
            + "<li><b>Error code: 6002</b>: When metadata update fails due to inconsistent data</li>"
            + "</ul>",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
    responseCode = "500",
    description = "Internal server error while processing upsert configuration metadata.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface UpsertConfigMetadataDoc {}
