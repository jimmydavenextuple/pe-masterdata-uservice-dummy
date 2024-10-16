package com.nextuple.pe.masterdata.calendar.controller.docs;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nimbusds.oauth2.sdk.ErrorResponse;
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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Upcoming Calendar Days Status",
    description = "Retrieves the list of the status of the upcoming days in a calendar.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the list of the status of the upcoming days in a calendar is retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = CalendarDaysStatusInfo.class),
            examples = {
              @ExampleObject(
                  summary = "When upcoming days calendar status is fetched successfully",
                  name =
                      "A 200 success code indicates that the list of the status of the upcoming days in a calendar is retrieved successfully.",
                  value =
                      """
                        {
                            "success": true,
                            "requestId": "730c4369-dfef-4813-8cd4-dbb369e7d90d",
                            "timestamp": 1714024112333,
                            "message": "Calendar status for upcoming days fetched successfully!",
                            "payload": [
                                {
                                    "date": "2024-12-31",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-01",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-02",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-03",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-04",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-05",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-06",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-07",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-08",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-09",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-10",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-11",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-12",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-13",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-14",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-15",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-16",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-17",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-18",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-19",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-20",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-21",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-22",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-23",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-24",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-25",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-26",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-27",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-28",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-29",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-30",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-01-31",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-01",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-02",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-03",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-04",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-05",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-06",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-07",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-08",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-09",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-10",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-11",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-12",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-13",
                                    "isActive": true
                                },
                                {
                                    "date": "2025-02-14",
                                    "isActive": true
                                }
                            ]
                        }
                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the list of the status of upcoming days in a calendar has not been retrieved."
            + "<ul>"
            + "<li><b>Error code: 16777201</b>: Calender doesn't exist.</li>"
            + "<li><b>Error code: 16777201</b>: No active calendar associated with the node.</li>"
            + "<li><b>Error code: 16777202</b>: No active calendar associated with the carrier & service.</li>"
            + "<li><b>Error code: 16777203/b>: No active calendar associated with the node, carrier & service.</li>"
            + "<li><b>Error code: 16777204/b>: Invalid fromDate or fromDate format.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Calender doesn't exist",
                  name = "A 400 error code indicates that Calender doesn't exist",
                  value =
                      """
                                    {
                                        "success": false,
                                        "requestId": "d2531654-b346-4e91-a50f-b8bfa9351aa7",
                                        "timestamp": 1714024765481,
                                        "message": "Calender doesn't exist",
                                        "payload": {
                                            "type": "ERROR",
                                            "code": 16777201,
                                            "fields": {
                                                "carrierServiceId": {
                                                    "rejectedValue": "ALL-EXPRES"
                                                },
                                                "orgId": {
                                                    "rejectedValue": "XYZINC"
                                                }
                                            }
                                        }
                                    }
                            """),
              @ExampleObject(
                  summary = "No active calendar associated to the node.",
                  name =
                      "A 400 error code indicates that no active calendar associated to the node.",
                  value =
                      """
                                  {
                                      "success": false,
                                      "requestId": "b6e1f460-d4da-47ba-b5ee-c269b38eb51d",
                                      "timestamp": 1714024456360,
                                      "message": "No active calendar associated to the node",
                                      "payload": {
                                          "type": "ERROR",
                                          "code": 16777201,
                                          "fields": {
                                              "nodeId": {
                                                  "rejectedValue": "N105"
                                              },
                                              "orgId": {
                                                  "rejectedValue": "XYZINC"
                                              }
                                          }
                                      }
                                  }
                          """),
              @ExampleObject(
                  summary = "No active calendar associated to the carrier.",
                  name =
                      "A 400 error code indicates that no active calendar associated to the carrier.",
                  value =
                      """
                                   {
                                               "success": false,
                                               "requestId": "5c10a897-f12c-483c-aeb0-8a1e76d3ade2",
                                               "timestamp": 1714024531167,
                                               "message": "No active calendar associated to the carrier & service",
                                               "payload": {
                                                   "type": "ERROR",
                                                   "code": 16777202,
                                                   "fields": {
                                                       "carrierServiceId": {
                                                           "rejectedValue": "ALL-EXPRESS"
                                                       },
                                                       "orgId": {
                                                           "rejectedValue": "XYZINC"
                                                       }
                                                   }
                                               }
                                           }
                                    """),
              @ExampleObject(
                  summary = "No active calendar associated to the node, carrier & service.",
                  name =
                      "A 400 error code indicates that no active calendar associated to the node, carrier & service.",
                  value =
                      """
                                            {
                                                "success": false,
                                                "requestId": "701ec1bc-bcd8-43c6-b33b-eecc43a7bfc5",
                                                "timestamp": 1714024568695,
                                                "message": "No active calendar associated to the node, carrier & service",
                                                "payload": {
                                                    "type": "ERROR",
                                                    "code": 16777203,
                                                    "fields": {
                                                        "orgId": {
                                                            "rejectedValue": "XYZINC"
                                                        },
                                                        "carrierServiceId": {
                                                            "rejectedValue": "ALL-EXPRESS"
                                                        },
                                                        "nodeId": {
                                                            "rejectedValue": "N104"
                                                        }
                                                    }
                                                }
                                            }
                                    """),
              @ExampleObject(
                  summary = "Invalid fromDate or fromDate format.",
                  name = "A 400 error code indicates that invalid fromDate or fromDate format",
                  value =
                      """
                                   {
                                       "success": false,
                                       "requestId": "bb29f43d-b792-4e08-b3b7-70340e081428",
                                       "timestamp": 1714025021638,
                                       "message": "Invalid fromDate or fromDate format, expected yyyy-MM-dd format",
                                       "payload": {
                                           "type": "ERROR",
                                           "code": 16777203,
                                           "fields": {
                                               "fromDate": {
                                                   "rejectedValue": "2024-01-AB"
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
            schema =
                @Schema(implementation = com.nextuple.common.response.error.ErrorResponse.class),
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
public @interface HandleGetUpcomingDaysCalendarStatusDoc {}
