/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    name = "pe-node-carrier-uservice",
    url = "${spring.application.dependencies.node-carrier:http://pe-node-carrier-uservice:8080/}")
public interface NodeCarriersFeign {

  @PostMapping("/v2/node/carrier")
  BaseResponse<NodeCarriersResponse> createNodeCarrier(
      @Valid @RequestBody NodeCarriersRequest nodeCarriersRequest);

  @GetMapping("/v2/node/carrier/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarriersResponse> getNodeCarrier(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @PutMapping("/v2/node/carrier/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarriersResponse> updateNodeCarrier(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption,
      @Valid @RequestBody NodeCarriersUpdateRequest nodeCarriersUpdateRequest);

  @DeleteMapping("/v2/node/carrier/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarriersResponse> deleteNodeCarrier(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/v2/node/carrier/{orgId}/{nodeId}")
  BaseResponse<List<NodeCarriersResponse>> getNodeCarriersList(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);

  @GetMapping("/v2/node/carrier/{orgId}/{nodeId}/carrier-service")
  BaseResponse<List<String>> getUniqueNodeCarrierServiceList(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);

  @GetMapping("/v2/node/carrier/{orgId}/{nodeId}/{serviceOption}")
  BaseResponse<List<NodeCarriersResponse>> getNodeCarriersList(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/v2/node/carrier/get-all-cache-keys")
  BaseResponse<List<NodeCarrierListCacheKeyDto>> getNodeCarriersCacheKeys(
      @NotNull @RequestParam Integer limit);

  @GetMapping("/v2/node/carrier/org/{orgId}/carrier-service/{carrierServiceId}")
  BaseResponse<List<NodeCarriersResponse>> getAllNodeCarriersByOrgIdCarrierServiceId(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String carrierServiceId);

  @GetMapping("/v2/node/carrier/org/{orgId}/nodeId/{nodeId}/carrier-service/{carrierServiceId}")
  BaseResponse<List<NodeCarriersResponse>> getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String carrierServiceId);
}
