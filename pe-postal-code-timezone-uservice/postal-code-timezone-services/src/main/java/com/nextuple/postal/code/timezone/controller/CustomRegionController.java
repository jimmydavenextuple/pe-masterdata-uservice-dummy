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
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.DeleteCustomRegionGeozonesRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.controller.docs.CreateCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.DeleteCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.DeleteCustomRegionGeozones;
import com.nextuple.postal.code.timezone.controller.docs.GetCustomRegionDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetCustomRegionInfoDoc;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing custom regions.
 *
 * <p>This controller provides APIs to create, update, delete, and retrieve custom regions for a
 * specific organization. It supports pagination for retrieving lists of custom regions and allows
 * the retrieval of individual custom region details based on organization ID and custom region ID.
 *
 * <p>The controller is tagged with "Custom Region APIs" for easy categorization in API
 * documentation.
 */
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

  /**
   * Creates a custom region with the given details.
   *
   * <p>This method processes a POST request to create a new custom region based on the details
   * provided in the request body.
   *
   * @param customRegionRequest The request body containing the details for the custom region to be
   *     created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created custom
   *     region details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Retrieves the details of a custom region by organization ID and custom region ID.
   *
   * <p>This method processes a GET request to fetch the details of a custom region identified by
   * the given organization ID and custom region ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the custom region.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched custom
   *     region details.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Updates a custom region with the given details.
   *
   * <p>This method processes a PUT request to update an existing custom region based on the details
   * provided in the request body.
   *
   * @param customRegionRequest The request body containing the details for the custom region to be
   *     updated.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated custom
   *     region details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Deletes a custom region by organization ID and custom region ID.
   *
   * <p>This method processes a DELETE request to remove a custom region identified by the given
   * organization ID and custom region ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param id The unique identifier of the custom region.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted custom region.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Deletes custom region geozones by organization ID and custom region ID.
   *
   * <p>This method processes a DELETE request to remove geozones of a custom region identified by
   * the given organization ID and custom region ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param deleteCustomRegionGeozonesRequest The request body containing the details for the
   *     geozones to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted custom region geozones.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Delete Custom Region",
      description = "Deletes the custom region by the organization ID and custom region ID.")
  @DeleteCustomRegionGeozones
  @DeleteMapping("/orgId/{orgId}/custom-region")
  public ResponseEntity<BaseResponse<CustomRegionResponse>> deleteCustomRegionGeoZones(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @RequestBody DeleteCustomRegionGeozonesRequest deleteCustomRegionGeozonesRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug(
        "Processing delete custom region request for id {} orgId {} and zipcode_prefixes {}",
        deleteCustomRegionGeozonesRequest.getId(),
        orgId,
        deleteCustomRegionGeozonesRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom region geozones deleted successfully.")
            .payload(
                customRegionService.deleteCustomRegionGeozones(
                    orgId,
                    deleteCustomRegionGeozonesRequest.getId(),
                    deleteCustomRegionGeozonesRequest))
            .build());
  }

  /**
   * Deletes custom region geozones based on the provided custom region request.
   *
   * <p>This method processes a DELETE request to remove geozones of a custom region based on the
   * details provided in the request body.
   *
   * @param customRegionRequest The request body containing the details for the custom region
   *     geozones to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted custom region geozones.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @DeleteMapping
  @DeleteCustomRegionGeozones
  public ResponseEntity<BaseResponse<CustomRegionResponse>> deleteCustomRegionByRequest(
      @RequestBody CustomRegionRequest customRegionRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug(
        "Processing delete custom region request based on CustomRegionRequest {}",
        customRegionRequest);
    DeleteCustomRegionGeozonesRequest deleteRequest =
        DeleteCustomRegionGeozonesRequest.builder().codes(customRegionRequest.getCodes()).build();
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom region geozones deleted successfully.")
            .payload(
                customRegionService.deleteCustomRegionGeozones(
                    customRegionRequest.getOrgId(), customRegionRequest.getId(), deleteRequest))
            .build());
  }

  /**
   * Retrieves a paginated list of custom regions for a given organization ID.
   *
   * <p>This method processes a GET request to fetch a paginated list of custom regions based on the
   * organization ID and provided pagination parameters.
   *
   * @param orgId The unique identifier of the organization.
   * @param pageParams The pagination parameters including page number, page size, sort by, and sort
   *     order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     custom regions.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Retrieves custom region information by organization ID and country.
   *
   * <p>This method processes a GET request to fetch custom region information based on the
   * organization ID, country, and optional custom region IDs and names.
   *
   * @param orgId The unique identifier of the organization.
   * @param country The unique identifier of the country.
   * @param customRegionIds Optional comma-separated values for unique identifiers of the custom
   *     regions.
   * @param customRegionNames Optional comma-separated values for custom region names.
   * @param pageParams The pagination parameters including page number, page size, sort by, and sort
   *     order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     custom region information.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetCustomRegionInfoDoc
  @GetMapping("/orgId/{orgId}/country/{country}")
  public ResponseEntity<BaseResponse<PagePayload<CustomRegionInfo>>> getCustomRegionInfo(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      @NotBlank(message = "country can't be empty")
          @Parameter(description = "Unique identifier of the Country.", example = "CA")
          @PathVariable
          String country,
      @RequestParam(required = false)
          @Parameter(
              description = "Comma Separated values for unique identifier of the custom region.",
              example = "CRID1,CRID2")
          String customRegionIds,
      @RequestParam(required = false)
          @Parameter(
              description = "Comma Separated values for custom region names.",
              example = "Manhattan North Area,Manhattan South Area")
          String customRegionNames,
      PageParams pageParams)
      throws CommonServiceException, PromiseEngineException {
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            orgId, country, customRegionIds, customRegionNames, pageParams);

    PagePayload<CustomRegionInfo> pagePayload =
        setCustomRegionInfoPagePayload(customRegionInfoPage, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Regions fetched successfully")
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

  private PagePayload<CustomRegionInfo> setCustomRegionInfoPagePayload(
      Page<CustomRegionInfo> customRegionInfoPage, PageParams pageParams, String orgId) {
    PagePayload<CustomRegionInfo> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) customRegionInfoPage.getTotalElements());
    pagination.setTotalPages(customRegionInfoPage.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse("customRegionId"));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            customRegionInfoPage.getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            customRegionInfoPage.getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(customRegionInfoPage.getContent());
    return pagePayload;
  }
}
