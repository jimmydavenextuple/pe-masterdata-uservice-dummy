/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.controller.docs.GetProcessingTimeBufferDoc;
import com.nextuple.dataupload.controller.docs.GetProcessingTimeBufferDocV1;
import com.nextuple.dataupload.service.ProcessingTimeBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Processing Time Buffer APIs.
 *
 * <p>This controller provides APIs to interact with the processing time buffer configurations for
 * specific organizations and nodes. It includes methods to retrieve paginated lists of processing
 * time buffer details with support for filtering by organization and node IDs.
 *
 * <p>The controller is tagged with "Processing Time Buffer APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/ui/processing-time-buffer")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Processing Time Buffer APIs")
public class ProcessingTimeBufferController {

  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT_UI
          + "/processing-time-buffer/orgId/%s?pageNo=%d&pageSize=%d";
  public static final String PAGINATION_NEXT_CONSTANT = "next";
  public static final String PAGINATION_PREVIOUS_CONSTANT = "previous";
  private final PageProperties pageProperties;

  private final ProcessingTimeBufferService processingTimeBufferService;

  /**
   * Retrieves a paginated list of processing time buffer details for the specified organization.
   *
   * <p>This method processes a GET request to fetch a paginated list of processing time buffer
   * details based on the provided organization ID. Pagination and sorting are supported through the
   * `pageParams` parameter.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param pageParams The pagination parameters, including page number, page size, sorting
   *     criteria, and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     processing time buffer details.
   * @throws CommonServiceException If there is an error while fetching processing time buffer
   *     details.
   */
  @GetMapping("/orgId/{orgId}")
  @GetProcessingTimeBufferDoc
  public ResponseEntity<BaseResponse<PagePayload<ProcessingTimeBufferResponse>>>
      getProcessingTimeBufferDetails(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = "Unique identifier of organization")
              @PathVariable
              String orgId,
          PageParams pageParams)
          throws CommonServiceException {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        processingTimeBufferService.getProcessingTimeBuffers(orgId, null, pageParams);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_NEXT_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_PREVIOUS_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    processingTimeBufferDtoPagePayload.getPagination().setNext(nextUri);
    processingTimeBufferDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing Time Buffer list fetched successfully")
            .payload(processingTimeBufferDtoPagePayload)
            .build());
  }

  /**
   * Retrieves a paginated list of processing time buffer details for the specified organization and
   * nodes.
   *
   * <p>This method processes a GET request to fetch a paginated list of processing time buffer
   * details based on the provided organization ID and optional node IDs. Pagination and sorting are
   * supported through the `pageParams` parameter.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param nodeIds A comma-separated list of node IDs to filter the results (optional).
   * @param pageParams The pagination parameters, including page number, page size, sorting
   *     criteria, and sort order.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a paginated list of
   *     processing time buffer details.
   * @throws CommonServiceException If there is an error while fetching processing time buffer
   *     details.
   */
  @GetMapping("/v1/orgId/{orgId}")
  @GetProcessingTimeBufferDocV1
  public ResponseEntity<BaseResponse<PagePayload<ProcessingTimeBufferResponse>>>
      getProcessingTimeBufferV1(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(description = "Unique identifier of organization")
              @PathVariable
              String orgId,
          @RequestParam(required = false)
              @Parameter(
                  description =
                      "Comma separated string that contains references of the nodes to be searched for.")
              String nodeIds,
          PageParams pageParams)
          throws CommonServiceException {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        processingTimeBufferService.getProcessingTimeBuffers(orgId, nodeIds, pageParams);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_NEXT_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            processingTimeBufferDtoPagePayload.getPagination().getTotalPages(),
            PAGINATION_PREVIOUS_CONSTANT,
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));

    processingTimeBufferDtoPagePayload.getPagination().setNext(nextUri);
    processingTimeBufferDtoPagePayload.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing Time Buffer list fetched successfully")
            .payload(processingTimeBufferDtoPagePayload)
            .build());
  }
}
