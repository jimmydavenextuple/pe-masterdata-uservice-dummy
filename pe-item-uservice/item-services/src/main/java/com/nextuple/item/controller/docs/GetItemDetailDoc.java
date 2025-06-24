package com.nextuple.item.controller.docs;

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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(summary = "Add Item", description = ItemConstants.ADD_ITEM_DESC)
@ApiResponse(
        responseCode = "200",
        description = ItemConstants.ADD_ITEM_SUCCESS,
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
                                                                           },
                                                                           "itemSubstitutionResponse": [
                                                                                     {
                                                                                         "itemId": "ITEM003",
                                                                                         "uom": "PALLET",
                                                                                         "conversionFactor": 30,
                                                                                         "priority": 1
                                                                                     }
                                                                                 ]
                                                                       }
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
                                        "{\n"
                                                + "    \"success\": false,\n"
                                                + "    \"requestId\": \"571473d2-d27f-4f0b-903c-91149805876e\",\n"
                                                + "    \"timestamp\": 1679648554379,\n"
                                                + "    \"message\": \"Bad Request\",\n"
                                                + "    \"payload\": {\n"
                                                + "        \"type\": \"ERROR\",\n"
                                                + "        \"code\": 2,\n"
                                                + "        \"fields\": {\n"
                                                + "            \"orgId\": {\n"
                                                + "                \"rejectedValue\": \"\",\n"
                                                + "                \"errorMessage\": \"orgId can't be blank\"\n"
                                                + "            }\n"
                                                + "        }\n"
                                                + "    }\n"
                                                + "}"),
                        @ExampleObject(
                                summary = "ItemList is not passed.",
                                name =
                                        "A 400 error code indicates that the ItemList is not passed and it is a mandatory field.",
                                value =
                                        "{\n"
                                                + "    \"success\": false,\n"
                                                + "    \"requestId\": \"571473d2-d27f-4f0b-903c-91149805876e\",\n"
                                                + "    \"timestamp\": 1679648554379,\n"
                                                + "    \"message\": \"Bad Request\",\n"
                                                + "    \"payload\": {\n"
                                                + "        \"type\": \"ERROR\",\n"
                                                + "        \"code\": 2,\n"
                                                + "        \"fields\": {\n"
                                                + "            \"orgId\": {\n"
                                                + "                \"rejectedValue\": \"\",\n"
                                                + "                \"errorMessage\": \"itemList can't be empty\"\n"
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
public @interface GetItemDetailDoc {}

