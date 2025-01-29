/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.*;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Node Service Option Buffers.
 *
 * <p>This controller provides APIs for creating, fetching, updating, and retrieving applicable Node
 * Service Option Buffers based on various parameters. It includes endpoints for creating a new
 * buffer, fetching details of a specific buffer, fetching applicable buffers, and updating an
 * existing buffer. This is the version 2 of the Node Service Option Buffer API.
 *
 * <p>All API endpoints are tagged with "Node Service Option Buffer APIs" for easy categorization in
 * API documentation.
 *
 * <p>Each API endpoint is documented with details about its purpose, request parameters, response
 * structure, and any exceptions that may be thrown.
 */
@Validated
@RestController
@Tag(name = "Node Service Option Buffer APIs")
@RequestMapping("/v2/node/service-option-buffer")
@RequiredArgsConstructor
@Slf4j
public class NodeServiceOptionBufferController {
  private final NodeServiceOptionBufferService nodeServiceOptionBufferService;

  /**
   * Creates a new Node Service Option Buffer based on the provided request data.
   *
   * <p>This endpoint processes the creation of a Node Service Option Buffer, which holds specific
   * configuration data related to service options for nodes.
   *
   * @param nodeServiceOptionBufferRequest the request body containing the details of the Node
   *     Service Option Buffer to be created. It must be a valid object.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the response details of
   *     the created Node Service Option Buffer.
   * @throws CommonServiceException if an error occurs while processing the creation request.
   */
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

  /**
   * Fetches the Node Service Option Buffer for the specified organization and ID.
   *
   * <p>This endpoint retrieves a Node Service Option Buffer based on the provided organization ID
   * and buffer ID.
   *
   * @param orgId the organization ID for which the Node Service Option Buffer is fetched. It must
   *     be a non-blank string.
   * @param id the ID of the Node Service Option Buffer to fetch. It must be a non-null value.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     fetched Node Service Option Buffer.
   * @throws CommonServiceException if an error occurs while processing the fetch request.
   */
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

  /**
   * Fetches the applicable Node Service Option Buffers for the specified organization, node,
   * service option, request date, and horizon days.
   *
   * <p>This endpoint retrieves the Node Service Option Buffers that are applicable based on the
   * provided parameters, considering the request date and horizon days.
   *
   * @param orgId the organization ID for which the Node Service Option Buffers are fetched. It must
   *     be a non-blank string.
   * @param nodeId the node ID for which the Node Service Option Buffers are fetched. It must be a
   *     non-blank string.
   * @param serviceOption the service option to filter the Node Service Option Buffers. It must be a
   *     non-blank string.
   * @param requestDate the request date for getting the applicable buffers, in UTC time. It must
   *     not be null.
   * @param horizonDays the number of days after the request date to fetch buffers. It must not be
   *     null and must be non-negative.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of applicable
   *     {@link NodeServiceOptionBufferResponse} objects.
   * @throws CommonServiceException if an error occurs while processing the fetch request.
   */
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

  /**
   * Updates the Node Service Option Buffer for the specified organization and ID with the provided
   * update request.
   *
   * <p>This endpoint allows for updating the Node Service Option Buffer based on the given
   * organization ID, node service option buffer ID, and the update request details.
   *
   * @param orgId the organization ID for which the Node Service Option Buffer is updated. It must
   *     be a non-blank string.
   * @param id the ID of the Node Service Option Buffer to update. It must be a non-null long value.
   * @param updateRequest the request body containing the details to update the Node Service Option
   *     Buffer. It must be valid.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the updated {@link
   *     NodeServiceOptionBufferResponse} object.
   * @throws CommonServiceException if an error occurs while processing the update request.
   */
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

  /**
   * Deletes the Node Service Option Buffer based on the provided delete request.
   *
   * <p>This endpoint allows for deleting a specific Node Service Option Buffer using the details
   * provided in the delete request.
   *
   * @param nodeServiceOptionBufferDeleteRequest the request body containing the details to delete
   *     the Node Service Option Buffer. It must be valid.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the result of the
   *     deletion operation and the {@link NodeServiceOptionBufferResponse} of the deleted buffer.
   * @throws CommonServiceException if an error occurs while processing the delete request.
   */
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

  /**
   * Deletes the Node Service Option Buffer by the given orgId and id.
   *
   * <p>This endpoint allows for deleting a specific Node Service Option Buffer by providing the
   * orgId and id of the buffer to be deleted. If the buffer with the given id exists, it will be
   * removed.
   *
   * @param orgId the ID of the organization to which the Node Service Option Buffer belongs. It
   *     cannot be blank.
   * @param id the ID of the Node Service Option Buffer to be deleted. It cannot be null.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the result of the
   *     deletion operation and the {@link NodeServiceOptionBufferResponse} of the deleted buffer.
   * @throws CommonServiceException if an error occurs while processing the delete request.
   */
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

  /**
   * Retrieves a list of Node Service Option Buffers based on the given orgId, nodeId, and
   * serviceOption.
   *
   * <p>This endpoint fetches the Node Service Option Buffers associated with the specified
   * organization ID (orgId), node ID (nodeId), and service option. The buffers will be returned in
   * a list.
   *
   * @param orgId the ID of the organization. It cannot be blank.
   * @param nodeId the ID of the node. It cannot be blank.
   * @param serviceOption the service option. It cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeServiceOptionBufferResponse} objects.
   * @throws CommonServiceException if an error occurs while fetching the buffers.
   */
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
