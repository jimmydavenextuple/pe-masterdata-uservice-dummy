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
    summary = "Update Transit Buffer Days",
    description =
        "Updates the transit buffer days information for the given organization ID, source geo zone, destination geo zone and carrier service ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transit buffer days information is updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Source geo zone is blank.</li>"
            + "<li><b>Error code: 2</b>: Destination geo zone is blank.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates that the sourceGeoZone is blank.",
                  name = "A 404 error code indicates that the sourceGeoZone is blank.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"7a5a082d-95c4-40bb-b2c8-d2ccd2b9210a#619\",\n"
                          + "    \"timestamp\": 1679991211059,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"sourceGeoZone\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"sourceGeoZone can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the destinationGeoZone is blank.",
                  name = "A 404 error code indicates that the destinationGeoZone is blank.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"7a5a082d-95c4-40bb-b2c8-d2ccd2b9210a#619\",\n"
                          + "    \"timestamp\": 1679991211059,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"sourceGeoZone\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"sourceGeoZone can't be blank\"\n"
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
public @interface UpdateTransitBufferDayDoc {}
