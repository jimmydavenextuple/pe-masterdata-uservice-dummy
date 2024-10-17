/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.controller;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATA_UPLOAD_PAGINATION_URL_CONSTANT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.jobs.dashboard.controller.docs.GetJobDoc;
import com.nextuple.jobs.dashboard.controller.docs.GetJobRecordsByFilters;
import com.nextuple.jobs.dashboard.controller.docs.GetJobsByFilterDoc;
import com.nextuple.jobs.dashboard.controller.docs.ProcessJobJsonBySchedulerDoc;
import com.nextuple.jobs.dashboard.controller.docs.ProcessJobOfflineDoc;
import com.nextuple.jobs.dashboard.controller.docs.ProcessJobOfflineWithFileMetaDataIdDoc;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.dashboard.service.JobService;
import com.nextuple.jobs.dashboard.service.JobsFpmService;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Job Dashboard APIs")
public class JobDashboardController {

  private static final String GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT
          + "/v1/org/%s/jobs-dashboard/%s/results?pageNo=%d&pageSize=%d";

  private final JobService jobService;
  private final JobsFpmService jobsFpmService;

  private final DefaultPageProperties defaultPageProperties;
  private static final String MESSAGE = "Job successfully created!";
  private static final String PAGINATION_URL =
      DATA_UPLOAD_PAGINATION_URL_CONSTANT + "/org/%s/jobs/filters/?pageNo=%d&pageSize=%d";

