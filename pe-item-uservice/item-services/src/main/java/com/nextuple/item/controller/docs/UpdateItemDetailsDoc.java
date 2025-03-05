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
@Operation(summary = "Update Item Details", description = ItemConstants.UPDATE_ITEM_DESC)
@ApiResponse(
    responseCode = "200",
    description = ItemConstants.UPDATE_ITEM_SUCCESS,
    content =
        @Content(
            schema = @Schema(implementation = ItemResponse.class),
            examples = {
              @ExampleObject(
                  summary = ItemConstants.UPDATE_ITEM_SUCCESS,
                  name = ItemConstants.UPDATE_ITEM_DESC,
                  value =
                      """
                                {
                                                     "success": true,
                                                     "requestId": "c7d82dbf-1f5b-47c1-8d83-8c346b9ea89a",
                                                     "timestamp": 1701767128015,
                                                     "message": "Item successfully created",
                                                     "payload": {
                                                          "itemId": 246,
                                                          "itemSource": "DSV-ITEM"
                                                          "orgId": "NEXTUPLE_GR",
                                                          "uom": "EACH",
                                                          "vendorType": "DSV",
                                                          "product": "PRODUCT-01",
                                                          "color": "RED",
                                                          "size": "10",
                                                          "shipEligible": true,
                                                          "parcelShipmentEligible": true,
                                                          "pickEligible": true,
                                                          "isDSVEligible: true,
                                                          "serviceOptionEligibilities": {
                                                              "sdndEligible": true
                                                          },
                                                         "height": 10.0,
                                                         "width": 5.0,
                                                         "length": 6.0,
                                                         "volume": 3.6,
                                                         "dimensionUom": "in",
                                                         "volumeUom": "litre",
                                                         "weight": 10.0,
                                                         "weightUom": "lbs",
                                                         "processingTime": 1.0,
                                                         "cost": "50",
                                                         "isHazmat": true,
                                                         "leadTime": 0,
                                                         "isWhiteGlove": true,
                                                         "inventoryNodeTypes": {
                                                            "S": ["MFC"]
                                                         }
                                                         "itemBanner": "Men's Shirt",
                                                         "handlingType": "Conveyable",
                                                         "activeItemBuffer": {
                                                            "bufferHours": 1.0,
                                                            "bufferStartDate": "2024-06-14T01:00:00.000+00:00",
                                                            "bufferEndDate":"2025-06-13T21:59:00.000+00:00"
                                                         },
                                                         "buyingCost": 20,
                                                         "customAttributes": {
                                                            "dynamicAtrr1": true
                                                         }
                                                     }
                                }
                                """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to update the item details is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: The item is not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item not found with given details.",
                  name =
                      "A 404 error code indicates that the item is not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"35acc109-c119-4f06-99fe-8ac3c81a918e#1647\",\n"
                          + "    \"timestamp\": 1679648989960,\n"
                          + "    \"message\": \"Item not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"itemId\": {\n"
                          + "                \"rejectedValue\": \"12345678\"\n"
                          + "            },\n"
                          + "            \"uom\": {\n"
                          + "                \"rejectedValue\": \"in\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"Bay\"\n"
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
public @interface UpdateItemDetailsDoc {}
