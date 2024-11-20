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
    summary = "Get Transit Buffer Request Job Reference",
    description =
        "Retrieves the details for transit buffer request job reference for a given external reference ID.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transit buffer request job reference is retrieved successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 16777209</b>: Transit buffer job references do not exist for the given ID.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Indicates that the transit buffer job references does not exist for the given ID.",
                  name =
                      "A 400 error code Indicates that the transit buffer job references does not exist for the given ID.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"c79d3e24-7870-49e6-98d6-7206ffadd54d#1174\",\n"
                          + "    \"timestamp\": 1679996267363,\n"
                          + "    \"message\": \"Unable to find transit buffer job references with this ID: 2\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 16777209,\n"
                          + "        \"fields\": {\n"
                          + "            \"extReferenceId\": {\n"
                          + "                \"rejectedValue\": \"2\"\n"
                          + "            },\n"
                          + "            \"id\": {}\n"
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
public @interface FindTransitBufferReqJobRefByExtRefIdDoc {}
