/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.CreateNodeServiceOptionBufferDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeServiceOptionBufferByIdDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeServiceOptionBufferDoc;
import com.nextuple.node.carrier.controller.docs.FetchApplicableNodeServiceOptionBuffersDoc;
import com.nextuple.node.carrier.controller.docs.FetchNodeServiceOptionBufferDoc;
import com.nextuple.node.carrier.controller.docs.GetBuffersByOrgIdAndNodeIdAndServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.UpdateNodeServiceOptionBufferDoc;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.service.NodeServiceOptionBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Node Service Option Buffer APIs")
@RequestMapping("/v2/node/service-option-buffer")
@RequiredArgsConstructor
@Slf4j
public class NodeServiceOptionBufferController {
  private final NodeServiceOptionBufferService nodeServiceOptionBufferService;

  @PostMapping
  @CreateNodeServiceOptionBufferDoc
  public ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>>
      createNodeServiceOptionBuffer(
          @Valid @RequestBody NodeServiceOptionBufferRequest nodeServiceOptionBufferRequest)
          throws CommonServiceException {
    log.debug(
        "Processing node service option buffer creation request {}",
        nodeServiceOptionBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer created successfully")
            .payload(
                nodeServiceOptionBufferService.createNodeServiceOptionBuffer(
                    nodeServiceOptionBufferRequest))
            .build());
  }

  @GetMapping("/{orgId}/{id}")
  @FetchNodeServiceOptionBufferDoc
  public ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> fetchNodeServiceOptionBuffer(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotNull(message = "Id can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ID,
              example = NodeCarrierConstants.ID_EXAMPLE)
          @PathVariable
          Long id)
      throws CommonServiceException {
    log.debug("Fetching node service option buffer by orgId {} and Id {} request", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer fetched successfully")
            .payload(nodeServiceOptionBufferService.fetchNodeServiceOptionBuffer(orgId, id))
            .build());
  }

  @GetMapping("/{orgId}/{nodeId}/{serviceOption}")
  @FetchApplicableNodeServiceOptionBuffersDoc
  public ResponseEntity<BaseResponse<List<NodeServiceOptionBufferResponse>>>
      fetchApplicableNodeServiceOptionBuffers(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "nodeId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "serviceOption can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @PathVariable
              String serviceOption,
          @Parameter(
                  description =
                      "Request date for getting the node service option buffers according to UTC time",
                  example = "2024-01-01")
              @NotNull(message = "Request date cannot be empty")
              @DateTimeFormat(pattern = "yyyy-MM-dd")
              @RequestParam
              LocalDate requestDate,
          @Parameter(description = "Horizon days to get buffers after request date", example = "7")
              @Min(value = 0, message = "horizonDays can't be negative")
              @NotNull(message = "Horizon days cannot be empty")
              @RequestParam
              Integer horizonDays)
          throws CommonServiceException {
    log.debug(
        "Fetching node service option buffer by orgId {} ,nodeId {}, serviceOption {}, requestDate {} and horizonDays {} request",
        orgId,
        nodeId,
        serviceOption,
        requestDate,
        horizonDays);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer fetched successfully")
            .payload(
                nodeServiceOptionBufferService.fetchApplicableNodeServiceOptionBuffers(
                    orgId, nodeId, serviceOption, requestDate, horizonDays))
            .build());
  }

  @PutMapping("/{orgId}/{id}")
  @UpdateNodeServiceOptionBufferDoc
  public ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>>
      updateNodeServiceOptionBuffer(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotNull(message = "Id can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ID,
                  example = NodeCarrierConstants.ID_EXAMPLE)
              @PathVariable
              Long id,
          @Valid @RequestBody NodeServiceOptionBufferUpdateRequest updateRequest)
          throws CommonServiceException {
    log.debug(
        "Processing node service option buffer update request by orgId {} and Id {}", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer updated successfully")
            .payload(
                nodeServiceOptionBufferService.updateNodeServiceOptionBuffer(
                    orgId, id, updateRequest))
            .build());
  }

  @DeleteMapping
  @DeleteNodeServiceOptionBufferDoc
  public ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>>
      deleteNodeServiceOptionBuffer(
          @Valid @RequestBody
              NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
          throws CommonServiceException {
    log.debug(
        "Processing node service option buffer delete request {}",
        nodeServiceOptionBufferDeleteRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer deleted successfully")
            .payload(
                nodeServiceOptionBufferService.deleteNodeServiceOptionBuffer(
                    nodeServiceOptionBufferDeleteRequest))
            .build());
  }

  @DeleteMapping("/{orgId}/{id}")
  @DeleteNodeServiceOptionBufferByIdDoc
  public ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>>
      deleteNodeServiceOptionBuffer(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotNull(message = "Id can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ID,
                  example = NodeCarrierConstants.ID_EXAMPLE)
              @PathVariable
              Long id)
          throws CommonServiceException {
    log.debug(
        "Processing node service option buffer delete request by orgId {} and Id {}", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node service option buffer deleted successfully")
            .payload(
                nodeServiceOptionBufferService.deleteNodeServiceOptionBufferByOrgIdAndId(orgId, id))
            .build());
  }

  @GetBuffersByOrgIdAndNodeIdAndServiceOptionDoc
  @GetMapping("/list/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeServiceOptionBufferResponse>>>
      getBuffersByOrgIdAndNodeIdAndServiceOption(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "nodeId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "serviceOption can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @RequestParam
              String serviceOption)
          throws CommonServiceException {
    log.debug("Processing get node service option buffers for orgId, nodeId and serviceOption");
    List<NodeServiceOptionBufferResponse> nodeServiceOptionBufferResponses =
        nodeServiceOptionBufferService.getBuffersByOrgIdAndNodeIdAndServiceOption(
            orgId, nodeId, serviceOption);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Service option buffer list fetched successfully")
            .payload(nodeServiceOptionBufferResponses)
            .build());
  }
}
