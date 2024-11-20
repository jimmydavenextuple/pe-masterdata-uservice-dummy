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
    summary = "Get Promise Sourcing Rules by Priority",
    description =
        "Retrieves the information about promise sourcing rules associated with the priority. This API retrieves the source nodes and organization ID combinations associated with the priority by passing the priority attribute in the path parameters for the required promise sourcing rule.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the information about the promise sourcing rules associated with the priority was fetched successfully.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface GetPromiseSourcingRulesByPriority {}
