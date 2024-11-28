package com.nextuple.pe.masterdata.calendar.controller.docs;

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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get all Node Carrier Service Calendars List by org ID",
    description = "Retrieves the list of the node carrier service calendars by organization ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of node carrier service calendars for given orgId is retrieved successfully.")
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
                      """
                                  {
                                    "success": false,
                                    "requestId": "75d5537d-60a6-4a2c-999f-07e68d8a36d4",
                                    "timestamp": 1698040474473,
                                    "payload": {
                                      "type": "ERROR",
                                      "code": 2
                                    }
                                  }
                                  """)
            }))
public @interface GetNodeCarrierServiceCalendarDoc {}
