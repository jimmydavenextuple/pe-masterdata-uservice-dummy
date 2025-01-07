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
    summary = "Delete Node Carrier Service Calendar for Node ID",
    description = "Deletes Node Carrier Service Calendars for the given node Id.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of carrier service calendars was deleted successfully.")
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
                            "timestamp": "1670589273234",
                            "payload": {
                                "type": "ERROR",
                                "code": 2
                            }
                        }
                        """)
            }))
public @interface DeleteNodeCarrierServiceCalendarForNodeIdDoc {}
