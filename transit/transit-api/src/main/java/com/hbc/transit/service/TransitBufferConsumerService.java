package com.hbc.transit.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
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
          {
            if (transitBufferReqJobRefResponse.getAction().equals(TransitBufferReqJobRefEnum.CREATE)
                || transitBufferReqJobRefResponse
                    .getAction()
                    .equals(TransitBufferReqJobRefEnum.UPDATE))
              transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                  transitBufferConfigRequest.getId(),
                  TransitBufferConfigRequestStatusEnum.COMPLETED);
            if (transitBufferReqJobRefResponse
                .getAction()
                .equals(TransitBufferReqJobRefEnum.DELETE))
              transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                  transitBufferConfigRequest.getId(), TransitBufferConfigRequestStatusEnum.DELETED);
            break;
          }

        case FAILED:
          {
            transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                transitBufferConfigRequest.getId(), TransitBufferConfigRequestStatusEnum.ERROR);
            break;
          }
        default:
          break;
      }
    }
  }
}
