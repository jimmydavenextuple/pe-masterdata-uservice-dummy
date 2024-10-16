package com.nextuple.promise.sourcing.rule.controller.docs;

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
    summary = "Delete Nodes from Node Group",
    description = "Deletes all the nodes from the nodegroup.")
@ApiResponse(
    responseCode = "200",
    description =
        "The 200 success code indicates that the nodes associated with a node group are deleted successfully.")
@ApiResponse(
    responseCode = "400",
    description =
        "The 400 error code indicates that the request to retrieve node groups for the organization ID is invalid.",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that the node group was not found for given node group ID.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "When sourcing attribute definition is not found with given ID.",
                  name =
                      "A 404 status code indicates that sourcing attributes definition by ID is not found.",
                  value =
                      """
                                              {
                                                  "success": false,
                                                  "requestId": "986c8a3b-d6cd-48a5-b546-c10a6a574a1f",
                                                  "timestamp": 1723411183994,
                                                  "message": "Node Group not found",
                                                  "payload": {
                                                      "type": "ERROR",
                                                      "code": 6001,
                                                      "fields": {
                                                          "id": {
                                                              "rejectedValue": 2890
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
                  summary = "Internal Server Error.",
                  name = "A 500 error code indicates that something went wrong.",
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
public @interface DeleteAllNodesFromNodeGroupDoc {}
