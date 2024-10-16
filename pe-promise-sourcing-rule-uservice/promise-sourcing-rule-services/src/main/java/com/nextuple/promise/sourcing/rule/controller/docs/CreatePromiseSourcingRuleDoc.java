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
    summary = "Create Promise Sourcing Rule",
    description =
        "Creates a new promise sourcing rule based on the priority of the source nodes. This API helps in deciding the priority of the source node that will source the destination and the EDD dates will be calculated according to that source node. If the priority value is of the highest order, the items get fulfilled at that source node. Once these items are fulfilled, they are ready to get shipped from the source node via different service options (Express, Standard, or SDND) to the desired destination geo zone. For example, for the destination geo zone 'M2A' with source node 'DC - 01' and service option 'STANDARD', the priority is set to '1'. Since the priority is of the highest order, the items will be fulfilled at source node 'DC - 01' and these items will be transported to the destination geo zone 'M2A' via service option 'STANDARD'."
            + "\n"
            + "This API allows enterprises to specify different attributes such as organization ID, service option, destination geo zone, source nodes, allocation rule ID, and priority.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that a new sourcing rule is created successfully based on priority of the source nodes.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface CreatePromiseSourcingRuleDoc {}
