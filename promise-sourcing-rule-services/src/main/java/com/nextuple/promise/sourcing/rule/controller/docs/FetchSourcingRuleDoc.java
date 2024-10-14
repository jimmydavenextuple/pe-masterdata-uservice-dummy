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
    summary = "Get Promise Source Rules",
    description =
        "Retrieves the promise sourcing rules associated with a specific organization ID, service option, destination geo zone, and allocation ID."
            + "The API retrieves the details by passing the organization ID, service option, destination geo zone, and allocation ID attribute in the path parameters for the required promise sourcing rule.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that a sourcing rules are fetched successfully.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface FetchSourcingRuleDoc {}
