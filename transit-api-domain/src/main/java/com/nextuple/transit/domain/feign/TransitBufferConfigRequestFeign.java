/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface TransitBufferConfigRequestFeign {

  @PostMapping("/transit/buffer-config-request")
  BaseResponse<TransitBufferConfigResponse> processTransitBufferConfigRequest(
      @Valid @RequestBody TransitBufferConfigRequest transitBufferConfigRequest);

  @PutMapping("/transit/buffer-config-request/update-status/{id}")
  BaseResponse<TransitBufferConfigResponse> updateTransitBufferConfigRequestStatus(
      @PathVariable Long id, @RequestParam TransitBufferConfigRequestStatusEnum status);

  @GetMapping("/transit/buffer-config-request/{orgId}")
  BaseResponse<List<TransitBufferConfigResponse>> getTransitBufferConfigRequests(
      @PathVariable String orgId, @RequestParam String carrierServiceId);

  @DeleteMapping("/transit/buffer-config-request")
  BaseResponse<TransitBufferConfigResponse> deleteTransitBufferConfigRequest(
      @RequestParam Long transitBufferRequestId, @RequestParam String createdBy);
}
