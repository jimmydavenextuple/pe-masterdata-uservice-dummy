/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.repository.impl;

import com.nextuple.transfer.schedule.cache.dto.TransferScheduleCacheRequest;
import com.nextuple.transfer.schedule.cache.repository.TransferScheduleRedisRepository;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferScheduleRedisRepositoryImpl implements TransferScheduleRedisRepository {

  @Autowired RedissonClient redissonClient;

  @Override
  public void save(TransferScheduleCreationRequest request) {
    String key = getCacheKey(request.getOrgId(), request.getDropoffNodeId());
    RMap<String, List<Map<String, Object>>> redisMap = redissonClient.getMap(key);

    List<Map<String, Object>> sourceNodes = redisMap.getOrDefault("snodes", new ArrayList<>());

    // Check if the source node already exists
    Optional<Map<String, Object>> existingSourceNode =
        sourceNodes.stream()
            .filter(node -> node.get("id").equals(request.getSourceNodeId()))
            .findFirst();

    Map<String, Object> newSourceNode = toHashValue(request);
    if (existingSourceNode.isPresent()) {
      // Update existing source node with new route
      List<Map<String, Object>> transfers =
          (List<Map<String, Object>>) existingSourceNode.get().get("transfers");
      transfers.addAll((List<Map<String, Object>>) newSourceNode.get("transfers"));
    } else {
      // Add new source node
      sourceNodes.add(newSourceNode);
    }

    redisMap.put("snodes", sourceNodes);
  }

  public String getCacheKey(String orgId, String dropoffNodeId) {
    StringBuilder key = new StringBuilder(String.format("%s:%s", orgId, dropoffNodeId));
    return key.toString();
  }

  public Map<String, Object> toHashValue(TransferScheduleCreationRequest request) {
    List<Map<String, Object>> transfers = new ArrayList<>();
    Map<String, Object> routeDetails = new HashMap<>();
    routeDetails.put("tst", request.getStartTime().toString());
    routeDetails.put("tet", request.getEndTime().toString());
    routeDetails.put("rule", request.getRule());
    routeDetails.put("ruleName", request.getRuleName());
    transfers.add(routeDetails);

    Map<String, Object> sourceNode = new HashMap<>();
    sourceNode.put("id", request.getSourceNodeId());
    sourceNode.put("transfers", transfers);

    return sourceNode;
  }

  @Override
  public void delete(TransferScheduleRequest request) {
    RMap<String, List<Map<String, Object>>> redisMap =
        redissonClient.getMap(getCacheKey(request.getOrgId(), request.getDropoffNodeId()));
    if (!redisMap.isEmpty()) {
      List<Map<String, Object>> sourceNodes = redisMap.get("snodes");
      if (sourceNodes != null) {
        sourceNodes.forEach(
            node -> {
              if (node.get("id").equals(request.getSourceNodeId())) {
                List<Map<String, Object>> transfers =
                    (List<Map<String, Object>>) node.get("transfers");
                transfers.removeIf(
                    route -> route.get("tst").equals(request.getStartTime().toString()));
              }
            });
        sourceNodes.removeIf(node -> ((List<Map<String, Object>>) node.get("transfers")).isEmpty());
        if (sourceNodes.isEmpty()) {
          redisMap.delete();
        } else {
          redisMap.put("snodes", sourceNodes);
        }
      }
    }
  }

  @Override
  public List<TransferScheduleResponse> fetch(
      TransferScheduleCacheRequest request, DateTime startRange, DateTime endRange) {
    List<TransferScheduleResponse> validTransfers = new ArrayList<>();
    String key = getCacheKey(request.getOrgId(), request.getDropoffNode());

    RMap<String, List<Map<String, Object>>> redisMap = redissonClient.getMap(key);
    if (!redisMap.isEmpty()) {
      List<Map<String, Object>> sourceNodes = redisMap.get("snodes");
      if (sourceNodes != null) {
        for (Map<String, Object> node : sourceNodes) {
          List<Map<String, Object>> transfers = (List<Map<String, Object>>) node.get("transfers");
          for (Map<String, Object> transfer : transfers) {
            DateTime startTime = new DateTime(transfer.get("tst"), DateTimeZone.UTC);
            DateTime endTime = new DateTime(transfer.get("tet"), DateTimeZone.UTC);
            boolean matchesRule =
                (request.getRule() == null || request.getRule().equals(transfer.get("rule")));
            boolean matchesRuleName =
                (request.getRuleName() == null
                    || request.getRuleName().equals(transfer.get("ruleName")));
            if ((startTime.isBefore(endRange)
                    || startTime.isEqual(endRange)
                    || endTime.isAfter(startRange)
                    || endTime.isEqual(startRange))
                && matchesRule
                && matchesRuleName) {
              TransferScheduleResponse response =
                  TransferScheduleResponse.builder()
                      .orgId(request.getOrgId())
                      .sourceNodeId((String) node.get("id"))
                      .dropoffNodeId(request.getDropoffNode())
                      .startTime(startTime.toDate())
                      .endTime(endTime.toDate())
                      .rule(request.getRule())
                      .ruleName(request.getRuleName())
                      .build();
              validTransfers.add(response);
            }
          }
        }
      }
    }
    return validTransfers;
  }
}
