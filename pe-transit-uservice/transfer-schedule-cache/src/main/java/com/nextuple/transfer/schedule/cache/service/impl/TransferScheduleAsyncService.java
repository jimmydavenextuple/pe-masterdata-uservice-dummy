package com.nextuple.transfer.schedule.cache.service.impl;

import com.nextuple.transfer.schedule.cache.repository.TransferScheduleRedisRepository;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransferScheduleAsyncService {
  @Autowired TransferScheduleRedisRepository transferScheduleRedisRepository;

  @Async(value = "transferScheduleTaskExecutor")
  public CompletableFuture<Void> saveTransferSchedulesAsync(
      List<TransferScheduleResponse> responses) {
    responses.forEach(
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
    return CompletableFuture.completedFuture(null);
  }
}
