package com.nextuple.jobs.dashboard.controller.docs;

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
    summary = "Get Pre Signed URL",
    description = "Retrieves the Pre Signed URL from the blob storage.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 status code indicates that the list of Jobs for given organization ID and job ID is fetched successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to retrieve pre signed URL is invalid.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "The module name is not valid.",
                  name =
                      "A 400 error code indicates that there was some error on the module name passed is not valid.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "cff4f287-4248-47ac-8d62-cebcf663c7f5#7215",
                                                    "timestamp": 1705662263963,
                                                    "message": "module name is not valid",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6008,
                                                        "fields": {
                                                            "moduleName": {
                                                                "rejectedValue": "postal-code-timezon"
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
                      "{\n"
                          + "  \"success\": false,\n"
                          + "  \"requestId\": \"75d5537d-60a6-4a2c-999f-07e68d8a36d4\",\n"
                          + "  \"timestamp\": 1698040474473,\n"
                          + "  \"payload\": {\n"
                          + "    \"type\": \"ERROR\",\n"
                          + "    \"code\": 2\n"
                          + "  }\n"
                          + "}")
            }))
public @interface GetPreSignedUrlDoc {}
