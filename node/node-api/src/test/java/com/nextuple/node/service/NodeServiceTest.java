package com.nextuple.node.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.TestUtil;
import com.nextuple.node.domain.NodeDomain;
import com.nextuple.node.domain.entity.NodeEntity;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.exception.NodeDomainException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NodeServiceTest {

  @InjectMocks private NodeService nodeService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeDomain nodeDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNodeTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    NodeRequest nodeRequest = testUtil.getNodeRequest();
    when(nodeDomain.saveNodeEntity(any(NodeEntity.class))).thenReturn(nodeEntity);

    NodeResponse received_dto = nodeService.createNode(testUtil.getNodeRequest());
    Assertions.assertEquals(nodeRequest.getNodeId(), received_dto.getNodeId());
    verify(nodeDomain, times(1)).saveNodeEntity(any(NodeEntity.class));
  }

  @Test
  void updateNodeDetailsTest() throws NodeDomainException, CommonServiceException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    NodeEntity updatedNodeEntity = testUtil.getUpdatedNodeEntity();
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.of(nodeEntity));
    when(nodeDomain.saveNodeEntity(any())).thenReturn(updatedNodeEntity);

    NodeResponse nodeResponse =
        nodeService.updateNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest);
    Assertions.assertEquals(testUtil.getUpdatedNodeResponse(), nodeResponse);

    verify(nodeDomain, times(1)).saveNodeEntity(any(NodeEntity.class));
    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void updateNodeDetailsTestNodeNotFoundException() throws NodeDomainException {
    NodeUpdationRequest nodeUpdationRequest = testUtil.getNodeUpdationRequest();
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeService.updateNodeDetails(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, nodeUpdationRequest));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodeDomain, times(0)).saveNodeEntity(any(NodeEntity.class));
    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTest() throws NodeDomainException, CommonServiceException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.of(nodeEntity));

    NodeResponse nodeResponse = nodeService.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(testUtil.getNodeResponse(), nodeResponse);
    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeDetailsByNodeIdAndOrgIdTestException() throws NodeDomainException {
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.getNodeDetails(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void deleteNodeTest() throws NodeDomainException, CommonServiceException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.of(nodeEntity));

    NodeResponse nodeResponse = nodeService.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(testUtil.getNodeResponse(), nodeResponse);
    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }

  @Test
  void deleteNodeTestException() throws NodeDomainException {
    when(nodeDomain.findNodeByNodeIdAndOrgId(any(), any())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeService.deleteNode(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Node not found with given details", exception.getMessage());

    verify(nodeDomain, times(1)).findNodeByNodeIdAndOrgId(any(), any());
  }
}
