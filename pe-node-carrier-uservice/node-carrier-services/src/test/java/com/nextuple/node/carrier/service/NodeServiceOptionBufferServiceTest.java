/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.node.carrier.TestUtil;
import com.nextuple.node.carrier.domain.NodeServiceOptionBufferDomain;
import com.nextuple.node.carrier.domain.NodeServiceOptionsDomain;
import com.nextuple.node.carrier.domain.entity.NodeServiceOptionBufferEntity;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import com.nextuple.node.carrier.service.impl.NodeServiceOptionBufferServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionBufferServiceTest {
  @InjectMocks private NodeServiceOptionBufferServiceImpl nodeServiceOptionBufferService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeServiceOptionBufferDomain nodeServiceOptionBufferDomain;
  @Mock private NodeServiceOptionsDomain nodeServiceOptionsDomain;
  @Mock private DateValidationUtil dateValidationUtil;

  @Test
  void createNodeServiceOptionBufferHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionEntity()));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(1));
    when(nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(1).get(0));
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.createNodeServiceOptionBuffer(
            testUtil.getNodeServiceOptionBufferRequest());
    assertEquals(TestUtil.SERVICE_OPTION, response.getServiceOption());
    verify(dateValidationUtil, times(1)).validateBufferStartAndEndDate(any(), any());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  void createNodeServiceOptionBufferNodeNotFoundTest() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferService.createNodeServiceOptionBuffer(
                    testUtil.getNodeServiceOptionBufferRequest()));
    assertEquals("Node Service Option details not found for the given buffer", ex.getMessage());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  void createNodeServiceOptionBufferNegativeBufferHoursTest() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionEntity()));
    NodeServiceOptionBufferRequest request = testUtil.getNodeServiceOptionBufferRequest();
    request.setBufferHours(-2D);
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionBufferService.createNodeServiceOptionBuffer(request));
    assertEquals("bufferHours cannot be negative", ex.getMessage());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
  }

  @Test
  void createNodeServiceOptionBufferOverlappingTest() throws CommonServiceException {
    when(nodeServiceOptionsDomain.findNodeServiceOptionEntity(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionEntity()));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(10));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferService.createNodeServiceOptionBuffer(
                    testUtil.getNodeServiceOptionBufferRequest()));
    assertEquals("Node Service Option Buffer window already exists or overlaps", ex.getMessage());
    verify(nodeServiceOptionsDomain, times(1)).findNodeServiceOptionEntity(any(), any(), any());
    verify(dateValidationUtil, times(1)).validateBufferStartAndEndDate(any(), any());
  }

  @Test
  void fetchNodeServiceOptionBufferHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.fetchNodeServiceOptionBuffer(TestUtil.ORG_ID, 1L);
    assertEquals(TestUtil.NODE_ID, response.getNodeId());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void fetchNodeServiceOptionBufferNotFoundTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> nodeServiceOptionBufferService.fetchNodeServiceOptionBuffer(any(), any()));
    assertEquals("Node service option buffer not found for given orgId and Id", ex.getMessage());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void fetchApplicableNodeServiceOptionBuffersHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findApplicableBuffers(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(5));
    List<NodeServiceOptionBufferResponse> response =
        nodeServiceOptionBufferService.fetchApplicableNodeServiceOptionBuffers(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION, LocalDate.now(), 7);
    assertEquals(5, response.size());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findApplicableBuffers(any(), any(), any(), any(), any());
  }

  @Test
  void updateNodeServiceOptionBufferHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(5).subList(0, 2));
    when(nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(1).get(0));
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.updateNodeServiceOptionBuffer(
            TestUtil.ORG_ID, 1L, testUtil.getNodeServiceOptionBufferUpdateRequest());
    assertEquals(TestUtil.SERVICE_OPTION, response.getServiceOption());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void updateNodeServiceOptionBufferHappyPathWithoutStartAndEndDateTest()
      throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeServiceOptionBufferDomain.saveNodeServiceOptionBufferEntity(any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(1).get(0));
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.updateNodeServiceOptionBuffer(
            TestUtil.ORG_ID, 1L, testUtil.getNodeServiceOptionBufferUpdateRequestWithoutDates());
    assertEquals(TestUtil.SERVICE_OPTION, response.getServiceOption());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
    verify(nodeServiceOptionBufferDomain, times(0))
        .findByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }

  @Test
  void updateNodeServiceOptionBufferOverlapTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(any(), any(), any()))
        .thenReturn(testUtil.getNodeServiceOptionBufferEntities(5));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferService.updateNodeServiceOptionBuffer(
                    TestUtil.ORG_ID, 2L, testUtil.getNodeServiceOptionBufferUpdateRequest()));
    assertEquals("Node Service Option Buffer window already exists or overlaps", ex.getMessage());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void deleteNodeServiceOptionBufferHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    doNothing().when(nodeServiceOptionBufferDomain).deleteByOrdIdAndId(any(), any());
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.deleteNodeServiceOptionBuffer(
            testUtil.getNodeServiceOptionBufferDeleteRequest());
    assertEquals(TestUtil.SERVICE_OPTION, response.getServiceOption());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any());
  }

  @Test
  void deleteNodeServiceOptionBufferNotFoundTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain
            .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferService.deleteNodeServiceOptionBuffer(
                    testUtil.getNodeServiceOptionBufferDeleteRequest()));
    assertEquals("Node service option buffer not found for given details", ex.getMessage());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findByOrgIdAndNodeIdAndServiceOptionAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any());
  }

  @Test
  void deleteNodeServiceOptionBufferByOrgIdAndIdHappyPathTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeServiceOptionBufferEntities(1).get(0)));
    doNothing().when(nodeServiceOptionBufferDomain).deleteByOrdIdAndId(any(), any());
    NodeServiceOptionBufferResponse response =
        nodeServiceOptionBufferService.deleteNodeServiceOptionBufferByOrgIdAndId(
            TestUtil.ORG_ID, 1L);
    assertEquals(TestUtil.SERVICE_OPTION, response.getServiceOption());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void deleteNodeServiceOptionBufferByOrgIdAndIdNotFoundTest() throws CommonServiceException {
    when(nodeServiceOptionBufferDomain.findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                nodeServiceOptionBufferService.deleteNodeServiceOptionBufferByOrgIdAndId(
                    TestUtil.ORG_ID, 1L));
    assertEquals("Node service option buffer not found for given orgId and Id", ex.getMessage());
    verify(nodeServiceOptionBufferDomain, times(1))
        .findNodeServiceOptionBufferEntityByOrgIdAndId(any(), any());
  }

  @Test
  void getBuffersByOrgIdAndNodeIdAndServiceOptionTest() throws CommonServiceException {
    NodeServiceOptionBufferEntity bufferEntity = new NodeServiceOptionBufferEntity();
    List<NodeServiceOptionBufferEntity> bufferEntities = Arrays.asList(bufferEntity);
    when(nodeServiceOptionBufferDomain.findByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION))
        .thenReturn(bufferEntities);
    NodeServiceOptionBufferResponse expectedResponse = new NodeServiceOptionBufferResponse();
    List<NodeServiceOptionBufferResponse> actualResponse =
        nodeServiceOptionBufferService.getBuffersByOrgIdAndNodeIdAndServiceOption(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.SERVICE_OPTION);
    assertEquals(1, actualResponse.size());
    assertEquals(expectedResponse, actualResponse.get(0));
    verify(nodeServiceOptionBufferDomain, times(1))
        .findByOrgIdAndNodeIdAndServiceOption(any(), any(), any());
  }
}
