/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.repository;

import static com.nextuple.transit.persistence.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.persistence.TestUtil;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TransferScheduleCustomRepositoryImplTest {
  @Mock private EntityManager entityManager;
  @Mock private CriteriaBuilder criteriaBuilder;
  @Mock private CriteriaQuery<TransferScheduleEntity> criteriaQuery;
  @Mock private Root<TransferScheduleEntity> root;
  @Mock private TypedQuery<TransferScheduleEntity> typedQuery;
  @Mock private CriteriaQuery<Long> countCriteriaQuery;
  @Mock private TypedQuery<Long> countTypedQuery;
  @InjectMocks private TransferScheduleCustomRepositoryImpl repository;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
    when(criteriaBuilder.createQuery(TransferScheduleEntity.class)).thenReturn(criteriaQuery);
    when(criteriaQuery.from(TransferScheduleEntity.class)).thenReturn(root);
    when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

    lenient().when(criteriaBuilder.createQuery(Long.class)).thenReturn(countCriteriaQuery);
    lenient().when(countCriteriaQuery.from(TransferScheduleEntity.class)).thenReturn(root);
    lenient().when(entityManager.createQuery(countCriteriaQuery)).thenReturn(countTypedQuery);
  }

  @Test
  @DisplayName("Happy Path - Find filtered set of transfer schedules")
  void findFilteredTransferSchedulesWithValidInput() {
    FetchTransferScheduleRequest request =
        testUtil.getFetchTransferScheduleRequest(
            Arrays.asList("source1", "source2"), Collections.singletonList("dropoff1"));
    Pageable pageable = PageRequest.of(0, 10, Sort.by("sourceNodeId").ascending());
    List<TransferScheduleEntity> results =
        Arrays.asList(new TransferScheduleEntity(), new TransferScheduleEntity());

    when(typedQuery.getResultList()).thenReturn(results);
    when(countTypedQuery.getSingleResult()).thenReturn(2L);

    Page<TransferScheduleEntity> resultPage =
        repository.findFilteredTransferSchedules(ORG_ID, request, pageable);

    assertEquals(2, resultPage.getTotalElements());
    assertEquals(2, resultPage.getContent().size());

    verify(entityManager, times(1)).createQuery(criteriaQuery);
    verify(entityManager, times(1)).createQuery(countCriteriaQuery);
  }

  @Test
  @DisplayName("No matching schedules found - Find filtered set of transfer schedules")
  void findFilteredTransferSchedulesTestWithEmptyResults() {
    FetchTransferScheduleRequest request =
        testUtil.getFetchTransferScheduleRequest(
            Collections.singletonList("source1"), Collections.singletonList("dropoff1"));
    Pageable pageable = PageRequest.of(0, 10);

    when(typedQuery.getResultList()).thenReturn(Collections.emptyList());
    when(countTypedQuery.getSingleResult()).thenReturn(0L);

    Page<TransferScheduleEntity> resultPage =
        repository.findFilteredTransferSchedules(ORG_ID, request, pageable);

    assertEquals(0, resultPage.getTotalElements());
    assertEquals(0, resultPage.getContent().size());

    verify(entityManager, times(1)).createQuery(criteriaQuery);
    verify(entityManager, times(1)).createQuery(countCriteriaQuery);
  }

  @Test
  @DisplayName("Exception - Find filtered set of transfer schedules")
  void findFilteredTransferSchedulesExceptionTest() {
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    Pageable pageable = PageRequest.of(0, 10);

    assertThrows(
        NullPointerException.class,
        () -> repository.findFilteredTransferSchedules(null, request, pageable));
  }

  @Test
  @DisplayName("Happy Path - Find transfer schedules in range")
  void findTransferSchedulesInRange() {
    TransferScheduleDomainRequest request = testUtil.getTransferScheduleDomainRequest();
    List<TransferScheduleEntity> results =
        Arrays.asList(new TransferScheduleEntity(), new TransferScheduleEntity());

    when(typedQuery.getResultList()).thenReturn(results);
    List<TransferScheduleEntity> actualResult = repository.findTransferSchedulesInRange(request);

    assertEquals(2, actualResult.size());
    verify(entityManager, times(1)).createQuery(criteriaQuery);
  }

  @Test
  @DisplayName("Happy Path - Find transfer schedules in range with empty fields")
  void findTransferSchedulesInRangeNullTest() {
    TransferScheduleDomainRequest request = testUtil.getTransferScheduleDomainRequest();
    request.setStartTimeLowerBound(null);
    request.setEndTimeLowerBound(null);
    request.setRule(null);
    request.setRuleName(null);
    List<TransferScheduleEntity> results =
        Arrays.asList(new TransferScheduleEntity(), new TransferScheduleEntity());

    when(typedQuery.getResultList()).thenReturn(results);
    List<TransferScheduleEntity> actualResult = repository.findTransferSchedulesInRange(request);

    assertEquals(2, actualResult.size());
    verify(entityManager, times(1)).createQuery(criteriaQuery);
  }
}
