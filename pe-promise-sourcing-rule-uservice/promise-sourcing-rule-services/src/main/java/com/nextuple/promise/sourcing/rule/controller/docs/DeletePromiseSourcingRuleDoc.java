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
    summary = "Delete Promise Sourcing Rule ",
    description =
        "Deletes the promise sourcing rule associated with the priority when it is no longer required. Promise sourcing rules can be deleted for various reasons, including discontinuation of services from the list of available options for shipping or deleting all information associated with a specific promise sourcing rule. This API deletes the information by passing the Org ID, Service Option, Destination Geo Zone, Source Nodes, Allocation Rule ID, and Priority attributes in the query parameters for the required promise sourcing rule.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a promise sourcing rule is deleted successfully.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface DeletePromiseSourcingRuleDoc {}
