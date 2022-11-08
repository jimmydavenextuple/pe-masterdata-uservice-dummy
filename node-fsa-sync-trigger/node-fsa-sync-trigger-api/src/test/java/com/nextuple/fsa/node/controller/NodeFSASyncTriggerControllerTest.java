package com.nextuple.fsa.node.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import com.nextuple.fsa.node.producer.NodeFSASyncRequestPublisher;
import com.nextuple.fsa.node.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class NodeFSASyncTriggerControllerTest {

  @InjectMocks NodeFSASyncTriggerController controller;
  @InjectMocks TestUtils testUtil;

  @Mock NodeFSASyncRequestPublisher nodeFSASyncRequestPublisher;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void nodeFSASync() {
    doNothing().when(nodeFSASyncRequestPublisher).publish(any(), any());
    ResponseEntity<BaseResponse<String>> responseEntity =
        controller.nodeFSASync(new NodeFSASyncRequest());
    Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
    verify(nodeFSASyncRequestPublisher, times(1)).publish(any(), any());
  }

  @Test
  void nodeFSASyncExceptionTeest() {
    doThrow(new RuntimeException("Error")).when(nodeFSASyncRequestPublisher).publish(any(), any());
    NodeFSASyncRequest request = new NodeFSASyncRequest();
    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          controller.nodeFSASync(request);
        });
  }
}
