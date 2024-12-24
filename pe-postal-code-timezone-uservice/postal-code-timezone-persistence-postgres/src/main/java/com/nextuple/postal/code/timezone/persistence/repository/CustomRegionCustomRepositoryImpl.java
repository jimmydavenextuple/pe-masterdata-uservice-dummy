/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postal.code.timezone.persistence.repository;

import com.nextuple.postal.code.timezone.persistence.entity.CustomRegionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class CustomRegionCustomRepositoryImpl implements CustomRegionCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<CustomRegionEntity> fetchCustomRegionsByCustomRegionNamesAndOrgId(
      List<String> customRegionNames, String orgId) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<CustomRegionEntity> query = cb.createQuery(CustomRegionEntity.class);
    Root<CustomRegionEntity> root = query.from(CustomRegionEntity.class);
    List<Predicate> predicates = new ArrayList<>();
    List<Predicate> namePredicates = new ArrayList<>();
    for (String customRegionName : customRegionNames) {
      namePredicates.add(
          cb.like(
              cb.lower(root.get("customRegionName")), "%" + customRegionName.toLowerCase() + "%"));
    }
    predicates.add(cb.or(namePredicates.toArray(new Predicate[0])));
    predicates.add(cb.equal(root.get("orgId"), orgId));
    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  public List<CustomRegionEntity> fetchCustomRegionByIdAndNameAndCountryAndOrgId(
      List<String> customRegionIds, List<String> customRegionNames, String orgId) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<CustomRegionEntity> query = cb.createQuery(CustomRegionEntity.class);
    Root<CustomRegionEntity> root = query.from(CustomRegionEntity.class);
    List<Predicate> idAndNameCombinedPredicates = new ArrayList<>();
    for (String customRegionId : customRegionIds) {
      for (String customRegionName : customRegionNames) {
        idAndNameCombinedPredicates.add(
            cb.and(
                cb.like(
                    cb.lower(root.get("customRegionName")),
                    "%" + customRegionName.toLowerCase() + "%"),
                cb.equal(root.get("id"), customRegionId)));
      }
    }
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.or(idAndNameCombinedPredicates.toArray(new Predicate[0])));
    predicates.add(cb.equal(root.get("orgId"), orgId));
    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
    return entityManager.createQuery(query).getResultList();
  }
}
