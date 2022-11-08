package com.nextuple.jobs.dashboard.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.jobs.dashboard.exception.JobException;
import com.nextuple.jobs.dashboard.service.JobService;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
public class JobDashboardController {

  private static final String GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL =
      "/data-upload/v1/org/%s/jobs-dashboard/%s/results?pageNo=%d&pageSize=%d";

  private final JobService jobService;

  private final DefaultPageProperties defaultPageProperties;
  private static final String MESSAGE = "Job successfully created!";
  private static final String PAGINATION_URL =
      "/data-upload/org/%s/jobs/filters/?pageNo=%d&pageSize=%d";

  @PostMapping(
      path = "/org/{orgId}/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<BaseResponse<JobResponse>> processJobOffline(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @RequestBody ByteArrayResource csvFile,
      @RequestParam("fileName") @NotNull @Valid String fileName)
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
  public ResponseEntity<BaseResponse<JobResponse>> processJobJsonOffline(
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestBody String request)
      throws JobException {
    log.debug("Processing offline job json request");

    var jobDto = jobService.processJobJsonOffline(request, orgId, jobType, Optional.empty());

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @PutMapping(path = "/org/{orgId}/jobs/{jobId}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobResponse>> processJobJsonByScheduler(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @PathVariable String jobId)
      throws JobException {
    log.debug("Processing offline job json request");

    var jobDto = jobService.processJobJsonOffline(orgId, jobType, Optional.ofNullable(jobId));

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @GetMapping(path = "/org/{orgId}/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobDto>> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable String jobId)
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
  public ResponseEntity<BaseResponse<PagePayload<JobResponse>>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId, JobFilters jobFilters)
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
            String.format(
                PAGINATION_URL,
                orgId,
                requiredPageNo + 1,
                jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            requiredPageNo,
            pageResp.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
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
  public ResponseEntity<BaseResponse<List<RecordStatusDto>>> getJobRecordsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(required = false) Optional<String> status)
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
  public ResponseEntity<BaseResponse<JobResponse>> processJobOfflineWithFileMetaDataId(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam @NotNull JobTypeEnum jobType,
      @RequestParam("fileMetadataId") @NotNull long fileMetadataId)
      throws JobException {
    log.debug("Processing offline job request");

    var jobDto = jobService.processJobOffline(orgId, jobType, fileMetadataId);

    log.debug("Processing offline job request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @GetMapping(
      path = "/v1/org/{orgId}/jobs-dashboard/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<RecordStatusDto>>> getJobRecordsByFilters(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(required = false) Optional<String> status,
      @RequestParam(required = false) Optional<Integer> pageNo,
      @RequestParam(required = false) Optional<Integer> pageSize)
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
            String.format(
                GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL,
                orgId,
                jobId,
                currentPageNo + 1,
                currentPageSize));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPageNo,
            pageResp.getPagination().getTotalPages(),
            "previous",
            String.format(
                GET_JOB_RECORDS_BY_FILTERS_PAGINATION_URL,
                orgId,
                jobId,
                currentPageNo - 1,
                currentPageSize));

    pageResp.getPagination().setNext(nextUri);
    pageResp.getPagination().setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of job records is successful")
                .build());
  }
}
