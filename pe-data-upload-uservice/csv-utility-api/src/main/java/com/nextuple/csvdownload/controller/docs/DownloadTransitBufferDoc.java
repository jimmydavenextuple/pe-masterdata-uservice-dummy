package com.nextuple.csvdownload.controller.docs;

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
    summary = "Download Transit Buffer",
    description = "Downloads the Transit Buffer for given transitBufferConfigRequestId.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 status code indicates that the Transit Buffer for given transitBufferConfigRequestId is downloaded successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to download transit buffer is not correct.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "The specified blob already exists",
                  name = "A 400 status code indicates that the specified blob already exists.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "6cbcd02f-d643-4271-83ac-e5992049a5c1#12252",
                                                    "timestamp": 1705647336042,
                                                    "message": "The specified blob already exists",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 1048561
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
public @interface DownloadTransitBufferDoc {}
