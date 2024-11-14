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
    summary = "Delete Zip Code Timezone",
    description =
        "Deletes the zip code timezone for the given organization ID and zip code prefix.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the custom zip code timezone is successfully deleted.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Zip code timezone is not found.</li>"
            + "<li><b>Error code: 4</b>: Organization ID is not present.</li>"
            + "<li><b>Error code: 4</b>: Zip code prefix is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Zip code timezone not found with the given details.",
                  name =
                      "A 400 error code indicates that the zip code timezone not found with the given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"e43eee06-04d7-4964-800d-a2ef21791ec3\",\n"
                          + "    \"timestamp\": 1679915788358,\n"
                          + "    \"message\": \"Zip Code Timezone not found![401.503]\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the required request parameter orgId is not present.",
                  name =
                      "A 400 error code indicates that the required request parameter orgId is not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f381924-e1e6-4ed0-b21d-42ff67acf7bf\",\n"
                          + "    \"timestamp\": 1679916047822,\n"
                          + "    \"message\": \"Required request parameter 'orgId' for method parameter type String is not present\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary =
                      "Indicates that the required request parameter zipCodePrefix is not present.",
                  name =
                      "A 400 error code indicates that the required request parameter zipCodePrefix is not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"b0b51d8f-ec8d-4774-965c-be841eea4dbd\",\n"
                          + "    \"timestamp\": 1679916082312,\n"
                          + "    \"message\": \"Required request parameter 'zipCodePrefix' for method parameter type String is not present\",\n"
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
public @interface DeletePostalCodeTimezoneDetails {}
