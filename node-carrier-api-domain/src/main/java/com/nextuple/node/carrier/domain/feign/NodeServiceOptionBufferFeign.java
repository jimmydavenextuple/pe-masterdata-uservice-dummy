/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-node-carrier-uservice",
    url = "${spring.application.dependencies.node-carrier:http://pe-node-carrier-uservice:8080/}")
public interface NodeServiceOptionBufferFeign {
  @PostMapping("/v2/node/service-option-buffer")
  BaseResponse<NodeServiceOptionBufferResponse> createNodeServiceOptionBuffer(
      @Valid @RequestBody NodeServiceOptionBufferRequest nodeServiceOptionBufferRequest);

  @GetMapping("/v2/node/service-option-buffer/{orgId}/{id}")
  BaseResponse<NodeServiceOptionBufferResponse> fetchNodeServiceOptionBuffer(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long id);

  @GetMapping("/v2/node/service-option-buffer/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<List<NodeServiceOptionBufferResponse>> fetchApplicableNodeServiceOptionBuffers(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption,
      @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate requestDate,
      @Min(value = 0) @NotNull @RequestParam Integer horizonDays);

  @PutMapping("/v2/node/service-option-buffer/{orgId}/{id}")
  BaseResponse<NodeServiceOptionBufferResponse> updateNodeServiceOptionBuffer(
      @NotBlank @PathVariable String orgId,
      @NotNull @PathVariable Long id,
      @Valid @RequestBody NodeServiceOptionBufferUpdateRequest updateRequest);

  @DeleteMapping("/v2/node/service-option-buffer")
  BaseResponse<NodeServiceOptionBufferResponse> deleteNodeServiceOptionBuffer(
      @Valid @RequestBody
          NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest);

  @DeleteMapping("/v2/node/service-option-buffer/{orgId}/{id}")
  BaseResponse<NodeServiceOptionBufferResponse> deleteNodeServiceOptionBuffer(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long id);

  @GetMapping("/v2/node/service-option-buffer/list/{orgId}/{nodeId}")
  BaseResponse<List<NodeServiceOptionBufferResponse>> getBuffersByOrgIdAndNodeIdAndServiceOption(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @RequestParam String serviceOption);
}
