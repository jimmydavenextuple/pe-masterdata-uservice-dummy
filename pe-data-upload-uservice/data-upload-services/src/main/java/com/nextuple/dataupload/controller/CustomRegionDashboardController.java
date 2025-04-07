/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetMarketRegionsDoc;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for custom region dashboard APIs.
 *
 * <p>This controller provides APIs to manage and retrieve custom region information for
 * organizations, with support for filtering by country, region IDs, and region names. It includes
 * pagination and sorting capabilities for efficient data retrieval.
 *
 * <p>The controller utilizes {@link PostalCodeFeign} client to interact with the postal code
 * service and retrieve custom region information. Key features include:
 *
 * <ul>
 *   <li>Filtering custom regions by organization and country
 *   <li>Optional filtering by region IDs and region names
 *   <li>Pagination support with customizable page size and number
 *   <li>Flexible sorting options with default sorting by customRegionId
 * </ul>
 *
 * <p>The controller is tagged with "Custom Regions APIs" for easy categorization in API
 * documentation.
 *
 * @see PostalCodeFeign
 * @see PageProperties
 * @see CustomRegionInfo
 */
@RestController
@RequestMapping("/ui/custom-region")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Custom Regions APIs")
public class CustomRegionDashboardController {

  private final PostalCodeFeign postalCodeFeign;

  private final PageProperties pageProperties;

  private static final String DEFAULT_SORT_BY_CUSTOM_REGION = "customRegionId";

  /**
   * Retrieves a paginated list of custom regions filtered by organization and country.
   *
   * <p>This method processes a GET request to fetch custom region data with support for additional
   * filtering by region IDs and names. It returns a paginated response containing custom region
   * entries.
   *
   * @param orgId The unique identifier for the organization. Must not be blank.
   * @param country The country code to filter custom regions. Must not be blank.
   * @param regionIds Optional comma-separated list of custom region IDs to filter the results.
   * @param regionNames Optional comma-separated list of custom region names to filter the results.
   * @param pageParams The pagination parameters including page number, page size, and sorting
   *     criteria.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     custom regions.
   */
  @GetMapping("/orgId/{orgId}/country/{country}")
  @GetMarketRegionsDoc
  public ResponseEntity<BaseResponse<PagePayload<CustomRegionInfo>>> getCustomRegions(
      @NotBlank(message = "OrgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "Country can't be empty")
          @PathVariable
          @Parameter(description = "Identifier for Country.")
          String country,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom regionID(s)")
          String regionIds,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom regionID(s)")
          String regionNames,
      PageParams pageParams) {
    Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    String sortBy = pageParams.getSortBy().orElse(DEFAULT_SORT_BY_CUSTOM_REGION);
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    BaseResponse<PagePayload<CustomRegionInfo>> feignResponse =
        postalCodeFeign.getCustomRegionInfo(
            orgId, country, regionIds, regionNames, pageNo, pageSize, sortBy, sortOrder);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(feignResponse.getMessage())
            .payload(feignResponse.getPayload())
            .build());
  }
}
