package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class JobsDashboardService {
  private final JobsDashboardClient jobsDashboardClient;
  private final Logger logger = LoggerFactory.getLogger(JobsDashboardService.class);

  public List<RecordStatusDto> getJobRecords(String jobId, String orgId, Optional<String> status)
      throws CommonServiceException {
    logger.debug("Processing job records for orgId and jobId");
    try {
      BaseResponse<List<RecordStatusDto>> baseResponse =
          jobsDashboardClient.getJobRecords(orgId, jobId, status.orElse(null));
      if (baseResponse != null && !CollectionUtils.isEmpty(baseResponse.getPayload())) {
        return baseResponse.getPayload();
      } else {
        logger.error(
            "Job records does not exists for orgId, jobId and status", orgId, jobId, status);
        throw new CommonServiceException(
            "Job records does not exists", HttpStatus.BAD_REQUEST, 0x1771, null);
      }
    } catch (Exception e) {
      logger.error("Error while fetching the job records");
      throw new CommonServiceException(
          "Error while fetching the job records", HttpStatus.BAD_REQUEST, 0x1772, null);
    }
  }
}
