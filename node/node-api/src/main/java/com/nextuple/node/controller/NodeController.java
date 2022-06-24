package com.nextuple.node.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.consumer.LocalCacheUpdateService;
import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.cache.service.NodeDataNearCacheService;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.exception.CommonServiceException;
import com.nextuple.node.exception.NodeDomainException;
import com.nextuple.node.service.NodeService;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

  @Autowired private final NodeDataNearCacheService nodeNearCacheService;

  private final LocalCacheUpdateService localCacheUpdateService;

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

  @GetMapping(path = "/test-api", produces = MediaType.APPLICATION_JSON_VALUE)
  public void getResponse()
      throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException,
          IllegalAccessException, NoSuchMethodException, InstantiationException {
    try {
      //      NodeDataCacheKey nodeCacheKey =
      // NodeDataCacheKey.builder().nodeId("7-64-59").orgId("Bay").build();
      //      NodeDataCacheValue nodeCacheValue = nodeNearCacheService.get(nodeCacheKey);
      //      System.out.println(nodeCacheValue);
      //      NodeDataCacheValue nodeCacheValue1 = nodeNearCacheService.get(nodeCacheKey);
      //      System.out.println(nodeCacheValue1);
      NodeDataCacheKey nodeCacheKey1 =
          NodeDataCacheKey.builder().nodeId("7-64-59").orgId("Bay").build();
      NodeDataCacheValue nodeCacheValue2 = nodeNearCacheService.get(nodeCacheKey1);
      System.out.println(nodeCacheValue2);
      NodeDataCacheValue nodeCacheValue3 = nodeNearCacheService.get(nodeCacheKey1);
      System.out.println(nodeCacheValue3);
      //      nodeNearCacheService.deleteAll();
      //      NodeDataCacheValue nodeCacheValue4 = nodeNearCacheService.get(nodeCacheKey);
      //      System.out.println(nodeCacheValue4);

      LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
      LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
      Map<String, Object> map = new HashMap<>();
      map.put("nodeId", "7-64-59");
      map.put("orgId", "Bay");
      localCacheUpdateMessage.setMessage(map);
      localCacheUpdateMessage.setEntityName("Node");
      localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

      localCacheUpdateService.handleLocalCacheUpdate(localCacheUpdateEvent);
    } catch (Exception exception) {
      System.out.println(exception.getCause());
    }
  }
}
