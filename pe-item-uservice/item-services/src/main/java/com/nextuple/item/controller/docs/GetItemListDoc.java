package com.nextuple.item.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.item.domain.constants.ItemConstants;
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
@Operation(summary = "Get Item Details List", description = ItemConstants.GET_ITEM_LIST_DESC)
@ApiResponse(responseCode = "200", description = ItemConstants.GET_ITEM_LIST_SUCCESS)
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 4</b>: Required request parameter “itemList” for the method parameter type “List” is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Required request parameter 'itemList' for method parameter type List is not present.",
                  name =
                      "A 400 error indicates that the required request parameter 'itemList' for method parameter type List is not present.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"c73307e7-0e7a-4fe7-bd68-d376c2f847c6\",\n"
                          + "    \"timestamp\": 1679650106695,\n"
                          + "    \"message\": \"Required request parameter 'itemList' for method parameter type List is not present\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 4\n"
                          + "    }\n"
                          + "}")
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6002</b>: The items are not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item not found with given details.",
                  name =
                      "A 404 error code indicates that the items are not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"b3a76fd9-5948-4a10-a632-6652eb678866\",\n"
                          + "    \"timestamp\": 1679650051297,\n"
                          + "    \"message\": \"Items not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6002,\n"
                          + "        \"fields\": {\n"
                          + "            \"itemList\": {\n"
                          + "                \"rejectedValue\": [\n"
                          + "                    \"1\"\n"
                          + "                ]\n"
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
public @interface GetItemListDoc {}
