package com.hbc.node.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.node.service.NodeService;
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
@RequestMapping("/node")
@RequiredArgsConstructor
public class NodeController {

  private static final Logger logger = LoggerFactory.getLogger(NodeController.class);
  private final NodeService nodeService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeResponse>> createNode(
      @Valid @RequestBody NodeRequest nodeRequest) throws NodeDomainException {
    logger.debug("Processing node creation request");
    try {
      var nodeResponse = nodeService.createNode(nodeRequest);
      logger.info("Response after creation of node :{}", nodeResponse);

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
    logger.debug("Processing update node details");
    try {

      var nodeResponse = nodeService.updateNodeDetails(nodeId, orgId, nodeUpdationRequest);
      logger.info("Response after updation of node :{}", nodeResponse);

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
    logger.debug("Processing get node details");
    try {

      var nodeResponse = nodeService.getNodeDetails(nodeId, orgId);

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
    logger.debug("Processing delete node");
    try {

      var nodeResponse = nodeService.deleteNode(nodeId, orgId);
      logger.info("Response after deletion of node :{}", nodeResponse);

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
