package com.nextuple.pe.masterdata.service.domain;

import com.nextuple.pe.masterdata.domain.NodeDomain;
import com.nextuple.pe.masterdata.domain.entity.NodeEntity;
import com.nextuple.pe.masterdata.domain.repository.NodeRepository;
import com.nextuple.pe.masterdata.exception.NodeDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
}
