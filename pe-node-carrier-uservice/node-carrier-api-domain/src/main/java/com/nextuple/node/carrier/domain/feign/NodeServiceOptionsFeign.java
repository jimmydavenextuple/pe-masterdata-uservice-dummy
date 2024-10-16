/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-node-carrier-uservice",
    url = "${spring.application.dependencies.node-carrier:http://pe-node-carrier-uservice:8080/}")
public interface NodeServiceOptionsFeign {

  @PostMapping("/v2/node/service-option")
  BaseResponse<NodeServiceOptionResponse> createNodeServiceOption(
      @Valid @RequestBody NodeServiceOptionRequest nodeServiceOptionRequest);

  @PutMapping("/v2/node/service-option/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<NodeServiceOptionResponse> updateNodeServiceOption(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption,
      @Valid @RequestBody NodeServiceOptionUpdateRequest nodeServiceOptionUpdateRequest);

  @GetMapping("/v2/node/service-option/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<NodeServiceOptionResponse> getNodeServiceOption(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption);

  @DeleteMapping("/v2/node/service-option/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<NodeServiceOptionResponse> deleteNodeServiceOption(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/v2/node/service-option/{orgId}/{nodeId}")
  BaseResponse<List<NodeServiceOptionResponse>> getNodeServiceOptionsList(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);
}
