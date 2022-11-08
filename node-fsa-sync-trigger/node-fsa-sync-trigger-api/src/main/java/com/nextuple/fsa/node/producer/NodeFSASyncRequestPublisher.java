package com.nextuple.fsa.node.producer;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NodeFSASyncRequestPublisher {
  private static final Logger logger = LoggerFactory.getLogger(NodeFSASyncRequestPublisher.class);

  @Autowired public KafkaTemplate<String, Object> kafkaTemplate;

  public void publish(NodeFSASyncRequest nodeFSASyncRequest, String nodeFSASyncRequestTopic) {
    try {
      if (Objects.nonNull(nodeFSASyncRequest)) {
        logger.debug("Sending node fsa sync request: {}", nodeFSASyncRequest);

        kafkaTemplate.send(
            MessageBuilder.withPayload(nodeFSASyncRequest)
                .setHeader(KafkaHeaders.TOPIC, nodeFSASyncRequestTopic)
                .setHeader(
                    CommonConstants.AUTHORIZATION_HEADER,
                    CurrentThreadContext.getLogContext().getAuthorizationHeader())
                .build());
      }
    } catch (Exception e) {
      logger.debug("Error while publishing node fsa sync request.", e);
    }
  }
}
