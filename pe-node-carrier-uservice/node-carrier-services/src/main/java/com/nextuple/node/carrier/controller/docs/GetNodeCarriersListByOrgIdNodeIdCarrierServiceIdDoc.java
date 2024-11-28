package com.nextuple.node.carrier.controller.docs;

import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
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
    summary = "Get Node Carriers List by org ID, node ID and Carrier Service ID",
    description =
        "Retrieves node carriers based on the provided orgId and nodeId and carrierServiceId.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the node carriers list for the combination of orgId, nodeId and carrierServiceId is fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carriers List Fetched Successfully",
                  name = "Node Carriers List",
                  value =
                      """
                                                {
                                                    "success": true,
                                                    "requestId": "c0bac494-982b-413e-9fbc-9409e1258a73",
                                                    "timestamp": 1732601425174,
                                                    "message": "Node Carrier details fetched successfully",
                                                    "payload": {
                                                        "nodeId": "10",
                                                        "orgId": "NEXTUPLE_GR",
                                                        "carrierServiceId": "DHL Express",
                                                        "serviceOption": "EXPRESS",
                                                        "lastPickupTime": "17:00"
                                                    }
                                                }
                                                        """)
            }))
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 exception code indicates that the Node carriers list not found with given orgId and nodeId and carrierServiceId.",
    content =
        @Content(
            schema = @Schema(implementation = NodeCarriersResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Node Carriers List not found for given key",
                  name = "Node Carriers List not found for given key",
                  value =
                      """
                                                {
                                                    "success": false,
                                                    "requestId": "9168b754-7db0-4f09-ae37-617accc2966d",
                                                    "timestamp": 1732601470754,
                                                    "message": "Node Carrier not found for given details",
                                                    "payload": {
                                                        "type": "ERROR",
                                                        "code": 6017,
                                                        "fields": {
                                                            "serviceOption": {
                                                                "rejectedValue": "EXPRESS"
                                                            },
                                                            "carrierServiceId": {
                                                                "rejectedValue": "DHL Express"
                                                            },
                                                            "nodeId": {
                                                                "rejectedValue": "1"
                                                            },
                                                            "orgId": {
                                                                "rejectedValue": "NEXTUPLE_GR"
                                                            }
                                                        }
                                                    }
                                                }
                                                                          """)
            }))
public @interface GetNodeCarriersListByOrgIdNodeIdCarrierServiceIdDoc {}
