package com.nextuple.transit.controller.docs;

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
import java.util.List;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Zone Details List For Destination Geo Zone",
    description =
        "Retrieves the zone information for the given organization ID and destination geo zone.")
@ApiResponse(
    responseCode = "200",
    description =
        "A 200 success code indicates that the zone information is retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = List.class),
            examples = {
              @ExampleObject(
                  summary = "When zone information is retrieved successfully.",
                  name =
                      "A 200 success code indicates that the zone information is retrieved successfully.",
                  value =
                      """
                                                {
                                                   "success": true,
                                                   "requestId": "1dd9552c-4b63-42d4-9017-dced8394b6a7#494",
                                                   "timestamp": "2022-07-26T05:23:29.018+00:00",
                                                   "message": "Zones list fetched successfully",
                                                   "payload": [
                                                     {
                                                       "orgId": "NEXTUPLE_GR",
                                                       "sourceGeoZone": "M1T",
                                                       "destinationGeozone": "AIJ",
                                                       "carrierServiceId": "Fedex-Air",
                                                       "zone": "Zone1",
                                                       "customAttributes": {
                                                          "Key": "value"
                                                       }
                                                     },
                                                     {
                                                       "orgId": "NEXTUPLE_GR",
                                                       "sourceGeoZone": "T2P",
                                                       "destinationGeozone": "AIJ",
                                                       "carrierServiceId": "UPS-Ground",
                                                       "zone": "Zone2",
                                                       "customAttributes": {
                                                          "Key": "value"
                                                       }
                                                     }
                                                   ]
                                                 }


                                                     """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 response code indicates that there is some issue with the input."
            + "<ul>"
            + "<li><b>Error code: 6001</b>: Zone data not found for the given details.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Indicates that the zone data not found with given details.",
                  name =
                      "A 400 error code indicates that the zone data not found with given details.",
                  value =
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"requestId\": \"05a17df4-04a9-425a-82dc-2b30d5da093d#273\",\n"
                          + "    \"timestamp\": 1679990361054,\n"
                          + "    \"message\": \"Zone data not found with given details\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 6001,\n"
                          + "        \"fields\": {\n"
                          + "            \"destinationGeozone\": {\n"
                          + "                \"rejectedValue\": \"T2\"\n"
                          + "            },\n"
                          + "            \"orgId\": {\n"
                          + "                \"rejectedValue\": \"BAY\"\n"
                          + "            }\n"
                          + "        }\n"
                          + "    }\n"
                          + "}")
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
                      "{\n"
                          + "    \"success\": false,\n"
                          + "    \"timestamp\": \"1670589273234\",\n"
                          + "    \"payload\": {\n"
                          + "        \"type\": \"ERROR\",\n"
                          + "        \"code\": 2\n"
                          + "    }\n"
                          + "}")
            }))
public @interface GetZoneDetailsListForDestinationGeoZoneDoc {}
