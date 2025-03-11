/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller.docs;

import com.nextuple.item.domain.outbound.ItemListResponse;
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
import org.springframework.web.ErrorResponse;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation(
    summary = "Get Item List.",
    description =
        "Retrieves a list of Item details for given orgId and itemIds. The result of this api is paginated.")
@ApiResponse(
    responseCode = "200",
    description = "A 200 success code indicates that the item details are retrieved successfully.",
    content =
        @Content(
            schema = @Schema(implementation = ItemListResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Item details fetched successfully",
                  name = "A 200 success code indicates successful retrieval of item details",
                  value =
                      """
                                                                  {
                                                                      "success": true,
                                                                      "requestId": "2f933659-6b9d-4a7f-b73f-d8e322968c13",
                                                                      "timestamp": 1723113739582,
                                                                      "message": "Item Details fetched successfully",
                                                                      "payload": {
                                                                         "data": [
                                                                            {
                                                                               "itemId": "item141017232-002",
                                                                               "itemSource": "HBC",
                                                                               "orgId": "NEXTUPLE_GR",
                                                                               "uom": "in",
                                                                               "shortDescription": "string",
                                                                               "serviceOptions": "EXPRESS,SDND,NEXTDAY",
                                                                               "processingTime": 0.0,
                                                                               "leadTime": null,
                                                                               "handlingType": null,
                                                                               "customAttributes": {
                                                                                    "owner": {
                                                                                        "primary": "CustomAttribute"                                                                                 \s
                                                                                        }
                                                                            }
                                                                            }
                                                                         ],
                                                                         "pagination": {
                                                                            "next": "/NEXTUPLE_GR/itemList?itemIds=JKTEST,item301555971,item141017232-002&pageNo=2&pageSize=1",
                                                                            "previous": "",
                                                                            "totalPages": 2,
                                                                            "currentPage": 1,
                                                                            "totalRecords": 2,
                                                                            "sortOrder": "ASC",
                                                                            "sortBy": "itemId"
                                                                         },
                                                                         "aggregation": null
                                                                      }
                                                                   }
                                                                                                                    """),
              @ExampleObject(
                  summary = "ItemId(s) and orgID combination does not exist",
                  name = "A 200 success code indicates successful retrieval of item details",
                  value =
                      """
                                                                  {
                                                                     "success": true,
                                                                     "requestId": "11e1357f-00c7-49f7-89ed-aff8fb7a0c98",
                                                                     "timestamp": 1723119604639,
                                                                     "message": "Item Details fetched successfully",
                                                                     "payload": {
                                                                        "data": [],
                                                                        "pagination": {
                                                                           "next": "",
                                                                           "previous": "",
                                                                           "totalPages": 0,
                                                                           "currentPage": 1,
                                                                           "totalRecords": 0,
                                                                           "sortOrder": "ASC",
                                                                           "sortBy": "itemId"
                                                                        },
                                                                        "aggregation": null
                                                                     }
                                                                  }
                                                                                                                    """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that the request to get items buffers list is not valid.")
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "There was some error on server while processing the request",
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
@ApiResponse(
    responseCode = "403",
    description = "A 403 code indicates that the client is forbidden to send the request.",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary = "Organization ID mismatched",
                  name =
                      "A 403 error code indicates that wrong organization ID is passed for given tenant.",
                  value =
                      """
                                                                                              {
                                                                                                "success": false,
                                                                                                "requestId": "4b608a08-e86a-448b-bea1-3bfbbda083ee#13098",
                                                                                                "timestamp": 1710323530881,
                                                                                                "message": "OrgId mismatch!",
                                                                                                "payload": {
                                                                                                  "type": "ERROR",
                                                                                                  "code": 1011
                                                                                                }
                                                                                              }
                                                                                              """)
            }))
public @interface GetItemListDoc {}
