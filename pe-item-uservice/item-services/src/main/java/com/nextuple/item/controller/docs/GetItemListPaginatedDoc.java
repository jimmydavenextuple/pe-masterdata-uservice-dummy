package com.nextuple.item.controller.docs;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.outbound.ItemListResponse;
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
@Operation(summary = "Get Item Details List", description = ItemConstants.GET_ITEM_LIST_DESC)
@ApiResponse(
    responseCode = "200",
    description = ItemConstants.GET_ITEM_LIST_PAGINATED_SUCCESS,
    content =
        @Content(
            schema = @Schema(implementation = ItemListResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Retrieves paginated list of items, based on itemId(s) and organization ID provided.",
                  name =
                      "A 200 OK indicates that the paginated list of item details fetched successfully.",
                  value =
                      """
                                             {
                                                "success": true,
                                                "requestId": "3bbea396-035d-45c3-9b44-c1e470b9138f",
                                                "timestamp": 1723095780074,
                                                "message": "Item List fetched successfully",
                                                "payload": {
                                                   "data": [
                                                      {
                                                         "itemId": "item141017232-002",
                                                         "itemSource": "item-source",
                                                         "orgId": "NEXTUPLE_GR",
                                                         "uom": "in",
                                                         "shortDescription": "string",
                                                         "serviceOptions": "EXPRESS,SDND,NEXTDAY",
                                                         "processingTime": 0.0,
                                                         "leadTime": null,
                                                         "handlingType": null
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
                                             """)
            }))
@ApiResponse(
    responseCode = "400",
    description =
        "A 400 error code indicates that there is an issue with the input."
            + "<ul>"
            + "<li><b>Error code: 2</b>: Required request parameter “itemIds” for the method parameter type “List” is not present.</li>"
            + "</ul>",
    content =
        @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
              @ExampleObject(
                  summary =
                      "Required request parameter 'itemIds' for method parameter type List is not present.",
                  name =
                      "A 400 error indicates that the required request parameter 'itemIds' for method parameter type List is not present.",
                  value =
                      """
                                                {
                                                   "success": false,
                                                   "requestId": "82486367-9c1c-467a-b2a3-7d2ab368ec77",
                                                   "timestamp": 1723095675073,
                                                   "message": "Bad Request",
                                                   "payload": {
                                                      "type": "ERROR",
                                                      "code": 2,
                                                      "fields": {
                                                         "itemIds": {
                                                            "rejectedValue": "",
                                                            "errorMessage": "itemIds cannot be blank"
                                                         }
                                                      }
                                                   }
                                                }
                                                """)
            }))
@ApiResponse(
    responseCode = "500",
    description = "A 500 error code indicates that something went wrong.")
public @interface GetItemListPaginatedDoc {}
