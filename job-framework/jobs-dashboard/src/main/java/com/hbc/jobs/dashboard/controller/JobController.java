package com.hbc.jobs.dashboard.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.exception.JobException;
import com.hbc.jobs.dashboard.service.JobService;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class JobController {

  private final JobService jobService;

  private static final String MESSAGE = "Job successfully created!";

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @Value("${pagination.default-page-no}")
  private Integer defaultPageNo;

  @Value("${pagination.default-page-size}")
  private Integer defaultPageSize;

  @PostMapping(
      path = "/org/{orgId}/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = {MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BaseResponse<JobDto>> processJobOffline(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @RequestParam("file") MultipartFile csvFile)
      throws JobException {
    log.debug("Processing offline job request");

    JobDto jobDto = jobService.processJobOffline(csvFile, orgId, jobType);

    log.debug("Processing offline job request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @PostMapping(
      path = "/org/{orgId}/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = "text/plain;charset=UTF-8")
  public ResponseEntity<BaseResponse<JobDto>> processJobJsonOffline(
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestBody String request)
      throws JobException {
    log.debug("Processing offline job json request");

    JobDto jobDto = jobService.processJobJsonOffline(request, orgId, jobType, Optional.empty());

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @PutMapping(
      path = "/org/{orgId}/jobs/{jobId}",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobDto>> processJobJsonOffline(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @RequestBody String request,
      @PathVariable String jobId)
      throws JobException {
    log.debug("Processing offline job json request");

    JobDto jobDto =
        jobService.processJobJsonOffline(request, orgId, jobType, Optional.ofNullable(jobId));

    log.debug("Processing offline job json request ends");

    return ResponseEntity.ok(BaseResponse.builder().message(MESSAGE).payload(jobDto).build());
  }

  @GetMapping(path = "/org/{orgId}/jobs/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobDto>> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable String jobId)
      throws JobException {
    log.debug("Inside getJob method");

    try {
      JobDto job = jobService.getJob(orgId, jobId);

      log.debug("Job successfully retrieved : {}", jobId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .payload(job)
              .message("Retrieval of the job is successful")
              .build());
    } catch (Exception e) {
      log.error("Failed to process get job by jobId request!!", e);
      throw e;
    }
  }

  /**
   * @param orgId
   * @param jobFilters
   * @return
   * @throws JobException
   */
  @GetMapping(path = "/org/{orgId}/jobs/filters", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<JobDto>>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId, JobFilters jobFilters)
      throws JobException {
    log.debug("--Inside getJobsByFilter()--");
    try {
      int requiredPageNo = jobFilters.getPageNo().orElse(defaultPageNo);
      int requiredPageSize = jobFilters.getPageSize().orElse(defaultPageSize);

      if (requiredPageNo < 1) {
        JobTypeEnum jobTypeEnum = jobFilters.getJobType().map(JobTypeEnum::valueOf).orElse(null);
        throw new JobException("PageNo can not be less than one", null, jobTypeEnum);
      }

      PagePayload<JobDto> pageResp =
          jobService.getJobsByJobInfo(
              orgId,
              jobFilters.getJobType(),
              jobFilters.getDays(),
              jobFilters.getSortBy(),
              jobFilters.getSortOrder(),
              requiredPageNo,
              requiredPageSize);

      return ResponseEntity.ok()
          .body(
              BaseResponse.builder()
                  .payload(pageResp)
                  .message("Retrieval of jobs is successful")
                  .build());
    } catch (Exception e) {
      log.error("Error while Fetching Jobs by params - getJobsByFilter()", e);
      throw e;
    }
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
      path = "/org/{orgId}/jobs/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<RecordStatusDto>>> getJobRecordsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(required = false) Optional<String> status)
      throws JobException {
    log.debug("--Inside getJobRecordsByFilter()--");
    try {

      List<RecordStatusDto> pageResp = jobService.getJobResults(orgId, jobId, status);

      return ResponseEntity.ok()
          .body(
              BaseResponse.builder()
                  .payload(pageResp)
                  .message("Retrieval of job records is successful")
                  .build());
    } catch (Exception e) {
      log.error("Error while Fetching Jobs records information - getJobRecordsByFilter()", e);
      throw e;
    }
  }
}
