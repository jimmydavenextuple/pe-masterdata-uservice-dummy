/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.nextuple.dataupload.controller.docs.GetTransitBufferConfigDoc;
import com.nextuple.dataupload.controller.docs.GetTransitBufferDetailsForCarrierServicesDoc;
import com.nextuple.dataupload.domain.pojo.TransitTimeBufferPageProperties;
import com.nextuple.dataupload.service.TransitTimeBufferService;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Transit Buffer Details for Carrier Services APIs.
 *
 * <p>This controller provides APIs to interact with transit time buffer details and configurations
 * for carrier services. It supports operations like fetching transit buffer details with
 * pagination, sorting, and filtering.
 *
 * <p>The controller is tagged with "Transit Buffer Details for Carrier Service APIs" for easy
 * categorization in API documentation.
 */
@RestController
@RequestMapping("/ui/v1/transit-buffer")
@RequiredArgsConstructor
@Tag(name = "Transit Buffer Details for Carrier Service APIs")
public class TransitTimeBufferForCarrerServiceController {

  private final TransitTimeBufferService transitTimeBufferService;
  private final TransitTimeBufferPageProperties transitTimeBufferPageProperties;
  private final DefaultPageProperties defaultPageProperties;
  private final Logger logger =
      LoggerFactory.getLogger(TransitTimeBufferForCarrerServiceController.class);
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/v1/transit-buffer/%s?pageNo=%d&pageSize=%d&sortBy=%s&sortOrder=%s";

  /**
   * Fetches the transit time buffer details for carrier services for the given organization.
   *
   * <p>This method processes a GET request to fetch transit time buffer details for carrier
   * services. The results are paginated based on the provided page parameters.
   *
   * @param orgId The unique identifier for the organization.
   * @param pageParams The pagination parameters (page number, page size, sorting order).
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched transit
   *     time buffer details.
   */
  @GetMapping("/{orgId}")
  @GetTransitBufferDetailsForCarrierServicesDoc
  public ResponseEntity<BaseResponse<PagePayload<TransitBufferDetailsResponse>>>
      getTransitTimeBufferDetailsForCarrierServices(
          @PathVariable @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @ModelAttribute("applicationForm") @Valid PageParams pageParams) {
    logger.debug("Processing get transit time buffer details");

    PagePayload<TransitBufferDetailsResponse> pagePayload =
        transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
            orgId,
            pageParams.getPageNo().orElse(defaultPageProperties.getPageNo()),
            pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
            pageParams.getSortBy().orElse(transitTimeBufferPageProperties.getSortBy()),
            pageParams.getSortOrder().orElse(transitTimeBufferPageProperties.getSortOrder()));

    updatePaginationDetails(pagePayload, orgId, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time buffers fetched successfully.")
            .payload(pagePayload)
            .build());
  }

  /**
   * Updates the pagination details (next and previous URIs) for the given page payload.
   *
   * <p>This method calculates and sets the next and previous pagination links in the provided
   * {@link PagePayload}.
   *
   * @param pagePayload The {@link PagePayload} containing the current page data.
   * @param orgId The unique identifier for the organization.
   * @param pageParams The pagination parameters (page number, page size, sorting order).
   */
  private void updatePaginationDetails(
      PagePayload<TransitBufferDetailsResponse> pagePayload, String orgId, PageParams pageParams) {
    int currentPage = pagePayload.getPagination().getCurrentPage();
    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                currentPage + 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                pagePayload.getPagination().getSortBy(),
                pagePayload.getPagination().getSortOrder()));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                currentPage - 1,
                pageParams.getPageSize().orElse(defaultPageProperties.getPageSize()),
                pagePayload.getPagination().getSortBy(),
                pagePayload.getPagination().getSortOrder()));

    pagePayload.getPagination().setNext(nextUri);
    pagePayload.getPagination().setPrevious(previousUri);
  }

  /**
   * Fetches the transit buffer configuration requests for a specific carrier service and
   * organization.
   *
   * <p>This method processes a GET request to fetch the transit buffer configuration requests for a
   * specified carrier service and organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param carrierServiceId The unique identifier for the carrier service.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched transit
   *     buffer configurations.
   */
  @GetMapping("/request/{orgId}")
  @GetTransitBufferConfigDoc
  public ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>>
      getTransitBufferConfigRequests(
          @PathVariable @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @NotBlank(message = "carrierServiceId can't be blank")
              @RequestParam
              @Parameter(description = "Unique identifier for Carrier Service.")
              String carrierServiceId) {
    logger.debug("Processing get transit buffer config requests");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit Buffer Configuration fetched successfully.")
            .payload(
                transitTimeBufferService.getTransitBufferConfigRequests(orgId, carrierServiceId))
            .build());
  }
}
