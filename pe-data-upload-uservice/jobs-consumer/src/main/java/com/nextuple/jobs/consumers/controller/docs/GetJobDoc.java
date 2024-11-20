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
    summary = "Get Job",
    description = "Retrieves the job for given organization ID and job ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the jobs for the given organization ID and job ID are fetched successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that job was not fetched successfully for given job ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Exception while retrieving the job by jobId",
                  name =
                      "A 400 error code indicates that job was not fetched successfully for given job ID.",
                  value =
                      """
                                                    "success": false,
                                                    "requestId": "2ec0f8d5-f681-4c90-b781-c08a36d2e623",
                                                    "timestamp": 1705657360316,
                                                    "message": "Exception while retrieving the job by jobId",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 16777170,
                                                        "fields": {
                                                            "pageNo": {},
                                                            "jobType": {
                                                                "rejectedValue": "0003cfb9-8b6c-4a04-ba0d-950f0e97b51e"
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
public @interface GetJobDoc {}
