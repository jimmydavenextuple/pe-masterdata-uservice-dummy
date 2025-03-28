/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface TransferScheduleFeignImpl
    extends GenericFeignService<String, BaseResponse<List<TransferScheduleRangeResponse>>> {
  @GetMapping("/transfer-schedule/get")
  BaseResponse<List<TransferScheduleRangeResponse>> get(String request);

  @GetMapping("/transfer-schedule/orgId/{orgId}/dropoffNodeId/{dropoffNodeId}")
  BaseResponse<List<TransferScheduleResponse>> fetchTransferSchedules(
      @PathVariable String orgId, @PathVariable String dropoffNodeId);

  @PostMapping("/transfer-schedule/time-range")
  BaseResponse<List<TransferScheduleRangeResponse>> fetchTransferSchedulesInRange(
      @RequestBody TransferScheduleRangeRequest transferScheduleRangeRequest);
}
