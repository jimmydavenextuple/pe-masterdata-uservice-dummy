package com.hbc.node.carrier.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.exception.InvalidDataException;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.service.NodeCarrierService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/carrier")
@RequiredArgsConstructor
public class NodeCarrierController {
  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierController.class);

  private final NodeCarrierService nodeCarrierService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> createNodeCarrier(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest)
      throws NodeCarrierDomainException, CommonServiceException, InvalidDataException {
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

  @PostMapping("/buffer")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> createOrUpdateBuffer(
      @Valid @RequestBody NodeCarrierBufferRequest nodeCarrierBufferRequest)
      throws NodeCarrierDomainException {
    logger.debug("Processing buffer data creation or updation");
    try {
      var nodeCarrierResponse =
          nodeCarrierService.createOrUpdateBufferData(nodeCarrierBufferRequest);
      logger.info("Response after creation or updation of buffer data : {}", nodeCarrierResponse);
      return ResponseEntity.ok(
          BaseResponse.builder().message("Added buffer data").payload(nodeCarrierResponse).build());
    } catch (Exception e) {
      logger.error("Failed to create or update node carrier buffer details");
      throw e;
    }
  }

  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> getNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption)
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
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption,
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
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption)
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
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String serviceOption)
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
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId)
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
}
