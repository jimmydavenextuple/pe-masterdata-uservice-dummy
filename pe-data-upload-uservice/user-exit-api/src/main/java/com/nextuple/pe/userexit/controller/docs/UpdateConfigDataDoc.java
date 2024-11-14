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
@Operation(
    summary = "Update Config Data",
    description = "Updates the configuration data for a user exit.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the configuration data for the user exit is updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the user exit implementation type value is incorrect.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User Exit implementation type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit implementation type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "0134ba6f-cc76-4af0-9d7a-abec98464bb1#16229",
                                                    "timestamp": 1705900857561,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "ueImplType": {
                                                                "rejectedValue": "ZPI",
                                                                "errorMessage": "values accepted for Enum class: [MOCK, REST]"
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "404",
    description = "A 404 error code indicates that the user exit configuration data was not found.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "UUser Exit config data not found",
                  name = "A 404 error code indicates that the user exit config data was not found.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "1471a531-b1dd-4e93-97d8-81ad357735ce#3278",
                                                    "timestamp": 1705902338769,
                                                    "message": "User Exit config data not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6006,
                                                        "fields": {
                                                            "id": {
                                                                "rejectedValue": "GetSourcingRules"
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
public @interface UpdateConfigDataDoc {}
