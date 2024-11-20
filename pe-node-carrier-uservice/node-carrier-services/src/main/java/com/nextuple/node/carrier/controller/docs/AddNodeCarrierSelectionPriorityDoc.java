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
    summary = "Add Node-Carrier Selection Priority",
    description = NodeCarrierConstants.ADD_NC_SELECTION_PRI_DESC)
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the priority for the node-carrier association is added successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Service option is not passed.</li>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Service Option is not",
                  name =
                      "A 400 error code indicates that the service option is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"4811dcb1-881e-4223-ae6e-69a05a118c9e#382\",\n"
                          + "    \"timestamp\": 1679888146400,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"serviceOption\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"serviceOption cannot be empty\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Organization ID is not passed.",
                  name =
                      "A 400 error code indicates that the orgId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"timestamp\": 1670588798304,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"null\",\n"
                          + "                \"errorMessage\": \"orgId must not be blank\"\n"
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
public @interface AddNodeCarrierSelectionPriorityDoc {}
