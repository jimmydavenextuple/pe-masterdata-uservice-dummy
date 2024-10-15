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
import com.nextuple.transit.controller.docs.CreateTransferScheduleDoc;
import com.nextuple.transit.controller.docs.DeleteTransferScheduleDoc;
import com.nextuple.transit.controller.docs.FetchTransferScheduleListDoc;
import com.nextuple.transit.controller.docs.GetTransferScheduleDoc;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
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

@Validated
@RestController
@RequestMapping("/transfer-schedule")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transfer Schedule APIs")
public class TransferScheduleController {
  private final TransferScheduleService transferScheduleService;
  private final PageProperties pageProperties;

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
