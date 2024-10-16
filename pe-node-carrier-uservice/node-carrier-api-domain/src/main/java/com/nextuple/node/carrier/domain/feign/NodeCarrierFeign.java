/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
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
public interface NodeCarrierFeign {

  @PostMapping("/node/carrier")
  BaseResponse<NodeCarrierResponse> createNodeCarrier(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest);

  @PutMapping("/node/carrier/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> updateNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption,
      @Valid @RequestBody NodeCarrierUpdateRequest nodeCarrierUpdateRequest);

  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> getNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @DeleteMapping("/node/carrier/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> deleteNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/{nodeId}/{orgId}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @PutMapping("/node/carrier/buffer")
  BaseResponse<NodeCarrierResponse> updateBuffer(
      @Valid @RequestBody NodeCarrierBufferRequest nodeCarrierBufferRequest);

  @PostMapping("/node/carrier/processing-lead-time")
  BaseResponse<NodeCarrierResponse> updateProcessingLeadTime(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest);

  @GetMapping("/node/carrier/node-carrier-selection/{orgId}/{serviceOption}/{destinationGeozone}")
  BaseResponse<List<NodeCarrierSelectionResponse>> getNodeCarrierSelectionDetails(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String serviceOption,
      @NotBlank @PathVariable String destinationGeozone);

  @PostMapping("/node/carrier/node-carrier-selection")
  BaseResponse<NodeCarrierSelectionResponse> addNodeCarrierSelectionPriority(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  @DeleteMapping("/node/carrier/{nodeId}/{orgId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @RequestParam String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/{nodeId}/{orgId}/{serviceOption}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListForServiceOption(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/get-all-cache-keys")
  BaseResponse<List<NodeCarrierListCacheKeyDto>> getNodeCarrierListCacheKeys(
      @NotNull @RequestParam Integer limit);

  @DeleteMapping("/node/carrier/node-carrier-selection")
  BaseResponse<NodeCarrierSelectionResponse> deleteNodeCarrierSelectionDetails(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  @GetMapping("/node/carrier/{orgId}/{nodeId}/carrier-service")
  BaseResponse<List<String>> getUniqueNodeCarrierServiceList(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);

  @GetMapping("/node/carrier/v1/{nodeId}/{orgId}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListWithLastPickUpTimeDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @GetMapping("/node/carrier/{orgId}/node-carriers")
  BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgId(
      @NotBlank @PathVariable String orgId);

  @GetMapping("/node/carrier/{orgId}/{carrierServiceId}/node-carriers")
  BaseResponse<List<NodeCarrierResponse>> getAllNodeCarriersByOrgIdCarrierServiceId(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String carrierServiceId);
}
