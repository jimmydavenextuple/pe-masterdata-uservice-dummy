/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.GenericPaginatedTableResponse;
import com.nextuple.dataupload.controller.docs.GetTransferSchedulesListDoc;
import com.nextuple.dataupload.controller.docs.GetTransferSchedulesListDocV2;
import com.nextuple.dataupload.service.TransferScheduleService;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing transfer schedules.
 *
 * <p>This controller provides APIs to retrieve and manage transfer schedules for organizations. It
 * supports paginated queries with sorting capabilities and includes versioned endpoints for
 * enhanced functionality.
 *
 * <p>Key features include:
 *
 * <ul>
 *   <li>Paginated retrieval of transfer schedules
 *   <li>Customizable sorting options
 *   <li>Version 2 API with additional pagination controls
 *   <li>Support for complex transfer schedule queries
 * </ul>
 *
 * @see TransferScheduleService
 * @see TransferScheduleResponse
 * @see FetchTransferScheduleRequest
 */
@Validated
@RestController
@RequestMapping("transfer-schedule/ui")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Transfer Schedules Details APIs")
public class TransferSchedulesController {
  private final TransferScheduleService transferScheduleService;
  public static final String TRANSFER_SCHEDULE_DEFAULT_SORT_BY = "sourceNodeId";

  /**
   * Retrieves a paginated list of all transfer schedules for the specified organization.
   *
   * <p>This endpoint processes a POST request to fetch transfer schedules based on the provided
   * criteria. The results are paginated and can be sorted according to the specified parameters.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param pageNo The page number to retrieve (default: 1)
   * @param pageSize The number of records per page (default: 10)
   * @param sortBy The field to sort by (default: sourceNodeId)
   * @param sortOrder The sort direction, either "ASC" or "DESC" (default: ASC)
   * @param request The {@link FetchTransferScheduleRequest} containing filter criteria
   * @return A {@link ResponseEntity} containing a paginated list of transfer schedules
   */
  @PostMapping(
      path = "/orgId/{orgId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetTransferSchedulesListDoc
  public ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>>
      getTransferSchedulesList(
          @NotBlank(message = "OrgId can't be empty")
              @PathVariable
              @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @RequestParam(required = false, defaultValue = "1") Integer pageNo,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize,
          @RequestParam(required = false, defaultValue = TRANSFER_SCHEDULE_DEFAULT_SORT_BY)
              String sortBy,
          @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
          @RequestBody @Valid FetchTransferScheduleRequest request) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(pageNo));
    pageParams.setPageSize(Optional.of(pageSize));
    pageParams.setSortBy(Optional.of(sortBy));
    pageParams.setSortOrder(Optional.of(sortOrder));

    PagePayload<TransferScheduleResponse> transferScheduleDtoPage =
        transferScheduleService.getTransferScheduleList(orgId, pageParams, request);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer schedules list fetched successfully")
            .payload(transferScheduleDtoPage)
            .build());
  }

  /**
   * Version 2 of the transfer schedules list retrieval endpoint with enhanced filtering based on
   * transfer schedule rules.
   *
   * <p>This endpoint provides additional functionality over v1, including the ability to show
   * transfer schedule which are either not associated to any rules or associated to rule with
   * active transfer schedule rule definition.
   *
   * @param orgId The unique identifier for the organization. Must not be blank.
   * @param isPaginated Flag to enable/disable pagination (default: true)
   * @param pageNo The page number to retrieve (default: 1)
   * @param pageSize The number of records per page (default: 10)
   * @param sortBy The field to sort by (default: sourceNodeId)
   * @param sortOrder The sort direction, either "ASC" or "DESC" (default: ASC)
   * @param request The {@link FetchTransferScheduleRequest} containing filter criteria
   * @return A {@link ResponseEntity} containing a paginated or complete list of transfer schedules
   * @throws CommonServiceException If there is an error processing the request
   */
  @PostMapping(
      path = "v2/orgId/{orgId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetTransferSchedulesListDocV2
  public ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>>
      getTransferSchedulesListV2(
          @NotBlank(message = "OrgId can't be empty")
              @PathVariable
              @Parameter(description = "Unique identifier for the organization.")
              String orgId,
          @RequestParam(required = false, defaultValue = "true")
              @Parameter(description = "Indicates whether pagination should be applied.")
              Boolean isPaginated,
          @RequestParam(required = false, defaultValue = "1")
              @Parameter(description = "Page number for pagination.")
              Integer pageNo,
          @RequestParam(required = false, defaultValue = "10")
              @Parameter(description = "Number of records per page.")
              Integer pageSize,
          @RequestParam(required = false, defaultValue = TRANSFER_SCHEDULE_DEFAULT_SORT_BY)
              @Parameter(description = "The parameter by which the results should be sorted.")
              String sortBy,
          @RequestParam(required = false, defaultValue = "ASC")
              @Parameter(
                  description =
                      "The sorting order of the results—either ascending (ASC) or descending (DESC).")
              String sortOrder,
          @RequestBody @Valid FetchTransferScheduleRequest request)
          throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(pageNo));
    pageParams.setPageSize(Optional.of(pageSize));
    pageParams.setSortBy(Optional.of(sortBy));
    pageParams.setSortOrder(Optional.of(sortOrder));

    GenericPaginatedTableResponse transferScheduleDtoPage =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPaginated);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer schedules list fetched successfully")
            .payload(transferScheduleDtoPage)
            .build());
  }
}
