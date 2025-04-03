package com.nextuple.vendor.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.vendor.domain.VendorConstants;
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
@Operation(summary = "Delete Vendor", description = VendorConstants.DELETE_VENDOR_DESC)
@ApiResponse(responseCode = "200", description = VendorConstants.DELETE_VENDOR_SUCCESS)
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to delete vendor details is not valid.")
@ApiResponse(
    responseCode = "404",
    description =
        "A 404 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Vendor not found with the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Vendor not found with the given details.",
                  name =
                      "A 404 error code indicates that vendor is not found with the given details.",
                  value =
                      """
    {
        "success": false,
        "requestId": "b46b2e8e-128a-4424-9db5-5615c7f2788d",
        "timestamp": 1679576978020,
        "message": "Vendor not found with given details",
        "payload": {
            "type": "ERROR",
            "code": 6001,
            "fields": {
                "nodeId": {
                    "rejectedValue": "123"
                },
                "orgId": {
                    "rejectedValue": "BAY"
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
public @interface DeleteVendorDoc {}
