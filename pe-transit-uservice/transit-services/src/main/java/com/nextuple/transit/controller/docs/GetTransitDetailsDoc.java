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
    summary = "Get Transit Details",
    description =
        "Retrieves the transit details associated with the source geo zone and the destination geo zone. The API retrieves the details including buffer days, buffer start date, and buffer end date by passing the organization ID, source geo zone, destination geo zone, carrier service ID, and service option attributes in the path parameters for the required transit.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transit details are retrieved successfully.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 response code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Transit data not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates that the transit data not found with given details.",
                  name =
                      "A 400 error code indicates that the transit data not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"b5e8ae16-d9d3-4e0d-89ae-f21a70cc40a6#227\",\n"
                          + "    \"timestamp\": 1679985651315,\n"
                          + "    \"message\": \"Transit data not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"sourceGeozone\": {\n"
                          + "                \"rejectedValue\": \"V6C\"\n"
                          + "            },\n"
                          + "            \"destinationGeozone\": {\n"
                          + "                \"rejectedValue\": \"Y0A\"\n"
                          + "            },\n"
                          + "            \"carrierServiceId\": {\n"
                          + "                \"rejectedValue\": \"ALL\"\n"
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
public @interface GetTransitDetailsDoc {}
