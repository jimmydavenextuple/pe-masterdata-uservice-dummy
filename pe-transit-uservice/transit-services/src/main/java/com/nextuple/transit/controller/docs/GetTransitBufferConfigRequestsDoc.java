package com.nextuple.transit.controller.docs;

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
    summary = "Get List of Transit Buffer Configuration Requests",
    description =
        "Retrieves the list of transit buffer configuration requests. This API fetches the information by passing the organization ID in the path variable and the carrier service ID in the request parameters.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of transit buffer configuration requests is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Required request parameter “carrierServiceId” is null.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Indicates that the required request parameter 'carrierServiceId' for method parameter type TransitBufferConfigRequestStatusEnum is present but converted to null.",
                  name =
                      "A 400 error code indicates that the required request parameter 'carrierServiceId' for method parameter type TransitBufferConfigRequestStatusEnum is present but converted to null.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"8e54f714-5fdc-4734-9c26-d51d3ed06713#299\",\n"
                          + "    \"timestamp\": 1679998510840,\n"
                          + "    \"message\": \"Required request parameter 'carrierServiceId' for method parameter type TransitBufferConfigRequestStatusEnum is present but converted to null."
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
                          + "    }\n"
                          + "}")
            }))
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
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"timestamp\": \"1670589273234\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2\n"
                          + "    }\n"
                          + "}")
            }))
public @interface GetTransitBufferConfigRequestsDoc {}
