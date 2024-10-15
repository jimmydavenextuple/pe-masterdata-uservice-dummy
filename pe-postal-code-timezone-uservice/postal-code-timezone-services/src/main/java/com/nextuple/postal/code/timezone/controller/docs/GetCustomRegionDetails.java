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
    description = "Fetches the custom region details based on org Id and region ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that custom region details are retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6003</b>: Zip code is not associated with the given codes.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Zip code association not found for the given codes.",
                  name =
                      "A 400 error code indicates that the zip code association not found for the given codes..",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"620bff35-bb4d-4b47-ad6d-bc409dbd0d65\",\n"
                          + "    \"timestamp\": 1679920796518,\n"
                          + "    \"message\": \"No Zip Code association found for the given codes\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6003,\n"
                          + "        \"fields\": {\n"
                          + "            \"org_id\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            },\n"
                          + "            \"unavailable_codes\": {\n"
                          + "                \"rejectedValue\": \"[T2P1S1, T2P1S2]\"\n"
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
public @interface GetCustomRegionDetails {}
