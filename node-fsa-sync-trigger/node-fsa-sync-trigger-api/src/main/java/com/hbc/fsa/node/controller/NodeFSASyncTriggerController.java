package com.hbc.fsa.node.controller;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.fsa.node.pojo.NodeFSASyncRequest;
import com.hbc.fsa.node.producer.NodeFSASyncRequestPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/node-fsa-sync")
public class NodeFSASyncTriggerController {
  @Value("${fsa-updates.sync-trigger-topic.name}")
  String nodeFSASyncRequestTopic;

  @Autowired NodeFSASyncRequestPublisher nodeFSASyncRequestPublisher;

  private static final Logger logger = LoggerFactory.getLogger(NodeFSASyncTriggerController.class);

  @PostMapping(value = "/")
  public ResponseEntity<BaseResponse<String>> nodeFSASync(@RequestBody NodeFSASyncRequest request) {
    logger.debug("Processing request for node fsa sync");
    try {
      nodeFSASyncRequestPublisher.publish(request, nodeFSASyncRequestTopic);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node FSA mapping sync request sent successfully")
              .build());
    } catch (Exception e) {
      logger.error("Error while sending node fsa sync request", e);
      throw e;
    }
  }
}
