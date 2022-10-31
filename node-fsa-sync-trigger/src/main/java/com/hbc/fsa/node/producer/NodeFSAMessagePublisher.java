package com.hbc.fsa.node.producer;

import com.hbc.fsa.node.pojo.NodeFSAMessage;
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
public class NodeFSAMessagePublisher {
  private static final Logger logger = LoggerFactory.getLogger(NodeFSAMessagePublisher.class);

  @Autowired public KafkaTemplate<String, Object> kafkaTemplate;

  public void publish(NodeFSAMessage nodeFSAMessage, String nodeFSAUpdatesTopic) {
    try {
      if (Objects.nonNull(nodeFSAMessage)) {
        logger.debug("Sending node fsa message: {}", nodeFSAMessage);

        kafkaTemplate.send(
            MessageBuilder.withPayload(nodeFSAMessage)
                .setHeader(KafkaHeaders.TOPIC, nodeFSAUpdatesTopic)
                .build());
      }
    } catch (Exception e) {
      logger.debug("Error while publishing stock flip event.", e);
    }
  }
}
