package com.nextuple.fsa.node.consumer;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import com.nextuple.fsa.node.service.NodeFSASyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
    topics = "${fsa-updates.sync-trigger-topic.name}",
    groupId = "${fsa-updates.sync-trigger-topic.group-id}",
    autoStartup = "${fsa-updates.activated:false}")
public class NodeFSASyncConsumer {
  @Autowired NodeFSASyncService nodeFSASyncService;
  private static final Logger logger = LoggerFactory.getLogger(NodeFSASyncConsumer.class);

  @KafkaHandler
  public void consumerNodeFSASyncRequest(
      @Payload NodeFSASyncRequest request, @Headers KafkaMessageHeaders headers) {
    logger.debug("Processing request for node fsa sync");

    try {
      String authToken = (String) headers.get(CommonConstants.AUTHORIZATION_HEADER);
      CurrentThreadContext.getLogContext().setAuthorizationHeader(authToken);
      nodeFSASyncService.sendNodeFSAMapping(request);
    } catch (Exception e) {
      logger.error("Error while sending node fsa sync messages", e);
      throw e;
    }
  }
}
