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
    summary = "Create Node Calendar",
    description =
        "Creates a working calendar that can be associated with a particular node, using different attributes such as calendar ID, organization ID, node ID, effective date and description.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the node calendar is created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the node calendar has not been created."
            + "<ul>"
            + "<li><b>Error code: 6002</b>: Node calendar already exists for the given details.</li>"
            + "<li><b>Error code: 6016</b>: Node calendar cannot be created because the node ID is invalid or the node is inactive.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node calendar already exists for the given details.",
                  name =
                      "A 400 error code indicates that node calendar already exists for the given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"729b621a-c58c-4cb8-be4e-d1694ac68983#1794\",\n"
                          + "    \"timestamp\": 1679545109052,\n"
                          + "    \"message\": \"Node Calendar already exists for the given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6002,\n"
                          + "        \"fields\": {\n"
                          + "            \"calendarId\": {\n"
                          + "                \"rejectedValue\": \"test_10\"\n"
                          + "            },\n"
                          + "            \"nodeId\": {\n"
                          + "                \"rejectedValue\": \"124\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            },\n"
                          + "            \"effectiveDate\": {\n"
                          + "                \"rejectedValue\": \"2022-07-24\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Cannot create the calendar as node id is invalid or node is inactive.",
                  name =
                      "A 400 error code indicates that the node id is invalid or node is inactive.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"d073c90e-d071-407a-b80a-c63839d43266\",\n"
                          + "    \"timestamp\": 1679544732160,\n"
                          + "    \"message\": \"Cannot create the calendar as Node id is invalid or node is inactive\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6016,\n"
                          + "        \"fields\": {\n"
                          + "            \"nodeId\": {\n"
                          + "                \"rejectedValue\": \"123\"\n"
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
public @interface HandleCreateNodeCalendarDoc {}
