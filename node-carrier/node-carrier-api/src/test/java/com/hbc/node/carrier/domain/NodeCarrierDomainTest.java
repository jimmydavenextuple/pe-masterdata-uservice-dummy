package com.hbc.node.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.node.carrier.TestUtil;
import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.exception.NodeCarrierSelectionDomainException;
import com.hbc.node.carrier.repository.NodeCarrierRepository;
import com.hbc.node.carrier.repository.NodeCarrierSelectionRepository;
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

  @Mock private NodeCarrierSelectionRepository nodeCarrierSelectionRepository;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("When node carrier is saved successfully")
  void saveNodeCarrierEntity() throws NodeCarrierDomainException {
    NodeCarrierEntity nodeCarrierEntity = testUtil.getNodeCarrierEntity();
    when(nodeCarrierRepository.save(any())).thenReturn(nodeCarrierEntity);

    NodeCarrierEntity nodeCarrier =
        nodeCarrierDomain.saveNodeCarrierEntity(testUtil.getNodeCarrierEntity());

    assertEquals(nodeCarrierEntity, nodeCarrier);
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

    assertEquals("Error while saving the node carrier", ex.getMessage());
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

    assertEquals(nodeCarrierEntity, nodeCarrier.get());
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

    assertEquals("Error while finding node carrier", ex.getMessage());
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

    assertEquals("Error while deleting node carrier", ex.getMessage());
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

    assertEquals(2, nodeCarrierEntities.size());
    assertEquals(nodeCarrierEntityList, nodeCarrierEntities);
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

    assertEquals("Error while finding node carrier list", ex.getMessage());
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node carrier list is fetched successfully by node id and org id")
  void findNodeCarrierByNodeIdAndOrgIdTest() throws NodeCarrierDomainException {
    List<NodeCarrierEntity> nodeCarrierEntityList = testUtil.getNodeCarrierEntityList2();
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndCarrierServiceId(
            anyString(), anyString(), anyString()))
        .thenReturn(nodeCarrierEntityList);

    List<NodeCarrierEntity> responseNodeCarrierList =
        nodeCarrierDomain.findNodeCarrierByNodeIdAndOrgId(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(nodeCarrierEntityList.size(), responseNodeCarrierList.size());
    assertEquals(TestUtil.NODE_ID, responseNodeCarrierList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, responseNodeCarrierList.get(0).getOrgId());
    assertEquals("", responseNodeCarrierList.get(0).getCarrierServiceId());
    assertEquals(nodeCarrierEntityList, responseNodeCarrierList);

    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndCarrierServiceId(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is some error in finding node carrier list by node id and org id")
  void findNodeCarrierByNodeIdAndOrgIdTestException() {
    when(nodeCarrierRepository.findByNodeIdAndOrgIdAndCarrierServiceId(
            anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierDomainException.class,
            () ->
                nodeCarrierDomain.findNodeCarrierByNodeIdAndOrgId(
                    TestUtil.NODE_ID, TestUtil.ORG_ID));

    assertEquals("Error while fetching node carrier list for nodeId and orgId", ex.getMessage());
    verify(nodeCarrierRepository, times(1))
        .findByNodeIdAndOrgIdAndCarrierServiceId(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When node carrier is saved successfully")
  void saveNodeCarrierSelectionEntityTest() throws NodeCarrierDomainException {
    NodeCarrierSelectionEntity nodeCarrierSelectionEntity =
        testUtil.getNodeCarrierSelectionEntity();
    when(nodeCarrierSelectionRepository.save(any())).thenReturn(nodeCarrierSelectionEntity);

    NodeCarrierSelectionEntity nodeCarrierEntity =
        nodeCarrierDomain.saveNodeCarrierSelectionEntity(testUtil.getNodeCarrierSelectionEntity());

    assertEquals(nodeCarrierEntity, nodeCarrierSelectionEntity);
    verify(nodeCarrierSelectionRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When there is some error in finding node carrier list")
  void findNodeCarrierSelectionEntityTest() {
    when(nodeCarrierSelectionRepository.findByOrgIdAndServiceOptionAndDestinationGeoZone(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getNodeCarrierSelectionEntity()));

    List<NodeCarrierSelectionEntity> responseNodeCarrierList =
        nodeCarrierDomain.findNodeCarrierByOrgIdAndServiceOptionAndDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.SERVICE_OPTION, TestUtil.DESTINATION_GEOZONE);

    assertEquals(TestUtil.SERVICE_OPTION, responseNodeCarrierList.get(0).getServiceOption());
    assertEquals(TestUtil.ORG_ID, responseNodeCarrierList.get(0).getOrgId());
    assertEquals(
        List.of(testUtil.getNodeCarrierSelectionEntity()).size(), responseNodeCarrierList.size());

    verify(nodeCarrierSelectionRepository, times(1))
        .findByOrgIdAndServiceOptionAndDestinationGeoZone(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  void findNodeCarrierSelectionDetailsTest() throws NodeCarrierSelectionDomainException {
    when(nodeCarrierSelectionRepository
            .findByOrgIdAndServiceOptionAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarrierSelectionEntity()));

    Optional<NodeCarrierSelectionEntity> response =
        nodeCarrierDomain.findNodeCarrierSelectionDetails(
            TestUtil.ORG_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertEquals(TestUtil.ORG_ID, response.get().getOrgId());
    assertEquals(TestUtil.SERVICE_OPTION, response.get().getServiceOption());
    verify(nodeCarrierSelectionRepository, times(1))
        .findByOrgIdAndServiceOptionAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
  }

  @Test
  void findNodeCarrierSelectionDetailsExceptionTest() throws NodeCarrierSelectionDomainException {
    when(nodeCarrierSelectionRepository
            .findByOrgIdAndServiceOptionAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierSelectionDomainException.class,
            () ->
                nodeCarrierDomain.findNodeCarrierSelectionDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SERVICE_OPTION,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE));

    assertEquals("Error while finding node carrier selection", ex.getMessage());
    verify(nodeCarrierSelectionRepository, times(1))
        .findByOrgIdAndServiceOptionAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
  }

  @Test
  void deleteNodeCarrierSelectionEntityTest() throws NodeCarrierSelectionDomainException {
    doNothing().when(nodeCarrierSelectionRepository).delete(any());
    nodeCarrierDomain.deleteNodeCarrierSelectionEntity(testUtil.getNodeCarrierSelectionEntity());

    verify(nodeCarrierSelectionRepository, times(1)).delete(any());
  }

  @Test
  void deleteNodeCarrierSelectionEntityExceptionTest() throws NodeCarrierSelectionDomainException {
    doThrow(new RuntimeException("error while deleting"))
        .when(nodeCarrierSelectionRepository)
        .delete(any());

    Exception ex =
        Assertions.assertThrows(
            NodeCarrierSelectionDomainException.class,
            () ->
                nodeCarrierDomain.deleteNodeCarrierSelectionEntity(
                    testUtil.getNodeCarrierSelectionEntity()));

    assertEquals("Error while deleting node carrier selection", ex.getMessage());
    verify(nodeCarrierSelectionRepository, times(1)).delete(any());
  }
}
