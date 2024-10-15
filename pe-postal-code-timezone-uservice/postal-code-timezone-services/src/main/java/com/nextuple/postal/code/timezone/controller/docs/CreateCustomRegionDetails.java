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
    summary = "Create custom region details",
    description = "Creates the custom region details after performing validations.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that custom region details is successfully created.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: ID field is not passed.</li>"
            + "<li><b>Error code: 2</b>: Organization ID field is not passed.</li>"
            + "<li><b>Error code: 6003</b>: Zip code is not associated with the given codes.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates that the ID field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the ID field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"56967abd-e0af-4084-be3d-535b312070a5\",\n"
                          + "    \"timestamp\": 1679920567484,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"id\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"id can't be empty\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the org ID field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the org ID field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"56967abd-e0af-4084-be3d-535b312070a5\",\n"
                          + "    \"timestamp\": 1679920567484,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"id\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"orgId can't be empty\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the org ID field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the org ID field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"842ab360-4f99-4baa-bba6-c3e7620ef34f\",\n"
                          + "    \"timestamp\": 1680753756080,\n"
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
public @interface CreateCustomRegionDetails {}
