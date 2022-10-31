package com.hbc.fsa.node.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.fsa.node.pojo.NodeFSASyncRequest;
import com.hbc.fsa.node.service.NodeFSASyncService;
import com.hbc.fsa.node.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    consumer.consumerNodeFSASyncRequest(request, null);
    verify(nodeFSASyncService, times(1)).sendNodeFSAMapping(any());
  }

  @Test
  void consumerNodeFSASyncRequestExceptionTest() {
    NodeFSASyncRequest request = testUtils.getNodeFSASyncRequest();
    doThrow(new RuntimeException("error")).when(nodeFSASyncService).sendNodeFSAMapping(any());
    Assertions.assertThrows(
        RuntimeException.class, () -> consumer.consumerNodeFSASyncRequest(request, null));
    verify(nodeFSASyncService, times(1)).sendNodeFSAMapping(any());
  }
}
