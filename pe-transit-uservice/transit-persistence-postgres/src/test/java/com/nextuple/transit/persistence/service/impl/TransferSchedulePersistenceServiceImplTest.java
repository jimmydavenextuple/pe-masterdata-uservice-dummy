/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.mapper.TransferScheduleEntityMapper;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

class TransferSchedulePersistenceServiceImplTest {
  @InjectMocks private TransferSchedulePersistenceServiceImpl transferSchedulePersistenceService;
  @InjectMocks private TestUtil testUtil;

  @Mock private TransferScheduleRepository transferScheduleRepository;
  @Mock private TransferScheduleEntityMapper transferScheduleEntityMapper;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        transferSchedulePersistenceService, "repository", transferScheduleRepository);
    ReflectionTestUtils.setField(
        transferSchedulePersistenceService, "mapper", transferScheduleEntityMapper);
  }

  @Test
  @DisplayName("Trasfer Schedules saved successfully")
  void createTransferScheduleTest() throws PromiseEngineException {
    TransferScheduleDomainDto domainDto = testUtil.getTransferScheduleDomainDto();
    when(transferScheduleRepository.save(any())).thenReturn(testUtil.getTransferScheduleEntity());
    when(transferScheduleEntityMapper.toDomain(any(TransferScheduleEntity.class)))
        .thenReturn(testUtil.getTransferScheduleDomainDto());
    TransferScheduleDomainDto dto =
        transferSchedulePersistenceService.saveTransferSchedule(domainDto);
    Assertions.assertEquals(TestUtil.SOURCE_NODE, dto.getSourceNodeId());
    Assertions.assertEquals(TestUtil.DESTINATION_NODE, dto.getDropoffNodeId());
    verify(transferScheduleRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Exception while saving transfer schedules")
  void createTransferScheduleExceptionTest() {
    TransferScheduleDomainDto domainDto = testUtil.getTransferScheduleDomainDto();
    when(transferScheduleRepository.save(any()))
        .thenThrow(new RuntimeException("Unique constraint violated."));
    when(transferScheduleEntityMapper.toDomain(any(TransferScheduleEntity.class)))
        .thenReturn(testUtil.getTransferScheduleDomainDto());
    Assertions.assertThrows(
        PromiseEngineException.class,
        () -> transferSchedulePersistenceService.saveTransferSchedule(domainDto));
    verify(transferScheduleRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("TrasferSchedules fetched successfully")
  void fetchTransferScheduleTest() {
    when(transferScheduleRepository.findByDropoffNodeIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getTransferScheduleEntityList());
    when(transferScheduleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getTransferScheduleDomainDto()));

    List<TransferScheduleDomainDto> dtos =
        transferSchedulePersistenceService.fetchUpcomingTransferSchedules(
            TestUtil.ORG_ID, TestUtil.DESTINATION_NODE);
    Assertions.assertEquals(TestUtil.SOURCE_NODE, dtos.get(0).getSourceNodeId());
    Assertions.assertEquals(TestUtil.DESTINATION_NODE, dtos.get(0).getDropoffNodeId());
    verify(transferScheduleRepository, times(1)).findByDropoffNodeIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("TrasferSchedules deleted successfully")
  void deleteTransferScheduleTest() throws PromiseEngineException, CommonServiceException {
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getTransferScheduleEntity()));
    when(transferScheduleEntityMapper.toDomain(any(TransferScheduleEntity.class)))
        .thenReturn(testUtil.getTransferScheduleDomainDto());

    TransferScheduleDomainDto dto =
        transferSchedulePersistenceService.deleteTransferSchedule(
            TestUtil.ORG_ID, TestUtil.SOURCE_NODE, TestUtil.DESTINATION_NODE, new Date());
    Assertions.assertEquals(TestUtil.SOURCE_NODE, dto.getSourceNodeId());
    Assertions.assertEquals(TestUtil.DESTINATION_NODE, dto.getDropoffNodeId());
    verify(transferScheduleRepository, times(1))
        .findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(any(), any(), any(), any());
    verify(transferScheduleRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("TrasferSchedules delete - record not found")
  void deleteTransferScheduleExceptionTest() {
    when(transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transferSchedulePersistenceService.deleteTransferSchedule(
                TestUtil.ORG_ID, TestUtil.SOURCE_NODE, TestUtil.DESTINATION_NODE, new Date()));
    verify(transferScheduleRepository, times(1))
        .findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(any(), any(), any(), any());
    verify(transferScheduleRepository, times(0)).delete(any());
  }

  @Test
  @DisplayName("Transfer Schedules fetched successfully with pagination")
  void fetchTransferSchedulesListTest() throws PromiseEngineException {
    FetchTransferScheduleRequest request =
        testUtil.getFetchTransferScheduleRequest(
            Arrays.asList("source1", "source2"), Collections.singletonList("dropoff1"));
    Pageable pageable = PageRequest.of(0, 10);
    List<TransferScheduleEntity> entities = testUtil.getTransferScheduleEntityList();
    Page<TransferScheduleEntity> entityPage = new PageImpl<>(entities, pageable, entities.size());

    when(transferScheduleRepository.findFilteredTransferSchedules(any(), any(), any()))
        .thenReturn(entityPage);
    when(transferScheduleEntityMapper.convertToTransferScheduleResponseFromEntity(any()))
        .thenReturn(testUtil.getTransferScheduleResponse());

    Page<TransferScheduleResponse> responsePage =
        transferSchedulePersistenceService.fetchTransferSchedulesList(
            TestUtil.ORG_ID, request, pageable);

    Assertions.assertNotNull(responsePage);
    Assertions.assertEquals(entities.size(), responsePage.getTotalElements());
    Assertions.assertEquals(entities.size(), responsePage.getContent().size());
    verify(transferScheduleRepository, times(1)).findFilteredTransferSchedules(any(), any(), any());
    verify(transferScheduleEntityMapper, times(entities.size()))
        .convertToTransferScheduleResponseFromEntity(any());
  }

  @Test
  @DisplayName("Exception while fetching Transfer Schedules")
  void fetchTransferSchedulesListExceptionTest() {
    FetchTransferScheduleRequest request =
        testUtil.getFetchTransferScheduleRequest(
            Arrays.asList("source1", "source2"), Collections.singletonList("dropoff1"));
    Pageable pageable = PageRequest.of(0, 10);

    when(transferScheduleRepository.findFilteredTransferSchedules(any(), any(), any()))
        .thenThrow(new RuntimeException("Database error occurred."));

    Assertions.assertThrows(
        PromiseEngineException.class,
        () ->
            transferSchedulePersistenceService.fetchTransferSchedulesList(
                TestUtil.ORG_ID, request, pageable));
    verify(transferScheduleRepository, times(1)).findFilteredTransferSchedules(any(), any(), any());
  }

  @Test
  @DisplayName("Trasfer Schedules fetched in range successfully")
  void fetchTransferSchedulesInRangeTest() {
    when(transferScheduleRepository.findTransferSchedulesInRange(any()))
        .thenReturn(testUtil.getTransferScheduleEntityList());
    when(transferScheduleEntityMapper.toDomain(anyList()))
        .thenReturn(List.of(testUtil.getTransferScheduleDomainDto()));

    TransferScheduleDomainRequest request = new TransferScheduleDomainRequest();
    List<TransferScheduleDomainDto> dtos =
        transferSchedulePersistenceService.fetchTransferSchedulesInRange(request);
    Assertions.assertEquals(TestUtil.SOURCE_NODE, dtos.get(0).getSourceNodeId());
    Assertions.assertEquals(TestUtil.DESTINATION_NODE, dtos.get(0).getDropoffNodeId());
    verify(transferScheduleRepository, times(1)).findTransferSchedulesInRange(any());
  }
}
