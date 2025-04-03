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
@Operation(summary = "Update Vendor", description = VendorConstants.UPDATE_VENDOR_DESC)
@ApiResponse(responseCode = "200", description = VendorConstants.UPDATE_VENDOR_DETAILS_SUCCESS)
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Vendor ID is not passed.</li>"
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
                                            "requestId": "f8788a11-7b1d-440d-9a2c-fde9ae072a79",
                                            "timestamp": 1679573885650,
                                            "message": "Bad Request",
                                            "payload": {
                                                    "type": "ERROR",
                                                    "code": 2,
                                                    "fields": {
                                                        "vendorId": {
                                                            "rejectedValue": "",
                                                            "errorMessage": "orgId can't be blank"
                                                        }
                                                    }
                                                }
                                            }"""),
              @ExampleObject(
                  summary = "VendorId not passed.",
                  name =
                      "A 400 error code indicates that the vendorId is not passed and it is a mandatory field.",
                  value =
                      """
                                            {
                                            "success": false,
                                            "requestId": "166b6603-8a1c-426f-95a3-a6d7666c351a#624",
                                            "timestamp": 1679537974009,
                                            "message": "Bad Request",
                                            "payload": {
                                                    "type": "ERROR",
                                                    "code": 2,
                                                    "fields": {
                                                        "serviceOption": {
                                                            "rejectedValue": "",
                                                            "errorMessage": "vendorId must not be blank"
                                                        }
                                                    }
                                                }
                                            }""")
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
public @interface UpdateVendorDetailsDoc {}
