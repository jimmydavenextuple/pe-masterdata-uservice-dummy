/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.repository;

import static com.nextuple.common.constants.CommonConstants.ORG_ID;

import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

@Slf4j
public class TransferScheduleCustomRepositoryImpl implements TransferScheduleCustomRepository {
  @PersistenceContext private EntityManager entityManager;

  private static final String END_TIME = "endTime";
  private static final String START_TIME = "startTime";
  private static final String DROPOFF_NODE_ID = "dropoffNodeId";
  private static final String SOURCE_NODE_ID = "sourceNodeId";

  @Override
  public Page<TransferScheduleEntity> findFilteredTransferSchedules(
      String orgId, FetchTransferScheduleRequest request, Pageable pageable) {
    DateTime startDateTime =
        request.getStartDate() != null
            ? request.getStartDate().toDateTimeAtStartOfDay()
            : DateTime.now().withTimeAtStartOfDay();
    DateTime endDateTime =
        request.getEndDate() != null
            ? request.getEndDate().toDateTimeAtStartOfDay().plusDays(1).minusMillis(1)
            : DateTime.now().withTimeAtStartOfDay().plusDays(30).plusDays(1).minusMillis(1);

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<TransferScheduleEntity> cq = cb.createQuery(TransferScheduleEntity.class);
    Root<TransferScheduleEntity> root = cq.from(TransferScheduleEntity.class);

    List<Predicate> predicates =
        buildPredicates(
            cb, root, orgId, request, startDateTime, endDateTime, request.getRuleInfo());
    cq.where(predicates.toArray(new Predicate[0]));

    applySorting(cq, cb, root, pageable);

    TypedQuery<TransferScheduleEntity> query = entityManager.createQuery(cq);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());

    List<TransferScheduleEntity> transferSchedules = query.getResultList();

    long total =
        fetchTotalCount(cb, orgId, request, startDateTime, endDateTime, request.getRuleInfo());

