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
@Operation(summary = "Create Metadata", description = "Creates the metadata for user exit.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the metadata for the user exit is created successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to create the user exit metadata is invalid.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "User Exit type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "9bd1add5-cc65-49e3-964a-81eea7eabf8c",
                                                    "timestamp": 1705901474920,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "type": {
                                                                "rejectedValue": "zczc",
                                                                "errorMessage": "values accepted for Enum class: [API, REGULAR]"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
              @ExampleObject(
                  summary = "User Exit execution failure type is invalid",
                  name =
                      "A 400 error code indicates that the value for user exit execution failure type is not correct.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "3b9c3da4-7996-4d88-9c7f-30a6ab3d019d",
                                                    "timestamp": 1705901657739,
                                                    "message": "Bad Request",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 2,
                                                        "fields": {
                                                            "executionFailureType": {
                                                                "rejectedValue": "sdf",
                                                                "errorMessage": "values accepted for Enum class: [SOFT, HARD]"
                                                            }
                                                        }
                                                    }
                                                }
                                                """),
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
                  name = "A 500 error code indicates that the metadata already exists.",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "c55ab4db-8c5c-49fd-9962-b4087a41e970",
                                                    "timestamp": 1705901725232,
                                                    "message": "Unable to add meta data",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6006,
                                                        "fields": {
                                                            "MetaData": {
                                                                "rejectedValue": {
                                                                    "createdDate": "2024-01-22T05:35:24.386+00:00",
                                                                    "lastModifiedDate": "2024-01-22T05:35:24.386+00:00",
                                                                    "createdBy": "NEXTUPLE_GR",
                                                                    "updatedBy": "NEXTUPLE_GR",
                                                                    "id": null,
                                                                    "name": "GetSourcingRules_NXT_ST3",
                                                                    "appName": "PE_NXT_ST2",
                                                                    "serviceName": "SourcingService_NXT_ST2",
                                                                    "description": "Changing data priority",
                                                                    "executionFailureType": "SOFT",
                                                                    "type": "API",
                                                                    "preUEName": null,
                                                                    "postUEName": null
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                """)
            }))
public @interface InsertMetadataDoc {}
