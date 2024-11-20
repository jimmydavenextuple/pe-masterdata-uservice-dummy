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
    summary = "Get Carrier Service Calendars List",
    description = "Retrieves the list of the carrier service calendars by carrier service ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of carrier service calendars is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: A carrier service calendar cannot be created because the calendar ID or organization ID is invalid.</li>"
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
                          + "    \"requestId\": \"d07c1f52-4415-49ff-a086-5a2201b81592\",\n"
                          + "    \"timestamp\": 1679548038778,\n"
                          + "    \"message\": \"Calender doesn't exist\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 16777201,\n"
                          + "        \"fields\": {\n"
                          + "            \"carrierServiceId\": {\n"
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
public @interface HandleGetCarrierServiceCalenderDoc {}
