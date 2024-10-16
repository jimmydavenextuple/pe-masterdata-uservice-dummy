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
    summary = "Delete Carrier Service",
    description =
        "Deletes a carrier service when it is no longer required. Carrier services can be deleted for various reasons, including discontinuation of services from the list of available options for shipping or deleting all information associated with a specific carrier service. This API deletes the information by passing the carrier ID, carrier service ID and organization ID attributes in the path parameters for the required carrier service.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the carrier service is deleted successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the carrier service is not deleted as there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Carrier cannot be deleted. Please delete the associated node carrier and last pickup time details before deleting this carrier.</li>"
            + "<li><b>Error code: 6001</b>: Carrier service not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Carrier cannot be deleted. Please delete the associated node-carrier and last pickup time details before deleting this carrier",
                  name = "A 400 error code indicates that carrier service cannot be deleted.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"c79773de-c295-442c-a669-b5a22acdba07#2178\",\n"
                          + "    \"timestamp\": 1679552578074,\n"
                          + "    \"message\": \"Carrier cannot be deleted. Please delete the associated node-carrier and last Pickup time details before deleting this carrier\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"carrierId\": {\n"
                          + "                \"rejectedValue\": \"A_STD\"\n"
                          + "            },\n"
                          + "            \"serviceId\": {\n"
                          + "                \"rejectedValue\": \"ALL-STANDARD\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Carrier service not found with given details.",
                  name =
                      "A 400 error code indicates that carrier service not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"9296307e-6617-4fa2-becc-cc69344ada02\",\n"
                          + "    \"timestamp\": 1679551793189,\n"
                          + "    \"message\": \"Carrier service not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"carrierId\": {\n"
                          + "                \"rejectedValue\": \"CID\"\n"
                          + "            },\n"
                          + "            \"serviceId\": {\n"
                          + "                \"rejectedValue\": \"CSID\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"DE\"\n"
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
public @interface DeleteCarrierServiceDoc {}
