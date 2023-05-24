/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.domain.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-node-uservice",
    url = "${spring.application.dependencies.node:http://pe-node-uservice:8080/}")
public interface NodeFeign {

  @PostMapping("/node")
  BaseResponse<NodeResponse> createNode(@Valid @RequestBody NodeRequest nodeRequest);

  @PutMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> updateNodeDetails(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody NodeUpdationRequest nodeUpdationRequest);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> getNodeDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @DeleteMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> deleteNode(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @GetMapping("/node/{orgId}")
  BaseResponse<PagePayload<NodeDto>> getNodeList(
      @NotBlank @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/node/all-nodes")
  BaseResponse<PagePayload<NodeResponse>> getAllNodesList(
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/node/get-all-cache-keys")
  BaseResponse<List<NodeCacheKeyDto>> getNodeCacheKeys(@NotNull @RequestParam Integer limit);
}
