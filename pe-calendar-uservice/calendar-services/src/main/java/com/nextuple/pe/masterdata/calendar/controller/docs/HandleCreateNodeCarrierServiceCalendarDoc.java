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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Create Node Carrier Service Calendar",
    description =
        "Creates a working calendar, that will be associated with a particular node carrier service, using different attributes such as calendar ID, organization ID, node ID, carrier service ID, effective date, and description.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node carrier service calendar is created successfully.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>:A node carrier service calendar cannot be created because the calendar ID or organization ID is invalid.</li>"
            + "<li><b>Error code: 6001</b>: Node carrier service calendar already exists for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Calendar does not exist.",
                  name = "A 400 error code indicates that the calendarId/orgId is invalid.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"3c6d441d-2030-48f0-80d5-2e424b3a2ac7#48\",\n"
                          + "    \"timestamp\": 1679547850996,\n"
                          + "    \"message\": \"Cannot create a node carrier service calendar as calendarId/orgId is invalid\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"test_\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Calendar does not exist.",
                  name =
                      "A 400 error code indicates that node carrier service calendar already exists for the given details..",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"95f6439f-0351-4ac8-b649-fd843576155c#55\",\n"
                          + "    \"timestamp\": 1679547546646,\n"
                          + "    \"message\": \"Node Carrier Service Calendar already exists for the given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6002,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"test_2\"\n"
                          + "            },\n"
                          + "            \"carrierServiceId\": {\n"
                          + "                \"rejectedValue\": \"123\"\n"
                          + "            },\n"
                          + "            \"nodeId\": {\n"
                          + "                \"rejectedValue\": \"123\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            },\n"
                          + "            \"effectiveDate\": {\n"
                          + "                \"rejectedValue\": \"2022-08-04\"\n"
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
public @interface HandleCreateNodeCarrierServiceCalendarDoc {}
