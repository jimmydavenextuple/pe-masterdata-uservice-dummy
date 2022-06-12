package com.nextuple.pe.masterdata.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.domain.node.NodeResponse;
import com.nextuple.pe.masterdata.controller.NodeController;
import com.nextuple.pe.masterdata.domain.inbound.NodeRequest;
import com.nextuple.pe.masterdata.domain.inbound.NodeUpdationRequest;
import com.nextuple.pe.masterdata.exception.NodeDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.NodeService;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeControllerTest {

  @InjectMocks private NodeController nodeController;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeService nodeService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNodeTest() throws NodeDomainException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeService.createNode(any(NodeRequest.class))).thenReturn(testUtil.getNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.createNode(nodeRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).createNode(any());
  }

  @Test
  void createNodeExceptionTest() throws NodeDomainException {
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeService.createNode(any(NodeRequest.class)))
        .thenThrow(new RuntimeException("Failed to create node"));

    Exception exception =
        Assertions.assertThrows(Exception.class, () -> nodeController.createNode(nodeRequest));
    Assertions.assertEquals("Failed to create node", exception.getMessage());

    verify(nodeService, times(1)).createNode(any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTest() throws NodeDomainException, CommonServiceException {
    NodeResponse nodeResponse = testUtil.getNodeResponse();
    when(nodeService.getNodeDetails(any(), any())).thenReturn(nodeResponse);

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(nodeResponse, responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdExceptionTest()
      throws NodeDomainException, CommonServiceException {
    when(nodeService.getNodeDetails(any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch node details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeController.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch node details", exception.getMessage());
    verify(nodeService, times(1)).getNodeDetails(any(), any());
  }

  @Test
  void updateNodeTest() throws NodeDomainException, CommonServiceException {
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
        .thenReturn(testUtil.getUpdatedNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getUpdatedNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).updateNodeDetails(anyString(), anyString(), any());
  }

  @Test
  void updateNodeExceptionTest() throws NodeDomainException, CommonServiceException {
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodeService.updateNodeDetails(anyString(), anyString(), any(NodeUpdationRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the node details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeController.updateNodeDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest));
    Assertions.assertEquals("Unable to update the node details", exception.getMessage());

    verify(nodeService, times(1)).updateNodeDetails(anyString(), anyString(), any());
  }

  @Test
  void deleteNodeTest() throws NodeDomainException, CommonServiceException {
    when(nodeService.deleteNode(any(), any())).thenReturn(testUtil.getNodeResponse());

    ResponseEntity<BaseResponse<NodeResponse>> responseEntity =
        nodeController.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(testUtil.getNodeResponse(), responseEntity.getBody().getPayload());

    verify(nodeService, times(1)).deleteNode(any(), any());
  }

  @Test
  void deleteNodeExceptionTest() throws NodeDomainException, CommonServiceException {
    when(nodeService.deleteNode(any(), any()))
        .thenThrow(new RuntimeException("Error while deleting node"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class, () -> nodeController.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while deleting node", exception.getMessage());

    verify(nodeService, times(1)).deleteNode(any(), any());
  }
}
