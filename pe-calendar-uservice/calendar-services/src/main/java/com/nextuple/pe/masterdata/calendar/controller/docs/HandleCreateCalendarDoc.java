package com.nextuple.pe.masterdata.calendar.controller.docs;

import com.nimbusds.oauth2.sdk.ErrorResponse;
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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Create Calendar",
    description =
        "Creates a working calendar with the calendar ID, organization ID, description, working days and exception days.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the calendar is created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the calendar has not been created successfully."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Calendar ID is blank.</li>"
            + "<li><b>Error code: 2</b>: The length must be between 0 and 40.</li>"
            + "<li><b>Error code: 2</b>: Organization ID is blank.</li>"
            + "<li><b>Error code: 6002</b>: Calendar already exists for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "CalendarId is not passed.",
                  name =
                      "A 400 error code indicates that the CalendarId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"8fe0ffab-55d3-418e-9744-3b74021b46b9\",\n"
                          + "    \"timestamp\": 1679539898456,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"calendarId can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Length must be between 0 and 40.",
                  name = "A 400 error code indicates that the length must be between 0 and 40.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"9c06a471-0f20-4f33-82e1-7cf3676f6062\",\n"
                          + "    \"timestamp\": 1679542611454,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"ssssssssssssssssssssssssssssssssssssssssssssssssssssss\",\n"
                          + "                \"errorMessage\": \"length must be between 0 and 40\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "OrgId is not passed.",
                  name =
                      "A 400 error code indicates that the OrgId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"0056221d-1932-4cee-afa7-20958a7ca9d2\",\n"
                          + "    \"timestamp\": 1679542746802,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"orgId can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Calendar already exists for the given details.",
                  name =
                      "A 400 error code indicates that the calendar already exists for the given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"2768585b-1794-4889-ac92-5a9ef99dcbca\",\n"
                          + "    \"timestamp\": 1679542828887,\n"
                          + "    \"message\": \"Calendar already exists for the given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6002,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"Cid\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
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
            schema =
                @Schema(implementation = com.nextuple.common.response.error.ErrorResponse.class),
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
public @interface HandleCreateCalendarDoc {}
