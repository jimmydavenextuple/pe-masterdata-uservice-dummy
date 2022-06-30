package com.hbc.node.carrier.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.node.carrier.TestUtil;
import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.repository.NodeCarrierRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierDomainTest {

  @InjectMocks private NodeCarrierDomain nodeCarrierDomain;

  @Mock private NodeCarrierRepository nodeCarrierRepository;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("When node carrier is saved successfully")
  void saveNodeCarrierEntity() throws NodeCarrierDomainException {
    NodeCarrierEntity nodeCarrierEntity = testUtil.getNodeCarrierEntity();
    when(nodeCarrierRepository.save(any())).thenReturn(nodeCarrierEntity);

    NodeCarrierEntity nodeCarrier =
        nodeCarrierDomain.saveNodeCarrierEntity(testUtil.getNodeCarrierEntity());

    Assertions.assertEquals(nodeCarrierEntity, nodeCarrier);
    verify(nodeCarrierRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When there is some error in saving node carrier")
  void saveNodeCarrierEntityTestException() {
    when(nodeCarrierRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierDomainException.class,
            () -> nodeCarrierDomain.saveNodeCarrierEntity(testUtil.getNodeCarrierEntity()));

    Assertions.assertEquals("Error while saving the node carrier", ex.getMessage());
    verify(nodeCarrierRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When node carrier is fetched successfully")
  void findNodeCarrierDetailsTest() throws NodeCarrierDomainException {
    NodeCarrierEntity nodeCarrierEntity = testUtil.getNodeCarrierEntity();
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(nodeCarrierEntity));

    Optional<NodeCarrierEntity> nodeCarrier =
        nodeCarrierDomain.findNodeCarrierDetails(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(nodeCarrierEntity, nodeCarrier.get());
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in finding node carrier")
  void findNodeCarrierDetailsTestException() {
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
            any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierDomainException.class,
            () ->
                nodeCarrierDomain.findNodeCarrierDetails(
                    TestUtil.NODE_ID,
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Error while finding node carrier", ex.getMessage());
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(any(), any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier is deleted successfully")
  void deleteNodeCarrierEntityTest() throws NodeCarrierDomainException {
    doNothing().when(nodeCarrierRepository).delete(any());

    nodeCarrierDomain.deleteNodeCarrierEntity(testUtil.getNodeCarrierEntity());

    verify(nodeCarrierRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("When there is some error in deleting node carrier")
  void deleteNodeCarrierEntityTestException() {
    doThrow(new RuntimeException("error while deleting")).when(nodeCarrierRepository).delete(any());

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierDomainException.class,
            () -> nodeCarrierDomain.deleteNodeCarrierEntity(testUtil.getNodeCarrierEntity()));

    Assertions.assertEquals("Error while deleting node carrier", ex.getMessage());
    verify(nodeCarrierRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("When node carrier list is fetched successfully ")
  void findNodeCarrierByNodeIdAOrgIdAndServiceOptionTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntityList = testUtil.getNodeCarrierEntityList();
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndServiceOption(any(), any(), any()))
        .thenReturn(nodeCarrierEntityList);

    List<NodeCarrierEntity> nodeCarrierEntities =
        nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(2, nodeCarrierEntities.size());
    Assertions.assertEquals(nodeCarrierEntityList, nodeCarrierEntities);
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in finding node carrier list")
  void findNodeCarrierByNodeIdAOrgIdAndServiceOptionTestException() {
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndServiceOption(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierDomainException.class,
            () ->
                nodeCarrierDomain.findNodeCarrierByNodeIdAOrgIdAndServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Error while finding node carrier list", ex.getMessage());
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndServiceOption(any(), any(), any());
  }
}
