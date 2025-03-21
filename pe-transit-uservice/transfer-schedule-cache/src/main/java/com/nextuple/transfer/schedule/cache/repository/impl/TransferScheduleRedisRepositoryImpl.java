/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.repository.impl;

import com.nextuple.transfer.schedule.cache.repository.TransferScheduleRedisRepository;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
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
    String key =
        getCacheKey(
            request.getOrgId(),
            request.getDropoffNodeId(),
            request.getRule(),
            request.getRuleName());
    RMap<String, List<Map<String, Object>>> redisMap = redissonClient.getMap(key);

    List<Map<String, Object>> sourceNodes = redisMap.getOrDefault("sourceNodes", new ArrayList<>());

    // Check if the source node already exists
    Optional<Map<String, Object>> existingSourceNode =
        sourceNodes.stream()
            .filter(node -> node.get("id").equals(request.getSourceNodeId()))
            .findFirst();

    Map<String, Object> newSourceNode = toHashValue(request);
    if (existingSourceNode.isPresent()) {
      // Update existing source node with new route
      List<Map<String, Object>> routes =
          (List<Map<String, Object>>) existingSourceNode.get().get("routes");
      routes.addAll((List<Map<String, Object>>) newSourceNode.get("routes"));
    } else {
      // Add new source node
      sourceNodes.add(newSourceNode);
    }

    redisMap.put("sourceNodes", sourceNodes);
  }

  public String getCacheKey(String orgId, String dropoffNodeId, String rule, String ruleName) {
    StringBuilder key = new StringBuilder(String.format("%s:%s", orgId, dropoffNodeId));
    if (rule != null && !rule.isEmpty()) {
      key.append(":").append(rule);
    }
    if (ruleName != null && !ruleName.isEmpty()) {
      key.append(":").append(ruleName);
    }
    return key.toString();
  }

  public Map<String, Object> toHashValue(TransferScheduleCreationRequest request) {
    List<Map<String, Object>> routes = new ArrayList<>();
    Map<String, Object> routeDetails = new HashMap<>();
    routeDetails.put("startTime", request.getStartTime().toString());
    routeDetails.put("endTime", request.getEndTime().toString());
    routes.add(routeDetails);

    Map<String, Object> sourceNode = new HashMap<>();
    sourceNode.put("id", request.getSourceNodeId());
    sourceNode.put("routes", routes);

    return sourceNode;
  }

  public void deleteTransferSchedule(TransferScheduleRequest request) {
    Set<String> keys =
        (Set<String>)
            redissonClient
                .getKeys()
                .getKeysByPattern(request.getOrgId() + ":" + request.getDropoffNodeId() + ":*");

    for (String cacheKey : keys) {
      RMap<String, List<Map<String, Object>>> redisMap = redissonClient.getMap(cacheKey);
      if (!redisMap.isEmpty()) {
        List<Map<String, Object>> sourceNodes = redisMap.get("sourceNodes");
        if (sourceNodes != null) {
          sourceNodes.forEach(
              node -> {
                if (node.get("id").equals(request.getSourceNodeId())) {
                  List<Map<String, Object>> routes = (List<Map<String, Object>>) node.get("routes");
                  routes.removeIf(
                      route -> route.get("startTime").equals(request.getStartTime().toString()));
                }
              });
          sourceNodes.removeIf(node -> ((List<Map<String, Object>>) node.get("routes")).isEmpty());
          if (sourceNodes.isEmpty()) {
            redisMap.delete();
          } else {
            redisMap.put("sourceNodes", sourceNodes);
          }
        }
      }
    }
  }
}
