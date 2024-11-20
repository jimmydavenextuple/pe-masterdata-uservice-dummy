package com.nextuple.node.carrier.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Update Node-Carrier Association",
    description = NodeCarrierConstants.UPDATE_NODE_CARRIER_DESC)
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node-carrier association details are updated successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update node-carrier association is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6003</b>: Node-carrier service option is not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carrier Service Option not found for given details.",
                  name =
                      "A 404 error code indicates that the node carrier service option not found for given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"57dee076-575c-46dc-a5f7-45ff6411d5a6#7\",\n"
                          + "    \"timestamp\": 1679889091345,\n"
                          + "    \"message\": \"Node Carrier Service Option not found for given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6003,\n"
                          + "        \"fields\": {\n"
                          + "            \"serviceOption\": {\n"
                          + "                \"rejectedValue\": \"EXPR\"\n"
                          + "            },\n"
                          + "            \"carrierServiceId\": {\n"
                          + "                \"rejectedValue\": \"ALL-EXPRESS\"\n"
                          + "            },\n"
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
public @interface UpdateNodeCarrierDoc {}
