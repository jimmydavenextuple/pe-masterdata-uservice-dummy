package com.nextuple.item.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.outbound.ItemResponse;
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
@Operation(summary = "Add Item", description = ItemConstants.ADD_ITEM_DESC)
@ApiResponse(
    responseCode = "200",
    description = ItemConstants.GET_ITEM_LIST_SUCCESS,
    content =
        @Content(
            schema = @Schema(implementation = ItemResponse.class),
            examples = {
              @ExampleObject(
                  summary = ItemConstants.GET_ITEM_LIST_SUCCESS,
                  name = ItemConstants.GET_ITEM_LIST_DESC,
                  value =
                      """
                                                  {
                                                                       [
                                                      {
                                                         "itemId": "item141017232-002",
                                                         "itemSource": "item-source",
                                                         "orgId": "NEXTUPLE_GR",
                                                         "uom": "in",
                                                         "shortDescription": "string",
                                                         "serviceOptions": "EXPRESS,SDND,NEXTDAY",
                                                         "processingTime": 0.0,
                                                         "leadTime": null,
                                                         "handlingType": null,
                                                         "itemSubstitutionResponse":{
                                                            "itemId": "item141017232-003",
                                                            "uom": "EACH",
                                                            "priority":"1",
                                                            "conversionFactor": 1.0,
                                                         }
                                                      }
                                                   ]
                                                  }
                                                  """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Item List is not passed.</li>"
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
                      """
        {
            "success": false,
            "requestId": "571473d2-d27f-4f0b-903c-91149805876e",
            "timestamp": 1679648554379,
            "message": "Bad Request",
            "payload": {
                "type": "ERROR",
                "code": 2,
                "fields": {
                    "orgId": {
                        "rejectedValue": "",
                        "errorMessage": "orgId can't be blank"
                    }
                }
            }
        }
        """),
              @ExampleObject(
                  summary = "ItemList is not passed.",
                  name =
                      "A 400 error code indicates that the ItemList is not passed and it is a mandatory field.",
                  value =
                      """
        {
            "success": false,
            "requestId": "571473d2-d27f-4f0b-903c-91149805876e",
            "timestamp": 1679648554379,
            "message": "Bad Request",
            "payload": {
                "type": "ERROR",
                "code": 2,
                "fields": {
                    "orgId": {
                        "rejectedValue": "",
                        "errorMessage": "itemList can't be empty"
                    }
                }
            }
        }
        """)
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
                      """
        {
            "success": false,
            "timestamp": "1670589273234",
            "payload": {
                "type": "ERROR",
                "code": 2
            }
        }
        """)
            }))
public @interface GetItemDetailDoc {}
