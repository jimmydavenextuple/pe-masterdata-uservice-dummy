package com.hbc.jobs.consumers.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobService;
import com.hbc.jobs.consumers.util.UriBuilder;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.JobFilters;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JobController {
  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  /**
   * @param orgId
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
    log.debug("--Inside getJobRecordsByFilter controller--");

    List<RecordStatusDto> pageResp = jobService.getJobResults(orgId, jobId, status);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of the job information is successful")
                .build());
  }

  /**
   * @param jobDto
   * @return
   * @throws JobException
   */
  @PostMapping(
      value = "/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobDto>> createJob(@Valid @RequestBody JobDto jobDto)
      throws JobException {
    log.info("-- Inside createJob controller --");

    JobDto job = jobService.createJob(jobDto);
    return ResponseEntity.ok(
        BaseResponse.builder().message("Job successfully created").payload(job).build());
  }

  /**
   * @param orgId
   * @param jobId
   * @return
   * @throws JobException
   */
  @GetMapping(path = "/org/{orgId}/jobs/{jobId}")
  public ResponseEntity<BaseResponse<JobDto>> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable String jobId)
      throws JobException {
    log.info("-- Inside getJob controller --");

    JobDto job = jobService.getJob(jobId, orgId);

    log.info("Job successfully retrieved : {}", jobId);

    return ResponseEntity.ok(
        BaseResponse.builder().message("Retrieval of the job is successful").payload(job).build());
  }

  /**
   * @param orgId
   * @param jobFilters
   * @return
   * @throws JobException
   */
  @GetMapping(value = "/org/{orgId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<JobDto>>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId, JobFilters jobFilters)
      throws JobException {
    log.debug("--Inside getJobsByFilter()--");

    int requiredPageNo = jobFilters.getPageNo();
    int requiredPageSize = jobFilters.getPageSize();

    if (requiredPageNo < 1) {
      throw new JobException("PageNo can not be less than one", null, requiredPageNo);
    }

    Page<JobDto> pageResp =
        jobService.getJobs(
            orgId,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize);

    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(requiredPageNo);
    pagination.setSortBy(jobFilters.getSortBy());
    pagination.setSortOrder(jobFilters.getSortOrder());

    PagePayload<JobDto> pagePayload = new PagePayload<>();
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);

    String nextUri =
        UriBuilder.buildUriForPagination(
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize,
            pageResp.getTotalPages(),
            "next");
    String previousUri =
        UriBuilder.buildUriForPagination(
            jobFilters.getJobType(),
            jobFilters.getDays(),
            jobFilters.getSortBy(),
            jobFilters.getSortOrder(),
            requiredPageNo,
            requiredPageSize,
            pageResp.getTotalPages(),
            "previous");
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pagePayload)
                .message("Retrieval of the jobs by params is successful")
                .build());
  }
}
