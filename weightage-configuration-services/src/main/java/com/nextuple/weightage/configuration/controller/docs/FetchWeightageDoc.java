package com.nextuple.weightage.configuration.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Fetch Weightage",
    description =
        "Retrieves the weightage configuration by the organization ID, configuration type and list of keys.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the weightage configuration is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Weightage configurations are not found for the given details.</li>"
            + "<li><b>Error code: 2</b>:  Organization ID field is not passed.</li>"
            + "<li><b>Error code: 2</b>: Type field is not passed.</li>"
            + "<li><b>Error code: 2</b>: Keys field is not passed.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Indicates that the Weightage Configurations not found for the given details.",
                  name =
                      "A 400 error code indicates that the indicates that the Weightage Configurations not found for the given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"76c1d735-7daa-4c72-a05c-6dfb9f694d95#35\",\n"
                          + "    \"timestamp\": 1679898306707,\n"
                          + "    \"message\": \"Weightage Configurations not found![401.503]\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the organization ID field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the organization ID field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"ca7a5cfc-6616-4196-bbf5-76cdb892570c#1\",\n"
                          + "    \"timestamp\": 1679898840680,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"type\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"orgId can't be empty.\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the type field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the type field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"ca7a5cfc-6616-4196-bbf5-76cdb892570c#1\",\n"
                          + "    \"timestamp\": 1679898840680,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"type\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"type can't be empty.\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the keys field is not passed.",
                  name =
                      "A 400 error code indicates that the indicates that the keys field is not passed.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"ca7a5cfc-6616-4196-bbf5-76cdb892570c#2\",\n"
                          + "    \"timestamp\": 1679898889815,\n"
                          + "    \"message\": \"Keys cannot contain null or an empty string\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001\n"
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
public @interface FetchWeightageDoc {}
