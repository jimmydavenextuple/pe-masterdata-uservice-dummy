/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.domain;

public class NodeConstants {
  private NodeConstants() {} // NOSONAR

  public static final String CREATE_NODE_DESC =
      "Creates a node using different attributes such as the organization ID, address, and zip code. The following types of nodes can be created: Fulfillment Centre (FC), Micro-Fulfillment Centre (MFC), Store and Drop-Ship Vendor (DSV).";
  public static final String DELETE_NODE_DESC =
      "Deletes an existing node when it is no longer required. Nodes can be deleted for various reasons such as closing stores as part of a rebranding strategy or if a store is too small to effectively handle the volume of merchandise. This API deletes the nodes by passing the organization ID and node ID attributes in the path parameters for the required node.";
  public static final String GET_NODES_DESC =
      "Retrieves the information about the nodes and provides retailers with a holistic view of their operations, customer preferences, and trends. This API retrieves the information by passing the organization ID and node ID attributes in the path parameters for the required node.";
  public static final String UPDATE_NODE_DESC =
      "Updates the node information such as organization ID, address, and zip code for the given node ID and organization ID.";
  public static final String GET_NODE_PAGE_DESC =
      "Retrieves the details of all the nodes associated with a specific organization ID. For example, for the organization ID “NXT”, the API will display all the nodes that are associated with the organization NXT.";
  public static final String GET_ALL_NODE_DESC = "Retrieves the details of all the nodes.";
  public static final String GET_NODE_CACHE_DESC =
      "Retrieves the list of all the cache keys for the node.";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";
  public static final String NODE_ID = "Unique identifier of the node.";
  public static final String NODE_ID_EXAMPLE = "NODE-01";
  public static final String ZIP_CODE_EXAMPLE = "M1R 5A2";
  public static final String SERVICE_OPTION_ELGIBILITIES =
      "List of service eligibility options and flag that indicates if the service eligibility option is enabled.";
  public static final String SERVICE_OPTION_ELGIBILITIES_EXAMPLE =
      "{\"sdndEligible\":true,\"expressEligible\":true,\"nextdayEligible\":true}";
  public static final String CREATE_NODE_DETAILS_SUCCESS =
      "A 200 success code indicates that the node has been created successfully.";
  public static final String UPDATE_NODE_DETAILS_SUCCESS =
      "A 200 success code indicates that the node details have been updated successfully.";
  public static final String GET_NODE_DETAILS_SUCCESS =
      "A 200 success code indicates that the node details are retrieved successfully.";
  public static final String DELETE_NODE_SUCCESS =
      "A 200 success code indicates that the given node has been deleted successfully.";
  public static final String GET_NODE_PAGE_SUCCESS =
      "A 200 success code indicates that the node details for the given organization ID are retrieved successfully";
  public static final String GET_ALL_NODE_SUCCESS =
      "A 200 success code indicates that all the node details are retrieved successfully.";
  public static final String GET_NODE_CACHE_SUCCESS =
      "A 200 success code indicates that all the cache keys for the node are retrieved successfully.";
  public static final String BOPIS_ELIGIBLE =
      "Flag that indicates if items bought online can be picked up at the node.";
  public static final String NODE_TYPE = "Type of the node.";
  public static final String NODE_TYPE_EXAMPLE = "DSV";
  public static final String IS_ACTIVE = "Flag that indicates whether node is active or not.";
  public static final String BOOL_EXAMPLE = "true";
  public static final String TIMEZONE = "Timezone of the node.";
  public static final String TIMEZONE_EXAMPLE = "UTC";
  public static final String COUNTRY = "Country in which the node is located.";
  public static final String COUNTRY_EXAMPLE = "CANADA";
  public static final String STREET = "Street on which the node is located.";
  public static final String STREET_EXAMPLE = "100 Metropolitan Road";
  public static final String CITY = "City in which the node is located.";
  public static final String CITY_EXAMPLE = "Scarborough";
  public static final String SHIP_TO_HOME =
      "Flag that indicates if the ship to home option is available.";
  public static final String STATE_EXAMPLE = "ON";
  public static final String START_WORKING_TIME = "Describes the opening time of the given node.";
  public static final String START_WORKING_TIME_EXAMPLE = "09:00";
  public static final String LAST_WORKING_TIME =
      "Describes the last working time of the given node.";
  public static final String LAST_WORKING_TIME_EXAMPLE = "23:00";
  public static final String LATITUDE = "Latitude at which the node is located.";
  public static final String LATITUDE_EXAMPLE = "43.769912";
  public static final String LONGITUDE = "Longitude at which the node is located.";
  public static final String LONGITUDE_EXAMPLE = "-79.296678";
  public static final String STATE = "State in which the node is located.";
  public static final String ZIP_CODE = "Zip code of the node.";
  public static final String NODE_LABOUR_TIER = "Tier of labour available at node.";
  public static final String NODE_LABOUR_TIER_EXAMPLE = "tier1";
  public static final String GET_NODE_TYPES_DESC =
      "Retrieves all the node types associated with a specific organization ID. For example, for the organization ID “NXT”, the API will display all the unique node types that are associated with the organization NXT.";
  public static final String GET_NODE_TYPES_SUCCESS =
      "A 200 success code indicates that the node types are retrieved successfully.";
  public static final String CHECK_NODES_EXIST_DESC =
      "Checks if the nodes exist for the given organization ID and list of node IDs. For example, for the organization ID “NXT” and node IDs “NODE-01” and “NODE-02”, the API will check if these nodes exist in the system.";
  public static final String CHECK_NODES_EXIST_SUCCESS =
      "A 200 success code indicates that successfully checked if the nodes exist for the given organization ID and list of node IDs.";
}
