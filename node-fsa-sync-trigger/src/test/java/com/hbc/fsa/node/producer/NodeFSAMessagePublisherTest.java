package com.hbc.fsa.node.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.fsa.node.pojo.NodeFSAMessage;
import com.hbc.fsa.node.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.SettableListenableFuture;

class NodeFSAMessagePublisherTest {
  @InjectMocks NodeFSAMessagePublisher publisher;
  @InjectMocks TestUtils testUtils;

  @Mock KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void publishTest() {
    SettableListenableFuture<SendResult<String, Object>> future = new SettableListenableFuture<>();
    when(kafkaTemplate.send((Message<?>) any())).thenReturn(future);
    publisher.publish(testUtils.getNodeFSAMessage(), "exampleTopic");
    verify(kafkaTemplate, times(1)).send((Message<?>) any());
    ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);
    verify(kafkaTemplate, times(1)).send(messageCaptor.capture());
    Object payload = messageCaptor.getValue().getPayload();
    Assertions.assertEquals(TestUtils.FSA_MESSAGE_NAME, ((NodeFSAMessage) payload).getEventName());
    Assertions.assertEquals(TestUtils.ORG_ID, ((NodeFSAMessage) payload).getOrgId());
    Assertions.assertEquals(TestUtils.NODE_ID, ((NodeFSAMessage) payload).getNodeId());
    Assertions.assertEquals(
        TestUtils.FSA_LIST,
        ((NodeFSAMessage) payload).getFsaCoverage().get(TestUtils.SERVICE_OPTION_1).getFsas());
  }

  @Test
  void publishNullTest() {
    publisher.publish(null, "exampleTopic");
    verify(kafkaTemplate, times(0)).send((Message<?>) any());
  }

  @Test
  void publishExceptionTest() {
    when(kafkaTemplate.send((Message<?>) any())).thenThrow(new RuntimeException("Error"));
    Assertions.assertDoesNotThrow(
        () -> publisher.publish(testUtils.getNodeFSAMessage(), "exampleTopic"));
  }
}
