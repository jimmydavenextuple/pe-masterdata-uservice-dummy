/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.CreateNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarrierByNodeIdDoc;
import com.nextuple.node.carrier.controller.docs.DeleteNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersListByOrgIdAndNodeIdAndServiceOptionDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersListByOrgIdNodeIdCarrierServiceIdDoc;
import com.nextuple.node.carrier.controller.docs.GetNodeCarriersListDoc;
import com.nextuple.node.carrier.controller.docs.GetUniqueNodeCarriersServiceListDoc;
import com.nextuple.node.carrier.controller.docs.UpdateNodeCarriersDoc;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.service.NodeCarriersService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Controller for managing Node Carrier operations (Version 2).
 *
 * <p>This controller provides APIs for creating, fetching, updating, and deleting Node Carrier
 * details. It also includes operations for retrieving lists of node carriers and unique carrier
 * service names. This is the version 2 of the Node Carriers API.
 *
 * <p>The controller is tagged with "Node Carriers APIs" for easy categorization in API
 * documentation.
 *
 * <p>Each API endpoint is annotated with detailed information about its purpose, input parameters,
 * response structure, and potential exceptions.
 */
@Validated
@RestController
@Tag(name = "Node Carriers APIs")
@RequestMapping("/v2/node/carrier")
@RequiredArgsConstructor
public class NodeCarriersController {

  private final NodeCarriersService nodeCarriersService;
  private static final Logger logger = LoggerFactory.getLogger(NodeCarriersController.class);

  /**
   * Creates a new node carrier based on the provided request data.
   *
   * <p>This endpoint processes the creation of a new node carrier. It accepts a {@link
   * NodeCarriersRequest} containing the necessary information for the node carrier and returns the
   * created node carrier details in the response.
   *
   * @param nodeCarriersRequest the request body containing the data to create a new node carrier.
   *     Must be a valid object as per the validation annotations on the {@link
   *     NodeCarriersRequest}.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     created node carrier.
   * @throws CommonServiceException if a service-related error occurs during the creation process.
   * @throws InvalidDataException if the provided data is invalid or does not meet the required
   *     criteria.
   */
  @CreateNodeCarriersDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> createNodeCarrier(
      @Valid @RequestBody NodeCarriersRequest nodeCarriersRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug("Processing node carrier creation request {}", nodeCarriersRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier successfully created")
            .payload(nodeCarriersService.createNodeCarrier(nodeCarriersRequest))
            .build());
  }

  /**
   * Fetches the details of a node carrier based on the provided identifiers.
   *
   * <p>This endpoint retrieves the details of a specific node carrier using the given parameters:
   * organization ID, node ID, carrier service ID, and service option. It returns the details of the
   * node carrier if found.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @param carrierServiceId the unique identifier for the carrier service. Cannot be blank.
   * @param serviceOption the service option for the node carrier. Cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     requested node carrier.
   * @throws CommonServiceException if an error occurs while fetching the node carrier details.
   */
  @GetNodeCarriersDoc
  @GetMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> getNodeCarrier(
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
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws CommonServiceException {
    logger.debug(
        "Fetching node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier details fetched successfully")
            .payload(
                nodeCarriersService.getNodeCarrierDetails(
                    orgId, nodeId, carrierServiceId, serviceOption))
            .build());
  }

