/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.item.persistence.entity.ItemSubstitutionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

class ItemSubstitutionCustomRepositoryImplTest {

  private ItemSubstitutionCustomRepositoryImpl repository;

  @Mock private EntityManager entityManager;

  @Mock private CriteriaBuilder criteriaBuilder;

  @Mock private CriteriaQuery<ItemSubstitutionEntity> criteriaQuery;

  @Mock private Root<ItemSubstitutionEntity> root;

  @Mock private TypedQuery<ItemSubstitutionEntity> typedQuery;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    repository = new ItemSubstitutionCustomRepositoryImpl(entityManager);
  }

  @Test
  @DisplayName("Test find by orgId and primary itemId and primaryUom custom query")
  void testFindByOrgIdAndPrimaryItemIdAndPrimaryUomList() {
    // Mock input data
    String orgId = "org1";
    List<Pair<String, String>> itemIdUomPairs =
        List.of(Pair.of("ITEM001", "EA"), Pair.of("ITEM002", "KG"));

    // Mock behavior
    when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
    when(criteriaBuilder.createQuery(ItemSubstitutionEntity.class)).thenReturn(criteriaQuery);
    when(criteriaQuery.from(ItemSubstitutionEntity.class)).thenReturn(root);

    Predicate predicate1 = mock(Predicate.class);
    Predicate predicate2 = mock(Predicate.class);
    Predicate orgPredicate = mock(Predicate.class);
    Predicate andPredicate1 = mock(Predicate.class);
    Predicate andPredicate2 = mock(Predicate.class);
    Predicate orPredicate = mock(Predicate.class);

    when(criteriaBuilder.equal(root.get("primaryItemId"), "ITEM001")).thenReturn(predicate1);
    when(criteriaBuilder.equal(root.get("primaryUom"), "EA")).thenReturn(predicate2);
    when(criteriaBuilder.equal(root.get("orgId"), orgId)).thenReturn(orgPredicate);
    when(criteriaBuilder.and(predicate1, predicate2, orgPredicate)).thenReturn(andPredicate1);

    when(criteriaBuilder.equal(root.get("primaryItemId"), "ITEM002")).thenReturn(predicate1);
    when(criteriaBuilder.equal(root.get("primaryUom"), "KG")).thenReturn(predicate2);
    when(criteriaBuilder.and(predicate1, predicate2, orgPredicate)).thenReturn(andPredicate2);

    when(criteriaBuilder.or(andPredicate1, andPredicate2)).thenReturn(orPredicate);
    when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
    when(criteriaQuery.where(orPredicate)).thenReturn(criteriaQuery);
    when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

    List<ItemSubstitutionEntity> mockResult = new ArrayList<>();
    when(typedQuery.getResultList()).thenReturn(mockResult);

    // Call the method
    List<ItemSubstitutionEntity> result =
        repository.findByOrgIdAndPrimaryItemIdAndPrimaryUomList(orgId, itemIdUomPairs);

    // Verify behavior
    assertEquals(mockResult, result);
    verify(entityManager, times(1)).getCriteriaBuilder();
    verify(criteriaBuilder, times(1)).createQuery(ItemSubstitutionEntity.class);
    verify(criteriaQuery, times(1)).from(ItemSubstitutionEntity.class);
    verify(criteriaBuilder, times(6)).equal(any(), anyString());
  }
}
