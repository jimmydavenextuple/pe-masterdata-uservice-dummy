package com.nextuple.postal.code.timezone.controller.docs;

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
    summary = "Fetch custom region details",
    description = "Fetches the custom region id based on zip code.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that custom region ID is retrieved successfully.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Zip code is not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Zip Code not found.",
                  name =
                      "A 404 error code indicates that the zip code is not found with the given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"d306ddcd-d6a9-4d27-a37d-79acb407bc2e#443\",\n"
                          + "    \"timestamp\": 1679922938391,\n"
                          + "    \"message\": \"Zip Code not found\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"zip_code\": {\n"
                          + "                \"rejectedValue\": \"T2PS2K\"\n"
                          + "            }\n"
                          + "        }\n"
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
                  summary = "There was some error on server while processing the request",
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
public @interface GetCustomRegionIdDetails {}
