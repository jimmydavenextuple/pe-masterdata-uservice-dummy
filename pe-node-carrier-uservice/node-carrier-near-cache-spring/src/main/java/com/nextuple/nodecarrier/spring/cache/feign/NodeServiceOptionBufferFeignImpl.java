/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.nodecarrier.spring.cache.feign;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-node-carrier-uservice",
    url = "${spring.application.dependencies.node-carrier:http://pe-node-carrier-uservice:8080/}")
public interface NodeServiceOptionBufferFeignImpl
    extends GenericFeignService<String, BaseResponse<List<NodeServiceOptionBufferResponse>>> {

  @GetMapping("v2/node/service-option-buffer/get")
  BaseResponse<List<NodeServiceOptionBufferResponse>> get(String request);

  @GetMapping("v2/node/service-option-buffer/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<List<NodeServiceOptionBufferResponse>> fetchApplicableNodeServiceOptionBuffers(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption,
      @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate requestDate,
      @Min(value = 0) @NotNull @RequestParam Integer horizonDays)
      throws CommonServiceException;
}
