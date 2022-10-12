package com.hbc.node.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.node.TestUtil;
import com.hbc.node.domain.NodeDomain;
import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

class NodeServiceTest {

  @InjectMocks private NodeService nodeService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeDomain nodeDomain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Set<String> nodeTypes = Set.of("STORE", "FC", "MFC", "DROPSHIP VENDOR");
    ReflectionTestUtils.setField(nodeService, "nodeTypes", nodeTypes);
  }

  @Test
  void createNodeTest() throws NodeDomainException, CommonServiceException {
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

  @Test
  void getNodeListByOrgIdTest() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDto> nodeDtoPage = new PageImpl<>(nodeDtoList, pageable, nodeDtoList.size());

    when(nodeDomain.getNodeByOrgId(any(), any(), any(), any(), any())).thenReturn(nodeDtoPage);

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    Assertions.assertEquals(nodeDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodeDomain, Mockito.times(1)).getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeListByOrgIdDefaultSortOrderTest() throws NodeDomainException, CommonServiceException {
    List<NodeDto> nodeDtoList = testUtil.getNodeDtoList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeDto> nodeDtoPage = new PageImpl<>(nodeDtoList, pageable, nodeDtoList.size());

    when(nodeDomain.getNodeByOrgId(any(), any(), any(), any(), any())).thenReturn(nodeDtoPage);

    Page<NodeDto> response =
        nodeService.getNodeListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.DEFAULT_SORT_ORDER);

    Assertions.assertEquals(nodeDtoList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodeDomain, Mockito.times(1)).getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getNodeListByOrgIdExceptionTest() throws NodeDomainException {
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeService.getNodeListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "invalid sort order"));

    Assertions.assertEquals(
        "Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(nodeDomain, Mockito.times(0)).getNodeByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getAllNodeCacheKeysTest() throws NodeDomainException {
    List<NodeEntity> nodeEntities = testUtil.getNodeEntityList();

    when(nodeDomain.getAllNodeEntities(any())).thenReturn(nodeEntities);

    List<NodeCacheKeyDto> response = nodeService.getAllNodeCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(nodeEntities.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeDomain, times(1)).getAllNodeEntities(any());
  }

  @Test
  void createNodeTest_validateFields() throws NodeDomainException {
    NodeRequest nodeRequest1 = testUtil.getNodeRequest();
    nodeRequest1.setCountry("IND");
    NodeRequest nodeRequest2 = testUtil.getNodeRequest();
    nodeRequest2.setTimezone("IST");
    NodeRequest nodeRequest3 = testUtil.getNodeRequest();
    nodeRequest3.setNodeType(" ");
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest1));
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest2));
    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeService.createNode(nodeRequest3));
    verify(nodeDomain, times(0)).saveNodeEntity(any(NodeEntity.class));
  }
}
