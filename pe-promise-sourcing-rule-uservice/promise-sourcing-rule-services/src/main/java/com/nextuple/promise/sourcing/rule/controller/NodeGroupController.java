/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateNodeGroupRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllNodeGroupsByOrgIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetNodeGroupDetailsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateNodeGroupDoc;
import com.nextuple.promise.sourcing.rule.persistence.domain.NodeGroupDomainDto;
import com.nextuple.promise.sourcing.rule.service.NodeGroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/node-group")
@RequiredArgsConstructor
@Tag(name = "Node Group APIs")
public class NodeGroupController {

  private static final Logger logger = LoggerFactory.getLogger(NodeGroupController.class);

  private final NodeGroupService nodeGroupService;

  @CreateNodeGroupDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> createNodeGroup(
      @Valid @RequestBody CreateNodeGroupRequest nodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Node Group request");
    try {
      var nodeGroupResponse = nodeGroupService.createNodeGroup(nodeGroupRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group successfully created!")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create node group request", e);
      throw e;
    }
  }

  @GetNodeGroupDetailsDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> getNodeGroupByIdandOrgId(
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier of the node group.", example = "1")
          Long id,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get node group by id request");
    try {
      var nodeGroupResponse = nodeGroupService.fetchNodeGroupByIdandOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node group successfully fetched")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node group by id request", e);
      throw e;
    }
  }

  @UpdateNodeGroupDoc
  @PutMapping(
      value = "/orgId/{orgId}/id/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NodeGroupResponse>> updateNodeGroup(
      @NotNull(message = "id can't be null")
          @Min(value = 0)
          @PathVariable
          @Parameter(description = "Unique identifier for the node group", example = "1")
          Long id,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody UpdateNodeGroupRequest updateNodeGroupRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update node group request");
    try {
      var nodeGroupResponse = nodeGroupService.updateNodeGroup(id, orgId, updateNodeGroupRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group Details successfully updated")
              .payload(nodeGroupResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update node group request", e);
      throw e;
    }
  }

  @GetMapping(value = "/orgId/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetAllNodeGroupsByOrgIdDoc
  public ResponseEntity<BaseResponse<List<NodeGroupDomainDto>>> getNodeGroupListByOrgId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    try {
      var nodeGroupList = nodeGroupService.getNodeGroupListForOrgId(orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Group List fetched successfully")
              .payload(nodeGroupList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get node group list request", e);
      throw e;
    }
  }
}
