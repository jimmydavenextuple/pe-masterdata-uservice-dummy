/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.controller.docs.*;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import com.nextuple.node.carrier.exception.NodeCarrierSelectionDomainException;
import com.nextuple.node.carrier.service.NodeCarrierService;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Node Carrier operations.
 *
 * <p>This controller provides APIs for creating and fetching Node Carrier details. It includes
 * methods for creating node carriers and retrieving specific node carrier details based on unique
 * identifiers.
 *
 * <p>The controller is tagged with "Node Carrier APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Node Carrier APIs")
@RequestMapping("/node/carrier")
@RequiredArgsConstructor
public class NodeCarrierController {
  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierController.class);
  private static final String NODE_CARRIER_SUCCESS_MESSAGE =
      "Node Carrier list fetched successfully";

  private final NodeCarrierService nodeCarrierService;

  /**
   * Creates a new node carrier.
   *
   * <p>This endpoint processes requests to create a new node carrier using the provided {@link
   * NodeCarrierRequest}. If successful, it returns a {@link NodeCarrierResponse} with details of
   * the created node carrier.
   *
   * @param nodeCarrierRequest the request object containing the details for the node carrier to be
   *     created. It must be valid according to the constraints defined in the {@link
   *     NodeCarrierRequest}.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} object with a success
   *     message and the {@link NodeCarrierResponse} payload representing the newly created node
   *     carrier.
   * @throws NodeCarrierDomainException if a domain-specific error occurs during the node carrier
   *     creation.
   * @throws InvalidDataException if the provided data is invalid.
   * @throws CommonServiceException if a common service-related error occurs during the process.
   */
  @CreateNodeCarrierDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> createNodeCarrier(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest)
      throws NodeCarrierDomainException, InvalidDataException, CommonServiceException {
    logger.debug("Processing node carrier creation request");
    try {
      var nodeCarrierResponse = nodeCarrierService.createNodeCarrier(nodeCarrierRequest);
      logger.info("Response after creation of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier successfully created")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create node carrier details");
      throw e;
    }
  }

  /**
   * Creates or updates the buffer data for a node carrier.
   *
   * <p>This endpoint processes requests to either create or update buffer data for a node carrier
   * based on the provided {@link NodeCarrierBufferRequest}. It returns the updated or created
   * {@link NodeCarrierResponse} upon successful completion.
   *
   * @param nodeCarrierBufferRequest the request object containing the buffer data details for the
   *     node carrier. It must be valid according to the constraints defined in the {@link
   *     NodeCarrierBufferRequest}.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} object with a success
   *     message and the {@link NodeCarrierResponse} payload representing the created or updated
   *     node carrier buffer data.
   * @throws NodeCarrierDomainException if a domain-specific error occurs during the creation or
   *     update of the buffer data.
   * @throws CommonServiceException if a common service-related error occurs during the process.
   */
  @UpdateBufferDoc
  @PutMapping("/buffer")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateBuffer(
      @Valid @RequestBody NodeCarrierBufferRequest nodeCarrierBufferRequest)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing buffer data creation or updation");
    try {
      var nodeCarrierResponse = nodeCarrierService.updateBufferData(nodeCarrierBufferRequest);
      logger.info("Response after creation or updation of buffer data : {}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Buffer data successfully added.")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node carrier buffer details");
      throw e;
    }
  }

  /**
   * Retrieves the details of a specific node carrier based on the provided parameters.
   *
   * <p>This endpoint fetches node carrier details using the {@code nodeId}, {@code orgId}, {@code
   * carrierServiceId}, and {@code serviceOption} parameters. These values identify the specific
   * node carrier to retrieve.
   *
   * @param nodeId the unique identifier of the node. Must not be blank.
   * @param orgId the unique identifier of the organization. Must not be blank.
   * @param carrierServiceId the unique identifier of the carrier service. Must not be blank.
   * @param serviceOption the specific service option for the node carrier. Must not be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} object with a success
   *     message and the {@link NodeCarrierResponse} payload containing the details of the requested
   *     node carrier.
   * @throws NodeCarrierDomainException if a domain-specific error occurs while fetching the node
   *     carrier details.
   * @throws CommonServiceException if a common service-related error occurs during the request.
   */
  @GetNodeCarrierDoc
  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
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
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing get node carrier details");
    try {
      var nodeCarrierResponse =
          nodeCarrierService.getNodeCarrierDetails(nodeId, orgId, carrierServiceId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier details fetched successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get node carrier details");
      throw e;
    }
  }

  /**
   * Updates the details of a specific node carrier based on the provided parameters.
   *
   * <p>This endpoint allows the modification of a node carrier's details using the {@code nodeId},
   * {@code orgId}, {@code carrierServiceId}, and {@code serviceOption} parameters, along with the
   * new data provided in the request body. The request body should contain the updated node carrier
   * details.
   *
   * @param nodeId the unique identifier of the node. Must not be blank.
   * @param orgId the unique identifier of the organization. Must not be blank.
   * @param carrierServiceId the unique identifier of the carrier service. Must not be blank.
   * @param serviceOption the specific service option for the node carrier. Must not be blank.
   * @param nodeCarrierUpdateRequest the object containing the updated node carrier details. Must be
   *     valid.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} object with a success
   *     message and the {@link NodeCarrierResponse} payload containing the updated details of the
   *     node carrier.
   * @throws NodeCarrierDomainException if a domain-specific error occurs while updating the node
   *     carrier details.
   * @throws CommonServiceException if a common service-related error occurs during the update
   *     process.
   * @throws InvalidDataException if invalid data is provided in the request body.
   */
  @UpdateNodeCarrierDoc
  @PutMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
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
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption,
      @Valid @RequestBody NodeCarrierUpdateRequest nodeCarrierUpdateRequest)
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
    logger.debug("Processing update node carrier details");

    try {
      var nodeCarrierResponse =
          nodeCarrierService.updateNodeCarrier(
              nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);
      logger.info("Response after updation of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier updated successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node carrier details");
      throw e;
    }
  }

  /**
   * Deletes a specific node carrier based on the provided parameters.
   *
   * <p>This endpoint allows for the deletion of a node carrier by specifying the {@code nodeId},
   * {@code orgId}, {@code carrierServiceId}, and {@code serviceOption}. The method will remove the
   * node carrier from the system, and the response will indicate whether the deletion was
   * successful.
   *
   * @param nodeId the unique identifier of the node. Must not be blank.
   * @param orgId the unique identifier of the organization. Must not be blank.
   * @param carrierServiceId the unique identifier of the carrier service. Must not be blank.
   * @param serviceOption the specific service option for the node carrier. Must not be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} object with a success
   *     message and the {@link NodeCarrierResponse} payload indicating the result of the deletion
   *     operation.
   * @throws NodeCarrierDomainException if a domain-specific error occurs while deleting the node
   *     carrier.
   * @throws CommonServiceException if a common service-related error occurs during the deletion
   *     process.
   */
  @DeleteNodeCarrierDoc
  @DeleteMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> deleteNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
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
          String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing delete node carrier");
    try {
      var nodeCarrierResponse =
          nodeCarrierService.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
      logger.info("Response after deletion of node-carrier :{}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier deleted successfully")
              .payload(nodeCarrierResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete node carrier details");
      throw e;
    }
  }

  /**
   * Retrieves a list of node carrier details for the given node ID, organization ID, and service
   * option.
   *
   * <p>This endpoint fetches a list of {@link NodeCarrierResponse} based on the {@code nodeId},
   * {@code orgId}, and {@code serviceOption} provided in the path. The returned list will contain
   * the carrier details associated with the specified parameters.
   *
   * @param nodeId the unique identifier of the node. Must not be blank.
   * @param orgId the unique identifier of the organization. Must not be blank.
   * @param serviceOption the specific service option for the node carrier. Must not be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and a
   *     payload of a list of {@link NodeCarrierResponse}.
   * @throws NodeCarrierDomainException if a domain-specific error occurs while retrieving the node
   *     carrier details.
   * @throws CommonServiceException if a common service-related error occurs during the retrieval
   *     process.
   */
  @GetNodeCarrierAssociationDoc
  @GetMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotBlank(message = "serviceOption can't be empty")
          @Parameter(
              description = NodeCarrierConstants.SERVICE_OPTION,
              example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
          @PathVariable
          String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing get node carrier for nodeId, orgId and serviceOption");
    try {
      List<NodeCarrierResponse> nodeCarrierResponseList =
          nodeCarrierService.getNodeCarrierForNodeIdAOrgIdAndServiceOption(
              nodeId, orgId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node Carrier details list fetched successfully")
              .payload(nodeCarrierResponseList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch node carrier details list");
      throw e;
    }
  }

  /**
   * Retrieves a list of node carrier details for the specified node ID and organization ID.
   *
   * <p>This endpoint fetches a list of {@link NodeCarrierResponse} based on the {@code nodeId} and
   * {@code orgId} provided in the path. The returned list will contain the carrier details
   * associated with the specified parameters.
   *
   * @param nodeId the unique identifier of the node. Must not be blank.
   * @param orgId the unique identifier of the organization. Must not be blank.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and a
   *     payload of a list of {@link NodeCarrierResponse}.
   * @throws NodeCarrierDomainException if a domain-specific error occurs while retrieving the node
   *     carrier details.
   */
  @GetNodeCarrierListDoc
  @GetMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrierList(
      @NotBlank(message = "nodeId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.NODE_ID,
              example = NodeCarrierConstants.NODE_ID_EXAMPLE)
          @PathVariable
          String nodeId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAndOrgId(nodeId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }

  /**
   * Updates the processing lead time for a node carrier.
   *
   * <p>This endpoint updates the processing lead time for the specified node carrier. The request
   * body must contain the necessary details to update the processing lead time. The response will
   * contain the updated {@link NodeCarrierResponse} with the updated lead time information.
   *
   * @param nodeCarrierRequest the request body containing the node carrier details with the updated
   *     processing lead time. Must be valid according to the {@link NodeCarrierRequest}
   *     constraints.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the updated {@link NodeCarrierResponse}.
   * @throws NodeCarrierDomainException if there is an error in the domain logic while updating the
   *     processing lead time.
   */
  @UpdateProcessingLeadTimeDoc
  @PostMapping("/processing-lead-time")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateProcessingLeadTime(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest) throws NodeCarrierDomainException {
    logger.debug("Processing update processing lead time request");

    var nodeCarrierResponse = nodeCarrierService.updateProcessingLeadTime(nodeCarrierRequest);
    logger.info("Response after updating processing lead time :{}", nodeCarrierResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Processing lead time updated successfully for a node carrier")
            .payload(nodeCarrierResponse)
            .build());
  }

  /**
   * Adds a priority to the node carrier selection.
   *
   * <p>This endpoint allows you to add a priority to the selection of node carriers. The request
   * body must contain the details required to add the priority to a specific node carrier
   * selection. The response will contain the details of the node carrier selection priority that
   * was added.
   *
   * @param nodeCarrierSelectionRequest the request body containing the details for adding the node
   *     carrier selection priority. Must be valid according to the {@link
   *     NodeCarrierSelectionRequest} constraints.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the {@link NodeCarrierSelectionResponse} that contains the updated selection priority.
   */
  @AddNodeCarrierSelectionPriorityDoc
  @PostMapping("/node-carrier-selection")
  public ResponseEntity<BaseResponse<NodeCarrierSelectionResponse>> addNodeCarrierSelectionPriority(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest) {
    var nodeCarrierSelectionPriorityResponse =
        nodeCarrierService.addNodeCarrierSelectionPriority(nodeCarrierSelectionRequest);
    logger.info(
        "Response after addition of node-carrier selection priority :{}",
        nodeCarrierSelectionPriorityResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection priority successfully added")
            .payload(nodeCarrierSelectionPriorityResponse)
            .build());
  }

  /**
   * Fetches the details of node carrier selections based on the given parameters.
   *
   * <p>This endpoint retrieves the details of the node carrier selection based on the organization
   * ID, service option, and destination geozone. The response will contain a list of node carrier
   * selection details.
   *
   * @param orgId the unique identifier of the organization. Must be non-empty.
   * @param serviceOption the service option for which the node carrier selection details are
   *     required. Must be non-empty.
   * @param destinationGeozone the destination geozone for which the node carrier selection details
   *     are required. Must be non-empty.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and a
   *     list of {@link NodeCarrierSelectionResponse} containing the selection details.
   */
  @GetNodeCarrierSelectionDetailsDoc
  @GetMapping("/node-carrier-selection/{orgId}/{serviceOption}/{destinationGeozone}")
  public ResponseEntity<BaseResponse<List<NodeCarrierSelectionResponse>>>
      getNodeCarrierSelectionDetails(
          @NotBlank(message = "orgId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @NotBlank(message = "serviceOption cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @PathVariable
              String serviceOption,
          @NotBlank(message = "destinationGeozone cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.DEST_GEOZONE,
                  example = NodeCarrierConstants.GEOZONE_EXAMPLE)
              @PathVariable
              String destinationGeozone) {
    var nodeCarrierSelectionList =
        nodeCarrierService.getNodeCarrierSelectionDetails(orgId, serviceOption, destinationGeozone);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection details fetched successfully")
            .payload(nodeCarrierSelectionList)
            .build());
  }

  /**
   * Deletes a node carrier based on the given node ID, organization ID, carrier service ID
   * (optional), and service option.
   *
   * <p>This endpoint removes a node carrier by matching the specified node ID, organization ID, and
   * service option. Optionally, the carrier service ID can be provided to further refine the
   * deletion.
   *
   * @param nodeId the unique identifier of the node. Must be non-empty.
   * @param orgId the unique identifier of the organization. Must be non-empty.
   * @param carrierServiceId the unique identifier of the carrier service. Can be optional.
   * @param serviceOption the service option associated with the node carrier. Must be non-empty.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the {@link NodeCarrierResponse} of the deleted node carrier.
   * @throws NodeCarrierDomainException if any domain-specific error occurs during the deletion
   *     process.
   * @throws CommonServiceException if any general service exception occurs.
   */
  @DeleteNodeCarrierByOrgIdAndServiceOption
  @DeleteMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>>
      deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
          @NotBlank(message = "nodeId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "orgId cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId,
          @Parameter(
                  description = "Specifies the unique identifier of the carrier service.",
                  example = "123")
              @RequestParam(required = false)
              String carrierServiceId,
          @NotBlank(message = "serviceOption cannot be empty")
              @Parameter(
                  description = NodeCarrierConstants.SERVICE_OPTION,
                  example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
              @PathVariable
              String serviceOption)
          throws NodeCarrierDomainException, CommonServiceException {
    logger.debug("Processing delete node carrier");

    var nodeCarrierResponse =
        nodeCarrierService.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);
    logger.info("Response after deletion of node-carrier :{}", nodeCarrierResponse);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier deleted successfully")
            .payload(nodeCarrierResponse)
            .build());
  }

  /**
   * Retrieves a list of node carrier cache keys from the database.
   *
   * <p>This endpoint fetches the cache keys for node carriers, limiting the number of rows returned
   * based on the provided {@code limit} parameter. The {@code limit} determines the maximum number
   * of cache keys to fetch.
   *
   * @param limit the maximum number of cache keys to retrieve from the database. Must be a positive
   *     integer.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of {@link NodeCarrierListCacheKeyDto} containing the cache keys.
   * @throws NodeCarrierDomainException if any error occurs during the retrieval of node carrier
   *     cache keys.
   */
  @GetNodeCarrierListCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> getNodeCarrierListCacheKeys(
      @RequestParam
          @Parameter(
              description = "Specifies the number of rows to be returned from DB.",
              example = "1")
          Integer limit)
      throws NodeCarrierDomainException {
    logger.debug("Processing get Node Carrier List Cache Keys");

    var response = nodeCarrierService.getAllNodeCarrierCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  /**
   * Deletes the node carrier selection details based on the provided request.
   *
   * <p>This endpoint deletes the node carrier selection details specified in the request body. It
   * processes the {@link NodeCarrierSelectionRequest} to delete the associated selection data.
   *
   * @param nodeCarrierSelectionRequest the request body containing the details for the node carrier
   *     selection to delete. This should be a valid request with all necessary information.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the deleted {@link NodeCarrierSelectionResponse}.
   * @throws CommonServiceException if any error occurs during the deletion process related to the
   *     service layer.
   * @throws NodeCarrierSelectionDomainException if any domain-specific exception occurs while
   *     deleting the selection.
   */
  @DeleteNodeCarrierDocSelectionDetailsDoc
  @DeleteMapping("/node-carrier-selection")
  public ResponseEntity<BaseResponse<NodeCarrierSelectionResponse>>
      deleteNodeCarrierSelectionDetails(
          @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest)
          throws CommonServiceException, NodeCarrierSelectionDomainException {
    var nodeCarrierSelectionResponse =
        nodeCarrierService.deleteNodeCarrierSelection(nodeCarrierSelectionRequest);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection deleted successfully")
            .payload(nodeCarrierSelectionResponse)
            .build());
  }

  /**
   * Retrieves a list of unique node carrier services for the specified node and organization.
   *
   * <p>This endpoint fetches a list of unique carrier services associated with the given node ID
   * and organization ID. It ensures that the list contains only distinct carrier services for the
   * provided node.
   *
   * @param orgId the organization ID for which the node carrier services are to be fetched. Must be
   *     non-blank and provided in the path variable.
   * @param nodeId the node ID for which the carrier services are to be fetched. Must be non-blank
   *     and provided in the path variable.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of unique node
   *     carrier services for the given node and organization.
   * @throws NodeCarrierDomainException if any error occurs during the process of retrieving the
   *     node carrier services.
   */
  @GetUniqueNodeCarrierServiceListDoc
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
      throws NodeCarrierDomainException {
    logger.debug("Processing get list of unique node-carrier-service");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeCarrierService.getUniqueNodeCarrierServiceList(nodeId, orgId))
            .build());
  }

  /**
   * Retrieves a list of node carrier details with the associated last pickup time for the specified
   * node and organization.
   *
   * <p>This endpoint fetches a list of node carrier details, including the last pickup time, for
   * the given node ID and organization ID. The response contains node carriers along with their
   * last pickup time information.
   *
   * @param nodeId the node ID for which the carrier details are to be fetched. Must be non-blank
   *     and provided in the path variable.
   * @param orgId the organization ID for which the node carrier details are to be fetched. Must be
   *     non-blank and provided in the path variable.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of node carrier
   *     details including the last pickup time for the given node and organization.
   * @throws NodeCarrierDomainException if any error occurs during the process of retrieving the
   *     node carrier details.
   */
  @GetNodeCarrierListWithLastPickUpTimeDetailsDoc
  @GetMapping("/v1/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>>
      getNodeCarrierListWithLastPickUpTimeDetails(
          @NotBlank(message = "nodeId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.NODE_ID,
                  example = NodeCarrierConstants.NODE_ID_EXAMPLE)
              @PathVariable
              String nodeId,
          @NotBlank(message = "orgId can't be empty")
              @Parameter(
                  description = NodeCarrierConstants.ORG_ID,
                  example = NodeCarrierConstants.ORG_ID_EXAMPLE)
              @PathVariable
              String orgId)
          throws NodeCarrierDomainException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierListForNodeIdAndOrgId(nodeId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier list with last pickup time details fetched successfully")
            .payload(nodeCarrierResponseList)
            .build());
  }

  /**
   * Retrieves all node carriers associated with the specified organization ID.
   *
   * <p>This endpoint fetches a list of all node carriers for the given organization ID. The
   * response includes the details of each node carrier.
   *
   * @param orgId the organization ID for which the node carriers are to be fetched. Must be
   *     non-blank and provided in the path variable.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of node carrier
   *     details associated with the specified organization ID.
   * @throws NodeCarrierDomainException if any error occurs during the process of retrieving the
   *     node carriers.
   */
  @GetAllNodeCarriersByOrgIdDoc
  @GetMapping("/{orgId}/node-carriers")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getAllNodeCarriersByOrgId(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(
              description = NodeCarrierConstants.ORG_ID,
              example = NodeCarrierConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get all node carriers for a given orgId");
    var nodeCarrierResponseList = nodeCarrierService.getAllNodeCarrierByOrgId(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }

  /**
   * Retrieves all node carriers associated with the specified organization ID and carrier service
   * ID.
   *
   * <p>This endpoint fetches a list of all node carriers for the given organization ID and carrier
   * service ID. The response includes the details of each node carrier associated with both
   * parameters.
   *
   * @param orgId the organization ID for which the node carriers are to be fetched. Must be
   *     non-blank and provided in the path variable.
   * @param carrierServiceId the carrier service ID for which the node carriers are to be fetched.
   *     Must be non-blank and provided in the path variable.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with a list of node carrier
   *     details associated with the specified organization ID and carrier service ID.
   * @throws NodeCarrierDomainException if any error occurs during the process of retrieving the
   *     node carriers.
   */
  @GetAllNodeCarriersByOrgIdCarrierServiceIdDoc
  @GetMapping("/{orgId}/{carrierServiceId}/node-carriers")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>>
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
          throws NodeCarrierDomainException {
    logger.debug("Processing get all node carriers for a given orgId and carrierServiceId");
    var nodeCarrierResponseList =
        nodeCarrierService.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(NODE_CARRIER_SUCCESS_MESSAGE)
            .payload(nodeCarrierResponseList)
            .build());
  }
}
