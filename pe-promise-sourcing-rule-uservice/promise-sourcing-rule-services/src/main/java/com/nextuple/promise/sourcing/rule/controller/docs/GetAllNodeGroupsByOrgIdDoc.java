package com.nextuple.promise.sourcing.rule.controller.docs;

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
    summary = "Get Node Groups by OrgId",
    description = "Retrieves all the Node Groups by Organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "The 200 success code indicates that the list of node groups for given organization ID is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "The 400 error code indicates that the request to retrieve node groups for the organization ID is invalid.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Internal Server Error.",
                  name = "A 500 error code indicates that something went wrong.",
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
public @interface GetAllNodeGroupsByOrgIdDoc {}
