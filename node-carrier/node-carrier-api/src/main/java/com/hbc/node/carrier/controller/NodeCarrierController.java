package com.hbc.node.carrier.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
import com.hbc.node.carrier.exception.InvalidDataException;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.exception.NodeCarrierSelectionDomainException;
import com.hbc.node.carrier.service.NodeCarrierService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

@Validated
@RestController
@RequestMapping("/node/carrier")
@RequiredArgsConstructor
public class NodeCarrierController {
  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierController.class);

  private final NodeCarrierService nodeCarrierService;

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

  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty") @PathVariable String serviceOption)
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

  @PutMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> updateNodeCarrier(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty") @PathVariable String serviceOption,
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

  @DeleteMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> deleteNodeCarrier(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @NotBlank(message = "serviceOption can't be empty") @PathVariable String serviceOption)
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

  @GetMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrier(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "serviceOption can't be empty") @PathVariable String serviceOption)
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

  @GetMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getNodeCarrierList(
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get node carrier for nodeId and orgId");
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierService.getNodeCarrierForNodeIdAndOrgId(nodeId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier list fetched successfully")
            .payload(nodeCarrierResponseList)
            .build());
  }

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

  @GetMapping("/node-carrier-selection/{orgId}/{serviceOption}/{destinationGeozone}")
  public ResponseEntity<BaseResponse<List<NodeCarrierSelectionResponse>>>
      getNodeCarrierSelectionDetails(
          @NotBlank(message = "orgId cannot be empty") @PathVariable String orgId,
          @NotBlank(message = "serviceOption cannot be empty") @PathVariable String serviceOption,
          @NotBlank(message = "destinationGeozone cannot be empty") @PathVariable
              String destinationGeozone) {
    var nodeCarrierSelectionList =
        nodeCarrierService.getNodeCarrierSelectionDetails(orgId, serviceOption, destinationGeozone);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier selection details fetched successfully")
            .payload(nodeCarrierSelectionList)
            .build());
  }

  @DeleteMapping("/{nodeId}/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>>
      deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
          @NotBlank(message = "nodeId cannot be empty") @PathVariable String nodeId,
          @NotBlank(message = "orgId cannot be empty") @PathVariable String orgId,
          @RequestParam(required = false) String carrierServiceId,
          @NotBlank(message = "serviceOption cannot be empty") @PathVariable String serviceOption)
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

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierListCacheKeyDto>>> getNodeCarrierListCacheKeys(
      @RequestParam Integer limit) throws NodeCarrierDomainException {
    logger.debug("Processing get Node Carrier List Cache Keys");

    var response = nodeCarrierService.getAllNodeCarrierCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

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

  @GetMapping("/{orgId}/{nodeId}/carrier-service")
  public ResponseEntity<BaseResponse<List<String>>> getUniqueNodeCarrierServiceList(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get list of unique node-carrier-service");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(nodeCarrierService.getUniqueNodeCarrierServiceList(nodeId, orgId))
            .build());
  }

  @GetMapping("/v1/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>>
      getNodeCarrierListWithLastPickUpTimeDetails(
          @NotBlank(message = "nodeId can't be empty") @PathVariable String nodeId,
          @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
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

  @GetMapping("/{orgId}/node-carriers")
  public ResponseEntity<BaseResponse<List<NodeCarrierResponse>>> getAllNodeCarriersByOrgId(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId)
      throws NodeCarrierDomainException {
    logger.debug("Processing get all node carriers for a given orgId");
    var nodeCarrierResponseList = nodeCarrierService.getAllNodeCarrierByOrgId(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier list fetched successfully")
            .payload(nodeCarrierResponseList)
            .build());
  }
}
