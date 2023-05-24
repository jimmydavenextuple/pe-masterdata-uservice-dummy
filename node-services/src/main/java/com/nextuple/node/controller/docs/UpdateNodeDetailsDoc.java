package com.nextuple.node.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.domain.NodeConstants;
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
@Operation(summary = "Update Node", description = NodeConstants.UPDATE_NODE_DESC)
@ApiResponse(responseCode = "200", description = NodeConstants.UPDATE_NODE_DETAILS_SUCCESS)
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Node ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Street is not passed.</li>"
            + "<li><b>Error code: 2</b>: Country is not passed.</li>"
            + "<li><b>Error code: 2</b>: Province is not passed.</li>"
            + "<li><b>Error code: 2</b>: Timezone is not passed.</li>"
            + "<li><b>Error code: 2</b>: Postal code is not passed.</li>"
            + "<li><b>Error code: 2</b>: Latitude is not passed.</li>"
            + "<li><b>Error code: 2</b>: Longitude is not passed.</li>"
            + "<li><b>Error code: 2</b>: Node type is not passed.</li>"
            + "<li><b>Error code: 6001</b>: Country passed is invalid.</li>"
            + "<li><b>Error code: 6001</b>: Timezone passed is invalid.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "OrgId is not passed.",
                  name =
                      "A 400 error code indicates that the orgId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"f8788a11-7b1d-440d-9a2c-fde9ae072a79\",\n"
                          + "    \"timestamp\": 1679573885650,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"nodeId\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"orgId can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "nodeId not passed.",
                  name =
                      "A 400 error code indicates that the nodeId is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"166b6603-8a1c-426f-95a3-a6d7666c351a#624\",\n"
                          + "    \"timestamp\": 1679537974009,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"serviceOption\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"nodeId must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "street not passed.",
                  name =
                      "A 400 error code indicates that the street is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"a9d5a8af-acbb-4b3b-aa02-6abcd6977c55\",\n"
                          + "    \"timestamp\": 1679574054233,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"street\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"street can't be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Country not passed.",
                  name =
                      "A 400 error code indicates that the country is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"country must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Province not passed.",
                  name =
                      "A 400 error code indicates that the province is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"province must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Timezone not passed.",
                  name =
                      "A 400 error code indicates that the timezone is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"timezone must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Latitude not passed.",
                  name =
                      "A 400 error code indicates that the latitude is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"country must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Longitude not passed.",
                  name =
                      "A 400 error code indicates that the longitude is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"country must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "nodeType not passed.",
                  name =
                      "A 400 error code indicates that the nodeType is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"1f7010bd-3d17-4eba-b5bd-b29446415eb2#605\",\n"
                          + "    \"timestamp\": 1679538685503,\n"
                          + "    \"message\": \"Bad Request\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2,\n"
                          + "        \"fields\": {\n"
                          + "            \"orderType\": {\n"
                          + "                \"rejectedValue\": \"\",\n"
                          + "                \"errorMessage\": \"nodeType must not be blank\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Invalid country passed.",
                  name =
                      "A 400 error code indicates that the nodeType is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"f3de36eb-f9c8-4abd-850f-b33478a7cc9e\",\n"
                          + "    \"timestamp\": 1679574975884,\n"
                          + "    \"message\": \"Invalid Country Found\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"country\": {\n"
                          + "                \"rejectedValue\": \"C\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}"),
              @ExampleObject(
                  summary = "Invalid timezone passed.",
                  name =
                      "A 400 error code indicates that the nodeType is not passed and it is a mandatory field.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"34e6189e-54f8-4ae2-a21c-2f5af9152bd3\",\n"
                          + "    \"timestamp\": 1679575052209,\n"
                          + "    \"message\": \"Invalid Timezone Found\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"timezone\": {\n"
                          + "                \"rejectedValue\": \"America/oronto\"\n"
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
public @interface UpdateNodeDetailsDoc {}
