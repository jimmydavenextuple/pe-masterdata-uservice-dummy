/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import static com.nextuple.transit.utils.DashboardUtil.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.*;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleBatchRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleBatchResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.service.TransferScheduleService;
import com.nextuple.transit.utils.DashboardUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Transfer Schedules in the transit system.
 *
 * <p>This controller provides APIs for creating, updating, fetching, and deleting transfer
 * schedules. It allows users to manage transfer schedules and their configurations within the
 * transit system, including handling operations based on provided transfer schedule requests, such
 * as creating and updating transfer schedules, retrieving transfer schedule details, and deleting
 * outdated transfer schedules.
 *
 * <p>The controller is tagged with "Transfer Schedule APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/transfer-schedule")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transfer Schedule APIs")
public class TransferScheduleController {
  private final TransferScheduleService transferScheduleService;
  private final PageProperties pageProperties;

  /**
   * Creates a new transfer schedule in the transit system.
   *
   * <p>This method processes a POST request to create a new transfer schedule based on the provided
   * request details. It validates the input request and delegates the creation process to the
   * `TransferScheduleService`.
   *
   * @param tranferScheduleRequest The request object containing the details of the transfer
   *     schedule to be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the created transfer schedule details.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     creation process.
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @CreateTransferScheduleDoc
  public ResponseEntity<BaseResponse<TransferScheduleResponse>> createTransferSchedule(
      @Valid @RequestBody TransferScheduleCreationRequest tranferScheduleRequest)
      throws CommonServiceException, PromiseEngineException {
    var response = transferScheduleService.createTransferSchedule(tranferScheduleRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer Schedule created successfully.")
            .payload(response)
            .build());
  }

  /**
   * Processes a batch of transfer schedules for a specific organization.
   *
   * <p>This method processes a POST request to handle a batch of transfer schedules based on the
   * provided request details. It validates the input request and delegates the batch processing to
   * the `TransferScheduleService`.
   *
   * @param transferScheduleBatchRequest The request object containing the details of the transfer
   *     schedules to be processed in batch.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the batch processing result.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     batch processing.
   */
  @PostMapping("/batch/orgId/{orgId}")
  @BatchTransferScheduleDoc
  public ResponseEntity<BaseResponse<TransferScheduleBatchResponse>> batchTransferSchedules(
      @RequestBody TransferScheduleBatchRequest transferScheduleBatchRequest,
      @PathVariable("orgId") String orgId)
      throws PromiseEngineException {
    TransferScheduleBatchResponse response =
        transferScheduleService.batchTransferSchedules(transferScheduleBatchRequest, orgId);
    if (response.getFailureCount() > 0) {
      return new ResponseEntity<>(
          BaseResponse.builder()
              .message("Batch processing completed with a few failures.")
              .payload(response)
              .build(),
          HttpStatus.MULTI_STATUS);
    }

    return ResponseEntity.ok(
        BaseResponse.builder().message("Batch processing completed.").payload(response).build());
  }

