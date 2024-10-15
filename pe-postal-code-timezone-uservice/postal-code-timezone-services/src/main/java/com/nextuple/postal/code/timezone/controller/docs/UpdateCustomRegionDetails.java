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
    summary = "Updating custom region details",
    description = "Updates the custom region details.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that custom region details for the given custom region ID are updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update custom region details for the given custom region ID is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6004</b>: Custom region is not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Custom Region not found.",
                  name = "A 404 error code indicates that there is some issue with the input.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"db8b5231-9ee1-4014-bc0e-c4b55e4d5c5c#2697\",\n"
                          + "    \"timestamp\": 1679922274917,\n"
                          + "    \"message\": \"Custom Region not found\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6004,\n"
                          + "        \"fields\": {\n"
                          + "            \"org_id\": {\n"
                          + "                \"rejectedValue\": \"NEXTUPLE_GR\"\n"
                          + "            },\n"
                          + "            \"id\": {\n"
                          + "                \"rejectedValue\": \"CRID1\"\n"
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
public @interface UpdateCustomRegionDetails {}