    return new PageImpl<>(transferSchedules, pageable, total);
  }

  @Override
  public List<TransferScheduleEntity> findTransferSchedulesInRange(
      TransferScheduleDomainRequest request) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<TransferScheduleEntity> cq = cb.createQuery(TransferScheduleEntity.class);
    Root<TransferScheduleEntity> root = cq.from(TransferScheduleEntity.class);

    List<Predicate> predicates = new ArrayList<>();
    // required predicates
    predicates.add(cb.equal(root.get(ORG_ID), request.getOrgId()));
    predicates.add(cb.equal(root.get(DROPOFF_NODE_ID), request.getDropoffNodeId()));

    // optional predicates
    if (Objects.nonNull(request.getRule()) && Objects.nonNull(request.getRuleName())) {
      predicates.add(cb.equal(root.get("rule"), request.getRule()));
      predicates.add(cb.equal(root.get("ruleName"), request.getRuleName()));
    }

    if (request.getExclusive() != null
        && request.getExclusive()
        && Objects.nonNull(request.getStartTimeLowerBound())
        && Objects.nonNull(request.getEndTimeUpperBound())) {
      predicates.add(
          cb.or(
              cb.between(
                  root.get(START_TIME),
                  request.getStartTimeLowerBound(),
                  request.getStartTimeUpperBound()),
              cb.between(
                  root.get(END_TIME),
                  request.getEndTimeLowerBound(),
                  request.getEndTimeUpperBound())));
    } else {
      if (Objects.nonNull(request.getStartTimeLowerBound())) {
        predicates.add(
            cb.between(
                root.get(START_TIME),
                request.getStartTimeLowerBound(),
                request.getStartTimeUpperBound()));
      }

      if (Objects.nonNull(request.getEndTimeLowerBound())) {
        predicates.add(
            cb.between(
                root.get(END_TIME),
                request.getEndTimeLowerBound(),
                request.getEndTimeUpperBound()));
      }
    }
    cq.where(cb.and(predicates.toArray(new Predicate[0])));
    return entityManager.createQuery(cq).getResultList();
  }

  private List<Predicate> buildPredicates(
      CriteriaBuilder cb,
      Root<TransferScheduleEntity> root,
      String orgId,
      FetchTransferScheduleRequest request,
      DateTime startDateTime,
      DateTime endDateTime,
      List<Pair<String, String>> ruleInfo) {
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(root.get(ORG_ID), orgId));

    if (!CollectionUtils.isEmpty(request.getDropoffNodeIds())) {
      predicates.add(
          buildPredicateForNodeIds(cb, root, request.getDropoffNodeIds(), DROPOFF_NODE_ID));
    }

    if (!CollectionUtils.isEmpty(request.getSourceNodeIds())) {
      predicates.add(
          buildPredicateForNodeIds(cb, root, request.getSourceNodeIds(), SOURCE_NODE_ID));
    }

    if (startDateTime != null && endDateTime != null) {
      predicates.add(cb.between(root.get(END_TIME), startDateTime.toDate(), endDateTime.toDate()));
    }

    if (Objects.nonNull(ruleInfo)) {
      List<Predicate> rulePredicates = new ArrayList<>();

      Predicate ruleAndRuleNameNull =
          cb.and(cb.isNull(root.get("rule")), cb.isNull(root.get("ruleName")));
      rulePredicates.add(ruleAndRuleNameNull);

      for (Pair<String, String> entry : ruleInfo) {
        Predicate ruleNameAndRuleMatch =
            cb.and(
                cb.equal(root.get("ruleName"), entry.getFirst()),
                cb.equal(root.get("rule"), entry.getSecond()));
        rulePredicates.add(ruleNameAndRuleMatch);
      }

      predicates.add(cb.or(rulePredicates.toArray(new Predicate[0])));
    }

    return predicates;
  }

  private Predicate buildPredicateForNodeIds(
      CriteriaBuilder cb,
      Root<TransferScheduleEntity> root,
      List<String> nodeIds,
      String fieldName) {
    return cb.or(
        nodeIds.stream()
            .map(nodeId -> cb.equal(root.get(fieldName), nodeId))
            .toArray(Predicate[]::new));
  }

  private void applySorting(
      CriteriaQuery<TransferScheduleEntity> cq,
      CriteriaBuilder cb,
      Root<TransferScheduleEntity> root,
      Pageable pageable) {
    List<String> allowedSortFields = Arrays.asList(SOURCE_NODE_ID, DROPOFF_NODE_ID);
    // Default to sorting by sourceNodeId
    Order defaultOrder = cb.asc(root.get(SOURCE_NODE_ID));
    List<Order> orders = new ArrayList<>();
    pageable
        .getSort()
        .forEach(
            order -> {
              String sortProperty = order.getProperty();
              log.debug("Sorting by: {} in order: {}", sortProperty, order.getDirection());

              if (allowedSortFields.contains(sortProperty)) {
                Order sortOrder =
                    order.isAscending()
                        ? cb.asc(root.get(sortProperty))
                        : cb.desc(root.get(sortProperty));
                orders.add(sortOrder);
              }
            });

    // Apply sorting orders or default if no valid sorting fields
    cq.orderBy(orders.isEmpty() ? Collections.singletonList(defaultOrder) : orders);
  }

  private long fetchTotalCount(
      CriteriaBuilder cb,
      String orgId,
      FetchTransferScheduleRequest request,
      DateTime startDateTime,
      DateTime endDateTime,
      List<Pair<String, String>> ruleInfo) {
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<TransferScheduleEntity> countRoot = countQuery.from(TransferScheduleEntity.class);
    countQuery.select(cb.count(countRoot));

    List<Predicate> countPredicates =
        buildPredicates(cb, countRoot, orgId, request, startDateTime, endDateTime, ruleInfo);

    countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));

    return entityManager.createQuery(countQuery).getSingleResult();
  }
}
