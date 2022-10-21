package com.hbc.node.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.node.TestUtil;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.node.repository.NodeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class NodeDomainTest {

  @InjectMocks private NodeDomain nodeDomain;
  @InjectMocks private TestUtil testUtil;

  @Mock private NodeRepository nodeRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveNodeTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeRepository.save(any())).thenReturn(nodeEntity);

    NodeEntity node = nodeDomain.saveNodeEntity(nodeEntity);
    Assertions.assertEquals(nodeEntity, node);

    verify(nodeRepository, times(1)).save(any());
  }

  @Test
  void saveNodeExceptionTest() {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Exception exception =
        assertThrows(NodeDomainException.class, () -> nodeDomain.saveNodeEntity(nodeEntity));
    Assertions.assertEquals("Error while saving the node", exception.getMessage());
    verify(nodeRepository, times(1)).save(any());
  }

  @Test
  void getNodeDetailsTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    when(nodeRepository.findByNodeIdAndOrgId(any(), any())).thenReturn(Optional.of(nodeEntity));

    Optional<NodeEntity> optionalNodeEntity =
        nodeDomain.findNodeByNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(nodeEntity, optionalNodeEntity.get());

    verify(nodeRepository, times(1)).findByNodeIdAndOrgId(any(), any());
  }

  @Test
  void getNodeDetailsTestException() {
    when(nodeRepository.findByNodeIdAndOrgId(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodeDomain.findNodeByNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding node", exception.getMessage());
    verify(nodeRepository, times(1)).findByNodeIdAndOrgId(any(), any());
  }

  @Test
  void nodeDeletionTest() throws NodeDomainException {
    NodeEntity nodeEntity = testUtil.getNodeEntity();
    doNothing().when(nodeRepository).delete(any());
    nodeDomain.deleteNode(nodeEntity);

    verify(nodeRepository, times(1)).delete(any());
  }

  @Test
  void nodeDeletionTestException() {
    doThrow(new RuntimeException("error while deleting")).when(nodeRepository).delete(any());

    Exception exception =
        assertThrows(
            NodeDomainException.class, () -> nodeDomain.deleteNode(testUtil.getNodeEntity()));
    Assertions.assertEquals("Error while deleting node", exception.getMessage());
    verify(nodeRepository, times(1)).delete(any());
  }

  @Test
  void getNodeByOrgIdDefaultSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);

    Page<NodeDto> response =
        nodeDomain.getNodeByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC");

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdDESCSortOrderTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(nodeEntityPage);

    Page<NodeDto> response =
        nodeDomain.getNodeByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "desc");

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: DESC", response.getSort().toString());

    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getNodeByOrgIdTestException() {
    when(nodeRepository.findNodeByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));

    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodeDomain.getNodeByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while finding node list", exception.getMessage());
    verify(nodeRepository, times(1)).findNodeByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getAllNodesPaginatedTest() throws NodeDomainException {
    List<NodeEntity> nodeEntityList = testUtil.getNodeEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<NodeEntity> nodeEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(nodeRepository.findAll(any(Pageable.class))).thenReturn(nodeEntityPage);

    Page<NodeResponse> response = nodeDomain.getAllNodesPaginated(1, 1, TestUtil.SORT_BY, "ASC");

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("nodeId: ASC", response.getSort().toString());

    verify(nodeRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodesPaginatedTestException() {
    when(nodeRepository.findAll(any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node list"));

    Exception exception =
        assertThrows(
            NodeDomainException.class,
            () -> nodeDomain.getAllNodesPaginated(1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while finding node list", exception.getMessage());
    verify(nodeRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodeEntitiesTest() throws NodeDomainException {
    List<NodeEntity> nodeEntities = testUtil.getNodeEntityList();
    when(nodeRepository.findAllNodeEntities(any())).thenReturn(nodeEntities);

    List<NodeEntity> responseList = nodeDomain.getAllNodeEntities(2);
    Assertions.assertEquals(2, responseList.size());
    Assertions.assertEquals(nodeEntities.get(0).getNodeId(), responseList.get(0).getNodeId());

    verify(nodeRepository, times(1)).findAllNodeEntities(any());
  }

  @Test
  void getAllNodeEntitiesExceptionTest() {
    when(nodeRepository.findAllNodeEntities(any()))
        .thenThrow(new RuntimeException("Error while fetching all node records"));

    Exception exception =
        assertThrows(NodeDomainException.class, () -> nodeDomain.getAllNodeEntities(2));
    Assertions.assertEquals("Error while fetching all node records", exception.getMessage());
    verify(nodeRepository, times(1)).findAllNodeEntities(any());
  }
}
