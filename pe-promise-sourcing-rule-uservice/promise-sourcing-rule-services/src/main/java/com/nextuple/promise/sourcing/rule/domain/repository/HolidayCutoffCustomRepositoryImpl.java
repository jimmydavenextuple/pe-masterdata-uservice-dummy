/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.repository;

import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

@Slf4j
public class HolidayCutoffCustomRepositoryImpl implements HolidayCutoffCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Page<HolidayCutoffEntity> findFilteredHolidayCutoffRules(
      String orgId,
      HolidayCutoffUIRequest holidayCutoffUIRequest,
      List<List<String>> possibleRuleCombinations,
      Pageable pageable) {

    // Create CriteriaBuilder
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Create CriteriaQuery
    CriteriaQuery<HolidayCutoffEntity> cq = cb.createQuery(HolidayCutoffEntity.class);
    Root<HolidayCutoffEntity> root = cq.from(HolidayCutoffEntity.class);

    // List to hold predicates
    List<Predicate> predicates = new ArrayList<>();

    // Add predicates for orgId and sourcingAttributesDefinitionId
    predicates.add(cb.equal(root.get("orgId"), orgId)); // Use fresh paths
    predicates.add(
        cb.equal(
            root.get("sourcingAttributesDefinitionId"),
            holidayCutoffUIRequest.getSourcingAttributesDefinitionId()));

    // Add predicates for holidayCutoffNames
    if (!ObjectUtils.isEmpty(holidayCutoffUIRequest.getHolidayCutoffNames())) {
      predicates.add(
          buildPredicateForHolidayCutoffNames(
              cb, root, holidayCutoffUIRequest.getHolidayCutoffNames()));
    }

    // Add predicates for possibleRuleCombinations
    if (!ObjectUtils.isEmpty(possibleRuleCombinations)) {
      predicates.add(buildPredicateForHolidayCutoffRules(cb, root, possibleRuleCombinations));
    }

    // Combine all predicates
    cq.where(cb.and(predicates.toArray(new Predicate[0])));

    // Apply sorting
    if (pageable.getSort().isSorted()) {
      List<Order> orders = new ArrayList<>();
      for (Sort.Order order : pageable.getSort()) {
        Order jpaOrder =
            order.isAscending()
                ? cb.asc(root.get(order.getProperty()))
                : cb.desc(root.get(order.getProperty()));
        orders.add(jpaOrder);
      }
      cq.orderBy(orders);
    }

    // Create query and apply pagination
    TypedQuery<HolidayCutoffEntity> query = entityManager.createQuery(cq);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());

    // Fetch results
    List<HolidayCutoffEntity> holidayCutoffRules = query.getResultList();

    // Fetch total count
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<HolidayCutoffEntity> countRoot = countQuery.from(HolidayCutoffEntity.class);
    countQuery.select(cb.count(countRoot));

    // Recreate the predicates for the count query to avoid reusing paths
    List<Predicate> countPredicates = new ArrayList<>();
    countPredicates.add(cb.equal(countRoot.get("orgId"), orgId));
    countPredicates.add(
        cb.equal(
            countRoot.get("sourcingAttributesDefinitionId"),
            holidayCutoffUIRequest.getSourcingAttributesDefinitionId()));

    if (!ObjectUtils.isEmpty(holidayCutoffUIRequest.getHolidayCutoffNames())) {
      countPredicates.add(
          buildPredicateForHolidayCutoffNames(
              cb, countRoot, holidayCutoffUIRequest.getHolidayCutoffNames()));
    }

    if (!ObjectUtils.isEmpty(possibleRuleCombinations)) {
      countPredicates.add(
          buildPredicateForHolidayCutoffRules(cb, countRoot, possibleRuleCombinations));
    }

    countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
    long total = entityManager.createQuery(countQuery).getSingleResult();

    return new PageImpl<>(holidayCutoffRules, pageable, total);
  }

  private Predicate buildPredicateForHolidayCutoffNames(
      CriteriaBuilder cb, Root<HolidayCutoffEntity> root, List<String> holidayCutoffNames) {

    List<Predicate> predicates = new ArrayList<>();
    for (String name : holidayCutoffNames) {
      predicates.add(
          cb.like(cb.lower(root.get("holidayCutoffName")), "%" + name.toLowerCase() + "%"));
    }
    return cb.or(predicates.toArray(new Predicate[0]));
  }

  private Predicate buildPredicateForHolidayCutoffRules(
      CriteriaBuilder cb,
      Root<HolidayCutoffEntity> root,
      List<List<String>> possibleRuleCombinations) {

    List<Predicate> predicates = new ArrayList<>();
    for (List<String> ruleCombination : possibleRuleCombinations) {
      StringBuilder rulePattern = new StringBuilder();
      for (int i = 0; i < ruleCombination.size(); i++) {
        if (ObjectUtils.isEmpty(ruleCombination.get(i))) {
          rulePattern.append("%");
        } else {
          rulePattern.append(ruleCombination.get(i).toLowerCase());
        }
        if (i < ruleCombination.size() - 1) {
          rulePattern.append(":");
        }
      }
      predicates.add(cb.like(cb.lower(root.get("holidayCutoffRule")), rulePattern.toString()));
    }
    return cb.or(predicates.toArray(new Predicate[0]));
  }
}
