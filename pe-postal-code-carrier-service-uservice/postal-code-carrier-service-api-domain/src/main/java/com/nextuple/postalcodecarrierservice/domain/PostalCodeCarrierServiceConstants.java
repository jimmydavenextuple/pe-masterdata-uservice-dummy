/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.domain;

public class PostalCodeCarrierServiceConstants {
  private PostalCodeCarrierServiceConstants() {} // NOSONAR

  public static final String CREATE_POSTAL_CODE_CARRIER_SERVICE_DESC =
      "Creates a postal code carrier service mapping using zip code, carrier service ID, red zone flag and red zone reason.";
  public static final String DELETE_POSTAL_CODE_CARRIER_SERVICE_DESC =
      "Deletes an existing postal code carrier service mapping when it is no longer required.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_DESC =
      "Retrieves the postal code carrier service mapping information for the specified zip code and carrier service ID.";
  public static final String UPDATE_POSTAL_CODE_CARRIER_SERVICE_DESC =
      "Updates the postal code carrier service mapping information such as red zone flag and red zone reason.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_PAGE_DESC =
      "Retrieves the details of all postal code carrier service mappings with pagination support.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_CACHE_DESC =
      "Retrieves the list of all cache keys for postal code carrier service mappings.";

  public static final String ZIP_CODE = "ZIP code for the postal code carrier service mapping.";
  public static final String ZIP_CODE_EXAMPLE = "12345";
  public static final String CARRIER_SERVICE_ID = "Unique identifier of the carrier service.";
  public static final String CARRIER_SERVICE_ID_EXAMPLE = "FEDEX_GROUND";
  public static final String IS_RED_ZONE =
      "Flag indicating if the postal code is in a red zone for the carrier service.";
  public static final String IS_RED_ZONE_EXAMPLE = "true";
  public static final String RED_ZONE_REASON =
      "Reason for the postal code being marked as red zone.";
  public static final String RED_ZONE_REASON_EXAMPLE = "Remote area with limited access";

  public static final String CREATE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS =
      "A 200 success code indicates that the postal code carrier service mapping has been created successfully.";
  public static final String UPDATE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS =
      "A 200 success code indicates that the postal code carrier service mapping has been updated successfully.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_SUCCESS =
      "A 200 success code indicates that the postal code carrier service mapping details are retrieved successfully.";
  public static final String DELETE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS =
      "A 200 success code indicates that the postal code carrier service mapping has been deleted successfully.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_PAGE_SUCCESS =
      "A 200 success code indicates that the postal code carrier service mappings are retrieved successfully.";
  public static final String GET_POSTAL_CODE_CARRIER_SERVICE_CACHE_SUCCESS =
      "A 200 success code indicates that all cache keys for postal code carrier service mappings are retrieved successfully.";
}
