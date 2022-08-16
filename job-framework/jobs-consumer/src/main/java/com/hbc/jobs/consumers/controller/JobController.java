package com.hbc.jobs.consumers.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.consumers.exception.JobException;
import com.hbc.jobs.consumers.service.JobService;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${pagination.default-page-no}")
  private Integer defaultPageNo;

  @Value("${pagination.default-page-size}")
  private Integer defaultPageSize;

  @Value("${pagination.default-sort-field}")
  private String defaultSortField;

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
    try {

      List<RecordStatusDto> pageResp = jobService.getJobResults(orgId, jobId, status);

      return ResponseEntity.ok()
          .body(
              BaseResponse.builder()
                  .payload(pageResp)
                  .message("Retrieval of the job information is successful")
                  .build());
    } catch (Exception e) {
      log.error("Error while Fetching Jobs by params - getJobRecordsByFilter()", e);
      throw e;
    }
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

    try {
      JobDto job = jobService.createJob(jobDto);
      return ResponseEntity.ok(
          BaseResponse.builder().message("Job successfully created").payload(job).build());
    } catch (Exception e) {
      log.error("Creation of a job failed!", e);
      throw e;
    }
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

    try {
      JobDto job = jobService.getJob(jobId, orgId);

      log.info("Job successfully retrieved : {}", jobId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Retrieval of the job is successful")
              .payload(job)
              .build());
    } catch (Exception e) {
      log.error("Failed to process get job by jobId request!!", e);
      throw e;
    }
  }

  /**
   * @param orgId
   * @param jobType
   * @param days
   * @param sortField
   * @param sortOrder
   * @param pageNo
   * @param pageSize
   * @return
   * @throws JobException
   */
  @GetMapping(value = "/org/{orgId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<JobDto>>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam(required = false, name = "jobType") Optional<String> jobType,
      @RequestParam(required = false, name = "days") Optional<Integer> days,
      @RequestParam(required = false, name = "sortField") Optional<String> sortField,
      @RequestParam(required = false, name = "sortOrder") Optional<String> sortOrder,
      @RequestParam(required = false, name = "pageNo") Optional<Integer> pageNo,
      @RequestParam(required = false, name = "pageSize") Optional<Integer> pageSize)
      throws JobException {
    log.debug("--Inside getJobsByFilter()--");
    try {

      int requiredPageNo = pageNo.orElse(defaultPageNo);
      int requiredPageSize = pageSize.orElse(defaultPageSize);

      if (requiredPageNo < 1) {
        throw new JobException("PageNo can not be less than one", null, requiredPageNo);
      }

      Page<JobDto> pageResp =
          jobService.getJobs(
              orgId,
              jobType,
              days,
              sortField.orElse(defaultSortField),
              sortOrder,
              requiredPageNo,
              requiredPageSize);

      PagePayload<JobDto> pagePayload = new PagePayload<>();
      PagePayload.Pagination pagination = new PagePayload.Pagination();
      pagination.setTotalRecords((int) pageResp.getTotalElements());
      pagination.setTotalPages(pageResp.getTotalPages());
      pagination.setCurrentPage(requiredPageNo);
      pagePayload.setData(pageResp.getContent());
      pagePayload.setPagination(pagination);

      String nextUri =
          buildUriForPagination(
              jobType,
              days,
              sortField,
              sortOrder,
              requiredPageNo,
              requiredPageSize,
              pageResp.getTotalPages(),
              "next");
      String previousUri =
          buildUriForPagination(
              jobType,
              days,
              sortField,
              sortOrder,
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
    } catch (Exception e) {
      log.error("Error while Fetching Jobs by params - getJobsByFilter()", e);
      throw e;
    }
  }

  /**
   * @param jobType
   * @param days
   * @param sortField
   * @param sortOrder
   * @param currentPageNo
   * @param pageSize
   * @param totalPages
   * @param uriType
   * @return
   */
  @SuppressWarnings("squid:S107")
  private String buildUriForPagination(
      Optional<String> jobType,
      Optional<Integer> days,
      Optional<String> sortField,
      Optional<String> sortOrder,
      int currentPageNo,
      int pageSize,
      int totalPages,
      String uriType) {
    if (uriType.equalsIgnoreCase("next")) {
      if (currentPageNo >= totalPages) {
        return null;
      }
      return "/jobs?jobType="
          + jobType.orElse("")
          + "&days="
          + days.orElse(0)
          + "&sortField="
          + sortField.orElse(defaultSortField)
          + "&sortOrder="
          + sortOrder.orElse("DESC")
          + "&pageNo="
          + (currentPageNo + 1)
          + "&pageSize="
          + pageSize;
    } else {
      if (currentPageNo == 1) {
        return null;
      }
      return "/jobs?jobType="
          + jobType.orElse("")
          + "&days="
          + days.orElse(0)
          + "&sortField="
          + sortField.orElse(defaultSortField)
          + "&sortOrder="
          + sortOrder.orElse("DESC")
          + "&pageNo="
          + (currentPageNo - 1)
          + "&pageSize="
          + pageSize;
    }
  }
}
