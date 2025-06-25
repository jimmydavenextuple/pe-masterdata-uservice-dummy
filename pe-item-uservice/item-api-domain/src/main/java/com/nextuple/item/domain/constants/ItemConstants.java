/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.constants;

public class ItemConstants {

  private ItemConstants() {} // NOSONAR

  public static final String ADD_ITEM_DESC =
      "Adds new items or updates item if it exists in the retail system's inventory to ensure that the inventory is up-to-date and accurate. This API adds items by passing the item ID, organization ID, and UOM attributes in the path parameters for the required item.";
  public static final String UPDATE_ITEM_DESC =
      "Updates details such as prices, descriptions, images, customAttributes and service option eligibility for a specific item in the enterprise's catalog.";
  public static final String DELETE_ITEM_DESC =
      "Deletes an item when it is no longer required. Items can be deleted for a variety of reasons including deleting items that are no longer in stock or have been discontinued, and items that are part of a promotion or sale once the promotion or sale has ended. This API deletes the item by passing the organization ID, item ID, and UOM attributes in the path parameter for the required item.";
  public static final String GET_ITEM_DESC =
      "Retrieves the information such as the item's name, description, price, images, customAttributes and other relevant information about a specific item in a retailer's catalog. This API retrieves the information by passing the organization ID, item ID, and UOM attributes in the path parameters for the required item.";
  public static final String GET_ITEM_LIST_DESC =
      "Retrieves the information such as list of the items in a retailer's catalog. This API retrieves the information by passing the organization ID and list of item IDs in the query parameters for the required item.";
  public static final String ITEM_ID = "Unique identifier of the item.";
  public static final String ITEM_ID_EXAMPLE = "ITEM-01";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";
  public static final String UOM = "Unit of measure of the item.";
  public static final String UOM_EXAMPLE = "EACH";
  public static final String ITEM_SOURCE =
      "The source node or supplier from which a product should be shipped.";
  public static final String ITEM_SOURCE_EXAMPLE = "DSV-ITEM";
  public static final String VENDOR_TYPE = "Type of the vendor.";
  public static final String VENDOR_TYPE_EXAMPLE = "DSV";
  public static final String PRODUCT = "Product ID of the item.";
  public static final String PRODUCT_EXAMPLE = "PRODUCT-01";
  public static final String COLOR = "Color of the item.";
  public static final String COLOR_EXAMPLE = "RED";
  public static final String SIZE = "Size of the item.";
  public static final String SIZE_EXAMPLE = "10";
  public static final String IS_DSV_ELIGIBLE =
      "Flag indicating whether the item is eligible for DSV.";
  public static final String SHIP_ELIGIBLE = "Flag indicating whether the item can be shipped.";
  public static final String PICK_ELIGIBLE =
      "Flag indicating whether the item is eligible for PICK.";
  public static final String PARCEL_SHIPMENT_ELIGIBLE =
      "Flag indicating the parcel shipment eligibility.";
  public static final String SERVICE_OPTION_ELIGIBILITIES =
      "Specifies a boolean flag for service option eligibility.";
  public static final String SERVICE_OPTION_ELIGIBILITIES_EXAMPLE =
      "{\"sdndEligible\":true,\"expressEligible\":true,\"nextdayEligible\":true}";
  public static final String SHIP_ALONE = "Flag indicating if the item is shipped alone.";
  public static final String HEIGHT = "Height of the item.";
  public static final String WIDTH = "Width of the item.";
  public static final String LENGTH = "Length of the item.";
  public static final String VOLUME = "Volume of the item.";
  public static final String DIMENSION_UOM = "Unit of measure for the item dimension.";
  public static final String VOLUME_UOM = "Unit of measure for the item volume.";
  public static final String WEIGHT = "Weight of the item.";
  public static final String BOOL_EXAMPLE = "true";
  public static final String VOLUME_UOM_EXAMPLE = "LITRE";
  public static final String DIMENSION_UOM_EXAMPLE = "CM";
  public static final String DIMENSION_EXAMPLE = "10.0";
  public static final String TIME_EXAMPLE = "1";
  public static final String WEIGHT_UOM_EXAMPLE = "LBS";
  public static final String WEIGHT_UOM = "Unit of measure of the item weight.";
  public static final String PROCESSING_TIME = "Time taken to process the item.";
  public static final String COST = "Specifies the cost of the item.";
  public static final String IS_HAZMAT = "Flag indicating if the item is a hazardous material.";
  public static final String SHORT_DESCRIPTION = "Brief description about the item.";
  public static final String IS_WHITE_GLOVE = "Flag indicating if the item is a white glove item.";
  public static final String LEAD_TIME = "Total time taken to process and deliver the item.";
  public static final String DEPARTMENT_NUMBER = "Department number of the item.";
  public static final String DEPARTMENT_NUMBER_EXAMPLE = "DEPT-01-SPORTS";
  public static final String DEPARTMENT_NAME = "Department name of the item.";
  public static final String DEPARTMENT_NAME_EXAMPLE = "SPORTS";
  public static final String IMAGE_URL = "Image URL of the item.";
  public static final String IMAGE_URL_EXAMPLE = "https://pe-item.nextuple.com/media/ITEM-01.png";
  public static final String INVENTORY_NODE_TYPES = "Inventory node types for the given item.";
  public static final String ITEM_BANNER = "The classification/group name of the item.";
  public static final String ITEM_BANNER_EXAMPLE = "Sports Equipment";
  public static final String TOPIC_NAME = "Name of the kafka topic.";
  public static final String TOPIC_NAME_EXAMPLE = "item.promise.nextuple";
  public static final String ADD_ITEM_SUCCESS =
      "A 200 success code indicates that the item has been added successfully.";
  public static final String UPDATE_ITEM_SUCCESS =
      "A 200 success code indicates that the item details are updated successfully.";
  public static final String GET_ITEM_SUCCESS =
      "A 200 success code indicates that the item details are retrieved successfully.";
  public static final String DELETE_ITEM_SUCCESS =
      "A 200 success code indicates that the item is deleted successfully.";
  public static final String GET_ITEM_LIST_SUCCESS =
      "A 200 success code indicates that the item list is retrieved successfully.";
  public static final String HANDLING_TYPE = "Handling type of the item.";
  public static final String HANDLING_TYPE_EXAMPLE = "Conveyable";
  public static final String BUFFER_HOURS = "Buffer hours of the item buffer.";
  public static final String BUFFER_HOURS_EXAMPLE = "10.0";
  public static final String BUFFER_START_DATE = "Buffer start date of the item buffer.";
  public static final String BUFFER_START_DATE_EXAMPLE = "2023-01-01T00:00:00Z";
  public static final String BUFFER_END_DATE = "Buffer end date of the item buffer.";
  public static final String BUFFER_END_DATE_EXAMPLE = "2023-02-01T00:00:00Z";
  public static final String ID = "Unique identifier of the item buffer.";
  public static final String ID_EXAMPLE = "1";
  public static final String ACTIVE_ITEM_BUFFER = "Specifies an active buffer for an item.";
  public static final String ACTIVE_ITEM_BUFFER_EXAMPLE =
      "{\"bufferHours\":1,\"bufferStartDate\":\"2024-06-14T01:00:00.000+00:00\",\"bufferEndDate\":\"2025-06-13T21:59:00.000+00:00\"}";
  public static final String BUYING_COST = "Per unit buying cost of the item.";
  public static final String BUYING_COST_EXAMPLE = "2";
  public static final String SHORT_DESCRIPTION_EXAMPLE = "Bold fit Skipping Rope";
  public static final String ITEM_SORT_BY = "itemId";
  public static final String GET_ITEM_LIST_PAGINATED_SUCCESS =
      "A 200 success code indicates that the paginated item list is retrieved successfully.";
  public static final String PAGINATION_NEXT_URL = "next";
  public static final String PAGINATION_PREVIOUS_URL = "previous";
  public static final String VALID_DROPOFF_DURATION =
      "Valid dropOff duration in hours for the item.";
  public static final String VALID_DROPOFF_DURATION_EXAMPLE = "2.4";

  public static final String CONVERSION_FACTOR = "Conversion factor of the alternate item.";

  public static final String CONVERSION_FACTOR_EXAMPLE = "in";
  public static final String PRIORITY = "Priority for the alternate item substitution.";
  public static final String PRIORITY_EXAMPLE = "1";
}
