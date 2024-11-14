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
    summary = "Get Config Data",
    description =
        "Retrieves the configuration data for the given organization ID, user exit, service, and app name.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the configuration data for the given organization ID, user exit, service, and app name is fetched successfully.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the configuration data was not found for the given parameters.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User Exit config data not found",
                  name =
                      "A 404 error code indicates that the configuration data was not found for the given parameters.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "1b966acb-40b9-4c33-83fd-de3e2270467e",
                                                    "timestamp": 1705672622473,
                                                    "message": "User Exit config data not found",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6006,
                                                        "fields": {
                                                            "ServiceName": {
                                                                "rejectedValue": "SourcingService"
                                                            },
                                                            "UserExitName": {
                                                                "rejectedValue": "GetSolutionCostForApiUE"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            },
                                                            "AppName": {
                                                                "rejectedValue": "PE"
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
public @interface FetchConfigDatadoc {}
