package com.nextuple.node.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.domain.NodeConstants;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
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
@Operation(summary = "Get Nodes Types", description = NodeConstants.GET_NODE_TYPES_DESC)
@ApiResponse(
    responseCode = "200",
    description = NodeConstants.GET_NODE_TYPES_SUCCESS,
    content =
        @Content(
            schema = @Schema(implementation = NodeTypesResponse.class),
            examples = {
              @ExampleObject(
                  summary = "All unique node types fetched successfully",
                  name =
                      "A 200 success code indicates that the node types are retrieved successfully.",
                  value =
                      """
                                        {
                                           "success": true,
                                           "requestId": "7bd9efda-5786-4bd2-94eb-b6221ce3b0ed",
                                           "timestamp": 1723445222448,
                                           "message": "Node types fetched successfully.",
                                           "payload": {
                                              "nodeTypes": [
                                                 "DC",
                                                 "FC"
                                              ]
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
                                            }""")
            }))
public @interface GetNodeTypesDoc {}
