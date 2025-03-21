/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.service.impl;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transfer.schedule.cache.dto.TransferScheduleCacheRequest;
import com.nextuple.transfer.schedule.cache.repository.TransferScheduleRedisRepository;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRangeRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferScheduleCacheServiceImpl {
  @Autowired TransferScheduleRedisRepository transferScheduleRedisRepository;
  @Autowired TransferScheduleFeign transferScheduleFeign;

  @Value("${transfer-schedule.horizon-days:5}")
  private int horizonDays;

  @Value("${transfer-schedule.past-days:5}")
  private int pastDays;

  public void saveToRedis(TransferScheduleCreationRequest request) {
    transferScheduleRedisRepository.save(request);
  }

  public void deleteFromRedis(TransferScheduleRequest request) {
    transferScheduleRedisRepository.delete(request);
  }

  public List<TransferScheduleResponse> fetchFromRedis(TransferScheduleCacheRequest request) {
    DateTime startRange = new DateTime(request.getDateBucket()).minusDays(pastDays - 1);
    startRange = startRange.withTime(0, 0, 0, 0);
    DateTime endRange = new DateTime(request.getDateBucket()).plusDays(horizonDays - 1);
    endRange = endRange.withTime(23, 59, 59, 0);
    List<TransferScheduleResponse> transfers =
        transferScheduleRedisRepository.fetch(request, startRange, endRange);
    if (transfers.isEmpty()) {
      try {
        DateTime startTime = new DateTime(request.getDateBucket());
        startTime = startTime.withTime(0, 0, 0, 0);
        DateTime endTime = new DateTime(request.getDateBucket());
        endTime = endTime.withTime(23, 59, 59, 0);
        TransferScheduleRangeRequest transferScheduleRangeRequest =
            TransferScheduleRangeRequest.builder()
                .orgId(request.getOrgId())
                .dropoffNodeId(request.getDropoffNode())
                .rule(request.getRule())
                .ruleName(request.getRuleName())
                .startTime(startTime)
                .horizonDays(horizonDays)
                .endTime(endTime)
                .pastDays(pastDays)
                .exclusive(true)
                .build();

        BaseResponse<List<TransferScheduleResponse>> responseList =
            transferScheduleFeign.fetchTransferSchedulesInRange(transferScheduleRangeRequest);
        if (responseList.getPayload().isEmpty()) {
          return List.of();
        }
        responseList
            .getPayload()
            .forEach(
                response ->
                    transferScheduleRedisRepository.save(
                        TransferScheduleCreationRequest.builder()
                            .orgId(response.getOrgId())
                            .sourceNodeId(response.getSourceNodeId())
                            .dropoffNodeId(response.getDropoffNodeId())
                            .startTime(new DateTime(response.getStartTime()))
                            .endTime(new DateTime(response.getEndTime()))
                            .rule(response.getRule())
                            .ruleName(response.getRuleName())
                            .build()));
        return responseList.getPayload();
      } catch (RuntimeException ex) {
        return List.of();
      }
    }
    return transfers;
  }
}
