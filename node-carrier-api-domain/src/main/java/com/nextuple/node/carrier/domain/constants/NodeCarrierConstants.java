/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.constants;

public class NodeCarrierConstants {

  private NodeCarrierConstants() {} // NOSONAR

  public static final String CREATE_NODE_DESC =
      "Adds or associates a carrier service with a node. A carrier service is only considered for EDD calculation if it is associated with the node. This API can also be used to add service options, last pickup time, and processing time to a specific node. Once the last pickup time is added to a node, the API can use it to calculate the EDD. For example, if a node specifies that the last pickup time for a particular carrier service is 19:00, the API will use this time to calculate the EDD. This API is also used to add the processing time to a particular node, which is the time it takes the node to process a particular request.";
  public static final String GET_NODE_CARRIER_DESC =
      "Retrieves the information about the carrier services associated with a specific Node ID. This API retrieves the information by passing the Node ID, Organization ID, Carrier Service ID, and Service Option attributes in the path parameters for the desired node.";
  public static final String UPDATE_NODE_CARRIER_DESC =
      "Updates the information such as the processing time and last pickup time for a given node. The processing time and last pickup time can be updated by stores during the Christmas season when the stores operate for longer hours, or during holidays when the stores are not operational. This API updates the information by passing the Node ID, Organization ID, Carrier Service ID and Service Option attributes in the path parameters for the required node.";
  public static final String DELETE_NODE_CARRIER_BY_NODEID_ORGID_CSID_SO_DESC =
      "Deletes an existing node carrier when it is no longer required. Node carriers can be deleted for various reasons such as discontinuation of services, unavailability of service options, and availability of better carriers. This API deletes the information by passing the Node ID, Organization ID, Carrier Service ID, and Service Option attributes in the path parameters for the required node.";
  public static final String DELETE_NODE_CARRIER_BY_NODEID_ORGID_SO_DESC =
      "Deletes an existing node-carrier association when it is no longer required. Node carriers can be deleted for a variety of reasons, including discontinuation of services, unavailability of service options, and availability of better carriers. This API deletes the information by passing the Node ID, Organization ID, and Service Option attributes in the path parameters for the required node.";
  public static final String GET_NODE_CARRIER_LIST_BY_NODEID_ORGID_SO_DESC =
      "Retrieves the list of carrier services associated with a specific Node ID, Organization ID and Service Option. The API retrieves the information by passing the Node ID, Organization ID and Service Option attributes in the path parameters for the required node.";
  public static final String GET_NODE_CARRIER_LIST_BY_NODEID_ORGID_DESC =
      "Retrieves the list of carrier services associated with a specific Node ID and Organization ID. The API retrieves the information by passing the Node ID and Organization ID attributes in the path parameters for the required node.";
  public static final String ADD_NC_SELECTION_PRI_DESC =
      "Adds or associates a carrier service with a node ID based on the selection priority of the store. The selection priority is based on the transit time of the carrier service. Carrier services that have a quicker transit time have a selection priority of 1, and those that have a longer transit time have a selection priority of 0. For example, if a store wants to select a carrier service with a quicker transit time, then the store opts for the carrier service with a selection priority of 1 in the API. However, during the holidays, if a store wants to opt for a carrier service with a longer transit time, then the store opts for the carrier service with a selection priority of 0 in the API.";
  public static final String GET_NC_SELECTION_PRI_DESC =
      "Retrieves the information about the selection priority assigned to a carrier service associated with a specific node ID. The selection priority assigned to a carrier service affects the EDD calculation, because the selection priority is assigned based on the transit time of the carrier service. The API retrieves the information by passing the Organization ID, Service Option and Destination Geo Zone attributes in the path parameters for the required node.";
  public static final String GET_NC_WITH_LAST_PT_DESC =
      "Retrieves the list of node-carrier associations with details of the last pickup time. This API retrieves the information by passing Node ID and Organization ID attributes in the path parameters.";
  public static final String GET_UNIQUE_NCS_DESC =
      "Retrieves the list of unique node-carrier services for a given Organization ID and Node ID. This API retrieves the information by passing Organization ID and Node ID attributes in the path parameters.";
  public static final String DELETE_NC_SELECTION_DESC =
      "Deletes the selection priority information assigned to a carrier service associated with a specific node when it is no longer required.";
  public static final String GET_ALL_NC_BY_ORGID_DESC =
      "Retrieves the list of all node-carriers for the given Organization ID.";
  public static final String GET_ALL_NC_BY_ORGID_CSID_DESC =
      "Retrieves the list of all node-carriers for a given combination of Organization ID and Carrier Service ID. This API retrieves the information by passing the Organization ID and Carrier Service ID attributes in path parameters.";
  public static final String UPDATE_PLT_DESC =
      "Updates the processing lead time for a node-carrier association.";
  public static final String UPDATE_BUFFER_DESC =
      "Updates the information such as buffer time or buffer hours for the stores. A retailer company can use this API to quickly update the buffer time across their stores. Stores can update the buffer time during Christmas when orders are higher, or during the holidays when the stores are not operational on certain days. This API can be used to update the Buffer Start Date and Buffer End Date to update the Buffer Hours.";

  public static final String NODE_ID = "Unique identifier of the node.";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String CARRIER_SERVICE_ID = "Unique identifier of the carrier service.";
  public static final String SERVICE_OPTION =
      "Service option that associates the node with the specified carrier.";
  public static final String PROCESSING_TIME =
      "Processing time of the node-carrier association in hours.";
  public static final String BUFFER_START_DATE = "Buffer start date in UTC format.";
  public static final String BUFFER_END_DATE = "Buffer end date in UTC format.";
  public static final String BUFFER_HOURS = "Buffer hours of the node-carrier in hours.";
  public static final String PICKUP_TIME = "Last pickup time of the node-carrier.";
  public static final String SRC_GEOZONE = "Geozone of the source.";
  public static final String DEST_GEOZONE = "Geozone of the destination.";
  public static final String NODE_ID_EXAMPLE = "NODE-01";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";
  public static final String CARRIER_SERVICE_ID_EXAMPLE = "PURO-EXPRESS";
  public static final String SERVICE_OPTION_EXAMPLE = "EXPRESS";
  public static final String DOUBLE_EXAMPLE = "2.0";
  public static final String DATE_EXAMPLE = "2023-01-01T00:00:00Z";
  public static final String PICKUP_TIME_EX = "19:00";
  public static final String GEOZONE_EXAMPLE = "ON";
}
