/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.controller.docs.CreateCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.DeleteCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetCustomRegionListDoc;
import com.nextuple.postal.code.timezone.controller.docs.UpdateCustomRegionDetails;
import com.nextuple.postal.code.timezone.service.CustomRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/custom-region")
@RequiredArgsConstructor
@Tag(name = "Custom Region APIs")
public class CustomRegionController {

  private static final Logger logger = LoggerFactory.getLogger(CustomRegionController.class);
  private final CustomRegionService customRegionService;
  private final PageProperties pageProperties;
  private static final String PAGINATION_URL = "/%s?pageNo=%d&pageSize=%d";
  public static final String CUSTOM_REGION_DEFAULT_SORT_BY = "id";

  @Operation(
      summary = "Create Custom Region",
      description = "Creates a custom region with the given details.")
  @CreateCustomRegionDetails
  @PostMapping
  public ResponseEntity<BaseResponse<CustomRegionResponse>> createCustomRegion(
      @Valid @RequestBody CustomRegionRequest customRegionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create custom region request");

    return new ResponseEntity<>(
        BaseResponse.builder()
            .message("Custom Region successfully created")
            .payload(customRegionService.createCustomRegion(customRegionRequest))
            .build(),
        HttpStatus.CREATED);
  }

  @Operation(
      summary = "Get Custom Region",
      description = "Retrieves the custom region by the organization ID and custom region ID.")
  @GetCustomRegionDetails
  @GetMapping("/orgId/{orgId}/custom-region/{id}")
  public ResponseEntity<BaseResponse<CustomRegionResponse>> fetchCustomRegionDetailsByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "regionId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the region.")
          String id)
      throws CommonServiceException {
    logger.debug("--Processing get custom regions request --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Regions Details fetched successfully")
            .payload(customRegionService.fetchRegionByOrgIdAndId(orgId, id))
            .build());
  }

  @Operation(
      summary = "Update Custom Region",
      description = "Updates the custom region with the given details.")
  @UpdateCustomRegionDetails
  @PutMapping
  public ResponseEntity<BaseResponse<CustomRegionResponse>> updateCustomRegion(
      @Valid @RequestBody CustomRegionRequest customRegionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update custom region request");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Region successfully updated")
            .payload(customRegionService.updateCustomRegion(customRegionRequest))
            .build());
  }

  @Operation(
      summary = "Delete Custom Region",
      description = "Deletes the custom region by the organization ID and custom region ID.")
  @DeleteCustomRegionDetails
  @DeleteMapping("/orgId/{orgId}/custom-region/{id}")
  public ResponseEntity<BaseResponse<CustomRegionResponse>> deleteCustomRegion(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "regionId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the region.")
          String id)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete custom region request");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Region successfully deleted")
            .payload(customRegionService.deleteCustomRegion(orgId, id))
            .build());
  }

  @GetCustomRegionListDoc
  @GetMapping("/list/orgId/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CustomRegionDto>>> getCustomRegionList(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      PageParams pageParams)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get custom regions list for an orgId");

    Page<CustomRegionDto> customRegionDtoPage =
        customRegionService.getCustomRegionListByOrgId(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(CUSTOM_REGION_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<CustomRegionDto> pagePayload =
        setCustomRegionPagePayload(customRegionDtoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Region List fetched successfully")
            .payload(pagePayload)
            .build());
  }

  private PagePayload<CustomRegionDto> setCustomRegionPagePayload(
      Page<CustomRegionDto> customRegionDtoPage, PageParams pageParams, @NotBlank String orgId) {
    PagePayload<CustomRegionDto> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) customRegionDtoPage.getTotalElements());
    pagination.setTotalPages(customRegionDtoPage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(CUSTOM_REGION_DEFAULT_SORT_BY));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            customRegionDtoPage.getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            customRegionDtoPage.getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(customRegionDtoPage.getContent());

    return pagePayload;
  }
}
