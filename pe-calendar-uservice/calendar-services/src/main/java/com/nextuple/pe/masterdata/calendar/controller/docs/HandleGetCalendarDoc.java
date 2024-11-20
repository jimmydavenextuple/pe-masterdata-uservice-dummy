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
    summary = "Get Calendar By Calendar ID",
    description =
        "Retrieves the information about working calendars. This API retrieves the information by passing the organization ID and calendar ID attributes in the path parameters of the request.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the calendar details are retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the calendar details have not been retrieved successfully."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Calendar does not exist.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Calendar does not exist.",
                  name = "A 400 error code indicates that the calendar does not exist.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"f6e63bfa-d657-4495-9e6b-45f7df99315c\",\n"
                          + "    \"timestamp\": 1679543597755,\n"
                          + "    \"message\": \"Calendar does not exist\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"C_ID_FALSE\"\n"
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
public @interface HandleGetCalendarDoc {}
