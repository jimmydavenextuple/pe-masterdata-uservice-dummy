/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionEntity;
import com.nextuple.node.carrier.repository.NodeServiceOptionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionsDomainTest {

  @InjectMocks private NodeServiceOptionsDomain nodeServiceOptionsDomain;

  @Mock private NodeServiceOptionRepository nodeServiceOptionRepository;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("When node service option entity is saved successfully")
  void saveNodeServiceOptionEntityTest() throws CommonServiceException {
    NodeServiceOptionEntity nodeServiceOptionEntity = testUtil.getNodeServiceOptionEntity();
    when(nodeServiceOptionRepository.save(any())).thenReturn(nodeServiceOptionEntity);

    NodeServiceOptionEntity savedNodeServiceOptionEntity =
        nodeServiceOptionsDomain.saveNodeServiceOptionEntity(nodeServiceOptionEntity);

    assertEquals(nodeServiceOptionEntity, savedNodeServiceOptionEntity);
    verify(nodeServiceOptionRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When there is an error in saving node service option entity")
  void saveNodeServiceOptionEntityExceptionTest() {
    when(nodeServiceOptionRepository.save(any()))
        .thenThrow(new RuntimeException("Error while saving"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionsDomain.saveNodeServiceOptionEntity(
                    testUtil.getNodeServiceOptionEntity()));

    verify(nodeServiceOptionRepository, times(1)).save(any());
    assertEquals("Error while saving the node service option", ex.getMessage());
  }

  @Test
  @DisplayName("When node service option entity is deleted successfully")
  void deleteNodeServiceOptionEntityTest() throws CommonServiceException {
    NodeServiceOptionEntity entity = new NodeServiceOptionEntity();
    when(nodeServiceOptionRepository.deleteByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(Optional.of(entity));

    nodeServiceOptionsDomain.deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(
        TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);

    verify(nodeServiceOptionRepository, times(1))
        .deleteByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is an error in deleting node service option entity")
  void deleteNodeServiceOptionEntityExceptionTest() throws CommonServiceException {
    doThrow(new RuntimeException("Error while deleting"))
        .when(nodeServiceOptionRepository)
        .deleteByOrgIdAndNodeIdAndServiceOption(any(), any(), any());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionsDomain
                    .deleteNodeServiceOptionEntityByOrgIdAndNodeIdAndServiceOption(
                        TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION));

    assertEquals("Error while deleting the node service option", ex.getMessage());
    verify(nodeServiceOptionRepository, times(1))
        .deleteByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When node service option is fetched successfully")
  void findNodeCarrierDetailsTest() throws CommonServiceException {
    NodeServiceOptionEntity nodeServiceOptionEntity = testUtil.getNodeServiceOptionEntity();

    when(nodeServiceOptionRepository.findByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(Optional.of(nodeServiceOptionEntity));

    Optional<NodeServiceOptionEntity> nodeCarrier =
        nodeServiceOptionsDomain.findNodeServiceOptionEntity(
            TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION);

    assertEquals(nodeServiceOptionEntity, nodeCarrier.get());
    verify(nodeServiceOptionRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("When there is some error in finding node service option")
  void findNodeServiceOptionEntityTestException() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionsDomain.findNodeServiceOptionEntity(
                    TestUtil.NODE_ID, TestUtil.ORG_ID, TestUtil.SERVICE_OPTION));

    assertEquals("Error while finding node service option", ex.getMessage());
    verify(nodeServiceOptionRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Node Service Option List Found")
  void findByOrgIdAndNodeId_Success() throws CommonServiceException {
    List<NodeServiceOptionEntity> optionEntityList = new ArrayList<>();
    optionEntityList.add(testUtil.getNodeServiceOptionEntity()); // Adding a sample option entity
    when(nodeServiceOptionRepository.findByOrgIdAndNodeId(any(), any()))
        .thenReturn(optionEntityList);
    List<NodeServiceOptionEntity> result =
        nodeServiceOptionsDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());

    verify(nodeServiceOptionRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }

  @Test
  @DisplayName("Node Service Option List Not Found")
  void findByOrgIdAndNodeId_NotFound() throws CommonServiceException {
    when(nodeServiceOptionRepository.findByOrgIdAndNodeId(any(), any()))
        .thenReturn(new ArrayList<>());
    List<NodeServiceOptionEntity> result =
        nodeServiceOptionsDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID);
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(nodeServiceOptionRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }

  @Test
  @DisplayName("Error Handling - Repository Throwing Exception")
  void findByOrgIdAndNodeId_Exception() {
    when(nodeServiceOptionRepository.findByOrgIdAndNodeId(any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionsDomain.findByOrgIdAndNodeId(TestUtil.ORG_ID, TestUtil.NODE_ID));

    assertEquals("Error while finding node service option", exception.getMessage());
    verify(nodeServiceOptionRepository, times(1)).findByOrgIdAndNodeId(any(), any());
  }
}
