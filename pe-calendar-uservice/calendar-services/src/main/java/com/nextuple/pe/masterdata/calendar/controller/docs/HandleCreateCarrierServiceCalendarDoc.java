package com.nextuple.pe.masterdata.calendar.controller.docs;

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
    summary = "Create Carrier Service Calendar",
    description =
        "Creates a working calendar that can be associated with a particular carrier service, using different attributes such as calendar ID, organization ID, carrier service ID, shipping stage, effective date, and description.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the carrier service calendar is created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the calendar has not been created successfully."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: A carrier service calendar cannot be created because the calendar ID or organization ID is invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Cannot create a carrier service calendar as calendar Id/organization Id is invalid.",
                  name =
                      "A 400 error code indicates that the calendarId/organization Id is invalid.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"3c6d441d-2030-48f0-80d5-2e424b3a2ac7#12\",\n"
                          + "    \"timestamp\": 1679546705676,\n"
                          + "    \"message\": \"Cannot create a carrier service calendar as calendarId/orgId is invalid\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"test_11\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"NAY\"\n"
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
public @interface HandleCreateCarrierServiceCalendarDoc {}
