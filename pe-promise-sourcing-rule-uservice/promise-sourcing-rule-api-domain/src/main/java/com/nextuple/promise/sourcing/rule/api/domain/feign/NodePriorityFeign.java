/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NodePriorityUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodePriorityResponse;
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
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
public interface NodePriorityFeign {

  @PostMapping("/node-priority")
  BaseResponse<NodePriorityResponse> addNodePriorityToNodeGroup(
      @Valid @RequestBody NodePriorityRequest nodePriorityRequest);

  @GetMapping("/node-priority/list/orgId/{orgId}/nodeGroupId/{nodeGroupId}")
  BaseResponse<NodeGroupDetailsResponse> getNodesAssociatedToANodeGroup(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long nodeGroupId);

  @GetMapping("/node-priority/orgId/{orgId}/id/{id}")
  BaseResponse<NodePriorityResponse> getNodePriorityDetailsByOrgIdAndId(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long id);

  @GetMapping("/node-priority/orgId/{orgId}/nodeId/{nodeId}")
  BaseResponse<List<NodePriorityResponse>> getNodePriorityDetailsByOrgIdAndNodeId(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);

  @PutMapping("/node-priority")
  BaseResponse<NodePriorityResponse> updatePriorityWithinNodeGroup(
      @NotBlank @RequestParam String orgId,
      @NotNull @RequestParam Long nodeGroupId,
      @NotBlank @RequestParam String nodeId,
      @Valid @RequestBody NodePriorityUpdationRequest nodePriorityUpdationRequest);

  @DeleteMapping("/node-priority")
  BaseResponse<NodePriorityResponse> removeNodeFromNodeGroup(
      @NotBlank @RequestParam String orgId,
      @NotNull @RequestParam Long nodeGroupId,
      @NotBlank @RequestParam String nodeId);

  @PutMapping("/node-priority/orgId/{orgId}/nodeGroupName/{nodeGroupName}")
  BaseResponse<NodeGroupWithNodesResponse> updateNodesWithinNodeGroup(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String nodeGroupName,
      @Valid @RequestBody NodeGroupWithNodesResponse nodeGroupWithNodes);
}