  /**
   * Retrieves transfer schedules for a specific organization and dropoff node.
   *
   * <p>This method processes a GET request to fetch transfer schedules based on the provided
   * organization ID and dropoff node ID. It delegates the retrieval process to the
   * `TransferScheduleService`.
   *
   * @param orgId The unique identifier of the organization.
   * @param dropoffNodeId The unique identifier of the dropoff node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of transfer schedules.
   */
  @GetMapping(
      path = "/orgId/{orgId}/dropoffNodeId/{dropoffNodeId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetTransferScheduleDoc
  public ResponseEntity<BaseResponse<List<TransferScheduleResponse>>> getTransferSchedules(
      @PathVariable("orgId") String orgId,
      @PathVariable(name = "dropoffNodeId") String dropoffNodeId) {
    List<TransferScheduleResponse> transferScheduleResponses =
        transferScheduleService.fetchTransferSchedules(orgId, dropoffNodeId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer Schedules fetched successfully")
            .payload(transferScheduleResponses)
            .build());
  }

  /**
   * Retrieves transfer schedules within a specified time range.
   *
   * <p>This method processes a POST request to fetch transfer schedules based on the provided time
   * range request details. It delegates the retrieval process to the `TransferScheduleService`.
   *
   * @param transferScheduleRangeRequest The request object containing the time range details for
   *     fetching transfer schedules.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of transfer schedules within the specified time range.
   */
  @PostMapping(path = "/time-range", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetTransferScheduleDoc
  public ResponseEntity<BaseResponse<List<TransferScheduleRangeResponse>>>
      getTransferSchedulesInRange(
          @RequestBody TransferScheduleRangeRequest transferScheduleRangeRequest) {
    List<TransferScheduleRangeResponse> transferScheduleResponses =
        transferScheduleService.fetchTransferSchedulesInRange(transferScheduleRangeRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer Schedules fetched successfully")
            .payload(transferScheduleResponses)
            .build());
  }

  /**
   * Deletes a transfer schedule based on the provided request details.
   *
   * <p>This method processes a DELETE request to remove a transfer schedule based on the provided
   * request details. It delegates the deletion process to the `TransferScheduleService`.
   *
   * @param request The request object containing the details of the transfer schedule to be
   *     deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the deleted transfer schedule details.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     deletion process.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @DeleteTransferScheduleDoc
  public ResponseEntity<BaseResponse<TransferScheduleResponse>> deleteTransferSchedule(
      @RequestBody @Valid TransferScheduleRequest request)
      throws PromiseEngineException, CommonServiceException {
    TransferScheduleResponse response = transferScheduleService.deleteTransferSchedule(request);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer schedule deleted successfully")
            .payload(response)
            .build());
  }

  /**
   * Fetches a paginated list of transfer schedules based on the provided request details.
   *
   * <p>This method processes a POST request to retrieve a paginated list of transfer schedules
   * based on the provided request details. It delegates the retrieval process to the
   * `TransferScheduleService`.
   *
   * @param orgId The unique identifier of the organization.
   * @param isPaginated Indicates whether the response should be paginated or not.
   * @param pageNo The page number for pagination (default: 1).
   * @param pageSize The number of items per page for pagination (default: 10).
   * @param sortBy The field by which to sort the results (default: "createdAt").
   * @param sortOrder The order in which to sort the results (default: "ASC").
   * @param request The request object containing the details for fetching transfer schedules.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the paginated list of transfer schedules.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   * @throws PromiseEngineException If an error occurs related to the promise engine during the
   *     retrieval process.
   */
  @PostMapping(
      path = "/orgId/{orgId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @FetchTransferScheduleListDoc
  public ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>>
      fetchTransferScheduleList(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              @PathVariable
              String orgId,
          @RequestParam(required = false, defaultValue = "true") Boolean isPaginated,
          @RequestParam(required = false, defaultValue = "1") Integer pageNo,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize,
          @RequestParam(required = false, defaultValue = TRANSFER_SCHEDULE_DEFAULT_SORT_BY)
              String sortBy,
          @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
          @RequestBody @Valid FetchTransferScheduleRequest request)
          throws CommonServiceException, PromiseEngineException {
    log.debug("Processing get all transfer schedule");

    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(pageNo));
    pageParams.setPageSize(Optional.of(pageSize));
    pageParams.setSortBy(Optional.of(sortBy));
    pageParams.setSortOrder(Optional.of(sortOrder));

    Page<TransferScheduleResponse> transferScheduleDtoPage =
        transferScheduleService.fetchTransferScheduleList(orgId, isPaginated, pageParams, request);

    PagePayload<TransferScheduleResponse> pagePayload =
        DashboardUtil.setPagePayload(
            transferScheduleDtoPage,
            pageParams,
            orgId,
            pageProperties,
            TRANSFER_SCHEDULE_DEFAULT_SORT_BY);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transfer Schedule details fetched successfully")
            .payload(pagePayload)
            .build());
  }
}
