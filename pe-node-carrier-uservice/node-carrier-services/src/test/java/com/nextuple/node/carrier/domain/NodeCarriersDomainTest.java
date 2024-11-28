/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import com.nextuple.node.carrier.repository.NodeCarriersRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NodeCarriersDomainTest {

  @InjectMocks private NodeCarriersDomain nodeCarriersDomain;

  @Mock private NodeCarriersRepository nodeCarriersRepository;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("When node carrier entity is saved successfully")
  void saveNodeCarrierEntityTest() throws CommonServiceException {
    NodeCarriersEntity nodeCarriersEntity = testUtil.getNodeCarriersEntity();
    when(nodeCarriersRepository.save(any())).thenReturn(nodeCarriersEntity);

    NodeCarriersEntity savedNodeCarrierEntity =
        nodeCarriersDomain.saveNodeCarrierEntity(testUtil.getNodeCarriersEntity());

    assertEquals(nodeCarriersEntity, savedNodeCarrierEntity);
    verify(nodeCarriersRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When there is an error in saving node carrier entity")
  void saveNodeCarrierEntityExceptionTest() {
    when(nodeCarriersRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> nodeCarriersDomain.saveNodeCarrierEntity(testUtil.getNodeCarriersEntity()));

    assertEquals("Error while saving the node carrier", ex.getMessage());
    verify(nodeCarriersRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When node carrier entity is deleted successfully")
  void deleteNodeCarrierEntityTest() throws CommonServiceException {
    NodeCarriersEntity entity = new NodeCarriersEntity();
    when(nodeCarriersRepository.deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(entity));

    nodeCarriersDomain.deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
        TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.SERVICE_OPTION);

    verify(nodeCarriersRepository, times(1))
        .deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is an error in deleting node carrier entity")
  void deleteNodeCarrierEntityExceptionTest() {
    doThrow(new RuntimeException("Error while deleting"))
        .when(nodeCarriersRepository)
        .deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain
                    .deleteNodeCarrierEntityByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
                        TestUtil.NODE_ID,
                        TestUtil.ORG_ID,
                        TestUtil.CARRIER_SERVICE_ID,
                        TestUtil.SERVICE_OPTION));

    assertEquals("Error while deleting the node carrier", ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When node carrier details are found successfully")
  void findNodeCarrierDetailsTest() throws CommonServiceException {
    NodeCarriersEntity nodeCarriersEntity = testUtil.getNodeCarriersEntity();
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(nodeCarriersEntity));

    Optional<NodeCarriersEntity> foundNodeCarrierEntity =
        nodeCarriersDomain.findNodeCarrierDetails(
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    assertEquals(nodeCarriersEntity, foundNodeCarrierEntity.get());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is an error in finding node carrier details")
  void findNodeCarrierDetailsExceptionTest() {
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while finding details"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain.findNodeCarrierDetails(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    assertEquals("Error while finding the node carrier", ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When node carrier details are found successfully by filter")
  void filterAndGetNodeCarrierDetailsTest() throws CommonServiceException {
    NodeCarriersEntity nodeCarriersEntity = testUtil.getNodeCarriersEntity();
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(nodeCarriersEntity));

    Optional<NodeCarriersEntity> foundNodeCarrierEntity =
        nodeCarriersDomain.filterAndGetNodeCarrierDetails(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    assertEquals(nodeCarriersEntity, foundNodeCarrierEntity.get());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is an error in finding node carrier details by filter")
  void filterAndGetNodeCarrierDetailsExceptionTest() {
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while finding details"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain.filterAndGetNodeCarrierDetails(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));

    assertEquals("Error while finding the node carrier", ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
            anyString(), anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When node carriers are found by orgId and nodeId")
  void findByOrgIdAndNodeId_Success() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntities =
        Arrays.asList(testUtil.getNodeCarriersEntity(), testUtil.getNodeCarriersEntity2());
    when(nodeCarriersRepository.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenReturn(nodeCarriersEntities);

    List<NodeCarriersEntity> foundNodeCarriers =
        nodeCarriersDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);

    assertNotNull(foundNodeCarriers);
    assertEquals(nodeCarriersEntities.size(), foundNodeCarriers.size());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }

  @Test
  @DisplayName("When an error occurs while finding node carriers by orgId and nodeId")
  void findByOrgIdAndNodeId_Exception() {

    when(nodeCarriersRepository.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID))
        .thenThrow(new RuntimeException("Error"));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarriersDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
            });
    assertEquals("Error while finding the node carrier", ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
  }

  @Test
  @DisplayName("When node carrier list is fetched successfully")
  void findNodeCarriersListByOrgIdAndNodeIdAndServiceOptionTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntityList = testUtil.getNodeCarriersEntityList();
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndServiceOption(
            anyString(), anyString(), anyString()))
        .thenReturn(nodeCarriersEntityList);

    List<NodeCarriersEntity> responseNodeCarriersList =
        nodeCarriersDomain.findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(nodeCarriersEntityList.size(), responseNodeCarriersList.size());
    Assertions.assertEquals(TestUtil.NODE_ID, responseNodeCarriersList.get(0).getNodeId());
    Assertions.assertEquals(TestUtil.ORG_ID, responseNodeCarriersList.get(0).getOrgId());
    Assertions.assertEquals(nodeCarriersEntityList, responseNodeCarriersList);

    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("When there is some error in finding node carriers list")
  void findNodeCarriersListByOrgIdAndNodeIdAndServiceOptionTestException() {
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndServiceOption(
            anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain.findNodeCarriersListByOrgIdAndNodeIdAndServiceOption(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    Assertions.assertEquals("Error while finding the node carriers", ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(anyString(), anyString(), anyString());
  }

  @Test
  void getAllNodeCarriersTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntities = testUtil.getNodeCarriersEntityList();

    Page<NodeCarriersEntity> page = new PageImpl<>(nodeCarriersEntities);
    when(nodeCarriersRepository.findAll(any(Pageable.class))).thenReturn(page);

    List<NodeCarriersEntity> response = nodeCarriersDomain.getAllNodeCarriers(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(nodeCarriersEntities.get(0).getNodeId(), response.get(0).getNodeId());
    verify(nodeCarriersRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodeCarriersExceptionTest() {
    when(nodeCarriersRepository.findAll(any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching node carrier list"));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class, () -> nodeCarriersDomain.getAllNodeCarriers(2));

    Assertions.assertEquals("Error while fetching node carriers list", ex.getMessage());
    verify(nodeCarriersRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void getAllNodeCarriersByOrgIdCarrierServiceIdTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntityList = testUtil.getNodeCarriersEntityList();

    when(nodeCarriersRepository.findByOrgIdAndCarrierServiceId(any(), any()))
        .thenReturn(nodeCarriersEntityList);

    List<NodeCarriersEntity> responseNodeCarrierList =
        nodeCarriersDomain.getAllNodeCarriersByOrgIdCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    assertEquals(nodeCarriersEntityList.size(), responseNodeCarrierList.size());
    assertEquals(TestUtil.NODE_ID, responseNodeCarrierList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, responseNodeCarrierList.get(0).getOrgId());
    assertEquals(nodeCarriersEntityList, responseNodeCarrierList);

    verify(nodeCarriersRepository, times(1)).findByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  void getAllNodeCarriersByOrgIdCarrierServiceIdTestException() {
    when(nodeCarriersRepository.findByOrgIdAndCarrierServiceId(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching node carrier details"));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain.getAllNodeCarriersByOrgIdCarrierServiceId(TestUtil.ORG_ID, ""));

    assertEquals(
        "Error while fetching node carrier details for orgId and carrierServiceId",
        ex.getMessage());
    verify(nodeCarriersRepository, times(1)).findByOrgIdAndCarrierServiceId(any(), any());
  }

  @Test
  @Description("Fetch Node Carriers by orgId, nodeId and carrierServiceId - Happy Path")
  void getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceIdTest() throws CommonServiceException {
    List<NodeCarriersEntity> nodeCarriersEntityList = testUtil.getNodeCarriersEntityList();
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenReturn(nodeCarriersEntityList);
    List<NodeCarriersEntity> responseNodeCarrierList =
        nodeCarriersDomain.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID);
    assertEquals(nodeCarriersEntityList.size(), responseNodeCarrierList.size());
    assertEquals(TestUtil.NODE_ID, responseNodeCarrierList.get(0).getNodeId());
    assertEquals(TestUtil.ORG_ID, responseNodeCarrierList.get(0).getOrgId());
    assertEquals(TestUtil.CARRIER_SERVICE_ID, responseNodeCarrierList.get(0).getCarrierServiceId());
    assertEquals(nodeCarriersEntityList, responseNodeCarrierList);
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceId(any(), any(), any());
  }

  @Test
  @Description("Fetch Node Carriers by orgId, nodeId and carrierServiceId - Exception Scenario")
  void getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceIdException() {
    when(nodeCarriersRepository.findByOrgIdAndNodeIdAndCarrierServiceId(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching node carrier details"));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarriersDomain.getAllNodeCarriersByOrgIdNodeIdAndCarrierServiceId(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID));
    assertEquals(
        "Error while fetching node carrier details for orgId, nodeId and carrierServiceId",
        ex.getMessage());
    verify(nodeCarriersRepository, times(1))
        .findByOrgIdAndNodeIdAndCarrierServiceId(any(), any(), any());
  }
}
