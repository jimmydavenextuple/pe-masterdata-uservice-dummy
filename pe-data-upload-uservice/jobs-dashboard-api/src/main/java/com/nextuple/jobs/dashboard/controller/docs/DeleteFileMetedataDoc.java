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
    summary = "Delete Metadata by ID",
    description = "Deletes a file metadata for the file with the given ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the file metadata for a given ID is deleted successfully.")
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that file metadata for given ID was not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "File metadata for given ID is not found",
                  name =
                      "A 400 error code indicates that file metadata for given ID was not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "d30026e7-241b-45ae-8eb3-7c0eade40392",
                                                    "timestamp": 1705666826157,
                                                    "message": "Failed to find record: ID does not exist",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 16777209,
                                                        "fields": {
                                                            "Id": {
                                                                "rejectedValue": 784949
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
public @interface DeleteFileMetedataDoc {}
