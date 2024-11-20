/*
 *
 *  * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.jobs.dashboard.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NEIP;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.PE;

import com.nextuple.common.base.PagePayload;
import com.nextuple.jobs.consumers.domain.mapper.JobsFpmMapper;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.plt.client.FPMServiceClient;
import com.nextuple.plt.model.FeedFilters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobsFpmService {
  private static final JobsFpmMapper INSTANCE = Mappers.getMapper(JobsFpmMapper.class);
  @Autowired FPMServiceClient fpmServiceClient;
  @Autowired JobService jobService;

  @Value("${neip.job-types:itembuffer}")
  private String fpmJobTypes;

  public PagePayload<JobResponseForNotification> getJobResponseForNotificationPagePayload(
      String orgId, JobFilters jobFilters, int requiredPageNo, int requiredPageSize)
      throws JobException, com.nextuple.plt.exception.JobException {
    List<JobResponseForNotification> responseList = new ArrayList<>();
    PagePayload<JobResponseForNotification> finalRespFromJobsFramework = new PagePayload<>();

    getJobFrameworkRecords(orgId, jobFilters, requiredPageNo, requiredPageSize, responseList);
    FeedFilters feedFilters = getFeedFilters(jobFilters, requiredPageNo, requiredPageSize);
    getFpmRecords(orgId, feedFilters, responseList);

    responseList.sort(
        Comparator.comparing(
            JobResponse::getProcessingStartedAt, Comparator.nullsFirst(Comparator.reverseOrder())));
    finalRespFromJobsFramework.setData(responseList.stream().limit(requiredPageSize).toList());
    return finalRespFromJobsFramework;
  }

  private static FeedFilters getFeedFilters(
      JobFilters jobFilters, int requiredPageNo, int requiredPageSize) {
    FeedFilters feedFilters = new FeedFilters();
    feedFilters.setPageSize(Optional.of(requiredPageSize));
    feedFilters.setPageNo(Optional.of(requiredPageNo));
    feedFilters.setSortOrder(jobFilters.getSortOrder());
    feedFilters.setFeedType(jobFilters.getJobType());
    feedFilters.setSortBy(jobFilters.getSortBy());
    return feedFilters;
  }

  private void getFpmRecords(
      String orgId, FeedFilters feedFilters, List<JobResponseForNotification> responseList)
      throws com.nextuple.plt.exception.JobException {
    com.nextuple.plt.model.PagePayload<com.nextuple.plt.domain.outbound.JobResponse> fpmResponse =
        fpmServiceClient.getJobsByFilter(orgId, feedFilters).getBody();
    List<String> jobTypes = Arrays.stream(fpmJobTypes.split(",")).map(String::trim).toList();
    if (Objects.nonNull(fpmResponse)) {
      for (com.nextuple.plt.domain.outbound.JobResponse response : fpmResponse.getData()) {
        JobResponseForNotification jobResponseForNotification =
            INSTANCE.toJobResponseForNotification(response);
        if (jobTypes.contains(response.getFeedType())) {
          jobResponseForNotification.setJobType(response.getFeedType());
          jobResponseForNotification.setOrigin(NEIP);
          responseList.add(jobResponseForNotification);
        }
      }
    }
  }

  private void getJobFrameworkRecords(
      String orgId,
      JobFilters jobFilters,
      int requiredPageNo,
      int requiredPageSize,
      List<JobResponseForNotification> responseList)
      throws JobException {
    PagePayload<JobResponse> pageResp =
        jobService.getJobsByJobInfo(
            orgId,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize);
    for (JobResponse response : pageResp.getData()) {
      JobResponseForNotification jobResponseForNotification =
          INSTANCE.toJobResponseForNotification(response);
      jobResponseForNotification.setOrigin(PE);
      responseList.add(jobResponseForNotification);
    }
  }
}
