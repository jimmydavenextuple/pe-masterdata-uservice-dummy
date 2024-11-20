/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.repository.NodeServiceOptionBufferRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NodeServiceOptionBufferDomainTest {
  @InjectMocks private NodeServiceOptionBufferDomain nodeServiceOptionBufferDomain;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeServiceOptionBufferRepository nodeServiceOptionBufferRepository;

  @Test
  @DisplayName("Successful Execution - Save Node ServiceOption BufferEntity")
  void saveNodeServiceOptionBufferEntitySucessTest() throws CommonServiceException {
    NodeServiceOptionBufferEntity entity = testUtil.getNodeServiceOptionBufferEntity();
    when(nodeServiceOptionBufferRepository.save(any())).thenReturn(entity);
    assertEquals(entity, nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(entity));
    verify(nodeServiceOptionBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Exception - Save Node ServiceOption BufferEntity")
  void saveNodeServiceOptionBufferEntityExceptionTest() {
    NodeServiceOptionBufferEntity entity = testUtil.getNodeServiceOptionBufferEntity();
    when(nodeServiceOptionBufferRepository.save(any())).thenThrow(new RuntimeException());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(entity));
    assertEquals("Error while saving the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Successful Execution - Find Node ServiceOption BufferEntity By OrgId And Id")
  void findNodeServiceOptionBufferEntityByOrgIdAndIdSucessTest() throws CommonServiceException {
    NodeServiceOptionBufferEntity entity = testUtil.getNodeServiceOptionBufferEntity();
    when(nodeServiceOptionBufferRepository.findByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(entity));
    assertEquals(
        entity,
        nodeServiceOptionBufferDomain
            .findNodeServiceOptionBufferEntityByOrgIdAndId(TestUtil.ORG_ID, 1L)
            .get());
    verify(nodeServiceOptionBufferRepository, times(1)).findByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName("Exception - Find Node ServiceOption BufferEntity By OrgId And Id")
  void findNodeServiceOptionBufferEntityByOrgIdAndIdExceptionTest() {
    when(nodeServiceOptionBufferRepository.findByOrgIdAndId(any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(
                    TestUtil.ORG_ID, 1L));
    assertEquals("Error while fetching the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1)).findByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName(
      "Successful Execution - Find By OrgId And NodeId And ServiceOption And BufferStartDate And BufferEndDate")
  void findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDateSucessTest()
      throws CommonServiceException {
    NodeServiceOptionBufferEntity entity = testUtil.getNodeServiceOptionBufferEntity();
    when(nodeServiceOptionBufferRepository
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(entity));
    assertEquals(
        entity,
        nodeServiceOptionBufferDomain
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, new Date(), new Date())
            .get());
    verify(nodeServiceOptionBufferRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Exception - Find By OrgId And NodeId And ServiceOption And BufferStartDate And BufferEndDate")
  void findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDateExceptionTest() {
    when(nodeServiceOptionBufferRepository
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferDomain
                    .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                        TestUtil.ORG_ID,
                        TestUtil.NODE_ID,
                        TestUtil.SERVICE_OPTION,
                        new Date(),
                        new Date()));
    assertEquals("Error while fetching the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Delete By OrdId And Id")
  void deleteByOrdIdAndIdSuccessTest() throws CommonServiceException {
    doNothing().when(nodeServiceOptionBufferRepository).deleteByOrgIdAndId(any(), any());
    nodeServiceOptionBufferDomain.deleteByOrdIdAndId(TestUtil.ORG_ID, 1L);
    verify(nodeServiceOptionBufferRepository, times(1)).deleteByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName("Exception - Delete By OrdId And Id")
  void deleteByOrdIdAndIdExceptionTest() {
    doThrow(new RuntimeException())
        .when(nodeServiceOptionBufferRepository)
        .deleteByOrgIdAndId(any(), any());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionBufferDomain.deleteByOrdIdAndId(TestUtil.ORG_ID, 1L));
    assertEquals("Error while deleting the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1)).deleteByOrgIdAndId(any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Find Applicable Buffers")
  void findApplicableBuffersSucessTest() throws CommonServiceException {
    List<NodeServiceOptionBufferEntity> entities = testUtil.getNodeServiceOptionBufferEntities(3);
    when(nodeServiceOptionBufferRepository.findApplicableBuffers(any(), any(), any(), any(), any()))
        .thenReturn(entities);
    assertEquals(
        3,
        nodeServiceOptionBufferDomain
            .findApplicableBuffers(
                TestUtil.ORG_ID,
                TestUtil.NODE_ID,
                TestUtil.SERVICE_OPTION,
                LocalDate.now(),
                LocalDate.now())
            .size());
    verify(nodeServiceOptionBufferRepository, times(1))
        .findApplicableBuffers(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Exception - Find Applicable Buffers")
  void findApplicableBuffersExceptionTest() {
    when(nodeServiceOptionBufferRepository.findApplicableBuffers(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferDomain.findApplicableBuffers(
                    TestUtil.ORG_ID,
                    TestUtil.NODE_ID,
                    TestUtil.SERVICE_OPTION,
                    LocalDate.now(),
                    LocalDate.now()));
    assertEquals("Error while fetching the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1))
        .findApplicableBuffers(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Successful Execution - Find By OrgId And NodeId And ServiceOption")
  void findByOrgIdAndNodeIdAndServiceOption_SuccessTest() throws CommonServiceException {

    List<NodeServiceOptionBufferEntity> expectedEntities =
        Collections.singletonList(new NodeServiceOptionBufferEntity());

    when(nodeServiceOptionBufferRepository.findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION))
        .thenReturn(expectedEntities);

    List<NodeServiceOptionBufferEntity> actualEntities =
        nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
    assertEquals(expectedEntities, actualEntities);
    verify(nodeServiceOptionBufferRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
  }

  @Test
  @DisplayName("Exception - Find By OrgId And NodeId And ServiceOption")
  void findByOrgIdAndNodeIdAndServiceOption_ExceptionTest() {
    when(nodeServiceOptionBufferRepository.findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION))
        .thenThrow(new RuntimeException());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(
                    TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION));

    assertEquals("Error while fetching the node service option buffer entity", ex.getMessage());
    verify(nodeServiceOptionBufferRepository, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
  }
}
