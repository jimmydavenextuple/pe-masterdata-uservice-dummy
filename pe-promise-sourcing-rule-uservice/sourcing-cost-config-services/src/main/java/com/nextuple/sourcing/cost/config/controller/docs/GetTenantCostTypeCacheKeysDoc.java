package com.nextuple.sourcing.cost.config.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Tenant Cost Type Cache Keys",
    description = "Retrieves the list of all the cache keys for the given tenant cost type.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the the list of all the cache keys for tenant cost type are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary =
                      "When the list of all the cache keys for tenant cost type are retrieved successfully.",
                  name =
                      "A 200 success code indicates that all the cache keys for tenant cost type are retrieved successfully.",
                  value =
                      """
                                                {
                                                        "success": true,
                                                        "requestId": "1b924fa1-cd0e-451c-9ec3-9b520c8c25a5#1271",
                                                        "timestamp": 1701763961129,
                                                        "message": "Tenant cost type Keys fetched successfully",
                                                        "payload": [
                                                            {
                                                                "orgId": "SIGNET"
                                                            },
                                                            {
                                                                "orgId": "NEXTUPLE_GR"
                                                            },
                                                            {
                                                                "orgId": "XYZINC"
                                                            }
                                                        ]
                                                    }
                                                          """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Required request parameter \"limit\" for the method parameter type \"integer\" is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "'limit' parameter not present.",
                  name = "A 404 error code indicates that the 'limit' parameter not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"452d3fc1-27a1-4d2b-aeeb-ba4908a03eee\",\n"
                          + "    \"timestamp\": 1679576331648,\n"
                          + "    \"message\": \"Required request parameter 'limit' for method parameter type Integer is not present\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
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
public @interface GetTenantCostTypeCacheKeysDoc {}
