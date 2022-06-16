package com.nextuple.pe.masterdata.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.pe.masterdata.domain.inbound.NodeRequest;
import com.nextuple.pe.masterdata.domain.inbound.NodeUpdationRequest;
import com.nextuple.pe.masterdata.exception.NodeDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.NodeService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/node")
@RequiredArgsConstructor
public class NodeController {

  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);
  private final NodeService nodeService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeResponse>> createNode(
      @Valid @RequestBody NodeRequest nodeRequest) throws NodeDomainException {
    logger.info("Processing node creation request");
    try {
      NodeResponse nodeResponse = nodeService.createNode(nodeRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node successfully created")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create node");
      throw e;
    }
  }

  @PutMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> updateNodeDetails(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {
    logger.info("Processing update node details");
    try {

      NodeResponse nodeResponse = nodeService.updateNodeDetails(nodeId, orgId, nodeUpdationRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node details updated successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node details");
      throw e;
    }
  }

  @GetMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> getNodeDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId)
      throws NodeDomainException, CommonServiceException {
    logger.info("Processing get node details");
    try {

      NodeResponse nodeResponse = nodeService.getNodeDetails(nodeId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node details fetched successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch node details");
      throw e;
    }
  }

  @DeleteMapping("/{nodeId}/{orgId}")
  public ResponseEntity<BaseResponse<NodeResponse>> deleteNode(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId)
      throws NodeDomainException, CommonServiceException {
    logger.info("Processing delete node");
    try {

      NodeResponse nodeResponse = nodeService.deleteNode(nodeId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node deleted successfully")
              .payload(nodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete node");
      throw e;
    }
  }
}
