/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.repository;

import com.nextuple.item.persistence.entity.ItemSubstitutionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSubstitutionCustomRepositoryImpl implements ItemSubstitutionCustomRepository {

  private final EntityManager entityManager;

  @Override
  public List<ItemSubstitutionEntity> findByOrgIdAndPrimaryItemIdAndPrimaryUomList(
      String orgId, List<Pair<String, String>> itemIdUomPairs) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ItemSubstitutionEntity> query = cb.createQuery(ItemSubstitutionEntity.class);
    Root<ItemSubstitutionEntity> root = query.from(ItemSubstitutionEntity.class);

    List<Predicate> orPredicates = new ArrayList<>();

    for (Pair<String, String> pair : itemIdUomPairs) {
      Predicate itemNameMatch = cb.equal(root.get("primaryItemId"), pair.getFirst());
      Predicate itemUomMatch = cb.equal(root.get("primaryUom"), pair.getSecond());
      Predicate orgIdMatch = cb.equal(root.get("orgId"), orgId);
      Predicate andPredicate = cb.and(itemNameMatch, itemUomMatch, orgIdMatch);
      orPredicates.add(andPredicate);
    }

    query.select(root).where(cb.or(orPredicates.toArray(new Predicate[0])));

    return entityManager.createQuery(query).getResultList();
  }
}