  /**
   * Updates the details of a node carrier based on the provided identifiers and update request.
   *
   * <p>This endpoint allows updating an existing node carrier by providing the organization ID,
   * node ID, carrier service ID, service option, and the details to update.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @param carrierServiceId the unique identifier for the carrier service. Cannot be blank.
   * @param serviceOption the service option for the node carrier. Cannot be blank.
   * @param nodeCarriersUpdateRequest the request containing the updated details for the node
   *     carrier.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the updated node
   *     carrier details.
   * @throws CommonServiceException if an error occurs while updating the node carrier.
   * @throws InvalidDataException if the provided update data is invalid.
   */
  @UpdateNodeCarriersDoc
  @PutMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> updateNodeCarrier(
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
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption,
      @Valid @RequestBody NodeCarriersUpdateRequest nodeCarriersUpdateRequest)
      throws CommonServiceException, InvalidDataException {
    logger.debug(
        "Updating node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier updated successfully")
            .payload(
                nodeCarriersService.updateNodeCarrier(
                    orgId, nodeId, carrierServiceId, serviceOption, nodeCarriersUpdateRequest))
            .build());
  }

  /**
   * Deletes a node carrier based on the provided identifiers.
   *
   * <p>This endpoint allows deleting a node carrier by providing the organization ID, node ID,
   * carrier service ID, and service option.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @param carrierServiceId the unique identifier for the carrier service. Cannot be blank.
   * @param serviceOption the service option for the node carrier. Cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} indicating success of the
   *     deletion.
   * @throws CommonServiceException if an error occurs while deleting the node carrier.
   */
  @DeleteNodeCarriersDoc
  @DeleteMapping("/{orgId}/{nodeId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarriersResponse>> deleteNodeCarrier(
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
      @NotBlank(message = "carrierServiceId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.CARRIER_SERVICE_ID,
              example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws CommonServiceException {
    logger.debug(
        "Deleting node carrier by orgId {} and nodeId {} and carrierServiceId {} and serviceOption {} request",
        orgId,
        nodeId,
        carrierServiceId,
        serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier deleted successfully")
            .payload(
                nodeCarriersService.deleteNodeCarrier(
                    orgId, nodeId, carrierServiceId, serviceOption))
            .build());
  }

  /**
   * Deletes a node carrier based on the provided organization ID and node ID.
   *
   * <p>This endpoint allows deleting a node carrier by providing the organization ID and node ID.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank. return a {@link
   *     ResponseEntity} containing a {@link BaseResponse} indicating success of the deletion with a
   *     list of deleted node carriers.
   */
  @DeleteNodeCarrierByNodeIdDoc
  @DeleteMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> deleteNodeCarrierByNodeId(
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
          String nodeId)
      throws CommonServiceException {
    logger.debug("Deleting node carrier by orgId {} and nodeId {} request", orgId, nodeId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier deleted successfully")
            .payload(nodeCarriersService.deleteNodeCarrierByNodeId(orgId, nodeId))
            .build());
  }

  /**
   * Retrieves a list of node carriers based on the provided organization ID and node ID.
   *
   * <p>This endpoint returns a list of node carriers for a given organization and node.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of node
   *     carriers.
   * @throws CommonServiceException if an error occurs while fetching the node carriers list.
   */
  @GetNodeCarriersListDoc
  @GetMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>> getNodeCarriersList(
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
          String nodeId)
      throws CommonServiceException {
    logger.debug("Processing get node carriers for nodeId and orgId");
    List<NodeCarriersResponse> nodeCarriersResponseList =
        nodeCarriersService.getNodeCarriersListByOrgIdAndNodeId(orgId, nodeId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  /**
   * Retrieves a list of unique carrier service names based on the provided organization ID and node
   * ID.
   *
   * <p>This endpoint returns a list of carrier service names that are associated with a given
   * organization and node.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of carrier
   *     service names.
   * @throws CommonServiceException if an error occurs while fetching the list of carrier services.
   */
  @GetUniqueNodeCarriersServiceListDoc
  @GetMapping("/{orgId}/{nodeId}/carrier-service")
  public ResponseEntity<BaseResponse<List<String>>> getUniqueNodeCarrierServiceList(
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
          String nodeId)
      throws CommonServiceException {
    logger.debug("Processing get list of carrier service names");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeCarriersService.getListOfCarrierServiceNameByOrgIdAndNodeId(orgId, nodeId))
            .build());
  }

  /**
   * Retrieves a list of node carriers based on the provided organization ID, node ID, and service
   * option.
   *
   * <p>This endpoint returns a list of node carriers associated with the given organization, node,
   * and service option.
   *
   * @param orgId the unique identifier for the organization. Cannot be blank.
   * @param nodeId the unique identifier for the node. Cannot be blank.
   * @param serviceOption the service option for the node carrier. Cannot be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of node
   *     carriers.
   * @throws CommonServiceException if an error occurs while fetching the list of node carriers.
   */
  @GetNodeCarriersListByOrgIdAndNodeIdAndServiceOptionDoc
  @GetMapping("/{orgId}/{nodeId}/{serviceOption}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
      getNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
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
              String serviceOption)
          throws CommonServiceException {
    logger.debug("Processing get node carriers for nodeId and orgId and serviceOption");
    List<NodeCarriersResponse> nodeCarriersResponseList =
        nodeCarriersService.getNodeCarriersList(orgId, nodeId, serviceOption);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  /**
   * Retrieves a list of Node Carrier cache keys with an optional limit on the number of results.
   *
   * <p>This endpoint allows users to fetch Node Carrier cache keys, with an optional limit to
   * control the number of results returned.
   *
   * @param limit the maximum number of cache keys to return. Must be a positive integer.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of Node
   *     Carrier cache keys.
   * @throws CommonServiceException if an error occurs while fetching the Node Carrier cache keys.
   */
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> getNodeCarriersCacheKeys(
      @RequestParam Integer limit) throws CommonServiceException {
    logger.debug("Processing get Node Carrier List Cache Keys");
    var response = nodeCarriersService.getAllNodeCarriersCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  /**
   * Retrieves a list of Node Carriers based on the given organization ID and carrier service ID.
   *
   * <p>This endpoint allows users to fetch all Node Carriers for a specific organization and
   * carrier service.
   *
   * @param orgId the unique identifier for the organization. It cannot be empty.
   * @param carrierServiceId the unique identifier for the carrier service. It cannot be empty.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of Node
   *     Carriers.
   * @throws CommonServiceException if an error occurs while fetching the Node Carriers for the
   *     given orgId and carrierServiceId.
   */
  @GetMapping("/org/{orgId}/carrier-service/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
      getAllNodeCarriersByOrgIdCarrierServiceId(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "carrierServiceId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.CARRIER_SERVICE_ID,
                  example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
              @PathVariable
              String carrierServiceId)
          throws CommonServiceException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarriersResponseList =
        nodeCarriersService.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }

  /**
   * Retrieves a list of Node Carriers based on the given organization ID, node ID, and carrier
   * service ID.
   *
   * <p>This endpoint allows users to fetch all Node Carriers for a specific organization, node, and
   * carrier service.
   *
   * @param orgId the unique identifier for the organization. It cannot be empty.
   * @param nodeId the unique identifier for the node. It cannot be empty.
   * @param carrierServiceId the unique identifier for the carrier service. It cannot be empty.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the list of Node
   *     Carriers.
   * @throws CommonServiceException if an error occurs while fetching the Node Carriers for the
   *     given
   */
  @GetMapping("/org/{orgId}/nodeId/{nodeId}/carrier-service/{carrierServiceId}")
  @GetNodeCarriersListByOrgIdNodeIdCarrierServiceIdDoc
  public ResponseEntity<BaseResponse<List<NodeCarriersResponse>>>
      getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "carrierServiceId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.CARRIER_SERVICE_ID,
                  example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
              @PathVariable
              String carrierServiceId)
          throws CommonServiceException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarriersResponseList =
        nodeCarriersService.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            orgId, nodeId, carrierServiceId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carriers list fetched successfully")
            .payload(nodeCarriersResponseList)
            .build());
  }
}