  @PostMapping(
      path = "/org/{orgId}/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_OCTET_STREAM_VALUE)
  @ProcessJobOfflineDoc
  public ResponseEntity<BaseResponse<JobResponse>> processJobOffline(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @RequestParam @NotNull @Valid @Parameter(description = "Type of job.") JobTypeEnum jobType,
      @RequestBody @Parameter(description = "The file to be uploaded.") ByteArrayResource csvFile,
      @RequestParam @Parameter(description = "Name of the file.") @NotNull @Valid String fileName)
      throws JobException {
    log.debug("Processing offline job request");

    var jobDto = jobService.processJobOffline(csvFile, orgId, jobType, fileName);

    log.debug("Processing offline job request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @PostMapping(
      path = "/org/{orgId}/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = "text/plain;charset=UTF-8")
  @Hidden
  public ResponseEntity<BaseResponse<JobResponse>> processJobJsonOffline(
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestBody String request)
      throws JobException {
    log.debug("Processing offline job json request");

    var jobDto = jobService.processJobJsonOffline(request, orgId, jobType, Optional.empty());

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @PutMapping(path = "/org/{orgId}/jobs/{jobId}", produces = APPLICATION_JSON_VALUE)
  @ProcessJobJsonBySchedulerDoc
  public ResponseEntity<BaseResponse<JobResponse>> processJobJsonByScheduler(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @RequestParam @NotNull @Valid @Parameter(description = "Type of job.") JobTypeEnum jobType,
      @PathVariable @Parameter(description = "Unique identifier for the job.") String jobId)
      throws JobException {
    log.debug("Processing offline job json request");

    var jobDto = jobService.processJobJsonOffline(orgId, jobType, Optional.ofNullable(jobId));

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @GetMapping(path = "/org/{orgId}/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetJobDoc
  public ResponseEntity<BaseResponse<JobDto>> getJob(
      @NotEmpty
          @NotNull
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotEmpty
          @NotNull
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String jobId)
      throws JobException {
    log.debug("Inside getJob method");

    JobDto job = jobService.getJob(orgId, jobId);

    log.debug("Job successfully retrieved : {}", jobId);

    return ResponseEntity.ok(
        BaseResponse.builder().payload(job).message("Retrieval of the job is successful").build());
  }

  /**
   * @param orgId
   * @param jobFilters
   * @return
   * @throws JobException
   */
  @GetMapping(path = "/org/{orgId}/jobs/filters", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetJobsByFilterDoc
  public ResponseEntity<BaseResponse<PagePayload<JobResponse>>> getJobsByFilter(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @ModelAttribute("applicationForm") @Valid JobFilters jobFilters)
      throws JobException {
    log.debug("--Inside getJobsByFilter()--");
    int requiredPageNo = jobFilters.getPageNo().orElse(defaultPageProperties.getPageNo());
    int requiredPageSize = jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize());

    if (requiredPageNo < 1) {
      var jobTypeEnum = jobFilters.getJobType().map(JobTypeEnum::valueOf).orElse(null);
      throw new JobException("PageNo can not be less than one", null, jobTypeEnum);
    }

    PagePayload<JobResponse> pageResp =
        jobService.getJobsByJobInfo(
            orgId,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize);
    String nextUri =
        PaginationUtil.buildUriForPagination(
            requiredPageNo,
            pageResp.getPagination().getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                requiredPageNo + 1,
                jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            requiredPageNo,
            pageResp.getPagination().getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                requiredPageNo - 1,
                jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize())));

    pageResp.getPagination().setNext(nextUri);
    pageResp.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of jobs is successful")
                .build());
  }

  /**
   * Get the detailed information of a job
   *
   * @param jobId
   * @param status
   * @return
   * @throws JobException
   */
  @GetMapping(
      path = "/org/{orgId}/jobs-dashboard/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetJobRecordsByFilters
  public ResponseEntity<BaseResponse<List<RecordStatusDto>>> getJobRecordsByFilter(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @NotEmpty
          @NotNull
          @PathVariable("jobId")
          @Parameter(description = "Unique identifier for the job.")
          String jobId,
      @RequestParam(required = false) @Parameter(description = "Status of the job.")
          Optional<String> status)
      throws JobException {
    log.debug("--Inside getJobRecordsByFilter()--");

    List<RecordStatusDto> pageResp = jobService.getJobResults(orgId, jobId, status);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of job records is successful")
                .build());
  }

  @PostMapping(path = "/v1/org/{orgId}/jobs", produces = APPLICATION_JSON_VALUE)
  @ProcessJobOfflineWithFileMetaDataIdDoc
  public ResponseEntity<BaseResponse<JobResponse>> processJobOfflineWithFileMetaDataId(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @RequestParam @NotNull @Parameter(description = "Type of the job.") JobTypeEnum jobType,
      @RequestParam("fileMetadataId")
          @NotNull
          @Parameter(description = "Unique identifier for the file metadata.")
          long fileMetadataId)
      throws JobException {
    log.debug("Processing offline job request");

    var jobDto = jobService.processJobOffline(orgId, jobType, fileMetadataId);

    log.debug("Processing offline job request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @GetMapping(
      path = "/v1/org/{orgId}/jobs-dashboard/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @GetJobRecordsByFilters
  public ResponseEntity<BaseResponse<PagePayload<RecordStatusDto>>> getJobRecordsByFilters(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @NotEmpty
          @NotNull
          @PathVariable("jobId")
          @Parameter(description = "Unique identifier for the job.")
          String jobId,
      @RequestParam(required = false) @Parameter(description = "Status of the job.")
          Optional<String> status,
      @RequestParam(required = false) @Parameter(description = "Number of the page.")
          Optional<Integer> pageNo,
      @RequestParam(required = false) @Parameter(description = "Number of records in a page.")
          Optional<Integer> pageSize)
      throws JobException {
    log.debug("--Inside getJobRecordsByFilter()--");

    int currentPageNo = pageNo.orElse(defaultPageProperties.getPageNo());
    int currentPageSize = pageSize.orElse(defaultPageProperties.getPageSize());

    PagePayload<RecordStatusDto> pageResp =
        jobService.getJobRecordsByFilters(orgId, jobId, status, currentPageNo, currentPageSize);

    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPageNo,
            pageResp.getPagination().getTotalPages(),
            "next",
            GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL.formatted(
                orgId, jobId, currentPageNo + 1, currentPageSize));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPageNo,
            pageResp.getPagination().getTotalPages(),
            "previous",
            GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL.formatted(
                orgId, jobId, currentPageNo - 1, currentPageSize));

    pageResp.getPagination().setNext(nextUri);
    pageResp.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of job records is successful")
                .build());
  }

  @GetMapping(path = "/v2/org/{orgId}/jobs/filters", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetJobsByFilterDoc
  public ResponseEntity<BaseResponse<PagePayload<JobResponseForNotification>>> getJobsByFilterV2(
      @NotEmpty
          @NotNull
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier for the organization.")
          String orgId,
      @ModelAttribute("applicationForm") @Valid JobFilters jobFilters)
      throws JobException, com.nextuple.plt.exception.JobException {
    log.debug("--Inside getJobsByFilter()--");
    int requiredPageNo = jobFilters.getPageNo().orElse(defaultPageProperties.getPageNo());
    int requiredPageSize = jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize());

    if (requiredPageNo < 1) {
      var jobTypeEnum = jobFilters.getJobType().map(JobTypeEnum::valueOf).orElse(null);
      throw new JobException("PageNo can not be less than one", null, jobTypeEnum);
    }

    PagePayload<JobResponseForNotification> finalResp =
        jobsFpmService.getJobResponseForNotificationPagePayload(
            orgId, jobFilters, requiredPageNo, requiredPageSize);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(finalResp)
                .message("Retrieval of jobs is successful")
                .build());
  }
}
