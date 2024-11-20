package com.nextuple.jobs.consumers.controller.docs;

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
    summary = "Create Job",
    description = "Creates jobs for the given organization ID and job ID. ")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the jobs for the given organization ID and job ID are created successfully.")
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that the request to create the job is incorrect.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "The value for status is not correct",
                  name = "A 400 error code indicates that the value for status is invalid.",
                  value =
                      """
                                        {
                                            "success": false,
                                            "requestId": "06ea87a6-99cc-4ddb-8629-d3f04332cd12",
                                            "timestamp": 1705657599385,
                                            "message": "Bad Request",
                                            "payload": {
                                                "type": "ERROR",
                                                "code": 2,
                                                "fields": {
                                                    "status": {
                                                        "rejectedValue": "dlkhfals",
                                                        "errorMessage": "values accepted for Enum class: [PROCESSING, COMPLETED, FAILED, PROCESSED, RUNNING, SUBMITTED]"
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
public @interface CreateJobDoc {}
