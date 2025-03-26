package com.nextuple.dataupload.controller.docs;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
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
    summary = "Fetch Transfer Schedules List With Transfer Rules",
    description =
        "Retrieves transfer schedules based on organization ID , configuration type and transfer schedules parameters.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the transfer schedules are fetched successfully.",
    content =
        @Content(
            schema = @Schema(implementation = TransferScheduleResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Transfer schedules fetched successfully",
                  name =
                      "A 200 response code indicates that transfer schedules are fetched successfully.",
                  value =
                      """
                                                 {
                                                           "success": true,
                                                           "requestId": "85e6ba48-65b4-415f-b889-0f4d9be94ca6",
                                                           "timestamp": 1725006810795,
                                                           "message": "Transfer Schedule details fetched successfully",
                                                           "payload": {
                                                             "data": {
                                                               "columns": [
                                                                 {
                                                                   "columnName": "Rule Name",
                                                                   "columnMeta": "ruleName",
                                                                   "isSortable": false
                                                                 },
                                                                 {
                                                                   "columnName": "Origin Node",
                                                                   "columnMeta": "sourceNodeId",
                                                                   "isSortable": true
                                                                 },
                                                                 {
                                                                   "columnName": "Destination Node",
                                                                   "columnMeta": "dropoffNodeId",
                                                                   "isSortable": false
                                                                 },
                                                                 {
                                                                   "columnName": "PickUp",
                                                                   "columnMeta": "startTime",
                                                                   "isSortable": false
                                                                 },
                                                                 {
                                                                   "columnName": "DropOff",
                                                                   "columnMeta": "endTime",
                                                                   "isSortable": false
                                                                 },
                                                                 {
                                                                   "columnName": "Attr 1",
                                                                   "columnMeta": "attribute1",
                                                                   "isSortable": false
                                                                 },
                                                                 {
                                                                   "columnName": "Attr 2",
                                                                   "columnMeta": "attribute2",
                                                                   "isSortable": false
                                                                 }
                                                               ],
                                                               "rows": [
                                                                 {
                                                                   "attribute1": "value1",
                                                                   "attribute2": "value2",
                                                                   "dropoffNodeId": "CAPNode1",
                                                                   "sourceNodeId": "CAPNode2",
                                                                   "startTime": "2024-08-28T15:00:00.000Z",
                                                                   "endTime": "2024-08-30T15:00:00.000Z"
                                                                 },
                                                                 {
                                                                   "attribute1": "value1",
                                                                   "attribute2": "value2",
                                                                   "dropoffNodeId": "CAPNode1",
                                                                   "sourceNodeId": "NODE4",
                                                                   "startTime": "2024-08-30T12:00:00.000Z",
                                                                   "endTime": "2024-08-30T15:00:00.000Z"
                                                                 },
                                                                 {
                                                                   "attribute1": "value1",
                                                                   "attribute2": "value2",
                                                                   "dropoffNodeId": "CAPNode1",
                                                                   "sourceNodeId": "NODE4",
                                                                   "startTime": "2024-08-29T12:00:00.000Z",
                                                                   "endTime": "2024-08-29T15:00:00.000Z"
                                                                 }
                                                               ]
                                                             },
                                                             "pagination": {
                                                               "totalRecords": 3,
                                                               "currentPage": 1,
                                                               "pageSize": 10,
                                                               "totalPages": 1,
                                                               "sortBy": "sourceNodeId"
                                                             }
                                                           }
                                                         }
                                        """)
            }))
@ApiResponse(
    responseCode = "400",
    description = "A 400 error code indicates that there is some issue with the input.",
    content =
        @Content(
            schema = @Schema(implementation = com.nimbusds.oauth2.sdk.ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "'startDate' for method parameter is in incorrect format.",
                  name =
                      "A 400 error code indicates that the 'startTime' for method parameter type Local is in incorrect format.",
                  value =
                      """
                                                    {
                                                        "success": false,
                                                        "requestId": "95f6439f-0351-4ac8-b649-fd843576155c#17",
                                                        "timestamp": 1679545687292,
                                                        "message": "JSON parse error: Invalid format: "2025-03-21T" is malformed at T",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 2
                                                        }
                                                    }"""),
              @ExampleObject(
                  summary = "'startDate' for method parameter is in incorrect format.",
                  name =
                      "A 400 error code indicates that the 'endTime' for method parameter type Local is in incorrect format.",
                  value =
                      """
                                                    {
                                                        "success": false,
                                                        "requestId": "95f6439f-0351-4ac8-b649-fd843576155c#17",
                                                        "timestamp": 1679545687292,
                                                        "message": "JSON parse error: Invalid format: "2025-03-21T" is malformed at T",
                                                        "payload": {
                                                            "type": "ERROR",
                                                            "code": 2
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
                                                                                                  "requestId": "3b8b4852-93a6-4570-b685-1d2771ebce56",
                                                                                                  "timestamp": 1705579225540,
                                                                                                  "payload": {
                                                                                                      "type": "ERROR",
                                                                                                      "code": 2
                                                                                                  }
                                                                                              }
                                                                                              """)
            }))
public @interface GetTransferSchedulesListDocV2 {}
