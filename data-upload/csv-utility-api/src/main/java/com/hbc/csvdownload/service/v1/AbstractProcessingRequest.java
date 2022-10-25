package com.hbc.csvdownload.service.v1;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AbstractProcessingRequest {

  private final Logger logger = LoggerFactory.getLogger(AbstractProcessingRequest.class);
  private final JobsDashboardClient jobsDashboardClient;

  public JobResponse submitJob(String orgId, JobTypeEnum jobType, Long fileMetadataId)
      throws JobSubmissionException {
    try {
      return jobsDashboardClient.processJobOffline(orgId, jobType, fileMetadataId).getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while submitting job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      logger.error("Error while submitting job to job framework", e);
      throw new JobSubmissionException("Error while submitting job to job framework", e, orgId);
    }
  }
}
