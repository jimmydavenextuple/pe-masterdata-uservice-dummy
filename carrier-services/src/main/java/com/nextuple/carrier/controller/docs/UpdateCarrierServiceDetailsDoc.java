package com.nextuple.carrier.controller.docs;

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
    summary = "Update Carrier Service",
    description =
        "Updates the carrier name, service name, and service options for a carrier service that has already been created. This API updates the information by passing the carrier ID, carrier service ID, and organization ID attributes in the path parameters for the required carrier service.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the carrier service details are updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the carrier service details are not updated as there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Carrier service not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Carrier service not found with given details.",
                  name =
                      "A 400 error code indicates that carrier service not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"ca798f9e-0b11-4520-b804-9bb3721404ba\",\n"
                          + "    \"timestamp\": 1679552291399,\n"
                          + "    \"message\": \"Carrier service not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"carrierId\": {\n"
                          + "                \"rejectedValue\": \"CarrierId-01\"\n"
                          + "            },\n"
                          + "            \"serviceId\": {\n"
                          + "                \"rejectedValue\": \"serviceId-01\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"OrgId-01\"\n"
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
public @interface UpdateCarrierServiceDetailsDoc {}
