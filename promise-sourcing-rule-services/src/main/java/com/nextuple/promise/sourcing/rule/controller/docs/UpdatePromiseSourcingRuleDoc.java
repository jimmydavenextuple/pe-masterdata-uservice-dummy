package com.nextuple.promise.sourcing.rule.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Update Promise Source Rules",
    description =
        "Updates the priority for a particular promise sourcing rule. This API updates the information by passing the organization ID, service option, destination geo zone, source nodes, allocation rule ID, and priority attributes in the query parameters as well as the JSON body for the required promise sourcing rule.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a promise sourcing rule was updated successfully.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface UpdatePromiseSourcingRuleDoc {}
