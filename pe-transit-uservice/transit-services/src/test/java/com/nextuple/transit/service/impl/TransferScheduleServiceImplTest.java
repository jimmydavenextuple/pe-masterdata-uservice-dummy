/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.service.TransferSchedulePersistenceService;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class TransferScheduleServiceImplTest {
  @InjectMocks TransferScheduleServiceImpl transferScheduleService;
  @InjectMocks TestUtil testUtil;

  @Mock NodeFeign nodeFeign;
  @Mock TransferSchedulePersistenceService transferSchedulePersistenceService;
  @Mock PageParams pageParams;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("create transfer schedules")
  void createTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleResponse response =
        transferScheduleService.createTransferSchedule(
            testUtil.getTransferScheduleCreationRequest());
    Assertions.assertEquals("Node1", response.getSourceNodeId());
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(1)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - start time after end time")
  void createTransferScheduleInvalidTimeTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build());
    when(transferSchedulePersistenceService.saveTransferSchedule(any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleCreationRequest transferScheduleCreationRequest =
        testUtil.getTransferScheduleCreationRequest();
    DateTime dateTime = new DateTime();
    transferScheduleCreationRequest.setStartTime(dateTime.plusDays(1));
    transferScheduleCreationRequest.setEndTime(dateTime);

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> transferScheduleService.createTransferSchedule(transferScheduleCreationRequest));
    verify(nodeFeign, times(0)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - invalid node - feign success false")
  void createTransferScheduleInvalidNodeTest() throws PromiseEngineException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build())
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(false).build());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transferScheduleService.createTransferSchedule(
                testUtil.getTransferScheduleCreationRequest()));
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("create transfer schedules - invalid node - feign response empty")
  void createTransferScheduleEmptyFeignResponseTest() throws PromiseEngineException {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeDetail("Node1")).success(true).build())
        .thenReturn(BaseResponse.builder().success(true).build());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transferScheduleService.createTransferSchedule(
                testUtil.getTransferScheduleCreationRequest()));
    verify(nodeFeign, times(2)).getNodeDetails(any(), any());
    verify(transferSchedulePersistenceService, times(0)).saveTransferSchedule(any());
  }

  @Test
  @DisplayName("Fetch transfer schedules")
  void fetchTransferScheduleTest() {
    when(transferSchedulePersistenceService.fetchUpcomingTransferSchedules(any(), any()))
        .thenReturn(List.of(testUtil.getTransferScheduleEntity()));
    List<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferSchedules(TestUtil.ORG_ID, TestUtil.DROPOFF_NODE);
    Assertions.assertEquals("Node1", response.get(0).getSourceNodeId());
    verify(transferSchedulePersistenceService, times(1))
        .fetchUpcomingTransferSchedules(any(), any());
  }

  @Test
  @DisplayName("Delete transfer schedules - happy path")
  void deleteTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    when(transferSchedulePersistenceService.deleteTransferSchedule(any(), any(), any(), any()))
        .thenReturn(testUtil.getTransferScheduleEntity());
    TransferScheduleResponse response =
        transferScheduleService.deleteTransferSchedule(testUtil.getTransferScheduleRequest());
    Assertions.assertEquals("Node1", response.getSourceNodeId());
    verify(transferSchedulePersistenceService, times(1))
        .deleteTransferSchedule(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - paginated")
  void fetchTransferScheduleListPaginatedTest()
      throws PromiseEngineException, CommonServiceException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));
    when(pageParams.getPageSize()).thenReturn(java.util.Optional.of(10));

    Page<TransferScheduleResponse> mockPage =
        new PageImpl<>(List.of(testUtil.getTransferScheduleResponse()));

    when(transferSchedulePersistenceService.fetchTransferSchedulesList(any(), any(), any()))
        .thenReturn(mockPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(
            TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);

    Assertions.assertEquals(1, response.getTotalElements());
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - non-paginated")
  void fetchTransferScheduleListNonPaginatedTest()
      throws PromiseEngineException, CommonServiceException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Page<TransferScheduleResponse> mockPage =
        new PageImpl<>(List.of(testUtil.getTransferScheduleResponse()));

    when(transferSchedulePersistenceService.fetchTransferSchedulesList(any(), any(), any()))
        .thenReturn(mockPage);

    Page<TransferScheduleResponse> response =
        transferScheduleService.fetchTransferScheduleList(
            TestUtil.ORG_ID, false, pageParams, fetchTransferScheduleRequest);

    Assertions.assertEquals(1, response.getTotalElements());
    verify(transferSchedulePersistenceService, times(1))
        .fetchTransferSchedulesList(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch transfer schedules list - invalid sort field")
  void fetchTransferScheduleListInvalidSortByTest() {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("invalidField"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }

  @Test
  @DisplayName("Fetch transfer schedules list - invalid sort order")
  void fetchTransferScheduleListInvalidSortOrderTest() {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("INVALID"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }

  @Test
  @DisplayName("Fetch transfer schedules list - exception from persistence service")
  void fetchTransferScheduleListPersistenceServiceExceptionTest() throws PromiseEngineException {
    var fetchTransferScheduleRequest = testUtil.getFetchTransferScheduleRequest();
    when(pageParams.getSortBy()).thenReturn(java.util.Optional.of("sourceNodeId"));
    when(pageParams.getSortOrder()).thenReturn(java.util.Optional.of("ASC"));
    when(pageParams.getPageNo()).thenReturn(java.util.Optional.of(1));
    when(pageParams.getPageSize()).thenReturn(java.util.Optional.of(10));

    doThrow(
            new PromiseEngineException(
                ApplicationLayer.DAO_LAYER,
                ExceptionCodeMapping.DAO_SAVE_FAILED,
                "Persistence service exception"))
        .when(transferSchedulePersistenceService)
        .fetchTransferSchedulesList(any(), any(), any());

    Assertions.assertThrows(
        PromiseEngineException.class,
        () -> {
          transferScheduleService.fetchTransferScheduleList(
              TestUtil.ORG_ID, true, pageParams, fetchTransferScheduleRequest);
        });
  }
}
