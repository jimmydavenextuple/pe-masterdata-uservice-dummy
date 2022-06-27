package com.nextuple.node.carrier.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.exception.NodeCarrierDomainException;
import com.nextuple.node.carrier.service.NodeCarrierService;
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
      throws NodeCarrierDomainException, CommonServiceException {
    logger.info("Processing node carrier creation request");
    try {
      NodeCarrierResponse nodeCarrierResponse =
          nodeCarrierService.createNodeCarrier(nodeCarrierRequest);

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

  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<NodeCarrierResponse>> getNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption)
      throws NodeCarrierDomainException, CommonServiceException {
    logger.info("Processing get node carrier details");
    try {
      NodeCarrierResponse nodeCarrierResponse =
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
      throws NodeCarrierDomainException, CommonServiceException {
    logger.info("Processing update node carrier details");

    try {
      NodeCarrierResponse nodeCarrierResponse =
          nodeCarrierService.updateNodeCarrier(
              nodeId, orgId, carrierServiceId, serviceOption, nodeCarrierUpdateRequest);

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
    logger.info("Processing delete node carrier");
    try {
      NodeCarrierResponse nodeCarrierResponse =
          nodeCarrierService.deleteNodeCarrier(nodeId, orgId, carrierServiceId, serviceOption);

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
    logger.info("Processing get node carrier for nodeId, orgId and serviceOption");
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
}
