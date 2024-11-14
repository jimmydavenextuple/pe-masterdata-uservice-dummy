/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.data.domain.Sort.Order;

@ExtendWith(MockitoExtension.class)
class HolidayCutoffCustomRepositoryImplTest {

  @Mock private EntityManager entityManager;

  @Mock private CriteriaBuilder criteriaBuilder;

  @Mock private CriteriaQuery<HolidayCutoffEntity> criteriaQuery;

  @Mock private Root<HolidayCutoffEntity> root;

  @Mock private TypedQuery<HolidayCutoffEntity> typedQuery;

  @Mock private CriteriaQuery<Long> countCriteriaQuery;

  @Mock private TypedQuery<Long> countTypedQuery;

  @InjectMocks private HolidayCutoffCustomRepositoryImpl holidayCutoffCustomRepository;

  @BeforeEach
  void setUp() {
    when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
    when(criteriaBuilder.createQuery(HolidayCutoffEntity.class)).thenReturn(criteriaQuery);
    when(criteriaQuery.from(HolidayCutoffEntity.class)).thenReturn(root);
    when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

    when(criteriaBuilder.createQuery(Long.class)).thenReturn(countCriteriaQuery);
    when(countCriteriaQuery.from(HolidayCutoffEntity.class)).thenReturn(root);
    when(entityManager.createQuery(countCriteriaQuery)).thenReturn(countTypedQuery);
  }

  @Test
  @DisplayName("Happy Path - Find filtered set of holiday cutoff rules")
  void findFilteredHolidayCutoffRulesWithValidInput() {
    HolidayCutoffUIRequest holidayCutoffUIRequest = new HolidayCutoffUIRequest();
    holidayCutoffUIRequest.setSourcingAttributesDefinitionId(1L);
    holidayCutoffUIRequest.setHolidayCutoffNames(Arrays.asList("cutoff1", "cutoff2"));

    List<List<String>> possibleRuleCombinations = new ArrayList<>();
    possibleRuleCombinations.add(Arrays.asList("rule1", "rule2"));
    possibleRuleCombinations.add(Arrays.asList("rule3", ""));

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.by("ruleName")).ascending());
    List<HolidayCutoffEntity> results =
        Arrays.asList(new HolidayCutoffEntity(), new HolidayCutoffEntity());

    when(typedQuery.getResultList()).thenReturn(results);
    when(countTypedQuery.getSingleResult()).thenReturn(2L);

    Page<HolidayCutoffEntity> resultPage =
        holidayCutoffCustomRepository.findFilteredHolidayCutoffRules(
            TestUtil.ORG_ID, holidayCutoffUIRequest, possibleRuleCombinations, pageable);

    Assertions.assertNotNull(resultPage);
    assertEquals(2, resultPage.getTotalElements());
    assertEquals(2, resultPage.getContent().size());

    verify(entityManager, times(1)).createQuery(criteriaQuery);
    verify(entityManager, times(1)).createQuery(countCriteriaQuery);
  }

  @Test
  @DisplayName("No matching rules found - Find filtered set of holiday cutoff rules")
  void findFilteredHolidayCutoffRulesTestWithEmptyResults() {
    HolidayCutoffUIRequest holidayCutoffUIRequest = new HolidayCutoffUIRequest();
    holidayCutoffUIRequest.setSourcingAttributesDefinitionId(1L);

    List<List<String>> possibleRuleCombinations = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 10);

    when(typedQuery.getResultList()).thenReturn(new ArrayList<>());
    when(countTypedQuery.getSingleResult()).thenReturn(0L);

    Page<HolidayCutoffEntity> resultPage =
        holidayCutoffCustomRepository.findFilteredHolidayCutoffRules(
            TestUtil.ORG_ID, holidayCutoffUIRequest, possibleRuleCombinations, pageable);

    Assertions.assertNotNull(resultPage);
    assertEquals(0, resultPage.getTotalElements());
    assertTrue(resultPage.getContent().isEmpty());

    verify(entityManager, times(1)).createQuery(criteriaQuery);
    verify(entityManager, times(1)).createQuery(countCriteriaQuery);
  }

  @Test
  @DisplayName("Exception - Find filtered set of holiday cutoff rules")
  void findFilteredHolidayCutoffRulesExceptionTest() {
    HolidayCutoffUIRequest holidayCutoffUIRequest = new HolidayCutoffUIRequest();
    List<List<String>> possibleRuleCombinations = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 10);

    assertThrows(
        NullPointerException.class,
        () -> {
          holidayCutoffCustomRepository.findFilteredHolidayCutoffRules(
              null, holidayCutoffUIRequest, possibleRuleCombinations, pageable);
        });
  }
}
