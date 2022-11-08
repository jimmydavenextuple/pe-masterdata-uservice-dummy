package com.nextuple.fsa.node.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import com.nextuple.fsa.node.service.NodeFSASyncService;
import com.nextuple.fsa.node.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

class NodeFSASyncConsumerTest {
  @InjectMocks NodeFSASyncConsumer consumer;
  @InjectMocks TestUtils testUtils;

  @Mock NodeFSASyncService nodeFSASyncService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void consumerNodeFSASyncRequestTest() {
    NodeFSASyncRequest request = testUtils.getNodeFSASyncRequest();
    doNothing().when(nodeFSASyncService).sendNodeFSAMapping(any());
    KafkaMessageHeaders headers = mock(KafkaMessageHeaders.class);
    headers.put(CommonConstants.AUTHORIZATION_HEADER, "xyz");
    consumer.consumerNodeFSASyncRequest(request, headers);
    verify(nodeFSASyncService, times(1)).sendNodeFSAMapping(any());
  }

  @Test
  void consumerNodeFSASyncRequestExceptionTest() {
    NodeFSASyncRequest request = testUtils.getNodeFSASyncRequest();
    KafkaMessageHeaders headers = mock(KafkaMessageHeaders.class);
    headers.put(CommonConstants.AUTHORIZATION_HEADER, "xyz");
    doThrow(new RuntimeException("error")).when(nodeFSASyncService).sendNodeFSAMapping(any());
    Assertions.assertThrows(
        RuntimeException.class, () -> consumer.consumerNodeFSASyncRequest(request, headers));
    verify(nodeFSASyncService, times(1)).sendNodeFSAMapping(any());
  }
}
