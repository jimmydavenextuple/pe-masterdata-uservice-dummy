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
    summary = "Get Distinct Source Geo Zones and Destination Geo Zones",
    description =
        "Retrieves the unique source and destination geo zones for the given organization ID and carrier service ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the unique source and destination geo zone information is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Required request parameter “orgId” is not present.</li>"
            + "<li><b>Error code: 4</b>: Required request parameter “carrierServiceId” is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Indicates that the required request parameter 'orgId' for method parameter type String is not present.",
                  name =
                      "A 400 error code indicates that the required request parameter 'orgId' for method parameter type String is not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"c79d3e24-7870-49e6-98d6-7206ffadd54d#1115\",\n"
                          + "    \"timestamp\": 1679995516439,\n"
                          + "    \"message\": \"Required request parameter 'orgId' for method parameter type String is not present\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary =
                      "Indicates that the required request parameter 'carrierServiceId' for method parameter type String is not present.",
                  name =
                      "A 400 error code indicates that the required request parameter 'carrierServiceId' for method parameter type String is not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"c79d3e24-7870-49e6-98d6-7206ffadd54d#1120\",\n"
                          + "    \"timestamp\": 1679995578123,\n"
                          + "    \"message\": \"Required request parameter 'carrierServiceId' for method parameter type String is not present\",\n"
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
public @interface GetDistinctSourceAndDestinationGeozonesDoc {}
