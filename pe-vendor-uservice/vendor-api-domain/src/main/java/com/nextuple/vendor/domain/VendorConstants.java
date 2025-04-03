package com.nextuple.vendor.domain;

public class VendorConstants {
  public static final String CREATE_VENDOR_DESC =
      "Creates a vendor using different attributes such as the organization ID, vendorDescription, vendorType.";
  public static final String DELETE_VENDOR_DESC =
      "Deletes an existing vendor when it is no longer required. This API deletes the vendor by passing the organization ID and vendor ID attributes in the path parameters for the required vendor.";
  public static final String GET_NODES_DESC =
      "Retrieves the information about the nodes and provides retailers with a holistic view of their operations, customer preferences, and trends. This API retrieves the information by passing the organization ID and node ID attributes in the path parameters for the required node.";
  public static final String UPDATE_VENDOR_DESC =
      "Updates the vendor information such as organization ID, vendorType, vendorDescription given vendor ID and organization ID.";
  public static final String GET_NODE_PAGE_DESC =
      "Retrieves the details of all the nodes associated with a specific organization ID. For example, for the organization ID “NXT”, the API will display all the nodes that are associated with the organization NXT.";
  public static final String GET_ALL_NODE_DESC = "Retrieves the details of all the nodes.";
  public static final String GET_NODE_CACHE_DESC =
      "Retrieves the list of all the cache keys for the node.";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";
  public static final String VENDOR_ID = "Unique identifier of the vendor.";
  public static final String VENDOR_ID_EXAMPLE = "VENDOR-01";
  public static final String ZIP_CODE_EXAMPLE = "M1R 5A2";
  public static final String SERVICE_OPTION_ELGIBILITIES =
      "List of service eligibility options and flag that indicates if the service eligibility option is enabled.";
  public static final String SERVICE_OPTION_ELGIBILITIES_EXAMPLE =
      "{\"sdndEligible\":true,\"expressEligible\":true,\"nextdayEligible\":true}";
  public static final String CREATE_VENDOR_DETAILS_SUCCESS =
      "A 200 success code indicates that the vendor has been created successfully.";
  public static final String UPDATE_VENDOR_DETAILS_SUCCESS =
      "A 200 success code indicates that the vendor details have been updated successfully.";
  public static final String GET_NODE_DETAILS_SUCCESS =
      "A 200 success code indicates that the node details are retrieved successfully.";
  public static final String DELETE_VENDOR_SUCCESS =
      "A 200 success code indicates that the given vendor has been deleted successfully.";
  public static final String GET_NODE_PAGE_SUCCESS =
      "A 200 success code indicates that the node details for the given organization ID are retrieved successfully";
  public static final String GET_ALL_NODE_SUCCESS =
      "A 200 success code indicates that all the node details are retrieved successfully.";
  public static final String GET_NODE_CACHE_SUCCESS =
      "A 200 success code indicates that all the cache keys for the node are retrieved successfully.";
  public static final String BOPIS_ELIGIBLE =
      "Flag that indicates if items bought online can be picked up at the node.";
  public static final String VENDOR_TYPE = "Type of the vendor.";
  public static final String VENDOR_TYPE_EXAMPLE = "Store";
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
  public static final String VENDOR_DESC = "Vendor name.";
  public static final String VENDOR_DESC_EXAMPLE = "ABC Retail Vendor";
}
