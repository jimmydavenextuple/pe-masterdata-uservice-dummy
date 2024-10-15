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
    summary = "Create Transit Details",
    description =
        "Creates the new transit data based on the items shipped from the source to the destination. This API allows retailers to specify different attributes such as organization ID, source geo zone, destination geo zone, carrier service ID, and transit days.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the transit details are created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Transit data cannot be created for the given carrier service ID and organization ID.</li>"
            + "<li><b>Error code: 2</b>: Geo zone is not valid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Indicates that the transit data cannot be created with given carrierServiceId and orgId.",
                  name =
                      "A 400 error code indicates that the transit data cannot be created with given carrierServiceId and orgId.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"19253187-cea6-4f26-adac-651f438b12ee#7\",\n"
                          + "    \"timestamp\": 1679983790813,\n"
                          + "    \"message\": \"Transit data cannot be created with given carrierServiceId and orgId\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"carrierServiceId\": {\n"
                          + "                \"rejectedValue\": \"UPS-GROUND\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"NEXTUPLE\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Indicates that the geoZone is not valid.",
                  name = "A 400 error code indicates that the geoZone is not valid.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"19253187-cea6-4f26-adac-651f438b12ee#6\",\n"
                          + "    \"timestamp\": 1679983789772,\n"
                          + "    \"message\": \"geoZone is not valid\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            },\n"
                          + "            \"geoZone\": {\n"
                          + "                \"rejectedValue\": \"M1R\"\n"
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
public @interface AddTransitDataDoc {}
