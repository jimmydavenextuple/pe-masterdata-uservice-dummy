package com.nextuple.pe.userexit.controller.docs;

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
@Operation(summary = "Update Metadata", description = "Updates the metadata for a user exit.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the metadata for the user exit is updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update the user exit metadata is invalid.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User Exit type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "9bd1add5-cc65-49e3-964a-81eea7eabf8c",
                                                    "timestamp": 1705901474920,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "type": {
                                                                "rejectedValue": "zczc",
                                                                "errorMessage": "values accepted for Enum class: [API, REGULAR]"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "User Exit execution failure type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit execution failure type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3b9c3da4-7996-4d88-9c7f-30a6ab3d019d",
                                                    "timestamp": 1705901657739,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "executionFailureType": {
                                                                "rejectedValue": "sdf",
                                                                "errorMessage": "values accepted for Enum class: [SOFT, HARD]"
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description = " A 404 error code indicates that the user exit metadata is not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User exit meta data is not found",
                  name = "A 404 error code indicates that the user exit meta data is not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "cff4f287-4248-47ac-8d62-cebcf663c7f5#10052",
                                                    "timestamp": 1705902722654,
                                                    "message": "User Exit meta data not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6006,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": "GetItemDetails"
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
public @interface UpdateMetadataDoc {}
