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

@Validated
@RestController
@RequestMapping("transfer-schedule/ui")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Transfer Schedules Details APIs")
public class TransferSchedulesController {
  private final TransferScheduleService transferScheduleService;
  public static final String TRANSFER_SCHEDULE_DEFAULT_SORT_BY = "sourceNodeId";

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

  @PostMapping(
      path = "v2/orgId/{orgId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetTransferSchedulesListDocV2
  public ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>>
      getTransferSchedulesListV2(
          @NotBlank(message = "OrgId can't be empty")
              @PathVariable
              @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @RequestParam(required = false, defaultValue = "true") Boolean isPaginated,
          @RequestParam(required = false, defaultValue = "1") Integer pageNo,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize,
          @RequestParam(required = false, defaultValue = TRANSFER_SCHEDULE_DEFAULT_SORT_BY)
              String sortBy,
          @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
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
