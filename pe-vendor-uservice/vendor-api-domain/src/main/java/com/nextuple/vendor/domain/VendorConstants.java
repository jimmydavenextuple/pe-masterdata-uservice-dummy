/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.domain;

public class VendorConstants {

  private VendorConstants() {} // NOSONAR

  public static final String CREATE_VENDOR_DESC =
      "Creates a vendor using different attributes such as the organization ID, vendorDescription, vendorType.";
  public static final String DELETE_VENDOR_DESC =
      "Deletes an existing vendor when it is no longer required. This API deletes the vendor by passing the organization ID and vendor ID attributes in the path parameters for the required vendor.";
  public static final String UPDATE_VENDOR_DESC =
      "Updates the vendor information such as organization ID, vendorType, vendorDescription given vendor ID and organization ID.";
  public static final String GET_VENDOR_DESC =
      "Fetches the vendor information based on the vendor key - organization Id and vendor Id fields.";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String VENDOR_ID = "Unique identifier of the vendor.";
  public static final String VENDOR_ID_EXAMPLE = "VENDOR-01";
  public static final String CREATE_VENDOR_DETAILS_SUCCESS =
      "A 200 success code indicates that the vendor has been created successfully.";
  public static final String UPDATE_VENDOR_DETAILS_SUCCESS =
      "A 200 success code indicates that the vendor details have been updated successfully.";

  public static final String GET_VENDOR_DETAILS_SUCCESS =
      "A 200 success code indicates that the vendor details have been retrieved successfully.";
  public static final String DELETE_VENDOR_SUCCESS =
      "A 200 success code indicates that the given vendor has been deleted successfully.";
  public static final String VENDOR_TYPE = "Type of the vendor.";
  public static final String VENDOR_TYPE_EXAMPLE = "Store";
  public static final String VENDOR_DESC = "Vendor name.";
  public static final String VENDOR_DESC_EXAMPLE = "ABC Retail Vendor";
}
