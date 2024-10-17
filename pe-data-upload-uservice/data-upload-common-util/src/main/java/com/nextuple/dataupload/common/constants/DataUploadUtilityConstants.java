/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.constants;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtilityConstants {

  public static final String CREATE = "CREATE";
  public static final String UPDATE = "UPDATE";
  public static final String DELETE = "DELETE";
  public static final String ACTION = "action";
  public static final String ORG_ID = "orgId";

  public static final String DATA_UPLOAD_PAGINATION_URL_CONSTANT =
      "/promising-engine/pe-data-upload";
  public static final String DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT + "/ui";
  public static final String EDD = "edd";
  public static final String END_ESTIMATED_DELIVERY_DATE = "endEstimatedDeliveryDate";
  public static final String END_ESTIMATED_SHIP_DATE = "endEstimatedShipDate";
  public static final String LINES_ORDERED_QUANTITY = "linesOrderedQuantity";
  public static final String LINES_SOURCENODES_SOURCE_NODE_ID = "lines_sourceNodes_sourceNodeId";
  public static final String LINES_SOURCENODES_SOURCE_NODE_TYPE =
      "lines_sourceNodes_sourceNodeType";
  public static final String LINES_SOURCENODES_FULLFILLED_QUANTITY =
      "lines_sourceNodes_fulfilledQuantity";
  public static final String EXCEPTION_LINES_LINE_ID = "exception_lines_lineId\n";
  public static final String CUTOFF_TIME = "cutOffTime";
  public static final String LINES_ITEM_ID = "lines_itemId";
  public static final String LINES_ITEM_TYPE = "lines_itemType";
  public static final String LINES_REQUEST_QTY = "lines_requestQuantity";
  public static final String LINES_MAX_AVAILABLE_QTY = "lines_maxAvailableQuantity";
  public static final String LINES_PROMISE_DETAILS_SOURCE_NODE_ID =
      "lines_promiseDetails_sourceNodeId";
  public static final String LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE =
      "lines_promiseDetails_sourceNodeType";
  public static final String LINES_PROMISE_DETAILS_SOURCE_NODE_TYPE_FILL_QTY =
      "lines_promiseDetails_sourceNodeType_fillQuantity";
  public static final String HAS_EXCEPTIONS = "hasExceptions";
  public static final String EXCEPTION_LINES_ITEM_ID = "exception_lines_itemId";
  public static final String EXCEPTION_LINES_ITEM_TYPE = "exception_lines_itemType";
  public static final String EXCEPTION_LINES_ERROR_CODE = "exception_lines_errorCode";
  public static final String EXCEPTION_LINES_ERROR_MESSAGE = "exception_lines_errorMessage";
  public static final String EXCEPTION_LINES_REQUEST_QTY = "exception_lines_requestQuantity";
  public static final String EXCEPTION_LINES_UNAVAILABLE_QTY =
      "exception_lines_unavailableQuantity";
  public static final String EDD_COMPUTATION_ORDER_AND_LINE_LIMIT_MESSAGE =
      "Edd Computation limit exceeded";

  public static final String ORDER_LIMIT = "OrderLimit";
  public static final String ORDER_LINE_LIMIT = "OrderLineLimit";
  public static final String ROW_NUMBER = "RowNumber";

  public static final String SESSION_ID = "sessionId";
  public static final String BASKET_ID = "basketId";
  public static final String PAGE_NAME = "pageName";
  public static final String SHIP_TO_ADDRESS_ZIPCODE = "shipToAddress_zipCode";
  public static final String SHIP_TO_ADDRESS_REGION = "shipToAddress_region";
  public static final String SHIP_TO_ADDRESS_STATE = "shipToAddress_state";
  public static final String SHIP_TO_ADDRESS_COUNTRY = "shipToAddress_country";
  public static final String SHIP_TO_ADDRESS_TIMEZONE = "shipToAddress_timezone";
  public static final String LINES_ITEM_ITEM_ID = "lines_item_itemId";
  public static final String LINES_ITEM_ITEM_TYPE = "lines_item_itemType";
  public static final String LINES_ITEM_UNIT_OF_MEASURE = "lines_item_unitOfMeasure";
  public static final String LINES_ITEM_SELLER = "lines_item_seller";
  public static final String LINES_LINE_ID = "lines_lineId";
  public static final String LINES_SERVICE_OPTION = "lines_serviceOption";
  public static final String LINES_ITEM_PRODUCT_CLASS = "lines_item_productClass";
  public static final String LINES_REQUIRED_QTY = "lines_requiredQty";
  public static final String LINES_SHIP_TO_ADDRESS_ZIPCODE = "lines_shipToAddress_zipCode";
  public static final String LINES_SHIP_TO_ADDRESS_REGION = "lines_shipToAddress_region";
  public static final String LINES_SHIP_TO_ADDRESS_PROVINCE = "lines_shipToAddress_province";
  public static final String LINES_SHIP_TO_ADDRESS_STATE = "lines_shipToAddress_state";
  public static final String LINES_SHIP_TO_ADDRESS_COUNTRY = "lines_shipToAddress_country";
  public static final String LINES_SHIP_TO_ADDRESS_TIMEZONE = "lines_shipToAddress_timezone";
  public static final String ORGANIZATION_CODE = "organizationCode";
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String UPDATE_U = "UPDATE";
  public static final String DELETE_D = "DELETE";
  public static final Set<String> actionTypes = Set.of(UPDATE, DELETE);
  public static final String CARRIER_SERVICE_HEADER = "Carrier Service:";
  public static final String CARRIER_SERVICE_ID = "carrierServiceId";
  public static final String CARRIER_SERVICE = "Carrier Service:";
  public static final String CARRIER_SERVICES = "carrierServices";
  public static final String DESTINATION_SOURCE_GEOZONE_HEADER =
      "Destination geoZone / Source geoZone ->";
  public static final String NODE_ID = "nodeId";
  public static final String SERVICE_OPTION = "serviceOption";
  public static final String PROCESSING_TIME_IN_HRS = "processingTime (in hrs)";
  public static final String LAST_PICKUP_TIME = "lastPickupTime";
  public static final String FILE_URI = "fileUri";
  public static final String CSV_FILE = "csvFile";
  public static final String CSV_HEADERS = "csvHeaders";
  public static final String FILE_TYPE = "fileType";
  public static final String SOURCE_GEO_ZONE = "sourceGeozone";
  public static final String DESTINATION_GEO_ZONE = "destinationGeozone";
  public static final String TRANSIT_DAYS = "transitDays";
  public static final String STREET = "street";
  public static final String CITY = "city";

  public static final String COUNTRY = "country";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String TIMEZONE = "timezone";
  public static final String SHIP_TO_HOME = "shipToHome";
  public static final String SDND_ELIGIBLE = "sdndEligible";
  public static final String BOPIS_ELIGIBLE = "bopisEligible";
  public static final String NEXTDAY_ELIGIBLE = "nextdayEligible";
  public static final String EXPRESS_ELIGIBLE = "expressEligible";
  public static final String NODE_TYPE = "nodeType";
  public static final String IS_ACTIVE = "isActive";
  public static final String START_WORKING_TIME = "startWorkingTime";
  public static final String LAST_WORKING_TIME = "lastWorkingTime";
  public static final String SOURCE_NODES = "sourceNodes";
  public static final String PRIORITY = "priority";
  public static final String ALLOCATION_RULE_ID = "allocationRuleId";
  public static final String CARRIER_ID = "carrierId";
  public static final String CARRIER_NAME = "carrierName";
  public static final String SERVICE_NAME = "serviceName";
  public static final String SERVICE_OPTIONS = "serviceOptions";

  public static final String STATE = "state";
  public static final String TIMEZONE1 = "timeZone";
  public static final String TYPE = "type";
  public static final String KEY = "key";
  public static final String WEIGHTAGE = "weightage";
  public static final String CALENDAR_ID = "calendarId";
  public static final String DESCRIPTION = "description";
  public static final String IS_MONDAY_WORKING = "isMondayWorking";
  public static final String IS_TUESDAY_WORKING = "isTuesdayWorking";
  public static final String IS_WEDNESDAY_WORKING = "isWednesdayWorking";
  public static final String IS_THURSDAY_WORKING = "isThursdayWorking";
  public static final String IS_FRIDAY_WORKING = "isFridayWorking";
  public static final String IS_SATURDAY_WORKING = "isSaturdayWorking";
  public static final String IS_SUNDAY_WORKING = "isSundayWorking";
  public static final String EXCEPTION_DAYS = "exceptionDays";
  public static final String EFFECTIVE_DATE = "effectiveDate";
  public static final String SHIPPING_STAGE = "shippingStage";
  public static final String ACTION_INVALID_MESSAGE = "action type invalid";
  public static final String BUFFER_HOURS = "bufferHours";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String BUFFER_DAYS = "bufferDays";
  public static final String SELECTION_CRITERIA = "selectionCriteria";

  public static final String INVALID_SELECTION_CRITERIA = "Invalid selection criteria";
  public static final String CREATE_C = "CREATE";
  public static final String CREATED_BY = "createdBy";
  public static final String BUFFER_START_DATE = "bufferStartDate";
  public static final String BUFFER_END_DATE = "bufferEndDate";
  public static final String CUSTOM_REGIONS = "customRegions";
  public static final String ZIP_CODE = "zipCode";
  public static final String ZIP_CODE_PREFIX = "zipCodePrefix";
  public static final String CART_ID = "cartId";
  public static final String DEFAULT = "DEFAULT";
  public static final String REGION_ID = "regionId";
  public static final String CUSTOM_REGION_NAME = "customRegionName";
  public static final String CUSTOM_REGION_DESCRIPTION = "customRegionDescription";
  public static final String GEOZONES = "geozones";
  public static final String NODE_LABOUR_TIER = "nodeLabourTier";
  public static final String COST_DEF_ORG_ID = "Org Id";
  public static final String COST_DEF_COST_TYPE = "Cost Type";
  public static final String CSV_VALUES = "csvValues";

  public static final String NEIP = "NEIP";
  public static final String PE = "PE";
  public static final String ITEM_BUFFER = "itembuffer";
  public static final String FAILURE = "FAILURE";
}
