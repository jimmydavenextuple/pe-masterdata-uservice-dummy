/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface TransferScheduleFeign {

  @PostMapping("/transfer-schedule")
  BaseResponse<TransferScheduleResponse> createTransferSchedule(
      @RequestBody TransferScheduleCreationRequest tranferScheduleRequest);

  @GetMapping("/transfer-schedule/orgId/{orgId}/dropoffNodeId/{dropoffNodeId}")
  BaseResponse<List<TransferScheduleResponse>> getTransferSchedules(
      @PathVariable("orgId") String orgId,
      @PathVariable(name = "dropoffNodeId") String dropoffNodeId);

  @PostMapping("/transfer-schedule/time-range")
  BaseResponse<List<TransferScheduleResponse>> fetchTransferSchedulesInRange(
      @RequestBody TransferScheduleRangeRequest transferScheduleRangeRequest);

  @DeleteMapping("/transfer-schedule")
  BaseResponse<TransferScheduleResponse> deleteTransferSchedule(
      @RequestBody TransferScheduleRequest request);

  @PostMapping(path = "/transfer-schedule/orgId/{orgId}")
  BaseResponse<PagePayload<TransferScheduleResponse>> fetchTransferSchedule(
      @PathVariable @NotBlank String orgId,
      @RequestParam Boolean isPaginated,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder,
      @Valid @RequestBody FetchTransferScheduleRequest request);
}
