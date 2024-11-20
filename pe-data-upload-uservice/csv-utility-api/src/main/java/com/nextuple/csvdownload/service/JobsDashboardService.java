/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
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
