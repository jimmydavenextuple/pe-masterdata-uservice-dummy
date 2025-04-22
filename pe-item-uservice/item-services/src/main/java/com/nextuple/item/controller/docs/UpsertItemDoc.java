package com.nextuple.item.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
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
@Operation(
    summary = "Create or Update Item",
    description = "Creates a new item or updates the item if it already exists.")
@ApiResponse(
    responseCode = "200",
    description = "Item saved successfully",
    content =
        @Content(
            schema = @Schema(implementation = ItemResponse.class),
            examples =
                @ExampleObject(
                    name = "Success - Item Upserted",
                    summary = "Item saved successfully",
                    value =
                        """
                {
                    "success": true,
                    "requestId": "68c11700-ee1b-4b73-b0f8-64429f394cc6",
                    "timestamp": 1744810756928,
                    "message": "Item saved successfully",
                    "payload": {
                        "customAttributes": null,
                        "itemId": "item2261",
                        "itemSource": "item-source",
                        "orgId": "NEXTUPLE_GR",
                        "uom": "in",
                        "vendorType": "vendor-A",
                        "product": "0600fsfs70fsfsf1424423",
                        "color": "BLACK",
                        "size": "111",
                        "shipEligible": true,
                        "parcelShipmentEligible": true,
                        "pickEligible": true,
                        "isDSVEligible": null,
                        "serviceOptionEligibilities": {
                            "expressEligible": true,
                            "sdndEligible": true,
                            "nextdayEligible": true
                        },
                        "shipAlone": false,
                        "height": 0.0,
                        "width": 0.0,
                        "length": 0.0,
                        "volume": 3.6,
                        "dimensionUom": "each",
                        "volumeUom": "string",
                        "weight": 0.0,
                        "weightUom": "lbs",
                        "processingTime": 10.0,
                        "cost": "510",
                        "isHazmat": true,
                        "leadTime": 0,
                        "isWhiteGlove": true,
                        "inventoryNodeTypes": null,
                        "itemBanner": null,
                        "handlingType": "SAMPLE",
                        "buyingCost": null,
                        "validDropoffDuration": null
                    }
                }
                """)))
@ExampleObject(
    summary = "Processing Time Updated",
    name = "Update Response - processingTime changed",
    value =
        """
                {
                    "success": true,
                    "requestId": "63f4566f-e332-4e13-8ffb-437192a0e7ba",
                    "timestamp": 1744810832572,
                    "message": "Item saved successfully",
                    "payload": {
                        "customAttributes": null,
                        "itemId": "item2261",
                        "itemSource": "item-source",
                        "orgId": "NEXTUPLE_GR",
                        "uom": "in",
                        "vendorType": "vendor-A",
                        "product": "0600fsfs70fsfsf1424423",
                        "color": "BLACK",
                        "size": "111",
                        "shipEligible": true,
                        "parcelShipmentEligible": true,
                        "pickEligible": true,
                        "isDSVEligible": null,
                        "serviceOptionEligibilities": {
                            "expressEligible": true,
                            "sdndEligible": true,
                            "nextdayEligible": true
                        },
                        "shipAlone": false,
                        "height": 0.0,
                        "width": 0.0,
                        "length": 0.0,
                        "volume": 3.6,
                        "dimensionUom": "each",
                        "volumeUom": "string",
                        "weight": 0.0,
                        "weightUom": "lbs",
                        "processingTime": 100.0,
                        "cost": "510",
                        "isHazmat": true,
                        "leadTime": 0,
                        "isWhiteGlove": true,
                        "inventoryNodeTypes": null,
                        "itemBanner": null,
                        "handlingType": "SAMPLE",
                        "buyingCost": null,
                        "validDropoffDuration": null
                    }
                }
                """)
@ApiResponse(
    responseCode = "400",
    description = "Bad Request – Input validation failed",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples =
                @ExampleObject(
                    name = "Validation Error",
                    summary = "Required fields missing or invalid",
                    value =
                        """
                {
                    "success": false,
                    "requestId": "c6689e4b-6439-4f76-a4da-7357838e991b",
                    "timestamp": 1744810779557,
                    "message": "Bad Request",
                    "payload": {
                        "type": "ERROR",
                        "code": 2,
                        "fields": {
                            "serviceOptionEligibilities": {
                                "errorMessage": "serviceOptionEligibilities can't be null"
                            },
                            "shipEligible": {
                                "errorMessage": "shipEligible can't be null"
                            },
                            "isWhiteGlove": {
                                "errorMessage": "isWhiteGlove can't be null"
                            },
                            "pickEligible": {
                                "errorMessage": "pickEligible can't be null"
                            },
                            "parcelShipmentEligible": {
                                "errorMessage": "parcelShipmentEligible can't be null"
                            },
                            "handlingType": {
                                "errorMessage": "handlingType can't be blank"
                            }
                        }
                    }
                }
                """)))
@ApiResponse(
    responseCode = "500",
    description = "Unexpected server error",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples =
                @ExampleObject(
                    name = "Internal Server Error",
                    summary = "Something went wrong on the server",
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
                """)))
public @interface UpsertItemDoc {}
