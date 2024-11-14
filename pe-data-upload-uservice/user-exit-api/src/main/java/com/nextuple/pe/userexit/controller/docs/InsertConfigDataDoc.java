package com.nextuple.pe.userexit.controller.docs;

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
    summary = "Create Config Data",
    description = " Creates configuration data for the user exit.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 status code indicates that the configuration data for the user exit is created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the user exit implementation type value is incorrect.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User Exit implementation type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit implementation type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "0134ba6f-cc76-4af0-9d7a-abec98464bb1#16229",
                                                    "timestamp": 1705900857561,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "ueImplType": {
                                                                "rejectedValue": "ZPI",
                                                                "errorMessage": "values accepted for Enum class: [MOCK, REST]"
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
                  summary = "There was some error on server while processing the request",
                  name = "A 500 error code indicates that there the config data already exists.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "cff4f287-4248-47ac-8d62-cebcf663c7f5#9979",
                                                    "timestamp": 1705901783282,
                                                    "message": "Unable to add config data",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6006,
                                                        "fields": {
                                                            "ConfigData": {
                                                                "rejectedValue": {
                                                                    "createdDate": "2024-01-22T05:36:23.275+00:00",
                                                                    "lastModifiedDate": "2024-01-22T05:36:23.275+00:00",
                                                                    "createdBy": "NEXTUPLE_GR",
                                                                    "updatedBy": "NEXTUPLE_GR",
                                                                    "id": null,
                                                                    "userExitName": "GetItemDetails1",
                                                                    "appName": "PE",
                                                                    "orgId": "NEXTUPLE_GR",
                                                                    "serviceName": "ItemService",
                                                                    "url": "com.nextuple.promise.item.ItemMockProvider",
                                                                    "ueImplType": "MOCK",
                                                                    "attributeJsonPath": "serviceOption:$.serviceOption,orgId:$.orgId,uom:$.orderLines[0].item.unitOfMeasure,lineId:$.orderLines[0].lineId,sessionId:$.sessionId,pageName:$.pageName"
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
public @interface InsertConfigDataDoc {}
