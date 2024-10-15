/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransitBufferConsumerService {
  private final TransitBufferConfigRequestService transitBufferConfigRequestService;
  private final TransitBufferReqJobRefService transitBufferReqJobRefService;

  public void processJobRecordForTransitBuffer(JobDetailsDto jobDetailsDto)
      throws TransitBufferReqJobRefDomainException, CommonServiceException {
    if (jobDetailsDto.getJobType().equals(JobTypeEnum.TRANSIT_BUFFER_REQUEST)) {
      var transitBufferReqJobRefResponse =
          transitBufferReqJobRefService
              .getTransitBufferReqJobRefByExtReferenceId(jobDetailsDto.getJobId())
              .get(0);
      var transitBufferConfigRequest =
          transitBufferConfigRequestService.getTransitBufferRequest(
              transitBufferReqJobRefResponse.getTransitBufferReqId());
      switch (jobDetailsDto.getStatus()) {
        case COMPLETED:
          transitBufferUpdateOnComplete(transitBufferReqJobRefResponse, transitBufferConfigRequest);
          break;

        case FAILED:
          transitBufferUpdateOnFailed(transitBufferConfigRequest);
          break;
        default:
          break;
      }
    }
  }

  private void transitBufferUpdateOnFailed(
      TransitBufferConfigRequestDomainDto transitBufferConfigRequest)
      throws CommonServiceException {
    transitBufferConfigRequestService.updateTransitBufferRequestStatus(
        transitBufferConfigRequest.getId(), TransitBufferConfigRequestStatusEnum.ERROR);
  }

  private void transitBufferUpdateOnComplete(
      TransitBufferReqJobRefResponse transitBufferReqJobRefResponse,
      TransitBufferConfigRequestDomainDto transitBufferConfigRequest)
      throws CommonServiceException {
    if (transitBufferReqJobRefResponse.getAction().equals(TransitBufferReqJobRefEnum.CREATE)
        || transitBufferReqJobRefResponse.getAction().equals(TransitBufferReqJobRefEnum.UPDATE))
      transitBufferConfigRequestService.updateTransitBufferRequestStatus(
          transitBufferConfigRequest.getId(), TransitBufferConfigRequestStatusEnum.COMPLETED);
    if (transitBufferReqJobRefResponse.getAction().equals(TransitBufferReqJobRefEnum.DELETE))
      transitBufferConfigRequestService.updateTransitBufferRequestStatus(
          transitBufferConfigRequest.getId(), TransitBufferConfigRequestStatusEnum.DELETED);
  }
}
