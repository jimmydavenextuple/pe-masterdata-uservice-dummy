package com.nextuple.node.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.node.domain.NodeConstants;
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
@Operation(summary = "Create Node", description = NodeConstants.CREATE_NODE_DESC)
@ApiResponse(responseCode = "200", description = NodeConstants.CREATE_NODE_DETAILS_SUCCESS)
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Organization ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Node ID is not passed.</li>"
            + "<li><b>Error code: 2</b>: Street is not passed.</li>"
            + "<li><b>Error code: 2</b>: Country is not passed.</li>"
            + "<li><b>Error code: 2</b>: State is not passed.</li>"
            + "<li><b>Error code: 2</b>: Timezone is not passed.</li>"
            + "<li><b>Error code: 2</b>: Zipcode is not passed.</li>"
            + "<li><b>Error code: 2</b>: Latitude is not passed.</li>"
            + "<li><b>Error code: 2</b>: Longitude is not passed.</li>"
            + "<li><b>Error code: 2</b>: Node type is not passed.</li>"
            + "<li><b>Error code: 2</b>: StartWorkingTime passed but LastWorkingTime not passed.</li>"
            + "<li><b>Error code: 2</b>: StartWorkingTime passed in invalid format.</li>"
            + "<li><b>Error code: 2</b>: LastWorkingTime not greater than StartWorkingTime</li>"
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
                                            "nodeId": {
                                            "rejectedValue": "",
                                            "errorMessage": "orgId can't be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "nodeId not passed.",
                  name =
                      "A 400 error code indicates that the nodeId is not passed and it is a mandatory field.",
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
                                              "errorMessage": "nodeId must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "street not passed.",
                  name =
                      "A 400 error code indicates that the street is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "a9d5a8af-acbb-4b3b-aa02-6abcd6977c55",
                          "timestamp": 1679574054233,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "street": {
                                            "rejectedValue": "",
                                            "errorMessage": "street can't be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "Country not passed.",
                  name =
                      "A 400 error code indicates that the country is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                            "rejectedValue": "",
                                            "errorMessage": "country must not be blank"
                                       }
                                     }
                                }
                          }"""),
              @ExampleObject(
                  summary = "State not passed.",
                  name =
                      "A 400 error code indicates that the state is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                            "rejectedValue": "",
                                            "errorMessage": "state must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "Timezone not passed.",
                  name =
                      "A 400 error code indicates that the timezone is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                          "rejectedValue": "",
                                          "errorMessage": "timezone must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "Latitude not passed.",
                  name =
                      "A 400 error code indicates that the latitude is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                          "rejectedValue": "",
                                          "errorMessage": "country must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "Longitude not passed.",
                  name =
                      "A 400 error code indicates that the longitude is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                          "rejectedValue": "",
                                          "errorMessage": "country must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary = "nodeType not passed.",
                  name =
                      "A 400 error code indicates that the nodeType is not passed and it is a mandatory field.",
                  value =
                      """
                          {
                          "success": false,
                          "requestId": "1f7010bd-3d17-4eba-b5bd-b29446415eb2#605",
                          "timestamp": 1679538685503,
                          "message": "Bad Request",
                          "payload": {
                          "type": "ERROR",
                          "code": 2,
                          "fields": {
                                            "orderType": {
                                          "rejectedValue": "",
                                          "errorMessage": "nodeType must not be blank"
                                      }
                                  }
                              }
                          }"""),
              @ExampleObject(
                  summary =
                      "StartWorkingTime passed but not LastWorkingTime or when LastWorkingTime passed not StartWorkingTime.",
                  name =
                      "A 400 error code indicates that either startWorkingTime or lastWorkingTime field was not passed.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "7b064ccf-221f-45e3-ba40-d5c3d32b19ba",
                                                "timestamp": 1714649604124,
                                                "message": "Either both startWorkingTime and lastWorkingTime should be present or neither should be present.",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "lastWorkingTime": {},
                                                        "startWorkingTime": {
                                                            "rejectedValue": "09:00"
                                                        }
                                                    }
                                                }
                                            }"""),
              @ExampleObject(
                  summary =
                      "StartWorkingTime or LastWorkingTime not passed in the correct format (HH:MM).",
                  name =
                      "A 400 error code indicates that the StartWorkingTime or LastWorkingTime was not passed in the correct format (HH:MM).",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "42c71d29-16e2-42cf-aa96-afab293f787d",
                                                "timestamp": 1714650353489,
                                                "message": "startWorkingTime is invalid.",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "startWorkingTime": {
                                                            "rejectedValue": "1900"
                                                        }
                                                    }
                                                }
                                            }"""),
              @ExampleObject(
                  summary = "LastWorkingTime is not greater than StartWorkingTime.",
                  name =
                      "A 400 error code indicates that the LastWorkingTime is not greater than StartWorkingTime.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "bfa20f3e-3ad9-4ab4-bef1-ad893af65cad",
                                                "timestamp": 1714650576688,
                                                "message": "lastWorkingTime should be equal or greater than startWorkingTime.",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 6001,
                                                    "fields": {
                                                        "lastWorkingTime": {
                                                            "rejectedValue": "16:00"
                                                        },
                                                        "startWorkingTime": {
                                                            "rejectedValue": "19:00"
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
public @interface CreateNodeDoc {}
