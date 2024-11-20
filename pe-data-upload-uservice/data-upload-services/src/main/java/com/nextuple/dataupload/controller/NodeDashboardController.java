/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetNodeTypesDoc;
import com.nextuple.dataupload.service.NodeDetailsService;
import com.nextuple.node.domain.outbound.NodeTypesResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/node")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Node Details UI APIs")
public class NodeDashboardController {

  private final NodeDetailsService nodeDetailsService;

  @GetMapping("/node-types/{orgId}")
  @GetNodeTypesDoc
  public ResponseEntity<BaseResponse<NodeTypesResponse>> getUniqueNodeTypes(
      @NotBlank(message = "OrgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId) {
    NodeTypesResponse nodeTypesResponse = nodeDetailsService.getNodeTypes(orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeTypesResponse)
            .message("Node types fetched successfully.")
            .build());
  }
}
