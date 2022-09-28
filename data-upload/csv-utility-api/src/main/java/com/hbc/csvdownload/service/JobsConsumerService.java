package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobsConsumerService {
  private final JobsConsumerClient jobsConsumerClient;
  private final Logger logger = LoggerFactory.getLogger(JobsConsumerService.class);

  public JobDto getJob(String jobId, String orgId) throws CommonServiceException {
    logger.debug("Processing job dto for orgId and jobId");
    try {
      BaseResponse<JobDto> baseResponse = jobsConsumerClient.getJob(orgId, jobId);
      if (baseResponse != null && baseResponse.getPayload() != null) {
        return baseResponse.getPayload();
      } else {
        logger.error("Job dto does not exists for orgId and jobId", orgId, jobId);
        throw new CommonServiceException(
            "Job dto does not exists", HttpStatus.BAD_REQUEST, 0x1771, null);
      }
    } catch (Exception e) {
      logger.error("Error while fetching the job records");
      throw new CommonServiceException(
          "Error while fetching the job records", HttpStatus.BAD_REQUEST, 0x1772, null);
    }
  }
}
